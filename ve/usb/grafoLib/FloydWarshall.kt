/**
 * Autor: Christopher Gómez.
 * Fecha: 12/dic/2021
 */

package ve.usb.grafoLib
import kotlin.Double.Companion.POSITIVE_INFINITY
import java.util.LinkedList

/**
 * Implementación del algoritmo de Floyd-Warshall que encuentra los
 * caminos de costo mínimo entre todos los pares de vértices de un dígrafo.
 * 
 * Se ejecuta el algoritmo de Floyd-Warshall en el momento en que se crea
 * una instancia de la clase.
 *
 * Se asume que la matriz de costos corresponde a un dígrafo sin ciclos
 * negativos. En caso contrario, el resultado del algoritmo no es especificado.
 *
 * @param [W]: Matriz de costos asociada a un dígrafo con pesos en los lados.
 *
 * @throws [RuntimeException] La matriz [W] no es cuadrada.
 */
public class FloydWarshall(val W: Array<Array<Double>>) {
    private val n = W.size
    private var dist = W

    // Crea la matriz de predecesores inicial
    private var pred = Array<Array<Int?>>(n) { i ->
        Array<Int?>(n) { j ->
            if (i != j && W[i][j] < POSITIVE_INFINITY) i else null
        }
    }

    init {
        // Chequea que sea una matriz nxn
        if (W.any { it.size != n }) throw RuntimeException("La matriz dada no es cuadrada.") 

        for (k in 0 until n) {
            for (i in 0 until n) {
                for (j in 0 until n) {
                    val d_ikj = dist[i][k] + dist[k][j]

                    /* dist[i][j] obtiene el valor del mínimo entre sí mismo y d_ikj.
                    Se actualiza paralelamente la matriz de predecesores. */ 
                    if (dist[i][j] > d_ikj) {
                        dist[i][j] = d_ikj
                        pred[i][j] = pred[k][j]
                    }
                }
            }
        }
    }

    /**
     * Retorna la matriz con las distancias de los caminos de costo
     * mínimo entre todos los pares de vértices.
     *
     * Tiempo de ejecución: O(1).
     * Precondición: true
     * Postcondición: [obtenerMatrizDistancia] es una matriz tal que
     *                su entrada ij representa el costo del camino de
     *                costo mínimo de i a j.
     */
    fun obtenerMatrizDistancia(): Array<Array<Double>> = dist

    /**
     * Retorna la matriz con los predecesores de todos los vértices en los
     * caminos de costo mínimo entre todos los pares de vértices.
     *
     * Tiempo de ejecución: O(1).
     * Precondición: true
     * Postcondición: [obtenerMatrizPredecesores] es una matriz tal que
     *                su entrada ij es: - null si el vértice j no es alcanzable
     *                                    desde i.
     *                                  - k si existe un camino de costo mínimo
     *                                    de i a j que pase por k.
     */
    fun obtenerMatrizPredecesores(): Array<Array<Int?>> = pred
    
    /**
     * Retorna el costo del camino de costo mínimo de u a v
     *
     * @throws [RuntimeException] El vértice u o v dado está fuera del 
     *                            intervalo [0..|V|).
     * 
     * Tiempo de ejecución: O(1).
     * Precondición: [u] y [v] pertenecen al conjunto de vértices del dígrafo.
     * Postcondición: [costo] es un real con el costo del camino
     *                de costo mínimo de [u] a [v]
     */
    fun costo(u: Int, v: Int): Double {
        chequearVertice(u)
        chequearVertice(v)

        return dist[u][v]
    }

    /**
     * Retorna un booleano indicando si el vértice v es alcanzable desde u
     * en un camino de costo mínimo.
     *
     * @throws [RuntimeException] El vértice u o v dado está fuera del 
     *                            intervalo [0..|V|).
     * 
     * Tiempo de ejecución: O(1).
     * Precondición: [u] y [v] pertenecen al conjunto de vértices del dígrafo.
     * Postcondición: [existeUnCamino] es: - True si existe un camino de costo mínimo
     *                                       de [u] a [v].
     *                                     - False de otra forma.
     */
    fun existeUnCamino(u: Int, v: Int): Boolean {
        chequearVertice(u)
        chequearVertice(v)

        return dist[u][v] != POSITIVE_INFINITY
    }

    /** 
     * Retorna el camino de costo mínimo desde u hasta v.
     *
     * @throws [RuntimeException] El vértice u o v dado está fuera del 
     *                            intervalo [0..|V|).
     * 
     * Tiempo de ejecución: O(E) en el peor caso.
     * Precondición: [u] y [v] pertenecen al conjunto de vértices del dígrafo.
     * Postcondición: [obtenerCaminoDeCostoMinimo] es un objeto iterable cuyos
     *                elementos son los arcos que conforman el camino de costo
     *                mínimo desde [u] hasta [v] en orden.
     */
    fun obtenerCaminoDeCostoMinimo(u: Int, v: Int): Iterable<Arco> {
        chequearVertice(u)
        chequearVertice(v)

        val S = LinkedList<Arco>()
        
        var q: Int? = v
        while (pred[u][q!!] != null) {
            val p = pred[u][q]
            S.addFirst(Arco(p!!, q, W[p][q]))
            q = p
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