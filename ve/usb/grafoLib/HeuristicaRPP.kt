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
                // 

                //

                //
            }

            if (!esConexo || !esPar(gP)) {
                // Líneas 16 a 23
            }

            gP.agregarArista(Arista(0, 1, 2.0))

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
            if (args.size != 2 || (args[0] != "a" && args[0] != "v")) {
                println("Error: Usage >./runHeurRPP.sh [a|v] <ruta/a/instancia>")
                exitProcess(1)
            }

            val vertexScan = args[0] == "v"
            val nombreArchivo = args[1]

            // NOMBRE : <name of instance>
            // COMENTARIO : <comment>
            // VERTICES : <number of vertices>
            // ARISTAS_REQ : <number of required edges>
            // ARISTAS_NOREQ : <number of non-required edges>
            // LISTA_ARISTAS_REQ :
            // (<u1>,<u2>) coste <cu1> <cu2>
            // .
            // .
            // .
            // LISTA_ARISTAS_NOREQ :
            // (<v1>,<v2>) coste <cv1> <cv2>
            // .
            // .
            // .

            if (File(nombreArchivo).exists()) {
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

                // Ejecuta el algoritmo de RPP
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

            } else {
                println("El archivo indicado en $nombreArchivo no existe o no se puede leer.")
                return
            } 
        }
    }
}