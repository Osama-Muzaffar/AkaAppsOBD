package com.akapps.obd2carscannerapp.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.SearchManager
import android.bluetooth.BluetoothAdapter
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.preference.PreferenceManager
import android.util.Log
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.LocaleListCompat
import com.google.android.gms.ads.AdError
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.akapps.androbd.plugin.mgr.PluginManager
import com.akapps.ecu.EcuCodeItem
import com.akapps.ecu.EcuDataItem
import com.akapps.ecu.EcuDataItems
import com.akapps.ecu.EcuDataPv
import com.akapps.ecu.prot.obd.ElmProt
import com.akapps.ecu.prot.obd.ObdProt
import com.akapps.ecu.prot.obd.ObdProt.OBD_SVC_DATA
import com.akapps.ecu.prot.obd.ObdProt.OBD_SVC_READ_CODES
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.DEVICE_ADDRESS
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.DEVICE_NAME
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.DEVICE_PORT
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.DISPLAY_UPDATE_TIME
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.ELM_ADAPTIVE_TIMING
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.ELM_CUSTOM_INIT_CMDS
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.ELM_RESET_ON_NRC
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.KEEP_SCREEN_ON
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.LOG_MASTER
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.MEASURE_SYSTEM
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.MESSAGE_DATA_ITEMS_CHANGED
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.MESSAGE_DEVICE_NAME
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.MESSAGE_FILE_READ
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.MESSAGE_FILE_WRITTEN
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.MESSAGE_OBD_ECUS
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.MESSAGE_OBD_NRC
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.MESSAGE_OBD_NUMCODES
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.MESSAGE_OBD_STATE_CHANGED
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.MESSAGE_STATE_CHANGE
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.MESSAGE_TOAST
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.MESSAGE_TOOLBAR_VISIBLE
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.MESSAGE_UPDATE_VIEW
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.NIGHT_MODE
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.PREF_DATA_DISABLE_MAX
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.PREF_FULLSCREEN
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.PREF_USE_LAST
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.REQUEST_CONNECT_DEVICE_INSECURE
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.REQUEST_CONNECT_DEVICE_SECURE
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.REQUEST_CONNECT_DEVICE_USB
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.REQUEST_ENABLE_BT
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.REQUEST_GRAPH_DISPLAY_DONE
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.REQUEST_SELECT_FILE
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.REQUEST_SETTINGS
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.TOAST
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.currDataAdapter
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.emptyStringSet
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.ignoreNrcs
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.initialBtStateEnabled
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.log
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.mBluetoothAdapter
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.mConnectedDeviceName
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.mDfcAdapter
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.mPidAdapter
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.mPluginDataAdapter
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.mPluginPvs
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.mTidAdapter
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.mVidAdapter
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.menu
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.mode
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.nightMode
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.prefs
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.rootLogger
import com.akapps.obd2carscannerapp.Activity.DashboardActivity.Constants.updateTimer
import com.akapps.obd2carscannerapp.Adapter.MainItemAdapter
import com.akapps.obd2carscannerapp.Ads.AdManager
import com.akapps.obd2carscannerapp.Ads.AdManagerCallback
import com.akapps.obd2carscannerapp.Ads.BannerAdView
import com.akapps.obd2carscannerapp.BtCommService
import com.akapps.obd2carscannerapp.BtDeviceListActivity
import com.akapps.obd2carscannerapp.BuildConfig
import com.akapps.obd2carscannerapp.CommService
import com.akapps.obd2carscannerapp.CommService.MEDIUM
import com.akapps.obd2carscannerapp.DfcItemAdapter
import com.akapps.obd2carscannerapp.Models.MainItemModel
import com.akapps.obd2carscannerapp.Models.SharedPreferencesHelper
import com.akapps.obd2carscannerapp.ObdItemAdapter
import com.akapps.obd2carscannerapp.PluginDataAdapter
import com.akapps.obd2carscannerapp.R
import com.akapps.obd2carscannerapp.TidItemAdapter
import com.akapps.obd2carscannerapp.Trips.PairedDeviceSharedPreference
import com.akapps.obd2carscannerapp.VidItemAdapter
import com.akapps.obd2carscannerapp.databinding.ActivityDashboardBinding
import com.akapps.pvs.ProcessVar
import com.akapps.pvs.PvChangeEvent
import com.akapps.pvs.PvChangeListener
import com.akapps.pvs.PvList
import com.quadsquad.savestatus.videodownloader.storysaver.statuskeeper.adsConfigs.adsConfig
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import java.util.Arrays
import java.util.Locale
import java.util.Objects
import java.util.Timer
import java.util.TimerTask
import java.util.TreeSet
import java.util.logging.FileHandler
import java.util.logging.Level
import java.util.logging.Logger


class DashboardActivity : PluginManager(), PvChangeListener, AdapterView.OnItemLongClickListener,
    PropertyChangeListener,
    SharedPreferences.OnSharedPreferenceChangeListener,
    AbsListView.MultiChoiceModeListener {

        lateinit var binding: ActivityDashboardBinding

    var mainAdapter: MainItemAdapter?=null
    var mainList: ArrayList<MainItemModel> = ArrayList()

    object Constants {


        @kotlin.jvm.JvmField
        var nightMode: Boolean= false

        @kotlin.jvm.JvmField
        var mPluginPvs: PvList =
            PvList()

        const val DEVICE_NAME = "device_name"
        const val TOAST = "toast"
        const val PREF_AUTOHIDE = "autohide_toolbar"
        const val PREF_FULLSCREEN = "full_screen"
        const val PREF_AUTOHIDE_DELAY = "autohide_delay"

        /**
         * Message types sent from the BluetoothChatService Handler
         */
        const val MESSAGE_STATE_CHANGE = 1
        const val MESSAGE_FILE_READ = 2
        const val MESSAGE_DEVICE_NAME = 4
        const val MESSAGE_TOAST = 5
        const val MESSAGE_UPDATE_VIEW = 7
        const val MESSAGE_TOOLBAR_VISIBLE = 12

        /**
         * Mapping list from Plugin.CsvField to EcuDataPv.key
         */
        val csvFidMap = arrayOf(
            EcuDataPv.FID_MNEMONIC,
            EcuDataPv.FIELDS[EcuDataPv.FID_DESCRIPT],
            EcuDataPv.FID_MIN,
            EcuDataPv.FID_MAX,
            EcuDataPv.FIELDS[EcuDataPv.FID_UNITS]
        )

        const val DEVICE_ADDRESS = "device_address"
        const val DEVICE_PORT = "device_port"
        const val MEASURE_SYSTEM = "measure_system"
        const val NIGHT_MODE = "night_mode"
        const val ELM_ADAPTIVE_TIMING = "adaptive_timing_mode"
        const val ELM_RESET_ON_NRC = "elm_reset_on_nrc"
        const val PREF_USE_LAST = "USE_LAST_SETTINGS"
        const val PREF_OVERLAY = "toolbar_overlay"
        const val PREF_DATA_DISABLE_MAX = "data_disable_max"
        const val MESSAGE_FILE_WRITTEN = 3
        const val MESSAGE_DATA_ITEMS_CHANGED = 6
        const val MESSAGE_OBD_STATE_CHANGED = 8
        const val MESSAGE_OBD_NUMCODES = 9
        const val MESSAGE_OBD_ECUS = 10
        const val MESSAGE_OBD_NRC = 11
        const val TAG = "AndrOBD"

        /**
         * internal Intent request codes
         */
        const val REQUEST_CONNECT_DEVICE_SECURE = 1
        const val REQUEST_CONNECT_DEVICE_INSECURE = 2
        const val REQUEST_ENABLE_BT = 3
        const val REQUEST_SELECT_FILE = 4
        const val REQUEST_SETTINGS = 5
        const val REQUEST_CONNECT_DEVICE_USB = 6
        const val REQUEST_GRAPH_DISPLAY_DONE = 7

        /**
         * app exit parameters
         */
        const val EXIT_TIMEOUT = 2500

        /**
         * time between display updates to represent data changes
         */
        const val DISPLAY_UPDATE_TIME = 250

        const val LOG_MASTER = "log_master"
        const val KEEP_SCREEN_ON = "keep_screen_on"
        const val ELM_CUSTOM_INIT_CMDS = "elm_custom_init_cmds"

        /**
         * Logging
         */
        val rootLogger = Logger.getLogger("")
        val log = Logger.getLogger(TAG)

        /**
         * Timer for display updates
         */
        var updateTimer: Timer? = null

        /**
         * empty string set as default parameter
         */
        val emptyStringSet = HashSet<String>()

//        /**
//         * Container for Plugin-provided data
//         */
//        val mPluginPvs = PvList()

        /**
         * app preferences ...
         */
        lateinit var prefs: SharedPreferences

        /**
         * dialog builder
         */
//        lateinit var dlgBuilder: AlertDialog.Builder

        /**
         * Local Bluetooth adapter
         */
        var mBluetoothAdapter: BluetoothAdapter? = null

        /**
         * Name of the connected BT device
         */
        var mConnectedDeviceName: String? = null

        /**
         * menu object
         */
        var menu: Menu?=null

        /**
         * Data list adapters
         */
        lateinit var mPidAdapter: ObdItemAdapter
        lateinit var mVidAdapter: VidItemAdapter
        lateinit var mTidAdapter: TidItemAdapter
        lateinit var mDfcAdapter: DfcItemAdapter
        lateinit var mPluginDataAdapter: PluginDataAdapter
        lateinit var currDataAdapter: ObdItemAdapter

        /**
         * initial state of bluetooth adapter
         */
        var initialBtStateEnabled = false

        /**
         * last time of back key pressed
         */
        var lastBackPressTime: Long = 0

        /**
         * toast for showing exit message
         */
        var exitToast: Toast? = null

        /**
         * Flag to temporarily ignore NRCs
         * This flag is used to temporarily allow negative OBD responses without issuing an error message.
         * i.e. un-supported mode 0x0A for DFC reading
         */
        var ignoreNrcs = false

        var mode: MODE = MODE.OFFLINE

    }

    private val ff_selected = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            CommService.elm.setFreezeFrame_Id(position)
        }

        override fun onNothingSelected(parent: AdapterView<*>) {}
    }

    /**
     * Member object for the BT comm services
     */
    private var mCommService: CommService? = null

    /**
     * file helper
     */
//    private lateinit var fileHelper: FileHelper

    /**
     * the local list view
     */
    private lateinit var mListView: View

    /**
     * current data view mode
     */
    private var dataViewMode: DATA_VIEW_MODE = DATA_VIEW_MODE.LIST

    /**
     * AutoHider for the toolbar
     */
