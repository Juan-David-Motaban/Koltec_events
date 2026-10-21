
package com.example.myapplication

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.model.Oportunidad
import com.example.myapplication.model.Organizacion
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.util.ValidadorFormulario
import com.example.myapplication.viewmodel.OportunidadViewModel
import com.example.myapplication.viewmodel.OrganizacionViewModel
import java.util.Calendar
import java.util.Locale

private val Naranja = Color(0xFFFF713F)
private val Violeta = Color(0xFF7757E8)
private val MoradoOscuro = Color(0xFF211653)
private val TextoSuave = Color(0xFF7776A8)
private val Lavanda = Color(0xFFF1EEFF)
private val LavandaIcono = Color(0xFFE8E1FF)
private val NaranjaClaro = Color(0xFFFFE2D7)
private val Blanco = Color.White
private val VerdeEstado = Color(0xFF18865B)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Lavanda
                ) {
                    AplicacionOportunidades()
                }
            }
        }
    }
}

@Composable
fun AplicacionOportunidades() {
    var pantalla by remember { mutableStateOf("inicio") }

    val oportunidadViewModel = remember { OportunidadViewModel() }
    val organizacionViewModel = remember { OrganizacionViewModel() }

    when (pantalla) {
        "inicio" -> PantallaInicio(
            oportunidades = oportunidadViewModel.oportunidades.size,
            organizaciones = organizacionViewModel.organizaciones.size,
            onRegistrar = { pantalla = "registro" },
            onConsultar = { pantalla = "lista" },
            onOrganizaciones = { pantalla = "organizaciones" },
            onSeguimiento = { pantalla = "seguimiento" }
        )

        "registro" -> FormularioOportunidad(
            onVolver = { pantalla = "inicio" },
            onGuardar = { nombre, organizacion, fecha, ubicacion, tipo, publico, observaciones ->
                oportunidadViewModel.registrarOportunidad(
                    nombreEvento = nombre,
                    organizacion = organizacion,
                    fecha = fecha,
                    ubicacion = ubicacion,
                    tipoEvento = tipo,
                    publicoEsperado = publico,
                    observaciones = observaciones
                )
            },
            onExito = { pantalla = "lista" }
        )

        "lista" -> PantallaListaOportunidades(
            oportunidades = oportunidadViewModel.oportunidades,
            cantidadOrganizaciones = organizacionViewModel.organizaciones.size,
            onVolver = { pantalla = "inicio" },
            onRegistrar = { pantalla = "registro" }
        )

        "organizaciones" -> PantallaOrganizaciones(
            viewModel = organizacionViewModel,
            onVolver = { pantalla = "inicio" }
        )

        "seguimiento" -> PantallaSeguimiento(
            oportunidades = oportunidadViewModel.oportunidades,
            organizaciones = organizacionViewModel.organizaciones.size,
            onVolver = { pantalla = "inicio" },
            onConsultar = { pantalla = "lista" },
            onActualizar = { oportunidad ->
                oportunidadViewModel.actualizarOportunidad(oportunidad)
            }
        )
    }
}

