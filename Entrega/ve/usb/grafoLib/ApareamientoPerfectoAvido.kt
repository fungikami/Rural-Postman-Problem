/**
 * Autor: Ka Fung & Christopher Gómez
 * Fecha: 16/Ene/2022.
 */

package ve.usb.grafoLib

import java.util.LinkedList

/**
 * Implementación de un algoritmo de ávido para obtener un apareamiento
 * perfecto. 
 *
 * Se determina un apareamiento perfecto del grafo con la creación de una
 * instancia de la clase.
 *
 * @throws [RuntimeException] Si el grafo [g] no es completo o
 *                            no tiene un número para de vértices.
 * 
 * @param [g]: Grafo no dirigido a determinar apareamiento perfecto ávido.
 */
public class ApareamientoPerfectoAvido(g: GrafoNoDirigido) {
    val n = g.obtenerNumeroDeVertices()
    val M = mutableSetOf<Arista>()

    init {
        // Verificar que sea completo y número par de vértices
        if (n % 2 == 1) throw RuntimeException("El grafo no tiene un número par de vértices")
        if (!esCompleto(g)) throw RuntimeException("El grafo no es completo")
        
        val vP = BooleanArray(n) { true }

        // Construye una lista con lados ordenados 
        val L = LinkedList<Arista>()
        g.aristas().forEach { L.add(it) }
        L.sort()

        var paresDeVertices = n / 2
        while (paresDeVertices != 0) {
            val lado = L.poll()
            val i = lado.cualquieraDeLosVertices()
            val j = lado.elOtroVertice(i)

            if (vP[i] && vP[j]) {
                M.add(lado)
                vP[i] = false
                vP[j] = false
                paresDeVertices--
            } 
        }
    }
    
    /** 
     * Retorna el conjunto de lados del apareamiento perfecto.
     * 
     * Tiempo de ejecución: O(1).
     * Precondición: true.
     * Postcondición: [obtenerApareamiento] conjunto de lados 
     *                del apareamiento ávido.
     */
    fun obtenerApareamiento(): MutableSet<Arista> = M

    /** 
     * Verifica si un grafo no dirigido es completo.
     * 
     * Tiempo de ejecución: O(|V²|).
     * Precondición: true.
     * Postcondición: [esCompleto] es -True si [g] es completo.
     *                                -False de otra forma. 
     */
    private fun esCompleto(g: GrafoNoDirigido): Boolean {
        val nLados = g.obtenerNumeroDeLados()
        
        if (nLados != n * (n - 1) / 2) return false

        for (i in 0 until n) {
            val estaConectado = BooleanArray(n)
            estaConectado[i] = true

            g.adyacentes(i).forEach { estaConectado[it.elOtroVertice(i)] = true }

            if (!estaConectado.all { it }) return false
        }

        return true       
    }
}