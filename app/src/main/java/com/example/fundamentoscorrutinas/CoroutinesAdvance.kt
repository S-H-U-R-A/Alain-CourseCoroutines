package com.example.fundamentoscorrutinas

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import java.util.concurrent.TimeoutException

val cities = listOf("Santander", "CDMX", "Lima", "Buenos Aires", "Alicante")

fun main() {
    //basicChannel()
    //closeChannel()
    //produceChannel()
    //pipeLines()
    //bufferChannels()
    exceptions()


    readLine()//Usado para poder ver los errores
}

fun exceptions() {
    //Como controlar las Excepciones en Corrutinas
    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("Notifica al programador... $throwable en $coroutineContext")

        if(throwable is ArithmeticException) println("Mostrar mensajes de reintentar\n")
    }

    runBlocking {
        newTopic("Manejo de Excepciones en Corrutinas")

        launch {
            try {
                delay(100)
                //throw Exception()
            }catch (e: Exception){
                e.printStackTrace()
            }
        }

        val globalScope = CoroutineScope(Job() + exceptionHandler)
        globalScope.launch {
            delay(200)
            throw TimeoutException()
        }

        CoroutineScope(Job() + exceptionHandler).launch {
            val result = async {
                delay(500)
                multilambda(2, 3){
                    if(it > 5) throw ArithmeticException()
                }
            }
            println( "Result: ${ result.await() }")
        }

        //EXCEPTIONHANDLER WITH CHANNELS
        val channel = Channel<String>()
        CoroutineScope( Job() ).launch( exceptionHandler ) {
            delay(800)
            cities.forEach {
                channel.send(it)
                if( it == "Lima" ) channel.close()
            }
        }

        channel.consumeEach { println(it) }
    }
}

fun bufferChannels() {
    runBlocking {
        newTopic("CHANNEL SIN USAR BUFFER")
        val time = System.currentTimeMillis()
        val channel = Channel<String>()
        launch {
            cities.forEach {
                delay(150)
                channel.send(it)
            }
            //BUENA PRACTICA CERRAR LOS CANALES
            channel.close()
        }
        launch {
            delay(1000)
            channel.consumeEach { println(it) }
            println("Time: ${System.currentTimeMillis() - time}")
        }

        newTopic("USANDO BUFFER PARA MEJORAR RENDIMIENTO")
        val time2 = System.currentTimeMillis()
        val channel2 = Channel<String>(3)//<- CAPACITY INDICA EL BUFFER
        launch {
            cities.forEach {
                delay(150)
                channel2.send(it)
            }
            //BUENA PRACTICA CERRAR LOS CANALES
            channel2.close()
        }
        launch {
            delay(1000)
            channel2.consumeEach { println(it) }
            println("Buffer Time: ${System.currentTimeMillis() - time2}")
        }
    }
}

fun pipeLines() {
    runBlocking {
        newTopic("PipeLines")
        val citiesChannel = produceCities() // <- CHANNEL PRODUCTOR
        val foodsChannel = produceFoods(citiesChannel)// CHANNEL CONSUMIDOR DEL ANTERIOR, PARA PRODUCIR SU DATA, ESTO ES LO SE LLAMA PIPELINE

        foodsChannel.consumeEach { println(it) }

        //COMO BUENA PRACTICA CERRAMOS LOS DOS CANALES
        citiesChannel.cancel()
        foodsChannel.cancel()

    }
}

fun produceChannel() {
    runBlocking {
        newTopic("Canales y el patrón productor-consumidor")
        val names = produceCities()
        names.consumeEach { println(it) }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
fun CoroutineScope.produceFoods(cities: ReceiveChannel<String>): ReceiveChannel<String> = produce {
    for(city in cities){
        val food = getFoodByCity(city)
        send("$food desde $city")
    }
}

suspend fun getFoodByCity(city: String): String {
    delay(400)
    return  when(city){
        "Santander" -> "Arepa"
        "CDMX" -> "Taco"
        "Lima" -> "Ceviche"
        "Buenos Aires" -> "Milanesa"
        "Alicante" -> "Paella"
        else -> "Sin data"
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
fun CoroutineScope.produceCities(): ReceiveChannel<String> = produce {
    cities.forEach { send(it) }
}

fun closeChannel() {
    runBlocking {
        newTopic("Cerrar un Canal")

        val channel = Channel<String>()

        launch {
            cities.forEach {
                channel.send(it)

                //Esto cerraría el Channel pero daría un Exception
                //if(it == "Lima") channel.close()

                //Otra forma de cerrar el channel retornando a launch en este caso, sin ninguna Exception
                if (it == "Lima") {
                    channel.close()
                    return@launch
                }
            }
            //Cerrando el Channel una vez completado
            //channel.close()
        }

/*        for(value in channel){
            println(value)
        }*/

        //OTRA FORMA DE CONSUMIR UN CHANNEL
        //Si el canal no esta cerrado
/*        while (!channel.isClosedForReceive){
            println( channel.receive())
        }*/

        //OTRA FORMA DINAMICA DE CONSUMIR EL CHANNEL
        channel.consumeEach { println(it) }

    }
}

fun basicChannel() {
    runBlocking {
        newTopic("Canal básico")
        //Es como un tipo de dato envoltorio que permite cambiar flows entre corrutinas
        val channel = Channel<String>()

        launch {
            cities.forEach {
                channel.send(it)
            }
        }

        //Mediante un Loop podemos ir recuperando los valores del Channel
        //Esto aplicaria si usamos por ejemplo un for clasico
        repeat(5) {
            println(channel.receive())
        }

/*        for(value in channel){
            println(value)
        }*/


    }
}
