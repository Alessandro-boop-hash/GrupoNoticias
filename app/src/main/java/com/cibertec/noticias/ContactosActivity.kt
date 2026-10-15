package com.cibertec.noticias

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.cibertec.noticias.adaptador.AdaptadorContactos
import com.cibertec.noticias.base.BaseActivity
import com.cibertec.noticias.datos.CatalogoContactos
import com.cibertec.noticias.databinding.ActivityContactosBinding
import com.cibertec.noticias.modelo.Contacto
import com.cibertec.noticias.util.intentar
import com.cibertec.noticias.util.mostrarDialogo
import com.cibertec.noticias.util.versionDeLaApp

/**
 * Listado de contactos de la red de prensa.
 *
 * Es la pantalla que implementa el MENU CONTEXTUAL: la lista se registra con
 * registerForContextMenu(), por lo que al mantener presionado un contacto
 * Android infla menu_contextual_contacto.xml y el usuario elige entre llamar,
 * enviar un mensaje o ver la ficha.
 */
class ContactosActivity : BaseActivity() {

    private lateinit var vinculo: ActivityContactosBinding
    private lateinit var adaptador: AdaptadorContactos

    /** Contacto sobre el que se abrio el menu contextual. */
    private var contactoDelMenu: Contacto? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vinculo = ActivityContactosBinding.inflate(layoutInflater)
        setLayout(vinculo.root)

        setSupportActionBar(vinculo.barraContactos)
        supportActionBar?.title = getString(R.string.contactos_titulo)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        configurarLista()

        // Activa el menu contextual para todas las filas de la lista.
        registerForContextMenu(vinculo.listaContactos)
    }

    private fun configurarLista() {
        adaptador = AdaptadorContactos(CatalogoContactos.listar())
        vinculo.listaContactos.layoutManager = LinearLayoutManager(this)
        vinculo.listaContactos.adapter = adaptador
    }

    // ------------------------------------------------------------------
    // MENU DE OPCIONES
    // ------------------------------------------------------------------

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_contactos, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            onBackPressedDispatcher.onBackPressed()
            true
        }

        R.id.menuMarcarTodos -> {
            Toast.makeText(this, R.string.contactos_marcados, Toast.LENGTH_SHORT).show()
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

    // ------------------------------------------------------------------
    // MENU CONTEXTUAL
    // ------------------------------------------------------------------

    /**
     * Se dispara al mantener presionado cualquier item de listaContactos.
     * El contacto se obtiene del tag de la fila (ver [AdaptadorContactos]),
     * ya que RecyclerView no construye un AdapterContextMenuInfo.
     */
    override fun onCreateContextMenu(menu: ContextMenu, view: View, info: ContextMenuInfo?) {
        super.onCreateContextMenu(menu, view, info)

        val contacto = view.tag as? Contacto ?: return
        contactoDelMenu = contacto

        menuInflater.inflate(R.menu.menu_contextual_contacto, menu)
        menu.setHeaderTitle(R.string.contextual_menu_titulo)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val contacto = contactoDelMenu ?: return super.onContextItemSelected(item)

        return when (item.itemId) {
            R.id.contextualLlamar -> {
                llamar(contacto)
                true
            }

            R.id.contextualMensaje -> {
                enviarMensaje(contacto)
                true
            }

            R.id.contextualPerfil -> {
                mostrarFicha(contacto)
                true
            }

            else -> super.onContextItemSelected(item)
        }
    }

    override fun onContextMenuClosed(menu: Menu) {
        super.onContextMenuClosed(menu)
        contactoDelMenu = null
    }

    // ------------------------------------------------------------------
    // Acciones del menu contextual (intents implicitos)
    // ------------------------------------------------------------------

    /** ACTION_DIAL: abre el marcador sin marcar la llamada. */
    private fun llamar(contacto: Contacto) {
        val marcar = Intent(
            Intent.ACTION_DIAL,
            Uri.parse("tel:${contacto.telefonoNormalizado}")
        )
        intentar(marcar, R.string.sin_registro_llamada)
    }

    /** ACTION_SENDTO: abre el editor de mensajes con el número precargado. */
    private fun enviarMensaje(contacto: Contacto) {
        val escribir = Intent(
            Intent.ACTION_SENDTO,
            Uri.parse("smsto:${contacto.telefonoNormalizado}")
        ).apply {
            putExtra("sms_body", getString(R.string.perfil_correo_cuerpo))
        }
        intentar(escribir, R.string.sin_registro_mensaje)
    }

    private fun mostrarFicha(contacto: Contacto) {
        mostrarDialogo(
            getString(R.string.contacto_perfil_titulo, contacto.puesto, contacto.telefono),
            getString(R.string.contacto_perfil_cuerpo, contacto.puesto, contacto.telefono),
            R.string.dialogo_ok
        )
    }
}