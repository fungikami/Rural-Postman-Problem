/**
 * Autor: Christopher Gòmez
 * Fecha: 01/Nov/2021
 */

package ve.usb.grafoLib

import java.io.File
import java.io.FileInputStream
import java.util.Scanner
import java.lang.StringBuilder

/** 
 * Clase que implementa la interfaz Grafo para representar la estructura
 * de datos de dígrafo como una lista de adyacencia. Hace uso de las estructura 
 * de datos Array y ArrayList, de las librerías estándar de Kotlin.
 * 
 * Se refiere a V y E como el conjunto de vértices y lados del grafo,
 * respectivamente, y ady[v] como conjunto de elementos adyacentes
 * a un vértice dado v.
 */ 
public class GrafoDirigido : Grafo {

    private var numDeLados = 0
    private var numDeVertices = 0
    private var listaDeAdy = Array<ArrayList<Arco>>(0){ arrayListOf() }
    private var gradInt = IntArray(0)

    /* Construye un dígrafo a partir de [numDeVertices]. */
    constructor(numDeVertices: Int) {
        this.numDeVertices = numDeVertices
        listaDeAdy = Array<ArrayList<Arco>>(numDeVertices){ arrayListOf() }
        gradInt = IntArray(numDeVertices)
    }

    /**
     * Construye un dígrafo a partir del archivo en la ruta [nombreDeArchivo].
     * 
     * El formato del archivo es el siguiente:
     *  -La primera línea contiene el número de vértices.
     *  -La segunda línea contiene el número de lados.
     *  -Las siguientes líneas contienen los vértices inicial y final, separados 
     *   por un espacio en blanco.
     *  -Adicionalmente, si [conPeso] es true, cada línea desde la tercera contiene
     *   un número real con el peso del vértice, luego del segundo vértice, separado
     *   por un espacio en blanco.
     */
    constructor(nombreArchivo: String, conPeso: Boolean) {
	    if (File(nombreArchivo).exists()) {
            val sc = Scanner(FileInputStream(nombreArchivo))
            // La primera línea contiene el número de vértices
            numDeVertices = sc.nextLine()!!.toInt()
            listaDeAdy = Array<ArrayList<Arco>>(numDeVertices){ arrayListOf() }
            gradInt = IntArray(numDeVertices)

            /* La segunda línea contiene el número de lados, que será
            la cantidad de líneas a leer del archivo */
            repeat(sc.nextLine()!!.toInt()) {
                val a = sc.nextLine()!!.split(' ')

                this.agregarArco(Arco(
                    a[0].toInt(), 
                    a[1].toInt(),
                    if (conPeso) a[2].toDouble() else 0.0
                    )
                )
            }
        } else {
            println("El archivo indicado en $nombreArchivo no existe o no se puede leer.")
            return
        } 
    }

    /**
     * Agrega un arco al dígrafo.
     *
     * @throws [RuntimeException] El arco [a] tiene un vértice fuera del intervalo [0..|V|)
     *                            o ya existe en el grafo.
     * 
     * Tiempo de ejecución: O(|ady[a.fuente()]|).
     * Precondición: [a] es un Arco válido y no está en el grafo.
     * Postcondicion: E = E0 U {a}.     
     */
    fun agregarArco(a: Arco) {
        chequearArco(a)
        val v = a.fuente()

        listaDeAdy[v].add(a)

        numDeLados++
        gradInt[a.sumidero()]++
    }

    /** 
     * Retorna el grado del vértice [v] en el dígrafo.
     * 
     * @throws [RuntimeException] El vértice [v] está fuera del intervalo [0..|V|)
     * 
     * Tiempo de ejecución: O(1).
     * Precondición: [v] pertenece al conjunto de vértices del dígrafo.
     * Postcondición: [grado] es un entero con el grado de [v].
     */ 
    override fun grado(v: Int): Int = this.gradoInterior(v) + this.gradoExterior(v)

