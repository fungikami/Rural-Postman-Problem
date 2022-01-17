/**
 * Autor: Christopher Gómez
 * Fecha: 01/Nov/2021
 */

package ve.usb.grafoLib

/** 
 * Implementación del algoritmo Depth-First Search.
 *  
 * Ejecuta el algoritmo DFS tomando al vértice [s] desde todos
 * los vértices del grafo cuando se crea una instancia.
 * 
 * @param [g]: grafo sobre el que se ejecuta el algoritmo.
 */
public class BusquedaEnProfundidad(val g: Grafo) {

    // Propiedades de los vértices del grafo
    private val n = g.obtenerNumeroDeVertices()
    private val color = Array<Color>(n) { Color.BLANCO }
    private val pred = Array<Int?>(n) { null }
    private val ti = IntArray(n)
    private val tf = IntArray(n)
    private var tiempo = 0

    init {
	    // Algoritmo DFS

        for (v in 0 until n) {
            if (color[v] == Color.BLANCO){
                dfsVisit(g, v)
            }
        }
    }

    /**
     * Explora recursivamente todos los vértices alcanzables desde [u]
     * en el grafo [g].
     * 
     * Tiempo de ejecución: O(|E|).
     * Precondición: [g] es un grafo.
     *               [u] es un vértice perteneciente al grafo.
     * Postcondición: true
     */
    private fun dfsVisit(g: Grafo, u: Int) {
        // Se empieza a explorar u
        tiempo++
        ti[u] = tiempo
        color[u] = Color.GRIS

        g.adyacentes(u).forEach {
            // Se selecciona el adyacente
            val v = it.elOtroVertice(u)

            if (color[v] == Color.BLANCO) {
                pred[v] = u
                dfsVisit(g, v)
            }
        }

        // Se termina de explorar u
        color[u] = Color.NEGRO
        tiempo++
        tf[u] = tiempo
    }

    /**
     * Retorna el predecesor de un vértice [v], o null si el vértice no
     * tiene predecesor.
     * 
     * @throws [RuntimeException] El vértice está fuera del intervalo [0..|V|).
     *
     * Tiempo de ejecución: O(1).
     * Precondición: [v] pertenece al conjunto de vértices del grafo.
     * Postcondición: [obtenerPredecesor] es un entero con el predecesor de
     *                [v], o null si [v] no tiene predecesor.
     */   
    fun obtenerPredecesor(v: Int): Int? {
        chequearVertice(v)
        return pred[v]
    }

    /**
     * Retorna un par con el tiempo inicial y final de un vértice durante la
     * ejecución de DFS. 
     *
     * @throws [RuntimeException] El vértice está fuera del intervalo [0..|V|).
     * 
     * Tiempo de ejecución: O(1).
     * Precondición: [v] pertenece al conjunto de vértices del grafo.
     * Postcondición: [obtenerTiempos] es un Par con el tiempo inicial 
     *                y final en que el vértice fue visitado en la ejecución
     *                de DFS.
     */
    fun obtenerTiempos(v: Int): Pair<Int, Int> {
        chequearVertice(v)
        return Pair(ti[v], tf[v])
    }

    /** 
     * Verifica que el vértice esté en el intervalo [0..|V|). Lanza una excepción en caso
     * de que no.
     *
     * @throws [RuntimeException] El vértice está fuera del intervalo [0..|V|).
     * 
     * Tiempo de ejecución: O(1)
     * Precondición: [v] es un entero.
     * Postcondición: this = this0.
     */
    private fun chequearVertice(v: Int) {
        if (v < 0 || v >= n) {
            throw RuntimeException("El vértice $v no pertenece al grafo.")
        }
    }
}