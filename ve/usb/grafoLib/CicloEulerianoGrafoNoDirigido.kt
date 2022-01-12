/**
 * Autor: Ka Fung & Christopher Gómez
 * Fecha: 16/Ene/2021.
 */

package ve.usb.grafoLib
import java.util.LinkedList

/**
 * Clase que determina si el grafo no dirigido [g] posee un ciclo euleriano, y obtiene
 * el ciclo euleriano en caso afirmativo.
 * 
 * @throws [RuntimeException] Si el grafo [g] no es conexo.
 * 
 * @param [g]: Grafo no dirigido sobre el que se ejecuta el algoritmo.
 */
public class CicloEulerianoGrafoNoDirigido(val g: GrafoNoDirigido) {
    private val n = g.obtenerNumeroDeVertices()
    private val color = Array<Color>(n) { Color.BLANCO }

    private var euleriano = true
    private var ladosVisitados = mutableSetOf<Arista>()
    private var cicloEuler = Array<Arista?>(g.obtenerNumeroDeLados()) { null }
    private var cicloEulerIndex = g.obtenerNumeroDeLados() - 1

    init {
        if (!esConexo(g)) throw RuntimeException("El grafo no es conexo.")

        // Obtiene los aristas del ciclo euleriano
        var aristas = g.aristas()
        eulerTour(g, aristas.first())
    }

    /**
     * Explora recursivamente todos los aristas alcanzables desde [lado]
     * en el grafo [g].
     * 
     * Modificación de DFS que recorre aristas en lugar de vértices, y 
     * agrega las arista al ciclo euleriano al terminar un camino.
     * 
     * Tiempo de ejecución: O(|E|).
     * Precondición: [g] es un dígrafo.
     *               [lado] es un arista perteneciente al dígrafo.
     * Postcondición: true
     */
    private fun eulerTour(g: GrafoNoDirigido, lado: Arista) {
        ladosVisitados.add(lado)
        
        g.adyacentes(lado.cualquieraDeLosVertices()).forEach {
            // Si no se había visitado el lado se avanza por el camino
            if (ladosVisitados.add(it)) eulerTour(g, it)
        }
        // Terminado un camino se agrega el lado al ciclo
        cicloEuler[cicloEulerIndex] = lado
        cicloEulerIndex--
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
        color[u] = Color.GRIS

        g.adyacentes(u).forEach {
            // Se selecciona el adyacente
            val v = it.elOtroVertice(u)
            if (color[v] == Color.BLANCO) dfsVisit(g, v)
        }

        // Se termina de explorar u
        color[u] = Color.NEGRO
    }

    /**
     * Retorna un booleano indicando si el grafo [g] es fuertemente conexo.
     *  
     * Tiempo de ejecución: O(|V| + |E|).
     * Precondición: true.
     * Postcondición: [esFC] es: -True si [g] es fuertemente conexo.
     *                           -False de otra forma.
     */
    private fun esConexo(g: GrafoNoDirigido): Boolean {
        // Si desde el vertice 0 no se recorre todo el grafo, retorna false
        dfsVisit(g, 0)

        return color.all { it == Color.NEGRO }
    }

    /**
     * Retorna un objeto Iterable que contiene los lados del ciclo euleriano.
     * en orden.
     *  
     * @throws [RuntimeException] El grafo [g] no tiene un ciclo euleriano.
     *
     * Tiempo de ejecución: O(1).
     * Precondición: true.
     * Postcondición: [obtenerCicloEuleriano] es: un objeto iterable con los
     *                aristas en orden del camino del ciclo euleriano. 
     */ 
    fun obtenerCicloEuleriano(): Iterable<Arista> {
        if (!tieneCicloEuleriano()) throw RuntimeException("El grafo no tiene ciclo euleriano.")
        return cicloEuler.filterNotNull()
    }

    /**
     * Retorna un booleano indicando si [g] tiene un ciclo euleriano.
     * 
     * Tiempo de ejecución: O(1).
     * Precondición: true.
     * Postcondición: [tieneCicloEuleriano] es: -True si [g] tiene un ciclo euleriano.
     *                                          -False de otra forma.
     */
    fun tieneCicloEuleriano(): Boolean = euleriano
}