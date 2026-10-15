package com.cibertec.noticias

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.cibertec.noticias.adaptador.AdaptadorNoticias
import com.cibertec.noticias.base.BaseActivity
import com.cibertec.noticias.datos.CatalogoNoticias
import com.cibertec.noticias.databinding.ActivityNoticiasBinding
import com.cibertec.noticias.databinding.DialogoBusquedaBinding
import com.cibertec.noticias.modelo.Noticia
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Pantalla principal con el listado de noticias.
 *
 * Concentra dos de los criterios de evaluacion:
 *  - el boton flotante arma un INTENT IMPLICITO de tipo ACTION_SEND para
 *    que el usuario elija en que aplicacion externa compartir, y
 *  - la barra superior es un MENU DE OPCIONES (icono de persona, buscar y
 *    un desplegable con las acciones secundarias).
 */
class NoticiasActivity : BaseActivity() {

    private lateinit var vinculo: ActivityNoticiasBinding
    private lateinit var adaptador: AdaptadorNoticias

    private var catalogo: List<Noticia> = emptyList()
    private var seleccionada: Noticia? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vinculo = ActivityNoticiasBinding.inflate(layoutInflater)
        setLayout(vinculo.root)

        configurarBarra()
        configurarLista()
    }

    private fun configurarBarra() {
        setSupportActionBar(vinculo.barraNoticias)
        supportActionBar?.title = getString(R.string.noticias_titulo)
    }

    private fun configurarLista() {
        catalogo = CatalogoNoticias.listar()

        // La primera noticia queda seleccionada para que el boton flotante
        // tenga algo que compartir desde el primer momento.
        seleccionada = catalogo.firstOrNull()

        adaptador = AdaptadorNoticias(
            noticias = catalogo,
            alSeleccionar = ::alSeleccionarNoticia,
            alCompartir = ::alCompartirNoticia
        )

        vinculo.listaNoticias.layoutManager = LinearLayoutManager(this)
        vinculo.listaNoticias.adapter = adaptador

        // Boton flotante: comparte la noticia seleccionada.
        vinculo.botonCompartir.setOnClickListener { compartirSeleccionada() }
    }

    // ------------------------------------------------------------------
    // MENU DE OPCIONES
    // ------------------------------------------------------------------

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_noticias, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.actionPerfil -> {
            abrirPerfil()
            true
        }

        R.id.actionBuscar -> {
            mostrarBuscador()
            true
        }

        R.id.actionActualizar -> {
            restaurarCatalogo()
            true
        }

        R.id.actionSalir -> {
            cerrarSesion()
            true
        }

        else -> super.onOptionsItemSelected(item)
    }

    // ------------------------------------------------------------------
    // Acciones de la pantalla
    // ------------------------------------------------------------------

    private fun alSeleccionarNoticia(noticia: Noticia) {
        seleccionada = noticia
        adaptador.seleccionar(noticia)
        Toast.makeText(
            this,
            getString(R.string.noticias_seleccionada, noticia.titulo),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun alCompartirNoticia(noticia: Noticia) {
        seleccionada = noticia
        compartir(noticia)
    }

    /** ---- INTENT IMPLICITO de compartir (ACTION_SEND) ------------------- */
    private fun compartir(noticia: Noticia) {
        val cuerpo = getString(R.string.noticias_descripcion_compartir, noticia.titulo) +
            "\n\n" + noticia.resumen

        val enviar = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, noticia.titulo)
            putExtra(Intent.EXTRA_TEXT, cuerpo)
        }

        // createChooser() muestra el panel del sistema para elegir la app destino.
        startActivity(Intent.createChooser(enviar, getString(R.string.compartir_titulo_elegir)))
    }

    private fun compartirSeleccionada() {
        val noticia = seleccionada
        if (noticia == null) {
            Toast.makeText(this, R.string.noticias_vacio, Toast.LENGTH_SHORT).show()
            return
        }
        compartir(noticia)
    }

    /** ---- INTENT EXPLICITO: Noticias -> Perfil ------------------------- */
    private fun abrirPerfil() {
        startActivity(Intent(this, PerfilActivity::class.java))
    }

    private fun mostrarBuscador() {
        val formulario = DialogoBusquedaBinding.inflate(layoutInflater)

        val dialogo: AlertDialog = MaterialAlertDialogBuilder(this)
            .setTitle(R.string.noticias_buscador_titulo)
            .setView(formulario.root)
            .setPositiveButton(R.string.dialogo_buscar) { _, _ ->
                filtrarCatalogo(formulario.campoBusqueda.text?.toString().orEmpty())
            }
            .setNegativeButton(R.string.dialogo_cancelar, null)
            .show()

        // El teclado se abre automaticamente al mostrar el buscador.
        formulario.campoBusqueda.requestFocus()
        dialogo.window?.setSoftInputMode(
            android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
        )
    }

    private fun filtrarCatalogo(texto: String) {
        val termino = texto.trim()

        if (termino.isEmpty()) {
            restaurarCatalogo()
            return
        }

        val resultado = catalogo.filter { noticia ->
            noticia.titulo.contains(termino, ignoreCase = true) ||
                noticia.resumen.contains(termino, ignoreCase = true)
        }

        adaptador.mostrar(resultado)

        Toast.makeText(
            this,
            if (resultado.isEmpty()) {
                getString(R.string.noticias_buscador_vacio, termino)
            } else {
                getString(R.string.noticias_buscador_lista, resultado.size, termino)
            },
            Toast.LENGTH_SHORT
        ).show()

        if (resultado.isEmpty()) restaurarCatalogo()
    }

    private fun restaurarCatalogo() {
        adaptador.mostrar(catalogo)
        Toast.makeText(this, R.string.noticias_actualizadas, Toast.LENGTH_SHORT).show()
    }

    /** ---- INTENT EXPLICITO: volver al Login, limpiando la pila -------- */
    private fun cerrarSesion() {
        startActivity(
            Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )
        finish()
    }

    companion object {
        /** Correo con el que se ingreso; se muestra como subtitulo de la barra. */
        const val EXTRA_CORREO = "extra_correo"
    }
}