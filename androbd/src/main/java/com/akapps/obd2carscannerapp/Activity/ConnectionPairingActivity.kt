package com.akapps.obd2carscannerapp.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.akapps.obd2carscannerapp.Adapter.PairedAdapter
import com.akapps.obd2carscannerapp.BtDeviceListActivity
import com.akapps.obd2carscannerapp.CommService
import com.akapps.obd2carscannerapp.Utils.AppUtils.connectedDevice
import com.akapps.obd2carscannerapp.databinding.ActivityBluetoothPairingBinding
import com.akapps.ecu.prot.obd.ElmProt
import com.akapps.obd2carscannerapp.R
import com.akapps.pvs.PvList



class ConnectionPairingActivity : AppCompatActivity() {
    lateinit var binding: ActivityBluetoothPairingBinding
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var availableDevicesList: ArrayList<BluetoothDevice>? = null
    var scanneradapter: PairedAdapter?= null
    private val REQUEST_PERMISSIONS = 100
    var isScanning= false
    private val REQUEST_BLUETOOTH_PERMISSION = 1
    private val REQUEST_ENABLE_BT = 3
    private var mCommService: CommService? = null
    private var mConnectedDeviceName: String? = null
    var mPluginPvs: PvList =
        PvList()

    /*
     * Implementations of PluginManager data interface callbacks
     */
    enum class MODE {
        OFFLINE,  //< OFFLINE mode
        ONLINE,  //< ONLINE mode
        DEMO,  //< DEMO mode
        FILE,  //< FILE mode
    }

    private var mode: MODE = MODE.OFFLINE
    private var obdService: Int = ElmProt.OBD_SVC_NONE

    companion object{

    }

    /*public static CenteredTextFragment1 createFor(String text) {
        CenteredTextFragment1 fragment = new CenteredTextFragment1();
        Bundle args = new Bundle();
        args.putString(EXTRA_TEXT, text);
        fragment.setArguments(args);
        return fragment;
    }*/
    // private AdView mAdView;
    private var device: BluetoothDevice? = null


    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action

