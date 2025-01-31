package com.akapps.obd2carscannerapp.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.preference.PreferenceManager
import android.text.format.DateFormat
import android.util.Log
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.akapps.ecu.EcuDataItem
import com.akapps.ecu.EcuDataPv
import com.akapps.ecu.prot.obd.ElmProt
import com.akapps.ecu.prot.obd.ObdProt
import com.akapps.obd2carscannerapp.Activity.MyMainActivity.Constants.DEVICE_ADDRESS
import com.akapps.obd2carscannerapp.Activity.MyMainActivity.Constants.DEVICE_NAME
import com.akapps.obd2carscannerapp.Activity.MyMainActivity.Constants.MESSAGE_DATA_ITEMS_CHANGED
import com.akapps.obd2carscannerapp.Activity.MyMainActivity.Constants.MESSAGE_DEVICE_NAME
import com.akapps.obd2carscannerapp.Activity.MyMainActivity.Constants.MESSAGE_FILE_READ
import com.akapps.obd2carscannerapp.Activity.MyMainActivity.Constants.MESSAGE_FILE_WRITTEN
import com.akapps.obd2carscannerapp.Activity.MyMainActivity.Constants.MESSAGE_OBD_ECUS
import com.akapps.obd2carscannerapp.Activity.MyMainActivity.Constants.MESSAGE_OBD_NRC
import com.akapps.obd2carscannerapp.Activity.MyMainActivity.Constants.MESSAGE_OBD_NUMCODES
import com.akapps.obd2carscannerapp.Activity.MyMainActivity.Constants.MESSAGE_OBD_STATE_CHANGED
import com.akapps.obd2carscannerapp.Activity.MyMainActivity.Constants.MESSAGE_STATE_CHANGE
import com.akapps.obd2carscannerapp.Activity.MyMainActivity.Constants.MESSAGE_TOAST
import com.akapps.obd2carscannerapp.Activity.MyMainActivity.Constants.MESSAGE_TOOLBAR_VISIBLE
import com.akapps.obd2carscannerapp.Activity.MyMainActivity.Constants.MESSAGE_UPDATE_VIEW
import com.akapps.obd2carscannerapp.Activity.MyMainActivity.Constants.TOAST
import com.akapps.obd2carscannerapp.Activity.MyMainActivity.Constants.currDataAdapter
import com.akapps.obd2carscannerapp.Activity.MyMainActivity.Constants.log
import com.akapps.obd2carscannerapp.Activity.MyMainActivity.Constants.mCommService
import com.akapps.obd2carscannerapp.Activity.MyMainActivity.Constants.mConnectedDeviceName
import com.akapps.obd2carscannerapp.Activity.MyMainActivity.Constants.mDfcAdapter
import com.akapps.obd2carscannerapp.Activity.MyMainActivity.Constants.mPidAdapter
import com.akapps.obd2carscannerapp.Activity.MyMainActivity.Constants.mPluginPvs
import com.akapps.obd2carscannerapp.Activity.MyMainActivity.Constants.mTidAdapter
import com.akapps.obd2carscannerapp.Activity.MyMainActivity.Constants.mVidAdapter
import com.akapps.obd2carscannerapp.Activity.MyMainActivity.Constants.mode
import com.akapps.obd2carscannerapp.Activity.MyMainActivity.Constants.prefs
import com.akapps.obd2carscannerapp.Adapter.MainItemAdapter
import com.akapps.obd2carscannerapp.Ads.AdManager
import com.akapps.obd2carscannerapp.Ads.AdManagerCallback
import com.akapps.obd2carscannerapp.Ads.BannerAdView
import com.akapps.obd2carscannerapp.BtCommService
import com.akapps.obd2carscannerapp.BtDeviceListActivity
import com.akapps.obd2carscannerapp.BuildConfig
import com.akapps.obd2carscannerapp.CommService
import com.akapps.obd2carscannerapp.DfcItemAdapter
import com.akapps.obd2carscannerapp.Models.MainItemModel
import com.akapps.obd2carscannerapp.Models.SharedPreferencesHelper
import com.akapps.obd2carscannerapp.ObdItemAdapter
import com.akapps.obd2carscannerapp.PluginDataAdapter
import com.akapps.obd2carscannerapp.R
import com.akapps.obd2carscannerapp.TidItemAdapter
import com.akapps.obd2carscannerapp.Trips.PairedDeviceSharedPreference
import com.akapps.obd2carscannerapp.Utils.AppUtils
import com.akapps.obd2carscannerapp.Utils.AppUtils.intersitialAdUtils
import com.akapps.obd2carscannerapp.VidItemAdapter
import com.akapps.obd2carscannerapp.databinding.ActivityMyMainBinding
import com.akapps.pvs.PvChangeEvent
import com.akapps.pvs.PvChangeListener
import com.akapps.pvs.PvList
import com.floor.planner.models.NativeBannerFull
import com.quadsquad.savestatus.videodownloader.storysaver.statuskeeper.adsConfigs.adsConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import java.util.Arrays
import java.util.Calendar
import java.util.Objects
import java.util.Timer
import java.util.TreeSet
import java.util.logging.FileHandler
import java.util.logging.Level
import java.util.logging.Logger

class MyMainActivity : AppCompatActivity(), PvChangeListener, AdapterView.OnItemLongClickListener,
    AbsListView.MultiChoiceModeListener, SharedPreferences.OnSharedPreferenceChangeListener,
    PropertyChangeListener {

    var mainAdapter: MainItemAdapter?=null
    var mainList: ArrayList<MainItemModel> = ArrayList()
    lateinit var binding: ActivityMyMainBinding

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

        var mode: MyMainActivity.MODE = MyMainActivity.MODE.OFFLINE
        public var mCommService: CommService? = null

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
    private var dataViewMode: MyMainActivity.DATA_VIEW_MODE = MyMainActivity.DATA_VIEW_MODE.LIST

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
                        binding.rlConnectcard.visibility= View.GONE
                        binding.connectedlinear.visibility= View.VISIBLE
                        binding.statustxt.text = getString(R.string.connected_to) + mConnectedDeviceName
                        val colorStateList =
                            ColorStateList.valueOf(Color.GREEN) // Set your desired color here
                        // Set the background tint
                        binding.statusrelative.setBackgroundTintList(colorStateList)
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.connected_to) + mConnectedDeviceName,
                            Toast.LENGTH_SHORT
                        ).show()

                        PairedDeviceSharedPreference.getInstance(this@MyMainActivity)
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

                    MESSAGE_UPDATE_VIEW ->{

                    }
