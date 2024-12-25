package com.akapps.obd2carscannerapp.Activity;

import static com.akapps.obd2carscannerapp.Ads.billing.AppPurchase.getInstance;
import static com.quadsquad.savestatus.videodownloader.storysaver.statuskeeper.adsConfigs.RemoteValueKt.getAdsConfig;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.akapps.obd2carscannerapp.Trips.PairedDeviceSharedPreference;
import com.akapps.obd2carscannerapp.databinding.ActivityRealTimesBinding;
import com.floor.planner.models.NativeBannerFull;
import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.control.TroubleCodesCommand;
import com.github.pires.obd.commands.engine.AbsoluteLoadCommand;
import com.github.pires.obd.commands.engine.LoadCommand;
import com.github.pires.obd.commands.engine.MassAirFlowCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.engine.ThrottlePositionCommand;
import com.github.pires.obd.commands.pressure.BarometricPressureCommand;
import com.github.pires.obd.commands.pressure.FuelPressureCommand;
import com.github.pires.obd.commands.temperature.AirIntakeTemperatureCommand;
import com.github.pires.obd.commands.temperature.EngineCoolantTemperatureCommand;
import com.github.pires.obd.enums.AvailableCommandNames;
import com.akapps.obd2carscannerapp.Ads.BannerAdView;
import com.akapps.obd2carscannerapp.Ads.billing.AppPurchase;
import com.akapps.obd2carscannerapp.BuildConfig;
import com.akapps.obd2carscannerapp.Models.OBDCommandHelper;
import com.akapps.obd2carscannerapp.Models.ObdCommandJob;
import com.akapps.obd2carscannerapp.Models.ObdConfigHelper;
import com.akapps.obd2carscannerapp.R;
import com.akapps.obd2carscannerapp.Trips.AbstractGatewayService;
import com.akapps.obd2carscannerapp.Trips.ObdGatewayService1;
import com.akapps.obd2carscannerapp.Utils.AppUtils;
import com.akapps.obd2carscannerapp.Utils.Constants;

import java.io.IOException;
import java.util.Random;

import roboguice.RoboGuice;

public class MyRealTimeActivity extends AppCompatActivity{
    private static final String TAG = "Realtime_Data";
    ActivityRealTimesBinding binding;
    private boolean isServiceBound;
    private AbstractGatewayService service;

