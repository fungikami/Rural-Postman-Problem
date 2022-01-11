/**
 * Autor: Christopher Gómez.
 * Fecha: 8/nov/2021
 */

package ve.usb.grafoLib

import java.util.LinkedList

/**
 * Implementación del algoritmo de Kosaraju para hallar las
 * componentes fuertemente conexas en un dígrafo.
 * 
 * Se determinan las componentes fuertemente conexas al momento
 * que se crea una instancia de la clase.
 * 
 * @param [g]: dígrafo sobre el que se ejecuta el algoritmo.
 */
public class CFC(val g: GrafoDirigido) {
    private val n = g.obtenerNumeroDeVertices()
    private val color = Array<Color>(n) { Color.BLANCO }
    private val cfc = ArrayList<MutableSet<Int>>()
    private val id = IntArray(n)
    private val ordenVertices = LinkedList<Int>()
    private var visitandoGrafoInverso = false
    private var grafoComponente = GrafoDirigido(0)

    init {
        // Se construye el orden decreciente de vértices por su tiempo final
        for (v in 0 until n) if (color[v] == Color.BLANCO) dfsVisit(g, v)
        for (v in 0 until n) color[v] = Color.BLANCO
        
        val gT = dígrafoInverso(g)

	    // Algoritmo DFS
        // Se recorre en orden topológico
        visitandoGrafoInverso = true
        ordenVertices.forEach {
            if (color[it] == Color.BLANCO){
                val CFCActual = mutableSetOf<Int>()
                dfsVisit(gT, it, CFCActual)
                cfc.add(CFCActual)
            }
        }

        /* En este momento cfc es una lista con
        todas las cfc del grafo (conjuntos a su vez). */

        // Se crea el grafo componente
        grafoComponente = GrafoDirigido(cfc.size)
        
        for (lado in g.arcos()) {
            val v = lado.fuente()
            val u = lado.sumidero()

            if (estanFuertementeConectados(v, u)) continue

            try {
                grafoComponente.agregarArco(Arco(id[v], id[u]))
            } catch (e: RuntimeException) {
                // Si se trata de agregar 
                continue
            }
        }
    }

    /**
     * Explora recursivamente todos los vértices alcanzables desde [u]
     * en el grafo [g].
     * 
     * dfsVisit modificado para guardar en una pila los vértices
     * a medida que se terminan de visitar, sin almacenar tiempos
     * en caso de que la visitandoGrafoInverso sea false.
     * 
     * En caso contrario, añade a [set] los vértices en el mismo arbol
     * DFS y actualiza su identificador en el arreglo id.
     * 
     * Tiempo de ejecución: O(|E|).
     * Precondición: [g] es un grafo.
     *               [u] es un vértice perteneciente al grafo.
     *               [set] (opcional) es un MutableSet de enteros con
     *               vértices pertenecientes al mismo bloque de la partición
     *               inducida por la relación de conectividad fuerte.
     * Postcondición: true
     */
    private fun dfsVisit(g: Grafo, u: Int, set: MutableSet<Int> = mutableSetOf<Int>()) {
        // Se empieza a explorar u
        color[u] = Color.GRIS

        if (visitandoGrafoInverso) {
            set.add(u)
            id[u] = cfc.size
        }

        g.adyacentes(u).forEach {
            // Se selecciona el adyacente
            val v = it.elOtroVertice(u)

            // Si no se ha visitado se añade a la cfc actual
            if (color[v] == Color.BLANCO) dfsVisit(g, v, set)
        }

        // Se termina de explorar u
        color[u] = Color.NEGRO
        if (!visitandoGrafoInverso) ordenVertices.addFirst(u)
    }

    /**
     * Retorna un booleano indicando si [v] y [u] pertenecen
     * al mismo bloque de la partición inducida por la relación
     * de conectividad fuerte.
     * 
     * @throws [RuntimeException] Alguno de los dos vértices está fuera
     *                            del intervalo [0..|V|).
     * 
     * Tiempo de ejecución: O(1).
     * Precondición: [v] y [u] pertenecen al conjunto de vértices del dígrafo.
     * Postcondición: [estanFuertementeConectados] es: -True si [v] y [u] pertenecen
     *                                     a la misma componente fuertemente conexa.
     *                                                 -False de otra forma.
     */
    fun estanFuertementeConectados(v: Int, u: Int): Boolean {
        g.chequearVertice(v)
        g.chequearVertice(u)

        return id[v] == id[u]
    }

    // Indica el número de componentes fuertemente conexas
    fun numeroDeCFC(): Int = cfc.size

    /**
     * Retorna el identificador de la componente fuertemente
     * conexa donde está contenido el vértice v. 
     * 
     * @throws [RuntimeException] El vértice [v] está fuera del intervalo [0..|V|).
     * 
     * Tiempo de ejecución: O(1).
     * Precondición: [v] pertenece al conjunto de vértices del dígrafo.
     * Postcondición: [obtenerIdentificadorCFC] Es un entero no negativo en
     *                [0..numeroDeCFC) con el identificador de [v].
     */
    fun obtenerIdentificadorCFC(v: Int): Int {
        g.chequearVertice(v)

        return id[v]
    }
    
    /**
     * Retorna un objeto Iterable con los bloques de partición de la partición
     * inducida por la relación de conectividad fuerte entre vértices.
     * 
     * Tiempo de ejecución: O(1).
     * Precondición: true.
     * Postcondición: [obtenerCFC] Es un objeto iterable con las componentes
     *                fuertemente conexas del dígrafo g. 
     */
    fun obtenerCFC(): Iterable<MutableSet<Int>> = cfc

    /**
     * Retorna el grafo componente asociado a las componentes fuertemente conexas,
     * donde el identificado de los vértices del grafo está asociado con los indicados
     * en la función obtenerIdentificadorCFC.
     * 
     * Ejemplo: Un lado <u, v> en el grafo de retorno implica la existencia
     *          un lado <p, q> en g tal que p pertenece a una CFC y q a otra
     *          distinta, tal que obtenerIdentificadorCFC(p) = u y 
     *          obtenerIdentificadorCFC(q) = v.
     * 
     * Tiempo de ejecución: O(1).
     * Precondición: true.
     * Postcondición: [obtenerGrafoComponente] Es el grafo componente de g.
     */
    fun obtenerGrafoComponente(): GrafoDirigido = grafoComponente
}