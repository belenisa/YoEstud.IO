package com.example.yoestudio.ui.Pantallas

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.util.Patterns
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.yoestudio.ViewModel.UsuarioView
import android.widget.Toast
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearUsuarioPantalla(
    navController: NavController,
    viewModel: UsuarioView,
    drawerState: DrawerState,
    scope: CoroutineScope
) {

    val context = LocalContext.current
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    val correoValido = correo.isNotBlank() &&
            Patterns.EMAIL_ADDRESS.matcher(correo).matches()
    var password by remember { mutableStateOf("") }
    var aceptar by remember { mutableStateOf(false) }

    val formularioValido =
        nombre.isNotBlank() &&
                correoValido &&
                password.isNotBlank() &&
                aceptar

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Usuario") },
                navigationIcon = {
                    IconButton(onClick = {
                        scope.launch { drawerState.open() }
                    }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Spacer(modifier = Modifier.height(40.dp))

            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(140.dp), // un poco más grande
                tint = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(0.9f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
            {
                Column(modifier = Modifier.padding(20.dp)) {

                    // 🧑 Nombre
                    Text("Nombre de Usuario")
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    //  Correo
                    Text("Correo Electronico")
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = correo,
                        onValueChange = { correo = it },
                        modifier = Modifier.fillMaxWidth(),
                        isError = correo.isNotEmpty() && !correoValido,
                        supportingText = {
                            if (correo.isNotEmpty() && !correoValido) {
                                Text("Correo no válido")
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 🔒 Password
                    Text("Contraseña")
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // ✅ Checkbox
                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Checkbox(
                            checked = aceptar,
                            onCheckedChange = { aceptar = it }
                        )

                        Column {
                            Text("Aceptar Términos y condiciones")
                            Text(
                                "Leer los Términos y condiciones",
                                color = Color.Blue,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))



                    Button(
                        onClick = {

                            viewModel.crearUsuarioManual(
                                context = context,
                                nombre = nombre,
                                email = correo,
                                password = password,
                                onSuccess = {
                                    Toast.makeText(context, "Usuario creado", Toast.LENGTH_SHORT)
                                        .show()
                                    navController.navigate("inicio")
                                },
                                onError = {
                                    Toast.makeText(
                                        context,
                                        "Error al crear usuario",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )

                        },
                        enabled = formularioValido,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (formularioValido)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text("Registrarse", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        }
    }
}