package com.akapps.obd2carscannerapp.Database

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.akapps.obd2carscannerapp.Database.DatabaseHelper.Companion.TABLE_OBD
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "obdf.db"
        private const val DATABASE_VERSION = 1
        private const val DATABASE_PATH = "/data/data/com.akapps.obd2carscannerapp/databases/"
        private const val TABLE_OBD = "obd"
        // Page size for pagination
        private const val PAGE_SIZE = 30
    }

    private val context: Context = context

    override fun onCreate(db: SQLiteDatabase?) {
        // No need to create tables here since we are copying the existing database
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Handle database upgrade as needed
    }

    fun createDatabase() {
        val dbFile = context.getDatabasePath(DATABASE_NAME)

        if (!dbFile.exists()) {
            this.readableDatabase
            copyDatabase()
        }
    }

    private fun copyDatabase() {
        try {
            val inputStream: InputStream = context.assets.open(DATABASE_NAME)
            val outputStream: OutputStream = FileOutputStream(DATABASE_PATH + DATABASE_NAME)

            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }

            outputStream.flush()
            outputStream.close()
            inputStream.close()
        } catch (e: IOException) {
            throw RuntimeException("Error copying database", e)
        }
    }

    fun openDatabase(): SQLiteDatabase {
        return SQLiteDatabase.openDatabase(
            DATABASE_PATH + DATABASE_NAME,
            null,
            SQLiteDatabase.OPEN_READWRITE
        )
    }

    // Paginated data retrieval
    fun getRowsFromObdTablePaginated(offset: Int): List<ObdEntry> {
        val db = openDatabase()
        val resultList = mutableListOf<ObdEntry>()
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM $TABLE_OBD LIMIT $PAGE_SIZE OFFSET ?",
            arrayOf(offset.toString())
        )

        cursor.use { // Automatically closes the cursor after usage
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                    val code = cursor.getString(cursor.getColumnIndexOrThrow("code"))
                    val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                    val descrip = cursor.getString(cursor.getColumnIndexOrThrow("descrip"))
                    val sol = cursor.getString(cursor.getColumnIndexOrThrow("sol"))
                    val symptom = cursor.getString(cursor.getColumnIndexOrThrow("symptom"))
                    val causes = cursor.getString(cursor.getColumnIndexOrThrow("causes"))
                    val link = cursor.getString(cursor.getColumnIndexOrThrow("link"))

                    val entry = ObdEntry(id, code, name, descrip, sol, symptom, causes, link)
                    resultList.add(entry)
                } while (cursor.moveToNext())
            }
        }

        db.close()
        return resultList
    }

    // Original method modified to use pagination
    fun getAllRowsFromObdTable(): List<ObdEntry> {
        val allRows = mutableListOf<ObdEntry>()
        var offset = 0

        while (true) {
            val rows = getRowsFromObdTablePaginated(offset)
            if (rows.isEmpty()) break
            allRows.addAll(rows)
            offset += PAGE_SIZE
        }

        return allRows
    }

    fun getRowsFromObdTableWithCode(code: String): List<ObdEntry> {
        val db = openDatabase()
        val resultList = mutableListOf<ObdEntry>()
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM $TABLE_OBD WHERE code = ?",
            arrayOf(code)
        )

        cursor.use { // Automatically closes the cursor after usage
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                    val code = cursor.getString(cursor.getColumnIndexOrThrow("code"))
                    val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                    val descrip = cursor.getString(cursor.getColumnIndexOrThrow("descrip"))
                    val sol = cursor.getString(cursor.getColumnIndexOrThrow("sol"))
                    val symptom = cursor.getString(cursor.getColumnIndexOrThrow("symptom"))
                    val causes = cursor.getString(cursor.getColumnIndexOrThrow("causes"))
                    val link = cursor.getString(cursor.getColumnIndexOrThrow("link"))

                    val entry = ObdEntry(id, code, name, descrip, sol, symptom, causes, link)
                    resultList.add(entry)
                } while (cursor.moveToNext())
            }
        }

        db.close()
        return resultList
    }
}