@Composable
fun PantallaInicio(
    oportunidades: Int,
    organizaciones: Int,
    onRegistrar: () -> Unit,
    onConsultar: () -> Unit,
    onOrganizaciones: () -> Unit,
    onSeguimiento: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            LogoJO()
            Spacer(Modifier.width(12.dp))
            Surface(
                Modifier.width(2.dp).height(38.dp),
                color = Color(0xFFBEB4EB)
            ) {}
            Spacer(Modifier.width(14.dp))
            Text(
                "KOLTEC EVENTS",
                fontSize = 19.sp,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Bold,
                color = MoradoOscuro
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "BIENVENIDO/A",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp,
                    color = Violeta
                )
                Text(
                    "Tu próximo evento comienza aquí.",
                    fontSize = 29.sp,
                    lineHeight = 35.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MoradoOscuro
                )
                Text(
                    "Organiza oportunidades, administra tus organizaciones y mantén el control de tus eventos.",
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    color = TextoSuave
                )
            }
            Spacer(Modifier.width(4.dp))
            IlustracionCalendario()
        }

        ResumenGeneral(oportunidades, organizaciones)

        Button(
            onClick = onRegistrar,
            modifier = Modifier.fillMaxWidth().height(58.dp),
            shape = RoundedCornerShape(17.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Naranja)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(10.dp))
            Text("REGISTRAR OPORTUNIDAD", fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.weight(1f))
            Icon(Icons.Default.ArrowForward, contentDescription = null)
        }

        Text(
            "MÓDULOS",
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 2.sp,
            color = Violeta
        )

        TarjetaModulo(
            "Consultar eventos",
            "Revisa las oportunidades que ya registraste.",
            Icons.Default.CalendarMonth,
            Violeta,
            LavandaIcono,
            onConsultar
        )

        TarjetaModulo(
            "Organizaciones",
            "Registra y consulta las organizaciones.",
            Icons.Default.Apartment,
            Naranja,
            NaranjaClaro,
            onOrganizaciones
        )

        TarjetaModulo(
            "Seguimiento comercial",
            "Administra contactos, propuestas y próximas gestiones.",
            Icons.Default.Insights,
            Violeta,
            LavandaIcono,
            onSeguimiento
        )

        Text(
            "KOLTEC EVENTS",
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            textAlign = TextAlign.Center,
            fontSize = 11.sp,
            letterSpacing = 2.sp,
            fontWeight = FontWeight.Bold,
            color = Violeta
        )
    }
}

@Composable
fun LogoJO() {
    Box(
        modifier = Modifier.size(width = 65.dp, height = 58.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.size(width = 56.dp, height = 52.dp)) {
            val triangulo = Path().apply {
                moveTo(size.width / 2f, 0f)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }
            drawPath(triangulo, Naranja)
        }
        Text(
            "J&O",
            modifier = Modifier.padding(top = 15.dp),
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Blanco
        )
    }
}

@Composable
fun IlustracionCalendario() {
    Box(
        modifier = Modifier.size(width = 105.dp, height = 125.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier.size(100.dp),
            shape = RoundedCornerShape(30.dp),
            color = Color(0xFFE4DCFF)
        ) {}

        Card(
            modifier = Modifier.width(82.dp).height(87.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Blanco)
        ) {
            Column {
                Surface(
                    modifier = Modifier.fillMaxWidth().height(24.dp),
                    color = Violeta,
                    shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)
                ) {}
                Column(
                    modifier = Modifier.padding(9.dp),
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    repeat(2) {
                        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            repeat(4) {
                                Surface(
                                    Modifier.size(12.dp),
                                    shape = RoundedCornerShape(3.dp),
                                    color = LavandaIcono
                                ) {}
                            }
                        }
                    }
                }
            }
        }

        Surface(
            modifier = Modifier.align(Alignment.BottomEnd).size(36.dp),
            shape = RoundedCornerShape(50),
            color = Naranja
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Blanco
                )
            }
        }
    }
}

@Composable
fun ResumenGeneral(oportunidades: Int, organizaciones: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Blanco)
    ) {
        Column(
            modifier = Modifier.padding(17.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Text(
                "RESUMEN GENERAL",
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp,
                color = Violeta
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                ResumenDato(
                    oportunidades,
                    "Oportunidades",
                    Icons.Default.CalendarMonth,
                    Modifier.weight(1f)
                )
                Surface(
                    Modifier.width(1.dp).height(55.dp),
                    color = Color(0xFFC7BDF5)
                ) {}
                Spacer(Modifier.width(12.dp))
                ResumenDato(
                    organizaciones,
                    "Organizaciones",
                    Icons.Default.Apartment,
                    Modifier.weight(1f)
                )
            }
            Text(
                "Información actualizada durante esta sesión.",
                fontSize = 12.sp,
                color = TextoSuave
            )
        }
    }
}

