package com.akapps.obd2carscannerapp.Database

data class ObdEntry(
    val id: Int,
    val code: String?,
    val name: String?,
    val descrip: String?,
    val sol: String?,
    val symptom: String?,
    val causes: String?,
    val link: String?
)

