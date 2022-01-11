/**
 * Autor: Ka Fung & Christopher Gómez
 * Fecha: 30/Nov/2021.
 */

package ve.usb.grafoLib

import java.util.LinkedList
import kotlin.Double.Companion.POSITIVE_INFINITY

/**
 * Implementación de un algoritmo basado en Búsqueda en Amplitud
 * que determina el ancestro común más bajo de un par de vértices.
 * Se considera en el algoritmo que un vértice no es ancestro ni
 * descendiente de sí mismo.
 * 
 * @throws [RuntimeException] El grafo de entrada no es DAG.
 * 
 * @param [g]: dígrafo sobre el que se ejecuta el algoritmo.
 */
public class LCA(val g: GrafoDirigido) {
    private val n = g.obtenerNumeroDeVertices()
    private val color = Array<Color>(n) { Color.BLANCO }
    private val dist = IntArray(n) { POSITIVE_INFINITY.toInt() }
    private val ancestro = Array<MutableSet<Int>>(n) { mutableSetOf() }
    private var vFuente = 0

    init {
        if (CicloDigrafo(g).existeUnCiclo()) throw RuntimeException("El grafo no es acíclico.")

        // Buscar el vértice fuente
        for (v in 0 until n) {
            if (g.gradoInterior(v) == 0) {
                vFuente = v 
                break
            }
        }
        
        /* Aplicar BFS modificado desde el vertice fuente
        para hallar los ancestros de cada vértice.*/
        dist[vFuente] = 0
        color[vFuente] = Color.GRIS
        val Q = LinkedList<Int>()
        Q.add(vFuente)
        
        while (!Q.isEmpty()) {
            val u = Q.poll()

            g.adyacentes(u).forEach {
                // Se selecciona el adyacente
                val s = it.elOtroVertice(u)

                // Guardar ancestros del vértice.
                dist[s] = dist[u] + 1
                ancestro[s].addAll(ancestro[u])
                ancestro[s].add(u)

                if (color[s] == Color.BLANCO) {
                    color[s] = Color.GRIS
                    Q.add(s)
                }
            }
            color[u] = Color.NEGRO
        }
    }

    /**
     * Retorna el ancestro común más bajo (LCA) de dos vértices [v] y [u].
     * 
     * @throws [RuntimeException] Alguno de los dos vértices está fuera
     *                            del intervalo [0..|V|).
     * 
     * Tiempo de ejecución: O(V) en el peor caso.
     * Precondición: [v] y [u] pertenecen al conjunto de vértices del dígrafo.
     * Postcondición: [obtenerLCA] es un entero que representa un vértice tal 
     *                que ninguno sus descendientes es ancestro [u] y [v].
     */ 
    fun obtenerLCA(v: Int, u: Int) : Int {
        g.chequearVertice(v)
        g.chequearVertice(u)
        
        // Se busca el ancestro en común con mayor nivel
        val ancestrosComun = ancestro[u].intersect(ancestro[v])
        
        // Si no hay ancestros en común, se retorna -1
        var maxNivel = -1
        var maxNivelVert = -1

        ancestrosComun.forEach {
            if (dist[it] > maxNivel) {
                maxNivel = dist[it]
                maxNivelVert = it
            }
        }

        return maxNivelVert
    }
}