@Composable
fun ResumenDato(
    cantidad: Int,
    etiqueta: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        Surface(
            Modifier.size(44.dp),
            shape = RoundedCornerShape(14.dp),
            color = LavandaIcono
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icono, contentDescription = null, tint = Violeta)
            }
        }
        Column {
            Text(
                cantidad.toString(),
                fontSize = 27.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MoradoOscuro
            )
            Text(etiqueta, fontSize = 11.sp, color = TextoSuave)
        }
    }
}

@Composable
fun TarjetaModulo(
    titulo: String,
    descripcion: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    colorIcono: Color,
    fondoIcono: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(19.dp),
        colors = CardDefaults.cardColors(containerColor = Blanco)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(13.dp)
        ) {
            Surface(
                Modifier.size(55.dp),
                shape = RoundedCornerShape(50),
                color = fondoIcono
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icono, contentDescription = null, tint = colorIcono)
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(titulo, fontWeight = FontWeight.Bold, color = MoradoOscuro)
                Text(
                    descripcion,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = TextoSuave
                )
            }
            Icon(
                Icons.Default.ArrowForward,
                contentDescription = null,
                tint = Violeta
            )
        }
    }
}

@Composable
fun EncabezadoPantalla(titulo: String, subtitulo: String) {
    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
        Text(
            titulo,
            fontSize = 27.sp,
            lineHeight = 33.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MoradoOscuro
        )
        Text(
            subtitulo,
            fontSize = 14.sp,
            lineHeight = 21.sp,
            color = TextoSuave
        )
    }
}

@Composable
fun FormularioOportunidad(
    onVolver: () -> Unit,
    onGuardar: (String, String, String, String, String, String, String) -> String,
    onExito: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var organizacion by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("") }
    var publico by remember { mutableStateOf("") }
    var observaciones by remember { mutableStateOf("") }
    var errores by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var mensaje by remember { mutableStateOf("") }
    var errorGeneral by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(22.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        EncabezadoPantalla(
            "Registrar oportunidad",
            "Completa los datos del evento para registrarlo."
        )

        CampoTexto(
            "Nombre del evento *",
            nombre,
            { nombre = it },
            "Ej. Seminario empresarial",
            error = errores["nombreEvento"]
        )

        CampoTexto(
            "Organización *",
            organizacion,
            { organizacion = it },
            "Nombre de la empresa",
            error = errores["organizacion"]
        )

        CampoFecha(
            etiqueta = "Fecha del evento *",
            fecha = fecha,
            alSeleccionar = {
                fecha = it
                errores = errores - "fecha"
            },
            error = errores["fecha"]
        )

        CampoTexto(
            "Ubicación *",
            ubicacion,
            { ubicacion = it },
            "Comuna o dirección",
            error = errores["ubicacion"]
        )

        CampoTexto(
            "Tipo de evento *",
            tipo,
            { tipo = it },
            "Conferencia, feria o seminario",
            error = errores["tipoEvento"]
        )

        CampoTexto(
            "Público esperado *",
            publico,
            { publico = it },
            "Cantidad de asistentes",
            tipoTeclado = KeyboardType.Number,
            error = errores["publicoEsperado"]
        )

        CampoTexto(
            "Observaciones",
            observaciones,
            { observaciones = it },
            "Información adicional",
            multilinea = true
        )

        if (mensaje.isNotBlank()) {
            Text(
                mensaje,
                color = if (errorGeneral) MaterialTheme.colorScheme.error else VerdeEstado,
                fontWeight = FontWeight.SemiBold
            )
        }

        Button(
            onClick = {
                errores = ValidadorFormulario.erroresOportunidad(
                    nombreEvento = nombre,
                    organizacion = organizacion,
                    fecha = fecha,
                    ubicacion = ubicacion,
                    tipoEvento = tipo,
                    publicoEsperado = publico
                )

                if (errores.isNotEmpty()) {
                    mensaje = "Revisa los campos marcados antes de continuar."
                    errorGeneral = true
                } else {
                    mensaje = onGuardar(
                        nombre,
                        organizacion,
                        fecha,
                        ubicacion,
                        tipo,
                        publico,
                        observaciones
                    )

                    errorGeneral =
                        mensaje != "Oportunidad registrada correctamente."

                    if (!errorGeneral) onExito()
                }
            },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Naranja)
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("GUARDAR OPORTUNIDAD", fontWeight = FontWeight.Bold)
        }

        BotonVolver(onVolver)
    }
}

