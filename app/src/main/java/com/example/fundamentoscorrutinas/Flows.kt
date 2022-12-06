package com.example.fundamentoscorrutinas

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*
import kotlin.random.Random
import kotlin.system.measureTimeMillis

fun main() {
    //coldFlow()
    //cancelFlow()
    //flowOperators()
    //terminalFlowOperators()
    //bufferFlow()
    //conflationFlow()
    //multiFlow()
    //flatFlows()
    //flowExceptions()
    completions()
}

fun completions() {
    runBlocking {
        newTopic("Fin de un flujo(onCompletion)")
        getCitiesFlow()
            .onCompletion {
                println("\nQuitar el ProgressBar...")
            }/*.collect {
                println(it)
            }*/

        getMatchResultFlow()
            .onCompletion {
                println("\nMostrar las estadisticas del juego")
            }.catch {
                emit("Error $this")
            }/*.collect {
                println(it)
            }*/

        newTopic("Cancelar Flows ")
        getDataByFlowStatic()
            .onCompletion {
                println("Ya no le interesa al usuario....")
            }.cancellable()
            .collect{
                if(it > 22.5f ) cancel()
                println(it)
            }
    }
}

fun flowExceptions() {
    runBlocking {
        newTopic("Control de errores")
        newTopic("TRY/CATCH")
/*        try {
            getMatchResultFlow()
                .collect(){
                    println(it)
                    if(it.contains("2")) throw Exception("Habian acordado 1 a 1 :V")
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }*/

        newTopic("Transparencia, Usar el catch de los Flows")
        getMatchResultFlow()
            .catch {
                emit("Error: $this")
            }
            .collect {
                println(it)
                if (it.contains("Error")) println("Notifica al programador")
            }
    }
}

@OptIn(FlowPreview::class)
fun flatFlows() {
    runBlocking {
        //La idea de estos flujos es que se mezclan en uno solo
        newTopic("Flujos de aplanamiento CONCAT")
        getCitiesFlow()
            .flatMapConcat { cities ->
                getDataToFlatFlow(cities)
            }.map {
                setFormat(it)
            }/*.collect {
                println(it)
            }*/

        newTopic("Flujos de aplanamiento MERGE")
        getCitiesFlow()
            .flatMapMerge { cities ->
                getDataToFlatFlow(cities)
            }.map {
                setFormat(it)
            }.collect {
                println(it)
            }
    }
}

fun getDataToFlatFlow(city: String): Flow<Float> = flow {
    (1..3).forEach {
        println("Temperatura de ayer en $city...")
        emit(Random.nextInt(10, 30).toFloat())

        println("Temperatura actual en $city...")
        delay(150)
        emit(20 + it + Random.nextFloat())
    }
}

fun getCitiesFlow(): Flow<String> = flow {
    listOf("Santander", "CDMX", "Lima").forEach { city ->
        println("\nConsultando ciudad.....")
        delay(1000)
        emit(city)
    }
}

fun multiFlow() {
    runBlocking {
        newTopic("Zip y Combine")
        //Una forma de mezclar dos flujos, composición de flujos
        getDataByFlowStatic()
            .map {
                setFormat(it)
            }/*.zip(getMatchResultFlow()){ grados, resultadosPartido ->
                "$resultadosPartido with $grados"
            }*/
            .combine(getMatchResultFlow()) { grados, resultadosPartido ->
                "$resultadosPartido with $grados"
            }.collect {
                println(it)
            }
    }
}

fun conflationFlow() {
    runBlocking {
        newTopic("FUSION")
        val time = measureTimeMillis {
            getMatchResultFlow()
                .conflate()
                .collect {
                    delay(100)
                    println(it)
                }
        }
        println("Time: ${time}ms")
    }
}

fun getMatchResultFlow(): Flow<String> =  flow {
        var equipoCasa = 0
        var equipoVisitante = 0
        (0..45).forEach {
            println("Minuto: $it")
            delay(50)
            equipoCasa += kotlin.random.Random.nextInt(0, 21) / 20
            equipoVisitante += kotlin.random.Random.nextInt(0, 21) / 20
            emit("HomeTeam-> $equipoCasa - $equipoVisitante <-AwayTeam")

            if (equipoCasa == 2 || equipoVisitante == 2) throw Exception("Habian acordado 1 a 1 :V")
        }
}


