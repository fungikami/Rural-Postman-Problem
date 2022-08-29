/**
 * Autor: Christopher Gómez.
 * Fecha: 15/Nov/2021.
 */


package ve.usb.grafoLib

/**
 * Implementación del algoritmo basado en Unión de Conjuntos Disjuntos
 * para determinar las componentes conexas de un grafo no dirigido.
 * 
 * Los Conjuntos Disjuntos usados están represetados como árboles y
 * usan las heurísticas de compresión de camino y unión por rango
 * 
 * Se determinan las componentes conexas con la creación de una instancia
 * de la clase.
 * 
 * @param [g]: grafo no dirigido sobre el que se ejecuta el algoritmo.
 */
public class ComponentesConexasCD(val g: GrafoNoDirigido) {
    private val n = g.obtenerNumeroDeVertices()
    private val cc = ConjuntosDisjuntos(n)
    private val id = HashMap<Int, Int>()    // id[repr] = identificador
    private val idInv = HashMap<Int, Int>() // idInv[identificador] = repr    
    private val repr = ArrayList<Int>()     // lista de repr

    init {
        g.aristas().forEach {
            val u = it.cualquieraDeLosVertices()
            val v = it.elOtroVertice(u)

            cc.union(u, v)
        }
        
        for (s in 0 until n) {
            val reprCC = cc.encontrarConjunto(s)
            
            if (!(reprCC in repr)) {
                repr.add(reprCC)
                id[reprCC] = repr.size - 1
                idInv[repr.size - 1] = reprCC
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
    fun estanMismaComponente(v: Int, u: Int): Boolean {
        g.chequearVertice(u)
        g.chequearVertice(v)

        return cc.encontrarConjunto(u) == cc.encontrarConjunto(v)
    }

    /**
     * Retorna el número de componentes conexas del grafo.
     * 
     * Tiempo de ejecución: O(1).
     * Precondición: true.
     * Postcondición: [numeroDeComponentesConexas] es un entero
     *                con el número de componentes conexas de g.
     */   
    fun numeroDeComponentesConexas(): Int = cc.numConjuntosDisjuntos() 

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

        return id[cc.encontrarConjunto(v)]!!
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
    fun numVerticesDeLaComponente(compID: Int): Int {
        if (0 > compID || compID >= numeroDeComponentesConexas()) {
            throw RuntimeException("El identificador $compID no pertenece a ninguna componente conexa.")
        }
        return cc.obtenerNumDeElementos(idInv[compID]!!)
    }
}