@Composable
fun CampoFecha(
    etiqueta: String,
    fecha: String,
    alSeleccionar: (String) -> Unit,
    error: String? = null
) {
    val contexto = LocalContext.current
    val calendario = remember { Calendar.getInstance() }

    fun abrirCalendario() {
        val partes = fecha.split("/")
        val anioInicial = partes.getOrNull(2)?.toIntOrNull()
            ?: calendario.get(Calendar.YEAR)
        val mesInicial = (partes.getOrNull(1)?.toIntOrNull()
            ?: (calendario.get(Calendar.MONTH) + 1)) - 1
        val diaInicial = partes.getOrNull(0)?.toIntOrNull()
            ?: calendario.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            contexto,
            { _, anio, mes, dia ->
                alSeleccionar(
                    String.format(
                        Locale.ROOT,
                        "%02d/%02d/%04d",
                        dia,
                        mes + 1,
                        anio
                    )
                )
            },
            anioInicial,
            mesInicial.coerceIn(0, 11),
            diaInicial.coerceIn(1, 31)
        ).show()
    }

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            etiqueta,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MoradoOscuro
        )

        OutlinedTextField(
            value = fecha,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { abrirCalendario() },
            placeholder = { Text("Selecciona una fecha") },
            trailingIcon = {
                IconButton(onClick = { abrirCalendario() }) {
                    Icon(
                        Icons.Default.EditCalendar,
                        contentDescription = "Abrir calendario",
                        tint = Violeta
                    )
                }
            },
            isError = error != null,
            supportingText = {
                if (error != null) Text(error)
            },
            shape = RoundedCornerShape(14.dp)
        )
    }
}

@Composable
fun PantallaListaOportunidades(
    oportunidades: List<Oportunidad>,
    cantidadOrganizaciones: Int,
    onVolver: () -> Unit,
    onRegistrar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(22.dp),
        verticalArrangement = Arrangement.spacedBy(17.dp)
    ) {
        EncabezadoPantalla(
            "Mis oportunidades",
            "Todos tus eventos, organizados en un solo lugar."
        )

        ResumenGeneral(oportunidades.size, cantidadOrganizaciones)

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "EVENTOS REGISTRADOS",
                modifier = Modifier.weight(1f),
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.4.sp,
                color = Violeta
            )
            Surface(
                shape = RoundedCornerShape(50),
                color = LavandaIcono
            ) {
                Text(
                    oportunidades.size.toString(),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontWeight = FontWeight.Bold,
                    color = Violeta
                )
            }
        }

        if (oportunidades.isEmpty()) {
            MensajeVacio(
                "Todavía no hay eventos",
                "Registra una oportunidad para verla en esta sección."
            )
        } else {
            oportunidades.forEach { oportunidad ->
                TarjetaOportunidad(oportunidad)
            }
        }

        Button(
            onClick = onRegistrar,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Naranja)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("REGISTRAR OTRA OPORTUNIDAD", fontWeight = FontWeight.Bold)
        }

        BotonVolver(onVolver)
    }
}

