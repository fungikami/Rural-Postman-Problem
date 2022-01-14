/**
 * Autor: Christopher Gómez.
 * Fecha: 15/Nov/2021.
 */

package ve.usb.grafoLib

import java.util.LinkedList

/**
 * Modificación de la implementación del algoritmo basado en Búsqueda en
 * Profundidad, hecha de forma iterativa con una pila, para determinar las
 * componentes conexas de un grafo no dirigido.
 * 
 * Se determinan las componentes conexas con la creación de una instancia
 * de la clase.
 * 
 * @param [g]: grafo no dirigido sobre el que se ejecuta el algoritmo.
 */
public class ComponentesConexasDFSIter(val g: GrafoNoDirigido) {
    // Propiedades de los vértices del grafo
    private val n = g.obtenerNumeroDeVertices()
    private val color = Array<Color>(n) {Color.BLANCO}
    private val ccIndex = IntArray(n)
    private var contCC = 0
    private var numVert = ArrayList<Int>()

    init {
        for (v in 0 until n) {
            if (color[v] == Color.BLANCO) {
                numVert.add(0)
                dfsVisit(g, v)
                contCC++
            }
        }
    }

    /*
     * Explora iterativamente todos los vértices alcanzables desde [u]
     * en el grafo [g].
     * 
     * 
     * dfsVisit modificado para hallar y contar componentes conexas
     * en un grafo no dirigido, de forma iterativa usando una pila.
     * 
     * 
     * Tiempo de ejecución: O(|E|).
     * Precondición: [g] es un grafo.
     *               [u] es un vértice perteneciente al grafo.
     * Postcondición: true
     */
    private fun dfsVisit(g: Grafo, t: Int) {
        color[t] = Color.GRIS
        ccIndex[t] = contCC
        numVert[contCC]++

        val Q = LinkedList<Int>()
        Q.addFirst(t)

        while (Q.size != 0) {
            val u = Q.poll()

            val ady = g.adyacentes(u)
            ady.forEach {
                val v = it.elOtroVertice(u)
                if (color[v] == Color.BLANCO) {
                    ccIndex[v] = contCC
                    numVert[contCC]++
                    color[v] = Color.NEGRO
                    Q.addFirst(v)
                }
            }
        }
    }

    /**
     * Retorna un booleano indicando si los dos vertices están en la misma
     * componente conexa.
     * 
     * @throws [RuntimeException] Alguno de los vértices está fuera del
     *                            intervalo [0..|V|).
     * 
     * Tiempo de ejecución: O(1).
     * Precondición: [v] y [u] pertenece al conjunto de vértices del grafo.
     * Postcondición: [estanMismaComponente] es -True si [u] y [v] están en
     *                                           la misma componente conexa.
     *                                          -False de otra forma.
     */   
    fun estanMismaComponente(v: Int, u: Int) : Boolean {
        if (v < 0 || v >= n) throw RuntimeException("El vértice $v no pertenece al grafo.")
        if (u < 0 || u >= n) throw RuntimeException("El vértice $u no pertenece al grafo.")
        return ccIndex[v] == ccIndex[u]
    }

    /**
     * Retorna el número de componentes conexas del grafo.
     * 
     * Tiempo de ejecución: O(1).
     * Precondición: true.
     * Postcondición: [numeroDeComponentesConexas] es un entero
     *                con el número de componentes conexas de g.
     */  
    fun numeroDeComponentesConexas() : Int = contCC

    /**
     * Retorna el identificador de la componente conexa donde
     * está contenido el vértice v. 
     * 
     * @throws [RuntimeException] El vértice [v] está fuera del intervalo [0..|V|).
     * 
     * Tiempo de ejecución: O(1).
     * Precondición: [v] pertenece al conjunto de vértices del grafo no dirigido.
     * Postcondición: [obtenerComponente] Es un entero no negativo en
     *                [0..numeroDeCC) con el identificador de [v].
     */
    fun obtenerComponente(v: Int) : Int {
        if (v < 0 || v >= n) throw RuntimeException("El vértice $v no pertenece al grafo.")
        return ccIndex[v]
    }

    /**
     * Retorna el número de vértices que conforman una componente conexa dada.
     * 
     * @throws [RuntimeException] [compID] no se corresponde con ningún identificador.
     * 
     * Tiempo de ejecución: O(1). 
     * Precondición: [compID] Es un entero no negativo en
     *               [0..numeroDeCC) con el identificador de alguna componente.
     * Postcondición: [numVerticesDeLaComponente] es un entero con en número
     *                de elementos de la componente conexa cuyo identificador
     *                es [compID]
     */
    fun numVerticesDeLaComponente(compID: Int) : Int {
        if (compID < 0 || compID >= contCC) {
            throw RuntimeException("El identificador $compID no pertenece a ninguna componente conexa.")
        }
        return numVert[compID]
    }

}
