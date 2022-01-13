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

        // Crea E', se añaden los lados (u, v) y (v, u)
        val eP = g.aristas().toMutableList()
        val m = eP.size

        for (i in 0 until m) { 
            val u = eP[i].cualquieraDeLosVertices()
            val v = eP[i].elOtroVertice(u)
            eP.add(Arista(v, u, eP[i].peso()))
        }

        // Se ordenan los lados
        eP.sortWith(compareBy({ it.cualquieraDeLosVertices() }, { it }))

        val indV = IntArray(n)
        var k = -1
        
        // indV[i] = { índ. de la 1ra aparición de (i, j) en eP para algún j }
        eP.forEachIndexed { i, it ->
            val u = it.cualquieraDeLosVertices()
            if (u > k) {
                k = u
                indV[k] = i
            }
        }
        
        while (!vP.isEmpty()) {
            // Escoge aleatoriamente un vértice i de vP
            val i = vP.random()

            // Escoge el lado (i, j) con con menor costo
            val lado = eP[indV[i]]
            val j = lado.elOtroVertice(i)            
            M.add(lado)
            
            // Elimina los vértices i, j de vP
            vP.remove(i)
            vP.remove(j)
        }
    }
    
    fun obtenerApareamiento(): MutableSet<Arista> = M

    private fun esCompleto(g: GrafoNoDirigido): Boolean = true
}