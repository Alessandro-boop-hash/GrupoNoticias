package com.cibertec.noticias

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import com.cibertec.noticias.base.BaseActivity
import com.cibertec.noticias.databinding.ActivityLoginBinding

/**
 * Pantalla de inicio de sesion (simulado).
 *
 * Valida el formulario y, cuando es correcto, abre la pantalla de Noticias
 * mediante un INTENT EXPLICITO (constructor Intent(this, Clase::class.java)).
 */
class LoginActivity : BaseActivity() {

    private lateinit var vinculo: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vinculo = ActivityLoginBinding.inflate(layoutInflater)
        setLayout(vinculo.root)
        registrarEventos()
    }

    private fun registrarEventos() {
        vinculo.botonIngresar.setOnClickListener { ingresar() }
        vinculo.botonRegistrar.setOnClickListener { registrar() }

        // El teclado_virtual tambien dispara el ingreso con la tecla "Enter".
        vinculo.campoPassword.setOnEditorActionListener { vista, accionId, _ ->
            if (accionId == EditorInfo.IME_ACTION_DONE) {
                ingresar()
                true
            } else {
                false
            }
        }
    }

    /** Valida los campos y, si todo esta correcto, navega a Noticias. */
    private fun ingresar() {
        val correo = vinculo.campoCorreo.text?.toString()?.trim().orEmpty()
        val clave = vinculo.campoPassword.text?.toString()?.trim().orEmpty()

        if (!validarFormulario(correo, clave)) return

        Toast.makeText(
            this, getString(R.string.login_bienvenida, correo), Toast.LENGTH_SHORT
        ).show()

        // ---- INTENT EXPLICITO: Login -> Noticias -------------------------
        val destino = Intent(this, NoticiasActivity::class.java).apply {
            putExtra(NoticiasActivity.EXTRA_CORREO, correo)
        }
        startActivity(destino)
        finish()
    }

    /** El registro tambien es simulado: solo informa que la cuenta quedo creada. */
    private fun registrar() {
        val correo = vinculo.campoCorreo.text?.toString()?.trim().orEmpty()
        if (correo.isEmpty()) {
            vinculo.panelCorreo.error = getString(R.string.login_error_correo)
            return
        }
        vinculo.panelCorreo.error = null
        Toast.makeText(
            this, getString(R.string.login_registro_simulado, correo), Toast.LENGTH_LONG
        ).show()
    }

    /** Devuelve true cuando correo y contrasena tienen un formato aceptable. */
    private fun validarFormulario(correo: String, clave: String): Boolean {
        var correcto = true

        vinculo.panelCorreo.error = null
        vinculo.panelPassword.error = null

        when {
            correo.isEmpty() -> {
                vinculo.panelCorreo.error = getString(R.string.login_error_correo)
                correcto = false
            }

            !Patterns.EMAIL_ADDRESS.matcher(correo).matches() -> {
                vinculo.panelCorreo.error = getString(R.string.login_error_correo_invalido)
                correcto = false
            }
        }

        if (clave.isEmpty()) {
            vinculo.panelPassword.error = getString(R.string.login_error_password)
            correcto = false
        }

        if (!correcto) {
            Toast.makeText(
                this, getString(R.string.login_error_correo), Toast.LENGTH_SHORT
            ).show()
        }
        return correcto
    }
}