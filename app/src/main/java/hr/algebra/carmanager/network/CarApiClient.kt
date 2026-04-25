package hr.algebra.carmanager.network

import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class CarApiClient {

    fun fetchCarFact(): String {
        val url = URL("https://catfact.ninja/fact")
        val connection = url.openConnection() as HttpURLConnection

        return try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            val response = connection.inputStream.bufferedReader().use {
                it.readText()
            }

            val json = JSONObject(response)
            json.getString("fact")
        } finally {
            connection.disconnect()
        }
    }
}