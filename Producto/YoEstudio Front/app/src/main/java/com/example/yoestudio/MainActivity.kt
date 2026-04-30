package com.example.yoestudio

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.yoestudio.Pantallas.Configuracion


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(android.R.layout.simple_list_item_1)
        startActivity(Intent(this, Configuracion::class.java))
        finish()
    }
}