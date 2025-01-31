package com.akapps.obd2carscannerapp.Models

import android.nfc.tech.MifareUltralight.PAGE_SIZE
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akapps.obd2carscannerapp.Database.DatabaseHelper
import com.akapps.obd2carscannerapp.Database.ObdEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FaultCodesViewModel : ViewModel() {
    private val _obdEntries = MutableLiveData<List<ObdEntry>>()
    val obdEntries: LiveData<List<ObdEntry>> get() = _obdEntries

    private var currentOffset = 0
    private var isLoading = false
    private var hasMoreData = true
    private val pageSize = 30

    fun loadObdEntries(dbHelper: DatabaseHelper) {
        if (isLoading || !hasMoreData) return

        isLoading = true

        viewModelScope.launch(Dispatchers.IO) {
            val newEntries = dbHelper.getRowsFromObdTablePaginated(currentOffset)
            if (newEntries.isNotEmpty()) {
                currentOffset += pageSize
                val updatedList = (_obdEntries.value ?: emptyList()) + newEntries
                _obdEntries.postValue(updatedList)
            } else {
                hasMoreData = false
            }
            isLoading = false
        }
    }
}





/*class FaultCodesViewModel : ViewModel() {
    private val _obdEntries = MutableLiveData<List<ObdEntry>>()
    val obdEntries: LiveData<List<ObdEntry>> get() = _obdEntries



    fun loadObdEntries(databaseHelper: DatabaseHelper) {
        viewModelScope.launch(Dispatchers.IO) {
            //val entries = databaseHelper.getAllRowsFromObdTable()
            val entries = databaseHelper.getAllRowsFromObdTable()
            _obdEntries.postValue(entries)
        }
    }
}*/