import ve.usb.grafoLib.*

val CARPETA = "GrafosPrueba/"

fun main(args: Array<String>) {
    val filename = "$CARPETA/${args[0]}"


    // testAPA(filename)
    
    testAPAVS(filename)
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