package com.skywave.callpeon

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    var btnSend: Button? = null
    private val FCM_API = "https://fcm.googleapis.com/fcm/send"
    private val serverKey =
        "key=" + "AAAAZPWrNgw:APA91bExsMk3HoYxA19IuUIW-qIsNiwnVUTsScvJPVNaxezSnlF2Q-O2hlSVneHer9CmqEfVESJTefx0Ns9RuRul9Q3isaAfo0Whx0M5SfboKkRXzVMeerMbHGPc5Ayo6RicN6IUtbL7"
    private val contentType = "application/json"
    val TAG = "NOTIFICATION TAG"
    private var requestQueue: RequestQueue? = null

    var NOTIFICATION_TITLE: String? = null
    var NOTIFICATION_MESSAGE: String? = null
    var TOPIC: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestQueue = getRequestQueue()
        btnSend = findViewById(R.id.btnSend)
//        Company LoggedIn
        btnSend?.visibility = View.GONE
        btnSend?.setOnClickListener {
            sendNotification()
        }
    }

    private fun sendNotification() {
        TOPIC = "/topics/userABC" //topic must match with what the receiver subscribed to

        NOTIFICATION_TITLE = "Please"
        NOTIFICATION_MESSAGE = "Come In"

        val notification = JSONObject()
        val notifcationBody = JSONObject()
        try {
            notifcationBody.put("title", NOTIFICATION_TITLE)
            notifcationBody.put("message", NOTIFICATION_MESSAGE)
            notification.put("to", TOPIC)
            notification.put("data", notifcationBody)
        } catch (e: JSONException) {
            Log.e(TAG, "onCreate: " + e.message)
        }
        sendNotification1(notification)
    }

    private fun sendNotification1(notification: JSONObject) {
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(FCM_API, notification,
            Response.Listener<JSONObject?> {
                Log.i(TAG, "onResponse: $it")
            },
            Response.ErrorListener {
                Toast.makeText(this@MainActivity, "Request error", Toast.LENGTH_LONG).show()
                Log.i(TAG, "onErrorResponse: Didn't work")
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = serverKey
                params["Content-Type"] = contentType
                return params
            }
        }
        getRequestQueue()!!.add(jsonObjectRequest)
    }

    private fun getRequestQueue(): RequestQueue? {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(applicationContext)
        }
        return requestQueue
    }
}