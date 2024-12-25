package com.akapps.obd2carscannerapp.Activity

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.floor.planner.models.NativeBannerFull
import com.github.eltonvs.obd.command.NoDataException
import com.github.eltonvs.obd.command.NonNumericResponseException
import com.github.eltonvs.obd.command.ObdRawResponse
import com.github.eltonvs.obd.command.ObdResponse
import com.github.eltonvs.obd.command.bytesToInt
import com.github.eltonvs.obd.command.control.VINCommand
import com.github.eltonvs.obd.connection.ObdDeviceConnection
import com.github.pires.obd.commands.ObdCommand
import com.github.pires.obd.commands.control.TroubleCodesCommand
import com.github.pires.obd.commands.engine.AbsoluteLoadCommand
import com.github.pires.obd.commands.engine.LoadCommand
import com.github.pires.obd.commands.engine.MassAirFlowCommand
import com.github.pires.obd.commands.engine.RPMCommand
import com.github.pires.obd.commands.engine.ThrottlePositionCommand
import com.github.pires.obd.commands.pressure.BarometricPressureCommand
import com.github.pires.obd.commands.pressure.FuelPressureCommand
import com.github.pires.obd.commands.temperature.AirIntakeTemperatureCommand
import com.github.pires.obd.commands.temperature.EngineCoolantTemperatureCommand
import com.github.pires.obd.enums.AvailableCommandNames
import com.akapps.obd2carscannerapp.CustomView.KdGaugeView
import com.akapps.obd2carscannerapp.Models.OBDCommandHelper
import com.akapps.obd2carscannerapp.Models.ObdCommandJob
import com.akapps.obd2carscannerapp.Models.ObdConfigHelper
import com.akapps.obd2carscannerapp.R
import com.akapps.obd2carscannerapp.Trips.AbstractGatewayService
import com.akapps.obd2carscannerapp.Trips.MockObdGatewayService
import com.akapps.obd2carscannerapp.Trips.ObdGatewayService1
import com.akapps.obd2carscannerapp.Utils.AppUtils
import com.akapps.obd2carscannerapp.Utils.Constants
import com.akapps.obd2carscannerapp.databinding.ActivityRealTimesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import roboguice.RoboGuice
import java.io.IOException


//@ContentView(R.layout.activity_real_time)
class RealTimeActivity : AppCompatActivity() {

    init {
        RoboGuice.setUseAnnotationDatabases(false)
    }

    lateinit var binding : ActivityRealTimesBinding

    public val TAG : String= "REAL_TIME_ACTIVITY"
    private var job: Job? = null


    private var isServiceBound = false
    private var service: AbstractGatewayService? = null

    private var prefs: SharedPreferences? = null

    private lateinit var gauges: List<KdGaugeView>
    private val ranges = listOf(
        14000 to 0, // (max to min) for gauge 1
        100 to 0,   // (max to min) for gauge 2
        100 to 0,   // (max to min) for gauge 3
        100 to 0,   // (max to min) for gauge 4
        100 to 0,   // (max to min) for gauge 5
        100 to 0,   // (max to min) for gauge 6
        100 to 0,   // (max to min) for gauge 7
        700 to 0,   // (max to min) for gauge 8
        100 to 0,   // (max to min) for gauge 9
        260 to 0    // (max to min) for gauge 10
    )



    override fun onDestroy() {
        super.onDestroy()
    }
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        binding= ActivityRealTimesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = getSharedPreferences(Constants.NAME_GENERAL_SHARED_PREFs , MODE_PRIVATE)
        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
        changeStatusBarColorFromResource(R.color.unchange_black)

        // create a log instance for use by this application
//        triplog = TripLog.getInstance(this.applicationContext)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        prefs = getSharedPreferences(Constants.NAME_GENERAL_SHARED_PREFs, MODE_PRIVATE)


        gauges = listOf(
            binding.speedMeter1,
            binding.speedMeter2,
            binding.speedMeter3,
            binding.speedMeter4,
            binding.speedMeter5,
            binding.speedMeter6,
            binding.speedMeter7,
            binding.speedMeter8,
            binding.speedMeter9,
            binding.speedMeter10,

        )

        binding.backimg.setOnClickListener {
            finish()
        }

