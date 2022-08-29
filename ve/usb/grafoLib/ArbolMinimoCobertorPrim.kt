/**
 * Autor: Christopher Gómez.
 * Fecha: 15/Nov/2021.
 */

package ve.usb.grafoLib

import kotlin.Double.Companion.POSITIVE_INFINITY
import java.util.LinkedList

/**
 * Implementación del algoritmo de Prim que determina el árbol mínimo cobertor
 * de un grafo no dirigido.
 * 
 * Se basa en la estructura de Cola de Prioridad usando Binary Heaps.
 * 
 * Se determina el árbol minimo cobertor con la creación de una instancia
 * de la clase.
 * 
 * @param [g]: grafo no dirigido sobre el que se ejecuta el algoritmo.
 */
public class ArbolMinimoCobertorPrim(val g: GrafoNoDirigido) {
    private val n = g.obtenerNumeroDeVertices()
    private val clave = DoubleArray(n, { POSITIVE_INFINITY })
    private val pred = Array<Int?>(n, { null })
    private val existe = BooleanArray(n, { true })
    private val amc = LinkedList<Arista>()
    private var w = 0.0

    init {
        clave[0] = 0.0
        val Q = ColaDePrioridad(Array<Pair<Int, Double>>(n, { Pair(it, clave[it]) }))

        while (!Q.estaVacia()) {
            val u = Q.extraerMinimo().first
            existe[u] = false

            g.adyacentes(u).forEach {
                val v = it.elOtroVertice(u)
                
                if (existe[v] && it.peso() < clave[v]) {
                    Q.disminuirClave(Pair(v, clave[v]), it.peso())
                    pred[v] = u
                    clave[v] = it.peso()
                }
            }
        }

        // Se construye el AMC con el arreglo de predecesores
        for (i in 1 until n) amc.add(Arista(pred[i]!!, i, clave[i]))
        amc.forEach { w += it.peso() }
    }

    /**
     * Retorna un objeto iterable con los lados del árbol mínimo cobertor
     * hallado.
     * 
     * Tiempo de ejecución: O(1).
     * Precondición: true.
     * Postcondición: [obtenerLados] es un objeto iterable de <Aristas> con
     *                un árbol mínimo cobertor del grafo [g].
     */
    fun obtenerLados(): Iterable<Arista> = amc
    
    /**
     * Retorna el peso del árbol mínimo cobertor hallado.
     * 
     * Tiempo de ejecución: O(1).
     * Precondición: true.
     * Postcondición: [obtenerPeso] es un Double con el peso del
     *                árbol mínimo cobertor de obtenerLados().
     */
    fun obtenerPeso(): Double = w

