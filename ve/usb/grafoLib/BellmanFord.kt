/**
 * Autor: Christopher Gómez.
 * Fecha: 6/Dic/2021.
 */

package ve.usb.grafoLib

import kotlin.Double.Companion.POSITIVE_INFINITY
import kotlin.Double.Companion.NEGATIVE_INFINITY
import java.util.LinkedList

/**
 * Implementación del algoritmo de Bellman-Ford que encuentra los
 * caminos de costo mínimo desde un vértice fuente [s].
 *
 * Se crea el árbol de caminos de costo mínimo con la creación de
 * una instancia.
 *
 * @param [g]: dígrafo sobre el que se ejecuta el algoritmo.
 * @param [s]: vértice fuente desde el que encontrarán los caminos
 *             de costo mínimo.
 *
 * @throws [RuntimeException] El vértice fuente [s] no pertenece al conjunto
 *                            de vértices del grafo.
 *
 */
public class BellmanFord(val g: GrafoDirigido, val s: Int) {
    private val n = g.obtenerNumeroDeVertices()
    private val dist = DoubleArray(n) { POSITIVE_INFINITY }
    private val pred = Array<Arco?>(n) { null }
    private val vertices = mutableSetOf<Int>()

    init {
        g.chequearVertice(s)

        dist[s] = 0.0

        repeat(n - 1) { g.arcos().forEach { relajacion(it) } }

        /* Se guardan en un conjunto los vértices que todavía se
        pueden relajar. Hay ciclos negativos si el conjunto no
        queda vacío. */
        for (lado in g.arcos()) {
            val u = lado.fuente()
            val v = lado.sumidero()

            if (dist[v] > dist[u] + lado.peso()) {
                vertices.add(u)
            }
        }

        /* Si hay ciclos negativos se encuentran
        los vértices del ciclo */
        for (w in vertices) {
            var fuente = pred[w]!!.fuente()

            /* Se hace BFS desde uno de los vértices pertenecientes
            al ciclo negativo para actualizar las distacias de todos
            los vértices alcanzables desde él a -inf */
            if (dist[fuente] != NEGATIVE_INFINITY) {
                dist[fuente] = NEGATIVE_INFINITY
                
                val Q = LinkedList<Int>()
                Q.add(fuente)
                
                while (!Q.isEmpty()) {
                    val u = Q.poll()

                    g.adyacentes(u).forEach {
                        val v = it.elOtroVertice(u)
                        if (dist[v] != NEGATIVE_INFINITY) {
                            dist[v] = NEGATIVE_INFINITY
                            Q.add(v)
                        }
                    }
                }
            }
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
     * Retorna un booleano indicando si hay un ciclo negativo en el camino
     * hasta los vértices alcanzables desde s.
     *
     * Tiempo de ejecución: O(1).
     * Precondición: true.
     * Postcondición: [tieneCicloNegativo] es: - True si existe un ciclo negativo
     *                                           en el camino hasta los vértices
     *                                           alcanzables desde s.
     *                                         - False de otra forma.
     */
    fun tieneCicloNegativo(): Boolean = !vertices.isEmpty()

    /**
     * Retorna los lados que conforman el ciclo negativo, en caso de haberlo.
     *
     * Tiempo de ejecución: O(E) en el peor caso.
     * Precondición: true.
     * Postcondición: [obtenerCicloNegativo] es un objeto iterable con los lados
     *                que conforman un ciclo negativo alcanzable desde s.
     *                En caso de no tener, es un iterable sin elementos.
     */
    fun obtenerCicloNegativo(): Iterable<Arco> {
        // Se usa una pila para guardar la secuencia de arcos a retornar
        val ciclo = LinkedList<Arco>()

        if (tieneCicloNegativo()) {
            var v = pred[vertices.last()]!!
            var x = v.fuente()

            /* Se hace backtracking usando el arreglo de predecesores
            para encontrar el ciclo negativo */
            v = pred[v.fuente()]!!
            while (v.sumidero() != x || ciclo.size <= 1) {
                ciclo.addFirst(v)
                v = pred[v.fuente()]!!
            }
        }

        return ciclo
    }
        

    /**
     * Retorna un booleano indicando si el vértice v es alcanzable desde s
     * en un camino de costo mínimo.
     *
     * @throws [RuntimeException] El vértice v dado está fuera del 
     *                            intervalo [0..|V|).
     * 
     * Tiempo de ejecución: O(1).
     * Precondición: [v] pertenece al conjunto de vértices del dígrafo.
     * Postcondición: [existeUnCamino] es: - True si existe un camino de costo mínimo
     *                                       de [s] a [v].
     *                                     - False de otra forma.
     */
    fun existeUnCamino(v: Int): Boolean {
        g.chequearVertice(v)

        return dist[v] != POSITIVE_INFINITY && dist[v] != NEGATIVE_INFINITY
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

        if (existeUnCamino(v)) {
            while (u != null) {
                S.addFirst(u)
                u = pred[u.fuente()]
            }
        }

        return S
    }
}