//    private lateinit var toolbarAutoHider: AutoHider

    /**
     * log file handler
     */
    private lateinit var logFileHandler: FileHandler

    /**
     * current OBD service
     */
    private var obdService: Int = ElmProt.OBD_SVC_NONE



    /**
     * Handle message requests
     */
    /*
     * Implementations of PluginManager data interface callbacks
     */

    @SuppressLint("HandlerLeak")
    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            try {
                var evt: PropertyChangeEvent

                // log trace message for received handler notification event
                log.log(Level.FINEST, "Handler notification: %s".format(msg.toString()))

                when (msg.what) {
                    MESSAGE_STATE_CHANGE -> {
                        // log trace message for received handler notification event
                        log.log(Level.FINEST, "State change: %s".format(msg.toString()))
                        when (msg.obj as CommService.STATE) {
                            CommService.STATE.CONNECTED -> onConnect()
                            CommService.STATE.CONNECTING -> setStatus(R.string.connecting)
                            else -> onDisconnect()
                        }
                    }

                    MESSAGE_FILE_WRITTEN -> {}

                    // data has been read - finish up
                    MESSAGE_FILE_READ -> {
                        Log.d("DashboardActivity", "handleMessage: File read state")
                        // set listeners for data structure changes
                        setDataListeners()
                        // set adapters data source to loaded list instances
                        mPidAdapter.setPvList(ObdProt.PidPvs)
                        mVidAdapter.setPvList(ObdProt.VidPvs)
                        mTidAdapter.setPvList(ObdProt.VidPvs)
                        mDfcAdapter.setPvList(ObdProt.tCodes)
                        // set OBD data mode to the one selected by input file
                        setObdService(CommService.elm.getService(), getString(R.string.saved_data))
                        // Check if last data selection shall be restored
                        if (obdService == ObdProt.OBD_SVC_DATA) {
                            checkToRestoreLastDataSelection()
                            checkToRestoreLastViewMode()
                        }
                    }

                    MESSAGE_DEVICE_NAME -> {
                        // save the connected device's name
                        mainAdapter?.setEnableItems()
                        mConnectedDeviceName = msg.data.getString(DEVICE_NAME)
                        val address = msg.data.getString(DEVICE_ADDRESS)
                        binding.connectcard.visibility= View.GONE
                        binding.connectedlinear.visibility= View.VISIBLE
                        binding.statustxt.text = "Connected"
                        val colorStateList =
                            ColorStateList.valueOf(Color.GREEN) // Set your desired color here
                        // Set the background tint
                        binding.statusrelative.setBackgroundTintList(colorStateList)
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.connected_to) + mConnectedDeviceName,
                            Toast.LENGTH_SHORT
                        ).show()

                        PairedDeviceSharedPreference.getInstance(this@DashboardActivity)
                            .putBTDeviceAddress(address)
                        binding.mainconstraint.visibility= View.VISIBLE
                        binding.scanninglayout.visibility= View.GONE
                    }

                    MESSAGE_TOAST -> {
                        if(msg.data.getString(TOAST).equals(getString(R.string.unabletoconnect))){
                            binding.mainconstraint.visibility= View.VISIBLE
                            binding.scanninglayout.visibility= View.GONE
                        }
                        Toast.makeText(
                            applicationContext,
                            msg.data.getString(TOAST),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    MESSAGE_DATA_ITEMS_CHANGED -> {
                        val event = msg.obj as PvChangeEvent
                        when (event.type) {
                            PvChangeEvent.PV_ADDED -> {
                                currDataAdapter.setPvList(currDataAdapter.pvs)
                                try {
                                    if (event.source === ObdProt.PidPvs) {
                                        // append plugin measurements to data list
                                        currDataAdapter.addAll(mPluginPvs.values)
                                        // Check if last data selection shall be restored
                                        checkToRestoreLastDataSelection()
                                        checkToRestoreLastViewMode()
                                    }
                                } catch (e: Exception) {
                                    log.log(Level.FINER, "Error adding PV", e)
                                }
                            }

                            PvChangeEvent.PV_CLEARED -> currDataAdapter.clear()
                        }
                    }

                    MESSAGE_UPDATE_VIEW -> getListView().invalidateViews()

                    // handle state change in OBD protocol
                    MESSAGE_OBD_STATE_CHANGED -> {
                        evt = msg.obj as PropertyChangeEvent
                        val state = evt.newValue as ElmProt.STAT
                        /* Show ELM status only in ONLINE mode */
                        if (mode != MODE.DEMO) {
                            setStatus(resources.getStringArray(R.array.elmcomm_states)[state.ordinal])
                        }
                        // if last selection shall be restored ...
                        if (istRestoreWanted(PRESELECT.LAST_SERVICE)) {
                            if (state == ElmProt.STAT.ECU_DETECTED) {
                                setObdService(prefs.getInt(PRESELECT.LAST_SERVICE.toString(), 0), null)
                            }
                        }
                    }

                    // handle change in number of fault codes
                    MESSAGE_OBD_NUMCODES -> {
                        evt = msg.obj as PropertyChangeEvent
                        setNumCodes(evt.newValue as Int)
                    }

                    // handle ECU detection event
                    MESSAGE_OBD_ECUS -> {
                        evt = msg.obj as PropertyChangeEvent
                        selectEcu(evt.newValue as Set<Int>)
                    }

                    // handle negative result code from OBD protocol
                    MESSAGE_OBD_NRC -> {
                        // show error dialog ...
                      /*  if (!ignoreNrcs) {
                            evt = msg.obj as PropertyChangeEvent
                            val nrc = evt.oldValue as ObdProt.NRC
                            val nrcMsg = evt.newValue as String
                            when (nrc.disp) {
                                ObdProt.NRC.DISP.ERROR -> dlgBuilder
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setTitle(R.string.obd_error)
                                    .setMessage(nrcMsg)
                                    .setPositiveButton(null, null)
                                    .show()

                                // Display warning (with confirmation)
                                ObdProt.NRC.DISP.WARN -> dlgBuilder
                                    .setIcon(android.R.drawable.ic_dialog_info)
                                    .setTitle(R.string.obd_error)
                                    .setMessage(nrcMsg)
                                    .setPositiveButton(null, null)
                                    .show()

                                // Display notification (no confirmation)
                                ObdProt.NRC.DISP.NOTIFY -> Toast.makeText(
                                    applicationContext,
                                    nrcMsg,
                                    Toast.LENGTH_SHORT
                                ).show()

                                ObdProt.NRC.DISP.HIDE -> { *//* intentionally ignore *//* }
                            }
                        }*/
                    }

                    // set toolbar visibility
                    MESSAGE_TOOLBAR_VISIBLE -> {
                        val visible = msg.obj as Boolean
                        // log action
                        log.fine("ActionBar: %s".format(if (visible) "show" else "hide"))
                        // set action bar visibility
//                        val ab = actionBar
//                        ab?.let {
//                            if (visible) {
//                                // ab.show()
//                            } else {
//                                it.hide()
//                            }
//                        }
                    }
                }
            } catch (ex: Exception) {
                log.log(Level.SEVERE, "Error in mHandler", ex)
            }
        }
    }

    /**
     * Set fixed PIDs for protocol to specified list of PIDs
     *
     * @param pidNumbers List of PIDs
     */
    companion object{
        fun setFixedPids(pidNumbers: Set<Int>) {
            val pids = pidNumbers.sorted().toIntArray()
            // set protocol fixed PIDs
            ObdProt.setFixedPid(pids)
        }
    }



    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        requestWindowFeature(Window.FEATURE_PROGRESS)
        binding= ActivityDashboardBinding.inflate(layoutInflater)
        // get additional permissions
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            // Storage Permissions
//            val REQUEST_EXTERNAL_STORAGE = 1
//            val PERMISSIONS_STORAGE = arrayOf(
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE
//            )
//            requestPermissions(PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE)
//            // Workaround for FileUriExposedException in Android >= M
//            val builder = VmPolicy.Builder()
//            StrictMode.setVmPolicy(builder.build())
//        }

//        dlgBuilder = AlertDialog.Builder(this)

        binding.settingicon.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }

        binding.proicon.setOnClickListener {
            startActivity(Intent(this, PurchaseActivity::class.java)
                .putExtra("which","multi"))
        }

        // get preferences
        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        // register for later changes
        prefs.registerOnSharedPreferenceChangeListener(this)


        // Overlay feature has to be set before window content is set
//        if (prefs.getBoolean(PREF_AUTOHIDE, false)
//            && prefs.getBoolean(PREF_OVERLAY, false)
//        ) {
//            window.requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY)
//        }

//        actionBar!!.hide()

        // Set up all data adapters
        mPidAdapter = ObdItemAdapter(this, R.layout.obd_item, ObdProt.PidPvs)
        mVidAdapter = VidItemAdapter(this, R.layout.obd_item, ObdProt.VidPvs)
        mTidAdapter = TidItemAdapter(this, R.layout.obd_item, ObdProt.VidPvs)
        mDfcAdapter = DfcItemAdapter(this, R.layout.obd_item, ObdProt.tCodes)
        mPluginDataAdapter =
            PluginDataAdapter(this, R.layout.obd_item, mPluginPvs)
        currDataAdapter = mPidAdapter


        // get list view
        mListView = window.layoutInflater.inflate(R.layout.obd_list, null)


        // update all settings from preferences
        onSharedPreferenceChanged(prefs, null)


        // set up logging system
//        setupLoggers()


        // create file helper instance
//        fileHelper = FileHelper(this)

        // set listeners for data structure changes
        setDataListeners()

        // automate elm status display
        CommService.elm.addPropertyChangeListener(this)


        // set up action bar
        val actionBar = actionBar
        actionBar?.setDisplayShowTitleEnabled(true)

        // start automatic toolbar hider
