package com.cibertec.noticias.adaptador

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.cibertec.noticias.R
import com.cibertec.noticias.databinding.ItemNoticiaBinding
import com.cibertec.noticias.modelo.Noticia
import kotlin.math.roundToInt

/**
 * Adapter del RecyclerView de noticias.
 *
 * Cada tarjeta se puede tocar para seleccionarla (es la noticia que enviara
 * el boton flotante de compartir) o mantener presionada para compartirla
 * de inmediato.
 *
 * @param alSeleccionar se invoca al tocar una tarjeta.
 * @param alCompartir   se invoca al mantener presionada una tarjeta.
 */
class AdaptadorNoticias(
    noticias: List<Noticia>,
    private val alSeleccionar: (Noticia) -> Unit,
    private val alCompartir: (Noticia) -> Unit
) : RecyclerView.Adapter<AdaptadorNoticias.CajaNoticia>() {

    private val items: MutableList<Noticia> = noticias.toMutableList()
    private var idSeleccionada: Int? = items.firstOrNull()?.id

    /** Reemplaza el contenido de la lista (se usa al buscar). */
    fun mostrar(noticias: List<Noticia>) {
        items.clear()
        items.addAll(noticias)
        if (items.none { it.id == idSeleccionada }) {
            idSeleccionada = items.firstOrNull()?.id
        }
        notifyDataSetChanged()
    }

    /** Marca una tarjeta como seleccionada y notifica el cambio a la vista. */
    fun seleccionar(noticia: Noticia) {
        if (idSeleccionada == noticia.id) return
        val anterior = idSeleccionada
        idSeleccionada = noticia.id
        listOf(anterior, noticia.id).forEach { id ->
            val posicion = items.indexOfFirst { it.id == id }
            if (posicion >= 0) notifyItemChanged(posicion)
        }
    }

    inner class CajaNoticia(private val vinculo: ItemNoticiaBinding) :
        RecyclerView.ViewHolder(vinculo.root) {

        fun enlazar(noticia: Noticia) {
            val contexto = vinculo.root.context
            val densidad = contexto.resources.displayMetrics.density

            vinculo.imagenNoticia.setImageResource(noticia.imagen)
            vinculo.imagenNoticia.contentDescription = noticia.titulo
            vinculo.tituloNoticia.text = noticia.titulo
            vinculo.resumenNoticia.text = noticia.resumen

            // Resalta la tarjeta seleccionada con un borde turquesa.
            val seleccionada = noticia.id == idSeleccionada
            vinculo.tarjetaNoticia.strokeWidth = if (seleccionada) (2 * densidad).roundToInt() else 0
            vinculo.tarjetaNoticia.strokeColor =
                ContextCompat.getColor(contexto, R.color.cyan_primario)

            vinculo.root.setOnClickListener { alSeleccionar(noticia) }
            vinculo.root.setOnLongClickListener {
                alCompartir(noticia)
                true
            }
        }
    }

    override fun onCreateViewHolder(padre: ViewGroup, tipo: Int): CajaNoticia {
        val vinculo = ItemNoticiaBinding.inflate(
            LayoutInflater.from(padre.context), padre, false
        )
        return CajaNoticia(vinculo)
    }

    override fun onBindViewHolder(caja: CajaNoticia, posicion: Int) {
        caja.enlazar(items[posicion])
    }

    override fun getItemCount(): Int = items.size
}