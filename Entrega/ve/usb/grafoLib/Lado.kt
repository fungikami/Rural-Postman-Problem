/**
 * Autor: Christopher Gómez.
 * Fecha: 01/Nov/2021.
 */

package ve.usb.grafoLib

/**
 * Clase abstracta que representa un lado de la interfaz Grafo.
 */
abstract class Lado(val a: Int, val b: Int) {

    /**
     * Retorna el vertice [a] del lado
     *
     * Tiempo de ejecución: O(1).
     * Precondición: true.
     * Postcondición: [cualquieraDeLosVertices] es un entero con uno de los vértices
     *                de la arista.
     */
    fun cualquieraDeLosVertices(): Int = a

    /**
     * Retorna el vértice de la arista que es distinto a [w].
     *
     * @throws [RuntimeException] [w] no es un vértice del lado.
     *
     * Tiempo de ejecución: O(1).
     * Precondición: [w] es un vértice del lado.
     * Postcondición: [elOtroVertice] != [w] y [elOtroVertice] es un vértice del lado.
     */
    fun elOtroVertice(w: Int): Int {
         return if (w == a) {
              b
         } else if (w == b) {
              a
         } else {
              throw RuntimeException("El vértice $w no se encuentra en el lado.")
         }
    }
}
