/**
 * Autor: Christopher Gómez.
 * Fecha: 8/nov/2021
 */

package ve.usb.grafoLib

import java.util.LinkedList

/**
 * Implementación del algoritmo de DFS para determinar la
 * existencia o no de un ciclo en un dígrafo.
 * 
 * El algoritmo se ejecuta en el momento que se crea una 
 * instancia de la clase.
 * 
 * @param [g]: dígrafo sobre el que se ejecuta el algoritmo.
 */
public class CicloDigrafo(val g: GrafoDirigido) {
    private val n = g.obtenerNumeroDeVertices()
    private val color = Array<Color>(n) { Color.BLANCO }
    private val pred = Array<Int?>(n) { null }
    private var inicio: Int?= null
    private var fin: Int? = null
    private var hayCiclo = false

    init {
	    // Algoritmo DFS
        for (v in 0 until n) if (color[v] == Color.BLANCO) dfsVisit(g, v)
    }

    /**
     * Explora recursivamente todos los vértices alcanzables desde [u]
     * en el grafo [g] hasta encontrar un ciclo, de haber uno.
     * 
     * dfsVisit modificado para hallar el vértice inicial y final de un
     * ciclo, si existe.
     * 
     * Tiempo de ejecución: O(|E|).
     * Precondición: [g] es un grafo.
     *               [u] es un vértice perteneciente al grafo.
     * Postcondición: true
     */
    private fun dfsVisit(g: Grafo, u: Int) {
        // Se empieza a explorar u

        /* Si ya se encontró un ciclo se deja de
        hacer DFS. */ 
        if (!hayCiclo) {
            color[u] = Color.GRIS

            for (lado in g.adyacentes(u)) {
                // Se selecciona el adyacente
                val v = lado.elOtroVertice(u)

                if (color[v] == Color.BLANCO) {
                    pred[v] = u
                    dfsVisit(g, v)
                } else if (color[v] == Color.GRIS) {
                    /* El vértice ya fue visitado y es
                    un back edge. Ciclo hallado. */
                    inicio = v
                    fin = u
                    hayCiclo = true
                    break
                }
            }
        }

        // Se termina de explorar u
        color[u] = Color.NEGRO
    }
    
    /** 
     * Retorna un objeto iterable con la secuencia de vértices del
     * ciclo.
     * 
     * @throws [RuntimeException] El dígrafo es acíclico.
     * 
     * Tiempo de ejecución: O(|V|) en el peor caso.
     * Precondición: true.
     * Postcondición: [cicloEncontrado] es un objecto iterable con
     *                una secuencia de vértices que representan el
     *                primer ciclo hallado en g.
     */
    fun cicloEncontrado(): Iterable<Int> {
        if (!hayCiclo) {
            throw RuntimeException("Error: El dígrafo es acíclico.")
        }

        // Se usa una pila para guardar la secuencia de vértices a retornar
        val S = LinkedList<Int>()
        var u = fin!!

        while (u != inicio){
            S.addFirst(u)
            u = pred[u]!!
        }
        S.addFirst(inicio)
        
        return S
    }

    /**
     * Retorna un booleano indicando si existe un ciclo en el dígrafo g.
     * 
     * Tiempo de ejecución: O(1).
     * Precondición: true.
     * Postcondición: [existeUnCiclo] es: -True si existe un ciclo en g.
     *                                    -False en caso contrario.
     */
    fun existeUnCiclo(): Boolean = hayCiclo
}