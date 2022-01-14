import ve.usb.grafoLib.*

val CARPETA = "GrafosPrueba"
val ANSI_VERDE = "\u001B[32m"
val ANSI_MORADO = "\u001B[36m"
val ANSI_RESET = "\u001B[0m"
val ANSI_SUBRAYADO = "\u001B[4m"

fun main(args: Array<String>) {
    val filename = "$CARPETA/${args[0]}"


    // testAPA(filename)
    // testAPAVS(filename)
    pruebasCicloEuleriano()
}

fun testAPA(filename: String) {
    println("Test de Greedy Perfect Matching\n")
    println("Grafo de prueba: $filename")
    
    val g = GrafoNoDirigido(filename, true)
    val apa = ApareamientoPerfectoAvido(g)

    println("Apareamiento Perfecto obtenido:")
    println(apa.obtenerApareamiento().joinToString(" - "))
}

fun testAPAVS(filename: String) {
    println("Test de Perfect Matching con Vertex Scan\n")
    println("Grafo de prueba: $filename")
    
    val g = GrafoNoDirigido(filename, true)
    val apa = ApareamientoVertexScan(g)

    println("Apareamiento Perfecto obtenido:")
    println(apa.obtenerApareamiento().joinToString(" - "))
}

fun pruebasCicloEuleriano() {
    /* -------------------------------
    Prueba 1
    Prueba de cicloEuleriano.jpeg 
    Grafo de 6 lados.

    Prueba 2
    Prueba de cicloEuleriano2.jpeg 
    Grafo de 10 lados.

    Prueba 3
    Prueba de cicloEuleriano3.jpg
    Grafo de 28 lados.

    Prueba 4
    No es un ciclo euleriano.
    ------------------------------- */
    print(ANSI_SUBRAYADO)
    println("\n${ANSI_VERDE}Casos de prueba para CicloEuleriano:${ANSI_RESET}")
    
    val PRUEBAS = arrayOf(
        "$CARPETA/cicloEuleriano.txt",
        "$CARPETA/cicloEuleriano2.txt",
        "$CARPETA/cicloEuleriano3.txt",
        "$CARPETA/cicloEuleriano4.txt"
    )
    
    // Prueba del ciclo euleriano
    PRUEBAS.forEachIndexed { i, prueba ->
        println(ANSI_MORADO)
        println("\nPrueba ${i + 1}:\n($prueba)")
        println(ANSI_RESET)
        
        val g = GrafoNoDirigido(prueba, false)
        val euler = CicloEulerianoGrafoNoDirigido(g)
        val esEuleriano = euler.tieneCicloEuleriano()
        println("   -Es un grafo euleriano: ${esEuleriano}") // true

        if (esEuleriano) {
            val ciclo = euler.obtenerCicloEuleriano()
            println("LADOS DEL CICLO")
            println(ciclo)
            val n = ciclo.count()

            if (esEuleriano) {
                // Verificación de correctitud del circuito obtenido
                var sumideroAnterior = ciclo.first().cualquieraDeLosVertices()
                var aristaAparece = mutableSetOf<Arista>()

                ciclo.forEach {
                    if (!aristaAparece.add(it)) {
                        println("   -Error: No se obtuvo un ciclo euleriano.")
                        return@forEach
                    }

                    val fuenteActual = it.cualquieraDeLosVertices()
                    if (sumideroAnterior != fuenteActual) {
                        println("   -Error: No se obtuvo un ciclo euleriano.")
                        return@forEach
                    }
                    sumideroAnterior = it.elOtroVertice(fuenteActual)
                }

                val cicloStr = ciclo.joinToString(separator = " -> ") { 
                    "${it.cualquieraDeLosVertices()}"
                }.plus(" -> ${ciclo.last().elOtroVertice(ciclo.last().cualquieraDeLosVertices())}")
                println("   -Circuito euleriano: $cicloStr")
                
                println("   -Lados del grafo: ${g.obtenerNumeroDeLados()}")
                println("   -Lados del ciclo: $n")
            }

        }
    }
}