@Composable
fun TarjetaOportunidad(oportunidad: Oportunidad) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Blanco),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 17.dp, end = 17.dp, top = 17.dp, bottom = 12.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(15.dp),
                    color = LavandaIcono
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Event,
                            contentDescription = null,
                            tint = Violeta
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        oportunidad.nombreEvento,
                        fontSize = 18.sp,
                        lineHeight = 23.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MoradoOscuro
                    )
                    Text(
                        oportunidad.organizacion,
                        fontSize = 13.sp,
                        color = TextoSuave,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            HorizontalDivider(color = Lavanda, thickness = 1.dp)

            Column(
                modifier = Modifier.padding(17.dp),
                verticalArrangement = Arrangement.spacedBy(13.dp)
            ) {
                DatoOportunidad(
                    Icons.Default.CalendarMonth,
                    "Fecha",
                    oportunidad.fecha
                )
                DatoOportunidad(
                    Icons.Default.LocationOn,
                    "Ubicación",
                    oportunidad.ubicacion
                )
                DatoOportunidad(
                    Icons.Default.Category,
                    "Tipo de evento",
                    oportunidad.tipoEvento
                )
                DatoOportunidad(
                    Icons.Default.Groups,
                    "Público esperado",
                    "${oportunidad.publicoEsperado} asistentes"
                )

                if (oportunidad.observaciones.isNotBlank()) {
                    DatoOportunidad(
                        Icons.Default.Notes,
                        "Observaciones",
                        oportunidad.observaciones
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = LavandaIcono
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 7.dp
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.Schedule,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Violeta
                            )
                            Text(
                                oportunidad.estado,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Violeta
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DatoOportunidad(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    etiqueta: String,
    valor: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            icono,
            contentDescription = null,
            modifier = Modifier.size(19.dp),
            tint = Violeta
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                etiqueta,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextoSuave
            )
            Text(
                valor,
                fontSize = 14.sp,
                lineHeight = 19.sp,
                color = MoradoOscuro
            )
        }
    }
}

@Composable
fun PantallaOrganizaciones(
    viewModel: OrganizacionViewModel,
    onVolver: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("") }
    var rubro by remember { mutableStateOf("") }
    var comuna by remember { mutableStateOf("") }
    var region by remember { mutableStateOf("") }
    var sitioWeb by remember { mutableStateOf("") }
    var publico by remember { mutableStateOf("") }
    var observaciones by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(22.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        EncabezadoPantalla(
            "Organizaciones",
            "Registra y consulta las organizaciones relacionadas con tus eventos."
        )

        CampoTexto("Nombre *", nombre, { nombre = it }, "Nombre de la organización")
        CampoTexto("Tipo de organización *", tipo, { tipo = it }, "Empresa, fundación o institución")
        CampoTexto("Rubro *", rubro, { rubro = it }, "Tecnología, educación, etc.")
        CampoTexto("Comuna *", comuna, { comuna = it }, "Ej. Puente Alto")
        CampoTexto("Región *", region, { region = it }, "Ej. Región Metropolitana")
        CampoTexto("Sitio web", sitioWeb, { sitioWeb = it }, "https://empresa.cl")
        CampoTexto("Público asociado", publico, { publico = it }, "Público relacionado")
        CampoTexto(
            "Observaciones",
            observaciones,
            { observaciones = it },
            "Información adicional",
            multilinea = true
        )

        if (mensaje.isNotBlank()) {
            Text(
                mensaje,
                color = if (error) MaterialTheme.colorScheme.error else VerdeEstado
            )
        }

        Button(
            onClick = {
                mensaje = viewModel.registrarOrganizacion(
                    nombre = nombre,
                    tipoOrganizacion = tipo,
                    rubro = rubro,
                    comuna = comuna,
                    region = region,
                    sitioWeb = sitioWeb,
                    publicoAsociado = publico,
                    observaciones = observaciones
                )
                error = mensaje != "Organización registrada correctamente."

                if (!error) {
                    nombre = ""
                    tipo = ""
                    rubro = ""
                    comuna = ""
                    region = ""
                    sitioWeb = ""
                    publico = ""
                    observaciones = ""
                }
            },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Naranja)
        ) {
            Text("REGISTRAR ORGANIZACIÓN", fontWeight = FontWeight.Bold)
        }

        Text(
            "ORGANIZACIONES REGISTRADAS: ${viewModel.organizaciones.size}",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextoSuave
        )

        if (viewModel.organizaciones.isEmpty()) {
            MensajeVacio(
                "Aún no hay organizaciones",
                "Las organizaciones que registres aparecerán aquí."
            )
        } else {
            viewModel.organizaciones.forEach { organizacion ->
                TarjetaOrganizacion(organizacion)
            }
        }

        BotonVolver(onVolver)
    }
}