    /**
     * Implementación del TAD Cola de Prioridad mediante un Min-Heap,
     * donde los elementos con menor valor tienen prioridad más alta.
     * 
     * De uso específico para la implemetación del algoritmo de Prim.
     * Solo soporta las operaciones de extraer minimo (desencolar) y
     * disminuir clave (aumentar prioridad).
     * 
     * @param queue: Arreglo de pares <Int, Double> con el que se inicializará
     *           la cola de prioridad. La prioridad de cada elemento se
     *           corresponder con el segundo componente de cada par.
     *           
     */
    inner class ColaDePrioridad(val queue: Array<Pair<Int, Double>>) {

        /* Se llevan los indices de cada vértice en un arreglo para
        poder disminuirClave en tiempo logarítmico, sin tener que buscar
        el elemento en el arreglo. */
        private val map = IntArray(queue.size, { it })
        private var heapSize = queue.size

        init {
            // Construye un Min-Heap con todos los elementos del arreglo inicial
            for (i in 0 until queue.size) map[vertice(i)] = i
            for (i in (queue.size / 2 - 1) downTo 0) minHeapify(i)
        }

        /**
         * Retorna el vértice en la entrada [i] de la cola.
         * 
         * Tiempo de ejecución: O(1).
         * Precondición: [i] es un entero en el intervalo [0..queue.size)
         * Postcondición: [vertice] es un entero correspondiente a la
         *                primera coordenada del par contenido en queue[i].
         */
        private fun vertice(i: Int): Int = queue[i].first

        /**
         * Retorna la clabe del elemento en la entrada [i] de la cola.
         * 
         * Tiempo de ejecución: O(1).
         * Precondición: [i] es un entero en el intervalo [0..queue.size)
         * Postcondición: [clave] es un Double correspondiente a la
         *                segunda coordenada del par contenido en queue[i],
         *                de acuerdo a la cual se ordena la cola.
         */
        private fun clave(i: Int): Double = queue[i].second

        /** 
         * Intercambia los elementos en los índices [i] y [j] del arreglo. 
         * 
         * Tiempo de ejecución: O(1).
         * Precondición: [i] y [j] son enteros en el intervalo [0..queue.size)
         * Postcondición: Los elementos en los índices [i] y [j] de la cola
         *                se encuentran permutados.
         */
        private fun intercambiar(i: Int, j: Int) {
            val temp1 = queue[i]
            queue[i] = queue[j]
            queue[j] = temp1

            map[vertice(i)] = i 
            map[vertice(j)] = j
        }

        /** 
         * Retorna el índice del padre del elemento en el índice [i].
         * 
         * Tiempo de ejecución: O(1).
         * Precondición: [i] es un entero en el intervalo [0..queue.size)
         * Postcondición: [padre] es un entero con el índice en queue del
         *                padre del elemento en [i] en el Min-Heap.
         */
        private fun padre(i: Int): Int = if (i % 2 == 0) i / 2 - 1 else i / 2

        /**
         * Restaura las propiedades de un Min-Heap en [queue] enraizado en el índice [i].
         * 
         * Tiempo de ejecución: O(log(n)).
         * Precondición: [i] es un entero en el intervalo [0..queue.size)
         *               Los sub-árboles enraizados en los índices 2[i]+1
         *               y 2[i]+2 satisfacen las propiedades de un Min-Heap.
         * Postcondición: queue satisface las propiedades de un Min-Heap.
         */
        private fun minHeapify(i: Int) {
            var (l, r) = Pair(2*i + 1, 2*i + 2)
            
            var smallest = if (l < heapSize && clave(l) < clave(i)) l else i
            if (r < heapSize && clave(r) < clave(smallest)) smallest = r

            if (smallest != i) {
                intercambiar(i, smallest)
                minHeapify(smallest)
            }
        }

        /**
         * Retorna y desencola el elemento con mayor prioridad de la cola.
         * 
         * @throws [RuntimeException] La cola no tiene elementos.
         * 
         * Tiempo de ejecución: O(log(n))
         * Precondición: La cola debe tener al menos un elemento.
         * Postcondición: [extraerMinimo] es un par de <Int, Double> con el
         *                elemento de mayor prioridad de la cola.
         *                queue = queue0 - {[extraerMinimo]}.
         */
        fun extraerMinimo(): Pair<Int, Double> {
            if (heapSize < 1) throw RuntimeException("Error: Underflow de cola")

            val min = queue[0]
            map[vertice(0)] = -1

            queue[0] = queue[--heapSize]
            map[vertice(0)] = 0
            minHeapify(0)

            return min
        }

        /**
         * Disminuye a [k] la clave del elemento [key] de la cola.
         *
         * @throws [RuntimeException] [el] no se corresponde con ningun elemento de la cola.
         * @throws [RuntimeException] Se intentó aumentar la clave del elemento.
         * 
         * Tiempo de ejecución: O(log(n)) 
         * Precondición: [el] pertenece a queue.
         *               [k] es menor o igual la prioridad actual del elemento.
         * Postcondición: La clave del elemento [key] en la cola es [k].
         */
        fun disminuirClave(el: Pair<Int, Double>, k: Double) {
            var i = try {
                map[el.first]
            } catch (e: Exception) {
                throw RuntimeException("Error: No existe ningún elemento con clave $el en la cola.")
            }

            if (clave(i) < k) {
                throw RuntimeException("Error: Nueva clave ($k) es más grande que la anterior (${queue[i]})")
            }

            map[vertice(i)] = -1
            queue[i] = Pair(el.first, k)
            map[vertice(i)] = i

            while (i > 0 && clave(i) < clave(padre(i))) {
                intercambiar(i, padre(i))
                i = padre(i)
            }
        }

        /**
         * Retorna un booleano indicando si la cola está vacía.
         * 
         * Tiempo de ejecución: O(1).
         * Precondición: true.
         * Postcondiciñon: [estaVacia] es -True si la cola no tiene más elementos.
         *                                -False de otra forma.
         */
        fun estaVacia(): Boolean = heapSize == 0

        /**
         * Retorna un string con la representación del TAD ColaDePrioridad,
         * en el orden en que se encuentran en el heap de la implementación.
         * 
         * Tiempo de ejecución: O(n).
         * Precondición: true.
         * Postcondiciñon: [toString] es una string con la represenctación de
         *                 los elementos actualmente en la cola de prioridad.
         */
        override fun toString() : String = queue.sliceArray(0 until heapSize).contentToString()
    }
}