/*class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "obdf.db"
        private const val DATABASE_VERSION = 1
        private const val DATABASE_PATH = "/data/data/com.akapps.obd2carscannerapp/databases/"
        private const val TABLE_OBD = "obd"
        const val PAGE_SIZE = 100
    }

    private val context: Context = context

    override fun onCreate(db: SQLiteDatabase?) {
        // No need to create tables here since we are copying the existing database
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Handle database upgrade as needed
    }

    fun createDatabase() {
        val dbFile = context.getDatabasePath(DATABASE_NAME)
        if (!dbFile.exists()) {
            this.readableDatabase
            copyDatabase()
        }
    }

    private fun copyDatabase() {
        try {
            context.assets.open(DATABASE_NAME).use { inputStream ->
                FileOutputStream(DATABASE_PATH + DATABASE_NAME).use { outputStream ->
                    val buffer = ByteArray(1024)
                    var length: Int
                    while (inputStream.read(buffer).also { length = it } > 0) {
                        outputStream.write(buffer, 0, length)
                    }
                }
            }
        } catch (e: IOException) {
            throw RuntimeException("Error copying database", e)
        }
    }

    fun openDatabase(): SQLiteDatabase {
        return SQLiteDatabase.openDatabase(
            DATABASE_PATH + DATABASE_NAME,
            null,
            SQLiteDatabase.OPEN_READWRITE
        )
    }

    // Retrieve paginated data
    fun getRowsFromObdTablePaginated(offset: Int, limit: Int = PAGE_SIZE): List<ObdEntry> {
        val db = openDatabase()
        val resultList = mutableListOf<ObdEntry>()
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM $TABLE_OBD LIMIT $limit OFFSET ?",
            arrayOf(offset.toString())
        )

        cursor.use {
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                    val code = cursor.getString(cursor.getColumnIndexOrThrow("code"))
                    val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                    val descrip = cursor.getString(cursor.getColumnIndexOrThrow("descrip"))
                    val sol = cursor.getString(cursor.getColumnIndexOrThrow("sol"))
                    val symptom = cursor.getString(cursor.getColumnIndexOrThrow("symptom"))
                    val causes = cursor.getString(cursor.getColumnIndexOrThrow("causes"))
                    val link = cursor.getString(cursor.getColumnIndexOrThrow("link"))

                    val entry = ObdEntry(id, code, name, descrip, sol, symptom, causes, link)
                    resultList.add(entry)
                } while (cursor.moveToNext())
            }
        }

        db.close()
        return resultList
    }
    fun getRowsFromObdTableWithCode(code: String): List<ObdEntry> {
        val db = openDatabase()
        val resultList = mutableListOf<ObdEntry>()
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM $TABLE_OBD WHERE code = ?",
            arrayOf(code)
        )

        cursor.use { // Automatically closes the cursor after usage
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                    val code = cursor.getString(cursor.getColumnIndexOrThrow("code"))
                    val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                    val descrip = cursor.getString(cursor.getColumnIndexOrThrow("descrip"))
                    val sol = cursor.getString(cursor.getColumnIndexOrThrow("sol"))
                    val symptom = cursor.getString(cursor.getColumnIndexOrThrow("symptom"))
                    val causes = cursor.getString(cursor.getColumnIndexOrThrow("causes"))
                    val link = cursor.getString(cursor.getColumnIndexOrThrow("link"))

                    val entry = ObdEntry(id, code, name, descrip, sol, symptom, causes, link)
                    resultList.add(entry)
                } while (cursor.moveToNext())
            }
        }

        db.close()
        return resultList
    }

}*/