//                        getListView().invalidateViews()

                    // handle state change in OBD protocol
                    MESSAGE_OBD_STATE_CHANGED -> {
                        evt = msg.obj as PropertyChangeEvent
                        val state = evt.newValue as ElmProt.STAT
                        /* Show ELM status only in ONLINE mode */
                        if (mode != MyMainActivity.MODE.DEMO) {
                            setStatus(resources.getStringArray(R.array.elmcomm_states)[state.ordinal])
                        }
                        // if last selection shall be restored ...
                        if (istRestoreWanted(MyMainActivity.PRESELECT.LAST_SERVICE)) {
                            if (state == ElmProt.STAT.ECU_DETECTED) {
                                setObdService(prefs.getInt(MyMainActivity.PRESELECT.LAST_SERVICE.toString(), 0), null)
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        binding= ActivityMyMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
        changeStatusBarColorFromResource(R.color.unchange_black)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        var nexttime= getNextAlarmTime(this)
        Log.d("Next_Time", "onCreate: "+nexttime)
        binding.settingicon.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }

        binding.proicon.setOnClickListener {
            startActivity(Intent(this, PurchaseActivity::class.java)
                .putExtra("which","multi"))
        }

        // get preferences
        MyMainActivity.Constants.prefs = PreferenceManager.getDefaultSharedPreferences(this)

        // register for later changes
        MyMainActivity.Constants.prefs.registerOnSharedPreferenceChangeListener(this)

        AppUtils.issplashOpening= false

        // Overlay feature has to be set before window content is set
//        if (prefs.getBoolean(PREF_AUTOHIDE, false)
//            && prefs.getBoolean(PREF_OVERLAY, false)
//        ) {
//            window.requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY)
//        }

//        actionBar!!.hide()


        // get list view
        mListView = window.layoutInflater.inflate(R.layout.obd_list, null)


        // update all settings from preferences
//        onSharedPreferenceChanged(MainActivity.Constants.prefs, null)


        // set up logging system
//        setupLoggers()


        // create file helper instance
//        fileHelper = FileHelper(this)



        // set up action bar
        val actionBar = actionBar
        actionBar?.setDisplayShowTitleEnabled(true)

        // start automatic toolbar hider
//        setAutoHider(prefs.getBoolean(PREF_AUTOHIDE, false))


        // set content view
        setContentView(binding.root)


        findViewById<View>(R.id.rl_connectcard).setOnClickListener {
//            if (getMode()== MODE.DEMO){
//                setObdService(ObdProt.OBD_SVC_READ_CODES, "")
//                startActivity(Intent(this@MainActivity,ScanningActivity::class.java))
//            }
//            else {
//                setMode(MODE.DEMO)
//            }
//            if (getMode() == MODE.ONLINE) {
//                setObdService(ObdProt.OBD_SVC_READ_CODES, "")
//                startActivity(Intent(this@MainActivity,ScanningActivity::class.java))
//            }
//            else{
            checkAndRequestBluetoothPermission()
//            }
        }


        binding.rlScanningcard.setOnClickListener {
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
                        if(adsConfig!!.scanning_intersitial){
                            intersitialAdUtils++
                            if (AdManager.getInstance().isReady() && intersitialAdUtils %2==0) {
                                AdManager.getInstance().forceShowInterstitial(this@MyMainActivity,
                                    object : AdManagerCallback() {
                                        override fun onFailedToLoad(error: AdError?) {
                                            super.onFailedToLoad(error)
                                            setObdService(ObdProt.OBD_SVC_READ_CODES, "")
                                            startActivity(
                                                Intent(
                                                    this@MyMainActivity,
                                                    ScanningActivity::class.java
                                                )
                                            )
                                        }

                                        override fun onNextAction() {
                                            super.onNextAction()
                                            setObdService(ObdProt.OBD_SVC_READ_CODES, "")
                                            startActivity(
                                                Intent(
                                                    this@MyMainActivity,
                                                    ScanningActivity::class.java
                                                )
                                            )
                                        }
                                    },true)
                            }
                            else{
                                setObdService(ObdProt.OBD_SVC_READ_CODES, "")
                                startActivity(Intent(this@MyMainActivity, ScanningActivity::class.java))
                            }
                        }
                        else{
                            setObdService(ObdProt.OBD_SVC_READ_CODES, "")
                            startActivity(Intent(this@MyMainActivity, ScanningActivity::class.java))
                        }
                    }
                    else{
                        setObdService(ObdProt.OBD_SVC_READ_CODES, "")
                        startActivity(Intent(this@MyMainActivity, ScanningActivity::class.java))
                    }

                }
                else{
                    setObdService(ObdProt.OBD_SVC_READ_CODES, "")
                    startActivity(Intent(this@MyMainActivity, ScanningActivity::class.java))
                }
            }
            else{
                if(adsConfig !=null) {
                    if(adsConfig!!.scanning_intersitial){
                        intersitialAdUtils++
                        if (AdManager.getInstance().isReady() && intersitialAdUtils%2==0) {
                            AdManager.getInstance().forceShowInterstitial(this@MyMainActivity,
                                object : AdManagerCallback() {
                                    override fun onFailedToLoad(error: AdError?) {
                                        super.onFailedToLoad(error)
                                        setObdService(ObdProt.OBD_SVC_READ_CODES, "")
                                        startActivity(
                                            Intent(
                                                this@MyMainActivity,
                                                ScanningActivity::class.java
                                            )
                                        )
                                    }

                                    override fun onNextAction() {
                                        super.onNextAction()
                                        setObdService(ObdProt.OBD_SVC_READ_CODES, "")
                                        startActivity(
                                            Intent(
                                                this@MyMainActivity,
                                                ScanningActivity::class.java
                                            )
                                        )
                                    }
                                },true
                            )
                        }
                        else{
                            setObdService(ObdProt.OBD_SVC_READ_CODES, "")
                            startActivity(Intent(this@MyMainActivity, ScanningActivity::class.java))
                        }
                    }
                    else{
                        setObdService(ObdProt.OBD_SVC_READ_CODES, "")
                        startActivity(Intent(this@MyMainActivity, ScanningActivity::class.java))
                    }
                }
                else{
                    setObdService(ObdProt.OBD_SVC_READ_CODES, "")
                    startActivity(Intent(this@MyMainActivity, ScanningActivity::class.java))
                }
              }

        }

        binding.rlDisconnectcard.setOnClickListener {
            if (mCommService != null) {
                mCommService!!.stop()
            }
            setMode(MyMainActivity.MODE.OFFLINE)

        }
        // override comm medium with USB connect intent
        if ("android.hardware.usb.action.USB_DEVICE_ATTACHED" == intent.action) {
            CommService.medium = CommService.MEDIUM.USB
        }

        MyMainActivity.Constants.mPidAdapter = ObdItemAdapter(this@MyMainActivity, R.layout.obd_item, ObdProt.PidPvs)
        MyMainActivity.Constants.mVidAdapter = VidItemAdapter(this@MyMainActivity, R.layout.obd_item, ObdProt.VidPvs)
        MyMainActivity.Constants.mTidAdapter = TidItemAdapter(this@MyMainActivity, R.layout.obd_item, ObdProt.VidPvs)
        MyMainActivity.Constants.mDfcAdapter = DfcItemAdapter(this@MyMainActivity, R.layout.obd_item, ObdProt.tCodes)
        MyMainActivity.Constants.mPluginDataAdapter =
            PluginDataAdapter(this@MyMainActivity, R.layout.obd_item, MyMainActivity.Constants.mPluginPvs)
        MyMainActivity.Constants.currDataAdapter = MyMainActivity.Constants.mPidAdapter


        CoroutineScope(Dispatchers.IO).launch {
            // Set up all data adapters

            // set listeners for data structure changes
            setDataListeners()

            // automate elm status display
            CommService.elm.addPropertyChangeListener(this@MyMainActivity)

            when (CommService.medium) {
                CommService.MEDIUM.BLUETOOTH -> {
                    // Get local Bluetooth adapter
                    MyMainActivity.Constants.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    MyMainActivity.Constants.log.fine("Adapter: " + MyMainActivity.Constants.mBluetoothAdapter)
                    // If BT is not on, request that it be enabled.
                    if (getMode() != MyMainActivity.MODE.DEMO && MyMainActivity.Constants.mBluetoothAdapter != null) {
                        // remember initial bluetooth state
                        MyMainActivity.Constants.initialBtStateEnabled = MyMainActivity.Constants.mBluetoothAdapter!!.isEnabled
                        if (!MyMainActivity.Constants.initialBtStateEnabled) {
                            // request to enable bluetooth
//                        val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//                        startActivityForResult(enableIntent, REQUEST_ENABLE_BT)
                        } else {
                            // last device to be auto-connected?
                            if (istRestoreWanted(MyMainActivity.PRESELECT.LAST_DEV_ADDRESS)) {
                                // auto-connect ...
                                setMode(MyMainActivity.MODE.ONLINE)
                            } else {
                                // leave "connect" action to the user
                            }
                        }
                    }
                }

                CommService.MEDIUM.USB, CommService.MEDIUM.NETWORK -> setMode(MyMainActivity.MODE.ONLINE)
            }
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
                    if(adsConfig!!.main_native){
                        val nativeBannerFull = findViewById<NativeBannerFull>(R.id.nativefull)
                        nativeBannerFull.visibility=View.VISIBLE
                        nativeBannerFull.loadNativeBannerAd(this@MyMainActivity,BuildConfig.admob_native)
                    }
                    else if(adsConfig!!.main_banner_simple && adsConfig!!.main_banner_collapsible) {
                        val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                        bannerview.visibility=View.VISIBLE
                        bannerview.visibility = View.VISIBLE
                        bannerview.loadBanner(
                            this@MyMainActivity,
                            BuildConfig.admob_banner
                        )
                    }
                    else if(adsConfig!!.main_banner_simple) {

                        val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                        bannerview.visibility = View.VISIBLE
                        bannerview.loadBanner(
                            this@MyMainActivity,
                            BuildConfig.admob_banner
                        )
                    }
                    else  if(adsConfig!!.main_banner_collapsible) {
                        val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                        bannerview.visibility = View.VISIBLE
                        bannerview.loadCollapsibleBanner(
                            this@MyMainActivity,
                            BuildConfig.admob_banner,
                            true
                        )
                    }
                    else{
                        val adscontainer = findViewById<RelativeLayout>(R.id.ad_container)
                        adscontainer.visibility = View.GONE
                    }

                }
                else {
                    Log.d("test_remote", "remote config is null")
                    val adscontainer = findViewById<RelativeLayout>(R.id.ad_container)
                    adscontainer.visibility = View.GONE
                }
            }
            else {
                Log.d("test_remote", "remote config is null")
                val adscontainer = findViewById<RelativeLayout>(R.id.ad_container)
                adscontainer.visibility = View.GONE
            }
        }
        else {
            runOnUiThread {

                if(adsConfig !=null) {
                    if(adsConfig!!.main_native){
                        val nativeBannerFull = findViewById<NativeBannerFull>(R.id.nativefull)
                        nativeBannerFull.visibility=View.VISIBLE
                        nativeBannerFull.loadNativeBannerAd(this@MyMainActivity,BuildConfig.admob_native)
                    }
                    else if(adsConfig!!.main_banner_simple && adsConfig!!.main_banner_collapsible) {
                        val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                        bannerview.visibility=View.VISIBLE
                        bannerview.visibility = View.VISIBLE
                        bannerview.loadBanner(
                            this@MyMainActivity,
                            BuildConfig.admob_banner
                        )
                    }
                    else if(adsConfig!!.main_banner_simple) {

                        val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                        bannerview.visibility = View.VISIBLE
                        bannerview.loadBanner(
                            this@MyMainActivity,
                            BuildConfig.admob_banner
                        )
                    }
                    else  if(adsConfig!!.main_banner_collapsible) {
                        val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                        bannerview.visibility = View.VISIBLE
                        bannerview.loadCollapsibleBanner(
                            this@MyMainActivity,
                            BuildConfig.admob_banner,
                            true
                        )
                    }
                    else{
                        val adscontainer = findViewById<RelativeLayout>(R.id.ad_container)
                        adscontainer.visibility = View.GONE
                    }

                }
                else {
                    Log.d("test_remote", "remote config is null")
                    val adscontainer = findViewById<RelativeLayout>(R.id.ad_container)
                    adscontainer.visibility = View.GONE
                }
            }

        }

        makeMainItems()
        val prefrences= SharedPreferencesHelper(this)

        val preflng= prefrences.getString(SharedPreferencesHelper.KEY_LANGUAGE,"en")
