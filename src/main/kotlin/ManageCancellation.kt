import kotlinx.coroutines.*
import java.io.IOException

private fun Example1() = runBlocking {
    val startTime = System.currentTimeMillis()
    val job = launch(Dispatchers.Default) {
        var nextPrintTime = startTime
        var i = 0
        while (i < 10 && isActive) { //Если не проверять флаг isActive то корутина будет выполняться вечно
            if (System.currentTimeMillis() >= nextPrintTime) {
                println("Doing heavy work: $i")
                i++
                nextPrintTime += 500L
            }
        }
    }
    delay(1000)
    println("Cancelling coroutine")
    job.cancel()
    println("Main: now I can quit!")
}

private fun Example2() = runBlocking {
    val job = launch(Dispatchers.Default) {
        var i = 0
        while (i < 1000) {
            println("Doing heavy work ${i++}")
            delay(500) // так как delay проверяет флаг isActive то выполнение будет остановлено
        }
    }
    delay(1200)
    println("Cancelling")
    job.cancel()
    println("Main: Now I can quit!")
}

private fun Example3() = runBlocking {
    // 1
    val handler = CoroutineExceptionHandler { _, exception ->
        // 6
        println("Caught original $exception")
    }
    // 2
    val parentJob = GlobalScope.launch(handler) {
        val childJob = launch {
            // 4
            throw IOException()
        }
        try {
            childJob.join()
        } catch (e: CancellationException) {
            // 5
            println("Rethrowing CancellationException with original cause: ${e.cause}")
            throw e
        }
    }
    // 3
    parentJob.join()
}

private fun Example4() = runBlocking {
    val jobOne = launch {
        println("Job 1: Crunching numbers [Beep.Boop.Beep]…")
        delay(2000L)
    }
    val jobTwo = launch {
        println("Job 2: Crunching numbers [Beep.Boop.Beep]…")
        delay(500L)
    }
    // waits for both the jobs to complete
    joinAll(jobOne, jobTwo)
    println("main: Now I can quit.")
}

private fun Example5() = runBlocking {
    val job = launch {
        repeat(1000) { i ->
            println("$i. Crunching numbers [Beep.Boop.Beep]…")
            delay(500L)
        }
    }
    delay(1300L) // delay a bit
    println("main: I am tired of waiting!")
    // cancels the job and waits for job’s completion
    job.cancelAndJoin()
    println("main: Now I can quit.")
}

private fun Example6() = runBlocking {
    val parentJob = launch {
        val childOne = launch {
            repeat(1000) { i ->
                println(
                    "Child Coroutine 1: " +
                            "$i. Crunching numbers [Beep.Boop.Beep]…"
                )
                delay(500L)
            }
        }
        // Handle the exception thrown from `launch`
        // coroutine builder
        childOne.invokeOnCompletion { exception ->
            println("Child One: ${exception?.message}")
        }
        val childTwo = launch {
            repeat(1000) { i ->
                println(
                    "Child Coroutine 2: " +
                            "$i. Crunching numbers [Beep.Boop.Beep]…"
                )
                delay(500L)
            }
        }

        // Handle the exception thrown from `launch`
        // coroutine builder
        childTwo.invokeOnCompletion { exception ->
            println("Child Two: ${exception?.message}")
        }

    }
    delay(1200L)

    println("Calling cancelChildren() on the parentJob")
    parentJob.cancelChildren()

    println("parentJob isActive: ${parentJob.isActive}")
}

private fun Example7() = runBlocking {
    withTimeout(1500L) {
        repeat(1000) { i ->
            println("$i. Crunching numbers [Beep.Boop.Beep]...")
            delay(500L)
        }
    }
}


fun main(args: Array<String>) {
    Example7()
}