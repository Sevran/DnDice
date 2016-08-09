package io.deuxsept.dndice.Database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import io.deuxsept.dndice.Utils.Utils

/**
 * Created by Luo
 * 09/08/2016.
 */
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "rolls.db", null, 4) {

    val TAG = "DatabaseHelper"

    val TABLE_RECENT = "recent"
    val TABLE_FAVORITES = "favorites"

    val KEY_RECENT_ID = "id"
    val KEY_RECENT_FORMULA = "formula"
    val KEY_RECENT_RESULT = "result"
    val KEY_RECENT_DETAIL = "detail"
    val KEY_RECENT_TIMESTAMP = "timestamp"

    val KEY_FAVORITES_ID = "id"
    val KEY_FAVORITES_FORMULA = "formula"
    val KEY_FAVORITES_TIMESTAMP = "timestamp"

    val DATABASE_CREATE_RECENT =
            "CREATE TABLE if not exists " + TABLE_RECENT + " (" +
                    KEY_RECENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    KEY_RECENT_FORMULA + " TEXT,"+
                    KEY_RECENT_RESULT + " TEXT,"+
                    KEY_RECENT_DETAIL + " TEXT,"+
                    KEY_RECENT_TIMESTAMP + " INTEGER" +
                    ")"

    val DATABASE_CREATE_FAVORITES =
            "CREATE TABLE if not exists " + TABLE_FAVORITES + " (" +
                    KEY_FAVORITES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    KEY_FAVORITES_FORMULA + " TEXT,"+
                    KEY_FAVORITES_TIMESTAMP + " INTEGER" +
                    ")"

    override fun onCreate(db: SQLiteDatabase) {
        Utils.Log_i(TAG, "Creating: " + DATABASE_CREATE_RECENT)
        db.execSQL(DATABASE_CREATE_RECENT)
        Utils.Log_i(TAG, "Creating: " + DATABASE_CREATE_FAVORITES)
        db.execSQL(DATABASE_CREATE_FAVORITES)
    }

    override fun onUpgrade(db: SQLiteDatabase, i: Int, i1: Int) {
        Utils.Log_i(TAG, "Droping: " + DATABASE_CREATE_RECENT)
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_CREATE_RECENT)
        Utils.Log_i(TAG, "Droping: " + DATABASE_CREATE_FAVORITES)
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_CREATE_FAVORITES)
    }
}
