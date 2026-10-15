package com.cibertec.noticias

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.cibertec.noticias.base.BaseActivity
import com.cibertec.noticias.databinding.ActivityPrivacidadBinding
import com.cibertec.noticias.util.mostrarDialogo
import com.cibertec.noticias.util.versionDeLaApp

/**
 * Pantalla de Privacidad con las reglas de tratamiento de datos personales.
 * Se abre desde la opcion "Privacidad" del menu de opciones de Perfil.
 */
class PrivacidadActivity : BaseActivity() {

    private lateinit var vinculo: ActivityPrivacidadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vinculo = ActivityPrivacidadBinding.inflate(layoutInflater)
        setLayout(vinculo.root)

        setSupportActionBar(vinculo.barraPrivacidad)
        supportActionBar?.title = getString(R.string.privacidad_titulo)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // ------------------------------------------------------------------
    // MENU DE OPCIONES (desplegable de tres puntos)
    // ------------------------------------------------------------------

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_privacidad, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            onBackPressedDispatcher.onBackPressed()
            true
        }

        R.id.menuAyuda -> {
            mostrarDialogo(
                getString(R.string.dialogo_ayuda_titulo),
                getString(R.string.dialogo_ayuda_cuerpo),
                R.string.dialogo_ok
            )
            true
        }

        R.id.menuAcerca -> {
            mostrarDialogo(
                getString(R.string.dialogo_acerca_titulo),
                getString(R.string.dialogo_acerca_cuerpo, versionDeLaApp),
                R.string.dialogo_ok
            )
            true
        }

        else -> super.onOptionsItemSelected(item)
    }
}