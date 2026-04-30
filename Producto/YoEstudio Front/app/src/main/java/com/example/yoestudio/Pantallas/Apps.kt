package com.example.yoestudio.Pantallas

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.yoestudio.R

class Apps(private val apps: List<ApplicationInfo>,
           private val pm: PackageManager
) : BaseAdapter() {

    override fun getCount(): Int = apps.size

    override fun getItem(position: Int): Any = apps[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(parent.context)
            .inflate(R.layout.item, parent, false)

        val app = apps[position]

        val icon = view.findViewById<ImageView>(R.id.iconApp)
        val name = view.findViewById<TextView>(R.id.nameApp)

        // ✅ Nombre de la app
        name.text = pm.getApplicationLabel(app).toString()

        // ✅ Ícono real de la app
        icon.setImageDrawable(pm.getApplicationIcon(app))

        return view
    }
}
