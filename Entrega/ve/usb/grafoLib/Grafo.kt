/**
 * Autor: Christopher Gòmez
 * Fecha: 01/Nov/2021
 */

package ve.usb.grafoLib

/**
 * Interfaz para la implementación de la estructura de datos
 * Grafo.
 *
 * Se refiere a V y E como el conjunto de vértices y lados del
 * grafo, respectivamente.
 */ 
interface Grafo {
    /**
     * Retorna el número de lados en el grafo.
     *
     * Precondición: true.
     * Postcondición: [obtenerNumeroDeLados] = |E|
     */
    fun obtenerNumeroDeLados(): Int

    /**
     * Retorna el número de vértices del grafo.
     *
     * Precondición: true.
     * Postcondición: [obtenerNumeroDeVertices] = |V|
     */
    fun obtenerNumeroDeVertices(): Int

    /**                 
     * Retorna los lados adyacentes al vértice [v].
     *
     * @throws [RuntimeException] El vértice está fuera del intervalo [0..|V|).
     * 
     * Precondición: [v] pertenece al conjunto de vértices del grafo.
     * Postcondición: [adyacentes] es un objeto iterable que contiene
     *                todos los lados del grafo adyacentes al vértice [v].
     */
    fun adyacentes(v: Int): Iterable<Lado>

    /** 
     * Retorna el grado del vértice [v] del grafo.
     * 
     * @throws [RuntimeException] El vértice está fuera del intervalo [0..|V|).
     *
     * Precondición: [v] pertenece al conjunto de vértices del grafo.
     * Postcondición: [grado] es un entero con el grado de [v].
     */
    fun grado(v: Int): Int
}