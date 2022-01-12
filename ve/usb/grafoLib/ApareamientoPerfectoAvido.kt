/**
 * Autor: Ka Fung & Christopher Gómez
 * Fecha: 16/Ene/2022.
 */

import java.util.LinkedList

/*
 * Clase que representa implementacion de una lista enlazada
 */
public class ApareamientoPerfectoAvido(g: GrafoNoDirigido) {
    val n = g.numeroDeVertices()
    val M = mutableSetOf<Arista>()

    // POR TESTEAR
    init {
        // Verificar que sea completo y número par de vértices
        if (n % 2 == 1) throw RuntimeException("El grafo no tiene un número par de vértices")
        if (!esCompleto(g)) throw RuntimeException("El grafo no es completo")
        
        val vP = BooleanArray(n) { true }

        // Construir lista con lados ordenados 
        val L = LinkedList<Arista>()
        g.aristas().forEach { L.add(it) }
        L.sort()

        println("L: $L")

        var paresDeVertices = n / 2
        while (paresDeVertices != 0) {
            val lado = L.poll()
            val i = lado.cualquieraDeLosVertices()
            val j = lado.elOtroVertice(u)

            if (vP[i] && vP[j]) {
                M.add(lado)
                vP[i] = false
                vP[j] = false
                paresDeVertices--
            } 
        }
    }
    
    fun obtenerApareamiento(): MutableSet<Arista> = M

    private fun esCompleto(g: GrafoNoDirigido): Boolean = true
}