        val obdConnection = ObdDeviceConnection(AppUtils.connectedSocket!!.inputStream,
            AppUtils.connectedSocket!!.outputStream)

        CoroutineScope(Dispatchers.IO).launch {
            job = launch {
                yield()
                updateUI(obdConnection)

            }
        }

//        startLiveData()

//        val pid = PIDUtils.getPid(ObdModes.MODE_01, "OC")
//        val command = OBDCommand(pid!!)
//        command.run(AppUtils.connectedSocket!!.inputStream, AppUtils.connectedSocket!!.outputStream)
//        val result = command.formattedResult
//
//        Toast.makeText(this, "Rpm_data calculated result= "+pid.calculatedResult, Toast.LENGTH_SHORT).show()
//        Log.d("Rpm_data", "onCreate: formatted result+ "+result)
//        Log.d("Rpm_data", "onCreate: calculated result+ "+pid.calculatedResult)

//
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.BLUETOOTH_CONNECT
//            ) != PackageManager.PERMISSION_GRANTED
//        ){
//            requestPermissions(arrayOf(Manifest.permission.BLUETOOTH_CONNECT),111)
//        }
//
//        CoroutineScope(Dispatchers.IO).launch {
//            Log.d("Eltonvs_data", "Buillding connections: ")
//
//            // NB: BluetoothDevice device
//            val obd = OBD(AppUtils.connectedSocket, applicationContext)
//
//
//// Enable/disable headers
//            obd.enableHeaders(true)
//
//
//// Get device ID
//            val deviceId = obd.sendCommand(AT.DEVICE_IDENTIFICATION)
//
//
//// Get engine coolant temperature
//            val engineCoolantTemperature = obd.sendCommand(Mode01.ENGINE_COOLANT_TEMPERATURE)
//
//            Log.d("Eltonvs_data", "deviceId: "+deviceId.toString())
//            Log.d("Eltonvs_data", "engineCoolantTemperature: "+engineCoolantTemperature.toString())
//
//        }
//