/*class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "obdf.db"
        private const val DATABASE_VERSION = 1
        private const val DATABASE_PATH = "/data/data/com.akapps.obd2carscannerapp/databases/"
        private const val TABLE_OBD = "obd"
        // Page size for pagination
        private const val PAGE_SIZE = 20
    }

    private val context: Context = context

    override fun onCreate(db: SQLiteDatabase?) {
        // No need to create tables here since we are copying the existing database
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Handle database upgrade as needed
    }

    fun createDatabase() {
        val dbFile = context.getDatabasePath(DATABASE_NAME)

        if (!dbFile.exists()) {
            this.readableDatabase
            copyDatabase()
        }
    }

    private fun copyDatabase() {
        try {
            context.assets.open(DATABASE_NAME).use { inputStream ->
                FileOutputStream(DATABASE_PATH + DATABASE_NAME).use { outputStream ->
                    val buffer = ByteArray(1024)
                    var length: Int
                    while (inputStream.read(buffer).also { length = it } > 0) {
                        outputStream.write(buffer, 0, length)
                    }
                }
            }
        } catch (e: IOException) {
            throw RuntimeException("Error copying database", e)
        }
    }

    fun openDatabase(): SQLiteDatabase {
        return SQLiteDatabase.openDatabase(
            DATABASE_PATH + DATABASE_NAME,
            null,
            SQLiteDatabase.OPEN_READWRITE
        )
    }

    // Paginated data retrieval
    fun getRowsFromObdTablePaginated(offset: Int): List<ObdEntry> {
        val db = openDatabase()
        val resultList = mutableListOf<ObdEntry>()
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM $TABLE_OBD LIMIT $PAGE_SIZE OFFSET ?",
            arrayOf(offset.toString())
        )

        cursor.use { // Automatically closes the cursor after usage
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                    val code = cursor.getString(cursor.getColumnIndexOrThrow("code"))
                    val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                    val descrip = cursor.getString(cursor.getColumnIndexOrThrow("descrip"))
                    val sol = cursor.getString(cursor.getColumnIndexOrThrow("sol"))
                    val symptom = cursor.getString(cursor.getColumnIndexOrThrow("symptom"))
                    val causes = cursor.getString(cursor.getColumnIndexOrThrow("causes"))
                    val link = cursor.getString(cursor.getColumnIndexOrThrow("link"))

                    val entry = ObdEntry(id, code, name, descrip, sol, symptom, causes, link)
                    resultList.add(entry)
                } while (cursor.moveToNext())
            }
        }

        db.close()
        return resultList
    }

    // Original method modified to use pagination
    fun getAllRowsFromObdTable(): List<ObdEntry> {
        val allRows = mutableListOf<ObdEntry>()
        var offset = 0

        while (true) {
            val rows = getRowsFromObdTablePaginated(offset)
            if (rows.isEmpty()) break
            allRows.addAll(rows)
            offset += PAGE_SIZE
        }

        return allRows
    }

    fun getRowsFromObdTableWithCode(code: String): List<ObdEntry> {
        val db = openDatabase()
        val resultList = mutableListOf<ObdEntry>()
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM $TABLE_OBD WHERE code = ?",
            arrayOf(code)
        )

        cursor.use { // Automatically closes the cursor after usage
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                    val code = cursor.getString(cursor.getColumnIndexOrThrow("code"))
                    val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                    val descrip = cursor.getString(cursor.getColumnIndexOrThrow("descrip"))
                    val sol = cursor.getString(cursor.getColumnIndexOrThrow("sol"))
                    val symptom = cursor.getString(cursor.getColumnIndexOrThrow("symptom"))
                    val causes = cursor.getString(cursor.getColumnIndexOrThrow("causes"))
                    val link = cursor.getString(cursor.getColumnIndexOrThrow("link"))

                    val entry = ObdEntry(id, code, name, descrip, sol, symptom, causes, link)
                    resultList.add(entry)
                } while (cursor.moveToNext())
            }
        }

        db.close()
        return resultList
    }
}*/