//        setAutoHider(prefs.getBoolean(PREF_AUTOHIDE, false))


        // set content view
        setContentView(binding.root)


        findViewById<View>(R.id.connectcard).setOnClickListener {
//            if (getMode()== MODE.DEMO){
//                setObdService(ObdProt.OBD_SVC_READ_CODES, "")
//                startActivity(Intent(this@DashboardActivity,ScanningActivity::class.java))
//            }
//            else {
//                setMode(MODE.DEMO)
//            }
//            if (getMode() == MODE.ONLINE) {
//                setObdService(ObdProt.OBD_SVC_READ_CODES, "")
//                startActivity(Intent(this@DashboardActivity,ScanningActivity::class.java))
//            }
//            else{
                checkAndRequestBluetoothPermission()
//            }
        }


        binding.scanningcard.setOnClickListener {
            setObdService(ObdProt.OBD_SVC_READ_CODES, "")
            startActivity(Intent(this@DashboardActivity, ScanningActivity::class.java))
        }

        binding.disconnectcard.setOnClickListener {
            if (mCommService != null) {
                mCommService!!.stop()
            }
            setMode(MODE.OFFLINE)

        }
        // override comm medium with USB connect intent
        if ("android.hardware.usb.action.USB_DEVICE_ATTACHED" == intent.action) {
            CommService.medium = MEDIUM.USB
        }

        when (CommService.medium) {
            MEDIUM.BLUETOOTH -> {
                // Get local Bluetooth adapter
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                log.fine("Adapter: " + mBluetoothAdapter)
                // If BT is not on, request that it be enabled.
                if (getMode() != MODE.DEMO && mBluetoothAdapter != null) {
                    // remember initial bluetooth state
                    initialBtStateEnabled = mBluetoothAdapter!!.isEnabled
                    if (!initialBtStateEnabled) {
                        // request to enable bluetooth
//                        val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//                        startActivityForResult(enableIntent, REQUEST_ENABLE_BT)
                    } else {
                        // last device to be auto-connected?
                        if (istRestoreWanted(PRESELECT.LAST_DEV_ADDRESS)) {
                            // auto-connect ...
                            setMode(MODE.ONLINE)
                        } else {
                            // leave "connect" action to the user
                        }
                    }
                }
            }

            MEDIUM.USB, MEDIUM.NETWORK -> setMode(MODE.ONLINE)
        }

        if(PairedDeviceSharedPreference.getInstance(this).subsList.size>0){
            var isshwoingad= true
            for(purchases in PairedDeviceSharedPreference.getInstance(this).subsList){
//                    if (purchases.equals(getString(R.string.in_app_subscription_yearly_product_id))){
//                        isshwoingad= false
//                        break
//
//                    }
//                    else{
                isshwoingad= false
//                    }
            }
            if (isshwoingad){

                if(adsConfig !=null) {
                    if(adsConfig!!.main_banner_simple && adsConfig!!.main_banner_collapsible) {
                        val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                        bannerview.visibility = View.VISIBLE
                        bannerview.loadCollapsibleBanner(
                            this@DashboardActivity,
                            BuildConfig.admob_banner,
                            true
                        )
                    }
                    else if(adsConfig!!.main_banner_simple) {
                        val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                        bannerview.visibility = View.VISIBLE
                        bannerview.loadBanner(
                            this@DashboardActivity,
                            BuildConfig.admob_banner
                        )
                    }
                    else  if(adsConfig!!.main_banner_collapsible) {
                        val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                        bannerview.visibility = View.VISIBLE
                        bannerview.loadCollapsibleBanner(
                            this@DashboardActivity,
                            BuildConfig.admob_banner,
                            true
                        )
                    }
                    else{
                        val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                        bannerview.visibility = View.GONE
                    }

                }
                else {
                    Log.d("test_remote", "remote config is null")
                    val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                    bannerview.visibility = View.GONE
                }
            }
            else{
                val bannerview= findViewById<BannerAdView>(R.id.banneradsview)
                bannerview.visibility=View.GONE
            }
        }
        else {
            runOnUiThread {

                if(adsConfig !=null) {
                    if(adsConfig!!.main_banner_simple && adsConfig!!.main_banner_collapsible) {
                        val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                        bannerview.visibility = View.VISIBLE
                        bannerview.loadCollapsibleBanner(
                            this@DashboardActivity,
                            BuildConfig.admob_banner,
                            true
                        )
                    }
                    else if(adsConfig!!.main_banner_simple) {
                        val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                        bannerview.visibility = View.VISIBLE
                        bannerview.loadBanner(
                            this@DashboardActivity,
                            BuildConfig.admob_banner
                        )
                    }
                    else  if(adsConfig!!.main_banner_collapsible) {
                        val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                        bannerview.visibility = View.VISIBLE
                        bannerview.loadCollapsibleBanner(
                            this@DashboardActivity,
                            BuildConfig.admob_banner,
                            true
                        )
                    }
                    else{
                        val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                        bannerview.visibility = View.GONE
                    }

                }
                else {
                    Log.d("test_remote", "remote config is null")
                    val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                    bannerview.visibility = View.GONE
                }
            }

        }

  /*      if (!AppPurchase.getInstance().isPurchased()) {

            val bannerview= findViewById<BannerAdView>(R.id.bannerads)
            bannerview.loadCollapsibleBanner(this,"ca-app-pub-3940256099942544/9214589741",true)


            AdManager.getInstance().loadInterstitialAd(this,BuildConfig.admob_intersitial,
                object : InterstitialAdLoadCallback(){
                    override fun onAdFailedToLoad(p0: LoadAdError) {
                        super.onAdFailedToLoad(p0)
                        Log.d("Dashboard", "Intersitial ad load successfully")
                    }

                    override fun onAdLoaded(p0: InterstitialAd) {
                        super.onAdLoaded(p0)
                        Log.d("Dashboard", "Failed to Load: "+ p0.responseInfo)
                    }
                })

        }
        else{
            Log.d("Splash", "purchase true else is running ")
            var isshwoingad= true
            for(purchases in AppPurchase.getInstance().ownerIdInapps){
                if (purchases.equals("com.obd.inapp") || purchases.equals("com.obdblue.allpremium")){
                    isshwoingad= false
                }
                else{
                    isshwoingad= true
                }
            }
            Log.d("Splash", "isshwoingad = $isshwoingad ")
            if (isshwoingad){

                val bannerview= findViewById<BannerAdView>(R.id.bannerads)
                bannerview.loadCollapsibleBanner(this,"ca-app-pub-3940256099942544/9214589741",true)


                AdManager.getInstance().loadInterstitialAd(this,BuildConfig.admob_intersitial,
                    object : InterstitialAdLoadCallback(){
                        override fun onAdFailedToLoad(p0: LoadAdError) {
                            super.onAdFailedToLoad(p0)
                            Log.d("Dashboard", "Intersitial ad load successfully")
                        }

                        override fun onAdLoaded(p0: InterstitialAd) {
                            super.onAdLoaded(p0)
                            Log.d("Dashboard", "Failed to Load: "+ p0.responseInfo)
                        }
                    })

            }
        }
*/
        makeMainItems()
        val prefrences= SharedPreferencesHelper(this)

        val preflng= prefrences.getString(SharedPreferencesHelper.KEY_LANGUAGE,"en")
        setLocale(this,preflng)

    }

    @SuppressLint("MissingPermission")
    private fun enableBluetooth() {
        // request to enable bluetooth
        if (!mBluetoothAdapter!!.isEnabled) {
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT)

//        bluetoothAdapter!!.enable()
            Toast.makeText(this, "Bluetooth is being enabled...", Toast.LENGTH_SHORT).show()
        }
        else{
            Log.d("enableBluetooth", "enableBluetooth: set mode to online")
            setMode(MODE.ONLINE)
//            startActivityForResult(Intent(this@DashboardActivity, BluetoothPairingActivity::class.java),
//                REQUEST_CONNECT_DEVICE_SECURE)
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
                    REQUEST_ENABLE_BT
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

    fun makeMainItems(){
        mainList.clear()
        mainList.add(MainItemModel(R.drawable.ic_demo,getString(R.string.demo)))
        mainList.add(MainItemModel(R.drawable.ic_faults, getString(R.string.faults)))
        mainList.add(MainItemModel(R.drawable.ic_help, getString(R.string.help)))
        mainList.add(MainItemModel(R.drawable.ic_realtime, getString(R.string.realtime)))
        mainList.add(MainItemModel(R.drawable.ic_allsensors, getString(R.string.all_sensors)))
//        mainList.add(MainItemModel(R.drawable.ic_statistics,"Statistics"))
//        mainList.add(MainItemModel(R.drawable.ic_acceleration,"Acceleration Test"))
//        mainList.add(MainItemModel(R.drawable.ic_emissiontext,"Emission Test"))

        mainAdapter= MainItemAdapter(this,mainList,object : MainItemAdapter.MainItemInterface{
            override fun onMainItemClick(position: Int) {
                AdManager.getInstance().forceShowInterstitial(this@DashboardActivity,object : AdManagerCallback(){
                    override fun onNextAction() {
                        super.onNextAction()
                        Log.d("Dashboard", "Intersitial ad on Next Action")
                        if(position == 0){
                            if(getMode()== MODE.OFFLINE) {
                                setMode(MODE.DEMO)
                            }
                        }
                        if (position==1){
                            startActivity(Intent(this@DashboardActivity,FaultCodesActivity::class.java))
                        }
                        else if (position==2){
                            startActivity(Intent(this@DashboardActivity,HelpsActivity::class.java))
                        }
                        else if (position==3){
                            startActivity(Intent(this@DashboardActivity, MyRealTimeActivity::class.java))

                        }
                        else if (position==4){
                            setObdService(OBD_SVC_DATA,"")
                            startActivity(Intent(this@DashboardActivity,ObdDataActivity::class.java))

                        }
                    }

                    override fun onFailedToLoad(error: AdError?) {
                        super.onFailedToLoad(error)
                        Log.d("Dashboard", "Intersitial ad Failed ot load: ${error?.message}")
                        if(position == 0){
                            if(getMode()== MODE.OFFLINE) {
                                setMode(MODE.DEMO)
                            }
                        }
                        if (position==1){
                            startActivity(Intent(this@DashboardActivity,FaultCodesActivity::class.java))
                        }
                        else if (position==2){
                            startActivity(Intent(this@DashboardActivity,HelpsActivity::class.java))
                        }
                        else if (position==3){
                            startActivity(Intent(this@DashboardActivity, MyRealTimeActivity::class.java))

                        }
                        else if (position==4){
                            setObdService(OBD_SVC_DATA,"")
                            startActivity(Intent(this@DashboardActivity,ObdDataActivity::class.java))

                        }
                    }

                    override fun onAdLoaded() {
                        super.onAdLoaded()
                    }
                })

            }
        })
        binding.mainrv.adapter= mainAdapter
    }
    public override fun onStart() {
        super.onStart()
        // If the adapter is null, then Bluetooth is not supported
        if (CommService.medium == MEDIUM.BLUETOOTH && mBluetoothAdapter == null) {
            // start ELM protocol demo loop
            setMode(MODE.DEMO)
        }
    }

    override fun onPause() {
        super.onPause()

        // stop data display update timer
        updateTimer!!.cancel()
    }

    override fun onResume() {
        makeMainItems()

        // set up data display update timer
        updateTimer = Timer()
        val updateTask: TimerTask = object : TimerTask(
        ) {
            override fun run() {
                /* forward message to update the view */
                val msg = mHandler.obtainMessage(MESSAGE_UPDATE_VIEW)
                mHandler.sendMessage(msg)
            }
        }
        updateTimer!!.schedule(updateTask, 0, DISPLAY_UPDATE_TIME.toLong())

        when(getMode()){
            MODE.DEMO ->{
                mainAdapter!!.setEnableItems()
            }
            MODE.ONLINE ->{
                mainAdapter!!.setEnableItems()

            }
            MODE.OFFLINE ->{
                mainAdapter!!.setDisableItems()

            }
            else->{
                mainAdapter!!.setDisableItems()
            }
        }

        super.onResume()
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onDestroy()
     */
    @SuppressLint("MissingPermission")
    override fun onDestroy() {
        // Stop toolbar hider thread
//        setAutoHider(false)

        try {
            // Reduce ELM power consumption by setting it to sleep
            CommService.elm.goToSleep()
            // wait until message is out ...
            Thread.sleep(100, 0)
        } catch (e: InterruptedException) {
            // do nothing
            log.log(Level.FINER, e.localizedMessage)
        }

        /* don't listen to ELM data changes any more */
        removeDataListeners()
        // don't listen to ELM property changes any more
        CommService.elm.removePropertyChangeListener(this)

        // stop demo service if it was started
        setMode(MODE.OFFLINE)

        // stop communication service
        if (mCommService != null) {
            mCommService!!.stop()
        }

        // if bluetooth adapter was switched OFF before ...
        if (mBluetoothAdapter != null && !initialBtStateEnabled) {
            // ... turn it OFF again
            mBluetoothAdapter!!.disable()
        }
//
//        log.info(
//            String.format(
//                "%s %s finished",
//                getString(R.string.app_name),
//                getString(R.string.app_version)
//            )
//        )
//
//        /* remove log file handler, if available (file access was granted) */
//        if (logFileHandler != null) logFileHandler.close()
//        Logger.getLogger("").removeHandler(logFileHandler)

        super.onDestroy()
    }

    override fun setContentView(layoutResID: Int) {
        setContentView(layoutInflater.inflate(layoutResID, null))
    }

    override fun setContentView(view: View?) {
        super.setContentView(view)
//        listView.setOnTouchListener(toolbarAutoHider)
    }

    /**
     * handle pressing of the BACK-KEY
     */
    override fun onBackPressed() {
        super.onBackPressed()

/*        if (listAdapter === pluginHandler) {
            setObdService(obdService, null)
        } else {
            if (CommService.elm.service != ObdProt.OBD_SVC_NONE) {
                if (dataViewMode != DATA_VIEW_MODE.LIST) {
                    setDataViewMode(DATA_VIEW_MODE.LIST)
                    checkToRestoreLastDataSelection()
                } else {
                    setObdService(ObdProt.OBD_SVC_NONE, null)
                }
            } else {
                if (lastBackPressTime < System.currentTimeMillis() - EXIT_TIMEOUT) {
                    exitToast =
                        Toast.makeText(this, R.string.back_again_to_exit, Toast.LENGTH_SHORT)
                    exitToast!!.show()
                    lastBackPressTime = System.currentTimeMillis()
                } else {
                    if (exitToast != null) {
                        exitToast!!.cancel()
                    }
                    super.onBackPressed()
                }
            }
        }*/
    }

    /**
     * Handler for options menu creation event
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        menuInflater.inflate(R.menu.obd_services, menu.findItem(R.id.obd_services).subMenu)
        Constants.menu = menu
        // update menu item status for current conversion
        setConversionSystem(EcuDataItem.cnvSystem)
        return true
    }

    /**
     * Handler for Options menu selection
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.day_night_mode -> {
                // toggle night mode setting
                prefs.edit()
                    .putBoolean(NIGHT_MODE, !nightMode).apply()
                return true
            }

            R.id.secure_connect_scan -> {
                setMode(MODE.ONLINE)
                return true
            }

            R.id.reset_preselections -> {
                clearPreselections()
                recreate()
                return true
            }

            R.id.disconnect -> {
                // stop communication service
                if (mCommService != null) {
                    mCommService!!.stop()
                }
                setMode(MODE.OFFLINE)
                return true
            }

            R.id.settings -> {
                // Launch the BtDeviceListActivity to see devices and do scan
//                val settingsIntent = Intent(this, SettingsActivity::class.java)
//                startActivityForResult(settingsIntent, REQUEST_SETTINGS)
                return true
            }

            R.id.plugin_manager -> {
                setManagerView()
                return true
            }

            R.id.save -> {
                // save recorded data (threaded)
//                fileHelper.saveDataThreaded()
                return true
            }

            R.id.load -> {
                setMode(MODE.FILE)
                return true
            }

            R.id.service_none -> {
                setObdService(ObdProt.OBD_SVC_NONE, item.title)
                return true
            }

            R.id.service_data -> {
                setObdService(ObdProt.OBD_SVC_DATA, item.title)
                return true
            }

            R.id.service_vid_data -> {
                setObdService(ObdProt.OBD_SVC_VEH_INFO, item.title)
                return true
            }

            R.id.service_freezeframes -> {
                setObdService(ObdProt.OBD_SVC_FREEZEFRAME, item.title)
                return true
            }

            R.id.service_testcontrol -> {
                setObdService(ObdProt.OBD_SVC_CTRL_MODE, item.title)
                return true
            }

            R.id.service_codes -> {
                setObdService(ObdProt.OBD_SVC_READ_CODES, item.title)
                return true
            }

            R.id.service_clearcodes -> {
                clearObdFaultCodes()
                setObdService(ObdProt.OBD_SVC_READ_CODES, item.title)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemCheckedStateChanged(
        mode: ActionMode?,
        position: Int,
        id: Long,
        checked: Boolean
    ) {
        // Intentionally do nothing
    }

    override fun onCreateActionMode(mode: ActionMode, menu: Menu?): Boolean {
        val inflater = mode.menuInflater
        inflater.inflate(R.menu.context_graph, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return false
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.chart_selected -> {
                setDataViewMode(DATA_VIEW_MODE.CHART)
                return true
            }

            R.id.hud_selected -> {
                setDataViewMode(DATA_VIEW_MODE.HEADUP)
                return true
            }

            R.id.dashboard_selected -> {
                setDataViewMode(DATA_VIEW_MODE.DASHBOARD)
                return true
            }

            R.id.filter_selected -> {
                setDataViewMode(DATA_VIEW_MODE.FILTERED)
                return true
            }
        }
        return false
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var secureConnection = false
        Log.d("onActivityResult", "request code : $requestCode")
         when (requestCode) {
                REQUEST_CONNECT_DEVICE_SECURE -> {
                    secureConnection = true
                    // When BtDeviceListActivity returns with a device to connect
                    if (resultCode == RESULT_OK) {
                        // Get the device MAC address
                        val address = Objects.requireNonNull(data!!.extras)!!.getString(
                            BtDeviceListActivity.EXTRA_DEVICE_ADDRESS
                        )
                        // save reported address as last setting
                        prefs.edit()
                            .putString(PRESELECT.LAST_DEV_ADDRESS.toString(), address).apply()
                        connectBtDevice(address, secureConnection)
                        binding.mainconstraint.visibility= View.GONE
                        binding.scanninglayout.visibility= View.VISIBLE
                        PairedDeviceSharedPreference.getInstance(this)
                            .putBTDeviceAddress(address)

                    } else {
                        setMode(MODE.OFFLINE)
                    }
                }

                REQUEST_CONNECT_DEVICE_INSECURE ->
                    if (resultCode == RESULT_OK) {
                        val address = Objects.requireNonNull(data!!.extras)!!.getString(
                            BtDeviceListActivity.EXTRA_DEVICE_ADDRESS
                        )
                        prefs.edit()
                            .putString(PRESELECT.LAST_DEV_ADDRESS.toString(), address).apply()
                        connectBtDevice(address, secureConnection)
                        binding.mainconstraint.visibility= View.GONE
                        binding.scanninglayout.visibility= View.VISIBLE
                    } else {
                        setMode(MODE.OFFLINE)
                    }

                REQUEST_CONNECT_DEVICE_USB ->   {              // DeviceListActivity returns with a device to connect
//                    if (resultCode == RESULT_OK) {
//                        mCommService = UsbCommService(this, mHandler)
//                        mCommService!!.connect(UsbDeviceListActivity.selectedPort, true)
//                    } else {
//                        setMode(MODE.OFFLINE)
//                    }
                }
                REQUEST_ENABLE_BT -> {
                    Log.d("onActivityResult", "onActivityResult: Request code = $REQUEST_ENABLE_BT")
                    // When the request to enable Bluetooth returns
                    if (resultCode == RESULT_OK) {
                        // Start online mode
                        Log.d("enableBluetooth", "enableBluetooth: set mode to result online")

                        setMode(MODE.ONLINE)
//                        startActivityForResult(Intent(this@DashboardActivity, BluetoothPairingActivity::class.java),
//                            REQUEST_CONNECT_DEVICE_SECURE)
                    } else {
                        // Start demo service Thread
                        setMode(MODE.DEMO)
                    }
                }

                REQUEST_SELECT_FILE -> if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    val uri = data?.data
                    log.info("Load content: $uri")
                    // load data ...
//                    fileHelper.loadDataThreaded(uri, mHandler)
                    // don't allow saving it again
                    setMenuItemEnable(R.id.save, false)
                    setMenuItemEnable(R.id.obd_services, true)
                }

                REQUEST_SETTINGS -> {}
                REQUEST_GRAPH_DISPLAY_DONE ->                 // let context know that we are in list mode again ...
                    dataViewMode = DATA_VIEW_MODE.LIST
            }

    }

    /**
     * Handler for result messages from other activities
     */
 /*   public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        var secureConnection = false

        if (resultCode != RESULT_CANCELED) {
            when (requestCode) {
                REQUEST_CONNECT_DEVICE_SECURE -> {
                    secureConnection = true
                    // When BtDeviceListActivity returns with a device to connect
                    if (resultCode == RESULT_OK) {
                        // Get the device MAC address
                        val address = Objects.requireNonNull(data.extras)!!.getString(
                            BtDeviceListActivity.EXTRA_DEVICE_ADDRESS
                        )
                        // save reported address as last setting
                        prefs.edit()
                            .putString(PRESELECT.LAST_DEV_ADDRESS.toString(), address).apply()
                        connectBtDevice(address, secureConnection)
                    } else {
                        setMode(MODE.OFFLINE)
                    }
                }

                REQUEST_CONNECT_DEVICE_INSECURE ->
                    if (resultCode == RESULT_OK) {
                        val address = Objects.requireNonNull(data.extras)!!.getString(
                            BtDeviceListActivity.EXTRA_DEVICE_ADDRESS
                        )
                        prefs.edit()
                            .putString(PRESELECT.LAST_DEV_ADDRESS.toString(), address).apply()
                        connectBtDevice(address, secureConnection)
                    } else {
                        setMode(MODE.OFFLINE)
                    }

                REQUEST_CONNECT_DEVICE_USB ->                 // DeviceListActivity returns with a device to connect
                    if (resultCode == RESULT_OK) {
                        mCommService = UsbCommService(this, mHandler)
                        mCommService!!.connect(UsbDeviceListActivity.selectedPort, true)
                    } else {
                        setMode(MODE.OFFLINE)
                    }

                REQUEST_ENABLE_BT -> {
                    Log.d("onActivityResult", "onActivityResult: Request code = $REQUEST_ENABLE_BT")
                    // When the request to enable Bluetooth returns
                    if (resultCode == RESULT_OK) {
                        // Start online mode
                        setMode(MODE.ONLINE)
                    } else {
                        // Start demo service Thread
                        setMode(MODE.DEMO)
                    }
                }

                REQUEST_SELECT_FILE -> if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    val uri = data.data
                    log.info("Load content: $uri")
                    // load data ...
                    fileHelper.loadDataThreaded(uri, mHandler)
                    // don't allow saving it again
                    setMenuItemEnable(R.id.save, false)
                    setMenuItemEnable(R.id.obd_services, true)
                }

                REQUEST_SETTINGS -> {}
                REQUEST_GRAPH_DISPLAY_DONE ->                 // let context know that we are in list mode again ...
                    dataViewMode = DATA_VIEW_MODE.LIST
            }
        }
    }
*/
    override fun onSharedPreferenceChanged(prefs: SharedPreferences, key: String?) {
        // keep main display on?
        if (key == null || KEEP_SCREEN_ON == key) {
            window.addFlags(
                if (prefs.getBoolean(KEEP_SCREEN_ON, false)
                ) WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                else 0
            )
        }

        // FULL SCREEN operation based on preference settings
        if ((key == null) || PREF_FULLSCREEN == key) {
            window.setFlags(
                if (prefs.getBoolean(PREF_FULLSCREEN, true)
                ) WindowManager.LayoutParams.FLAG_FULLSCREEN else 0,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        // night mode
        if ((key == null) || NIGHT_MODE == key) {
            setNightMode(prefs.getBoolean(NIGHT_MODE, false))
        }

        // set default comm medium
//        if ((key == null) || SettingsActivity.KEY_COMM_MEDIUM == key) {
//            CommService.medium =
//                MEDIUM.values()[getPrefsInt(SettingsActivity.KEY_COMM_MEDIUM, 0)]
//        }

        // enable/disable ELM adaptive timing
        if ((key == null) || ELM_ADAPTIVE_TIMING == key) {
            CommService.elm.mAdaptiveTiming.mode = ElmProt.AdaptTimingMode.valueOf(
                (prefs.getString(
                    ELM_ADAPTIVE_TIMING,
                    ElmProt.AdaptTimingMode.OFF.toString()
                ))!!
            )
        }

        // set protocol flag to initiate immediate reset on NRC reception
        if (key == null || ELM_RESET_ON_NRC == key) {
            CommService.elm.setResetOnNrc(prefs.getBoolean(ELM_RESET_ON_NRC, false))
        }

        // set custom ELM init commands
        if ((key == null) || ELM_CUSTOM_INIT_CMDS == key) {
            val value = prefs.getString(ELM_CUSTOM_INIT_CMDS, null)
            if (value != null && value.length > 0) {
                CommService.elm.setCustomInitCommands(
                    value.split("\n".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray())
            }
        }

        // ELM timeout
//        if ((key == null) || SettingsActivity.ELM_MIN_TIMEOUT == key) {
//            CommService.elm.mAdaptiveTiming.elmTimeoutMin = getPrefsInt(
//                SettingsActivity.ELM_MIN_TIMEOUT,
//                CommService.elm.mAdaptiveTiming.elmTimeoutMin
//            )
//        }

        // ... measurement system
        if ((key == null) || MEASURE_SYSTEM == key) {
            setConversionSystem(getPrefsInt(MEASURE_SYSTEM, EcuDataItem.SYSTEM_METRIC))
        }

        // ... preferred protocol
//        if (key == null || SettingsActivity.KEY_PROT_SELECT == key) {
//            ElmProt.setPreferredProtocol(getPrefsInt(SettingsActivity.KEY_PROT_SELECT, 0))
//        }

        // log levels
        if (key == null || LOG_MASTER == key) {
            setLogLevels()
        }

        // update from protocol extensions
        if (key == null || key.startsWith("ext_file-")) {
            loadPreferredExtensions()
        }

        // set disabled ELM commands
//        if (key == null || SettingsActivity.ELM_CMD_DISABLE == key) {
//            ElmProt.disableCommands(prefs.getStringSet(SettingsActivity.ELM_CMD_DISABLE, null))
//        }

        // AutoHide ToolBar
//        if ((key == null) || PREF_AUTOHIDE == key || PREF_AUTOHIDE_DELAY == key) {
//            setAutoHider(prefs.getBoolean(PREF_AUTOHIDE, false))
//        }

        // Max. data disabling debounce counter
        if ((key == null) || PREF_DATA_DISABLE_MAX == key) {
            EcuDataItem.MAX_ERROR_COUNT = getPrefsInt(PREF_DATA_DISABLE_MAX, 3)
        }

        // Customized PID display color preference
        if (key != null) {
            // specific key -> update single
            updatePidColor(key)
            updatePidDisplayRange(key)
            updatePidUpdatePeriod(key)
        } else {
            // loop through all keys
            for (currKey: String in prefs.all.keys) {
                // update by key
                updatePidColor(currKey)
                updatePidDisplayRange(currKey)
                updatePidUpdatePeriod(currKey)
            }
        }
    }

    /**
     * Update PID PV display color from preference
     * @param key Preference key
     */
    private fun updatePidColor(key: String) {
        val pos = key.indexOf("/" + EcuDataPv.FID_COLOR)
        if (pos >= 0) {
            val mnemonic = key.substring(0, pos)
            val itm = EcuDataItems.byMnemonic[mnemonic]
            // Default BLACK is to detect key removal
            val color = prefs.getInt(key, Color.BLACK)
            if (Color.BLACK != color) {
                itm!!.pv[EcuDataPv.FID_COLOR] = color
                log.info(String.format("PID pref %s=#%08x", key, color))
            }
        }
    }

    /**
     * Update PID PV display color from preference
     * @param key Preference key
     */
    private fun updatePidDisplayRange(key: String) {
        val rangeFields = arrayOf(
            EcuDataPv.FID_MIN,
            EcuDataPv.FID_MAX
        )
        // Loop through <MIN/MAX>> fields
        for (field: String in rangeFields) {
            // If preference key matches PID/<MIN/MAX>
            val pos = key.indexOf("/$field")
            if (pos >= 0) {
                // Default MAX_VALUE is to detect key removal
                val value: Number = prefs.getFloat(key, Float.MAX_VALUE)
                if (Float.MAX_VALUE != value.toFloat()) {
                    // Find corresponding data item
                    val mnemonic = key.substring(0, pos)
                    val itm = EcuDataItems.byMnemonic[mnemonic]
                    // update display range limit in data item
                    itm!!.pv[field] = value

                    log.info(String.format("PID pref %s=%f", key, value))
                }
            }
        }
    }

    /**
     * Update customized PID display update period from preference
     * @param key Preference key
     */
    private fun updatePidUpdatePeriod(key: String) {
        // If preference key matches PID/<MIN/MAX>
        val pos = key.indexOf("/" + EcuDataPv.FID_UPDT_PERIOD)
        if (pos >= 0) {
            // Default MAX_VALUE is to detect key removal
            val value = prefs.getLong(key, 0)
            if (0L != value) {
                // Find corresponding data item
                val mnemonic = key.substring(0, pos)
                val itm = EcuDataItems.byMnemonic[mnemonic]
                // update display range limit in data item
                itm!!.updatePeriod_ms = value

//                log.info(String.format("PID pref %s=%f", key, value))
            }
        }
    }

    /**
     * Handle long licks on OBD data list items
     */
    override fun onItemLongClick(
        parent: AdapterView<*>?,
        view: View?,
        position: Int,
        id: Long
    ): Boolean {
        val intent: Intent
        val pv: EcuDataPv

        when (CommService.elm.service) {
            ObdProt.OBD_SVC_DATA -> {
                pv = listAdapter.getItem(position) as EcuDataPv
                /* only numeric values may be shown as graph/dashboard */
                if (pv[EcuDataPv.FID_VALUE] is Number) {
//                    DashBoardActivity.setAdapter(listAdapter)
//                    intent = Intent(this, DashBoardActivity::class.java)
//                    intent.putExtra(DashBoardActivity.POSITIONS, intArrayOf(position))
//                    startActivity(intent)
                }
            }

            ObdProt.OBD_SVC_READ_CODES, ObdProt.OBD_SVC_PERMACODES, ObdProt.OBD_SVC_PENDINGCODES -> try {
                intent = Intent(Intent.ACTION_WEB_SEARCH)
                val dfc = listAdapter.getItem(position) as EcuCodeItem
                intent.putExtra(
                    SearchManager.QUERY,
                    "OBD " + dfc[EcuCodeItem.FID_CODE].toString()
                )
                startActivity(intent)
            } catch (e: java.lang.Exception) {
                log.log(Level.SEVERE, "WebSearch DFC", e)
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            }

            ObdProt.OBD_SVC_VEH_INFO -> {
                // copy VID content to clipboard ...
                pv = listAdapter.getItem(position) as EcuDataPv
                val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(
                    pv[EcuDataPv.FID_DESCRIPT].toString(),
                    pv[EcuDataPv.FID_VALUE].toString()
                )
                clipboard.setPrimaryClip(clip)
                // Show Toast message
                Toast.makeText(this, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show()
            }

            ObdProt.OBD_SVC_CTRL_MODE -> {
                pv = listAdapter.getItem(position) as EcuDataPv
                // Confirm & perform OBD test control ...
                confirmObdTestControl(
                    pv[EcuDataPv.FID_DESCRIPT].toString(),
                    ObdProt.OBD_SVC_CTRL_MODE,
                    pv.getAsInt(EcuDataPv.FID_PID)
                )
            }
        }
        return true
    }

    /**
     * Handler for PV change events This handler just forwards the PV change
     * events to the android handler, since all adapter / GUI actions have to be
     * performed from the main handler
     *
     * @param event PvChangeEvent which is reported
     */
    @Synchronized
    override fun pvChanged(event: PvChangeEvent) {
        // forward PV change to the UI Activity
        val msg = mHandler.obtainMessage(MESSAGE_DATA_ITEMS_CHANGED)
        if (!event.isChildEvent) {
            msg.obj = event
            mHandler.sendMessage(msg)
        }
    }

    /**
     * Check if restore of specified preselection is wanted from settings
     *
     * @param preselect specified preselect
     * @return flag if preselection shall be restored
     */
    private fun istRestoreWanted(preselect: PRESELECT): Boolean {
        return prefs.getStringSet(
            PREF_USE_LAST,
            emptyStringSet
        )!!
            .contains(preselect.toString())
    }

    /**
     * Check if last data selection shall be restored
     *
     *
     * If previously selected items shall be re-selected, then re-select them
     */
    private fun checkToRestoreLastDataSelection() {
        // if last data items shall be restored
        if (istRestoreWanted(PRESELECT.LAST_ITEMS)) {
            // get preference for last seleted items
            val lastSelectedItems =
                toIntArray(prefs.getString(PRESELECT.LAST_ITEMS.toString(), ""))
            // select last selected items
            if (lastSelectedItems.size > 0) {
                if (!selectDataItems(lastSelectedItems)) {
                    // if items could not be applied
                    // remove invalid preselection
                    prefs.edit().remove(PRESELECT.LAST_ITEMS.toString()).apply()
                    log.warning(
                        String.format(
                            "Invalid preselection: %s",
                            lastSelectedItems.contentToString()
                        )
                    )
                }
            }
        }
    }

    /**
     * Check if last view mode shall be restored
     *
     *
     * If last view mode shall be restored by user settings,
     * then restore the last selected view mode
     */
    private fun checkToRestoreLastViewMode() {
        // if last view mode shall be restored
        if (istRestoreWanted(PRESELECT.LAST_VIEW_MODE)) {
            // set last data view mode
            val lastMode: DATA_VIEW_MODE =
                DATA_VIEW_MODE.valueOf(
                    prefs.getString(
                        PRESELECT.LAST_VIEW_MODE.toString(),
                        DATA_VIEW_MODE.LIST.toString()
                    )!!
                )
            setDataViewMode(lastMode)
        }
    }

    /**
     * convert result of Arrays.toString(int[]) back into int[]
     *
     * @param input String of array
     * @return int[] of String value
     */
    private fun toIntArray(input: String?): IntArray {
        var result = intArrayOf()
        var numValidEntries = 0
        try {
            val beforeSplit = input!!.replace("\\[|]|\\s".toRegex(), "")
            val split = beforeSplit.split(",".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            val ints = IntArray(split.size)
            for (s: String in split) {
                if (s.length > 0) {
                    ints[numValidEntries++] = s.toInt()
                }
            }
            result = ints.copyOf(numValidEntries)
        } catch (ex: java.lang.Exception) {
            log.severe(ex.toString())
        }

        return result
    }

    /**
     * Prompt for selection of a single ECU from list of available ECUs
     *
     * @param ecuAdresses List of available ECUs
     */
    private fun selectEcu(ecuAdresses: Set<Int>) {
        // if more than one ECUs available ...
        if (ecuAdresses.size > 1) {
            val preferredAddress =
                prefs.getInt(PRESELECT.LAST_ECU_ADDRESS.toString(), 0)
            // check if last preferred address matches any of the reported addresses
            if (istRestoreWanted(PRESELECT.LAST_ECU_ADDRESS)
                && ecuAdresses.contains(preferredAddress)
            ) {
                // set address
                CommService.elm.setEcuAddress(preferredAddress)
            } else {
                // NO match with preference -> allow selection

                // .. allow selection of single ECU address ...

                val entries = arrayOfNulls<CharSequence>(ecuAdresses.size)
                // create list of entries
                var i = 0
                for (addr: Int in ecuAdresses) {
                    entries[i++] = String.format("0x%X", addr)
                }
                // show dialog ...
//                dlgBuilder
//                    .setTitle(R.string.select_ecu_addr)
//                    .setItems(entries) { dialog, which ->
//                        val address = entries[which].toString().substring(2).toInt(16)
//                        // set address
//                        CommService.elm.setEcuAddress(address)
//                        // set this as preference (preference change will trigger ELM command)
//                        prefs.edit()
//                            .putInt(PRESELECT.LAST_ECU_ADDRESS.toString(), address)
//                            .apply()
//                    }
//                    .show()
            }
        }
    }

    /**
     * OnClick handler - Browse URL from content description
     *
     * @param view view source of click event
     */
    fun browseClickedUrl(view: View) {
        val url = view.contentDescription.toString()
        startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)))
    }

    /**
     * Unhide action bar
     */
    private fun unHideActionBar() {
//        if (toolbarAutoHider != null) {
//            toolbarAutoHider.showComponent()
//        }
    }

    protected fun setNightMode(nightMode: Boolean) {
        // store last mode selection
        Constants.nightMode = nightMode

        // Set display theme based on specified mode
        setTheme(if (nightMode) R.style.AppTheme_Dark else R.style.AppTheme)
        window.decorView.setBackgroundColor(if (nightMode) Color.BLACK else Color.WHITE)

        // Trigger screen update to get immediate reaction
        setObdService(obdService, null)
    }

    private fun setNumCodes(newNumCodes: Int) {
        // set list background based on MIL status
        val list = findViewById<View>(R.id.obd_list)
        list?.setBackgroundResource(
            if ((newNumCodes and 0x80) != 0
            ) R.drawable.mil_on
            else R.drawable.mil_off
        )
        // enable / disable freeze frames based on number of codes
        setMenuItemEnable(R.id.service_freezeframes, (newNumCodes != 0))
    }

    /**
     * Set enabled state for a specified menu item
     * * this includes shading disabled items to visualize state
     *
     * @param id      ID of menu item
     * @param enabled flag if to be enabled/disabled
     */
    private fun setMenuItemEnable(id: Int, enabled: Boolean) {
        if (menu != null) {
            val item = menu!!.findItem(id)
            if (item != null) {
                item.setEnabled(enabled)

                // if menu item has icon ...
                val icon = item.icon
                if (icon != null) {
                    // set it's shading
                    icon.alpha = if (enabled) 255 else 127
                }
            }
        }
    }

    /**
     * Set enabled state for a specified menu item
     * * this includes shading disabled items to visualize state
     *
     * @param id      ID of menu item
     * @param enabled flag if to be visible/invisible
     */
    private fun setMenuItemVisible(id: Int, enabled: Boolean) {
        if (menu != null) {
            val item = menu!!.findItem(id)
            item?.setVisible(enabled)
        }
    }

    /**
     * start/stop the autmatic toolbar hider
     */
    private fun setAutoHider(active: Boolean) {
//        // disable existing hider
//        if (toolbarAutoHider != null) {
//            // cancel auto hider
//            toolbarAutoHider.cancel()
//            // forget about it
////            toolbarAutoHider = null
//        }
//
//        // if new hider shall be activated
//        if (active) {
//            val timeout = getPrefsInt(PREF_AUTOHIDE_DELAY, 15)
//            toolbarAutoHider = AutoHider(
//                this,
//                mHandler,
//                (timeout * 1000).toLong()
//            )
//            // start with update resolution of 1 second
//            toolbarAutoHider.start(1000)
//        }
    }

    /**
     * Get preference int value
     *
     * @param key          preference key name
     * @param defaultValue numeric default value
     * @return preference int value
     */
    private fun getPrefsInt(key: String, defaultValue: Int): Int {
        var result = defaultValue

        try {
            result = prefs.getString(key, defaultValue.toString())!!.toInt()
        } catch (ex: java.lang.Exception) {
            // log error message
            log.severe(
                String.format(
                    "Preference '%s'(%d): %s",
                    key,
                    result,
                    ex.toString()
                )
            )
        }

        return result
    }

    /**
     * set listeners for data structure changes
     */
    private fun setDataListeners() {
        // add pv change listeners to trigger model updates
        ObdProt.PidPvs.addPvChangeListener(
            this,
            PvChangeEvent.PV_ADDED
                    or PvChangeEvent.PV_CLEARED
        )
        ObdProt.VidPvs.addPvChangeListener(
            this,
            (PvChangeEvent.PV_ADDED
                    or PvChangeEvent.PV_CLEARED)
        )
        ObdProt.tCodes.addPvChangeListener(
            this,
            (PvChangeEvent.PV_ADDED
                    or PvChangeEvent.PV_CLEARED)
        )
        mPluginPvs.addPvChangeListener(
            this,
            (PvChangeEvent.PV_ADDED
                    or PvChangeEvent.PV_CLEARED)
        )
    }

    /**
     * set listeners for data structure changes
     */
    private fun removeDataListeners() {
        // remove pv change listeners
        ObdProt.PidPvs.removePvChangeListener(this)
        ObdProt.VidPvs.removePvChangeListener(this)
        ObdProt.tCodes.removePvChangeListener(this)
        mPluginPvs.removePvChangeListener(this)
    }

    /**
     * get current operating mode
     */
    private fun getMode(): MODE {
        return mode
    }

    /**
     * set new operating mode
     *
     * @param mode new mode
     */
    private fun setMode(mode: MODE) {
        // if this is a mode change, or file reload ...
        var mode = mode
        if (mode != Constants.mode || mode == MODE.FILE) {
            if (mode != MODE.DEMO) {
                stopDemoService()
            }

            // Disable data updates in FILE mode
            ObdItemAdapter.allowDataUpdates = (mode != MODE.FILE)

            when (mode) {
                MODE.OFFLINE -> {
                    // update menu item states
                    setMenuItemVisible(R.id.disconnect, false)
                    setMenuItemVisible(R.id.secure_connect_scan, true)
                    setMenuItemEnable(R.id.obd_services, false)
                }

                MODE.ONLINE ->
                    when (CommService.medium) {
                    MEDIUM.BLUETOOTH ->
                        if (mBluetoothAdapter == null || !mBluetoothAdapter!!.isEnabled) {
                        Toast.makeText(this, getString(R.string.none_found), Toast.LENGTH_SHORT)
                            .show()
                        mode = MODE.OFFLINE
                    }
                        else {
                        // if pre-settings shall be used ...
                        val address = prefs.getString(
                            PRESELECT.LAST_DEV_ADDRESS.toString(),
                            null
                        )
                        if ((istRestoreWanted(PRESELECT.LAST_DEV_ADDRESS)
                                    && address != null)
                        ) {
                            // ... connect with previously connected device
                            connectBtDevice(
                                address,
                                prefs.getBoolean("bt_secure_connection", false)
                            )
                        } else {
                            // ... otherwise launch the BtDeviceListActivity to see devices and do scan
                            val serverIntent = Intent(
                                this,
                                ConnectionPairingActivity::class.java
                            )
                            startActivityForResult(
                                serverIntent,
                                if (prefs.getBoolean("bt_secure_connection", false)
                                ) REQUEST_CONNECT_DEVICE_SECURE
                                else REQUEST_CONNECT_DEVICE_INSECURE
                            )
                        }
                    }

                    MEDIUM.USB -> {
//                        val enableIntent = Intent(this, UsbDeviceListActivity::class.java)
//                        startActivityForResult(
//                            enableIntent,
//                            REQUEST_CONNECT_DEVICE_USB
//                        )
                    }

                    MEDIUM.NETWORK -> connectNetworkDevice(
                        prefs.getString(DEVICE_ADDRESS, null),
                        getPrefsInt(DEVICE_PORT, 23)
                    )
                }

                MODE.DEMO -> startDemoService()
                MODE.FILE -> {
                    setStatus(R.string.saved_data)
                    selectFileToLoad()
                }
            }
            // remember previous mode
            // set new mode
            Constants.mode = mode
            setStatus(mode.toString())
        }
    }

    /**
     * set mesaurement conversion system to metric/imperial
     *
     * @param cnvId ID for metric/imperial conversion
     */
    private fun setConversionSystem(cnvId: Int) {
        log.info("Conversion: " + resources.getStringArray(R.array.measure_options)[cnvId])
        if (EcuDataItem.cnvSystem != cnvId) {
            // set coversion system
            EcuDataItem.cnvSystem = cnvId
        }
    }

    /**
     * Set up loggers
     */
    private fun setupLoggers() {
        // set file handler for log file output
//        val logFileName = ((FileHelper.getPath(this) + File.separator) + "log")
//        try {
//            // ensure log directory is available
//            File(logFileName).mkdirs()
//            // Create new log file handler (max. 250 MB, 5 files rotated, non appending)
//            logFileHandler = FileHandler(
//                ("$logFileName/AndrOBD.log.%g.txt"),
//                250 * 1024 * 1024,
//                5,
//                false
//            )
//            // Set log message formatter
//            logFileHandler.formatter = object : SimpleFormatter(
//            ) {
//                val format: String = "%1\$tF\t%1\$tT.%1\$tL\t%4\$s\t%3\$s\t%5\$s%n"
//
//                @SuppressLint("DefaultLocale")
//                @Synchronized
//                override fun format(lr: LogRecord): String {
//                    return String.format(
//                        format,
//                        Date(lr.millis),
//                        lr.sourceClassName,
//                        lr.loggerName,
//                        lr.level.name,
//                        lr.message
//                    )
//                }
//            }
//            // add file logging ...
//            rootLogger.addHandler(logFileHandler)
//            // set
//            setLogLevels()
//        } catch (e: IOException) {
//            // try to log error (at least with system logging)
//            log.log(Level.SEVERE, logFileName, e)
//        }
    }

    fun setLocale(activity: Activity, langcode: String?) {
        if (langcode != "") {
            downloadSelectedLanguage(langcode)
            val appLocale = LocaleListCompat.forLanguageTags(langcode)
            AppCompatDelegate.setApplicationLocales(appLocale)
        }

    }


    fun downloadSelectedLanguage(lan: String?) {
        val splitInstallManager = SplitInstallManagerFactory.create(this)
        val locales: MutableList<Locale> = ArrayList()
        locales.add(Locale.forLanguageTag(lan))
        splitInstallManager.deferredLanguageInstall(locales)
    }
    /**
     * Set logging levels from shared preferences
     */
    private fun setLogLevels() {
        // get level from preferences
        var level: Level?
        try {
            level = Level.parse(prefs.getString(LOG_MASTER, "INFO"))
        } catch (e: java.lang.Exception) {
            level = Level.INFO
        }

        // set logger main level
        rootLogger.level = level
    }

    /**
     * Load optional extension files which may have
     * been defined in preferences
     */
    private fun loadPreferredExtensions() {
//        var errors = ""
//
//        // custom conversions
//        try {
//            val filePath = prefs.getString(SettingsActivity.extKeys[0], null)
//            if (filePath != null) {
//                log.info("Load ext. conversions: $filePath")
//                val uri = Uri.parse(filePath)
//                val inStr = contentResolver.openInputStream(uri)
//                EcuDataItems.cnv.loadFromStream(inStr)
//            }
//        } catch (e: java.lang.Exception) {
//            log.log(Level.SEVERE, "Load ext. conversions: ", e)
//            e.printStackTrace()
//            errors += e.localizedMessage + "\n"
//        }
//
//        // custom PIDs
//        try {
//            val filePath = prefs.getString(SettingsActivity.extKeys[1], null)
//            if (filePath != null) {
//                log.info("Load ext. conversions: $filePath")
//                val uri = Uri.parse(filePath)
//                val inStr = contentResolver.openInputStream(uri)
//                ObdProt.dataItems.loadFromStream(inStr)
//            }
//        } catch (e: java.lang.Exception) {
//            log.log(Level.SEVERE, "Load ext. PIDs: ", e)
//            e.printStackTrace()
//            errors += e.localizedMessage + "\n"
//        }
//
//        if (errors.length != 0) {
//            dlgBuilder
//                .setIcon(android.R.drawable.ic_dialog_alert)
//                .setTitle(R.string.extension_loading)
//                .setMessage(getString(R.string.check_cust_settings) + errors)
//                .show()
//        }
    }

    /**
     * Stop demo mode Thread
     */
    private fun stopDemoService() {
        if (getMode() == MODE.DEMO) {
            ElmProt.runDemo = false
            binding.connectcard.visibility= View.VISIBLE
            binding.connectedlinear.visibility= View.GONE
            binding.statustxt.text = "Disconnected"
            val colorStateList =
                ColorStateList.valueOf(Color.RED) // Set your desired color here
            // Set the background tint
            binding.statusrelative.setBackgroundTintList(colorStateList)
            mainAdapter.let {
                it?.setDisableItems()
            }
            Toast.makeText(this, getString(R.string.demo_stopped), Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Start demo mode Thread
     */
    private fun startDemoService() {
        if (getMode() != MODE.DEMO) {
            binding.connectcard.visibility= View.GONE
            binding.connectedlinear.visibility= View.VISIBLE
            binding.statustxt.text = "Connected"
            val colorStateList =
                ColorStateList.valueOf(Color.GREEN) // Set your desired color here
            // Set the background tint
            binding.statusrelative.setBackgroundTintList(colorStateList)
            mainAdapter.let {
                it!!.setEnableItems()
            }
            setStatus(getString(R.string.demo))
            Toast.makeText(this, getString(R.string.demo_started), Toast.LENGTH_SHORT).show()

            val allowConnect =
                mBluetoothAdapter != null && mBluetoothAdapter!!.isEnabled
            setMenuItemVisible(R.id.secure_connect_scan, allowConnect)
            setMenuItemVisible(R.id.disconnect, !allowConnect)

            setMenuItemEnable(R.id.obd_services, true)
            /* The Thread object for processing the demo mode loop */
            val demoThread = Thread(CommService.elm)
            demoThread.start()
        }
    }

    /**
     * set status message in status bar
     *
     * @param resId Resource ID of the text to be displayed
     */
    private fun setStatus(resId: Int) {
        setStatus(getString(resId))
    }

    /**
     * set status message in status bar
     *
     * @param subTitle status text to be set
     */
    private fun setStatus(subTitle: CharSequence) {
        val actionBar = actionBar
        if (actionBar != null) {
            actionBar.subtitle = subTitle
            // show action bar to make state change visible
            unHideActionBar()
        }
    }

    /**
     * Select file to be loaded
     */
    private fun selectFileToLoad() {
//        val file = File(FileHelper.getPath(this))
//        val intent = Intent(Intent.ACTION_GET_CONTENT)
//        intent.addCategory(Intent.CATEGORY_OPENABLE)
//        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//        val uri = FileProvider.getUriForFile(this@DashboardActivity, packageName + ".provider", file)
//        val type = "*/*"
//        intent.setDataAndType(uri, type)
//        startActivityForResult(intent, REQUEST_SELECT_FILE)
    }

    /**
     * clear all preselections
     */
    private fun clearPreselections() {
        for (selection: PRESELECT in PRESELECT.values()) {
            prefs.edit().remove(selection.toString()).apply()
        }
    }

    /**
     * Initiate a connect to the selected bluetooth device
     *
     * @param address bluetooth device address
     * @param secure  flag to indicate if the connection shall be secure, or not
     */
    private fun connectBtDevice(address: String?, secure: Boolean) {
        // Get the BluetoothDevice object
        val device = mBluetoothAdapter!!.getRemoteDevice(address)
        // Attempt to connect to the device
        mCommService = BtCommService(this, mHandler)
        mCommService!!.connect(device, secure)
    }

    /**
     * Initiate a connect to the selected network device
     *
     * @param address IP device address
     * @param port    IP port to connect to
     */
    private fun connectNetworkDevice(address: String?, port: Int) {
        // Attempt to connect to the device
//        mCommService = NetworkCommService(this, mHandler)
//        (mCommService as NetworkCommService).connect(address, port)
    }

    /**
     * Activate desired OBD service
     *
     * @param newObdService OBD service ID to be activated
     */
    private fun setObdService(newObdService: Int, menuTitle: CharSequence?) {
        // remember this as current OBD service
        obdService = newObdService
        ignoreNrcs = false

        if (obdService!=OBD_SVC_DATA && obdService!=OBD_SVC_READ_CODES) {
            setContentView(mListView)
            listView.onItemLongClickListener = this
            listView.setMultiChoiceModeListener(this)
            listView.choiceMode = ListView.CHOICE_MODE_SINGLE


            // set title
            val ab = actionBar
            if (ab != null) {
                // title specified ... show it
                if (menuTitle != null) {
                    ab.title = menuTitle
                } else {
                    // no title specified, set to app name if no service set
                    if (newObdService == ElmProt.OBD_SVC_NONE) {
                        ab.title = getString(R.string.app_name)
                    }
                }
            }

            // show / hide freeze frame selector */
            val ff_selector = findViewById<Spinner>(R.id.ff_selector)
            ff_selector.onItemSelectedListener = ff_selected
            ff_selector.adapter = mDfcAdapter
            ff_selector.visibility =
                if (newObdService == ObdProt.OBD_SVC_FREEZEFRAME) View.VISIBLE else View.GONE
        }
        // set protocol service
        CommService.elm.setService(
            newObdService,
            (getMode() != MODE.FILE && getMode() != MODE.OFFLINE)
        )
        when (newObdService) {
            ObdProt.OBD_SVC_DATA -> {
//                listView.choiceMode = ListView.CHOICE_MODE_MULTIPLE_MODAL
                currDataAdapter = mPidAdapter
            }

            ObdProt.OBD_SVC_FREEZEFRAME -> currDataAdapter = mPidAdapter
            ObdProt.OBD_SVC_PENDINGCODES, ObdProt.OBD_SVC_PERMACODES, ObdProt.OBD_SVC_READ_CODES -> {
                // NOT all DFC modes are supported by all vehicles, disable NRC handling for this request
                ignoreNrcs = true
                currDataAdapter = mDfcAdapter
            }

            ObdProt.OBD_SVC_CTRL_MODE -> currDataAdapter = mTidAdapter
            ObdProt.OBD_SVC_NONE -> {
                setContentView(R.layout.startup_layout)
                currDataAdapter = mVidAdapter
            }

            ObdProt.OBD_SVC_VEH_INFO -> currDataAdapter = mVidAdapter
        }
        // un-filter display
        setFiltered(false)

        listAdapter = currDataAdapter

        // remember this as last selected service
        if (newObdService > ObdProt.OBD_SVC_NONE) {
            prefs.edit().putInt(PRESELECT.LAST_SERVICE.toString(), newObdService)
                .apply()
        }
    }

    /**
     * Filter display items to just the selected ones
     */
    private fun setFiltered(filtered: Boolean) {
        if (filtered) {
            val selPids = TreeSet<Int>()
            val selectedPositions = getSelectedPositions()
            for (pos: Int in selectedPositions) {
                val pv = currDataAdapter.getItem(pos) as EcuDataPv?
                selPids.add(pv?.getAsInt(EcuDataPv.FID_PID) ?: 0)
            }
            currDataAdapter.filterPositions(selectedPositions)

            if (currDataAdapter === mPidAdapter) setFixedPids(
                selPids
            )
        } else {
            if (currDataAdapter === mPidAdapter) ObdProt.resetFixedPid()

            /* Return to original PV list */
            if (currDataAdapter === mPidAdapter) {
                currDataAdapter.setPvList(ObdProt.PidPvs)
                // append plugin measurements to data list
                currDataAdapter.addAll(mPluginPvs.values)
            } else if (currDataAdapter === mVidAdapter) currDataAdapter.setPvList(
                ObdProt.VidPvs
            )
            else if (currDataAdapter === mDfcAdapter) currDataAdapter.setPvList(
                ObdProt.tCodes
            )
            else if (currDataAdapter === mPluginDataAdapter) currDataAdapter.setPvList(
                mPluginPvs
            )
        }
    }

    /**
     * get the Position in model of the selected items
     *
     * @return Array of selected item positions
     */
    private fun getSelectedPositions(): IntArray {
        var selectedPositions: IntArray
        // SparseBoolArray - what a garbage data type to return ...
        val checkedItems = listView.checkedItemPositions
        // get number of items
        val checkedItemsCount = listView.checkedItemCount
        // dimension array
        selectedPositions = IntArray(checkedItemsCount)
        if (checkedItemsCount > 0) {
            var j = 0
            // loop through findings
            for (i in 0 until checkedItems.size()) {
                // Item position in adapter
                if (checkedItems.valueAt(i)) {
                    selectedPositions[j++] = checkedItems.keyAt(i)
                }
            }
            // trim to really detected value (workaround for invalid length reported)
            selectedPositions = selectedPositions.copyOf(j)
        }
        val strPreselect = selectedPositions.contentToString()
        log.fine("Preselection: '$strPreselect'")
        // save this as last seleted positions
        prefs.edit().putString(PRESELECT.LAST_ITEMS.toString(), strPreselect)
            .apply()
        return selectedPositions
    }

    /**
     * Set selection status on specified list item positions
     *
     * @param positions list of positions to be set
     * @return flag if selections could be applied
     */
    private fun selectDataItems(positions: IntArray): Boolean {
        val count: Int
        val max: Int
        val positionsValid: Boolean

        Arrays.sort(positions)
        max = if (positions.size > 0) positions[positions.size - 1] else 0
        count = listAdapter.count
        positionsValid = (max < count)
        // if all positions are valid for current list ...
        if (positionsValid) {
            // set list items as selected
            for (i: Int in positions) {
                listView.setItemChecked(i, true)
            }
        }

        // return validity of positions
        return positionsValid
    }

    /**
     * Handle bluetooth connection established ...
     */
    @SuppressLint("StringFormatInvalid")
    private fun onConnect() {
        stopDemoService()

        mode = MODE.ONLINE
        // handle further initialisations
        setMenuItemVisible(R.id.secure_connect_scan, false)
        setMenuItemVisible(R.id.disconnect, true)

        setMenuItemEnable(R.id.obd_services, true)
        // display connection status
        setStatus(getString(R.string.title_connected_to, mConnectedDeviceName))
        // send RESET to Elm adapter
        CommService.elm.reset()
    }

    /**
     * Handle bluetooth connection lost ...
     */
    private fun onDisconnect() {
        // handle further initialisations
        setMode(MODE.OFFLINE)
        binding.connectcard.visibility= View.VISIBLE
        binding.connectedlinear.visibility= View.GONE
        binding.statustxt.text = "Disconnected"
        val colorStateList =
            ColorStateList.valueOf(Color.RED) // Set your desired color here
        // Set the background tint
        binding.statusrelative.setBackgroundTintList(colorStateList)
        mainAdapter.let {
            it?.setDisableItems()
        }
    }

    /**
     * Property change listener to ELM-Protocol
     *
     * @param evt the property change event to be handled
     */
    override fun propertyChange(evt: PropertyChangeEvent) {
        /* handle protocol status changes */
        if ((ElmProt.PROP_STATUS == evt.propertyName)) {
            // forward property change to the UI Activity
            val msg = mHandler.obtainMessage(MESSAGE_OBD_STATE_CHANGED)
            msg.obj = evt
            mHandler.sendMessage(msg)
        } else {
            if ((ElmProt.PROP_NUM_CODES == evt.propertyName)) {
                // forward property change to the UI Activity
                val msg = mHandler.obtainMessage(MESSAGE_OBD_NUMCODES)
                msg.obj = evt
                mHandler.sendMessage(msg)
            } else {
                if ((ElmProt.PROP_ECU_ADDRESS == evt.propertyName)) {
                    // forward property change to the UI Activity
                    val msg = mHandler.obtainMessage(MESSAGE_OBD_ECUS)
                    msg.obj = evt
                    mHandler.sendMessage(msg)
                } else {
                    if ((ObdProt.PROP_NRC == evt.propertyName)) {
                        // forward property change to the UI Activity
                        val msg = mHandler.obtainMessage(MESSAGE_OBD_NRC)
                        msg.obj = evt
                        mHandler.sendMessage(msg)
                    }
                }
            }
        }
    }

    /**
     * clear OBD fault codes after a warning
     * confirmation dialog is shown and the operation is confirmed
     */
    private fun clearObdFaultCodes() {
//        dlgBuilder
//            .setIcon(android.R.drawable.ic_dialog_info)
//            .setTitle(R.string.obd_clearcodes)
//            .setMessage(R.string.obd_clear_info)
//            .setPositiveButton(
//                android.R.string.yes
//            ) { dialog, which -> // set service CLEAR_CODES to clear the codes
//                CommService.elm.service = ObdProt.OBD_SVC_CLEAR_CODES
//                // set service READ_CODES to re-read the codes
//                CommService.elm.service = ObdProt.OBD_SVC_READ_CODES
//            }
//            .setNegativeButton(android.R.string.no, null)
//            .show()
    }

    /**
     * confirm OBD test control
     * confirmation dialog is shown and the operation is confirmed
     */
    private fun confirmObdTestControl(testControlName: String, service: Int, tid: Int) {
//        dlgBuilder
//            .setIcon(android.R.drawable.ic_dialog_alert)
//            .setTitle(testControlName)
//            .setMessage(R.string.obd_test_confirm)
//            .setPositiveButton(
//                android.R.string.yes
//            ) { dialog, which -> runObdTestControl(testControlName, service, tid) }
//            .setNegativeButton(android.R.string.no, null)
//            .show()
    }

    /**
     * perform OBD test control
     * confirmation dialog is shown and the operation is confirmed
     */
    private fun runObdTestControl(testControlName: String, service: Int, tid: Int) {
        // start desired test TID
        val emptyBuffer = charArrayOf()
        CommService.elm.writeTelegram(emptyBuffer, service, tid)

//        // Show test progress message
//        dlgBuilder
//            .setIcon(android.R.drawable.ic_dialog_info)
//            .setTitle(testControlName)
//            .setMessage(R.string.obd_test_progress)
//            .setPositiveButton(
//                android.R.string.ok
//            ) { dialog, which -> }
//            .setNegativeButton(null, null)
//            .show()
    }

    /**
     * Set new data view mode
     *
     * @param dataViewMode new data view mode
     */
    private fun setDataViewMode(dataViewMode: DATA_VIEW_MODE) {
        // if this is a real change ...
        if (dataViewMode != this.dataViewMode) {
            log.info(
                String.format(
                    "Set view mode: %s -> %s",
                    this.dataViewMode, dataViewMode
                )
            )

            when (dataViewMode) {
                DATA_VIEW_MODE.LIST -> {
                    setFiltered(false)
                    listView.choiceMode = ListView.CHOICE_MODE_MULTIPLE_MODAL
                    this.dataViewMode = dataViewMode
                }

                DATA_VIEW_MODE.FILTERED -> if (listView.checkedItemCount > 0) {
                    setFiltered(true)
                    listView.choiceMode = ListView.CHOICE_MODE_SINGLE
                    this.dataViewMode = dataViewMode
                }

                DATA_VIEW_MODE.HEADUP, DATA_VIEW_MODE.DASHBOARD -> if (listView.checkedItemCount > 0) {
//                    DashBoardActivity.setAdapter(listAdapter)
//                    val intent = Intent(this, DashBoardActivity::class.java)
//                    intent.putExtra(DashBoardActivity.POSITIONS, getSelectedPositions())
//                    intent.putExtra(
//                        DashBoardActivity.RES_ID,
//                        if (dataViewMode == DATA_VIEW_MODE.DASHBOARD
//                        ) R.layout.dashboard
//                        else R.layout.head_up
//                    )
//                    startActivityForResult(intent, REQUEST_GRAPH_DISPLAY_DONE)
//                    this.dataViewMode = dataViewMode
                }

                DATA_VIEW_MODE.CHART -> if (listView.checkedItemCount > 0) {
//                    ChartActivity.setAdapter(listAdapter)
//                    val intent = Intent(this, ChartActivity::class.java)
//                    intent.putExtra(ChartActivity.POSITIONS, getSelectedPositions())
//                    startActivityForResult(intent, REQUEST_GRAPH_DISPLAY_DONE)
//                    this.dataViewMode = dataViewMode
                }
            }
            // remember this as the last data view mode (if not regular list)
            if (dataViewMode != DATA_VIEW_MODE.LIST) {
                prefs.edit()
                    .putString(PRESELECT.LAST_VIEW_MODE.toString(), dataViewMode.toString())
                    .apply()
            }
        }
    }

    override fun onDataListUpdate(csvString: String) {
//        log.log(Level.FINE, "PluginDataList: $csvString")
//        // append unknown items to list of known items
//        synchronized(mPluginPvs) {
//            for (csvLine: String in csvString.split("\n".toRegex())
//                .dropLastWhile { it.isEmpty() }
//                .toTypedArray()) {
//                val fields: Array<String> =
//                    csvLine.split(";".toRegex()).dropLastWhile { it.isEmpty() }
//                        .toTypedArray()
//                if (fields.size >= CsvField.values().size) {
//                    // check if PV already is known ...
//                    var pv: PluginDataPv? =
//                        mPluginPvs.get(fields.get(CsvField.MNEMONIC.ordinal)) as PluginDataPv?
//                    // if not, create a new one
//                    if (pv == null) {
//                        pv = PluginDataPv()
//                    }
//                    // fill field content
//                    for (fld: CsvField in CsvField.values()) {
//                        try {
//                            // if content is numeric, set numeric value
//                            val value: Double = fields.get(fld.ordinal).toDouble()
//                            pv.put(csvFidMap.get(fld.ordinal), value)
//                        } catch (ex: java.lang.Exception) {
//                            pv.put(csvFidMap.get(fld.ordinal), fields.get(fld.ordinal))
//                        }
//                    }
//                    // add/update into pv list
//                    mPluginPvs.put(pv.getKeyValue(), pv)
//                }
//            }
//        }
    }

    override fun onDataUpdate(key: String, value: String) {
        log.log(Level.FINE, "PluginData: $key=$value")
        // Update value of plugin data item
        synchronized(mPluginPvs) {
            val pv: ProcessVar? = mPluginPvs.get(key) as ProcessVar?
            if (pv != null) {
                try {
                    // if content is numeric, set numeric value
                    val numVal: Double = value.toDouble()
                    pv.put(
                        EcuDataPv.FIELDS.get(
                            EcuDataPv.FID_VALUE), numVal)
                } catch (ex: java.lang.Exception) {
                    pv.put(
                        EcuDataPv.FIELDS.get(
                            EcuDataPv.FID_VALUE), value)
                }
            }
        }
    }


    /*
     * Implementations of PluginManager data interface callbacks
     */
    /**
     * operating modes
     */
    enum class MODE {
        OFFLINE,  //< OFFLINE mode
        ONLINE,  //< ONLINE mode
        DEMO,  //< DEMO mode
        FILE,  //< FILE mode
    }

    /**
     * data view modes
     */
    enum class DATA_VIEW_MODE {
        LIST,  //< data list (un-filtered)
        FILTERED,  //< data list (filtered)
        DASHBOARD,  //< dashboard
        HEADUP,  //< Head up display
        CHART,  //< Chart display
    }

    /**
     * Preselection types
     */
    enum class PRESELECT {
        LAST_DEV_ADDRESS,
        LAST_ECU_ADDRESS,
        LAST_SERVICE,
        LAST_ITEMS,
        LAST_VIEW_MODE,
    }

}