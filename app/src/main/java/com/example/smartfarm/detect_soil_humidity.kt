package com.example.smartfarm

import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_detect_soil_humidity.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class detect_soil_humidity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference
    private lateinit var  statelistener : ValueEventListener

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detect_soil_humidity)

        val actionBar = supportActionBar
        actionBar!!.title = "Soil Humidity"

        // variable
        var humidityDetect : String = ""
        var humidityPercentage : Int = humidityDetect.toInt()
        var led : String = ""
        var ledOn : Int = led.toInt()

        val addWater =findViewById<RadioGroup>(radGroup1.id)

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

            //Find Firebases' file location
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
                        if(!p0.child("humid").value.toString().isNullOrEmpty()){
                            humidityDetect = p0.child("humid").value.toString()

                            text.setText(" "+p0.child("humid").value.toString()+"%")
                            text2.text = "Ultra 2 = " + p0.child("temp").value.toString()
                            textDateTime.text = "Date = " + dateText+hourText+minSecText

                            if(humidityDetect.toInt()<6){
                                textRecommendation.text = "Your soil is too try , recommend to ADD WATER into your soil!"}
                                else{
                                textRecommendation.text="Your soil is wet and in good form!"
                            }

                        }
                    }

                }
            })
        }
// radiogroup function


radGroup1.setOnCheckedChangeListener(
    RadioGroup.OnCheckedChangeListener { group, checkedId ->
        var radio1: String = "rad1"
        var radio2: String = "rad2"
        var radio3: String = "rad3"
        var radio4: String = "rad4"

        val radio : RadioButton = findViewById(checkedId )
       Toast.makeText(applicationContext,"Watering for :${radio.text}",
       Toast.LENGTH_SHORT ).show()

        if(radio.id.equals( radio1 ) ){

            var map2 = mutableMapOf<String,Any>()

            object : CountDownTimer(300000, 1000) {
                override fun onTick(millisUntilFinished: Long) {

                    map2["led"] = "1"
                    mDatabase.child("PI_06_CONTROL")
                        .updateChildren(map2)
                }

                override fun onFinish() {
                    Toast.makeText(applicationContext,"Watering done",
                        Toast.LENGTH_SHORT ).show()

                    map2["led"] = "0"
                    mDatabase.child("PI_06_CONTROL")
                        .updateChildren(map2)
                }
            }.start()

        }
        if(radio.id.equals( radio2 ) ){
            var map2 = mutableMapOf<String,Any>()

            object : CountDownTimer(600000, 1000) {
                override fun onTick(millisUntilFinished: Long) {

                    map2["led"] = "1"
                    mDatabase.child("PI_06_CONTROL")
                        .updateChildren(map2)
                }

                override fun onFinish() {
                    Toast.makeText(applicationContext,"Watering done",
                        Toast.LENGTH_SHORT ).show()

                    map2["led"] = "0"
                    mDatabase.child("PI_06_CONTROL")
                        .updateChildren(map2)
                }
            }.start()
        }
        if(radio.id.equals( radio3 ) ){
            var map2 = mutableMapOf<String,Any>()

            object : CountDownTimer(900000, 1000) {
                override fun onTick(millisUntilFinished: Long) {

                    map2["led"] = "1"
                    mDatabase.child("PI_06_CONTROL")
                        .updateChildren(map2)
                }

                override fun onFinish() {
                    Toast.makeText(applicationContext,"Watering done",
                        Toast.LENGTH_SHORT ).show()

                    map2["led"] = "0"
                    mDatabase.child("PI_06_CONTROL")
                        .updateChildren(map2)
                }
            }.start()
        }
        if(radio.id.equals( radio4 ) ){
            var map2 = mutableMapOf<String,Any>()

            object : CountDownTimer(1200000, 1000) {
                override fun onTick(millisUntilFinished: Long) {

                    map2["led"] = "1"
                    mDatabase.child("PI_06_CONTROL")
                        .updateChildren(map2)
                }

                override fun onFinish() {
                    Toast.makeText(applicationContext,"Watering done",
                        Toast.LENGTH_SHORT ).show()

                    map2["led"] = "0"
                    mDatabase.child("PI_06_CONTROL")
                        .updateChildren(map2)
                }
            }.start()
        }

    }
)



    }
}