@Composable
fun TarjetaOrganizacion(organizacion: Organizacion) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Blanco)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Text(
                organizacion.nombre,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MoradoOscuro
            )
            Text("Tipo: ${organizacion.tipoOrganizacion}", color = TextoSuave)
            Text("Rubro: ${organizacion.rubro}", color = TextoSuave)
            Text(
                "Ubicación: ${organizacion.comuna}, ${organizacion.region}",
                color = TextoSuave
            )

            if (organizacion.sitioWeb.isNotBlank()) {
                Text("Sitio web: ${organizacion.sitioWeb}", color = TextoSuave)
            }
            if (organizacion.publicoAsociado.isNotBlank()) {
                Text("Público asociado: ${organizacion.publicoAsociado}", color = TextoSuave)
            }
            if (organizacion.observaciones.isNotBlank()) {
                Text("Observaciones: ${organizacion.observaciones}", color = TextoSuave)
            }
        }
    }
}

@Composable
fun PantallaSeguimiento(
    oportunidades: List<Oportunidad>,
    organizaciones: Int,
    onVolver: () -> Unit,
    onConsultar: () -> Unit,
    onActualizar: (Oportunidad) -> String
) {
    var oportunidadSeleccionada by remember {
        mutableStateOf<Oportunidad?>(null)
    }
    var mensaje by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(22.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        EncabezadoPantalla(
            "Seguimiento comercial",
            "Administra contactos, propuestas y próximas gestiones."
        )

        ResumenGeneral(oportunidades.size, organizaciones)

        ResumenLinea(
            "Gestiones pendientes",
            oportunidades.count {
                it.estado == "Pendiente" || it.estado == "En contacto"
            }.toString()
        )

        ResumenLinea(
            "Propuestas enviadas",
            oportunidades.count { it.propuestaEnviada }.toString()
        )

        ResumenLinea(
            "Oportunidades aceptadas",
            oportunidades.count { it.estado == "Aceptada" }.toString()
        )

        AnimatedVisibility(
            visible = mensaje.isNotBlank(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                mensaje,
                color = VerdeEstado,
                fontWeight = FontWeight.SemiBold
            )
        }

        Text(
            "OPORTUNIDADES POR GESTIONAR",
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.3.sp,
            color = Violeta
        )

        if (oportunidades.isEmpty()) {
            MensajeVacio(
                "No hay oportunidades registradas",
                "Registra un evento para comenzar a gestionar sus contactos y propuestas."
            )
        } else {
            oportunidades.forEach { oportunidad ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Blanco)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(9.dp)
                    ) {
                        Text(
                            oportunidad.nombreEvento,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MoradoOscuro
                        )

                        Text(oportunidad.organizacion, color = TextoSuave)

                        HorizontalDivider(color = Lavanda)

                        ResumenLinea("Estado", oportunidad.estado)
                        ResumenLinea(
                            "Contacto",
                            oportunidad.contactoNombre.ifBlank { "Sin registrar" }
                        )
                        ResumenLinea(
                            "Área o cargo",
                            oportunidad.contactoArea.ifBlank { "Sin información" }
                        )
                        ResumenLinea(
                            "Propuesta",
                            if (oportunidad.propuestaEnviada) "Enviada"
                            else "Pendiente de envío"
                        )
                        ResumenLinea(
                            "Próximo seguimiento",
                            oportunidad.fechaSeguimiento.ifBlank {
                                "Sin fecha programada"
                            }
                        )

                        Button(
                            onClick = {
                                oportunidadSeleccionada = oportunidad
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(13.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Violeta
                            )
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("ADMINISTRAR SEGUIMIENTO")
                        }
                    }
                }
            }
        }

        Button(
            onClick = onConsultar,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Naranja)
        ) {
            Text("CONSULTAR MIS EVENTOS", fontWeight = FontWeight.Bold)
        }

        BotonVolver(onVolver)
    }

    oportunidadSeleccionada?.let { oportunidad ->
        DialogoEditarSeguimiento(
            oportunidad = oportunidad,
            onCerrar = { oportunidadSeleccionada = null },
            onGuardar = { actualizada ->
                val resultado = onActualizar(actualizada)
                mensaje = resultado
                oportunidadSeleccionada = null
            }
        )
    }
}

