package com.akapps.obd2carscannerapp.Models;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.akapps.obd2carscannerapp.Ads.billing.InAppPurchase;
import com.github.pires.obd.commands.control.TroubleCodesCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.ObdResetCommand;
import com.github.pires.obd.commands.protocol.ResetTroubleCodesCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.enums.ObdProtocols;
import com.github.pires.obd.exceptions.MisunderstoodCommandException;
import com.github.pires.obd.exceptions.NoDataException;
import com.github.pires.obd.exceptions.UnableToConnectException;
import com.akapps.obd2carscannerapp.R;
import com.akapps.obd2carscannerapp.Utils.AppUtils;
import com.akapps.obd2carscannerapp.Utils.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ClearMILHelper {

    private static final String TAG = "ClearMIL";
    private static final int NO_BLUETOOTH_DEVICE_SELECTED = 0;
    private static final int CANNOT_CONNECT_TO_DEVICE = 1;
    private static final int NO_DATA = 3;
    public static final int DATA_OK = 4;
    private static final int CLEAR_DTC = 5;
    private static final int OBD_COMMAND_FAILURE = 10;
    private static final int OBD_COMMAND_FAILURE_IO = 11;
    private static final int OBD_COMMAND_FAILURE_UTC = 12;
    private static final int OBD_COMMAND_FAILURE_IE = 13;
    private static final int OBD_COMMAND_FAILURE_MIS = 14;
    private static final int OBD_COMMAND_FAILURE_NODATA = 15;
    private static final int PROGRESS_UPDATE = 16;
    public static final int ERROR = 17;

    private static ProgressDialog progressDialog;
    private static BluetoothDevice dev = null;
    private static BluetoothSocket sock = null;

    private static ArrayList<String> dtcCodes;
    private static ArrayAdapter<String> myarrayAdapter;

    private static Context mContext;

    private static CardView btnClearMIL;

    private static InAppPurchase inAppPurchase;
    private static String PURCHASE_CONSUMEABLE_PRODUCT_ID ;
    private static String BASE_64_KEY;
    private static PurchaseSharedPreferences purchaseSharedPreferences;

    private static final String[] ANDROID_12_BT_PERMISSIONS = new String[]{
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    private static final String[] BT_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    private static OnLoadTroubleCodesProgressFinishedListener listener;

    public static void setContext(Activity mContext,
                                  CardView btnClearMILRef,
                                  InAppPurchase inAppPurchase,
                                  OnLoadTroubleCodesProgressFinishedListener listener) {
        ClearMILHelper.mContext = mContext;
        dev = AppUtils.INSTANCE.getConnectedDevice();
        btnClearMIL = btnClearMILRef;
        registerListener (listener);
        sock = AppUtils.INSTANCE.getConnectedSocket();
        PURCHASE_CONSUMEABLE_PRODUCT_ID = mContext.getResources().getString(R.string.in_app_purchase_clear_mil_for_one_time_product_id);
        ClearMILHelper.inAppPurchase = inAppPurchase;
        inAppPurchase.setOnPurchaseFinishedListener(
                new OnPurchaseFinishedListener() {
                    @Override
                    public void onPurchaseFinished(boolean purchaseStatus, String result) {
                        inAppPurchase.finishPurchasing();
                        if (purchaseStatus)
                            clearMIL();
                        else
                            Constants.showToast("Error please purchase first to use this feature", mContext);
                    }
                });
    }

    private static void registerListener(OnLoadTroubleCodesProgressFinishedListener activityListner) {
            listener = activityListner;
    }

    public static void clearMIL (){
        resetTroubleCodes();
    }

    private static void makeToast(String text) {
        Toast toast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    private static void makeToastLong(String text) {
        Toast toast = Toast.makeText(mContext, text, Toast.LENGTH_LONG);
        toast.show();
    }

    private static void resetTroubleCodes (){
        if(AppUtils.INSTANCE.getConnectedDevice()!=null){
            try {
                Log.d("TESTRESET", "Trying reset");
                ResetTroubleCodesCommand clear = new ResetTroubleCodesCommand();
                clear.run(sock.getInputStream(), sock.getOutputStream());
                String result = clear.getFormattedResult();
                if (purchaseSharedPreferences.getClearMilRemainingClicksCount() > 0)
                    purchaseSharedPreferences.increaseClearMILClicks();
                dtcCodes.clear();
                myarrayAdapter.notifyDataSetChanged();
                Constants.showToast("Cleared MIL", mContext);
                Log.d("TESTRESET", "Trying reset result: " + result);
            } catch (Exception e) {
                Log.e(
                        TAG,
                        "There was an error while establishing connection. -> "
                                + e.getMessage()
                );
            }
        }
    }

    private static void dataOk(String res) {
        Map<String, String> dtcVals = getDict(R.array.dtc_keys, R.array.dtc_values);
        dtcCodes = new ArrayList<String>();
        if (res != null) {
            for (String dtcCode : res.split("\n")) {
                dtcCodes.add(dtcCode + " : " + dtcVals.get(dtcCode));
                Log.d("TEST", dtcCode + " : " + dtcVals.get(dtcCode));
            }
        } else {
            dtcCodes.add("There are no errors");
        }

        //resetTroubleCodes();

        btnClearMIL.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                makeToast("Click on button");
                purchaseSharedPreferences = PurchaseSharedPreferences.getInstance(mContext);
                if (purchaseSharedPreferences.getUnlockAllFeaturesPurchaseStatus()){
                    clearMIL();
                    return;
                }
                if (purchaseSharedPreferences.getClearMILPurchaseStatus() && purchaseSharedPreferences.getClearMilRemainingClicksCount() > 0){

                   if( purchaseSharedPreferences.getClearMilRemainingClicksCount() > 0)
                   {

                       purchaseSharedPreferences.increaseClearMILClicks ();
                   }
                   else
                   {
                       purchaseSharedPreferences.increaseClearMILClicks ();
                   }
                    clearMIL();

                }else {
                    purchaseSharedPreferences.putUnlockClearMilForSixTimesPurchaseStatus(false);
                    inAppPurchase.requestPurchasing(PURCHASE_CONSUMEABLE_PRODUCT_ID,true);
                }
            }
        } );

    }

    private static void createAndShowProgressDialog (){
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle(mContext.getString(R.string.dialog_loading_title));
        progressDialog.setMessage(mContext.getString(R.string.dialog_loading_body));
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setMax(5);
        progressDialog.setProgress(0);
        progressDialog.show();
    }

    public static void loadAndShowTroubleCodes (){
        Executor executor = Executors.newSingleThreadExecutor();
        createAndShowProgressDialog();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                String result = "";
                synchronized (this) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if (ActivityCompat.checkSelfPermission(mContext, ANDROID_12_BT_PERMISSIONS[0]) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, ANDROID_12_BT_PERMISSIONS[1]) == PackageManager.PERMISSION_GRANTED)
                            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                        else
                            ActivityCompat.requestPermissions((Activity) mContext, ANDROID_12_BT_PERMISSIONS, 111);
                    }
                    else {
                        if (ActivityCompat.checkSelfPermission(mContext, BT_PERMISSIONS[0]) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, BT_PERMISSIONS[1]) == PackageManager.PERMISSION_GRANTED)
                            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                        else
                            ActivityCompat.requestPermissions((Activity) mContext, BT_PERMISSIONS, 111);
                    }
                    Log.d(TAG, "Starting OBD connection..");
                    if(AppUtils.INSTANCE.getConnectedDevice()!=null){
                        try {
                            Log.d(TAG, "Queueing jobs for connection configuration..");

                            mHandler.obtainMessage(PROGRESS_UPDATE, 1).sendToTarget();
                            new ObdResetCommand().run(sock.getInputStream(), sock.getOutputStream());

                            mHandler.obtainMessage(PROGRESS_UPDATE, 2).sendToTarget();
                            new EchoOffCommand().run(sock.getInputStream(), sock.getOutputStream());

                            mHandler.obtainMessage(PROGRESS_UPDATE, 3).sendToTarget();
                            new LineFeedOffCommand().run(sock.getInputStream(), sock.getOutputStream());

                            mHandler.obtainMessage(PROGRESS_UPDATE, 4).sendToTarget();
                            new SelectProtocolCommand(ObdProtocols.AUTO).run(sock.getInputStream(), sock.getOutputStream());

                            mHandler.obtainMessage(PROGRESS_UPDATE, 5).sendToTarget();
                            ModifiedTroubleCodesObdCommand tcoc = new ModifiedTroubleCodesObdCommand();
                            tcoc.run(sock.getInputStream(), sock.getOutputStream());
                            result = tcoc.getFormattedResult();

                            mHandler.obtainMessage(PROGRESS_UPDATE, 6).sendToTarget();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.e("DTCERR", e.getMessage());
                            mHandler.obtainMessage(OBD_COMMAND_FAILURE_IO).sendToTarget();
                            Thread.currentThread().interrupt();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            Log.e("DTCERR", e.getMessage());
                            mHandler.obtainMessage(OBD_COMMAND_FAILURE_IE).sendToTarget();
                            Thread.currentThread().interrupt();

                        } catch (UnableToConnectException e) {
                            e.printStackTrace();
                            Log.e("DTCERR", e.getMessage());
                            mHandler.obtainMessage(OBD_COMMAND_FAILURE_UTC).sendToTarget();
                            Thread.currentThread().interrupt();

                        } catch (MisunderstoodCommandException e) {
                            e.printStackTrace();
                            Log.e("DTCERR", e.getMessage());
                            mHandler.obtainMessage(OBD_COMMAND_FAILURE_MIS).sendToTarget();
                            Thread.currentThread().interrupt();

                        } catch (NoDataException e) {
                            Log.e("DTCERR", e.getMessage());
                            mHandler.obtainMessage(OBD_COMMAND_FAILURE_NODATA).sendToTarget();
                            Thread.currentThread().interrupt();

                        } catch (Exception e) {
                            Log.e("DTCERR", e.getMessage());
                            mHandler.obtainMessage(OBD_COMMAND_FAILURE).sendToTarget();
                            Thread.currentThread().interrupt();
                        } finally {
                        }
                    }
                }
                onProgressFinished (result);
            }
        });
    }

    private static void onProgressUpdate(int i) {
        progressDialog.setProgress(i);
    }

    private static void onProgressFinished(String result) {
        progressDialog.dismiss();
        listener.onLoadTroubleCodesProgressFinished(result, DATA_OK);
        mHandler.obtainMessage(DATA_OK, result).sendToTarget();
    }

    private static Handler mHandler = new Handler(new Handler.Callback() {
        public boolean handleMessage(Message msg) {
            Log.d(TAG, "Message received on handler");
            switch (msg.what) {
                case PROGRESS_UPDATE:
                    onProgressUpdate((int) msg.obj);
                    break;
                case NO_BLUETOOTH_DEVICE_SELECTED:
                    makeToast(mContext.getString(R.string.text_bluetooth_nodevice));
                    listener.onLoadTroubleCodesProgressFinished(mContext.getString(R.string.text_bluetooth_nodevice), ERROR);

                    break;
                case CANNOT_CONNECT_TO_DEVICE:
                    makeToast(mContext.getString(R.string.text_bluetooth_error_connecting));
                    listener.onLoadTroubleCodesProgressFinished(mContext.getString(R.string.text_bluetooth_error_connecting), ERROR);

                    break;
                case OBD_COMMAND_FAILURE:
                    makeToast(mContext.getString(R.string.text_obd_command_failure));
                    listener.onLoadTroubleCodesProgressFinished(mContext.getString(R.string.text_obd_command_failure), ERROR);

                    break;
                case OBD_COMMAND_FAILURE_IO:
                    makeToast(mContext.getString(R.string.text_obd_command_failure) + " IO");
                    listener.onLoadTroubleCodesProgressFinished(mContext.getString(R.string.text_obd_command_failure) + " IO", ERROR);

                    break;
                case OBD_COMMAND_FAILURE_IE:
                    makeToast(mContext.getString(R.string.text_obd_command_failure) + " IE");
                    listener.onLoadTroubleCodesProgressFinished(mContext.getString(R.string.text_obd_command_failure) + " IE" + " IO", ERROR);

                    break;
                case OBD_COMMAND_FAILURE_MIS:
                    makeToast(mContext.getString(R.string.text_obd_command_failure) + " MIS");
                    listener.onLoadTroubleCodesProgressFinished(mContext.getString(R.string.text_obd_command_failure) + " MIS" + " IO", ERROR);

                    break;
                case OBD_COMMAND_FAILURE_UTC:
                    makeToast(mContext.getString(R.string.text_obd_command_failure) + " UTC");
                    listener.onLoadTroubleCodesProgressFinished(mContext.getString(R.string.text_obd_command_failure) + " UTC"+ " IO", ERROR);

                    break;
                case OBD_COMMAND_FAILURE_NODATA:
                    makeToastLong(mContext.getString(R.string.text_noerrors));
                    listener.onLoadTroubleCodesProgressFinished(mContext.getString(R.string.text_noerrors) + " IO", ERROR);

                    //finish();
                    break;

                case NO_DATA:
                    makeToast(mContext.getString(R.string.text_dtc_no_data));
                    ///finish();
                    break;
                case DATA_OK:
                    dataOk((String) msg.obj);
                    break;

            }
            return false;
        }
    });

    private static Map<String, String> getDict(int keyId, int valId) {
        String[] keys = mContext.getResources().getStringArray(keyId);
        String[] vals = mContext.getResources().getStringArray(valId);

        Map<String, String> dict = new HashMap<String, String>();
        for (int i = 0, l = keys.length; i < l; i++) {
            dict.put(keys[i], vals[i]);
        }

        return dict;
    }

    private static class ModifiedTroubleCodesObdCommand extends TroubleCodesCommand {
        @Override
        public String getResult() {
            // remove unwanted response from output since this results in erroneous error codes
            return rawData.replace("SEARCHING...", "").replace("NODATA", "");
        }
    }

    public interface OnLoadTroubleCodesProgressFinishedListener{
        void onLoadTroubleCodesProgressFinished(String result, int response);
    }

}