        val nativebanner= findViewById<NativeBannerFull>(R.id.nativefull)
        nativebanner.loadNativeBannerAd(this,"ca-app-pub-3940256099942544/2247696110")


    }

    suspend fun obddata(){
        val obdConnection = ObdDeviceConnection(AppUtils.connectedSocket!!.inputStream,
            AppUtils.connectedSocket!!.outputStream)
        Log.d("Eltonvs_data", "obdConnection: "+obdConnection.toString())

        // Retrieving OBD Speed Command
//        val response = obdConnection.run(SpeedCommand())

// Using cache (use with caution)
        val cachedResponse = obdConnection.run(VINCommand(), useCache = true)
        val airtempintake = obdConnection.run(com.github.eltonvs.obd.command.temperature.AirIntakeTemperatureCommand())

// With a delay time - with this, the API will wait 500ms after executing the command
        val rpmresponse = obdConnection.run(com.github.eltonvs.obd.command.engine.RPMCommand(), useCache = true)

//        Log.d("Eltonvs_data", "response speed: "+response)
        Log.d("Eltonvs_data", "cachedResponse: "+cachedResponse)
        Log.d("Eltonvs_data", "rpmresponse: "+rpmresponse)
        Log.d("Eltonvs_data", "airtempintake: "+airtempintake)



    }

    fun stateUpdate(job: ObdCommandJob) {
        Log.d(TAG, "status update successfully")
        var cmdResult: String = ""
        val cmdName: String = job.getCommand().getName()
        val cmdID: String = OBDCommandHelper.LookUpCommand(cmdName)

        if (job.getState().equals(ObdCommandJob.ObdCommandJobState.EXECUTION_ERROR)) {
            cmdResult = job.getCommand().getResult()
            if (cmdResult != null && isServiceBound) {
                //obdStatusTextView.setText(cmdResult.toLowerCase());
            }
        } else if (job.getState().equals(ObdCommandJob.ObdCommandJobState.BROKEN_PIPE)) {
            if (isServiceBound) {
                stopLiveData()
            }
        } else if (job.getState().equals(ObdCommandJob.ObdCommandJobState.NOT_SUPPORTED)) {
            //cmdResult = getString(R.string.status_obd_no_support);
        } else {
            cmdResult = job.getCommand().getFormattedResult()
            /*if (isServiceBound)
                obdStatusTextView.setText(getString(R.string.status_obd_data));*/
        }

        //commandResult.put(cmdID, cmdResult);
        try {
            updateTripStatistic(job, cmdID)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateTripStatistic(job: ObdCommandJob, cmdID: String) {
        if (cmdID.endsWith(AvailableCommandNames.TROUBLE_CODES.toString())) {
            val loadCommand4 = job.getCommand() as TroubleCodesCommand
            // Toast.makeText(MainActivity.this,""+engineCoolantTemperatureCommand+"",Toast.LENGTH_SHORT).show();
            Log.d(TAG,
                "Calculated Result :" + loadCommand4.calculatedResult + "\n\nGet Result :" +
                        loadCommand4.result + "\n\nResult Unit :" + loadCommand4.resultUnit
            )
        } else if ((cmdID == AvailableCommandNames.ENGINE_RPM.toString())) {
            val command = job.getCommand() as RPMCommand
            //g1.setMaxValue(command.getRPM());
            binding.speedMeter1.setSpeed(command.rpm.toFloat())
            //currentTrip.setEngineRpmMax(command.getRPM());
        } else if (cmdID.endsWith(AvailableCommandNames.ENGINE_COOLANT_TEMP.toString())) {
            val engineCoolantTemperatureCommand =
                job.getCommand() as EngineCoolantTemperatureCommand
            //  Toast.makeText(MainRuntimeActivity.this,""+engineCoolantTemperatureCommand+"",Toast.LENGTH_SHORT).show();
            binding.speedMeter2.setSpeed(engineCoolantTemperatureCommand.temperature)
        } else if (cmdID.endsWith(AvailableCommandNames.ENGINE_LOAD.toString())) {
            val loadCommand = job.getCommand() as LoadCommand

            // Toast.makeText(MainActivity.this,""+engineCoolantTemperatureCommand+"",Toast.LENGTH_SHORT).show();
            binding.speedMeter3.setSpeed(loadCommand.percentage)
        } else if (cmdID.endsWith(AvailableCommandNames.AIR_INTAKE_TEMP.toString())) {
            val loadCommand1 = job.getCommand() as AirIntakeTemperatureCommand
            binding.speedMeter4.setSpeed(loadCommand1.imperialUnit)
        } else if (cmdID.endsWith(AvailableCommandNames.THROTTLE_POS.toString())) {
            val loadCommand3 = job.getCommand() as ThrottlePositionCommand
            binding.speedMeter5.setSpeed(loadCommand3.percentage)
        } else if (cmdID.endsWith(AvailableCommandNames.ABS_LOAD.toString())) {
            val loadCommand3 = job.getCommand() as AbsoluteLoadCommand
            binding.speedMeter6.setSpeed(loadCommand3.percentage)
        } else if (cmdID.endsWith(AvailableCommandNames.FUEL_PRESSURE.toString())) {
            val loadCommand3 = job.getCommand() as FuelPressureCommand
            binding.speedMeter7.setSpeed(loadCommand3.imperialUnit)
        } else if (cmdID.endsWith(AvailableCommandNames.MAF.toString())) {
            val loadCommand3 = job.getCommand() as MassAirFlowCommand
            binding.speedMeter8.setSpeed(loadCommand3.maf.toFloat())
        } else if (cmdID.endsWith(AvailableCommandNames.BAROMETRIC_PRESSURE.toString())) {
            val loadCommand3 = job.getCommand() as BarometricPressureCommand
            binding.speedMeter9.setSpeed(loadCommand3.imperialUnit)
        } else if (cmdID.endsWith(AvailableCommandNames.AIR_FUEL_RATIO.toString())) {
            //  AirFuelRatioCommand loadCommand3=(AirFuelRatioCommand) job.getCommand();
            // kdGaugeViewAirFuelRatio.setSpeed( (float) loadCommand3.getAirFuelRatio() );
        }
    }

    private fun startLiveData() {
        Constants.showToast("starting live data", this)
        doBindService()
        Handler().post(mQueueCommands)
    }

    private fun stopLiveData() {
        doUnbindService()
    }

    private fun doBindService() {
        if (!isServiceBound) {
            Log.d(
                TAG,
                "Binding OBD service.."
            )
            if (Constants.PreRequisites) {
                //btStatusTextView.setText(getString(R.string.status_bluetooth_connecting));
                val serviceIntent: Intent = Intent(this, ObdGatewayService1::class.java)
                bindService(serviceIntent, serviceConn, BIND_AUTO_CREATE)
                Constants.showToast("service binding started obdgatewayservice", this)
            } else {
                //btStatusTextView.setText(getString(R.string.status_bluetooth_disabled));
                val serviceIntent: Intent = Intent(this, MockObdGatewayService::class.java)
                bindService(serviceIntent, serviceConn, BIND_AUTO_CREATE)
                Constants.showToast("service binding started mockobdgatewayservice", this)
            }
        }
    }

    private fun doUnbindService() {
        if (isServiceBound) {
            if (service!!.isRunning) {
                service!!.stopService()
                /*if (Constants.PreRequisites)
                    btStatusTextView.setText(getString(R.string.status_bluetooth_ok));*/
            }
            Log.d(
                TAG,
                "Unbinding OBD service.."
            )
            unbindService(serviceConn)
            isServiceBound = false
            //obdStatusTextView.setText(getString(R.string.status_obd_disconnected));
        }
    }

    private fun queueCommands() {
        if (isServiceBound) {
            for (Command: ObdCommand in ObdConfigHelper.getCommands()) {
                if (prefs!!.getBoolean(
                        Command.name,
                        true
                    )
                ) service!!.queueJob(ObdCommandJob(Command))
                Log.d(TAG, "Added in queue")
            }
        }
    }

    private val serviceConn: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, binder: IBinder) {
            Constants.showToast("service bounded", this@RealTimeActivity)
            isServiceBound = true
            service = (binder as AbstractGatewayService.AbstractGatewayServiceBinder).getService()
            service!!.setContext(this@RealTimeActivity)
            try {
                service!!.startService()
                /*if (Constants.PreRequisites)
                    btStatusTextView.setText(getString(R.string.status_bluetooth_connected));*/
            } catch (ioe: IOException) {
                Log.e(
                    TAG,
                    "Failure Starting live data"
                )
                //btStatusTextView.setText(getString(R.string.status_bluetooth_error_connecting));
                doUnbindService()
            }
        }



        // This method is *only* called when the connection to the service is lost unexpectedly
        // and *not* when the client unbinds (http://developer.android.com/guide/components/bound-services.html)
        // So the isServiceBound attribute should also be set to false when we unbind from the service.
        override fun onServiceDisconnected(className: ComponentName) {
            isServiceBound = false
            Constants.showToast("service disconnected", this@RealTimeActivity)
        }
    }

    private val mQueueCommands: Runnable = object : Runnable {
        override fun run() {
            Log.d(TAG, "run: Queuecommand")
            if ((service != null) && service!!.isRunning()) {
                queueCommands()

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
            Handler().postDelayed(this, OBDCommandHelper.getObdUpdatePeriod(prefs).toLong())
        }
    }

    suspend fun askRPM(obdConnection: ObdDeviceConnection): String {
        return withContext(Dispatchers.IO) {
            try {
                //using custom command (RPMCommandFix() )because the one in the library is broken
                val aux: ObdResponse = obdConnection.run(RPMCommandFix(), delayTime = 100)
                Log.w("debugRPM", (aux.value.toInt()/4).toString())
                yield()
                (aux.value.toInt()/4).toString() // Return the value of aux.value
            } catch (e: NonNumericResponseException) {
                e.printStackTrace()
                "?NaN"
            } catch(e: NoDataException){
                e.printStackTrace()
                "!DATA"
            }
        }
    }

    private suspend fun RPM(obdConnection: ObdDeviceConnection): String{
        return withContext(Dispatchers.IO){
            askRPM(obdConnection)
        }
    }

    class RPMCommandFix : com.github.eltonvs.obd.command.ObdCommand() {
        override val tag = "ENGINE_RPM"
        override val name = "Engine RPM"
        override val mode = "01"
        override val pid = "0C"

        override val defaultUnit = "RPM"
        override val handler = { it: ObdRawResponse -> bytesToInt(it.bufferedValue, bytesToProcess = 2 ).toString()}
    }


    suspend fun updateUI(obdConnection: ObdDeviceConnection){
        val RPM_ret = RPM(obdConnection)
        Log.d(TAG, "updateUI: rpm= "+RPM_ret)
    }

}