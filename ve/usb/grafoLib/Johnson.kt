package ve.usb.grafoLib
import kotlin.Double.Companion.POSITIVE_INFINITY
import java.util.LinkedList

/**
 * Implementación del algoritmo de Johnson que encuentra los
 * caminos de costo mínimo entre todos los pares de vértices de un dígrafo.
 * 
 * Se ejecuta el algoritmo de Johnson en el momento en que se crea
 * una instancia de la clase.
 *
 * El algoritmo se basa en los algoritmos de Bellman-Ford y Dijkstra, usando
 * este último una cola de prioridad basada en la estructura Min-Heap. 
 *
 * @param [g]: grafo sobre el que se ejecuta el algoritmo.
 */
public class Johnson(val g: GrafoDirigido) {
    private val n = g.obtenerNumeroDeVertices()
    private var dist = Array<Array<Double>>(n) { Array<Double>(n) { POSITIVE_INFINITY } }
    private var dijs = arrayOfNulls<Dijkstra>(n)
    private var hayCicloNeg = false

    init {
        var g2 = GrafoDirigido(n + 1)

        // Copia g en g2
        g.arcos().forEach { g2.agregarArco(it) }

        // Agrega un último vértice y todos los lados
        for (i in 0 until n) { g2.agregarArco(Arco(n, i, 0.0)) }

        val bf = BellmanFord(g2, n)
        hayCicloNeg = bf.tieneCicloNegativo()

        // Crea la función h
        val h = { v: Int -> bf.costoHasta(v) }

        if (!hayCicloNeg) {
            /* Vuelve a construir g2 como reemplazo de G
            con la nueva función de pesos */
            g2 = GrafoDirigido(n)
            
            g.arcos().forEach { 
                val u = it.fuente()
                val v = it.sumidero()

                g2.agregarArco(Arco(u, v, it.peso() + h(u) - h(v)))
            }

            for (u in 0 until n) {
                val dij = Dijkstra(g2, u)
                dijs[u] = dij

                for (v in 0 until n) {
                    dist[u][v] = dij.costoHasta(v) + h(v) - h(u)
                }
            }
        }

    }

    /**
     * Retorna un booleano indicando si existe algún ciclo negativo en el grafo
     *
     * Tiempo de ejecución: O(1).
     * Precondición: true.
     * Postcondición: [hayCicloNegativo] es: - True si existe algún ciclo negativo
     *                                         en el grafo g.
     *                                       - False de otra forma.
     */
    fun hayCicloNegativo(): Boolean = hayCicloNeg
    
    /**
     * Retorna la matriz con las distancias de los caminos de costo
     * mínimo entre todos los pares de vértices.
     *
     * @throws [RuntimeException] El grafo g contiene un ciclo negativo.
     *
     * Tiempo de ejecución: O(1).
     * Precondición: El grafo g no tiene ciclos negativos.
     * Postcondición: [obtenerMatrizDistancia] es una matriz tal que
     *                su entrada ij representa el costo del camino de
     *                costo mínimo de i a j.
     */
    fun obtenerMatrizDistancia(): Array<Array<Double>> {
        if (hayCicloNegativo()) throw RuntimeException("El grafo contiene un ciclo negativo.")

        return dist
    }

    /**
     * Retorna el costo del camino de costo mínimo de u a v
     *
     * @throws [RuntimeException] El vértice u o v dado está fuera del 
     *                            intervalo [0..|V|).
     * @throws [RuntimeException] El grafo g contiene un ciclo negativo. 
     *
     * Tiempo de ejecución: O(1).
     * Precondición: [u] y [v] pertenecen al conjunto de vértices del dígrafo.
     * Postcondición: [costo] es un real con el costo del camino
     *                de costo mínimo de [u] a [v]
     */
    fun costo(u: Int, v: Int): Double {
        if (hayCicloNegativo()) throw RuntimeException("El grafo contiene un ciclo negativo.")
        
        g.chequearVertice(u)
        g.chequearVertice(v)

        return dist[u][v]
    }

    /**
     * Retorna un booleano indicando si el vértice v es alcanzable desde u
     * en un camino de costo mínimo.
     *
     * @throws [RuntimeException] El vértice u o v dado está fuera del 
     *                            intervalo [0..|V|).
     * @throws [RuntimeException] El grafo g contiene un ciclo negativo. 
     * 
     * Tiempo de ejecución: O(1).
     * Precondición: [u] y [v] pertenecen al conjunto de vértices del dígrafo.
     * Postcondición: [existeUnCamino] es: - True si existe un camino de costo mínimo
     *                                       de [u] a [v].
     *                                     - False de otra forma.
     */
    fun existeUnCamino(u: Int, v: Int): Boolean {
        if (hayCicloNegativo()) throw RuntimeException("El grafo contiene un ciclo negativo.")
        
        g.chequearVertice(u)
        g.chequearVertice(v)

        return dist[u][v] != POSITIVE_INFINITY
    }
    
    /** 
     * Retorna el camino de costo mínimo desde u hasta v.
     *
     * @throws [RuntimeException] El vértice u o v dado está fuera del 
     *                            intervalo [0..|V|).
     * @throws [RuntimeException] El grafo g contiene un ciclo negativo. 
     * 
     * Tiempo de ejecución: O(E) en el peor caso.
     * Precondición: [u] y [v] pertenecen al conjunto de vértices del dígrafo.
     * Postcondición: [obtenerCaminoDeCostoMinimo] es un objeto iterable cuyos
     *                elementos son los arcos que conforman el camino de costo
     *                mínimo desde [u] hasta [v] en orden.
     */
    fun obtenerCaminoDeCostoMinimo(u: Int, v: Int): Iterable<Arco> {
        if (hayCicloNegativo()) throw RuntimeException("El grafo contiene un ciclo negativo.")

        g.chequearVertice(u)
        g.chequearVertice(v)
        
        // Se usa una pila para guardar la secuencia de arcos a retornar
        val Q = LinkedList<Arco>()

        dijs[u]!!.obtenerCaminoDeCostoMinimo(v).forEach {
            val i = it.fuente()
            val j = it.sumidero()

            Q.add(Arco(i, j, dist[i][j]))
        }

        return Q
    }
}