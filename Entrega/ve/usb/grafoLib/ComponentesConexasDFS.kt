/**
 * Autor: Christopher Gómez.
 * Fecha: 15/Nov/2021.
 */

package ve.usb.grafoLib

/**
 * Implementación del algoritmo basado en Búsqueda en Profundidad
 * para determinar las componentes conexas de un grafo no dirigido.
 * 
 * Se determinan las componentes conexas con la creación de una instancia
 * de la clase.
 * 
 * @param [g]: grafo no dirigido sobre el que se ejecuta el algoritmo.
 */
public class ComponentesConexasDFS(val g: GrafoNoDirigido) {
    // Propiedades de los vértices del grafo
    private val n = g.obtenerNumeroDeVertices()
    private val color = Array<Color>(n) { Color.BLANCO }
    private val cc = IntArray(n)
    private var numCC = 0

    /* Lleva una lista con el número de elementos de cada
    componente conexa para obtenerlo luego en tiempo constante. */
    private val numElems = ArrayList<Int>()

    init {
	    // Algoritmo DFS
        for (v in 0 until n) {
            if (color[v] == Color.BLANCO){
                numElems.add(0)
                dfsVisit(g, v)
                numCC++
            }
        }
    }

    /*
     * Explora recursivamente todos los vértices alcanzables desde [u]
     * en el grafo [g].
     * 
     * dfsVisit modificado para hallar y contar componentes conexas
     * en un grafo no dirigido.
     * 
     * Tiempo de ejecución: O(|E|).
     * Precondición: [g] es un grafo.
     *               [u] es un vértice perteneciente al grafo.
     * Postcondición: true
     */
    private fun dfsVisit(g: Grafo, u: Int) {
        // Se empieza a explorar u
        color[u] = Color.GRIS
        cc[u] = numCC
        numElems[numCC]++

        g.adyacentes(u).forEach {
            // Se selecciona el adyacente
            val v = it.elOtroVertice(u)

            // Si no se ha visitado se añade a la cc actual
            if (color[v] == Color.BLANCO) dfsVisit(g, v)
        }

        // Se termina de explorar u
        color[u] = Color.NEGRO
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
    fun estanMismaComponente(v: Int, u: Int): Boolean {
        g.chequearVertice(u)
        g.chequearVertice(v)

        return cc[u] == cc[v]
    }

    /**
     * Retorna el número de componentes conexas del grafo.
     * 
     * Tiempo de ejecución: O(1).
     * Precondición: true.
     * Postcondición: [numeroDeComponentesConexas] es un entero
     *                con el número de componentes conexas de g.
     */  
    fun numeroDeComponentesConexas(): Int = numCC

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
    fun obtenerComponente(v: Int): Int {
        g.chequearVertice(v)

        return cc[v]
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
        if (0 > compID || compID >= numeroDeComponentesConexas()) {
            throw RuntimeException("El identificador $compID no pertenece a ninguna componente conexa.")
        }

        return numElems[compID]
    }

}
