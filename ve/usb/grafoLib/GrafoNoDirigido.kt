/**
 * Autor: Christopher Gómez
 * Fecha: 01/Nov/2021
 */

package ve.usb.grafoLib

import java.io.File
import java.io.FileInputStream
import java.util.Scanner
import java.lang.StringBuilder

/** 
 * Clase que implementa la interfaz Grafo para representar la estructura
 * de datos de grafo no dirigido como una lista de adyacencia. Hace uso
 * de las estructuras de datos Array y ArrayList, de las librerías estándar
 * de Kotlin.
 * 
 * Se refiere a V y E como el conjunto de vértices y lados del grafo,
 * respectivamente, y ady[v] como conjunto de elementos adyacentes
 * a un vértice dado v.
 */ 
public class GrafoNoDirigido: Grafo {
    private var numDeLados = 0
    private var numDeVertices = 0
    private var listaDeAdy = Array<ArrayList<Arista>>(0){ arrayListOf() }

    /**
     * Construye un grafo no dirigido con [numDeVertices] vértices, inicialmente
     * no contiene lados.
     */
    constructor(numDeVertices: Int) {
        this.numDeVertices = numDeVertices
        listaDeAdy = Array<ArrayList<Arista>>(numDeVertices){ arrayListOf() }
    }
    

    /** 
     * Construye un grafo a partir del archivo en la ruta [nombreDeArchivo].
     * 
     * El formato del archivo es el siguiente:
     *  -La primera línea contiene el número de vértices.
     *  -La segunda línea contiene el número de lados.
     *  -Las siguientes líneas contienen los vértices, separados por un espacio en
     *   blanco.
     *  -Adicionalmente, si [conPeso] es true, cada línea desde la tercera contiene
     *   un número real con el peso del vértice, luego del segundo vértice, separado
     *   por un espacio en blanco.
     */
    constructor(nombreArchivo: String, conPeso: Boolean) {
        if (File(nombreArchivo).exists()) {
            /* Se usa Scanner por eficiencia en la gestión de memoria al leer
            el archivo. */
            val sc = Scanner(FileInputStream(nombreArchivo))
            
            
            // La primera línea contiene el número de vértices
            numDeVertices = sc.nextLine()!!.toInt()
            listaDeAdy = Array<ArrayList<Arista>>(numDeVertices){ arrayListOf() }
            
            /* La segunda línea contiene el nùmero de lados, que será
            la cantidad de líneas a leer del archivo */
            repeat(sc.nextLine()!!.toInt()) {
                val a = sc.nextLine()!!.split(' ')
                
                this.agregarArista(Arista(
                    a[0].toInt(), 
                    a[1].toInt(),
                    if (conPeso) a[2].toDouble() else 0.0
                    )
                )
            }
        } else { 
            println("El archivo indicado en $nombreArchivo no existe o no se puede leer.")
        } 
    }

    /**
     * Agrega una arista al grafo no dirigido.
     *
     * @throws [RuntimeException] La arista tiene un vértice fuera del intervalo [0..|V|)
     *                             o ya existe en el grafo.
     * 
     * Tiempo de ejecución: O(this.listaDeAdy[a.v].size + this.listaDeAdy[a.u].size).
     * Precondición: [a] es una Arista válida y no está en el grafo.
     * Postcondicion: this.lados = this.lados0 U {a} y this.numDeVertices = this.numDeVertices0+1
     */
    fun agregarArista(a: Arista) {
        chequearArista(a)
        var v1 = a.cualquieraDeLosVertices()
        val v2 = a.elOtroVertice(v1)

        listaDeAdy[v1].add(a)
        listaDeAdy[v2].add(a)
        numDeLados++
    }

    /** 
     * Retorna el grado del vértice [v] en el grafo no dirigido.
     * 
     * @throws [RuntimeException] El vértice [v] está fuera del intervalo [0..|V|)
     * 
     * Tiempo de ejecución: O(1).
     * Precondición: [v] pertenece al conjunto de vértices del dígrafo.
     * Postcondición: [grado] es un entero con el grado de [v].
     */ 
    override fun grado(v: Int) : Int {
        chequearVertice(v)
        return listaDeAdy[v].size
    }

    /**
     * Retorna el número de lados en el grafo no dirigido.
     *
     * Tiempo de ejecución: O(1).
     * Precondición: true.
     * Postcondición: [obtenerNumeroDeLados] = |E|
     */
    override fun obtenerNumeroDeLados(): Int = numDeLados

    /**
     * Retorna el número de vértices del grafo no dirigido.
     *
     * Tiempo de ejecución: O(1).
     * Precondición: true.
     * Postcondición: [obtenerNumeroDeVertices] = |V|
     */
    override fun obtenerNumeroDeVertices(): Int = numDeVertices

    /** 
     * Retorna los lados adyacentes al vértice [v].
     *
     * @throws [RuntimeException] El vértice [v] está fuera del intervalo [0..|V|).
     * 
     * Tiempo de ejecución: O(1).
     * Precondición: [v] pertenece al conjunto de vértices del grafo
     *               no dirigido.
     * Postcondición: [adyacentes] es un objeto iterable que contiene
     *                todos los lados del grafo adyacentes al vértice [v].
     */
    override fun adyacentes(v: Int): Iterable<Arista> {
        chequearVertice(v)
        return listaDeAdy[v]
    }

    /**
     * Retorna todas las aristas del grafo no dirigido.
     *
     * Tiempo de ejecución: O(|V| + 2|E|).
     * Precondición: true
     * Postcondición: [aristas] es un objeto iterable que contiene
     *                todos los lados del grafo no dirigido.
     */
    fun aristas(): Iterable<Arista> {
        val c = ArrayList<Arista>()

        // Se concatenan todas las listas de adyacencia en una sola
        listaDeAdy.forEach { c.addAll(it) }

        // Se eliminan los grafos repetidos
        return c.distinct()
    }



    /** 
     * Retorna la representación en String del grafo no dirigido, como
     * una String de múltiples líneas donde cada línea tiene la forma:
     *
     * Vértice  | --> Lista de lados
     *
     * Tiempo de ejecución: O(|V| + 2|E|).
     * Precondición: true.
     * Postcondición: [toString] es una representación del grafo no dirigido 
     *                 como una cadena de caracteres.
     */
    override fun toString() : String {
        val str = StringBuilder()

        for ((i, el) in listaDeAdy.withIndex()) {
            str.append("%4d | ${if (el.size == 0) " " else "--> $el"}\n".format(i))
        }

	    return str.toString()
    }

    
    /** 
     * Verifica que la arista a insertar no esté repetida y tenga vértices válidos,
     * lanza una excepción en el caso de que la arista sea inválida o esté repetida.
     * Para uso interno en el método de agregarArista.
     *
     * @throws [RuntimeException] La arista tiene un vértice fuera del intervalo [0..|V|)
     *                                    o ya existe en el grafo.
     * 
     * Tiempo de ejecución: O(this.listaDeAdy[a.v].size + this.listaDeAdy[a.u].size)
     * Precondición: [a] es un arco.
     * Postcondición: this = this0.
     */
    private fun chequearArista(a: Arista) {
        val v1 = a.cualquieraDeLosVertices()
        val v2 = a.elOtroVertice(v1)
        chequearVertice(v1)
        chequearVertice(v2)

        if (a in listaDeAdy[v1] || a in listaDeAdy[v2]) {
            throw RuntimeException("La arista ya está en el grafo.")
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