package com.cibertec.noticias.adaptador

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cibertec.noticias.databinding.ItemContactoBinding
import com.cibertec.noticias.modelo.Contacto

/**
 * Adapter del listado de contactos.
 *
 * Las filas se registran con registerForContextMenu(), por eso el itemView
 * debe tener focusable y clickable en true para que el menu contextual de
 * Android se abra al mantener presionado.
 */
class AdaptadorContactos(
    private val contactos: List<Contacto>
) : RecyclerView.Adapter<AdaptadorContactos.CajaContacto>() {

    inner class CajaContacto(private val vinculo: ItemContactoBinding) :
        RecyclerView.ViewHolder(vinculo.root) {

        fun enlazar(contacto: Contacto) {
            vinculo.datosContacto.text = contacto.etiqueta

            // RecyclerView no genera un AdapterContextMenuInfo, por lo que el
            // contacto se deja en el tag de la fila para que el menu
            // contextual sepa sobre que item se abrio.
            vinculo.root.tag = contacto

            vinculo.root.setOnLongClickListener {
                vinculo.root.showContextMenu()
                true
            }
        }
    }

    override fun onCreateViewHolder(padre: ViewGroup, tipo: Int): CajaContacto {
        val vinculo = ItemContactoBinding.inflate(
            LayoutInflater.from(padre.context), padre, false
        )
        return CajaContacto(vinculo)
    }

    override fun onBindViewHolder(caja: CajaContacto, posicion: Int) {
        caja.enlazar(contactos[posicion])
    }

    override fun getItemCount(): Int = contactos.size

    /** Devuelve el contacto de una posicion, o null si la posicion ya no existe. */
    fun contactoEn(posicion: Int): Contacto? = contactos.getOrNull(posicion)
}