/**
 * Autor: Christopher Gómez.
 * Fecha: 8/nov/2021
 */

package ve.usb.grafoLib
import kotlin.Double.Companion.POSITIVE_INFINITY

/**
 * Retorna el grafo inverso de [g].
 * 
 * Tiempo de ejecución: O(|V| + |E|) en el peor caso.
 * Precondición: [g] es un grafo dirigido.
 * Postcondición: El grafo resultante es un grafo inverso de g.
 */
fun dígrafoInverso(g: GrafoDirigido): GrafoDirigido {
    val gInverso = GrafoDirigido(g.obtenerNumeroDeVertices())

    g.arcos().forEach {
        gInverso.agregarArco(Arco(it.sumidero(), it.fuente()))
    }

    return gInverso
}

/**
 * Retorna la matriz de costos asociada al grafo g.
 *
 * Tiempo de ejecución: O(V²)
 * Precondición: [g] es un grafo dirigido.
 * Postcondición: [matrizDeCostos] es la matriz de costos asociada a g.
 */
fun matrizDeCostos(g: GrafoDirigido): Array<Array<Double>> {
    val n = g.obtenerNumeroDeVertices()

    val W = Array<Array<Double>>(n) { i ->
        Array<Double>(n) { j ->
            if (i == j) 0.0 else POSITIVE_INFINITY
        }
    }

    g.arcos().forEach {
        val i = it.fuente()
        val j = it.sumidero()

        if (i != j) W[i][j] = it.peso()
    }

    return W
}