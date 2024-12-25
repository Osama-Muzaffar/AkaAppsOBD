package com.akapps.obd2carscannerapp.Trips;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PairedDeviceSharedPreference {
    private static final String PREF_NAME = "pairedDeviceSharedPref";
    private static final String BT_DEVICE_ADDRESS_KEY = "DEVICE_ADDRESS";
    private static final String APP_THEME = "APP_THEME";
    private static final String premiumpurchases = "premiumList";
    private static final String premiumsubscriptions = "premiumsubs";
    private static final String ADAPTER_TYPE = "ADAPTER_TYPE";
    private static final String IP_ADDRESS = "IP_ADDRESS";
    private static final String IP_PORT = "IP_PORT";

    private static PairedDeviceSharedPreference instance;
    private SharedPreferences mSharedPreferences;
    private Gson gson;

    private PairedDeviceSharedPreference(Context context) {
        mSharedPreferences = context.getSharedPreferences(PREF_NAME , Context.MODE_PRIVATE);
        gson = new Gson();

    }

    public static PairedDeviceSharedPreference getInstance(Context context) {
        if(instance == null)
            instance = new PairedDeviceSharedPreference(context);
        return instance;
    }

    public void putBTDeviceAddress (String deviceAddress){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(BT_DEVICE_ADDRESS_KEY , deviceAddress);
        editor.apply();
    }

    public String getBtDeviceAddress (){
        return mSharedPreferences.getString(BT_DEVICE_ADDRESS_KEY , "No name defined");
    }


    public void putAppTheme (String theme){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(APP_THEME , theme);
        editor.apply();
    }

    public String getAppTheme (){
        return mSharedPreferences.getString(APP_THEME , "default");
    }


    public void putIpAdress (String Ipaddress){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(IP_ADDRESS , Ipaddress);
        editor.apply();
    }

    public String getIpAddress (){
        return mSharedPreferences.getString(IP_ADDRESS , "192.168.0.10");
    }


    public void putIpPort (String IpPort){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(IP_PORT , IpPort);
        editor.apply();
    }

    public String getIport (){
        return mSharedPreferences.getString(IP_PORT , "35000");
    }



    public void setAdapterType (String type){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ADAPTER_TYPE , type);
        editor.apply();
    }

    public String getAdapterType (){
        return mSharedPreferences.getString(ADAPTER_TYPE , "default");
    }



    // Save list to SharedPreferences
    public void saveList(List<String> list) {
        String json = gson.toJson(list);
        mSharedPreferences.edit().putString(premiumpurchases, json).apply();
    }

    // Retrieve list from SharedPreferences
    public List<String> getList() {
        String json = mSharedPreferences.getString(premiumpurchases, null);
        if (json != null) {
            Type type = new TypeToken<List<String>>() {}.getType();
            return gson.fromJson(json, type);
        } else {
            return new ArrayList<>(); // Return an empty list if no data is found
        }
    }

    // Add an item to the list in SharedPreferences
    public void addItemToList(String item) {
        List<String> currentList = getList();
        if (!currentList.contains(item)) {
            currentList.add(item);
            saveList(currentList);
        }
    }


    // Remove an item from the list in SharedPreferences (if it exists)
    public void removeItemFromList(String item) {
        List<String> currentList = getList();
        if (currentList.contains(item)) {
            currentList.remove(item);
            saveList(currentList);
        }
    }

    // Remove an item from the list in SharedPreferences (if it exists)
    public void removeAllList() {
        List<String> currentList = getList();
        for(String item: currentList){
            Log.d("Prefs_remove", "removing item: "+item);
            removeItemFromList(item);
        }
    }


    // Save list to SharedPreferences
    public void saveSubsList(List<String> list) {
        String json = gson.toJson(list);
        mSharedPreferences.edit().putString(premiumsubscriptions, json).apply();
    }

    // Retrieve list from SharedPreferences
    public List<String> getSubsList() {
        String json = mSharedPreferences.getString(premiumsubscriptions, null);
        if (json != null) {
            Type type = new TypeToken<List<String>>() {}.getType();
            return gson.fromJson(json, type);
        } else {
            return new ArrayList<>(); // Return an empty list if no data is found
        }
    }

    // Add an item to the list in SharedPreferences
    public void addItemToSubsList(String item) {
        List<String> currentList = getSubsList();
        if (!currentList.contains(item)) {
            currentList.add(item);
            saveSubsList(currentList);
        }
    }


    // Remove an item from the list in SharedPreferences (if it exists)
    public void removeItemFromSubsList(String item) {
        List<String> currentList = getSubsList();
        if (currentList.contains(item)) {
            currentList.remove(item);
            saveSubsList(currentList);
        }
    }

    // Remove an item from the list in SharedPreferences (if it exists)
    public void removeAllSubsList() {
        List<String> currentList = getSubsList();
        for(String item: currentList){
            Log.d("Prefs_remove", "removing item: "+item);
            removeItemFromSubsList(item);
        }
    }




}