    /**
     * Retorna el grado exterior del vértice [v] en el grafo.
     * 
     * Tiempo de ejecución: O(1).
     * Precondición: [v] pertenece al conjunto de vértices del dígrafo.
     * Postcondición: [gradoExterior] es un entero con el grado exterior de [v].
     */
    fun gradoExterior(v: Int) : Int {
        chequearVertice(v)
        return listaDeAdy[v].size
    }
    
    /**
     * Retorna el grado interior del vértice [v] en el grafo.
     * 
     * Tiempo de ejecución: O(1).
     * Precondición: [v] pertenece al conjunto de vértices del dígrafo.
     * Postcondición: [gradoExterior] es un entero con el grado interior de [v].
     */
    fun gradoInterior(v: Int): Int {
        chequearVertice(v)
        return gradInt[v]
    }

    /**
     * Retorna el número de lados en el dígrafo.
     *
     * Tiempo de ejecución: O(1).
     * Precondición: true.
     * Postcondición: [obtenerNumeroDeLados] es el número de lados
     *                del dígrafo.
     */
    override fun obtenerNumeroDeLados(): Int = numDeLados

    /**
     * Retorna el número de vértices del dígrafo.
     *
     * Tiempo de ejecución: O(1).
     * Precondición: true.
     * Postcondición: [obtenerNumeroDeVertices] es el número de vértices
     *                del dígrafo.
     */
    override fun obtenerNumeroDeVertices(): Int = numDeVertices

    /** 
     * Retorna los lados adyacentes al vértice [v].
     *
     * @throws [RuntimeException] El vértice [v] está fuera del intervalo [0..|V|).
     * 
     * Tiempo de ejecución: O(1).
     * Precondición: [v] pertenece al conjunto de vértices del dígrafo.
     * Postcondición: [adyacentes] es un objeto iterable que contiene
     *                todos los lados del dígrafo adyacentes al vértice [v].
     */
    override fun adyacentes(v: Int): Iterable<Arco> {
        chequearVertice(v)
        return listaDeAdy[v]
    }

    /**
     * Retorna todos los arcos del dígrafo.
     *
     * Tiempo de ejecución: O(|V|).
     * Precondición: true
     * Postcondición: [arcos] es un objeto iterable que contiene
     *                todos los lados del dígrafo.
     */
    fun arcos(): Iterable<Arco> {
	    val c = ArrayList<Arco>()
        listaDeAdy.forEach { c.addAll(it) }

        return c
    }

    /** 
     * Retorna la representación en String del dígrafo, como
     * una String de múltiples líneas donde cada línea tiene la forma:
     *
     * Vértice  | --> Lista de lados
     *
     * Tiempo de ejecución: O(|V| + |E|).
     * Precondición: true.
     * Postcondición: [toString] es una representación del dígrafo como una
     *                cadena de caracteres.
     */
    override fun toString(): String {
	    val str = StringBuilder()

        for ((i, el) in listaDeAdy.withIndex()) {
            str.append("%4d | ${if (el.size == 0) " " else "--> $el"}\n".format(i))
        }

	    return str.toString()
    }
    
    /** 
     * Verifica que el arco a insertar no esté repetido y tenga vértices válidos,
     * lanza una excepción en el caso de que el arco sea inválido o esté repetido.
     *
     * @throws [RuntimeException] El arco tiene al menos un vértice fuera del intervalo
     *                            [0..|V|) o ya existe en el grafo.
     * 
     * Tiempo de ejecución: O(|ady[a.fuente()]|)
     * Precondición: [a] es un arco.
     * Postcondición: this = this0.
     */
    private fun chequearArco(a: Arco) {
        chequearVertice(a.fuente())
        chequearVertice(a.sumidero())

        if (a in listaDeAdy[a.fuente()]) {
            throw RuntimeException("El arco ya está en el grafo.")
        }
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
    fun chequearVertice(v: Int) {
        if (v < 0 || v >= numDeVertices) {
            throw RuntimeException("El vértice $v no pertenece al grafo.")
        }
    }
}