fun bufferFlow() {
    runBlocking {
        newTopic("Buffer para Flow")
        //Tareas en paralelo como las de Async pero dentro de un flow
        //Este metodo measureTimeMillis captura el tiempo total gastado en la ejecución
        //del bloque de código que contiene
        val time = measureTimeMillis {
            getDataByFlowStatic()
                .map {
                    setFormat(it)
                }
                .buffer()
                .collect {
                    delay(500)
                    println(it)
                }
        }
        println("Time: ${time}ms")

    }
}

fun getDataByFlowStatic(): Flow<Float> {
    return flow {
        (1..5).forEach {
            println("Procesando datos.......")
            delay(300)
            emit(20 + it + Random.nextFloat())
        }
    }
}

fun terminalFlowOperators() {
    runBlocking {
        newTopic("Operadores terminales")
        newTopic("LIST")
        val list = getDataByFlow()
            .map {
                //Como en las lambdas la ultima linea es el retorno del método
                setFormat(it)
                setFormat(convertCelsToFahr(it), "F")
            }
        //.toList()
        //println("list: $list")

        newTopic("SINGLE")
        //Si el flujo tiene más de un elemento, nos da Error
        //Porque single espera un flow de 1 solo elemento
        val single = getDataByFlow()
            //.take(1)
            .map {
                //Como en las lambdas la ultima linea es el retorno del método
                setFormat(it)
                setFormat(convertCelsToFahr(it), "F")
            }
        //.single()
        //println("Single: $single")

        newTopic("FIRST")
        val first = getDataByFlow()
            //.take(1)
            .map {
                //Como en las lambdas la ultima linea es el retorno del método
                setFormat(it)
                setFormat(convertCelsToFahr(it), "F")
            }
        //.first()
        //println("First: $first")

        newTopic("LAST")
        val last = getDataByFlow()
            //.take(1)
            .map {
                //Como en las lambdas la ultima linea es el retorno del método
                setFormat(it)
                setFormat(convertCelsToFahr(it), "F")
            }
        //.last()
        //println("Last: $last")

        newTopic("REDUCE")
        val saving = getDataByFlow()
            //Cuando usamos reduce el valor inicial será el que este en la
            //primera posición del flujo
            .reduce { accumulator, value ->
                println("Acumulador: $accumulator")
                println("Valor: $value")
                accumulator + value
            }
        //println("Guardado: $saving")

        newTopic("FOLD")
        val lastSaving = getDataByFlow()
            //Cuando usamos fold el valor inicial será un valor dado por nosotros
            .fold(saving) { accumulator, value ->
                println("Acumulador: $accumulator")
                println("Valor: $value")
                accumulator + value
            }
        println("Ultimo Guardado: $lastSaving")
    }
}

fun flowOperators() {
    runBlocking {
        newTopic("Operadores intermedios")
        newTopic("MAP")
        getDataByFlow()
            .map {
                //Como en las lambdas la ultima linea es el retorno del método
                setFormat(it)
                setFormat(convertCelsToFahr(it), "F")
            }
        //.collect { println(it) }

        newTopic("FILTER")
        getDataByFlow()
            .filter {
                it > 23
            }.map {
                setFormat(it)
            }
        //.collect { println(it) }

        newTopic("TRANSFORM")
        getDataByFlow()
            .transform {
                //Como transform usa emit para retornar el dato al flujo podemos
                //devolver más de un valor a la vez
                emit(setFormat(it))
                emit(setFormat(convertCelsToFahr(it), "F"))
            }
        //.collect{ println(it) }

        newTopic("TAKE")
        getDataByFlow()
            //Solo toma la cantidad de valores indicada del flow
            // Es limitar el tamaño del flujo a recolectar
            .take(3)
            .map {
                setFormat(it)
            }
        //.collect{ println(it) }


    }
}

fun convertCelsToFahr(cels: Float): Float = ((cels * 9) / 5) + 32

fun setFormat(temp: Float, degree: String = "C"): String =
    String.format(Locale.getDefault(), "%.1f°$degree", temp)

fun cancelFlow() {
    runBlocking {
        newTopic("Cancelar Flow")
        val job = launch {
            getDataByFlow().collect { println(it) }
        }
        delay(1750)
        job.cancel()
    }

}

fun coldFlow() {
    newTopic("Flows are Cold")
    runBlocking {
        val dataFlow: Flow<Float> = getDataByFlow()
        println("Esperando")
        delay(1800)
        dataFlow.collect {
            println(it)
        }
    }
}
