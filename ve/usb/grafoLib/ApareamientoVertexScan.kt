/**
 * Autor: Ka Fung & Christopher Gómez
 * Fecha: 16/Ene/2022.
 */

import java.util.LinkedList

/*
 * Clase que representa implementacion de una lista enlazada
 */

public class ApareamientoVertexScan(g: GrafoNoDirigido) {
    val n = g.obtenerNumeroDeVertices()
    val M = mutableSetOf<Arista>()

    // POR TESTEAR
    init {
        // Verificar que sea completo y número par de vértices
        if (n % 2 == 1) throw RuntimeException("El grafo no tiene un número par de vértices")
        if (!esCompleto(g)) throw RuntimeException("El grafo no es completo")
        
        // Crea V'
        val vP = mutableSetOf<Int>()
        for (i in 0 until n) vP.add(i)

        // Crea E'
        val eP = g.aristas()

        while (!vP.isEmpty()) {
            // Escoge aleatoriamente un vértice i de vP
            val i = vP.random()

            // Escoge el lado (i, j) con con menor costo
            val lado = Arista(0, 1) // TODO
            val j = 0
            
            M.add(lado)
            
            vP.remove(i)
            vP.remove(j)

            // Elimina todos los lados que tengan como adyacentes i o j
            g.adyacentes(i).forEach { eP.remove(it) }
            g.adyacentes(j).forEach { eP.remove(it) }
        }
    }
    
    fun obtenerApareamiento(): MutableSet<Arista> = M

    private fun esCompleto(g: GrafoNoDirigido): Boolean = true
}