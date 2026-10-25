package com.example.myapplication.device

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter

/**
 * Recurso nativo 3: lector de códigos QR.
 * Usa el escáner de Google Play Services (ML Kit): trae su propia pantalla de cámara,
 * por lo que la app no necesita declarar el permiso CAMERA.
 */
object QrScanner {
    fun scan(context: Context, onResult: (String) -> Unit, onError: (String) -> Unit) {
        val options = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
        GmsBarcodeScanning.getClient(context, options)
            .startScan()
            .addOnSuccessListener { barcode -> onResult(barcode.rawValue.orEmpty()) }
            .addOnFailureListener {
                onError("No se pudo abrir el lector QR. Revisa que el dispositivo tenga Google Play Services actualizado.")
            }
        // Si el usuario cancela el escaneo no se hace nada.
    }
}

/** Genera la imagen de un código QR a partir de un texto (ZXing). */
object QrGenerator {
    fun bitmap(texto: String, tamano: Int = 720): Bitmap? = try {
        val hints = mapOf(EncodeHintType.MARGIN to 1, EncodeHintType.CHARACTER_SET to "UTF-8")
        val matriz = QRCodeWriter().encode(texto, BarcodeFormat.QR_CODE, tamano, tamano, hints)
        val w = matriz.width
        val h = matriz.height
        val pixeles = IntArray(w * h) { i -> if (matriz.get(i % w, i / w)) Color.BLACK else Color.WHITE }
        Bitmap.createBitmap(pixeles, w, h, Bitmap.Config.ARGB_8888)
    } catch (e: Exception) {
        null
    }
}
