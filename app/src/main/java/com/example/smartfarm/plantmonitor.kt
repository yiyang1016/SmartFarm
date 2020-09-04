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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plantmonitor)

        val actionBar = supportActionBar
        actionBar!!.title = "Farm Security"

        //local variable declaration


        //val currentDate: String = "PI_06_"+SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        //val currentHour : String = SimpleDateFormat("HH",Locale.getDefault()).format(Date())
        //val currentMinuteSecond: String = SimpleDateFormat("mmss", Locale.getDefault()).format(Date())

        val currentDateTime = LocalDateTime.now()
        val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val hourFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("HH")
        val minFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("mm")
        val minSecFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("mmss")

        val dateText = currentDateTime.format(dateFormat)
        val hourText = currentDateTime.format(hourFormat)
        val minText = currentDateTime.format(minFormat)
        val minSecText = currentDateTime.format(minSecFormat).substring(0..2)+0
        val findDate = "PI_06_"+dateText
        mDatabase = Firebase.database.reference

        val closeAlarm = findViewById<Button>(R.id.btnAlarm)
        //close the alarm

        fun readData(){
            val ref = mDatabase
                .child(findDate)//get year Ex. PI_06_20200904
                .child(hourText)//get hour Ex. 01
                .child(minSecText)//get hour Ex. 1010
            ref.addValueEventListener(object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                    }
                override fun onDataChange(p0: DataSnapshot) {
                    distance.text = "Sound = "+p0.child("sound").value.toString()
                    Ultra2.text = "Ultra 2 = " + p0.child("ultra2").value.toString()
                    ultra.text = "Date = " + dateText+hourText+minSecText

                    //detectdistance = post["ultra2"].toString()
                    //buzzer = post["buzzer"].toString()
                }
            })
        }
        //Button Function
        //Security Status Switch
        swtAlertMode.setOnCheckedChangeListener{
                buttonView, isChecked ->
            if(isChecked) {
                //Start read data from firebase
                readData()
                //verifyDistance()
                
            }else{
                //Stop retrieving data from firebase
                mDatabase.removeEventListener(statelistener)
                val text = "Alarm Closed Successfully"
                Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()

            }
        }


    }
}