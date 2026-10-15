package com.cibertec.noticias.base

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updatePadding
import kotlin.math.max

/**
 * Activity base de la aplicacion.
 *
 * Centraliza dos tareas que se repiten en las cinco pantallas:
 *  1. el modo "edge to edge" (obligatorio a partir de targetSdk 35), y
 *  2. el margen automatico de las barras del sistema y del teclado, que se
 *     aplica como padding de la raiz del layout para que el contenido
 *     nunca quede oculto detras de la barra de estado o de la navegacion.
 */
abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }
    }

    /** Muestra la pantalla y protege la raiz del layout de los margenes del sistema. */
    protected fun setLayout(raiz: View) {
        setContentView(raiz)
        raiz.aplicarMargenesDelSistema()
    }
}

/** Suma el alto de las barras del sistema o del teclado (el mayor de ambos) al padding. */
private fun View.aplicarMargenesDelSistema() {
    val margenIzquierdo = paddingLeft
    val margenSuperior = paddingTop
    val margenDerecho = paddingRight
    val margenInferior = paddingBottom

    ViewCompat.setOnApplyWindowInsetsListener(this) { vista, insets ->
        val barras = insets.getInsets(
            WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
        )
        val teclado = insets.getInsets(WindowInsetsCompat.Type.ime())

        vista.updatePadding(
            left = margenIzquierdo + barras.left,
            top = margenSuperior + barras.top,
            right = margenDerecho + barras.right,
            bottom = margenInferior + max(barras.bottom, teclado.bottom)
        )
        insets
    }
    ViewCompat.requestApplyInsets(this)
}