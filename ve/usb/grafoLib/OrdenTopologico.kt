/**
 * Autor: Christopher Gómez.
 * Fecha: 8/nov/2021
 */

package ve.usb.grafoLib

import java.util.LinkedList

/**
 * Implementación del algoritmo que determina el orden topológico
 * de un DAG.
 * 
 * Ejecuta el algoritmo DGF sobre el grafo [g] cuando se crea una
 * nueva instancia. Si el grafo [g] no es DAG, detiene la búsqueda,
 * en caso contrario, almacena el orden decreciente de los tiempos
 * en que lo vértices son terminados de visitar.
 * 
 * @param [g]: dígrafo sobre el que se ejecuta el algoritmo.
 */
public class OrdenTopologico(val g: GrafoDirigido) {
    private val n = g.obtenerNumeroDeVertices()
    private val color = Array<Color>(n) { Color.BLANCO }
    private val topSort = LinkedList<Int>()
    private var hayCiclo = false

    init {
	    // Algoritmo DFS
        for (v in 0 until n) if (color[v] == Color.BLANCO) dfsVisit(g, v)
    }

    /**
     * Explora recursivamente todos los vértices alcanzables desde [u]
     * en el grafo [g] hasta encontrar un ciclo, de haber uno.
     * 
     * dfsVisit modificado para guardar en una pila los vértices
     * a medida que se terminan de visitar, sin almacenar tiempos. 
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
                    dfsVisit(g, v)
                } else if (color[v] == Color.GRIS) {
                    hayCiclo = true
                    break
                }
            }
        }

        // Se termina de explorar u
        color[u] = Color.NEGRO
        topSort.addFirst(u)
    }

    /**
     * Retorna un booleano que indica si el grafo g es un DAG.
     * 
     * Tiempo de ejecución: O(1).
     * Precondición: true.
     * Postcondición: [esUnGrafoAciclicoDirecto] es: - True si el grafo g es un DAG.
     *                                               - False de lo contrario.
     */
    fun esUnGrafoAciclicoDirecto(): Boolean = !hayCiclo
    
    /**
     * Retorna el ordenamiento topológico del grafo g.
     * 
     * @throws [RuntimeException] El grafo g no es un DAG.
     * 
     * Tiempo de ejecución: O(1).
     * Precondición: true.
     * Postcondición: [obtenerOrdenTopologico] es un ordenamiento
     *                topológico del grafo g.
     */
    fun obtenerOrdenTopologico(): Iterable<Int> {
        if (!esUnGrafoAciclicoDirecto()) {
            throw RuntimeException("Error: El dígrafo no es un DAG.")
        }

        return topSort
    }
}