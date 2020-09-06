package com.example.smartfarm

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_checkweather.*
import kotlinx.android.synthetic.main.activity_detect_soil_humidity.*
//import com.google.firebase.quickstart.database.databinding.ActivityNewPostBinding
//import com.google.firebase.quickstart.database.kotlin.models.Post
//import com.google.firebase.quickstart.database.kotlin.models.User
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

class checkweather : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference
    private lateinit var statelistener: ValueEventListener

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkweather)

        val actionBar = supportActionBar
        actionBar!!.title = "Farm Weather"

        //local variable declaration
        var detectlight : String = ""

        fun verifyBrigntness(){
            var convertBrightness : Int = detectlight.toInt()
            var mapBright = mutableMapOf<String, Any>()
            if(convertBrightness < 1 || swtBrightness.isChecked) {
                mapBright["led"] = "1"
                txtW_LED.text = "Lightbulb On"
                txtW_LED.setTextColor(Color.WHITE)
                lblbrightness.setTextColor(Color.WHITE)
                swtBrightness.setTextColor(Color.WHITE)
                txtbrightness.setTextColor(Color.WHITE)
                imageView.setImageResource(R.drawable.weather_background3)
                mDatabase.child("PI_06_CONTROL")
                    .updateChildren(mapBright)
            }else if(convertBrightness > 0 || !swtBrightness.isChecked){
                mapBright["led"] = "0"
                txtW_LED.text="Lightbulb Off"
                txtW_LED.setTextColor(Color.BLACK)
                lblbrightness.setTextColor(Color.BLACK)
                swtBrightness.setTextColor(Color.BLACK)
                txtbrightness.setTextColor(Color.BLACK)
                imageView.setImageResource(R.drawable.weather_background2)
                mDatabase.child("PI_06_CONTROL")
                    .updateChildren(mapBright)
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
            mDatabase = Firebase.database.reference

            //Find Firebase file location
            val ref = mDatabase
                .child(findDate)
                .child(hourText)
                .child(minSecText)
            ref.addValueEventListener(object:ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    val text = "Connection Failed"
                    Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
                }
                override fun onDataChange(snapshot: DataSnapshot){
                    if(snapshot.exists()){
                        if(!snapshot.child("tempe").value.toString().isNullOrEmpty()){
                            detectlight=snapshot.child("light").value.toString()
                            txtbrightness.text = snapshot.child("light").value.toString()+" Lux"
                            verifyBrigntness()
                        }
                    }
                }
            })
        }
        var timer = Timer("schedule", true)
        timer.scheduleAtFixedRate(1000, 1000) {
            readData()
        }
    }
}