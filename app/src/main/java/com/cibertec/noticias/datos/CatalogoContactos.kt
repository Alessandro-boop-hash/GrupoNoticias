package com.cibertec.noticias.datos

import com.cibertec.noticias.modelo.Contacto

/** Listado fijo de contactos de la red de prensa. */
object CatalogoContactos {

    fun listar(): List<Contacto> = listOf(
        Contacto(id = 1, puesto = "Prensa Editorial", telefono = "+51 987 987 987"),
        Contacto(id = 2, puesto = "Prensa Local", telefono = "+51 912 456 789"),
        Contacto(id = 3, puesto = "Radio Noticias", telefono = "+51 998 112 233"),
        Contacto(id = 4, puesto = "Revista Digital", telefono = "+51 955 334 455"),
        Contacto(id = 5, puesto = "Canal TV", telefono = "+51 977 889 900")
    )
}