            when (action) {
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    Log.d("Receiver", "onReceive: Discovery Started")
                    isScanning = true
                    binding.refreshtxt.visibility= View.GONE
                    binding.scanningprogress.visibility= View.VISIBLE
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    Log.d("Receiver", "onReceive: Discovery Finished")
                    isScanning = false
                    binding.refreshtxt.visibility= View.VISIBLE
                    binding.scanningprogress.visibility= View.GONE
                }
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice object and its info from the Intent
                    val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    if (device != null && device.name != null) {
                        // Check if the device is already in the list
                        var deviceExists = false
                        for (d in availableDevicesList!!) {
                            if (d.address == device.address) { // Compare using the device address
                                deviceExists = true
                                break
                            }
                        }
                        // Add the device to the list if it doesn't exist
                        if (!deviceExists) {
                            availableDevicesList!!.add(device)
                            scanneradapter!!.notifyDataSetChanged()
                        }

                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityBluetoothPairingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
        changeStatusBarColorFromResource(R.color.unchange_black)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        availableDevicesList= ArrayList()
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if (bluetoothAdapter!!.isEnabled){
            binding.connectingswitch.isChecked= true
            val pairedDevicesSet = bluetoothAdapter!!.bondedDevices
            val pairedDevicesList: ArrayList<BluetoothDevice> = ArrayList(pairedDevicesSet)
            val pairedadapter= PairedAdapter(this,pairedDevicesList)
            binding.pairedrecycler.layoutManager= LinearLayoutManager(this)
            binding.pairedrecycler.adapter= pairedadapter

            pairedadapter!!.setItemClickListner(object : PairedAdapter.ItemClickInterface{
                override fun onItemClick(position: Int) {
                    if (position > -1) {
                        device= pairedDevicesList!!.get(position)
                        connectedDevice= device
                        bluetoothAdapter!!.cancelDiscovery()

                        val intent = Intent()
                        intent.putExtra(BtDeviceListActivity.EXTRA_DEVICE_ADDRESS, device!!.address)

                        setResult(RESULT_OK, intent)
                        finish()

                    }
                }
            })
        }


        binding.connectingswitch.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            @SuppressLint("MissingPermission")
            @RequiresApi(Build.VERSION_CODES.S)
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if (isChecked){
                    checkAndRequestBluetoothPermission()

                }
                else{
                    if (bluetoothAdapter!!.isDiscovering()) {
                        bluetoothAdapter!!.cancelDiscovery()
                    }
                    binding.offbluetoothlinear.visibility= View.VISIBLE
                    binding.mainscroll.visibility= View.GONE
                    bluetoothAdapter!!.disable()
                    Toast.makeText(this@ConnectionPairingActivity, "Bluetooth is being disabled...", Toast.LENGTH_SHORT).show()
                }
            }

        })

        binding.refreshtxt.setOnClickListener {
            startDiscovery()
        }



        binding.backimg.setOnClickListener {
            if (bluetoothAdapter!!.isDiscovering()) {
                bluetoothAdapter!!.cancelDiscovery()
            }
            finish()
        }
        scanneradapter= PairedAdapter(this,availableDevicesList!!)
        binding.avialablerecycler.layoutManager= LinearLayoutManager(this)
        binding.avialablerecycler.adapter= scanneradapter

        scanneradapter!!.setItemClickListner(object : PairedAdapter.ItemClickInterface{
            override fun onItemClick(position: Int) {
                if (position > -1) {
                    device= availableDevicesList!!.get(position)
                    connectedDevice= device
                    bluetoothAdapter!!.cancelDiscovery()

                    //String address = "00:0D:18:A0:4E:35"; //FORCE OBD MAC Address
                    // Create the result Intent and include the MAC address
                    val intent = Intent()
                    intent.putExtra(BtDeviceListActivity.EXTRA_DEVICE_ADDRESS, device!!.address)


                    // Set result and finish this Activity
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        })


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Check if permissions are already granted
            if (checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
                // If permissions are not granted, request them

                requestPermissions(
                    arrayOf<String>(
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ), REQUEST_PERMISSIONS
                )
            } else {
                // Permissions already granted, proceed with Bluetooth scanning
                startDiscovery()
            }
        }
        else {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf<String>(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ), REQUEST_PERMISSIONS
                )
            }
            else{
                startDiscovery()
            }
        }

    }
    fun pairDevice(device: BluetoothDevice): Boolean {
        showToast("Pairing...")
        return try {
            val method = device.javaClass.getMethod("createBond")
            val paired = method.invoke(device) as Boolean
            if (paired) {
                showToast("Paired successfully. Connecting...")
                // Call your connectDevice method here
                if (connectDevice(device)) {
                    showToast("Connected")
                    finish()
                    true
                } else {
                    showToast("Connection failed")
                    false
                }
            } else {
                showToast("Pairing failed")
                false
            }
        } catch (e: Exception) {
            showToast("Device Not Available")
            e.printStackTrace()
            false
        }
    }

    fun connectDevice(device: BluetoothDevice): Boolean {
        // Add your logic to connect the device
        // This is a placeholder for the connection logic
        // Return true if connection is successful, false otherwise
        // For example:
        return try {
            val method = device.javaClass.getMethod("connect")
            method.invoke(device)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun showToast(message: String) {
        Toast.makeText(this@ConnectionPairingActivity, message, Toast.LENGTH_SHORT).show()
    }
    @SuppressLint("MissingPermission")
    private fun startDiscovery() {
        // Register for broadcasts when a device is discovered
        val filter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        }
        registerReceiver(broadcastReceiver, filter,RECEIVER_EXPORTED)

        // Start discovery
        if (bluetoothAdapter!!.isDiscovering()) {
            bluetoothAdapter!!.cancelDiscovery()
        }
        bluetoothAdapter!!.startDiscovery()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSIONS) {
            // Check if all permissions were granted
            var allPermissionsGranted = true
            for (grantResult in grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false
                    break
                }
            }

            if (allPermissionsGranted) {
                // Permissions granted, proceed with Bluetooth scanning
                startDiscovery()
            } else {
                // Permissions not granted, show a message or handle accordingly
                Toast.makeText(
                    this,
                    "Permissions needed for Bluetooth scanning were not granted.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        else if (requestCode == REQUEST_BLUETOOTH_PERMISSION) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableBluetooth()
            } else {
                Toast.makeText(this, "Bluetooth permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.S)
    private fun checkAndRequestBluetoothPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf<String>(Manifest.permission.BLUETOOTH_CONNECT),
                    REQUEST_BLUETOOTH_PERMISSION
                )
                return false
            } else {
                enableBluetooth()
                return true
            }
        }
        else{
            enableBluetooth()
            return true
        }
    }
    @SuppressLint("MissingPermission")
    private fun enableBluetooth() {
        // request to enable bluetooth
        val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(enableIntent, REQUEST_ENABLE_BT)

//        bluetoothAdapter!!.enable()
        Toast.makeText(this, "Bluetooth is being enabled...", Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("MissingPermission")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode){
            REQUEST_ENABLE_BT-> {
                if (resultCode == RESULT_OK)
                {
                    bluetoothAdapter!!.enable()
                    startDiscovery()
                    binding.offbluetoothlinear.visibility= View.GONE
                    binding.mainscroll.visibility= View.VISIBLE
                    val pairedDevicesSet = bluetoothAdapter!!.bondedDevices
                    val pairedDevicesList: ArrayList<BluetoothDevice> = ArrayList(pairedDevicesSet)
                    val pairedadapter= PairedAdapter(this,pairedDevicesList)
                    binding.pairedrecycler.layoutManager= LinearLayoutManager(this)
                    binding.pairedrecycler.adapter= pairedadapter

                    pairedadapter!!.setItemClickListner(object : PairedAdapter.ItemClickInterface{
                        override fun onItemClick(position: Int) {
                            if (position > -1) {
                                device= pairedDevicesList!!.get(position)
                                connectedDevice= device

                                bluetoothAdapter!!.cancelDiscovery()

                                val intent = Intent()
                                intent.putExtra(BtDeviceListActivity.EXTRA_DEVICE_ADDRESS, device!!.address)

                                setResult(RESULT_OK, intent)
                                finish()

                            }
                        }
                    })

                }
            }
        }

    }


}