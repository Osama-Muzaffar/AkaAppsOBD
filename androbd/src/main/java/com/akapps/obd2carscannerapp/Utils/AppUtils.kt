package com.akapps.obd2carscannerapp.Utils

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket

object AppUtils {
    var connectedDevice: BluetoothDevice?= null
    var connectedSocket: BluetoothSocket?= null
    var issplashOpening: Boolean= false
    var intersitialAdUtils: Int= 1


}