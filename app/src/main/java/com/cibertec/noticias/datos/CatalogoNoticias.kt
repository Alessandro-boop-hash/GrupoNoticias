package com.cibertec.noticias.datos

import com.cibertec.noticias.R
import com.cibertec.noticias.modelo.Noticia

/**
 * Catalogo fijo de noticias.
 *
 * En esta practica los datos estan simulados en memoria; en una version
 * real se reemplazaria por una llamada a la API periodistica.
 */
object CatalogoNoticias {

    fun listar(): List<Noticia> = listOf(
        Noticia(
            id = 1,
            titulo = "Apple Vision Pro",
            resumen = "The $3,500 \"spatial\" computer from the creator of Big Sur is a " +
                "game-changing step forward for AR. Though it lacks \"killer\" apps, this " +
                "Meta Quest rival sets a high bar for mixed reality headsets.",
            imagen = R.drawable.img_noticia_1
        ),
        Noticia(
            id = 2,
            titulo = "Meta Glasses",
            resumen = "The $700 \"portal\" wearable on the quest of Big Sur is a game-changer " +
                "for those looking to capture and analyze their daily routines, with lenses " +
                "that put photos, videos and the assistant on your face.",
            imagen = R.drawable.img_noticia_2
        ),
        Noticia(
            id = 3,
            titulo = "Notion",
            resumen = "Notion Mail is rolling out to everyone who waited on the list: " +
                "threads, calendar events and files now arrive in a single inbox.",
            imagen = R.drawable.img_noticia_1
        ),
        Noticia(
            id = 4,
            titulo = "Dan",
            resumen = "Dan Rosenberg previews the redesigned calendar view, where every " +
                "meeting is linked to the notes and tasks it generated.",
            imagen = R.drawable.img_noticia_2
        ),
        Noticia(
            id = 5,
            titulo = "Messial",
            resumen = "Messial raises $2M to bring AI agents to your inbox and let them " +
                "answer on your behalf while you are offline.",
            imagen = R.drawable.img_noticia_1
        ),
        Noticia(
            id = 6,
            titulo = "Jason",
            resumen = "Jason Calacanis on the future of no-code AI workflows: the gap between " +
                "a prototype and a shipped product keeps growing.",
            imagen = R.drawable.img_noticia_2
        )
    )
}