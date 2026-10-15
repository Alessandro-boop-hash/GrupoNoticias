package com.cibertec.noticias.util

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.annotation.StringRes
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Muestra un dialogo informativo con un unico boton de aceptacion.
 * Se usa en las pantallas de Perfil y Privacidad.
 */
fun Activity.mostrarDialogo(titulo: String, mensaje: String, @StringRes boton: Int) {
    MaterialAlertDialogBuilder(this)
        .setTitle(titulo)
        .setMessage(mensaje)
        .setPositiveButton(boton, null)
        .show()
}

/**
 * Lanza un intent implicito y avisa al usuario cuando ningun Activity del
 * dispositivo sabe atenderlo, en lugar de dejar que la app se cierre.
 */
fun Context.intentar(intent: Intent, @StringRes mensajeSiNoHayApp: Int) {
    try {
        startActivity(intent)
    } catch (excepcion: ActivityNotFoundException) {
        Toast.makeText(this, mensajeSiNoHayApp, Toast.LENGTH_LONG).show()
    }
}

/** Version declarada en el build.gradle, usada en el dialogo "Acerca de". */
val Context.versionDeLaApp: String
    get() = runCatching {
        packageManager.getPackageInfo(packageName, 0).versionName
    }.getOrNull() ?: "1.0"