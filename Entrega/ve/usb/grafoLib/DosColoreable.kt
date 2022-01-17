/**
 * Autor: Ka Fung & Christopher Gómez
 * Fecha: 30/Nov/2021.
 */

package ve.usb.grafoLib

/**
 * Implementación de un algoritmo basado en Búsqueda en Profundidad
 * para determinar si un grafo dado es bipartito.
 * 
 * Se determina si un grafo es 2-coloreable o bipartito
 * al momento que se crea una instancia de la clase.
 * 
 * @param [g]: Grafo no dirigido sobre el que se ejecuta el algoritmo.
 */
public class DosColoreable(val g: GrafoNoDirigido) {
    // Propiedades de los vértices del grafo
    private val n = g.obtenerNumeroDeVertices()
    private val color = Array<Color>(n) { Color.BLANCO }
    private val bipartitoColor = BooleanArray(n)
    private var bipartito = true

    init {
        for (v in 0 until n) {
            if (color[v] == Color.BLANCO) {
                bipartito = bipartito && dfsVisit(g, v)
            }
        }
    }

    /**
     * Explora recursivamente todos los vértices alcanzables desde [u]
     * en el grafo [g].
     *
     * dfsVisit modificado para determinar si un grafo es bipartito.
     * 
     * Tiempo de ejecución: O(|E|).
     * Precondición: [g] es un grafo.
     *               [u] es un vértice perteneciente al grafo.
     * Postcondición: true
     */
    private fun dfsVisit(g: Grafo, u: Int): Boolean {
        // Se empieza a explorar u
        color[u] = Color.GRIS

        g.adyacentes(u).forEach {
            // Se selecciona el adyacente
            val v = it.elOtroVertice(u)

            if (color[v] == Color.BLANCO) {
                /* Si no se ha visitado el vértice se le
                colorea con el color opuesto al predecesor. */
                bipartitoColor[v] = !bipartitoColor[u]
                
                if (!dfsVisit(g, v)) return false
            } else if (bipartitoColor[u] == bipartitoColor[v]) {
                return false
            }
        }

        // Se termina de explorar u
        color[u] = Color.NEGRO
        return true
    }

    /**
     * Retorna un booleano indicando si [g] es un grafo bipartito.
     * Tiempo de ejecución: O(1).
     * Precondición: true.
     * Postcondición: [esDosColoreable] es -True si [g] cumple con las 
     *                                      propiedades de un grafo bipartito.
     *                                     -False de otra forma.
     */
    fun esDosColoreable(): Boolean = bipartito
}
