package com.akapps.obd2carscannerapp.Utils;

import android.content.Context;

public class Constants {
    public static final String KEY_OBD_LIST = "OBD LIST";
    public static final String KEY_OBD_FROM_MAIN = " From Main";
    public static String PROTOCOL = "AUTO";

    // Testing Ids
        public static final String OPEN_APP_AD="ca-app-pub-3940256099942544/3419835294";


        // live Id
 //   public static final String OPEN_APP_AD="ca-app-pub-6941637095433882/8630298906";

    public static final String NAME_GENERAL_SHARED_PREFs = "General Shared Preferences";
    public static boolean IS_INTERSTITIAL_LOADED=false;
    public static final String TAG_SCANNED_DATA_FRAGMENT = "Scanned Data Fragment";
    public static final String TAG_VIEW_ALL_OBDs_FRAGMENT= "ViewAllOBDsFragment";
    public static final String TAG_OBD_FRAGMENT = "OBD Fragment";
    public static final String TAG_REALTIME_DATA_FRAGMENT = "REALTIME DATA FRAGMENT";
    public static final String TAG_CLEAR_MIL_FRAGMENT = "CLEAR MIL FRAGMENT";

    public static final String KEY_ON_SCAN_CLICK = "Scan Btn Clicked";
    public static final String KEY_ON_VIEW_ALL_FAULT_CODES_CLICK = "View All Btn Clicked";
    public static final String KEY_ON_RECYCLERVIEW_FAULT_CODES_ITEM_CLICK = "Fault Code Item Clicked";
    public static final String KEY_ON_CLEAR_MIL_CLICK = "Clear MIL Btn Clicked";
    public static final String KEY_ON_HELP_CLICK = "Help Btn Clicked";
    public static final String KEY_ON_BT_ICON_ACTION_BAR_CLICK = "BT Icon Btn Clicked";
    public static final String KEY_REALTIME_DATA_CLICK = "Realtime Data Btn Clicked";
    public static final String KEY_VIEW_OBD_FRAGMENT_FROM_VIEW_ALL = "Obd Fragment from View All";
    public static final String KEY_SPEAK_BTN_CLICKED = "Speak Btn clicked From OBD Fragment";
    public static final String KEY_ON_DIALOGVIEW_SCAN_CLICK = "Scan BT Devices";

    public static boolean PreRequisites = true;

    public static final String BLUETOOTH_LIST_KEY = "bluetooth_list_preference";
    public static final String UPLOAD_URL_KEY = "upload_url_preference";
    public static final String UPLOAD_DATA_KEY = "upload_data_preference";
    public static final String OBD_UPDATE_PERIOD_KEY = "obd_update_period_preference";
    public static final String VEHICLE_ID_KEY = "vehicle_id_preference";
    public static final String ENGINE_DISPLACEMENT_KEY = "engine_displacement_preference";
    public static final String VOLUMETRIC_EFFICIENCY_KEY = "volumetric_efficiency_preference";


    public static final String IMPERIAL_UNITS_KEY = "imperial_units_preference";
    public static final String COMMANDS_SCREEN_KEY = "obd_commands_screen";
    public static final String PROTOCOLS_LIST_KEY = "obd_protocols_preference";
    public static final String ENABLE_GPS_KEY = "enable_gps_preference";
    public static final String GPS_UPDATE_PERIOD_KEY = "gps_update_period_preference";
    public static final String GPS_DISTANCE_PERIOD_KEY = "gps_distance_period_preference";

    public static final String ENABLE_BT_KEY = "enable_bluetooth_preference";
    public static final String MAX_FUEL_ECON_KEY = "max_fuel_econ_preference";
    public static final String CONFIG_READER_KEY = "reader_config_preference";
    public static final String ENABLE_FULL_LOGGING_KEY = "enable_full_logging";
    public static final String DIRECTORY_FULL_LOGGING_KEY = "dirname_full_logging";
    public static final String DEV_EMAIL_KEY = "dev_email";


    public static void showToast(String msg, Context context){
     //   Toast.makeText(context, ""+msg, Toast.LENGTH_SHORT).show();
    }
}