    private SharedPreferences prefs ;
    private Handler handler = new Handler();
    private Runnable runnable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityRealTimesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
// Make status bar transparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.BLACK);
        }
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        );
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        prefs = getSharedPreferences(Constants.NAME_GENERAL_SHARED_PREFs , MODE_PRIVATE);
        setInitialToKdGaugeViews();
        binding.backimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if(AppUtils.INSTANCE.getConnectedDevice()!=null) {
            startLiveData();
        }
        else{
            runnable = new Runnable() {
                @Override
                public void run() {
                        updateGaugeValues();
                        handler.postDelayed(this, 1000); // Schedule the next update in 1 second
                }
            };
            handler.post(runnable);

        }
        if (PairedDeviceSharedPreference.getInstance(this).getSubsList().size() > 0) {
            boolean isShowingAd = true;
            for (String purchases : PairedDeviceSharedPreference.getInstance(this).getSubsList()) {
                // Uncomment and modify the following lines if needed
                // if (purchases.equals(getString(R.string.in_app_subscription_yearly_product_id))){
                //     isShowingAd = false;
                //     break;
                // } else {
                isShowingAd = false;
                // }
            }
            if (isShowingAd) {
                if (getAdsConfig() != null) {
                    if (getAdsConfig().getRealtime_native()) {
                        NativeBannerFull nativeBannerFull = findViewById(R.id.nativefull);
                        nativeBannerFull.setVisibility(View.VISIBLE);
                        nativeBannerFull.loadNativeBannerAd(this, BuildConfig.admob_native);
                    } else if (getAdsConfig().getRealtime_banner()) {
                        BannerAdView bannerView = findViewById(R.id.banneradsview);
                        bannerView.setVisibility(View.VISIBLE);
                        bannerView.loadBanner(this, BuildConfig.admob_banner);
                    } else {
                        RelativeLayout adsContainer = findViewById(R.id.ad_container);
                        adsContainer.setVisibility(View.GONE);
                    }
                }
                else {
                    Log.d("test_remote", "remote config is null");
                    RelativeLayout adsContainer = findViewById(R.id.ad_container);
                    adsContainer.setVisibility(View.GONE);
                }
            } else {
                Log.d("test_remote", "remote config is null");
                RelativeLayout adsContainer = findViewById(R.id.ad_container);
                adsContainer.setVisibility(View.GONE);
            }
        }
        else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (getAdsConfig() != null) {
                        if (getAdsConfig().getRealtime_native()) {
                            NativeBannerFull nativeBannerFull = findViewById(R.id.nativefull);
                            nativeBannerFull.setVisibility(View.VISIBLE);
                            nativeBannerFull.loadNativeBannerAd(MyRealTimeActivity.this, BuildConfig.admob_native);
                        } else if (getAdsConfig().getRealtime_banner()) {
                            BannerAdView bannerView = findViewById(R.id.banneradsview);
                            bannerView.setVisibility(View.VISIBLE);
                            bannerView.loadBanner(MyRealTimeActivity.this, BuildConfig.admob_banner);
                        } else {
                            RelativeLayout adsContainer = findViewById(R.id.ad_container);
                            adsContainer.setVisibility(View.GONE);
                        }
                    }
                    else {
                        Log.d("test_remote", "remote config is null");
                        RelativeLayout adsContainer = findViewById(R.id.ad_container);
                        adsContainer.setVisibility(View.GONE);
                    }
                }
            });
        }

   /*     if(!getInstance().isPurchased()){
            BannerAdView bannerAdView= findViewById(R.id.nativefull);
            bannerAdView.loadBanner(this, BuildConfig.admob_banner);
        }
        else{
            var isshwoingad= true;
            for(String purchases : AppPurchase.getInstance().getOwnerIdInapps()){
                if (purchases.equals("com.obd.inapp") || purchases.equals("com.obdblue.allpremium")){
                    isshwoingad= false;
                }
                else{
                    isshwoingad= true;
                }
            }
            if (isshwoingad){
                BannerAdView bannerAdView= findViewById(R.id.nativefull);
                bannerAdView.loadBanner(this, BuildConfig.admob_banner);
            }
            else {
                BannerAdView bannerAdView = findViewById(R.id.nativefull);
                bannerAdView.setVisibility(View.GONE);
            }
        }*/
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }



    static  {
        RoboGuice.setUseAnnotationDatabases(false);
    }


    private void setInitialToKdGaugeViews() {

        binding.speedMeter1.setSpeed(60);
        binding.speedMeter2.setSpeed(60);
        binding.speedMeter3.setSpeed(60);
        binding.speedMeter4.setSpeed(60);
        binding.speedMeter5.setSpeed(5);
        binding.speedMeter6.setSpeed(5);
        binding.speedMeter7.setSpeed(5);
        binding.speedMeter8.setSpeed(5);
        binding.speedMeter9.setSpeed(5);
        binding.speedMeter10.setSpeed(5);
    }

    public void stateUpdate(final ObdCommandJob job) {
        String cmdResult = "";
        final String cmdName = job.getCommand().getName();
        final String cmdID = OBDCommandHelper.LookUpCommand(cmdName);

        if (job.getState().equals(ObdCommandJob.ObdCommandJobState.EXECUTION_ERROR)) {
            cmdResult = job.getCommand().getResult();
            if (cmdResult != null && isServiceBound) {
                //obdStatusTextView.setText(cmdResult.toLowerCase());
            }
        } else if (job.getState().equals(ObdCommandJob.ObdCommandJobState.BROKEN_PIPE)) {
            if (isServiceBound) {
                stopLiveData();
            }
        } else if (job.getState().equals(ObdCommandJob.ObdCommandJobState.NOT_SUPPORTED)) {
            //cmdResult = getString(R.string.status_obd_no_support);
        } else {
            cmdResult = job.getCommand().getFormattedResult();
            /*if (isServiceBound)
                obdStatusTextView.setText(getString(R.string.status_obd_data));*/
        }

        //commandResult.put(cmdID, cmdResult);
        try {
            updateTripStatistic(job, cmdID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateTripStatistic(final ObdCommandJob job, final String cmdID) {
        if (cmdID.endsWith(AvailableCommandNames.TROUBLE_CODES.toString())) {
            TroubleCodesCommand loadCommand4 = (TroubleCodesCommand) job.getCommand();
            // Toast.makeText(MainActivity.this,""+engineCoolantTemperatureCommand+"",Toast.LENGTH_SHORT).show();
//            if (tvTroubles != null)
                binding.tvTroubles.setText("Calculated Result :" + loadCommand4.getCalculatedResult() + "\n\nGet Result :" +
                        loadCommand4.getResult() + "\n\nResult Unit :" + loadCommand4.getResultUnit());
        } else if (cmdID.equals(AvailableCommandNames.ENGINE_RPM.toString())) {
            RPMCommand command = (RPMCommand) job.getCommand();
            //g1.setMaxValue(command.getRPM());
            binding.speedMeter1.setSpeed(command.getRPM());
            //currentTrip.setEngineRpmMax(command.getRPM());
        } else if (cmdID.endsWith(AvailableCommandNames.ENGINE_COOLANT_TEMP.toString())) {
            EngineCoolantTemperatureCommand engineCoolantTemperatureCommand = (EngineCoolantTemperatureCommand) job.getCommand();
            //  Toast.makeText(MainRuntimeActivity.this,""+engineCoolantTemperatureCommand+"",Toast.LENGTH_SHORT).show();
            binding.speedMeter2.setSpeed(engineCoolantTemperatureCommand.getTemperature());
        } else if (cmdID.endsWith(AvailableCommandNames.ENGINE_LOAD.toString())) {
            LoadCommand loadCommand = (LoadCommand) job.getCommand();
            // Toast.makeText(MainActivity.this,""+engineCoolantTemperatureCommand+"",Toast.LENGTH_SHORT).show();

            binding.speedMeter3.setSpeed(loadCommand.getPercentage());

        } else if (cmdID.endsWith(AvailableCommandNames.AIR_INTAKE_TEMP.toString())) {
            AirIntakeTemperatureCommand loadCommand1 = (AirIntakeTemperatureCommand) job.getCommand();
            binding.speedMeter4.setSpeed(loadCommand1.getImperialUnit());
        } else if (cmdID.endsWith(AvailableCommandNames.THROTTLE_POS.toString())) {
            ThrottlePositionCommand loadCommand3 = (ThrottlePositionCommand) job.getCommand();
            binding.speedMeter5.setSpeed((float) loadCommand3.getPercentage());
        } else if (cmdID.endsWith(AvailableCommandNames.ABS_LOAD.toString())) {
            AbsoluteLoadCommand loadCommand3 = (AbsoluteLoadCommand) job.getCommand();
            binding.speedMeter6.setSpeed((float) loadCommand3.getPercentage());
        } else if (cmdID.endsWith(AvailableCommandNames.FUEL_PRESSURE.toString())) {
            FuelPressureCommand loadCommand3 = (FuelPressureCommand) job.getCommand();
            binding.speedMeter7.setSpeed((float) loadCommand3.getImperialUnit());
        } else if (cmdID.endsWith(AvailableCommandNames.MAF.toString())) {
            MassAirFlowCommand loadCommand3 = (MassAirFlowCommand) job.getCommand();
            binding.speedMeter8.setSpeed((float) loadCommand3.getMAF());
        } else if (cmdID.endsWith(AvailableCommandNames.BAROMETRIC_PRESSURE.toString())) {
            BarometricPressureCommand loadCommand3 = (BarometricPressureCommand) job.getCommand();
            binding.speedMeter9.setSpeed((float) loadCommand3.getImperialUnit());
        } else if (cmdID.endsWith(AvailableCommandNames.AIR_FUEL_RATIO.toString())) {
            //  AirFuelRatioCommand loadCommand3=(AirFuelRatioCommand) job.getCommand();
            // kdGaugeViewAirFuelRatio.setSpeed( (float) loadCommand3.getAirFuelRatio() );
        }

    }
    private void startLiveData() {
        Log.d(TAG, "startLiveData: Starting Live Data");
        doBindService();
        new Handler().post(mQueueCommands);
    }

    private void stopLiveData() {
        doUnbindService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLiveData();
        if (runnable!=null) {
            handler.removeCallbacks(runnable); // Stop updates when the activity is destroyed
        }
    }

    private void doBindService() {
        if (!isServiceBound) {
            //btStatusTextView.setText(getString(R.string.status_bluetooth_connecting));
            Intent serviceIntent = new Intent(this, ObdGatewayService1.class);
            this.bindService(serviceIntent, serviceConn, Context.BIND_AUTO_CREATE);
        }
    }

    private void doUnbindService() {
        if (isServiceBound) {
            if (service.isRunning()) {
                service.stopService();
                /*if (Constants.PreRequisites)
                    btStatusTextView.setText(getString(R.string.status_bluetooth_ok));*/
            }
            unbindService(serviceConn);
            isServiceBound = false;
            //obdStatusTextView.setText(getString(R.string.status_obd_disconnected));
        }
    }

    private void queueCommands() {
        if (isServiceBound) {
            for (ObdCommand Command : ObdConfigHelper.getCommands()) {
                if (prefs.getBoolean(Command.getName(), true))
                    service.queueJob(new ObdCommandJob(Command));
            }
        }
    }

    private ServiceConnection serviceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder binder) {
            Constants.showToast("service bounded", MyRealTimeActivity.this);
            isServiceBound = true;
            service = ((AbstractGatewayService.AbstractGatewayServiceBinder) binder).getService();
            service.setContext(MyRealTimeActivity.this);
            try {
                service.startService();
                /*if (Constants.PreRequisites)
                    btStatusTextView.setText(getString(R.string.status_bluetooth_connected));*/
            } catch (IOException ioe) {
                //btStatusTextView.setText(getString(R.string.status_bluetooth_error_connecting));
                doUnbindService();
            }
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

        // This method is *only* called when the connection to the service is lost unexpectedly
        // and *not* when the client unbinds (http://developer.android.com/guide/components/bound-services.html)
        // So the isServiceBound attribute should also be set to false when we unbind from the service.
        @Override
        public void onServiceDisconnected(ComponentName className) {
            isServiceBound = false;
            Constants.showToast("service disconnected", MyRealTimeActivity.this);
        }
    };

    private final Runnable mQueueCommands = new Runnable() {
        public void run() {
            if (service != null && service.isRunning() && service.queueEmpty()) {
                queueCommands();

                /*double lat = 0;
                double lon = 0;
                double alt = 0;
                final int posLen = 7;
                if (mGpsIsStarted && mLastLocation != null) {
                    lat = mLastLocation.getLatitude();
                    lon = mLastLocation.getLongitude();
                    alt = mLastLocation.getAltitude();

                    StringBuilder sb = new StringBuilder();
                    sb.append("Lat: ");
                    sb.append(String.valueOf(mLastLocation.getLatitude()).substring(0, posLen));
                    sb.append(" Lon: ");
                    sb.append(String.valueOf(mLastLocation.getLongitude()).substring(0, posLen));
                    sb.append(" Alt: ");
                    sb.append(String.valueOf(mLastLocation.getAltitude()));
                    gpsStatusTextView.setText(sb.toString());
                }
                if (prefs.getBoolean(Constants.UPLOAD_DATA_KEY, false)) {
                    // Upload the current reading by http
                    final String vin = prefs.getString(Constants.VEHICLE_ID_KEY, "UNDEFINED_VIN");
                    Map<String, String> temp = new HashMap<String, String>();
                    temp.putAll(commandResult);
                    ObdReading reading = new ObdReading(lat, lon, alt, System.currentTimeMillis(), vin, temp);
                    new UploadAsyncTask().execute(reading);

                } else if (prefs.getBoolean(Constants.ENABLE_FULL_LOGGING_KEY, false)) {
                    // Write the current reading to CSV
                    final String vin = prefs.getString(Constants.VEHICLE_ID_KEY, "UNDEFINED_VIN");
                    Map<String, String> temp = new HashMap<String, String>();
                    temp.putAll(commandResult);
                    ObdReading reading = new ObdReading(lat, lon, alt, System.currentTimeMillis(), vin, temp);
                    if (reading != null) myCSVWriter.writeLineCSV(reading);
                }
                commandResult.clear();*/
            }
            // run again in period defined in preferences
            new Handler().postDelayed(mQueueCommands, OBDCommandHelper.getObdUpdatePeriod(prefs));
        }
    };

    private void updateGaugeValues() {
        Random random = new Random();

        // Update each gauge with new values
        binding.speedMeter1.setSpeed(random.nextInt(14000));
        binding.speedMeter2.setSpeed(random.nextInt(100));
        binding.speedMeter3.setSpeed(random.nextInt(100));
        binding.speedMeter4.setSpeed(random.nextInt(100));
        binding.speedMeter5.setSpeed(random.nextInt(100));
        binding.speedMeter6.setSpeed(random.nextInt(100));
        binding.speedMeter7.setSpeed(random.nextInt(100));
        binding.speedMeter8.setSpeed(random.nextInt(100));
        binding.speedMeter9.setSpeed(random.nextInt(260));
        binding.speedMeter10.setSpeed(random.nextInt(100));
    }

}
