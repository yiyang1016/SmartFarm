package com.example.smartfarm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.btnsecurity)
        button.setOnClickListener{
        val intent = Intent(this, plantmonitor::class.java)
        startActivity(intent)
        }

        val btntemperature = findViewById<Button>(R.id.btntemperature)
        btntemperature.setOnClickListener{
            val intent = Intent(this, detectTemperature::class.java)
            startActivity(intent)
        }
        val btnhumidity = findViewById<Button>(R.id.btnhumidity)
        btnhumidity.setOnClickListener{
            val intent = Intent(this, detect_soil_humidity::class.java)
            startActivity(intent)
        }
    }
}
