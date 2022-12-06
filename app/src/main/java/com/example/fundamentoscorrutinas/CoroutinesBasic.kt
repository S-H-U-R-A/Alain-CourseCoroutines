package com.example.fundamentoscorrutinas

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce

fun main() {
    //globalScope()
    //suspendFun()
    //consLaunch()
    //consAsync()
    //job()
    //deferred()
    //consProduce()

    readLine()
}

fun consProduce() = runBlocking{
    newTopic("Produce")
    this.produceName().consumeEach {
        println(it)
    }

}

@OptIn(ExperimentalCoroutinesApi::class)
fun CoroutineScope.produceName(): ReceiveChannel<String> = produce {
    (1..5).forEach { send("name$it") }
}

fun deferred() {
    runBlocking {
        newTopic("Deferred")
        val deferred = async {
            startMessage()
            delay(2000)
            endMessage()
            // 2 //<- Ejemplo de último elemento retornado
            return@async 2 // Otra forma de retornar de ASYNC
        }
        println("Deferred: -> $deferred ")
        println("Deferred.awwait(): -> ${ deferred.await() }")
    }
}

fun job() {
    runBlocking {
       newTopic("Job")
        val job = launch {
            startMessage()
            delay(2000)
            endMessage()
        }
        //CON ESTAS CONSTANTES PODRIAMOS HACER VALIDACIONES
        println("Esta activo -> ${job.isActive}")
        println("Esta cancelado -> ${job.isCancelled}")
        println("Esta completo -> ${job.isCompleted}")

        //CANCELANDO O INTERRUMPIENDO LA CORRUTINA
        delay(1500)
        job.cancel()

        println("Esta activo -> ${job.isActive}")
        println("Esta cancelado -> ${job.isCancelled}")
        println("Esta completo -> ${job.isCompleted}")
    }
}

fun consAsync() {
    runBlocking {
        //La última linea en async aplica el funcionamiento
        //de las lambdas de retornan la última linea
        val res = async {
            startMessage()
            delay(2000)
            endMessage()
            // 2 //<- Ejemplo de último elemento retornado
            return@async 2 // Otra forma de retornar de ASYNC
        }
        println("Async: -> ${ res.await() }")
    }
}

fun consLaunch() {
    runBlocking {
        launch {
            startMessage()
            delay(1000)
            endMessage()
        }
    }
}

fun suspendFun() {
    newTopic("Supend")

}

@OptIn(DelicateCoroutinesApi::class)
fun globalScope() {

    newTopic("Global Scope")

    GlobalScope.launch {
        startMessage()
        delay(2000)
        println("Mi corrutina")
        endMessage()
    }

}

fun startMessage() {
    println("\nComenzando corrutina en el hilo ---${Thread.currentThread().name}---\n")
}

fun endMessage() {
    println("\nFinalizando corrutina en el hilo ---${Thread.currentThread().name}---\n")
}