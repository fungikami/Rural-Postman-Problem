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

        // Verifica si G' es conexo y par
        val cc = ComponentesConexasDFSIter(gP)
        val esConexo = cc.numeroDeComponentesConexas() == 1
        val esPar = esPar(gP)

        if (!esConexo) {
            if (!esPar) {

            } else {

            }
        }
    }

    private fun esPar(g: GrafoNoDirigido): Boolean {
        var esPar = true

        val paridad = BooleanArray(n) { true }

        gP.aristas().forEach {
            val u = it.cualquieraDeLosVertices()
            val v = it.elOtroVertice(u)
            
            if (paridad[u]) {
                paridad[u] = false
                esPar = false
            } else {
                paridad[u] = true
            }

            if (paridad[v]) {
                paridad[v] = false
                esPar = false
            } else {
                paridad[v] = true
            }
        }

        return esPar
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