package com.example.myapplication

import com.example.myapplication.domain.QrContent
import com.example.myapplication.domain.QrPayload
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class QrPayloadTest {
    @Test fun genera_y_lee_qr_de_empresa() {
        assertEquals("KOLTEC:EMP:3", QrPayload.empresa(3))
        assertEquals(QrContent.Empresa(3), QrPayload.parse(QrPayload.empresa(3)))
    }

    @Test fun genera_y_lee_qr_de_oportunidad() {
        assertEquals("KOLTEC:OPP:42", QrPayload.oportunidad(42))
        assertEquals(QrContent.Oportunidad(42), QrPayload.parse(QrPayload.oportunidad(42)))
    }

    @Test fun tolera_espacios_y_mayusculas() {
        assertEquals(QrContent.Empresa(7), QrPayload.parse("  koltec:emp:7 \n"))
    }

    @Test fun rechaza_contenido_ajeno_o_mal_formado() {
        assertNull(QrPayload.parse(null))
        assertNull(QrPayload.parse(""))
        assertNull(QrPayload.parse("https://ejemplo.cl"))
        assertNull(QrPayload.parse("KOLTEC:EMP"))
        assertNull(QrPayload.parse("KOLTEC:EMP:abc"))
        assertNull(QrPayload.parse("KOLTEC:EMP:0"))
        assertNull(QrPayload.parse("KOLTEC:EMP:-5"))
        assertNull(QrPayload.parse("KOLTEC:XXX:1"))
        assertNull(QrPayload.parse("OTRA:EMP:1"))
        assertNull(QrPayload.parse("KOLTEC:EMP:1:extra"))
        assertNull(QrPayload.parse("KOLTEC:EMP:99999999999"))
    }
}
