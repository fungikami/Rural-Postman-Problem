/**
 * Autor: Ka Fung & Christopher Gómez
 * Fecha: 16/Ene/2022.
 */

import kotlin.system.measureTimeMillis

/**
 * Clase que representa implementacion de una lista enlazada
 */
public class HeuristicaRPP {
    companion object {
    
        private fun ejecutarAlgoritmo(
            g: GrafoNoDirigido,
            R: MutableSet<Arista>,
            usarVertexScan: Boolean
        ): Iterable<Arista> {
            // Crea grafo G_R = <V_R, R> (V_R son los vértices de R)
            val n = g.obtenerNumeroDeVertices()
            val gP = GrafoNoDirigido(n)
            R.forEach { gP.agregarArista(it) }

            // Verifica si G' es conexo y par
            val cc = ComponentesConexasDFSIter(gP)
            val esConexo = cc.numeroDeComponentesConexas() == 1

            // Si G' no es conexo (sin importar si es o no par)
            if (!esConexo) {
                // Líneas 9 a 15
            }

            if (!esConexo || !esPar(gP)) {
                // Líneas 16 a 23
            }

            return CicloEulerianoGrafoNoDirigido(gP).obtenerCicloEuleriano()
        }

        private fun esPar(g: GrafoNoDirigido): Boolean {
            val n = g.obtenerNumeroDeVertices()
            val paridad = BooleanArray(n) { true }

            g.aristas().forEach {
                val u = it.cualquieraDeLosVertices()
                val v = it.elOtroVertice(u)
                
                paridad[u] = !paridad[u]
                paridad[v] = !paridad[v]
            }

            return paridad.all { it }
        }

        @JvmStatic fun main(args: Array<String>) {
            // Verificar argumentos
            
            // Crear grafo no dirigido conexo G = <V, E>
            val g = GrafoNoDirigido(2)
            g.agregarArista(Arista(0, 1, 2.0))
            
            // Obtener R ⊆ E
            val R = mutableSetOf<Arista>()

            R.add(Arista(0, 1, 2.0))

            val vertexScan = true

            var ciclo: Iterable<Arista>
            val ms = measureTimeMillis { 
                ciclo = ejecutarAlgoritmo(g, R, vertexScan)
            }
            
            // Imprime salida
            ciclo.forEach { print("${it.cualquieraDeLosVertices()} ") }
            val u = ciclo.last().cualquieraDeLosVertices()
            println(ciclo.last().elOtroVertice(u))
            println(ciclo.sumOf { it.peso() }.toInt())
            println("%.3f segs.".format(ms / 1000.0))
        }
    }
}