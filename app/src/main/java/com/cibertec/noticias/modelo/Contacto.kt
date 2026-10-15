package com.cibertec.noticias.modelo

/**
 * Contacto de la red de prensa al que apunta el menu contextual.
 *
 * @param puesto  |area o medio al que pertenece el contacto.
 * @param telefono numero en formato internacional, usado por los intents de
 *                 llamada y de envio de mensajes.
 */
data class Contacto(
    val id: Int,
    val puesto: String,
    val telefono: String
) {
    /** Texto mostrado en la fila de la lista, p. ej. "Prensa Editorial: +51 987 987 987". */
    val etiqueta: String get() = "$puesto: $telefono"

    /** Numero sin espacios, formato aceptado por los intents tel: y smsto:. */
    val telefonoNormalizado: String get() = telefono.filter { it.isDigit() || it == '+' }
}