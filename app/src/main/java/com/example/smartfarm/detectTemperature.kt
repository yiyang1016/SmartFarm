package com.example.smartfarm

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_detect_temperature.*
import kotlinx.android.synthetic.main.activity_plantmonitor.*
import kotlinx.android.synthetic.main.activity_plantmonitor.msstxt
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.LocalDate
import java.time.LocalTime
import java.time.Month
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.concurrent.fixedRateTimer
import kotlin.concurrent.scheduleAtFixedRate

class detectTemperature : AppCompatActivity() {
    private var handler: Handler? = null
    private var runnable: Runnable? = null
    private val EVERY_TEN_SECOND: Long = 10000

    private val swtOnOff = findViewById<Switch>(R.id.temSwitch)

    private var sensorTem: Double = 0.0
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("PI_06_CONTROL")
    private var buzzerStatus : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detect_temperature)

        val actionBar: ActionBar? = supportActionBar
        actionBar!!.title = "Temperature"

        var handler: Handler? = null
        var runnable: Runnable? = null

        executeHandler()
        getLatestSensorValue()

        val tembtn = findViewById<Button>(R.id.tembtn)
        tembtn.setOnClickListener{
            getLatestSensorValue()
        }


    }

    private fun executeHandler() {
        //If the handler and runnable are null we create it the first time.
        if (handler == null && runnable == null) {
            handler = Handler()
            runnable = object : Runnable {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun run() {
                    //Updating firebase store/getting
                    getLatestSensorValue()
                    //And we execute it again
                    handler!!.postDelayed(this, EVERY_TEN_SECOND)
                }
            }
        } else {
            handler?.postDelayed(runnable, EVERY_TEN_SECOND)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getLatestSensorValue() { // get and store to global variable sensors.
        val currentDateTime = LocalDateTime.now().minusSeconds(5)
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val hourFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH")
        val minSecFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("mmss")
        val dateText: String = currentDateTime.format(dateFormatter)
        val hourText: String = currentDateTime.format(hourFormatter)
        val minSecText: String = currentDateTime.format(minSecFormatter).substring(0..2) + "0"

        val dbRef = database.reference
        val lastQuery: Query = dbRef.child("PI_05_$dateText").child(hourText).child(minSecText)
        lastQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try {
                    if (dataSnapshot.exists()) {
                        if (dataSnapshot.child("tempe").value.toString().isNullOrEmpty()) {
                            sensorTem = (dataSnapshot.child("tempe").value.toString()).toDouble()
                        }
                        buzzerStatus = dataSnapshot.child("buzzer").value == 1
                    }
                    textView.text = dataSnapshot.child("tempe").value.toString() + " °C"
                    txtDate.text = "Date = " + dateText + hourText + minSecText
                    verifyTemperature()
                } catch (e: Exception) {
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors.
                Toast.makeText(applicationContext, "ERROR MTFK", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun OnBuzzer(){
        myRef.child("buzzer").setValue("1")
    }

    private fun OffBuzzer(){
        myRef.child("buzzer").setValue("0")
    }


    private fun verifyTemperature() {
        if (swtOnOff.isChecked) {
            if(buzzerStatus) {
                if (sensorTem > 39.0) {
                    msstxt.text = "It's too Hot!!!, Buzzer is on"
                    OnBuzzer()
                } else if (sensorTem < 19.0) {
                    msstxt.text = "It's too Cold!!!, Buzzer is on"
                    OnBuzzer()
                } else {
                    msstxt.text = "The condition is good."
                }
            }
        } else {
            msstxt.text = "The auto checking system is off."
            if(buzzerStatus) {
                myRef.child("buzzer").setValue("0")
            }
        }
    }
}