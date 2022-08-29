/**
 * Autor: Christopher Gómez
 * Fecha: 1/nov/2021
 */

package ve.usb.grafoLib

/**
 * Clase que implementa un Lado correspondiente a un dígrafo.
 *
 * @param [inicio] Entero que representa el vértice inicial del arco.
 * @param [final] Entero que representa el vértice final del arco.
 * @param [peso](opcional) Real que representa el peso del arco.
 *
 * @throws [RuntimeException] [v] < 0 o [u] < 0.
 */
public class Arco(val inicio: Int, val fin: Int, val peso: Double = 0.0) : Lado(inicio, fin) {

    init {
        if (inicio < 0 || fin < 0) {
            throw RuntimeException("Los vértices deben ser mayores o iguales a 0.")
        }
    }

    /**
     * Retorna el vértice inicial del arco.
     *
     * Tiempo de ejecución: O(1).
     * Precondición: true.
     * Postcondición: [fuente] es un entero con el vértice inicial del arco.
     */
    fun fuente(): Int = inicio

    /**
     * Retorna el vértice final del arco.
     * 
     * Tiempo de ejecución: O(1).
     * Precondición: true.
     * Postcondición: [sumidero] es un entero con el vértice final del arco.
     */
    fun sumidero(): Int = fin

    /**
     * Retorna el peso asociado al arco.
     *
     * Tiempo de ejecución: O(1).
     * Precondición: true.
     * Postcondición: [peso] es un real con el peso asociado al arco.
     */
    fun peso(): Double = peso

    /**
     * Retorna la representación en String del arco y su peso
     * asociado, como un par (<VerticeInicial, VerticeFinal>, peso).
     *
     * Tiempo de ejecución: O(1).
     * Precondición: true
     * Postcondición: [toString] es una representación del arco como una cadena 
     *                de caracteres
     */
    override fun toString(): String = "(<$inicio, $fin>, $peso)"

    /**
     * Define la igualdad de la Arista con [other], con respecto a sus vértices.
     * 
     * Tiempo de ejecución: O(1).
     * Precondición: true.
     * Postcondición: [equals] es -True si [other] es Arco y tiene los
     *                             mismo vértices inicial y final.
     *                            -False de otra forma.
     */
    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Arco) return false 
        
        return (
            this.fuente() == other.fuente() &&
            this.sumidero() == other.sumidero()
        )
    }
} 
