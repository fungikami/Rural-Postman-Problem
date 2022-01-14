/**
 * Autor: Ka Fung & Christopher Gómez
 * Fecha: 30/Nov/2021.
 */

package ve.usb.grafoLib
import java.util.LinkedList

/**
 * Clase que determina si el dígrafo [g] posee un ciclo euleriano, y obtiene
 * el ciclo euleriano en caso afirmativo.
 * 
 * @throws [RuntimeException] Si el grafo [g] no es fuertemente conexo.
 * 
 * @param [g]: dígrafo sobre el que se ejecuta el algoritmo.
 */
public class CicloEuleriano(val g: GrafoDirigido) {
    private val n = g.obtenerNumeroDeVertices()
    private val color = Array<Color>(n) { Color.BLANCO }

    private var euleriano = true
    private var ladosVisitados = mutableSetOf<Arco>()
    private var cicloEuler = Array<Arco?>(g.obtenerNumeroDeLados()) { null }
    private var cicloEulerIndex = g.obtenerNumeroDeLados() - 1

    init {
        if (!esFC(g)) throw RuntimeException("El grafo no es fuertemente conexo.")

        // Verifica si tiene un ciclo euleriano
        for (v in 0 until n) {
            if (g.gradoExterior(v) != g.gradoInterior(v)) {
                euleriano = false
                break
            }
        }

        // Obtiene los arcos del ciclo euleriano
        var arcos = g.arcos()
        eulerTour(g, arcos.first())
    }

    /**
     * Explora recursivamente todos los arcos alcanzables desde [lado]
     * en el grafo [g].
     * 
     * Modificación de DFS que recorre arcos en lugar de vértices, y 
     * agrega los arcos al ciclo euleriano al terminar un camino.
     * 
     * Tiempo de ejecución: O(|E|).
     * Precondición: [g] es un dígrafo.
     *               [lado] es un arco perteneciente al dígrafo.
     * Postcondición: true
     */
    private fun eulerTour(g: GrafoDirigido, lado: Arco) {
        ladosVisitados.add(lado)
        
        g.adyacentes(lado.sumidero()).forEach {
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
    private fun esFC(g: GrafoDirigido): Boolean {
        // Si desde el vertice 0 no se recorre todo el grafo, retorna false
        dfsVisit(g, 0)
        if (color.any { it == Color.BLANCO }) return false

        // Calcula inversa de g
        for (v in 0 until n) color[v] = Color.BLANCO
        val gT = dígrafoInverso(g)

        // Si desde el vertice 0 no se recorre todo el grafo inverso, retorna false
        dfsVisit(gT, 0)
        return color.all{ it == Color.NEGRO }
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
     *                arcos en orden del camino del ciclo euleriano. 
     */ 
    fun obtenerCicloEuleriano(): Iterable<Arco> {
        if (!euleriano) throw RuntimeException("El grafo no tiene ciclo euleriano.")
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