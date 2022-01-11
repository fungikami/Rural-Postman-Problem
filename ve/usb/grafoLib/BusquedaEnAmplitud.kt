/**
 * Autor: Christopher Gómez
 * Fecha: 01/Nov/2021
 */

package ve.usb.grafoLib

import java.util.LinkedList
import kotlin.Double.Companion.POSITIVE_INFINITY

/** 
 * Implementación del algoritmo sobre grafos Breadth-First Search.
 * 
 * Ejecuta el algoritmo BFS tomando al vértice [s] como fuente
 * cuando se crea una instancia.
 * 
 * @param [g]: grafo sobre el que se ejecuta el algoritmo.
 * @param [s]: Entero no negativo que represente el vértice fuente.
 * 
 * @throws [RuntimeException] El vértice fuente [s] no pertenece al conjunto
 *                            de vértices del grafo.
 */
public class BusquedaEnAmplitud(val g: Grafo, val s: Int) {
    
    // Propiedades de los vértices del grafo
    private val n = g.obtenerNumeroDeVertices()
    private val color = Array<Color>(n) { Color.BLANCO }
    private val dist = IntArray(n) { POSITIVE_INFINITY.toInt() }
    private val pred = Array<Int?>(n) { null }

    init {
        if (s < 0 || s >= n) {
            throw RuntimeException("El vértice $s no pertenece al grafo.")
        }

        // Algoritmo BFS
        dist[s] = 0
        color[s] = Color.GRIS
        val Q = LinkedList<Int>()
        Q.add(s)
        
        while (!Q.isEmpty()) {
            val u = Q.poll()

            g.adyacentes(u).forEach {
                // Se selecciona el adyacente
                val v = it.elOtroVertice(u)

                if (color[v] == Color.BLANCO) {
                    color[v] = Color.GRIS
                    dist[v] = dist[u] + 1
                    pred[v] = u
                    Q.add(v)
                }
            }
            color[u] = Color.NEGRO
        }
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
     * Retorna la distancia, del camino con menos lados, desde el vértice inicial s 
     * hasta el un vértice v. 
     *
     * @throws [RuntimeException] El vértice está fuera del intervalo [0..|V|).
     * 
     * Tiempo de ejecución: O(1).
     * Precondición: [v] pertenece al conjunto de vértices del grafo.
     * Postcondición: [obtenerDistancia] es un entero con la longitud del camino con
     *                menos lados desde [s] hasta [v], o POSITIVE_INFINITY.toInt() si
     *                ese camino no existe.
     */
    fun obtenerDistancia(v: Int): Int {
        chequearVertice(v)
        return dist[v]
    }

    /**
     * Indica si hay camino desde el vértice inicial [s] hasta el un vértice [v].
     *
     * @throws [RuntimeException] El vértice está fuera del intervalo [0..|V|).
     * 
     * Tiempo de ejecución: O(1).
     * Precondición: [v] pertenece al conjunto de vértices del grafo.
     * Postcondición: [hayCaminoHasta] es -True si hay un camino de [s] a [v].
     *                                    -False de otra forma. 
     */
    fun hayCaminoHasta(v: Int): Boolean {
        chequearVertice(v)
        return pred[v] != null
    }
    
    /**
     * Retorna el camino con menos lados desde el vértice inicial [s] 
     * hasta el un vértice [v].
     * 
     * @throws [RuntimeException] El vértice está fuera del intervalo [0..|V|).
     * 
     * Tiempo de ejecución: O(|V|) en el peor caso.
     * Precondición: [v] pertenece al conjunto de vértices del grafo.
     * Postcondición: [caminoConMenosLadosHasta] es un objeto Iterable con la
     *                secuencia de vértices correspondientes al camino más
     *                corto entre [s] y [v].
     */
    fun caminoConMenosLadosHasta(v: Int): Iterable<Int>  {
        chequearVertice(v)
        
        // Se usa una pila para guardar la secuencia de vértices a retornar
        val S = LinkedList<Int>()
        var u: Int? = v

        while (pred[u!!] != null){
            S.addFirst(u)
            u = pred[u]
            if (u == s) S.addFirst(u)
        }
        return S
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