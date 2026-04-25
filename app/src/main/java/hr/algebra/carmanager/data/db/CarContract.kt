package hr.algebra.carmanager.data.db

import android.provider.BaseColumns

object CarContract {

    const val DATABASE_NAME = "car_manager.db"
    const val DATABASE_VERSION = 1

    object CarEntry : BaseColumns {
        const val TABLE_NAME = "cars"

        const val COLUMN_ID = BaseColumns._ID
        const val COLUMN_BRAND = "brand"
        const val COLUMN_MODEL = "model"
        const val COLUMN_YEAR = "year"
        const val COLUMN_REGISTRATION_NUMBER = "registration_number"
        const val COLUMN_MILEAGE = "mileage"
        const val COLUMN_FUEL_TYPE = "fuel_type"
        const val COLUMN_REGISTRATION_EXPIRY_DATE = "registration_expiry_date"
        const val COLUMN_NOTES = "notes"
        const val COLUMN_IMAGE_URI = "image_uri"
    }

    const val SQL_CREATE_CARS_TABLE = """
        CREATE TABLE ${CarEntry.TABLE_NAME} (
            ${CarEntry.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            ${CarEntry.COLUMN_BRAND} TEXT NOT NULL,
            ${CarEntry.COLUMN_MODEL} TEXT NOT NULL,
            ${CarEntry.COLUMN_YEAR} INTEGER NOT NULL,
            ${CarEntry.COLUMN_REGISTRATION_NUMBER} TEXT NOT NULL,
            ${CarEntry.COLUMN_MILEAGE} INTEGER NOT NULL,
            ${CarEntry.COLUMN_FUEL_TYPE} TEXT NOT NULL,
            ${CarEntry.COLUMN_REGISTRATION_EXPIRY_DATE} TEXT NOT NULL,
            ${CarEntry.COLUMN_NOTES} TEXT,
            ${CarEntry.COLUMN_IMAGE_URI} TEXT
        )
    """

    const val SQL_DROP_CARS_TABLE = """
        DROP TABLE IF EXISTS ${CarEntry.TABLE_NAME}
    """
}