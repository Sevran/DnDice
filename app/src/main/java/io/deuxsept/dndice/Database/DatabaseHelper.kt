package io.deuxsept.dndice.Database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import io.deuxsept.dndice.Model.RollModel
import io.deuxsept.dndice.Utils.Utils
import java.util.*

/**
 * Created by Luo
 * 09/08/2016.
 */
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "rolls.db", null, 6) {

    val TAG = "DatabaseHelper"

    val TABLE_RECENT = "recent"
    val TABLE_FAVORITES = "favorites"

    val KEY_RECENT_ID = "id"
    val KEY_RECENT_FORMULA = "formula"
    val KEY_RECENT_RESULT = "result"
    val KEY_RECENT_DETAIL = "detail"
    val KEY_RECENT_FAVORITE = "favorite"
    val KEY_RECENT_TIMESTAMP = "timestamp"

    val KEY_FAVORITES_ID = "id"
    val KEY_FAVORITES_FORMULA = "formula"

    val DATABASE_CREATE_RECENT = """
        CREATE TABLE if not exists $TABLE_RECENT (
                    $KEY_RECENT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $KEY_RECENT_FORMULA TEXT,
                    $KEY_RECENT_RESULT TEXT,
                    $KEY_RECENT_DETAIL TEXT,
                    $KEY_RECENT_FAVORITE INTEGER,
                    $KEY_RECENT_TIMESTAMP INTEGER)"""

    val DATABASE_CREATE_FAVORITES = """
        CREATE TABLE if not exists $TABLE_FAVORITES (
                    $KEY_FAVORITES_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $KEY_FAVORITES_FORMULA TEXT)"""


    override fun onCreate(db: SQLiteDatabase) {
        create(db)
    }

    fun create(db: SQLiteDatabase) {
        Utils.log_i(TAG, "Creating: $DATABASE_CREATE_RECENT")
        db.execSQL(DATABASE_CREATE_RECENT)
        Utils.log_i(TAG, "Creating: $DATABASE_CREATE_FAVORITES")
        db.execSQL(DATABASE_CREATE_FAVORITES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion != newVersion) {
            Utils.log_i(TAG, "Dropping: $TABLE_RECENT")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_RECENT;")
            Utils.log_i(TAG, "Dropping: $TABLE_FAVORITES")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_FAVORITES;")
            create(db)
        }
    }

    companion object {
        private var mDatabaseHelper: DatabaseHelper? = null
        fun getInstance(context: Context): DatabaseHelper? {
            if (mDatabaseHelper == null) {
                mDatabaseHelper = DatabaseHelper(context)
            }
            return mDatabaseHelper
        }
    }

    fun getAllRecentRolls(): List<RollModel> {
        val ROLL_SELECT_QUERY = String.format("SELECT * FROM %s ORDER BY %s DESC", TABLE_RECENT, KEY_RECENT_ID)
        val rolls: MutableList<RollModel> = ArrayList()
        val cursor = readableDatabase.rawQuery(ROLL_SELECT_QUERY, null)
            if (cursor.moveToFirst()) {
                do {
                    rolls.add(RollModel(
                            cursor.getString(cursor.getColumnIndex(KEY_RECENT_FORMULA)),
                            cursor.getString(cursor.getColumnIndex(KEY_RECENT_RESULT)),
                            cursor.getString(cursor.getColumnIndex(KEY_RECENT_DETAIL)),
                            cursor.getInt(cursor.getColumnIndex(KEY_RECENT_ID)),
                            cursor.getLong(cursor.getColumnIndex(KEY_RECENT_TIMESTAMP)),
                            (cursor.getInt(cursor.getColumnIndex(KEY_RECENT_FAVORITE)) == 1)
                    ))
                } while(cursor.moveToNext())
            }
        cursor.close()
        return rolls
    }

    fun getNbRecentRolls(): Int {
        val ROLL_SELECT_QUERY = String.format("SELECT count(*) FROM %s", TABLE_RECENT)
        val cursor = readableDatabase.rawQuery(ROLL_SELECT_QUERY, null)
        cursor.moveToFirst()
        val nb = cursor.getInt(0)
        cursor.close()
        return nb
    }

    fun addRecentRoll(roll: RollModel) {
        if (getNbRecentRolls() >=  100) deleteOutdatedRoll()
        val values = ContentValues()
        values.put(KEY_RECENT_FORMULA, roll.formula)
        values.put(KEY_RECENT_RESULT, roll.result)
        values.put(KEY_RECENT_DETAIL, roll.detail)
        values.put(KEY_RECENT_TIMESTAMP, System.currentTimeMillis())
        val id = writableDatabase.insert(TABLE_RECENT, null, values)
        Utils.log_d("Database", "Recent roll inserted with id : " + id)
    }

    fun deleteOutdatedRoll() {
        val OUTDATED_QUERY = String.format("SELECT %s FROM %s ORDER BY id LIMIT 1", KEY_RECENT_ID, TABLE_RECENT)
        val cursor = readableDatabase.rawQuery(OUTDATED_QUERY, null)
        cursor.moveToFirst()
        val id = cursor.getInt(cursor.getColumnIndex(KEY_RECENT_ID))
        cursor.close()
        writableDatabase.delete(TABLE_RECENT, "$KEY_RECENT_ID = " + id, null)
        Utils.log_d("Database", "Recent roll deleted with id : " + id)
    }

    fun getAllFavoritesRolls(): List<RollModel> {
        val ROLL_SELECT_QUERY = String.format("SELECT * FROM %s ORDER BY %s DESC", TABLE_FAVORITES, KEY_FAVORITES_ID)
        val rolls: MutableList<RollModel> = ArrayList()
        val cursor = readableDatabase.rawQuery(ROLL_SELECT_QUERY, null)
        if (cursor.moveToFirst()) {
            do {
                rolls.add(RollModel(
                        cursor.getString(cursor.getColumnIndex(KEY_FAVORITES_FORMULA)),"","",
                        cursor.getInt(cursor.getColumnIndex(KEY_FAVORITES_ID))
                ))
            } while(cursor.moveToNext())
        }
        cursor.close()
        return rolls
    }

    fun addFavoriteRoll(roll: RollModel) {
        val values = ContentValues()
        values.put(KEY_FAVORITES_FORMULA, roll.formula)
        val id = writableDatabase.insert(TABLE_FAVORITES, null, values)
        Utils.log_d("Database", "Fav roll inserted with id : " + id)
    }

    fun deleteFavoriteRoll(rollId: Int) {
        writableDatabase.delete(TABLE_FAVORITES, "$KEY_FAVORITES_ID = " + rollId, null)
        Utils.log_d("Database", "Fav roll deleted with id : " + rollId)
    }
}
