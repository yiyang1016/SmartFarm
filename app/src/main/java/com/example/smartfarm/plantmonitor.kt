package com.example.smartfarm

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

class plantmonitor : AppCompatActivity() {
    //private val TAG ="ProfileActivity"
    //private lateinit var mUser : User
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference
    private lateinit var  statelistener : ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plantmonitor)

        val actionBar = supportActionBar
        actionBar!!.title = "Farm Security"

        //local variable declaration
        var detectdistance : String = ""
        var convertDistance : Int = detectdistance.toInt()

        var buzzer : String = ""
        var buzzerStatus : Int = buzzer.toInt()

        val currentDate: String = "PI_06_"+SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val currentHour : String = SimpleDateFormat("HH",Locale.getDefault()).format(Date())
        val currentMinuteSecond: String = SimpleDateFormat("mmss", Locale.getDefault()).format(Date())

        mDatabase = Firebase.database.reference

        val closeAlarm = findViewById<Button>(R.id.btnAlarm)
        //close the alarm
        fun readData(){
            statelistener = mDatabase
                .child("bait2123-202006-06")
                .child(currentDate)//get year Ex. PI_06_20200904
                .child(currentHour)//get hour Ex. 01
                .child(currentMinuteSecond)//get hour Ex. 1010
                .addValueEventListener(object : ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {
                    }
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var post = snapshot.value as Map<String,Any>
                        distance.text = detectdistance
                        detectdistance = post["ultra2"].toString()
                        buzzer = post["buzzer"].toString()
                    }
                })
        }

        fun verifyDistance(){
            if(convertDistance < 30.0 ){
                var map1 = mutableMapOf<String,Any>()
                map1["buzzer"] = "1"
                var map2 = mutableMapOf<String,Any>()
                map2["led"] = "1"

                FirebaseDatabase.getInstance().reference
                    .child("bait2123-202006-06")
                    .child("PI_06_CONTROL")
                    .updateChildren(map1)

                FirebaseDatabase.getInstance().reference
                    .child("bait2123-202006-06")
                    .child("PI_06_CONTROL")
                    .updateChildren(map2)
            }
        }


        //Button Function
        //Security Status Switch
        swtAlertMode.setOnCheckedChangeListener{
            buttonView, isChecked ->
            if(isChecked) {
                //Start read data from firebase
                readData()
                verifyDistance()
            }else{
                //Stop retrieving data from firebase
                mDatabase.removeEventListener(statelistener)
            }
        }

        //Close Alarm
        closeAlarm.setOnClickListener{
            readData()
            if(buzzerStatus == 0){
                val text = "Alarm not activate"
                Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
            }else{
                var map1 = mutableMapOf<String,Any>()
                map1["buzzer"] = "0"
                var map2 = mutableMapOf<String,Any>()
                map2["led"] = "0"
                var map3 = mutableMapOf<String,Any>()
                map3["lcdtext"] = " "

                FirebaseDatabase.getInstance().reference
                    .child("bait2123-202006-06")
                    .child("PI_06_CONTROL")
                    .updateChildren(map1)

                FirebaseDatabase.getInstance().reference
                    .child("bait2123-202006-06")
                    .child("PI_06_CONTROL")
                    .updateChildren(map2)

                FirebaseDatabase.getInstance().reference
                    .child("bait2123-202006-06")
                    .child("PI_06_CONTROL")
                    .updateChildren(map3)

                val text = "Alarm Closed Successfully"
                Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
            }
        }
    }
}