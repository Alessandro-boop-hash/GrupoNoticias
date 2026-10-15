package com.cibertec.noticias.modelo

import androidx.annotation.DrawableRes

/**
 * Noticia publicada en la pantalla principal.
 *
 * @param id      identificador unico, se usa para marcar la noticia seleccionada.
 * @param titulo  encabezado de la noticia.
 * @param resumen parrafo breve que se muestra bajo el titulo.
 * @param imagen  recurso de imagen que ilustra la noticia.
 */
data class Noticia(
    val id: Int,
    val titulo: String,
    val resumen: String,
    @param:DrawableRes val imagen: Int
)