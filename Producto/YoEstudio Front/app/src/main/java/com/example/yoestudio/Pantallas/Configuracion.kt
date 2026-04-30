package com.example.yoestudio.Pantallas

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.yoestudio.R

class Configuracion : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.configuracion_act)

        val pm = packageManager
        val apps = pm.getInstalledApplications(0)

        val lista = findViewById<ListView>(R.id.listaApps)
        lista.adapter = Apps(apps, pm)
    }

}
