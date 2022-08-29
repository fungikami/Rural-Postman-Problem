/**
 * Autor: Christopher Gómez.
 * Fecha: 15/Nov/2021.
 */

package ve.usb.grafoLib

/**
 * Implementación de las estructuras de datos de Conjuntos Disjuntos
 * como  árboles, usando las heurísticas de compresión de camino y
 * unión por rango.
 * 
 * Con la creación de una instancia de la clase se crean [n] conjuntos disjuntos 
 * iniciales, identificados con enteros en [0..n).
 * 
 * @param [n]: Número de elementos que conforman los conjuntos disjuntos.
 */
public class ConjuntosDisjuntos(val n: Int) {

    private val rango = IntArray(n) 
    private val padre = IntArray(n, { it })
    private var numDeConjuntos = n
    
    /* Lleva una lista tal que numElems[i] es el número de elementos
    del conjunto cuyo representativo es i para obtenerlo luego en tiempo
    constante. Solo se actualiza la cuenta de los representativos en cada
    unión. */
    private val numElems = IntArray(n, { 1 })

    /**
     * Une los conjuntos que contienen a elementos [v] y [u].
     * Retorna un booleano indicando si [v] y [u] pertenecían al mismo conjunto.
     * 
     * @throws [RuntimeException] [v] y/o [u] están fuera del intervalo [0..n).
     * 
     * Tiempo de ejecución: O(1).
     * Precondición: [v] y [u] son elementos que pertenecen a los conjuntos
     *               disjuntos a unir.
     * Postcondición: [union] es -False si [v] y [u] pertenecían al mismo
     *                            conjunto.
     *                           -True de otra forma.
     *                [v] y [u] se encuentran en el mismo conjunto de la
     *                estructura. 
     */
    fun union(v: Int, u: Int): Boolean {
        if (v < 0 || v >= n) throw RuntimeException("El elemento $v está fuera del rango [0..n).")
        if (u < 0 || u >= n) throw RuntimeException("El elemento $u está fuera del rango [0..n).")

        val x = encontrarConjunto(v)
        val y = encontrarConjunto(u)

        if (x == y) return false

        // Une el arbol de menor rango con el de mayor
        if  (rango[y] < rango[x]) {
            padre[y] = x
            numElems[x] += numElems[y]
        } else {
            padre[x] = y
            numElems[y] += numElems[x]
            if (rango[x] == rango[y]) rango[y]++
        }

        numDeConjuntos--
        return true
    }

    /**
     * Retorna el elemento representante del conjunto disjunto que contiene
     * a [v].
     * 
     * @throw [RuntimeException] [v] está fuera del intervalo [0..n).
     * 
     * Tiempo de ejecución: O(1).
     * Precondición: [v] es un entero con el elemento que al que se le
     *               quiere determinar el representante.
     * Postcondición: [encontrarConjunto] es un entero con el representante
     *                del conjunto que contiene a [v].
     */
    fun encontrarConjunto(v: Int): Int {
        if (v < 0 || v >= n) throw RuntimeException("El elemento $v está fuera del rango [0..n).")

        if (v != padre[v]) padre[v] = encontrarConjunto(padre[v])
        return padre[v]
    }

    /**
     * Retorna el número de conjuntos disjuntos que tiene la estructura.
     * 
     * @throw [RuntimeException] [v] está fuera del intervalo [0..n).
     * 
     * Tiempo de ejecución: O(1).
     * Precondición: true.
     * Poscondición: [numConjuntosDisjuntos] Es un entero con el número de
     *               conjuntos disjuntos que tiene la instancia actualmente. 
     */
    fun numConjuntosDisjuntos(): Int = numDeConjuntos

    /**
     * Retorna el número de elementos del conjunto disjunto que contiene a [v]
     * en la estructura.
     * 
     * @throw [RuntimeException] [v] está fuera del intervalo [0..n).
     * 
     * Tiempo de ejecución: O(1).
     * Precondición: [v] es un entero con el elemento que del cual se quiere
     *               sabe la cardinalidad del conjunto al que pertenece.
     * Poscondición: [numConjuntosDisjuntos] = |set| tal que set es el
     *               conjunto disjunto que contiene a [v]. 
     */
    fun obtenerNumDeElementos(v: Int): Int {
        if (v < 0 || v >= n) throw RuntimeException("El elemento $v está fuera del rango [0..n).")

        return numElems[encontrarConjunto(v)]
    }
}