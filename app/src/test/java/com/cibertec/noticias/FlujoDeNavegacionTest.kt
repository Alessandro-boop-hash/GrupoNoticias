package com.cibertec.noticias

import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cibertec.noticias.adaptador.AdaptadorContactos
import com.google.android.material.textfield.TextInputLayout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowDialog

/**
 * Pruebas de humo del flujo completo de la app: Login -> Noticias -> Perfil
 * -> Contactos, mas la construccion de los intents explicitos e implicitos.
 *
 * Se ejecutan sobre la JVM con Robolectric, sin necesidad de un emulador.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class FlujoDeNavegacionTest {

    private val correoValido = "alumno@cibertec.edu.pe"

    companion object {
        private const val CLAVE_VALIDA = "123456"
    }

    // ------------------------------------------------------------------
    // Pantalla 1: Login
    // ------------------------------------------------------------------

    @Test
    fun loginValidoAbreNoticiasConElCorreoIngresado() {
        val actividad = Robolectric.buildActivity(LoginActivity::class.java).setup().get()

        actividad.findViewById<android.widget.EditText>(R.id.campoCorreo).setText(correoValido)
        actividad.findViewById<android.widget.EditText>(R.id.campoPassword).setText(CLAVE_VALIDA)
        actividad.findViewById<View>(R.id.botonIngresar).performClick()

        // ---- INTENT EXPLICITO ---------------------------------------
        val destino = shadowOf(actividad).nextStartedActivity
        assertNotNull("El login valido debe navegar a Noticias", destino)
        assertEquals(NoticiasActivity::class.java.name, destino!!.component?.className)
        assertEquals(correoValido, destino.getStringExtra(NoticiasActivity.EXTRA_CORREO))
        assertTrue("El login debe cerrarse al navegar", actividad.isFinishing)
    }

    @Test
    fun loginSinCamposNoNavegaYMuestraError() {
        val actividad = Robolectric.buildActivity(LoginActivity::class.java).setup().get()

        actividad.findViewById<View>(R.id.botonIngresar).performClick()

        assertNull("No debe navegar con el formulario vacio", shadowOf(actividad).nextStartedActivity)
        assertEquals(
            actividad.getString(R.string.login_error_correo),
            actividad.findViewById<TextInputLayout>(R.id.panelCorreo).error
        )
    }

    @Test
    fun loginConCorreoInvalidoNoNavega() {
        val actividad = Robolectric.buildActivity(LoginActivity::class.java).setup().get()

        actividad.findViewById<android.widget.EditText>(R.id.campoCorreo).setText("no-es-un-correo")
        actividad.findViewById<android.widget.EditText>(R.id.campoPassword).setText(CLAVE_VALIDA)
        actividad.findViewById<View>(R.id.botonIngresar).performClick()

        assertNull(shadowOf(actividad).nextStartedActivity)
        assertEquals(
            actividad.getString(R.string.login_error_correo_invalido),
            actividad.findViewById<TextInputLayout>(R.id.panelCorreo).error
        )
    }

    // ------------------------------------------------------------------
    // Pantalla 2: Noticias
    // ------------------------------------------------------------------

    @Test
    fun noticiasMuestranElCatalogoCompleto() {
        val actividad = Robolectric.buildActivity(NoticiasActivity::class.java).setup().get()
        val lista = actividad.findViewById<RecyclerView>(R.id.listaNoticias)

        assertNotNull(lista.adapter)
        assertEquals(6, lista.adapter!!.itemCount)
    }

    @Test
    fun botonCompartirAbreElSelectorDeAplicaciones() {
        val actividad = Robolectric.buildActivity(NoticiasActivity::class.java).setup().get()

        actividad.findViewById<View>(R.id.botonCompartir).performClick()

        // ---- INTENT IMPLICITO ACTION_SEND ----------------------------
        val selector = shadowOf(actividad).nextStartedActivity
        assertNotNull("El boton flotante debe abrir el selector", selector)
        assertEquals(Intent.ACTION_CHOOSER, selector!!.action)

        @Suppress("DEPRECATION")
        val enviar = selector.getParcelableExtra<Intent>(Intent.EXTRA_INTENT)
        assertNotNull("El selector debe llevar el intent de compartir", enviar)
        assertEquals(Intent.ACTION_SEND, enviar!!.action)
        assertEquals("text/plain", enviar.type)
        assertTrue(
            "El texto compartido debe contener el titulo",
            enviar.getStringExtra(Intent.EXTRA_TEXT)!!.contains("Apple Vision Pro")
        )
    }

    @Test
    fun tocarUnaNoticiaLaSeleccionaParaCompartir() {
        val actividad = Robolectric.buildActivity(NoticiasActivity::class.java).setup().get()
        val lista = actividad.findViewById<RecyclerView>(R.id.listaNoticias)
        lista.measure(0, 0)
        lista.layout(0, 0, 1080, 4000)

        val segundaTarjeta = lista.getChildAt(1) ?: return
        segundaTarjeta.performClick()

        // La noticia seleccionada se comparte desde el boton flotante.
        actividad.findViewById<View>(R.id.botonCompartir).performClick()

        val selector = shadowOf(actividad).nextStartedActivity
        assertNotNull(selector)

        @Suppress("DEPRECATION")
        val enviar = selector!!.getParcelableExtra<Intent>(Intent.EXTRA_INTENT)
        assertTrue(
            "Debe compartirse la noticia tocada",
            enviar!!.getStringExtra(Intent.EXTRA_TEXT)!!.contains("Meta Glasses")
        )
    }

    @Test
    fun menuDeOpcionesDeNoticiasAbreElPerfil() {
        val actividad = Robolectric.buildActivity(NoticiasActivity::class.java).setup().get()

        // ---- MENU DE OPCIONES -> INTENT EXPLICITO --------------------
        shadowOf(actividad).clickMenuItem(R.id.actionPerfil)

        val destino = shadowOf(actividad).nextStartedActivity
        assertNotNull(destino)
        assertEquals(PerfilActivity::class.java.name, destino!!.component?.className)
    }

    // ------------------------------------------------------------------
    // Pantalla 3: Perfil
    // ------------------------------------------------------------------

    @Test
    fun menuDeOpcionesDePerfilNavegaAPrivacidadYContactos() {
        val actividad = Robolectric.buildActivity(PerfilActivity::class.java).setup().get()

        // ---- MENU DE OPCIONES: desplegable "Privacidad" ---------------
        shadowOf(actividad).clickMenuItem(R.id.menuPrivacidad)
        assertEquals(
            PrivacidadActivity::class.java.name,
            shadowOf(actividad).nextStartedActivity?.component?.className
        )

        // ---- MENU DE OPCIONES: desplegable "Contactos" ----------------
        shadowOf(actividad).clickMenuItem(R.id.menuContactos)
        assertEquals(
            ContactosActivity::class.java.name,
            shadowOf(actividad).nextStartedActivity?.component?.className
        )
    }

    @Test
    fun iconoDeCorreoDelPerfilAbreElClienteDeCorreo() {
        val actividad = Robolectric.buildActivity(PerfilActivity::class.java).setup().get()

        shadowOf(actividad).clickMenuItem(R.id.actionCorreo)

        // ---- INTENT IMPLICITO ACTION_SENDTO --------------------------
        val correo = shadowOf(actividad).nextStartedActivity
        assertNotNull(correo)
        assertEquals(Intent.ACTION_SENDTO, correo!!.action)
        assertEquals("mailto:redaccion@news.com", correo.data.toString())
    }

    @Test
    fun cerrarSesionVuelveAlLogin() {
        val actividad = Robolectric.buildActivity(PerfilActivity::class.java).setup().get()

        actividad.findViewById<View>(R.id.botonCerrarSesion).performClick()

        val destino = shadowOf(actividad).nextStartedActivity
        assertNotNull(destino)
        assertEquals(LoginActivity::class.java.name, destino!!.component?.className)
        assertTrue(destino.flags and Intent.FLAG_ACTIVITY_CLEAR_TASK != 0)
    }

    // ------------------------------------------------------------------
    // Pantalla 4 y 5: Privacidad y Contactos
    // ------------------------------------------------------------------

    @Test
    fun privacidadMuestraLasReglas() {
        val actividad = Robolectric.buildActivity(PrivacidadActivity::class.java).setup().get()

        val encabezado = actividad.findViewById<TextView>(R.id.encabezadoPrivacidad)
        val puntoUno = actividad.findViewById<TextView>(R.id.politicaPuntoUno)
        val puntoDos = actividad.findViewById<TextView>(R.id.politicaPuntoDos)

        assertEquals(actividad.getString(R.string.privacidad_encabezado), encabezado.text.toString())
        assertTrue(puntoUno.text.isNotBlank())
        assertTrue(puntoDos.text.isNotBlank())
    }

    @Test
    fun menuDeOpcionesDePrivacidadMuestraAcercaDe() {
        val actividad = Robolectric.buildActivity(PrivacidadActivity::class.java).setup().get()

        // El desplegable de tres puntos debe responder.
        shadowOf(actividad).clickMenuItem(R.id.menuAcerca)

        assertNotNull(ShadowDialog.getLatestDialog())
    }

    @Test
    fun contactosCarganElListadoYElMenuContextual() {
        val actividad = Robolectric.buildActivity(ContactosActivity::class.java).setup().get()
        val lista = actividad.findViewById<RecyclerView>(R.id.listaContactos)

        val adaptador = lista.adapter as AdaptadorContactos
        assertEquals(5, adaptador.itemCount)

        val contacto = adaptador.contactoEn(0)
        assertNotNull(contacto)
        assertEquals("Prensa Editorial: +51 987 987 987", contacto!!.etiqueta)

        // El contacto debe quedar disponible en el tag de la fila para el
        // menu contextual.
        lista.measure(0, 0)
        lista.layout(0, 0, 1080, 3000)
        val primeraFila = lista.getChildAt(0)
        assertNotNull("La lista debe dibujar al menos una fila", primeraFila)
        assertEquals(contacto, primeraFila!!.tag)

        // Mantener presionado la fila debe abrir el menu contextual.
        assertTrue(primeraFila.performLongClick())
    }

    @Test
    fun contactoEnPosicionInvalidaDevuelveNull() {
        val actividad = Robolectric.buildActivity(ContactosActivity::class.java).setup().get()
        val adaptador = actividad.findViewById<RecyclerView>(R.id.listaContactos)
            .adapter as AdaptadorContactos

        assertNull(adaptador.contactoEn(99))
        assertNull(adaptador.contactoEn(-1))
    }
}