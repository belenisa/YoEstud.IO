package com.example.yoestudio.Pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.yoestudio.Data.Network.PublicacionesDTOs
import com.example.yoestudio.Global.BotonIA_Circular
import com.example.yoestudio.Global.MenuLateral
import com.example.yoestudio.Utils.TokenManager
import com.example.yoestudio.ViewModel.ComentariosViewModel
import com.example.yoestudio.ViewModel.ConcentracionViewModel
import com.example.yoestudio.ViewModel.PublicacionesViewModel
import com.example.yoestudio.ui.theme.LocalThemeManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

import java.time.Duration
import java.time.LocalDateTime
import java.time.OffsetDateTime

data class ApuntePost(
    val autor: String,
    val carrera: String,
    val contenido: String,
    val ramo: String,
    val tiempo: String,
    val likes: Int,
    val comentarios: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Inicio(
    drawerState: DrawerState,
    scope: CoroutineScope,
    navController: NavHostController,
    viewModel: ConcentracionViewModel,
    publicacionesViewModel: PublicacionesViewModel
) {
    val estaActivo by viewModel.estaActivo.collectAsState()
    val tiempoRestante by viewModel.tiempoRestante.collectAsState()
    val context = LocalContext.current
    val publicaciones by publicacionesViewModel.publicaciones.collectAsState()
    val horaServidor by publicacionesViewModel
        .horaServidor
        .collectAsState()
    var nuevaPublicacion by remember { mutableStateOf("") }
    var hashtag by remember { mutableStateOf("") }
    var carrera by remember { mutableStateOf("") }
    val tokenManager = remember { TokenManager(context) }
    val nombreUsuario by tokenManager
        .getName
        .collectAsState(initial = "")
    val idUsuario by tokenManager
        .getUserId
        .collectAsState(initial = 0)

    val comentariosViewModel: ComentariosViewModel = viewModel()
    var publicacionComentariosAbiertos by remember {
        mutableStateOf<String?>(null)
    }
    val cantidadComentarios = remember {
        mutableStateMapOf<String, Int>()
    }

    val darkModeState = LocalThemeManager.current
    var expandido by remember {
        mutableStateOf(false)
    }

    val feedApuntes = remember {
        listOf(
            ApuntePost("Marcel Droguett", "Ingeniería Civil", "Acabo de subir el resumen completo de Cálculo II. ¡Espero les sirva!", "Cálculo II", "2h", 24, 5),
            ApuntePost("Belén Alarcón", "Diseño", "Guía de color y tipografía para el examen final. PDF disponible en mi perfil.", "Taller de Diseño", "4h", 42, 12),
            ApuntePost("Matías Sanhueza", "Psicología", "Mapa mental sobre las teorías de Freud. Muy útil para repasar rápido.", "Psicología General", "6h", 15, 3),
            ApuntePost("Benjamín Belmar", "Derecho", "Apuntes de Derecho Romano procesal. Viene con casos de ejemplo.", "Derecho Romano", "1d", 89, 21),
            ApuntePost("Estudiante 2026", "Medicina", "Resumen de anatomía: Sistema Nervioso Central.", "Anatomía I", "2d", 156, 45)
        )
    }

    LaunchedEffect(Unit) {
        publicacionesViewModel.cargarPublicaciones()
    }

    LaunchedEffect(comentariosViewModel.comentarios) {

        publicacionComentariosAbiertos?.let { id ->

            cantidadComentarios[id] =
                comentariosViewModel.comentarios.size
        }
    }

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
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                    title = {
                        Text("YOESTUD.IO", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onBackground, letterSpacing = 1.sp)
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground)
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { 
                                if (estaActivo) viewModel.cancelarEnfoque(context)
                                else navController.navigate("concentracion_setup")
                            }
                        ) {
                            Icon(
                                imageVector = if (estaActivo) Icons.Default.NotificationsActive else Icons.Default.Timer,
                                contentDescription = null,
                                tint = if (estaActivo) Color(0xFF00FF00) else MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
                )
            },
            floatingActionButton = {
                if (!estaActivo) {
                    BotonIA_Circular(navController)
                }
            }
        )
        { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                if (estaActivo) {
                    Surface(
                        color = Color(0xFF00FF00).copy(alpha = 0.1f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.cancelarEnfoque(context)
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.Timer,
                                contentDescription = null,
                                tint = Color(0xFF00FF00),
                                modifier = Modifier.size(16.dp)
                            )

                            Spacer(Modifier.width(8.dp))

                            Text(
                                "Modo Concentración: ${viewModel.formatearTiempo(tiempoRestante)} (Toca para parar)",
                                color = Color(0xFF00FF00),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }


                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    expandido = !expandido
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = "Crear publicación",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )

                            Icon(
                                imageVector = if (expandido)
                                    Icons.Default.ExpandLess
                                else
                                    Icons.Default.ExpandMore,
                                contentDescription = null
                            )
                        }

                        if (expandido) {

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = nuevaPublicacion,
                                onValueChange = {
                                    nuevaPublicacion = it
                                },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text("¿Qué estás estudiando hoy?")
                                },
                                minLines = 3
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = carrera,
                                onValueChange = {
                                    carrera = it
                                },
                                modifier = Modifier.fillMaxWidth(),
                                label = {
                                    Text("Carrera")
                                }
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = hashtag,
                                onValueChange = {
                                    hashtag = it
                                },
                                modifier = Modifier.fillMaxWidth(),
                                label = {
                                    Text("Hashtag")
                                },
                                placeholder = {
                                    Text("#Hashtag")
                                }
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {

                                OutlinedButton(
                                    onClick = {
                                    }
                                ) {

                                    Icon(
                                        Icons.Default.AttachFile,
                                        contentDescription = null
                                    )

                                    Spacer(Modifier.width(4.dp))

                                    Text("Documento")
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Button(
                                    onClick = {

                                        if (
                                            nuevaPublicacion.isNotBlank() &&
                                            carrera.isNotBlank()
                                        ) {

                                            publicacionesViewModel.crearPublicacion(
                                                PublicacionesDTOs(
                                                    id = "",
                                                    idUsuario = idUsuario ?: 0,
                                                    nombreUsuario = nombreUsuario ?: "Usuario",
                                                    carrera = carrera,
                                                    descripcion = nuevaPublicacion,
                                                    documento = null,
                                                    hashtag = hashtag,
                                                    fechaPublicacion = null,
                                                    likes = 0,
                                                    usuariosLikes = emptyList()
                                                )
                                            )

                                            nuevaPublicacion = ""
                                            carrera = ""
                                            hashtag = ""

                                            expandido = false

                                            publicacionesViewModel.cargarPublicaciones()
                                        }
                                    }
                                ) {

                                    Icon(
                                        Icons.Default.Send,
                                        contentDescription = null
                                    )

                                    Spacer(Modifier.width(4.dp))

                                    Text("Publicar")
                                }
                            }
                        }
                    }
                }


                    LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {

                    items(publicaciones) { publicacion ->

                        PostItem(
                            post = ApuntePost(
                                autor = publicacion.nombreUsuario,
                                carrera = publicacion.carrera,
                                contenido = publicacion.descripcion,
                                ramo = publicacion.hashtag ?: "General",
                                tiempo = calcularTiempo(
                                    publicacion.fechaPublicacion,
                                    horaServidor
                                ),
                                likes = publicacion.likes,

                                comentarios = cantidadComentarios[publicacion.id] ?: 0
                            ),
                            tieneLike = publicacion.usuariosLikes?.contains(
                                idUsuario ?: 0L
                            ) ?: false,

                            onLike = {
                                publicacionesViewModel.darLike(
                                    publicacion.id,
                                    idUsuario ?: 0L
                                )
                            },

                            onComentarios = {

                                if (publicacionComentariosAbiertos == publicacion.id) {

                                    publicacionComentariosAbiertos = null

                                } else {

                                    publicacion.id?.let { id ->

                                        publicacionComentariosAbiertos = id

                                        comentariosViewModel.cargarComentarios(id)
                                    }
                                }
                            },

                            comentariosAbiertos =
                                publicacionComentariosAbiertos == publicacion.id,

                            contenidoComentarios = {

                                var textoComentario by remember {
                                    mutableStateOf("")
                                }

                                Column {

                                    if (comentariosViewModel.comentarios.isEmpty()) {

                                        Text(
                                            text = "No hay comentarios todavía",
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )

                                    } else {

                                        comentariosViewModel.comentarios.forEach { comentario ->

                                            Card(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 4.dp),

                                                colors = CardDefaults.cardColors(
                                                    containerColor = MaterialTheme.colorScheme.surface
                                                )
                                            ) {

                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(12.dp),

                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.Top
                                                ) {

                                                    Column(
                                                        modifier = Modifier.weight(1f)
                                                    ) {

                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {

                                                            Text(
                                                                text = comentario.nombreUsuario ?: "Usuario",
                                                                fontWeight = FontWeight.Bold,
                                                                fontSize = 13.sp,
                                                                color = MaterialTheme.colorScheme.primary
                                                            )

                                                            Text(
                                                                text = " · ${
                                                                    calcularTiempo(
                                                                        comentario.fechaComentario,
                                                                        horaServidor
                                                                    )
                                                                }",
                                                                fontSize = 12.sp,
                                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                                            )
                                                        }

                                                        Spacer(modifier = Modifier.height(4.dp))

                                                        Text(
                                                            text = comentario.descripcion,
                                                            fontSize = 16.sp,
                                                            lineHeight = 22.sp,
                                                            color = MaterialTheme.colorScheme.onBackground
                                                        )
                                                    }

                                                    if (comentario.idUsuario == idUsuario) {

                                                        Box(
                                                            modifier = Modifier.clickable {

                                                                comentariosViewModel.eliminarComentario(
                                                                    comentario
                                                                )

                                                                cantidadComentarios[comentario.idPublicacion] =
                                                                    maxOf(
                                                                        0,
                                                                        (cantidadComentarios[comentario.idPublicacion] ?: 0) - 1
                                                                    )
                                                            }
                                                        ) {

                                                            ActionIcon(
                                                                Icons.Default.Delete,
                                                                ""
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = textoComentario,
                                        onValueChange = {
                                            textoComentario = it
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        placeholder = {
                                            Text("Escribe un comentario")
                                        }
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Button(
                                        onClick = {

                                            if (
                                                textoComentario.isNotBlank()
                                            ) {

                                                publicacion.id?.let { id ->

                                                    comentariosViewModel.agregarComentario(
                                                        idPublicacion = id,
                                                        idUsuario = idUsuario ?: 0L,
                                                        nombreUsuario = nombreUsuario ?: "Usuario",
                                                        descripcion = textoComentario
                                                    )

                                                    cantidadComentarios[id] =
                                                        (cantidadComentarios[id] ?: 0) + 1

                                                }

                                                textoComentario = ""
                                            }
                                        }
                                    ) {
                                        Text("Comentar")
                                    }
                                }
                            }
                        )

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f),
                            thickness = 0.5.dp
                        )
                    }

                    items(feedApuntes) { post ->
                        PostItem(post)

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f),
                            thickness = 0.5.dp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PostItem(post: ApuntePost,
        tieneLike: Boolean = false,
        onLike: () -> Unit = {},
        onComentarios: () -> Unit = {},
        comentariosAbiertos: Boolean = false,
        contenidoComentarios: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.7f), MaterialTheme.colorScheme.primary))),
            contentAlignment = Alignment.Center
        ) {
            Text(post.autor.take(1), color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(post.autor, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text("@${post.ramo.replace(" ", "").lowercase()}", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), fontSize = 14.sp)
                Text(" · ${post.tiempo}", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), fontSize = 14.sp)
            }
            
            Text(post.carrera, color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = post.contenido,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 15.sp,
                lineHeight = 20.sp
            )

            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = "#${post.ramo}",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Row(
                    modifier = Modifier.clickable {
                        onComentarios()
                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ActionIcon(
                        Icons.Outlined.ChatBubbleOutline,
                        post.comentarios.toString()
                    )
                }

                Row(
                    modifier = Modifier.clickable {
                        onLike()
                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = if (tieneLike)
                            Icons.Default.Favorite
                        else
                            Icons.Outlined.FavoriteBorder,

                        contentDescription = null,

                        tint = if (tieneLike)
                            Color.Red
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),

                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = post.likes.toString(),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        fontSize = 13.sp
                    )
                }

                ActionIcon(
                    Icons.Outlined.Share,
                    ""
                )
            }
            if (comentariosAbiertos) {

                Spacer(modifier = Modifier.height(8.dp))

                HorizontalDivider()

                Spacer(modifier = Modifier.height(8.dp))

                contenidoComentarios?.invoke()
            }
        }
    }
}

@Composable
fun ActionIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, count: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), modifier = Modifier.size(18.dp))
        if (count.isNotEmpty()) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(count, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), fontSize = 13.sp)
        }
    }
}

fun calcularTiempo(
    fecha: String?,
    horaServidor: String
): String {

    if (fecha.isNullOrBlank()) {
        return "Ahora"
    }

    if (horaServidor.isBlank()) {
        return "..."
    }


    val ahora = LocalDateTime.parse(
        horaServidor.replace("\"", "")
    )

    val duracion = try {

        val fechaPublicacion = OffsetDateTime.parse(fecha)

        Duration.between(
            fechaPublicacion.toLocalDateTime(),
            ahora
        )

    } catch (e: Exception) {

        val fechaPublicacion = LocalDateTime.parse(fecha)

        Duration.between(
            fechaPublicacion,
            ahora
        )
    }

    return when {
        duracion.toMinutes() < 1 -> "Ahora"
        duracion.toHours() < 1 -> "${duracion.toMinutes()}m"
        duracion.toDays() < 1 -> "${duracion.toHours()}h"
        else -> "${duracion.toDays()}d"
    }
}