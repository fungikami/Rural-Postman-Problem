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
    private var cicloEuler = Array<Arista>(g.obtenerNumeroDeLados()) { Arista(0, 1) }
    private var cicloEulerIndex = g.obtenerNumeroDeLados() - 1

    init {
        if (!esConexo(g)) throw RuntimeException("El grafo no es conexo.")

        // Verifica si tiene un ciclo euleriano (grado par)
        for (v in 0 until n) {
            if (g.grado(v) % 2 == 1) {
                euleriano = false
                break
            }
        }
        
        // Obtiene las aristas del ciclo euleriano
        var aristas = g.aristas()
        var u = aristas.first().cualquieraDeLosVertices()

        eulerTour(g, aristas.first(), u)
    
        // Reorienta los lados
        // Obtiene los primero dos lados para saber la "orientación" del ciclo
        val (l1, l2) = Pair(cicloEuler[0], cicloEuler[1])

        val u1 = l1.cualquieraDeLosVertices()
        val v1 = l1.elOtroVertice(u1)
        val u2 = l2.cualquieraDeLosVertices()
        val v2 = l1.elOtroVertice(u2)

        if (v1 == u2) {
        } else if (u1 == u2) {
            cicloEuler[0] = Arista(v1, u1, l1.peso())
        } else if (v1 == v2) {
            cicloEuler[1] = Arista(v2, u2, l2.peso())
        } else {
            cicloEuler[0] = Arista(v1, u1, l1.peso())
            cicloEuler[1] = Arista(v2, u2, l2.peso())
        }

        var m = cicloEuler.size
        var vAnterior = cicloEuler[0].cualquieraDeLosVertices()

        // Reorienta las aristas "mal orientadas en el ciclo"
        for (i in 0 until m) {
            var uActual = cicloEuler[i].cualquieraDeLosVertices()

            if (vAnterior != uActual) {
                val vActual = cicloEuler[i].elOtroVertice(uActual)
                cicloEuler[i] = Arista(vActual, uActual, cicloEuler[i].peso())
            }
            
            uActual = cicloEuler[i].cualquieraDeLosVertices()
            vAnterior = cicloEuler[i].elOtroVertice(uActual)
        }
    }

    /**
     * Explora recursivamente todos los aristas alcanzables desde [lado]
     * en el grafo [g].
     * 
     * Modificación de DFS que recorre aristas en lugar de vértices, y 
     * agrega las arista al ciclo euleriano al terminar un camino.
     * 
     * Tiempo de ejecución: O(|E|).
     * Precondición: [g] es un grafo no dirigido.
     *               [lado] es un arista perteneciente al grafo no dirigido.
     * Postcondición: true
     */
    private fun eulerTour(g: GrafoNoDirigido, lado: Arista, verticeInicial: Int) {
        ladosVisitados.add(lado)
        val verticeFinal = lado.elOtroVertice(verticeInicial)

        g.adyacentes(verticeFinal).forEach {
            // Si no se había visitado el lado se avanza por el camino
            if (ladosVisitados.add(it)) eulerTour(g, it, verticeFinal)
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
        return cicloEuler.toMutableList()
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