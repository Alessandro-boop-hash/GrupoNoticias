package com.cibertec.noticias

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.cibertec.noticias.base.BaseActivity
import com.cibertec.noticias.databinding.ActivityPerfilBinding
import com.cibertec.noticias.util.intentar
import com.cibertec.noticias.util.mostrarDialogo

/**
 * Pantalla de Perfil del usuario.
 *
 * Su barra de opciones es la que muestra el desplegable de tres puntos con
 * Privacidad, Contactos y Central de ayuda; ademas permite escribir un
 * correo mediante un intent implicito.
 */
class PerfilActivity : BaseActivity() {

    private lateinit var vinculo: ActivityPerfilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vinculo = ActivityPerfilBinding.inflate(layoutInflater)
        setLayout(vinculo.root)

        configurarBarra()
        registrarEventos()
    }

    private fun configurarBarra() {
        setSupportActionBar(vinculo.barraPerfil)
        supportActionBar?.title = getString(R.string.perfil_titulo)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun registrarEventos() {
        vinculo.opcionInformacion.setOnClickListener {
            mostrarDialogo(
                getString(R.string.perfil_informacion_titulo),
                getString(R.string.perfil_informacion_cuerpo),
                R.string.dialogo_ok
            )
        }

        vinculo.opcionNotificaciones.setOnClickListener {
            mostrarDialogo(
                getString(R.string.perfil_notificaciones_titulo),
                getString(R.string.perfil_notificaciones_cuerpo),
                R.string.dialogo_ok
            )
        }

        vinculo.opcionFavoritos.setOnClickListener {
            mostrarDialogo(
                getString(R.string.perfil_favoritos_titulo),
                getString(R.string.perfil_favoritos_cuerpo),
                R.string.dialogo_ok
            )
        }

        vinculo.botonCerrarSesion.setOnClickListener { cerrarSesion() }
    }

    // ------------------------------------------------------------------
    // MENU DE OPCIONES
    // ------------------------------------------------------------------

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_perfil, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        // Flecha de retroceso de la barra.
        android.R.id.home -> {
            onBackPressedDispatcher.onBackPressed()
            true
        }

        R.id.actionBuscar -> {
            mostrarDialogo(
                getString(R.string.menu_buscar),
                getString(R.string.perfil_buscador_mensaje),
                R.string.dialogo_ok
            )
            true
        }

        R.id.actionCorreo -> {
            escribirCorreo()
            true
        }

        R.id.menuPrivacidad -> {
            abrirPrivacidad()
            true
        }

        R.id.menuContactos -> {
            abrirContactos()
            true
        }

        R.id.menuCentral -> {
            mostrarDialogo(
                getString(R.string.dialogo_ayuda_titulo),
                getString(R.string.dialogo_ayuda_cuerpo),
                R.string.dialogo_ok
            )
            true
        }

        else -> super.onOptionsItemSelected(item)
    }

    // ------------------------------------------------------------------
    // Acciones de la pantalla
    // ------------------------------------------------------------------

    /** ---- INTENT IMPLICITO: abrir el cliente de correo ---------------- */
    private fun escribirCorreo() {
        val correo = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:${getString(R.string.perfil_correo_destinatario)}")
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.perfil_correo_asunto))
            putExtra(Intent.EXTRA_TEXT, getString(R.string.perfil_correo_cuerpo))
        }
        intentar(correo, R.string.sin_registro_mensaje)
    }

    /** ---- INTENT EXPLICITO: Perfil -> Privacidad ---------------------- */
    private fun abrirPrivacidad() {
        startActivity(Intent(this, PrivacidadActivity::class.java))
    }

    /** ---- INTENT EXPLICITO: Perfil -> Contactos ----------------------- */
    private fun abrirContactos() {
        startActivity(Intent(this, ContactosActivity::class.java))
    }

    /** ---- INTENT EXPLICITO: volver al Login limpiando la pila --------- */
    private fun cerrarSesion() {
        Toast.makeText(this, R.string.perfil_sesion_cerrada, Toast.LENGTH_SHORT).show()
        startActivity(
            Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )
        finish()
    }
}