//        setLocale(this,preflng)

    }

    override fun onResume() {
        super.onResume()
        makeMainItems()

        when(getMode()){
            MyMainActivity.MODE.DEMO ->{
                mainAdapter!!.setEnableItems()
            }
            MyMainActivity.MODE.ONLINE ->{
                mainAdapter!!.setEnableItems()

            }
            MyMainActivity.MODE.OFFLINE ->{
                mainAdapter!!.setDisableItems()

            }
            else->{
                mainAdapter!!.setDisableItems()
            }
        }

    }
    fun makeMainItems(){
        mainList.clear()
        mainList.add(MainItemModel(R.drawable.ic_demo,getString(R.string.demo)))
        mainList.add(MainItemModel(R.drawable.ic_faults, getString(R.string.faults)))
        mainList.add(MainItemModel(R.drawable.ic_help, getString(R.string.help)))
        mainList.add(MainItemModel(R.drawable.ic_realtime, getString(R.string.realtime)))
        mainList.add(MainItemModel(R.drawable.ic_allsensors, getString(R.string.all_sensors)))

        mainAdapter= MainItemAdapter(this,mainList,object : MainItemAdapter.MainItemInterface{
            override fun onMainItemClick(position: Int) {
                intersitialAdUtils++
                Log.d("Track_main_intersitial", "intersitialAdUtils: "+intersitialAdUtils)
                if (AdManager.getInstance().isReady() && intersitialAdUtils%2==0) {
                    Log.d("Track_main_intersitial", "Trying to show intersitial ad")
                    AdManager.getInstance()
                        .forceShowInterstitial(this@MyMainActivity, object : AdManagerCallback() {
                            override fun onNextAction() {
                                super.onNextAction()
                                Log.d("Dashboard", "Intersitial ad on Next Action")
                                if (position == 0) {
                                    if (getMode() == MyMainActivity.MODE.OFFLINE) {
                                        setMode(MyMainActivity.MODE.DEMO)
                                    }
                                }
                                if (position == 1) {
                                    startActivity(
                                        Intent(
                                            this@MyMainActivity,
                                            FaultCodesActivity::class.java
                                        )
                                    )
                                } else if (position == 2) {
                                    startActivity(
                                        Intent(
                                            this@MyMainActivity,
                                            HelpsActivity::class.java
                                        )
                                    )
                                } else if (position == 3) {
                                    startActivity(
                                        Intent(
                                            this@MyMainActivity,
                                            MyRealTimeActivity::class.java
                                        )
                                    )

                                } else if (position == 4) {
                                    setObdService(ObdProt.OBD_SVC_DATA, "")
                                    startActivity(
                                        Intent(
                                            this@MyMainActivity,
                                            ObdDataActivity::class.java
                                        )
                                    )

                                }
                            }

                            override fun onFailedToLoad(error: AdError?) {
                                super.onFailedToLoad(error)
                                Log.d(
                                    "Dashboard",
                                    "Intersitial ad Failed ot load: ${error?.message}"
                                )
                                if (position == 0) {
                                    if (getMode() == MyMainActivity.MODE.OFFLINE) {
                                        setMode(MyMainActivity.MODE.DEMO)
                                    }
                                }
                                if (position == 1) {
                                    startActivity(
                                        Intent(
                                            this@MyMainActivity,
                                            FaultCodesActivity::class.java
                                        )
                                    )
                                } else if (position == 2) {
                                    startActivity(
                                        Intent(
                                            this@MyMainActivity,
                                            HelpsActivity::class.java
                                        )
                                    )
                                } else if (position == 3) {
                                    startActivity(
                                        Intent(
                                            this@MyMainActivity,
                                            MyRealTimeActivity::class.java
                                        )
                                    )

                                } else if (position == 4) {
                                    setObdService(ObdProt.OBD_SVC_DATA, "")
                                    startActivity(
                                        Intent(
                                            this@MyMainActivity,
                                            ObdDataActivity::class.java
                                        )
                                    )

                                }
                            }

                            override fun onAdLoaded() {
                                super.onAdLoaded()
                            }
                        }, true)
                }
                else{
                    Log.d("Track_main_intersitial", "intersitial ad is not ready")
                    if (position == 0) {
                        if (getMode() == MyMainActivity.MODE.OFFLINE) {
                            setMode(MyMainActivity.MODE.DEMO)
                        }
                    }
                    if (position == 1) {
                        startActivity(
                            Intent(
                                this@MyMainActivity,
                                FaultCodesActivity::class.java
                            )
                        )
                    } else if (position == 2) {
                        startActivity(
                            Intent(
                                this@MyMainActivity,
                                HelpsActivity::class.java
                            )
                        )
                    } else if (position == 3) {
                        startActivity(
                            Intent(
                                this@MyMainActivity,
                                MyRealTimeActivity::class.java
                            )
                        )

                    } else if (position == 4) {
                        setObdService(ObdProt.OBD_SVC_DATA, "")
                        startActivity(
                            Intent(
                                this@MyMainActivity,
                                ObdDataActivity::class.java
                            )
                        )

                    }
                }
            }
        })
        binding.mainrv.adapter= mainAdapter
    }

    /**
     * Handle bluetooth connection established ...
     */
    @SuppressLint("StringFormatInvalid")
    private fun onConnect() {
        stopDemoService()

        MyMainActivity.Constants.mode = MyMainActivity.MODE.ONLINE
        // handle further initialisations
//        setMenuItemVisible(R.id.secure_connect_scan, false)
//        setMenuItemVisible(R.id.disconnect, true)
//
//        setMenuItemEnable(R.id.obd_services, true)
        // display connection status
        setStatus(getString(R.string.title_connected_to,
            MyMainActivity.Constants.mConnectedDeviceName
        ))
        // send RESET to Elm adapter
        CommService.elm.reset()
    }

    /**
     * Handle bluetooth connection lost ...
     */
    private fun onDisconnect() {
        // handle further initialisations
        setMode(MyMainActivity.MODE.OFFLINE)
        binding.rlConnectcard.visibility= View.VISIBLE
        binding.connectedlinear.visibility= View.GONE
        binding.statustxt.text = getString(R.string.disconnected)
        val colorStateList =
            ColorStateList.valueOf(Color.RED) // Set your desired color here
        // Set the background tint
        binding.statusrelative.setBackgroundTintList(colorStateList)
        mainAdapter.let {
            it?.setDisableItems()
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
//            unHideActionBar()
        }
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
        MyMainActivity.Constants.mPluginPvs.addPvChangeListener(
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
        MyMainActivity.Constants.mPluginPvs.removePvChangeListener(this)
    }

    /**
     * Activate desired OBD service
     *
     * @param newObdService OBD service ID to be activated
     */
    private fun setObdService(newObdService: Int, menuTitle: CharSequence?) {
        // remember this as current OBD service
        obdService = newObdService
        MyMainActivity.Constants.ignoreNrcs = false

        if (obdService!= ObdProt.OBD_SVC_DATA && obdService!= ObdProt.OBD_SVC_READ_CODES) {
            setContentView(mListView)
//            listView.onItemLongClickListener = this
//            listView.setMultiChoiceModeListener(this)
//            listView.choiceMode = ListView.CHOICE_MODE_SINGLE


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
            ff_selector.adapter = MyMainActivity.Constants.mDfcAdapter
            ff_selector.visibility =
                if (newObdService == ObdProt.OBD_SVC_FREEZEFRAME) View.VISIBLE else View.GONE
        }
        // set protocol service
        CommService.elm.setService(
            newObdService,
            (getMode() != MyMainActivity.MODE.FILE && getMode() != MyMainActivity.MODE.OFFLINE)
        )
        when (newObdService) {
            ObdProt.OBD_SVC_DATA -> {
//                listView.choiceMode = ListView.CHOICE_MODE_MULTIPLE_MODAL
                MyMainActivity.Constants.currDataAdapter =
                    MyMainActivity.Constants.mPidAdapter
            }

            ObdProt.OBD_SVC_FREEZEFRAME -> MyMainActivity.Constants.currDataAdapter =
                MyMainActivity.Constants.mPidAdapter
            ObdProt.OBD_SVC_PENDINGCODES, ObdProt.OBD_SVC_PERMACODES, ObdProt.OBD_SVC_READ_CODES -> {
                // NOT all DFC modes are supported by all vehicles, disable NRC handling for this request
                MyMainActivity.Constants.ignoreNrcs = true
                MyMainActivity.Constants.currDataAdapter =
                    MyMainActivity.Constants.mDfcAdapter
            }

            ObdProt.OBD_SVC_CTRL_MODE -> MyMainActivity.Constants.currDataAdapter =
                MyMainActivity.Constants.mTidAdapter
            ObdProt.OBD_SVC_NONE -> {
                setContentView(R.layout.startup_layout)
                MyMainActivity.Constants.currDataAdapter =
                    MyMainActivity.Constants.mVidAdapter
            }

            ObdProt.OBD_SVC_VEH_INFO -> MyMainActivity.Constants.currDataAdapter =
                MyMainActivity.Constants.mVidAdapter
        }
        // un-filter display
//        setFiltered(false)

//        listAdapter = MainActivity.Constants.currDataAdapter

        // remember this as last selected service
        if (newObdService > ObdProt.OBD_SVC_NONE) {
            MyMainActivity.Constants.prefs.edit().putInt(MyMainActivity.PRESELECT.LAST_SERVICE.toString(), newObdService)
                .apply()
        }
    }

    /**
     * Check if last data selection shall be restored
     *
     *
     * If previously selected items shall be re-selected, then re-select them
     */
    private fun checkToRestoreLastDataSelection() {
        // if last data items shall be restored
        if (istRestoreWanted(MyMainActivity.PRESELECT.LAST_ITEMS)) {
            // get preference for last seleted items
            val lastSelectedItems =
                toIntArray(MyMainActivity.Constants.prefs.getString(MyMainActivity.PRESELECT.LAST_ITEMS.toString(), ""))
            // select last selected items
            if (lastSelectedItems.size > 0) {
                if (!selectDataItems(lastSelectedItems)) {
                    // if items could not be applied
                    // remove invalid preselection
                    MyMainActivity.Constants.prefs.edit().remove(MyMainActivity.PRESELECT.LAST_ITEMS.toString()).apply()
                    MyMainActivity.Constants.log.warning(
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
        if (istRestoreWanted(MyMainActivity.PRESELECT.LAST_VIEW_MODE)) {
            // set last data view mode
            val lastMode: MyMainActivity.DATA_VIEW_MODE =
                MyMainActivity.DATA_VIEW_MODE.valueOf(
                    MyMainActivity.Constants.prefs.getString(
                        MyMainActivity.PRESELECT.LAST_VIEW_MODE.toString(),
                        MyMainActivity.DATA_VIEW_MODE.LIST.toString()
                    )!!
                )
            setDataViewMode(lastMode)
        }
    }

    /**
     * Check if restore of specified preselection is wanted from settings
     *
     * @param preselect specified preselect
     * @return flag if preselection shall be restored
     */
    private fun istRestoreWanted(preselect: MyMainActivity.PRESELECT): Boolean {
        return MyMainActivity.Constants.prefs.getStringSet(
            MyMainActivity.Constants.PREF_USE_LAST,
            MyMainActivity.Constants.emptyStringSet
        )!!
            .contains(preselect.toString())
    }
    /*
        * Implementations of PluginManager data interface callbacks
        */


    private fun setNumCodes(newNumCodes: Int) {
        // set list background based on MIL status
        val list = findViewById<View>(R.id.obd_list)
        list?.setBackgroundResource(
            if ((newNumCodes and 0x80) != 0
            ) R.drawable.mil_on
            else R.drawable.mil_off
        )
        // enable / disable freeze frames based on number of codes
//        setMenuItemEnable(R.id.service_freezeframes, (newNumCodes != 0))
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
                MyMainActivity.Constants.prefs.getInt(MyMainActivity.PRESELECT.LAST_ECU_ADDRESS.toString(), 0)
            // check if last preferred address matches any of the reported addresses
            if (istRestoreWanted(MyMainActivity.PRESELECT.LAST_ECU_ADDRESS)
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
     * Stop demo mode Thread
     */
    private fun stopDemoService() {
        if (getMode() == MyMainActivity.MODE.DEMO) {
            ElmProt.runDemo = false
            binding.rlConnectcard.visibility= View.VISIBLE
            binding.connectedlinear.visibility= View.GONE
            binding.statustxt.text = getString(R.string.disconnected)
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
        if (getMode() != MyMainActivity.MODE.DEMO) {
            binding.rlConnectcard.visibility= View.GONE
            binding.connectedlinear.visibility= View.VISIBLE
            binding.statustxt.text = getString(R.string.connected_to)+ " " + getString(R.string.demo)
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
                MyMainActivity.Constants.mBluetoothAdapter != null && MyMainActivity.Constants.mBluetoothAdapter!!.isEnabled
//            setMenuItemVisible(R.id.secure_connect_scan, allowConnect)
//            setMenuItemVisible(R.id.disconnect, !allowConnect)
//
//            setMenuItemEnable(R.id.obd_services, true)
            /* The Thread object for processing the demo mode loop */
            val demoThread = Thread(CommService.elm)
            demoThread.start()
        }
    }

    /**
     * set new operating mode
     *
     * @param mode new mode
     */
    private fun setMode(mode: MODE) {
        // if this is a mode change, or file reload ...
        var mode = mode
        if (mode != MyMainActivity.Constants.mode || mode == MyMainActivity.MODE.FILE) {
            if (mode != MyMainActivity.MODE.DEMO) {
                stopDemoService()
            }

            // Disable data updates in FILE mode
            ObdItemAdapter.allowDataUpdates = (mode != MyMainActivity.MODE.FILE)

            when (mode) {
                MyMainActivity.MODE.OFFLINE -> {
                    // update menu item states
//                    setMenuItemVisible(R.id.disconnect, false)
//                    setMenuItemVisible(R.id.secure_connect_scan, true)
//                    setMenuItemEnable(R.id.obd_services, false)
                }

                MyMainActivity.MODE.ONLINE -> when (CommService.medium) {
                    CommService.MEDIUM.BLUETOOTH -> if (MyMainActivity.Constants.mBluetoothAdapter == null || !MyMainActivity.Constants.mBluetoothAdapter!!.isEnabled) {
                        Toast.makeText(this, getString(R.string.none_found), Toast.LENGTH_SHORT)
                            .show()
                        mode = MyMainActivity.MODE.OFFLINE
                    } else {
                        // if pre-settings shall be used ...
                        val address = MyMainActivity.Constants.prefs.getString(
                            MyMainActivity.PRESELECT.LAST_DEV_ADDRESS.toString(),
                            null
                        )
                        if ((istRestoreWanted(MyMainActivity.PRESELECT.LAST_DEV_ADDRESS)
                                    && address != null)
                        ) {
                            // ... connect with previously connected device
                            connectBtDevice(
                                address,
                                MyMainActivity.Constants.prefs.getBoolean("bt_secure_connection", false)
                            )
                        } else {
                            // ... otherwise launch the BtDeviceListActivity to see devices and do scan
                            val serverIntent = Intent(
                                this,
                                ConnectionPairingActivity::class.java
                            )
                            startActivityForResult(
                                serverIntent,
                                if (MyMainActivity.Constants.prefs.getBoolean("bt_secure_connection", false)
                                ) MyMainActivity.Constants.REQUEST_CONNECT_DEVICE_SECURE
                                else MyMainActivity.Constants.REQUEST_CONNECT_DEVICE_INSECURE
                            )
                        }
                    }

                    CommService.MEDIUM.USB -> {
//                        val enableIntent = Intent(this, UsbDeviceListActivity::class.java)
//                        startActivityForResult(
//                            enableIntent,
//                            REQUEST_CONNECT_DEVICE_USB
//                        )
                    }

                    CommService.MEDIUM.NETWORK -> connectNetworkDevice(
                        MyMainActivity.Constants.prefs.getString(MyMainActivity.Constants.DEVICE_ADDRESS, null),
                        getPrefsInt(MyMainActivity.Constants.DEVICE_PORT, 23)
                    )
                }

                MyMainActivity.MODE.DEMO -> startDemoService()
                MyMainActivity.MODE.FILE -> {
                    setStatus(R.string.saved_data)
//                    selectFileToLoad()
                }
            }
            // remember previous mode
            // set new mode
            MyMainActivity.Constants.mode = mode
            setStatus(mode.toString())
        }
    }



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

    override fun pvChanged(event: PvChangeEvent?) {
        // forward PV change to the UI Activity
        val msg = mHandler.obtainMessage(MyMainActivity.Constants.MESSAGE_DATA_ITEMS_CHANGED)
        if (!event!!.isChildEvent) {
            msg.obj = event
            mHandler.sendMessage(msg)
        }
    }
    /**
     * get current operating mode
     */
    private fun getMode(): MyMainActivity.MODE {
        return MyMainActivity.Constants.mode
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
            MyMainActivity.Constants.log.severe(ex.toString())
        }

        return result
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
        count = 0
        positionsValid = (max < count)
//        // if all positions are valid for current list ...
//        if (positionsValid) {
//            // set list items as selected
//            for (i: Int in positions) {
//                listView.setItemChecked(i, true)
//            }
//        }

        // return validity of positions
        return positionsValid
    }

    /**
     * Set new data view mode
     *
     * @param dataViewMode new data view mode
     */
    private fun setDataViewMode(dataViewMode: MyMainActivity.DATA_VIEW_MODE) {
        // if this is a real change ...
      /*  if (dataViewMode != this.dataViewMode) {
            MainActivity.Constants.log.info(
                String.format(
                    "Set view mode: %s -> %s",
                    this.dataViewMode, dataViewMode
                )
            )

            when (dataViewMode) {
                MainActivity.DATA_VIEW_MODE.LIST -> {
                    setFiltered(false)
//                    listView.choiceMode = ListView.CHOICE_MODE_MULTIPLE_MODAL
                    this.dataViewMode = dataViewMode
                }

                MainActivity.DATA_VIEW_MODE.FILTERED -> if (listView.checkedItemCount > 0) {
                    setFiltered(true)
                    listView.choiceMode = ListView.CHOICE_MODE_SINGLE
                    this.dataViewMode = dataViewMode
                }

                MainActivity.DATA_VIEW_MODE.HEADUP, MainActivity.DATA_VIEW_MODE.DASHBOARD -> if (listView.checkedItemCount > 0) {
//                    MainActivity.setAdapter(listAdapter)
//                    val intent = Intent(this, MainActivity::class.java)
//                    intent.putExtra(MainActivity.POSITIONS, getSelectedPositions())
//                    intent.putExtra(
//                        MainActivity.RES_ID,
//                        if (dataViewMode == DATA_VIEW_MODE.DASHBOARD
//                        ) R.layout.dashboard
//                        else R.layout.head_up
//                    )
//                    startActivityForResult(intent, REQUEST_GRAPH_DISPLAY_DONE)
//                    this.dataViewMode = dataViewMode
                }

                MainActivity.DATA_VIEW_MODE.CHART -> if (listView.checkedItemCount > 0) {
//                    ChartActivity.setAdapter(listAdapter)
//                    val intent = Intent(this, ChartActivity::class.java)
//                    intent.putExtra(ChartActivity.POSITIONS, getSelectedPositions())
//                    startActivityForResult(intent, REQUEST_GRAPH_DISPLAY_DONE)
//                    this.dataViewMode = dataViewMode
                }
            }
            // remember this as the last data view mode (if not regular list)
            if (dataViewMode != MainActivity.DATA_VIEW_MODE.LIST) {
                MainActivity.Constants.prefs.edit()
                    .putString(MainActivity.PRESELECT.LAST_VIEW_MODE.toString(), dataViewMode.toString())
                    .apply()
            }
        }*/
    }

    /**
     * Initiate a connect to the selected bluetooth device
     *
     * @param address bluetooth device address
     * @param secure  flag to indicate if the connection shall be secure, or not
     */
    private fun connectBtDevice(address: String?, secure: Boolean) {
        // Get the BluetoothDevice object
        val device = MyMainActivity.Constants.mBluetoothAdapter!!.getRemoteDevice(address)
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
     * Get preference int value
     *
     * @param key          preference key name
     * @param defaultValue numeric default value
     * @return preference int value
     */
    private fun getPrefsInt(key: String, defaultValue: Int): Int {
        var result = defaultValue

        try {
            result = MyMainActivity.Constants.prefs.getString(key, defaultValue.toString())!!.toInt()
        } catch (ex: java.lang.Exception) {
            // log error message
            MyMainActivity.Constants.log.severe(
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
//                pv = listAdapter.getItem(position) as EcuDataPv
                /* only numeric values may be shown as graph/dashboard */
//                if (pv[EcuDataPv.FID_VALUE] is Number) {
//                    MainActivity.setAdapter(listAdapter)
//                    intent = Intent(this, MainActivity::class.java)
//                    intent.putExtra(MainActivity.POSITIONS, intArrayOf(position))
//                    startActivity(intent)
//                }
            }

            ObdProt.OBD_SVC_READ_CODES, ObdProt.OBD_SVC_PERMACODES, ObdProt.OBD_SVC_PENDINGCODES -> try {
//                intent = Intent(Intent.ACTION_WEB_SEARCH)
//                val dfc = listAdapter.getItem(position) as EcuCodeItem
//                intent.putExtra(
//                    SearchManager.QUERY,
//                    "OBD " + dfc[EcuCodeItem.FID_CODE].toString()
//                )
//                startActivity(intent)
            } catch (e: java.lang.Exception) {
                MyMainActivity.Constants.log.log(Level.SEVERE, "WebSearch DFC", e)
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            }

            ObdProt.OBD_SVC_VEH_INFO -> {
                // copy VID content to clipboard ...
//                pv = listAdapter.getItem(position) as EcuDataPv
//                val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
//                val clip = ClipData.newPlainText(
//                    pv[EcuDataPv.FID_DESCRIPT].toString(),
//                    pv[EcuDataPv.FID_VALUE].toString()
//                )
//                clipboard.setPrimaryClip(clip)
//                // Show Toast message
//                Toast.makeText(this, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show()
            }

            ObdProt.OBD_SVC_CTRL_MODE -> {
//                pv = listAdapter.getItem(position) as EcuDataPv
//                // Confirm & perform OBD test control ...
//                confirmObdTestControl(
//                    pv[EcuDataPv.FID_DESCRIPT].toString(),
//                    ObdProt.OBD_SVC_CTRL_MODE,
//                    pv.getAsInt(EcuDataPv.FID_PID)
//                )
            }
        }
        return true
    }

    /**
     * Handler for options menu creation event
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        menuInflater.inflate(R.menu.obd_services, menu.findItem(R.id.obd_services).subMenu)
        MyMainActivity.Constants.menu = menu
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
                MyMainActivity.Constants.prefs.edit()
                    .putBoolean(MyMainActivity.Constants.NIGHT_MODE, !MyMainActivity.Constants.nightMode).apply()
                return true
            }

            R.id.secure_connect_scan -> {
                setMode(MyMainActivity.MODE.ONLINE)
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
                setMode(MyMainActivity.MODE.OFFLINE)
                return true
            }

            R.id.settings -> {
                // Launch the BtDeviceListActivity to see devices and do scan
//                val settingsIntent = Intent(this, SettingsActivity::class.java)
//                startActivityForResult(settingsIntent, REQUEST_SETTINGS)
                return true
            }

            R.id.plugin_manager -> {
//                setManagerView()
                return true
            }

            R.id.save -> {
                // save recorded data (threaded)
//                fileHelper.saveDataThreaded()
                return true
            }

            R.id.load -> {
                setMode(MyMainActivity.MODE.FILE)
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
                setDataViewMode(MyMainActivity.DATA_VIEW_MODE.CHART)
                return true
            }

            R.id.hud_selected -> {
                setDataViewMode(MyMainActivity.DATA_VIEW_MODE.HEADUP)
                return true
            }

            R.id.dashboard_selected -> {
                setDataViewMode(MyMainActivity.DATA_VIEW_MODE.DASHBOARD)
                return true
            }

            R.id.filter_selected -> {
                setDataViewMode(MyMainActivity.DATA_VIEW_MODE.FILTERED)
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
            MyMainActivity.Constants.REQUEST_CONNECT_DEVICE_SECURE -> {
                secureConnection = true
                // When BtDeviceListActivity returns with a device to connect
                if (resultCode == RESULT_OK) {
                    // Get the device MAC address
                    val address = Objects.requireNonNull(data!!.extras)!!.getString(
                        BtDeviceListActivity.EXTRA_DEVICE_ADDRESS
                    )
                    // save reported address as last setting
                    MyMainActivity.Constants.prefs.edit()
                        .putString(MyMainActivity.PRESELECT.LAST_DEV_ADDRESS.toString(), address).apply()
                    connectBtDevice(address, secureConnection)
                    binding.mainconstraint.visibility= View.GONE
                    binding.scanninglayout.visibility= View.VISIBLE
                    PairedDeviceSharedPreference.getInstance(this)
                        .putBTDeviceAddress(address)

                } else {
                    setMode(MyMainActivity.MODE.OFFLINE)
                }
            }

            MyMainActivity.Constants.REQUEST_CONNECT_DEVICE_INSECURE ->
                if (resultCode == RESULT_OK) {
                    val address = Objects.requireNonNull(data!!.extras)!!.getString(
                        BtDeviceListActivity.EXTRA_DEVICE_ADDRESS
                    )
                    MyMainActivity.Constants.prefs.edit()
                        .putString(MyMainActivity.PRESELECT.LAST_DEV_ADDRESS.toString(), address).apply()
                    connectBtDevice(address, secureConnection)
                    binding.mainconstraint.visibility= View.GONE
                    binding.scanninglayout.visibility= View.VISIBLE
                } else {
                    setMode(MyMainActivity.MODE.OFFLINE)
                }

            MyMainActivity.Constants.REQUEST_CONNECT_DEVICE_USB ->   {              // DeviceListActivity returns with a device to connect
//                    if (resultCode == RESULT_OK) {
//                        mCommService = UsbCommService(this, mHandler)
//                        mCommService!!.connect(UsbDeviceListActivity.selectedPort, true)
//                    } else {
//                        setMode(MODE.OFFLINE)
//                    }
            }
            MyMainActivity.Constants.REQUEST_ENABLE_BT -> {
                Log.d("onActivityResult", "onActivityResult: Request code = ${MyMainActivity.Constants.REQUEST_ENABLE_BT}")
                // When the request to enable Bluetooth returns
                if (resultCode == RESULT_OK) {
                    // Start online mode
                    Log.d("enableBluetooth", "enableBluetooth: set mode to result online")

                    setMode(MyMainActivity.MODE.ONLINE)
//                        startActivityForResult(Intent(this@MainActivity, BluetoothPairingActivity::class.java),
//                            REQUEST_CONNECT_DEVICE_SECURE)
                } else {
                    // Start demo service Thread
                    setMode(MyMainActivity.MODE.DEMO)
                }
            }

            MyMainActivity.Constants.REQUEST_SELECT_FILE -> if (resultCode == RESULT_OK) {
                // Get the Uri of the selected file
                val uri = data?.data
                MyMainActivity.Constants.log.info("Load content: $uri")
                // load data ...
//                    fileHelper.loadDataThreaded(uri, mHandler)
                // don't allow saving it again
//                setMenuItemEnable(R.id.save, false)
//                setMenuItemEnable(R.id.obd_services, true)
            }

            MyMainActivity.Constants.REQUEST_SETTINGS -> {}
            MyMainActivity.Constants.REQUEST_GRAPH_DISPLAY_DONE ->                 // let context know that we are in list mode again ...
                dataViewMode = MyMainActivity.DATA_VIEW_MODE.LIST
        }

    }


    /**
     * Filter display items to just the selected ones
     */
    /*private fun setFiltered(filtered: Boolean) {
        if (filtered) {
            val selPids = TreeSet<Int>()
            val selectedPositions = getSelectedPositions()
            for (pos: Int in selectedPositions) {
                val pv = MyMainActivity.Constants.currDataAdapter.getItem(pos) as EcuDataPv?
                selPids.add(pv?.getAsInt(EcuDataPv.FID_PID) ?: 0)
            }
            MyMainActivity.Constants.currDataAdapter.filterPositions(selectedPositions)

            if (MyMainActivity.Constants.currDataAdapter === MyMainActivity.Constants.mPidAdapter) MyMainActivity.setFixedPids(
                selPids
            )
        } else {
            if (MyMainActivity.Constants.currDataAdapter === MyMainActivity.Constants.mPidAdapter) ObdProt.resetFixedPid()

            /* Return to original PV list */
            if (MyMainActivity.Constants.currDataAdapter === MyMainActivity.Constants.mPidAdapter) {
                MyMainActivity.Constants.currDataAdapter.setPvList(ObdProt.PidPvs)
                // append plugin measurements to data list
                MyMainActivity.Constants.currDataAdapter.addAll(MyMainActivity.Constants.mPluginPvs.values)
            } else if (MyMainActivity.Constants.currDataAdapter === MyMainActivity.Constants.mVidAdapter) MyMainActivity.Constants.currDataAdapter.setPvList(
                ObdProt.VidPvs
            )
            else if (MyMainActivity.Constants.currDataAdapter === MyMainActivity.Constants.mDfcAdapter) MyMainActivity.Constants.currDataAdapter.setPvList(
                ObdProt.tCodes
            )
            else if (MyMainActivity.Constants.currDataAdapter === MyMainActivity.Constants.mPluginDataAdapter) MyMainActivity.Constants.currDataAdapter.setPvList(
                MyMainActivity.Constants.mPluginPvs
            )
        }
    }*/

    private fun confirmObdTestControl(testControlName: String, service: Int, tid: Int) {
        
    }

    /**
     * set mesaurement conversion system to metric/imperial
     *
     * @param cnvId ID for metric/imperial conversion
     */
    private fun setConversionSystem(cnvId: Int) {
        MyMainActivity.Constants.log.info("Conversion: " + resources.getStringArray(R.array.measure_options)[cnvId])
        if (EcuDataItem.cnvSystem != cnvId) {
            // set coversion system
            EcuDataItem.cnvSystem = cnvId
        }
    }

    /**
     * clear all preselections
     */
    private fun clearPreselections() {
        for (selection: MyMainActivity.PRESELECT in MyMainActivity.PRESELECT.values()) {
            MyMainActivity.Constants.prefs.edit().remove(selection.toString()).apply()
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
     * get the Position in model of the selected items
     *
     * @return Array of selected item positions
     */
    private fun getSelectedPositions(): IntArray {
        var selectedPositions= IntArray(10)
        // SparseBoolArray - what a garbage data type to return ...
//        val checkedItems = listView.checkedItemPositions
//        // get number of items
//        val checkedItemsCount = listView.checkedItemCount
//        // dimension array
//        selectedPositions = IntArray(checkedItemsCount)
//        if (checkedItemsCount > 0) {
//            var j = 0
//            // loop through findings
//            for (i in 0 until checkedItems.size()) {
//                // Item position in adapter
//                if (checkedItems.valueAt(i)) {
//                    selectedPositions[j++] = checkedItems.keyAt(i)
//                }
//            }
//            // trim to really detected value (workaround for invalid length reported)
//            selectedPositions = selectedPositions.copyOf(j)
//        }
        val strPreselect = selectedPositions.contentToString()
        MyMainActivity.Constants.log.fine("Preselection: '$strPreselect'")
        // save this as last seleted positions
        MyMainActivity.Constants.prefs.edit().putString(MyMainActivity.PRESELECT.LAST_ITEMS.toString(), strPreselect)
            .apply()
        return selectedPositions
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
                    MyMainActivity.Constants.REQUEST_ENABLE_BT
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

    override fun onSharedPreferenceChanged(prefs: SharedPreferences, key: String?) {
        // keep main display on?
        if (key == null || MyMainActivity.Constants.KEEP_SCREEN_ON == key) {
            window.addFlags(
                if (prefs.getBoolean(MyMainActivity.Constants.KEEP_SCREEN_ON, false)
                ) WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                else 0
            )
        }

        // FULL SCREEN operation based on preference settings
        if ((key == null) || MyMainActivity.Constants.PREF_FULLSCREEN == key) {
            window.setFlags(
                if (prefs.getBoolean(MyMainActivity.Constants.PREF_FULLSCREEN, true)
                ) WindowManager.LayoutParams.FLAG_FULLSCREEN else 0,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        // night mode
        if ((key == null) || MyMainActivity.Constants.NIGHT_MODE == key) {
//            setNightMode(prefs.getBoolean(MainActivity.Constants.NIGHT_MODE, false))
        }

        // set default comm medium
//        if ((key == null) || SettingsActivity.KEY_COMM_MEDIUM == key) {
//            CommService.medium =
//                MEDIUM.values()[getPrefsInt(SettingsActivity.KEY_COMM_MEDIUM, 0)]
//        }

        // enable/disable ELM adaptive timing
        if ((key == null) || MyMainActivity.Constants.ELM_ADAPTIVE_TIMING == key) {
            CommService.elm.mAdaptiveTiming.mode = ElmProt.AdaptTimingMode.valueOf(
                (prefs.getString(
                    MyMainActivity.Constants.ELM_ADAPTIVE_TIMING,
                    ElmProt.AdaptTimingMode.OFF.toString()
                ))!!
            )
        }

        // set protocol flag to initiate immediate reset on NRC reception
        if (key == null || MyMainActivity.Constants.ELM_RESET_ON_NRC == key) {
            CommService.elm.setResetOnNrc(prefs.getBoolean(MyMainActivity.Constants.ELM_RESET_ON_NRC, false))
        }

        // set custom ELM init commands
        if ((key == null) || MyMainActivity.Constants.ELM_CUSTOM_INIT_CMDS == key) {
            val value = prefs.getString(MyMainActivity.Constants.ELM_CUSTOM_INIT_CMDS, null)
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
        if ((key == null) || MyMainActivity.Constants.MEASURE_SYSTEM == key) {
            setConversionSystem(getPrefsInt(MyMainActivity.Constants.MEASURE_SYSTEM, EcuDataItem.SYSTEM_METRIC))
        }

        // ... preferred protocol
//        if (key == null || SettingsActivity.KEY_PROT_SELECT == key) {
//            ElmProt.setPreferredProtocol(getPrefsInt(SettingsActivity.KEY_PROT_SELECT, 0))
//        }

        // log levels
        if (key == null || MyMainActivity.Constants.LOG_MASTER == key) {
//            setLogLevels()
        }

        // update from protocol extensions
        if (key == null || key.startsWith("ext_file-")) {
//            loadPreferredExtensions()
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
        if ((key == null) || MyMainActivity.Constants.PREF_DATA_DISABLE_MAX == key) {
            EcuDataItem.MAX_ERROR_COUNT = getPrefsInt(MyMainActivity.Constants.PREF_DATA_DISABLE_MAX, 3)
        }

        // Customized PID display color preference
        if (key != null) {
            // specific key -> update single
//            updatePidColor(key)
//            updatePidDisplayRange(key)
//            updatePidUpdatePeriod(key)
        } else {
            // loop through all keys
            for (currKey: String in prefs.all.keys) {
                // update by key
//                updatePidColor(currKey)
//                updatePidDisplayRange(currKey)
//                updatePidUpdatePeriod(currKey)
            }
        }
    }

    override fun propertyChange(evt: PropertyChangeEvent) {
        /* handle protocol status changes */
        if ((ElmProt.PROP_STATUS == evt.propertyName)) {
            // forward property change to the UI Activity
            val msg = mHandler.obtainMessage(MyMainActivity.Constants.MESSAGE_OBD_STATE_CHANGED)
            msg.obj = evt
            mHandler.sendMessage(msg)
        } else {
            if ((ElmProt.PROP_NUM_CODES == evt.propertyName)) {
                // forward property change to the UI Activity
                val msg = mHandler.obtainMessage(MyMainActivity.Constants.MESSAGE_OBD_NUMCODES)
                msg.obj = evt
                mHandler.sendMessage(msg)
            } else {
                if ((ElmProt.PROP_ECU_ADDRESS == evt.propertyName)) {
                    // forward property change to the UI Activity
                    val msg = mHandler.obtainMessage(MyMainActivity.Constants.MESSAGE_OBD_ECUS)
                    msg.obj = evt
                    mHandler.sendMessage(msg)
                } else {
                    if ((ObdProt.PROP_NRC == evt.propertyName)) {
                        // forward property change to the UI Activity
                        val msg = mHandler.obtainMessage(MyMainActivity.Constants.MESSAGE_OBD_NRC)
                        msg.obj = evt
                        mHandler.sendMessage(msg)
                    }
                }
            }
        }
    }



    @SuppressLint("MissingPermission")
    private fun enableBluetooth() {
        // request to enable bluetooth
        if (!MyMainActivity.Constants.mBluetoothAdapter!!.isEnabled) {
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableIntent, MyMainActivity.Constants.REQUEST_ENABLE_BT)

//        bluetoothAdapter!!.enable()
            Toast.makeText(this, "Bluetooth is being enabled...", Toast.LENGTH_SHORT).show()
        }
        else{

            intersitialAdUtils++
            Log.d("Track_main_intersitial", "intersitialAdUtils: "+intersitialAdUtils)
            if (AdManager.getInstance().isReady() && intersitialAdUtils%2==0) {
                Log.d("Track_main_intersitial", "Trying to show intersitial ad")
                AdManager.getInstance()
                    .forceShowInterstitial(this@MyMainActivity, object : AdManagerCallback() {
                        override fun onNextAction() {
                            super.onNextAction()
                            Log.d("Dashboard", "Intersitial ad on Next Action")
                            Log.d("enableBluetooth", "enableBluetooth: set mode to online")
                            setMode(MyMainActivity.MODE.ONLINE)
                        }

                        override fun onFailedToLoad(error: AdError?) {
                            super.onFailedToLoad(error)
                            Log.d(
                                "Dashboard",
                                "Intersitial ad Failed ot load: ${error?.message}"
                            )
                            Log.d("enableBluetooth", "enableBluetooth: set mode to online")
                            setMode(MyMainActivity.MODE.ONLINE)
                        }

                        override fun onAdLoaded() {
                            super.onAdLoaded()
                        }
                    }, true)


            }
            else{
                Log.d("enableBluetooth", "enableBluetooth: set mode to online")
                setMode(MyMainActivity.MODE.ONLINE)

            }

        }
    }

    fun getNextAlarmTime(context: Context): String? {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val nextAlarmClock = alarmManager.nextAlarmClock
        return if (nextAlarmClock != null) {
            val calendar = Calendar.getInstance().apply {
                timeInMillis = nextAlarmClock.triggerTime
            }
            DateFormat.format("yyyy-MM-dd HH:mm:ss", calendar).toString()
        } else {
            null
        }
    }
}
fun AppCompatActivity.changeStatusBarColorFromResource(colorResId: Int) {
    // Get the color from resources using ContextCompat
    val color = ContextCompat.getColor(this, colorResId)

    // Get the window of the activity
    val window: Window = this.window

    // Set the status bar color
    window.statusBarColor = color

    // Optional: Set the status bar text/icons to light or dark depending on the color
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (color == Color.WHITE || color == ContextCompat.getColor(this, android.R.color.white)) {
            // Light color background, so dark icons
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            // Dark color background, so light icons
            window.decorView.systemUiVisibility = 0
        }
    }
}