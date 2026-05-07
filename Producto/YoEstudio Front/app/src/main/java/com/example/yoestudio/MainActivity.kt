package com.example.yoestudio

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.yoestudio.Global.BotonAsistente
import com.example.yoestudio.Global.MenuLateral

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            Scaffold(
                floatingActionButton = {
                    BotonAsistente(navController = navController)
                }
            ) { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    MenuLateral(navController = navController)
                }
            }
        }
    }
}