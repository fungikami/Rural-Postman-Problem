/**
 * Autor: Christopher Gómez.
 * Fecha: 6/Dic/2021.
 */

package ve.usb.grafoLib

import kotlin.Double.Companion.POSITIVE_INFINITY
import java.util.LinkedList

/**
 * Implementación de un algoritmo basado en orden topológico y el
 * procedimiento de relajación para hallar todos los caminos de costo
 * mínimo desde un vértice fuente [s] fijo en DAGs. 
 *
 * @param [g]: dígrafo sobre el que se ejecuta el algoritmo.
 * @param [s]: vértice fuente desde el que encontrarán los caminos
 *             de costo mínimo.
 *
 * @throws [RuntimeException] El vértice fuente [s] no pertenece al conjunto
 *                            de vértices del grafo.
 * @throws [RuntimeException] El dígrafo [g] no es DAG.
 */
public class CCMDAG(val g: GrafoDirigido, val s: Int) {
    private val n = g.obtenerNumeroDeVertices()
    private val dist = DoubleArray(n) { POSITIVE_INFINITY }
    private val pred = Array<Arco?>(n) { null }
    private var cicloNeg = false
    private var verticeFinalCicloNeg = 0

    init {
        g.chequearVertice(s)

        /* Si no es DAG el método ya se encarga de lanzar la excepción
        correspondiente. */
        val topSort = OrdenTopologico(g).obtenerOrdenTopologico()

        dist[s] = 0.0

        topSort.forEach { u ->
            g.adyacentes(u).forEach { relajacion(it) }
        }
    }

    /**
     * Ejecuta el procedimiento de relajación sobre el lado [p].
     *
     * Tiempo de ejecución: O(1).
     * Precondición: [p] es un arco (u, v) perteneciente al conjunto de
     *               lados de g.
     * Postcondición: dist[p.sumidero()] <= dist[p.sumidero()]0.            
     */
    private fun relajacion(p: Arco) {
        val u = p.fuente()
        val v = p.sumidero()

        if (dist[v] > dist[u] + p.peso()) {
            dist[v] = dist[u] + p.peso
            pred[v] = p
        }
    }

     /**
     * Retorna un booleano indicando si el vértice v es alcanzable desde s.
     *
     * @throws [RuntimeException] El vértice v dado está fuera del 
     *                            intervalo [0..|V|).
     * 
     * Tiempo de ejecución: O(1).
     * Precondición: [v] pertenece al conjunto de vértices del dígrafo.
     * Postcondición: [existeUnCamino] es: - True si existe un camino de [s] a [v].
     *                                     - False de otra forma.
     */
    fun existeUnCamino(v: Int): Boolean {
        g.chequearVertice(v)

        return dist[v] != POSITIVE_INFINITY
    }


    /**
     * Retorna el costo del camino de costo mínimo de s a v
     *
     * @throws [RuntimeException] El vértice v dado está fuera del 
     *                            intervalo [0..|V|).
     * 
     * Tiempo de ejecución: O(1).
     * Precondición: [v] pertenece al conjunto de vértices del dígrafo.
     * Postcondición: [costoHasta] es un real con el costo del camino
     *                de costo mínimo de [s] a [v]
     */
    fun costoHasta(v: Int): Double {
        g.chequearVertice(v)

        return dist[v]
    }

    /** 
     * Retorna el camino de costo mínimo desde s hasta v.
     *
     * @throws [RuntimeException] El vértice v dado está fuera del 
     *                            intervalo [0..|V|).
     * 
     * Tiempo de ejecución: O(E) en el peor caso.
     * Precondición: [v] pertenece al conjunto de vértices del dígrafo.
     * Postcondición: [obtenerCaminoDeCostoMinimo] es un objeto iterable cuyos
     *                elementos son los arcos que conforman el camino de costo
     *                mínimo desde [s] hasta [v] en orden.
     */
    fun obtenerCaminoDeCostoMinimo(v: Int): Iterable<Arco> {
        g.chequearVertice(v)

        // Se usa una pila para guardar la secuencia de arcos a retornar
        val S = LinkedList<Arco>()
        var u = pred[v]

        while (u != null){
            S.addFirst(u)
            u = pred[u.fuente()]
        }
        
        return S
    }
}