/*class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "obdf.db"
        private const val DATABASE_VERSION = 1
        private const val DATABASE_PATH = "/data/data/com.akapps.obd2carscannerapp/databases/"
        private const val TABLE_OBD = "obd"
        //ad page size
        private const val PAGE_SIZE = 100
    }

    private val context: Context = context

    override fun onCreate(db: SQLiteDatabase?) {
        // No need to create tables here since we are copying the existing database
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Handle database upgrade as needed
    }

    fun createDatabase() {
        val dbFile = context.getDatabasePath(DATABASE_NAME)

        if (!dbFile.exists()) {
            this.readableDatabase
            copyDatabase()
        }
    }

    private fun copyDatabase() {
        try {
            val inputStream: InputStream = context.assets.open(DATABASE_NAME)
            val outputStream: OutputStream = FileOutputStream(DATABASE_PATH + DATABASE_NAME)

            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }

            outputStream.flush()
            outputStream.close()
            inputStream.close()
        } catch (e: IOException) {
            throw RuntimeException("Error copying database", e)
        }
    }

    fun openDatabase(): SQLiteDatabase {
        return SQLiteDatabase.openDatabase(
            DATABASE_PATH + DATABASE_NAME,
            null,
            SQLiteDatabase.OPEN_READWRITE
        )
    }



    // New method to get all rows from the obd table
    fun getAllRowsFromObdTable(): List<ObdEntry> {
        val db = openDatabase()
        val resultList = mutableListOf<ObdEntry>()
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_OBD", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val code = cursor.getString(cursor.getColumnIndexOrThrow("code"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val descrip = cursor.getString(cursor.getColumnIndexOrThrow("descrip"))
                val sol = cursor.getString(cursor.getColumnIndexOrThrow("sol"))
                val symptom = cursor.getString(cursor.getColumnIndexOrThrow("symptom"))
                val causes = cursor.getString(cursor.getColumnIndexOrThrow("causes"))
                val link = cursor.getString(cursor.getColumnIndexOrThrow("link"))

                val entry = ObdEntry(id, code, name, descrip, sol, symptom, causes, link)
                resultList.add(entry)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return resultList
    }

    fun getRowsFromObdTableWithCode(code: String): List<ObdEntry> {
        val db = openDatabase()
        val resultList = mutableListOf<ObdEntry>()
//        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_OBD Where code = $code", null)
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM $TABLE_OBD WHERE code = ?",
            arrayOf(code)
        )
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val code = cursor.getString(cursor.getColumnIndexOrThrow("code"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val descrip = cursor.getString(cursor.getColumnIndexOrThrow("descrip"))
                val sol = cursor.getString(cursor.getColumnIndexOrThrow("sol"))
                val symptom = cursor.getString(cursor.getColumnIndexOrThrow("symptom"))
                val causes = cursor.getString(cursor.getColumnIndexOrThrow("causes"))
                val link = cursor.getString(cursor.getColumnIndexOrThrow("link"))

                val entry = ObdEntry(id, code, name, descrip, sol, symptom, causes, link)
                resultList.add(entry)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return resultList
    }
}*/

// Paginated data retrieval
/*fun getRowsFromObdTablePaginated(offset: Int): List<ObdEntry> {
    val db = openDatabase()
    val resultList = mutableListOf<ObdEntry>()
    val cursor: Cursor = db.rawQuery(
        "SELECT * FROM $TABLE_OBD LIMIT $PAGE_SIZE OFFSET ?",
        arrayOf(offset.toString())
    )

    try {
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val code = cursor.getString(cursor.getColumnIndexOrThrow("code"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val descrip = cursor.getString(cursor.getColumnIndexOrThrow("descrip"))
                val sol = cursor.getString(cursor.getColumnIndexOrThrow("sol"))
                val symptom = cursor.getString(cursor.getColumnIndexOrThrow("symptom"))
                val causes = cursor.getString(cursor.getColumnIndexOrThrow("causes"))
                val link = cursor.getString(cursor.getColumnIndexOrThrow("link"))

                val entry = ObdEntry(id, code, name, descrip, sol, symptom, causes, link)
                resultList.add(entry)
            } while (cursor.moveToNext())
        }
    } finally {
        cursor.close()
        db.close()
    }

    return resultList
}*/