package hr.algebra.carmanager.data.repository

import android.content.ContentValues
import android.content.Context
import hr.algebra.carmanager.data.db.CarContract.CarEntry
import hr.algebra.carmanager.data.db.CarDbHelper
import hr.algebra.carmanager.model.Car

class CarRepository(context: Context) {

    private val dbHelper = CarDbHelper(context.applicationContext)

    fun insertCar(car: Car): Long {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(CarEntry.COLUMN_BRAND, car.brand)
            put(CarEntry.COLUMN_MODEL, car.model)
            put(CarEntry.COLUMN_YEAR, car.year)
            put(CarEntry.COLUMN_REGISTRATION_NUMBER, car.registrationNumber)
            put(CarEntry.COLUMN_MILEAGE, car.mileage)
            put(CarEntry.COLUMN_FUEL_TYPE, car.fuelType)
            put(CarEntry.COLUMN_REGISTRATION_EXPIRY_DATE, car.registrationExpiryDate)
            put(CarEntry.COLUMN_NOTES, car.notes)
            put(CarEntry.COLUMN_IMAGE_URI, car.imageUri)
        }

        return db.insert(CarEntry.TABLE_NAME, null, values)
    }

    fun getAllCars(): List<Car> {
        val cars = mutableListOf<Car>()
        val db = dbHelper.readableDatabase

        val cursor = db.query(
            CarEntry.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            "${CarEntry.COLUMN_BRAND} ASC"
        )

        cursor.use {
            while (it.moveToNext()) {
                cars.add(cursorToCar(it))
            }
        }

        return cars
    }

    fun getCarById(id: Long): Car? {
        val db = dbHelper.readableDatabase

        val cursor = db.query(
            CarEntry.TABLE_NAME,
            null,
            "${CarEntry.COLUMN_ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        cursor.use {
            return if (it.moveToFirst()) {
                cursorToCar(it)
            } else {
                null
            }
        }
    }

    fun updateCar(car: Car): Int {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(CarEntry.COLUMN_BRAND, car.brand)
            put(CarEntry.COLUMN_MODEL, car.model)
            put(CarEntry.COLUMN_YEAR, car.year)
            put(CarEntry.COLUMN_REGISTRATION_NUMBER, car.registrationNumber)
            put(CarEntry.COLUMN_MILEAGE, car.mileage)
            put(CarEntry.COLUMN_FUEL_TYPE, car.fuelType)
            put(CarEntry.COLUMN_REGISTRATION_EXPIRY_DATE, car.registrationExpiryDate)
            put(CarEntry.COLUMN_NOTES, car.notes)
            put(CarEntry.COLUMN_IMAGE_URI, car.imageUri)
        }

        return db.update(
            CarEntry.TABLE_NAME,
            values,
            "${CarEntry.COLUMN_ID} = ?",
            arrayOf(car.id.toString())
        )
    }

    fun deleteCar(id: Long): Int {
        val db = dbHelper.writableDatabase

        return db.delete(
            CarEntry.TABLE_NAME,
            "${CarEntry.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
    }

    fun searchCars(query: String): List<Car> {
        val cars = mutableListOf<Car>()
        val db = dbHelper.readableDatabase

        val cursor = db.query(
            CarEntry.TABLE_NAME,
            null,
            "${CarEntry.COLUMN_BRAND} LIKE ? OR ${CarEntry.COLUMN_MODEL} LIKE ?",
            arrayOf("%$query%", "%$query%"),
            null,
            null,
            "${CarEntry.COLUMN_BRAND} ASC"
        )

        cursor.use {
            while (it.moveToNext()) {
                cars.add(cursorToCar(it))
            }
        }

        return cars
    }

    private fun cursorToCar(cursor: android.database.Cursor): Car {
        return Car(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(CarEntry.COLUMN_ID)),
            brand = cursor.getString(cursor.getColumnIndexOrThrow(CarEntry.COLUMN_BRAND)),
            model = cursor.getString(cursor.getColumnIndexOrThrow(CarEntry.COLUMN_MODEL)),
            year = cursor.getInt(cursor.getColumnIndexOrThrow(CarEntry.COLUMN_YEAR)),
            registrationNumber = cursor.getString(cursor.getColumnIndexOrThrow(CarEntry.COLUMN_REGISTRATION_NUMBER)),
            mileage = cursor.getInt(cursor.getColumnIndexOrThrow(CarEntry.COLUMN_MILEAGE)),
            fuelType = cursor.getString(cursor.getColumnIndexOrThrow(CarEntry.COLUMN_FUEL_TYPE)),
            registrationExpiryDate = cursor.getString(cursor.getColumnIndexOrThrow(CarEntry.COLUMN_REGISTRATION_EXPIRY_DATE)),
            notes = cursor.getString(cursor.getColumnIndexOrThrow(CarEntry.COLUMN_NOTES)) ?: "",
            imageUri = cursor.getString(cursor.getColumnIndexOrThrow(CarEntry.COLUMN_IMAGE_URI))
        )
    }
}