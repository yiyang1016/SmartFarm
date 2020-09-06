package com.example.smartfarm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
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
//import com.google.firebase.referencecode.storage.R
//import com.google.firebase.storage.ktx.storage
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.squareup.picasso.Picasso
import org.jetbrains.anko.startActivity
import kotlin.concurrent.schedule

class plantmonitor : AppCompatActivity() {
    //private lateinit var mUser : User
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference
    private lateinit var  statelistener : ValueEventListener
    private lateinit var mDatabase1: DatabaseReference
    internal lateinit var img_1 : ImageView
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel : NotificationChannel
    lateinit var  builder: Notification.Builder
    val channelId="com.example.smartfarm"
    val description= "Intruder Founded"
    @RequiresApi(Build.VERSION_CODES.O)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plantmonitor)

        val actionBar = supportActionBar
        actionBar!!.title = "Farm Security"
        //set Action Bar Color
        val window: Window = this@plantmonitor.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this@plantmonitor, R.color.actionBarColor)

        img_1 = findViewById<View>(R.id.myImageView) as ImageView
        var findDate: String = ""
        var fullDate: String = ""
        var fullMinSec:String = ""
        var counter: Int = 0
        fun getImage(){
            val currentDateTime = LocalDateTime.now()
            val yearMonthFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMM")
            val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("dd")
            val hourFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("HH")
            val minFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("mm")
            val minSecFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("mmss")

            //Change Date Format
            val yearMonthText :String = currentDateTime.format(yearMonthFormat)
            val dateText = currentDateTime.format(dateFormat)
            val hourText = currentDateTime.format(hourFormat)
            val minText = currentDateTime.format(minFormat)
            val minSecText = currentDateTime.format(minSecFormat).substring(0..2)+0

            findDate = yearMonthText+"-"+dateText
            fullDate = yearMonthText+dateText+hourText
            fullMinSec = minSecText
        }
        //getImage()
        //local variable declaration
        var detectdistance : String = ""
        var detectSound : String = ""

        val closeAlarm = findViewById<Button>(R.id.btnAlarm)
        mDatabase = Firebase.database.reference
        val intent= Intent(applicationContext, plantmonitor::class.java)
        val pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT)
        fun verifyDistanceandSound(){
            if (swtAlertMode.isChecked == true){
                var convertDistance : Double = detectdistance.toDouble()
                var convertSound : Int = detectSound.toInt()
                if(convertDistance < 30.0 || convertSound > 508){
                    msstxt.text = "Intruder!!!"//508 = 60db(Sound)
                    msstxt.setTextColor(Color.parseColor("#FF0000"))
                    msstxt.setTextSize(60F)
                    //Open buzzer and led
                    var map1 = mutableMapOf<String,Any>()
                    map1["buzzer"] = "1"
                    var map2 = mutableMapOf<String,Any>()
                    map2["led"] = "1"
                    var map3 = mutableMapOf<String,Any>()
                    map3["lcd"] = "1"
                    var map4 = mutableMapOf<String,Any>()
                    map4["lcdtext"] = "Intruder Founded"


                    mDatabase.child("PI_06_CONTROL")
                        .updateChildren(map1)
                    mDatabase.child("PI_06_CONTROL")
                        .updateChildren(map2)
                    mDatabase.child("PI_06_CONTROL")
                        .updateChildren(map3)
                    mDatabase.child("PI_06_CONTROL")
                        .updateChildren(map4)

                    val currentDateTime = LocalDateTime.now()
                    val yearMonthFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMM")
                    val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("dd")
                    val hourFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("HH")
                    val minFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("mm")
                    val minSecFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("mmss")

                    //Change Date Format
                    val yearMonthText :String = currentDateTime.format(yearMonthFormat)
                    val dateText = currentDateTime.format(dateFormat)
                    val hourText = currentDateTime.format(hourFormat)
                    val minText = currentDateTime.format(minFormat)
                    val minSecText = currentDateTime.format(minSecFormat).substring(0..2)+0


                        findDate = yearMonthText+"-"+dateText
                        fullDate = yearMonthText+dateText+hourText+minSecText
                        imageDate.text = "Last intruder detected on : "+findDate


                    buzzerStatustxt.text = "Buzzer is On"
                    //Timer("SettingUp", false).schedule(5000) {
                    //   val url_1="https://firebasestorage.googleapis.com/v0/b/bait2123-"+findDate+".appspot.com/o/PI_06_CONTROL%2Fcam_"+fullDate+".jpg?alt=media"
                    //   Picasso.get().load(url_1).into(img_1)
                    //}

                    //Timer("SettingUp", false).schedule(10000) {
                    //    var map6 = mutableMapOf<String,Any>()
                    //    map6["camera"] = "0"
                    //    mDatabase.child("PI_06_CONTROL")
                    //        .updateChildren(map6)

                    //}
                    if(counter==0){

                        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)as NotificationManager
                        notificationChannel= NotificationChannel(channelId,description, NotificationManager.IMPORTANCE_HIGH)
                        notificationChannel.lightColor=Color.RED
                        notificationChannel.enableVibration(true)
                        notificationManager.createNotificationChannel(notificationChannel)
                        builder= Notification.Builder(this,channelId)
                            .setContentTitle("Intruder Founded!!! Please Check Your Farm")
                            .setSmallIcon(R.mipmap.icon_round)
                            .setContentIntent(pendingIntent)
                        notificationManager.notify(0,builder.build())
                        counter +1
                        val text = "Alarm Activated"
                        Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
                    }
                }else{
                    msstxt.text = "Still Safe"
                    msstxt.setTextColor(Color.parseColor("#7CFC00"))
                    msstxt.setTextSize(60F)
                }
            }else{
                counter = 0
                msstxt.text = "Please Open To Secure the Farms"
                msstxt.setTextColor(Color.parseColor("#808080"))
                msstxt.setTextSize(20F)
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
                            //distance.setText("Sound = "+p0.child("sound").value.toString()+" dB")
                            //Ultra2.text = "Ultra 2 = " + p0.child("ultra2").value.toString()
                            //ultra.text = "Date = " + dateText+hourText+minSecText
                        }
                    }
                }
            })
        }

        //Button Function
        //Security Status Switch
        var timer = Timer("schedule", true)
        timer.scheduleAtFixedRate(1000,1000) {
            readData()
        }
        swtAlertMode.setOnCheckedChangeListener{
                buttonView, isChecked ->
            if(isChecked) {
                val text = "Alarm is On"
                Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
            }else{
                val text = "Alarm Off Successfully"
                Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
                var map1 = mutableMapOf<String,Any>()
                map1["camera"] = "0"
                mDatabase.child("PI_06_CONTROL")
                    .updateChildren(map1)
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

                        }
                    }
                }
            })
            //verify buzzer status
            if(buzzerStatus.equals("0")){
                val text = "Alarm not activate"
                Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
            }else{
                //close buzzer and led
                var map1 = mutableMapOf<String,Any>()
                map1["buzzer"] = "0"
                var map2 = mutableMapOf<String,Any>()
                map2["led"] = "0"
                var map3 = mutableMapOf<String,Any>()
                map2["lcd"] = "0"
                var map4 = mutableMapOf<String,Any>()
                map2["lcdtext"] = ""
                var map5 = mutableMapOf<String,Any>()
                map2["camera"] = "0"
                mDatabase.child("PI_06_CONTROL")
                    .updateChildren(map1)
                mDatabase.child("PI_06_CONTROL")
                    .updateChildren(map2)
                mDatabase.child("PI_06_CONTROL")
                    .updateChildren(map3)
                mDatabase.child("PI_06_CONTROL")
                    .updateChildren(map4)
                mDatabase.child("PI_06_CONTROL")
                    .updateChildren(map5)
                buzzerStatustxt.text = "Buzzer is off"
                val text = "Alarm Closed Successfully"
                Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
                counter = 0
            }
        }
    }
}