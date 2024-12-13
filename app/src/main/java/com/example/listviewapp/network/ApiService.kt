package com.example.listviewapp.network

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.listviewapp.model.Item
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL

object ApiService {

    private const val API_URL = "https://ca23e1b87c513f5451c9.free.beeceptor.com/api/users/"


    fun getItem(onResult: (List<Item>?) -> Unit) {
        Thread {
            try {
                // Create a URI object from the URL string
                val uri = URI.create(API_URL)

                // Create a URL object
                val url = URL(uri.toString())

                // Open a connection to the API
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()

                // Read the response
                val inputStream = connection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))
                val response = StringBuilder()

                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }


                val animalList = parseAnimalResponse(response.toString())


                onResult(animalList)

                connection.disconnect()

            } catch (e: Exception) {
                Log.e("ApiService", "Error fetching animals: ${e.message}")
                onResult(null) // In case of error, return null
            }
        }.start()
    }

    fun deleteItem(itemId: Int, onResult: (Boolean) -> Unit) {
        Thread {
            try {
                // Construct the URL for the delete request (append the animal ID)
                val url = URL("$API_URL$itemId")

                // Open the connection
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "DELETE"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                // Send the request and get the response code
                val responseCode = connection.responseCode
                val inputStream = if (responseCode == HttpURLConnection.HTTP_NO_CONTENT || responseCode == HttpURLConnection.HTTP_OK) {
                    connection.inputStream
                } else {
                    connection.errorStream
                }
                val responseMessage = inputStream.bufferedReader().use { it.readText() }
                Log.d("ApiService", "Response: $responseMessage")

                // Check if deletion was successful
                if (responseCode == HttpURLConnection.HTTP_NO_CONTENT || responseCode == HttpURLConnection.HTTP_OK) {
                    onResult(true)
                } else {
                    onResult(false)
                }

                // Close the connection
                connection.disconnect()

            } catch (e: Exception) {
                Log.e("ApiService", "Error deleting animal: ${e.message}")
                onResult(false) // In case of error, return false
            }
        }.start()
    }

    fun updateItem(itemId: Int, newName: String, onResult: (Boolean) -> Unit) {
        Thread {
            try {
                val url = URL("$API_URL$itemId")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "PUT"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                val jsonBody = JSONObject()
                jsonBody.put("name", newName)

                connection.outputStream.write(jsonBody.toString().toByteArray())
                connection.outputStream.flush()

                val responseCode = connection.responseCode
                onResult(responseCode == HttpURLConnection.HTTP_OK)

                connection.disconnect()
            } catch (e: Exception) {
                Log.e("ApiService", "Error updating animal: ${e.message}")
                onResult(false)
            }
        }.start()
    }

    fun addItem(item: Item, onResult: (Boolean) -> Unit) {
        Thread {
            try {
                val url = URL(API_URL)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                val jsonBody = JSONObject()
                jsonBody.put("id", item.id)
                jsonBody.put("name", item.name)

                connection.outputStream.write(jsonBody.toString().toByteArray())
                connection.outputStream.flush()

                val responseCode = connection.responseCode
                Log.d("ApiService", "Add Animal Response Code: $responseCode")

                // Check if the response is HTTP_OK or HTTP_CREATED
                onResult(responseCode == HttpURLConnection.HTTP_CREATED || responseCode == HttpURLConnection.HTTP_OK)

                connection.disconnect()
            } catch (e: Exception) {
                Log.e("ApiService", "Error adding animal: ${e.message}")
                onResult(false)
            }
        }.start()
    }






    // Helper function to parse the JSON response into a list of Animal objects
    private fun parseAnimalResponse(response: String): List<Item> {
        val animalList = mutableListOf<Item>()
        try {
            val jsonArray = JSONArray(response)
            for (i in 0 until jsonArray.length()) {
                val animalJson = jsonArray.getJSONObject(i)
                val id = animalJson.getInt("id")
                val name = animalJson.getString("name")
                animalList.add(Item(id, name))
            }
        } catch (e: Exception) {
            Log.e("ApiService", "Error parsing JSON: ${e.message}")
        }
        return animalList
    }
}
