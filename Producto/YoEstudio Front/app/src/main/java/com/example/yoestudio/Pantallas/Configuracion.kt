package com.example.yoestudio.Pantallas

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.yoestudio.R

class Configuracion : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.configuracion_act)

        // 1️⃣ Pedimos las apps al celular
        val pm = packageManager
        val apps = pm.getInstalledApplications(0)

        // 2️⃣ Guardamos solo los nombres
        val nombresApps = ArrayList<String>()
        for (app in apps) {
            val nombre = pm.getApplicationLabel(app).toString()
            nombresApps.add(nombre)
        }

        // 3️⃣ Mostramos la lista
        val lista = findViewById<ListView>(R.id.listaApps)
        val adaptador = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            nombresApps
        )
        lista.adapter = adaptador
    }
}
