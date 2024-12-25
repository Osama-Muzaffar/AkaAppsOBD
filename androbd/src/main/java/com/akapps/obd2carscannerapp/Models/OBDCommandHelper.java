package com.akapps.obd2carscannerapp.Models;

import android.content.SharedPreferences;

import com.github.pires.obd.enums.AvailableCommandNames;
import com.akapps.obd2carscannerapp.Utils.Constants;

public class OBDCommandHelper {
    public static String LookUpCommand(String txt) {
        for (AvailableCommandNames item : AvailableCommandNames.values()) {
            if (item.getValue().equals(txt)) return item.name();
        }
        return txt;
    }

    public static int getObdUpdatePeriod(SharedPreferences prefs) {
        String periodString = prefs.
                getString(Constants.OBD_UPDATE_PERIOD_KEY, "4"); // 4 as in seconds
        int period = 4000; // by default 4000ms

        try {
            period = (int) (Double.parseDouble(periodString) * 1000);
        } catch (Exception e) {
        }

        if (period <= 0) {
            period = 4000;
        }

        return period;
    }

}
