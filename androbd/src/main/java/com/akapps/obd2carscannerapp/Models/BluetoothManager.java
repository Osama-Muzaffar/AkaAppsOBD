package com.akapps.obd2carscannerapp.Models;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;

public class BluetoothManager {

    private static final String TAG = BluetoothManager.class.getName();
    /*
     * http://developer.android.com/reference/android/bluetooth/BluetoothDevice.html
     * #createRfcommSocketToServiceRecord(java.util.UUID)
     *
     * "Hint: If you are connecting to a Bluetooth serial board then try using the
     * well-known SPP UUID 00001101-0000-1000-8000-00805F9B34FB. However if you
     * are connecting to an Android peer then please generate your own unique
     * UUID."
     */
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static BluetoothSocket bluetoothSocket = null;

    private static final String[] ANDROID_12_BT_PERMISSIONS = new String[]{
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    private static final String[] BT_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    /**
     * Instantiates a BluetoothSocket for the remote device and connects it.
     * <p/>
     * See http://stackoverflow.com/questions/18657427/ioexception-read-failed-socket-might-closed-bluetooth-on-android-4-3/18786701#18786701
     *
     * @param dev The remote device to connect to
     * @return The BluetoothSocket
     * @throws IOException
     */
    public static BluetoothSocket connect(BluetoothDevice dev, Context mContext) throws IOException {
        BluetoothSocket sock = null;
        BluetoothSocket sockFallback = null;

        Log.d(TAG, "Starting Bluetooth connection..");
    	try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ActivityCompat.checkSelfPermission(mContext, ANDROID_12_BT_PERMISSIONS[0]) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, ANDROID_12_BT_PERMISSIONS[1]) == PackageManager.PERMISSION_GRANTED) {
                    sock = dev.createRfcommSocketToServiceRecord(MY_UUID);
                    sock.connect();
                }
                else
                    ActivityCompat.requestPermissions((Activity) mContext, ANDROID_12_BT_PERMISSIONS, 111);
            }
            else {
                if (ActivityCompat.checkSelfPermission(mContext, BT_PERMISSIONS[0]) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, BT_PERMISSIONS[1]) == PackageManager.PERMISSION_GRANTED) {
                    sock = dev.createRfcommSocketToServiceRecord(MY_UUID);
                    sock.connect();
                }
                else
                    ActivityCompat.requestPermissions((Activity) mContext, BT_PERMISSIONS, 111);
            }

        } catch (Exception e1) {
            Log.e(TAG, "There was an error while establishing Bluetooth connection. Falling back..", e1);
            if (sock != null){
                Class<?> clazz = sock.getRemoteDevice().getClass();
                Class<?>[] paramTypes = new Class<?>[]{Integer.TYPE};
                try {
                    Method m = clazz.getMethod("createRfcommSocket", paramTypes);
                    Object[] params = new Object[]{Integer.valueOf(1)};
                    sockFallback = (BluetoothSocket) m.invoke(sock.getRemoteDevice(), params);
                    sockFallback.connect();
                    sock = sockFallback;
                } catch (Exception e2) {
                    e2.printStackTrace ();
                    Log.e(TAG, "Couldn't fallback while establishing Bluetooth connection.", e2);
                    throw new IOException(e2.getMessage());
                }
            }
        }
    	bluetoothSocket = sock;
    	return sock;
    }

    public static void disconnect () throws IOException {
        if (bluetoothSocket != null || bluetoothSocket.isConnected()){
            bluetoothSocket.close();
            bluetoothSocket = null;

        }
    }
}