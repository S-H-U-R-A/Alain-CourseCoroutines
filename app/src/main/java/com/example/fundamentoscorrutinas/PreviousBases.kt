package com.example.fundamentoscorrutinas

import kotlin.random.Random

private const val SEPARTOR = "================="

fun main(){
    //lambda()
    sequences()

}

fun sequences() {
    newTopic("Sequences")
    getDataBySequence().forEach {
        println("${it} C°")
    }
}

fun getDataBySequence(): Sequence<Float> {
    return sequence {
        (1..5) .forEach {
            println("Procesando datos.......")
            Thread.sleep(1700)
            yield( 20 + it + Random.nextFloat())
        }
    }
}

fun newTopic(topic: String){
    println("\n$SEPARTOR $topic $SEPARTOR\n")
}

fun lambda() {
    println( multi(2,3))

    multilambda(2,3){ result ->
        println(result)
    }

    sumLambda(2){ res ->
        (res * res).toDouble()
    }
}

//Función que retorna la lambda que recibe
fun multilambda(x: Int, y: Int, callback: ( result:Int )-> Unit ) {
    callback(x*y)
}

//Función que recibe un lambda como parametro y la usa para un cálculo
//Interno que hace la suma de dos elementos
fun sumLambda(x: Int, res: (x: Int) -> Double): Double {
    val y = x*x
    println(y + res(x))
    return y + res(x)
}


fun multi(x: Int, y: Int): Int {
    return x * y
}


