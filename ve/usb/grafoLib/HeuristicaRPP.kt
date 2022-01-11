/**
 * Autor: Ka Fung & Christopher Gómez
 * Fecha: 16/Ene/2022.
 */

/**
 * Clase que representa implementacion de una lista enlazada
 */
public class HeuristicaRPP {
    
    private fun ejecutarAlgoritmo(g: GrafoNoDirigido, R: MutableSet<Arista>, usarVertexScan: Boolean) {
        // Crea grafo G_R = <V_R, R> (V_R son los vértices de R)
        val n = g.obtenerNumeroDeVertices()
        val gP = GrafoNoDirigido(n)
        R.forEach { gP.agregarArista(it) }

        // Verifica si G' es conexo
        val cc = ComponentesConexasDFSIter(gP)
        
        if (cc.numeroDeComponentesConexas() == 1) {
            // Verificar si G' es par
            
            if () {

            }
           
        }

        // 

    }

    fun main(args: Array<String>) {
        // Verificar argumentos
        
        // Crear grafo no dirigido conexo G = <V, E>
        g = GrafoNoDirigido(0)
        // Obtener R ⊆ E

        val vertexScan = true
        ejecutarAlgoritmo(g, R, , vertexScan)
    }
}