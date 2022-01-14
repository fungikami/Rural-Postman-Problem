/**
 * Autor: Ka Fung & Christopher Gómez
 * Fecha: 16/Ene/2022.
 */

package ve.usb.grafoLib

import kotlin.system.measureTimeMillis
import kotlin.system.exitProcess
import java.io.File
import java.io.FileInputStream
import java.util.Scanner
import java.lang.StringBuilder

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
                // Construye grafo G_t de componentes conexas de G'
                // (El peso de cada arista es el del camino de costo minimo de (vi, vj))
                // 

                // Obtiene el árbol mínimo cobertor


                // Elimina los lados duplicados de E_t0


                // Agrega G' vértices en E_t que no se encuentren VR


                // Agrega a G' los lados E_t, se permite lados duplicados


            }

            if (!esConexo || !esPar(gP)) {
                // Líneas 16 a 23

                // Determina conjunto de vértices V_0 de grado impar 
                // val v0 = MutableSet<Int>() 
                // for (v in 0 until n) {
                //     if (gP.grado(v) % 2 == 1) v0.add(v)
                // }

                // Construye grafo G_0 con V_0
                // val G0 = GrafoNoDirigido(v0.size)


                // Determina apareamiento perfecto M
                // val M = if (usarVertexScan) ApareamientoVertexScan(G0) else ApareamientoPerfectoAvido(G0)

                // M.forEach {
                //     Obtener el CCMvi,vj asociado a (vi, vj);
                //     ParaCada lado (i, j) ∈ CCMvi,vj hacer
                //         si i /∈ G0 entonces se agrega el vértice i a G0
                //         si j /∈ G0 entonces se agrega el vértice j a G0
                //         Se agrega el lado (i, j) a G0 sin importar que se encuentre duplicado
                // }

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

        private fun desv(valorObt: Int, valorOpt: Int): Int {
            return (valorObt - valorOpt) * 100 / valorOpt
        }

        @JvmStatic fun main(args: Array<String>) {
            // Verificar argumentos
            if (args.size != 2 || (args[0] != "a" && args[0] != "v")) {
                println("Error: Usage >./runHeurRPP.sh [a|v] <ruta/a/instancia>")
                exitProcess(1)
            }

            val vertexScan = args[0] == "v"
            val nombreArchivo = args[1]

            // Verifica si el archivo existe
            if (!File(nombreArchivo).exists()) {
                println("El archivo indicado en $nombreArchivo no existe o no se puede leer.")
                exitProcess(1)
            }

            val sc = Scanner(FileInputStream(nombreArchivo))
            sc.nextLine()   // Saltar Nombre
            sc.nextLine()   // Saltar Comentario

            var aux = sc.nextLine()!!.split(" ") // Línea VERTICES : <nro. de vértices>
            val numDeVertices = aux[aux.size - 1].strip()!!.toInt()
            
            // Construye grafo G y conjunto R
            val g = GrafoNoDirigido(numDeVertices)
            val R = mutableSetOf<Arista>()

            aux = sc.nextLine()!!.split(" ")    // Línea ARISTAS_REQ : <nro. de lados requeridos>
            val aristasReq = aux[aux.size - 1].strip()!!.toInt() 

            aux = sc.nextLine()!!.split(" ")    // Línea ARISTAS_NOREQ : <nro. de lados no requeridos>
            val aristasNoReq = aux[aux.size - 1].strip()!!.toInt() 

            // Agregar aristas requeridas a G y R
            sc.nextLine()   // Saltar LISTA_ARISTAS_REQ
            repeat(aristasReq) {
                aux = Regex("[0-9]+").findAll(sc.nextLine()!!)
                    .map(MatchResult::value)
                    .toList()
                
                val a = Arista(
                    aux[0].toInt(), 
                    aux[1].toInt(), 
                    aux[2].toDouble()
                )
                g.agregarArista(a)
                R.add(a)
            }

            // Agregar aristas no requeridas a G
            sc.nextLine()   // Saltar LISTA_ARISTAS_NOREQ
            repeat(aristasReq) {
                aux = Regex("[0-9]+").findAll(sc.nextLine()!!)
                    .map(MatchResult::value)
                    .toList()
                
                val a = Arista(
                    aux[0].toInt(), 
                    aux[1].toInt(), 
                    aux[2].toDouble()
                )
                g.agregarArista(a)
            }

            // Ejecuta el algoritmo RPP
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