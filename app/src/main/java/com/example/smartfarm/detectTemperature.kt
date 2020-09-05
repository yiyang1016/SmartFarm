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

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference
    private lateinit var  statelistener : ValueEventListener
    var detecttem : String = ""

    private var buzzerStatus : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detect_temperature)

        val actionBar: ActionBar? = supportActionBar
        actionBar!!.title = "Temperature"

        mDatabase = Firebase.database.reference

        val swtOnOff = findViewById<Switch>(R.id.temSwitch)


        fun OnBuzzer() {
            var map1 = mutableMapOf<String,Any>()
            map1["buzzer"] = "1"
            var map2 = mutableMapOf<String,Any>()
            map1["lcdtext"] = "Temperature :" + detecttem
            var map3 = mutableMapOf<String,Any>()
            map3["lcd"] = "1"
            mDatabase.child("PI_06_CONTROL")
                .updateChildren(map1)
            mDatabase.child("PI_06_CONTROL")
                .updateChildren(map2)
            mDatabase.child("PI_06_CONTROL")
                .updateChildren(map3)
        }

        fun OffBuzzer() {
            var map1 = mutableMapOf<String,Any>()
            map1["buzzer"] = "0"
            mDatabase.child("PI_06_CONTROL")
                .updateChildren(map1)
        }

        fun verifyTemperature() {
            if (swtOnOff.isChecked) {
                if (buzzerStatus) {
                    if (detecttem.toDouble() > 40.0) {
                        msstxt.text = "It's too Hot!!!, Buzzer is on"
                    } else if (detecttem.toDouble() < 19.0) {
                        msstxt.text = "It's too Cold!!!, Buzzer is on"
                    } else {
                        msstxt.text = "The condition is good."
                        OffBuzzer()
                    }
                }else{
                    if (detecttem.toDouble() > 40.0) {
                        msstxt.text = "It's too Hot!!!, Buzzer is on"
                        OnBuzzer()
                    } else if (detecttem.toDouble() < 19.0) {
                        msstxt.text = "It's too Cold!!!, Buzzer is on"
                        OnBuzzer()
                    } else {
                        msstxt.text = "The condition is good."
                    }
                }
            } else {
                msstxt.text = "The auto checking system is off."
                if (buzzerStatus) {
                    OffBuzzer()
                }
            }
        }

        fun readData(){
            //Get local Date and Time
            val currentDateTime = LocalDateTime.now()
            val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
            val hourFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("HH")
            val minFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("mm")
            val minSecFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("mmss")

            //Change Date Format
            val dateText = currentDateTime.format(dateFormat)
            val hourText = currentDateTime.format(hourFormat)
            val minText = currentDateTime.format(minFormat)
            val minSecText = currentDateTime.format(minSecFormat).substring(0..2)+0
            val findDate = "PI_06_"+dateText

            //Find Firebase's file location
            val ref = mDatabase
                .child(findDate)//get year Ex. PI_06_20200904
                .child(hourText)//get hour Ex. 01
                .child(minSecText)//get hour Ex. 1010
            ref.addValueEventListener(object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                    val text = "Connection Failed"
                    Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
                }
                override fun onDataChange(p0: DataSnapshot) {
                    if(p0.exists()){
                        if(!p0.child("tempe").value.toString().isNullOrEmpty()){
                            detecttem = p0.child("tempe").value.toString()

                            textView.text = p0.child("tempe").value.toString()+" °C"
                            txtDate.text = hourText + ":" +minSecText
                        }
                        verifyTemperature()
                    }
                }
            })
        }

        var timer = Timer("schedule", true)
        timer.scheduleAtFixedRate(1000, 1000) {
            readData()
        }

        val tembtn = findViewById<Button>(R.id.tembtn)
        tembtn.setOnClickListener {
            readData()
        }

    }
}