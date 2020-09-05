package com.example.smartfarm

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_plantmonitor.*
import java.text.SimpleDateFormat
//import com.google.firebase.quickstart.database.databinding.ActivityNewPostBinding
//import com.google.firebase.quickstart.database.kotlin.models.Post
//import com.google.firebase.quickstart.database.kotlin.models.User
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.concurrent.fixedRateTimer
import kotlin.concurrent.scheduleAtFixedRate

class plantmonitor : AppCompatActivity() {
    //private val TAG ="ProfileActivity"
    //private lateinit var mUser : User
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference
    private lateinit var  statelistener : ValueEventListener

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plantmonitor)

        val actionBar = supportActionBar
        actionBar!!.title = "Farm Security"

        //local variable declaration
        var detectdistance : String = ""
        var detectSound : String = ""

        val closeAlarm = findViewById<Button>(R.id.btnAlarm)
        mDatabase = Firebase.database.reference

        fun verifyDistanceandSound(){
            var convertDistance : Double = detectdistance.toDouble()
            var convertSound : Int = detectSound.toInt()
            if(convertDistance < 30.0 || convertSound > 508){
                msstxt.text = "Intruder!!!"//508 = 60db(Sound)
                msstxt.setTextColor(Color.parseColor("#FF0000"))
            }else{
                msstxt.text = "Still Safe"
                msstxt.setTextColor(Color.parseColor("#7CFC00"))
            }
        }

        //retrieve sensor data
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
                        if(!p0.child("sound").value.toString().isNullOrEmpty()){
                            detectdistance = p0.child("ultra2").value.toString()
                            detectSound = p0.child("sound").value.toString()

                            verifyDistanceandSound()
                            distance.setText("Sound = "+p0.child("sound").value.toString())
                            Ultra2.text = "Ultra 2 = " + p0.child("ultra2").value.toString()
                            ultra.text = "Date = " + dateText+hourText+minSecText
                        }
                    }
                }
            })
        }



        //Button Function
        //Security Status Switch
        val timer = Timer("schedule", true)
        var counter: Int = 0
        swtAlertMode.setOnCheckedChangeListener{
                buttonView, isChecked ->
            if(isChecked) {
                //Start read data from firebase
                if(counter == 0){
                    timer.scheduleAtFixedRate(1000,1000){
                        readData()

                    }

                }else{
                    super.onResume()
                }
                //verifyDistance()
                btnAlarm.visibility = View.VISIBLE
            }else{
                //Stop retrieving data from firebase
                //timer.cancel()
                super.onPause()
                counter == 1
                //mDatabase.removeEventListener(statelistener)
                val text = "Alarm Closed Successfully"
                Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
            }
        }


        //Close Alarm
        closeAlarm.setOnClickListener{
            var buzzerStatus : String = ""
            //retrieve buzzer data
            val ref = mDatabase
                .child("PI_06_CONTROL")//get year Ex. PI_06_CONTROL
            ref.addValueEventListener(object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                    val text = "Connection Failed"
                    Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
                }
                override fun onDataChange(p0: DataSnapshot) {
                    if(p0.exists()){
                        if(!p0.child("buzzer").value.toString().isNullOrEmpty()){
                            buzzerStatus = p0.child("buzzer").value.toString()
                            buzzerStatustxt.text = "Buzzer is on"
                        }
                    }
                }
            })
            //verify buzzer status
            if(buzzerStatus.equals("0")){
                val text = "Alarm not activate"
                Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
            }else{
                var map1 = mutableMapOf<String,Any>()
                map1["buzzer"] = "0"
                var map2 = mutableMapOf<String,Any>()
                map2["led"] = "0"

                mDatabase.child("PI_06_CONTROL")
                    .updateChildren(map1)
                mDatabase.child("PI_06_CONTROL")
                    .updateChildren(map2)
                val text = "Alarm Closed Successfully"
                Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
            }
        }


    }
}