@Composable
fun ResumenLinea(etiqueta: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            etiqueta,
            modifier = Modifier.weight(1f),
            color = TextoSuave,
            fontSize = 13.sp
        )
        Text(
            valor,
            modifier = Modifier.weight(1f),
            color = MoradoOscuro,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun DialogoEditarSeguimiento(
    oportunidad: Oportunidad,
    onCerrar: () -> Unit,
    onGuardar: (Oportunidad) -> Unit
) {
    var estado by remember(oportunidad.id) {
        mutableStateOf(oportunidad.estado)
    }
    var contacto by remember(oportunidad.id) {
        mutableStateOf(oportunidad.contactoNombre)
    }
    var area by remember(oportunidad.id) {
        mutableStateOf(oportunidad.contactoArea)
    }
    var correo by remember(oportunidad.id) {
        mutableStateOf(oportunidad.contactoCorreo)
    }
    var telefono by remember(oportunidad.id) {
        mutableStateOf(oportunidad.contactoTelefono)
    }
    var propuesta by remember(oportunidad.id) {
        mutableStateOf(oportunidad.propuestaEnviada)
    }
    var costo by remember(oportunidad.id) {
        mutableStateOf(oportunidad.costoEstimado)
    }
    var condiciones by remember(oportunidad.id) {
        mutableStateOf(oportunidad.condicionesParticipacion)
    }
    var fecha by remember(oportunidad.id) {
        mutableStateOf(oportunidad.fechaSeguimiento)
    }
    var notas by remember(oportunidad.id) {
        mutableStateOf(oportunidad.notasSeguimiento)
    }
    var resultadoActivacion by remember(oportunidad.id) {
        mutableStateOf(oportunidad.resultadoActivacion)
    }
    var error by remember(oportunidad.id) {
        mutableStateOf("")
    }
    var desplegarEstados by remember {
        mutableStateOf(false)
    }

    val estados = listOf(
        "Pendiente",
        "En contacto",
        "Propuesta enviada",
        "Aceptada",
        "Rechazada"
    )

    AlertDialog(
        onDismissRequest = onCerrar,
        title = {
            Text(
                "Seguimiento comercial",
                fontWeight = FontWeight.Bold,
                color = MoradoOscuro
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 520.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    oportunidad.nombreEvento,
                    fontWeight = FontWeight.SemiBold,
                    color = Violeta
                )

                Text("Estado de la oportunidad")

                Box {
                    OutlinedButton(
                        onClick = { desplegarEstados = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(estado, modifier = Modifier.weight(1f))
                        Icon(
                            Icons.Default.ArrowForward,
                            contentDescription = "Cambiar estado"
                        )
                    }

                    DropdownMenu(
                        expanded = desplegarEstados,
                        onDismissRequest = { desplegarEstados = false }
                    ) {
                        estados.forEach { opcion ->
                            DropdownMenuItem(
                                text = { Text(opcion) },
                                onClick = {
                                    estado = opcion
                                    desplegarEstados = false
                                }
                            )
                        }
                    }
                }

                CampoTexto(
                    "Nombre del contacto",
                    contacto,
                    { contacto = it },
                    "Nombre del contacto"
                )

                CampoTexto(
                    "Área o cargo",
                    area,
                    { area = it },
                    "Marketing, RR. HH. o Bienestar"
                )

                CampoTexto(
                    "Correo electrónico",
                    correo,
                    { correo = it },
                    "contacto@empresa.cl",
                    tipoTeclado = KeyboardType.Email
                )

                CampoTexto(
                    "Teléfono",
                    telefono,
                    { telefono = it },
                    "+56 9 1234 5678",
                    tipoTeclado = KeyboardType.Phone
                )

                HorizontalDivider(color = Lavanda)

                Text(
                    "Propuesta comercial",
                    fontWeight = FontWeight.Bold,
                    color = MoradoOscuro
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = propuesta,
                        onCheckedChange = { propuesta = it }
                    )
                    Text("Propuesta enviada")
                }

                CampoTexto(
                    "Costo estimado",
                    costo,
                    { costo = it },
                    "Ej. 150000",
                    tipoTeclado = KeyboardType.Number
                )

                CampoTexto(
                    "Condiciones de participación",
                    condiciones,
                    { condiciones = it },
                    "Espacio, horario, requisitos...",
                    multilinea = true
                )

                CampoFecha(
                    etiqueta = "Próxima fecha de seguimiento",
                    fecha = fecha,
                    alSeleccionar = { fecha = it }
                )

                CampoTexto(
                    "Notas de seguimiento",
                    notas,
                    { notas = it },
                    "Llamada, correo o respuesta recibida...",
                    multilinea = true
                )

                CampoTexto(
                    "Resultado de la activación",
                    resultadoActivacion,
                    { resultadoActivacion = it },
                    "Pendiente, realizada, inscripciones...",
                    multilinea = true
                )

                if (error.isNotBlank()) {
                    Text(
                        error,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    error = ValidadorFormulario.validarSeguimiento(
                        correo = correo,
                        costoEstimado = costo
                    ) ?: ""

                    if (error.isBlank()) {
                        onGuardar(
                            oportunidad.copy(
                                estado = estado,
                                contactoNombre = contacto,
                                contactoArea = area,
                                contactoCorreo = correo,
                                contactoTelefono = telefono,
                                propuestaEnviada = propuesta,
                                costoEstimado = costo,
                                condicionesParticipacion = condiciones,
                                fechaSeguimiento = fecha,
                                notasSeguimiento = notas,
                                resultadoActivacion = resultadoActivacion
                            )
                        )
                    }
                }
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(Modifier.width(5.dp))
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCerrar) {
                Icon(Icons.Default.Close, contentDescription = null)
                Spacer(Modifier.width(5.dp))
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun CampoTexto(
    etiqueta: String,
    valor: String,
    alCambiar: (String) -> Unit,
    ejemplo: String? = null,
    multilinea: Boolean = false,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    error: String? = null
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            etiqueta,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MoradoOscuro
        )

        OutlinedTextField(
            value = valor,
            onValueChange = alCambiar,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                if (ejemplo != null) Text(ejemplo)
            },
            shape = RoundedCornerShape(14.dp),
            minLines = if (multilinea) 3 else 1,
            maxLines = if (multilinea) 5 else 1,
            keyboardOptions = KeyboardOptions(keyboardType = tipoTeclado),
            isError = error != null,
            supportingText = {
                if (error != null) Text(error)
            }
        )
    }
}

@Composable
fun MensajeVacio(titulo: String, descripcion: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = LavandaIcono)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                titulo,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MoradoOscuro
            )
            Text(
                descripcion,
                fontSize = 14.sp,
                lineHeight = 21.sp,
                color = TextoSuave
            )
        }
    }
}

@Composable
fun BotonVolver(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(15.dp)
    ) {
        Text("VOLVER AL INICIO", fontWeight = FontWeight.Bold)
    }
}
