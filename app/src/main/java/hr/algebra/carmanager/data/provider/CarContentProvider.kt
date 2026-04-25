package hr.algebra.carmanager.data.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import hr.algebra.carmanager.data.db.CarContract.CarEntry
import hr.algebra.carmanager.data.db.CarDbHelper

class CarContentProvider : ContentProvider() {

    private lateinit var dbHelper: CarDbHelper

    override fun onCreate(): Boolean {
        dbHelper = CarDbHelper(requireNotNull(context))
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val db = dbHelper.readableDatabase

        val cursor = when (uriMatcher.match(uri)) {
            CARS -> db.query(
                CarEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder ?: "${CarEntry.COLUMN_BRAND} ASC"
            )

            CAR_ID -> {
                val id = ContentUris.parseId(uri)
                db.query(
                    CarEntry.TABLE_NAME,
                    projection,
                    "${CarEntry.COLUMN_ID} = ?",
                    arrayOf(id.toString()),
                    null,
                    null,
                    sortOrder
                )
            }

            else -> throw IllegalArgumentException("Nepoznat URI: $uri")
        }

        cursor.setNotificationUri(requireNotNull(context).contentResolver, uri)
        return cursor
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri {
        if (uriMatcher.match(uri) != CARS) {
            throw IllegalArgumentException("Insert nije podržan za URI: $uri")
        }

        val db = dbHelper.writableDatabase
        val id = db.insert(CarEntry.TABLE_NAME, null, values)

        if (id == -1L) {
            throw IllegalStateException("Greška pri unosu automobila")
        }

        val insertedUri = ContentUris.withAppendedId(CONTENT_URI, id)
        requireNotNull(context).contentResolver.notifyChange(insertedUri, null)

        return insertedUri
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        val db = dbHelper.writableDatabase

        val rowsUpdated = when (uriMatcher.match(uri)) {
            CAR_ID -> {
                val id = ContentUris.parseId(uri)
                db.update(
                    CarEntry.TABLE_NAME,
                    values,
                    "${CarEntry.COLUMN_ID} = ?",
                    arrayOf(id.toString())
                )
            }

            CARS -> db.update(
                CarEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs
            )

            else -> throw IllegalArgumentException("Update nije podržan za URI: $uri")
        }

        if (rowsUpdated > 0) {
            requireNotNull(context).contentResolver.notifyChange(uri, null)
        }

        return rowsUpdated
    }

    override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        val db = dbHelper.writableDatabase

        val rowsDeleted = when (uriMatcher.match(uri)) {
            CAR_ID -> {
                val id = ContentUris.parseId(uri)
                db.delete(
                    CarEntry.TABLE_NAME,
                    "${CarEntry.COLUMN_ID} = ?",
                    arrayOf(id.toString())
                )
            }

            CARS -> db.delete(
                CarEntry.TABLE_NAME,
                selection,
                selectionArgs
            )

            else -> throw IllegalArgumentException("Delete nije podržan za URI: $uri")
        }

        if (rowsDeleted > 0) {
            requireNotNull(context).contentResolver.notifyChange(uri, null)
        }

        return rowsDeleted
    }

    override fun getType(uri: Uri): String {
        return when (uriMatcher.match(uri)) {
            CARS -> "vnd.android.cursor.dir/vnd.$AUTHORITY.cars"
            CAR_ID -> "vnd.android.cursor.item/vnd.$AUTHORITY.cars"
            else -> throw IllegalArgumentException("Nepoznat URI: $uri")
        }
    }

    companion object {
        const val AUTHORITY = "hr.algebra.carmanager.provider"

        private const val CARS = 1
        private const val CAR_ID = 2

        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/${CarEntry.TABLE_NAME}")

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, CarEntry.TABLE_NAME, CARS)
            addURI(AUTHORITY, "${CarEntry.TABLE_NAME}/#", CAR_ID)
        }
    }
}