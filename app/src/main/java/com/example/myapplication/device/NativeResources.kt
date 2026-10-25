package com.example.myapplication.device

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.location.LocationManager
import java.io.File
import java.io.FileOutputStream

/** Recurso nativo 1: cámara. Guarda la foto en el almacenamiento interno privado de la app. */
object PhotoStorage {
    fun save(context: Context, bitmap: Bitmap): String? = try {
        val dir = File(context.filesDir, "photos").apply { mkdirs() }
        val file = File(dir, "opp_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.JPEG, 85, it) }
        file.absolutePath
    } catch (e: Exception) {
        null
    }
}

/**
 * Recurso nativo 2: ubicación (aproximada, permiso COARSE, opcional).
 * Solo se lee una vez la última posición conocida; nunca se guarda ni se hace seguimiento.
 */
object LocationHelper {
    @SuppressLint("MissingPermission")
    fun lastKnown(context: Context): Pair<Double, Double>? = try {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        lm.getProviders(true)
            .mapNotNull { lm.getLastKnownLocation(it) }
            .maxByOrNull { it.time }
            ?.let { it.latitude to it.longitude }
    } catch (e: SecurityException) {
        null
    }
}
