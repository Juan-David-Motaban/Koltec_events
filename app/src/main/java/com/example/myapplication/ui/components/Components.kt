package com.example.myapplication.ui.components

import android.app.DatePickerDialog
import android.graphics.BitmapFactory
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.asImageBitmap
import com.example.myapplication.domain.DateUtils
import com.example.myapplication.model.Opportunity
import com.example.myapplication.model.OpportunityStatus
import com.example.myapplication.model.Priority
import com.example.myapplication.ui.theme.*
import java.util.Calendar

/** Campo de texto con ícono, mensaje de error visual (ícono + texto animado) y teclado configurable. */
@Composable
fun AppTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    error: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    minLines: Int = 1,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailing: @Composable (() -> Unit)? = null,
    helper: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null) },
        trailingIcon = when {
            trailing != null -> trailing
            error != null -> ({ Icon(Icons.Filled.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error) })
            else -> null
        },
        isError = error != null,
        supportingText = {
            AnimatedVisibility(visible = error != null || helper != null, enter = fadeIn(), exit = fadeOut()) {
                Text(error ?: helper.orEmpty())
            }
        },
        singleLine = singleLine,
        minLines = minLines,
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier.fillMaxWidth()
    )
}

/** Campo de fecha dd/MM/aaaa: se puede escribir o elegir con el calendario nativo. */
@Composable
fun DateField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null
) {
    val context = LocalContext.current
    fun openPicker() {
        val parsed = DateUtils.parse(value) ?: DateUtils.startOfDay()
        val c = Calendar.getInstance().apply { timeInMillis = parsed }
        DatePickerDialog(
            context,
            { _, y, m, d -> onValueChange("%02d/%02d/%04d".format(d, m + 1, y)) },
            c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
    AppTextField(
        label = label, value = value, onValueChange = onValueChange,
        icon = Icons.Filled.Event, error = error, modifier = modifier,
        keyboardType = KeyboardType.Number, helper = "Formato dd/MM/aaaa",
        trailing = {
            IconButton(onClick = { openPicker() }) {
                Icon(Icons.Filled.CalendarMonth, contentDescription = "Abrir calendario", tint = MaterialTheme.colorScheme.primary)
            }
        }
    )
}

/** Selector desplegable. Si se entrega [onClear] se agrega la opción "Todos" (útil en filtros). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DropdownField(
    label: String,
    selected: T?,
    options: List<T>,
    optionLabel: (T) -> String,
    onSelected: (T) -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClear: (() -> Unit)? = null,
    error: String? = null,
    helper: String? = null
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }, modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selected?.let(optionLabel) ?: if (onClear != null) "Todos" else "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            leadingIcon = { Icon(icon, contentDescription = null) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            isError = error != null,
            supportingText = { if (error != null || helper != null) Text(error ?: helper.orEmpty()) },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            if (onClear != null) {
                DropdownMenuItem(text = { Text("Todos") }, onClick = { onClear(); expanded = false })
            }
            options.forEach { option ->
                DropdownMenuItem(text = { Text(optionLabel(option)) }, onClick = { onSelected(option); expanded = false })
            }
        }
    }
}

/** Calificación 1..5 con estrellas. */
@Composable
fun RatingRow(label: String, value: Int, onChange: (Int) -> Unit) {
    Column {
        Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row {
            (1..5).forEach { i ->
                IconButton(onClick = { onChange(i) }) {
                    Icon(
                        if (i <= value) Icons.Filled.Star else Icons.Filled.StarBorder,
                        contentDescription = "$i de 5",
                        tint = if (i <= value) Naranja else MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}

@Composable
fun StarsText(value: Int) {
    Text("★".repeat(value) + "☆".repeat((5 - value).coerceAtLeast(0)), color = Naranja, fontSize = 14.sp)
}

@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text.uppercase(), modifier = modifier.padding(top = 8.dp),
        fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.5.sp,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
        Column(Modifier.weight(1f)) {
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value.ifBlank { "—" }, fontSize = 14.sp)
        }
    }
}

@Composable
fun Chip(text: String, color: Color, modifier: Modifier = Modifier) {
    Surface(shape = RoundedCornerShape(50), color = color.copy(alpha = 0.14f), modifier = modifier) {
        Text(
            text, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color, maxLines = 1, overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun StatusChip(status: OpportunityStatus) = Chip(status.label, statusColor(status))

@Composable
fun PriorityChip(priority: Priority) = Chip("Prioridad ${priority.label}", priorityColor(priority))

@Composable
fun EmptyState(title: String, description: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(description, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
        }
    }
}

/** Texto relativo al evento: "En 5 días", "Hoy", "Hace 3 días". */
fun eventCountdown(o: Opportunity, today: Long = DateUtils.startOfDay()): String {
    val d = DateUtils.daysBetween(today, o.eventStart)
    return when {
        d == 0 -> "Hoy"
        d == 1 -> "Mañana"
        d > 1 -> "En $d días"
        d == -1 -> "Ayer"
        else -> "Hace ${-d} días"
    }
}

@Composable
fun OpportunityCard(o: Opportunity, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth().animateContentSize().clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(Modifier.size(44.dp), shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.Event, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                }
                Column(Modifier.weight(1f)) {
                    Text(o.name, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text(o.orgName, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.CalendarMonth, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                Text("${DateUtils.format(o.eventStart)} · ${eventCountdown(o)}", fontSize = 13.sp)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Place, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                Text("${o.comuna}, ${o.region} · ${o.type.label}", fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusChip(o.status)
                PriorityChip(o.priority)
            }
        }
    }
}

/** Miniatura de la foto guardada (recurso nativo: cámara). */
@Composable
fun PhotoThumb(path: String, modifier: Modifier = Modifier) {
    val bitmap = remember(path) {
        runCatching {
            BitmapFactory.decodeFile(path, BitmapFactory.Options().apply { inSampleSize = 2 })?.asImageBitmap()
        }.getOrNull()
    }
    if (bitmap != null) {
        Image(
            bitmap = bitmap, contentDescription = "Foto de la oportunidad",
            contentScale = ContentScale.Crop,
            modifier = modifier.fillMaxWidth().height(180.dp).let { it }
        )
    }
}
