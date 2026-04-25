package hr.algebra.carmanager.data.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class CarDbHelper(context: Context) : SQLiteOpenHelper(
    context,
    CarContract.DATABASE_NAME,
    null,
    CarContract.DATABASE_VERSION
) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CarContract.SQL_CREATE_CARS_TABLE)
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        db.execSQL(CarContract.SQL_DROP_CARS_TABLE)
        onCreate(db)
    }
}