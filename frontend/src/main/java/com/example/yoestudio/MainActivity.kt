package com.example.yoestudio

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.yoestudio.Global.BotonAsistente
import com.example.yoestudio.Global.MenuLateral
import com.example.yoestudio.ui.theme.AppNavigation

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()

            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    MenuLateral(
                        navController = navController,
                        drawerState = drawerState,
                        scope = scope
                    )
                }
            ) {

                Scaffold(
                    floatingActionButton = {
                        BotonAsistente(navController = navController)
                    }
                ) { paddingValues ->

                    Box(modifier = Modifier.padding(paddingValues)) {

                        
                        AppNavigation(
                            navController = navController,
                            drawerState = drawerState,
                            scope = scope
                        )
                    }
                }
            }
        }
    }
}