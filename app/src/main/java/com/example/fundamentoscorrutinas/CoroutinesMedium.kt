package com.example.fundamentoscorrutinas

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

fun main() {
    //dispatchers()
    //nested() //<-Anidar corrutinas
    //changewithContext()
    basicFlows()
}

fun basicFlows() {
    newTopic("Flows básicos")
    runBlocking {
        launch {
            getDataByFlow().collect{
                println("$it C°")
            }
        }
        launch(Dispatchers.IO) {
            (1..50).forEach { _ ->
                delay(180)
                println("Tarea 2....")
            }
        }
    }
}

fun getDataByFlow(): Flow<Float> {
    return flow {
        (1..5) .forEach {
            println("Procesando datos.......")
            delay(1700)
            emit( 20 + it + Random.nextFloat())
        }
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun changewithContext() {
    runBlocking {
        newTopic("WithContext")
        startMessage()
            withContext(newSingleThreadContext("Curso Android")){
                startMessage()
                delay(1200)
                println("Prueba withContext")
                endMessage()
            }
            withContext(Dispatchers.IO){
                startMessage()
                delay(1200)
                println("Simulación de petición al servidor")
                endMessage()
            }
        endMessage()
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun nested() {
    runBlocking {
        newTopic("Anidar")

        val job = launch {
            startMessage()

            launch {
                startMessage()
                delay(2000)
                println("Otra tarea")
                endMessage()
            }

            launch(Dispatchers.IO){
                startMessage()

                launch(newSingleThreadContext("Curso Android")) {
                    startMessage()
                    println("Otra tarea interna del servidor 2")
                    endMessage()
                }

                delay(1500)
                println("Tarea en el servidor 1")
                endMessage()
            }

            var sum = 0
            (1..200).forEach {
                sum += it
                delay(150)
            }

            println("Sum = $sum")

            endMessage()
        }

        delay(700)
        job.cancel()
        println("Job cancelado...")

    }
}

@OptIn(DelicateCoroutinesApi::class)
fun dispatchers() {
    runBlocking {
        newTopic("Dispatchers")

        launch {
            startMessage()
            println("None")
            endMessage()
        }

        launch(Dispatchers.IO) {
            startMessage()
            println("IO")
            endMessage()
        }

        launch(Dispatchers.Default) {
            startMessage()
            println("Default")
            endMessage()
        }

        launch(Dispatchers.Unconfined) {
            startMessage()
            println("Unconfined")
            endMessage()
        }

        //Dispatcher personalizado
        launch(newSingleThreadContext("Curso Android")) {
            startMessage()
            println("Personalizado")
            endMessage()
        }

        //Otra forma de crear un Distpacher personalizado
        newSingleThreadContext("Curso Android 2").use {
            launch(it) {
                startMessage()
                println("Personalizado 2")
                endMessage()
            }
        }
    }
}
