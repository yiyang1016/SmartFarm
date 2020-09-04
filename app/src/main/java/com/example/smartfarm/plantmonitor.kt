package com.example.smartfarm

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
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
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plantmonitor)

        val actionBar = supportActionBar
        actionBar!!.title = "Farm Security"
        mDatabase = Firebase.database.reference
        //fun currentUserPreference(): DatabaseReference =
        //    mDatabase.child("").child(mAuth.currentUser!!.)


        val button = findViewById<Button>(R.id.btnAlarm)
        button.setOnClickListener{
            mDatabase.child("PI_06_CONTROL")
                .child("buzzer").setValue("0")
            mDatabase.child("PI_06_CONTROL")
                .child("led").setValue("0")
            mDatabase.child("PI_06_CONTROL")
                .child("lcdtext").setValue("")
        }
        var detectdistance : String = ""
        var convertDistance : Int = detectdistance.toInt()
        val currentDate: String = "PI_06_"+SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val currentHour : String = SimpleDateFormat("HH",Locale.getDefault()).format(Date())
        val currentMinuteSecond: String = SimpleDateFormat("mmss", Locale.getDefault()).format(Date())
        fun readObserveData(){
            FirebaseDatabase.getInstance().reference
                .child("bait2123-202006-06")
                .child(currentDate)//get year Ex. PI_06_20200904
                .child(currentHour)//get hour Ex. 01
                .child(currentMinuteSecond)//get hour Ex. 1010
                .addValueEventListener(object : ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {
                    }
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var post = snapshot.value as Map<String,Any>
                        detectdistance = post["ultra2"].toString()
                        distance.text = detectdistance
                    }
                })
        }
        fun verifyDistance(){
            if(convertDistance < 30.0 ){
                mDatabase.child("PI_06_CONTROL")
                    .child("buzzer").setValue("1")
                mDatabase.child("PI_06_CONTROL")
                    .child("led").setValue("1")
                mDatabase.child("PI_06_CONTROL")
                    .child("lcdtext").setValue("WARNING!!!")
            }
        }
        readObserveData()   
    }
}