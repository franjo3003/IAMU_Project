package hr.algebra.carmanager.network

import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class CarApiClient {

    fun fetchCarMakes(): List<String> {
        val url = URL("https://vpic.nhtsa.dot.gov/api/vehicles/getallmakes?format=json")

        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"

        val reader = BufferedReader(InputStreamReader(connection.inputStream))
        val response = reader.readText()

        val jsonObject = JSONObject(response)
        val results = jsonObject.getJSONArray("Results")

        val makes = mutableListOf<String>()

        for (i in 0 until results.length()) {
            val obj = results.getJSONObject(i)
            makes.add(obj.getString("Make_Name"))
        }

        val allowedMakes = listOf(
            "BMW",
            "TOYOTA",
            "VOLKSWAGEN",
            "AUDI",
            "MERCEDES-BENZ",
            "FORD",
            "HONDA",
            "HYUNDAI",
            "KIA",
            "RENAULT"
        )

        return makes
            .filter { it.uppercase() in allowedMakes }
            .distinct()
    }
}