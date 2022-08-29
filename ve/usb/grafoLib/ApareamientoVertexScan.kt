/**
 * Autor: Ka Fung & Christopher Gómez
 * Fecha: 16/Ene/2022.
 */

package ve.usb.grafoLib

import java.util.LinkedList
import kotlin.Double.Companion.POSITIVE_INFINITY

/**
 * Implementación del algoritmo Vertex Scan para obtener un apareamiento
 * perfecto. 
 *
 * Se determina un apareamiento perfecto del grafo con la creación de una
 * instancia de la clase.
 *
 * @throws [RuntimeException] Si el grafo [g] no es completo o
 *                            no tiene un número para de vértices.
 * 
 * @param [g]: Grafo no dirigido a determinar apareamiento perfecto.
 */
public class ApareamientoVertexScan(g: GrafoNoDirigido) {
    val n = g.obtenerNumeroDeVertices()
    val M = mutableSetOf<Arista>()

    init {
        // Verificar que sea completo y número par de vértices
        if (n % 2 == 1) throw RuntimeException("El grafo no tiene un número par de vértices")
        if (!esCompleto(g)) throw RuntimeException("El grafo no es completo")
        
        // Crea V'
        val vP = mutableSetOf<Int>()
        for (i in 0 until n) vP.add(i)
        
        while (!vP.isEmpty()) {
            // Escoge aleatoriamente un vértice i de vP
            val i = vP.random()

            // Escoge el lado (i, j) con con menor costo
            var lado = Arista(0, 1)
            var min = POSITIVE_INFINITY
            g.adyacentes(i).forEach {
                val p = it.peso()

                if (it.elOtroVertice(i) in vP && p < min) {
                    min = p
                    lado = it
                }
            }
            M.add(lado)
            
            // Elimina los vértices i, j de vP
            vP.remove(i)
            vP.remove(lado.elOtroVertice(i))
        }
    }
     
    /** 
     * Retorna el conjunto de lados del apareamiento perfecto.
     * 
     * Tiempo de ejecución: O(1).
     * Precondición: true.
     * Postcondición: [obtenerApareamiento] conjunto de lados 
     *                del apareamiento perfecto.
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