import kotlinx.coroutines.*

private fun Example1() = runBlocking {
    val launchJob = GlobalScope.launch {
        println("1. Exception created via launch coroutine")
        throw IndexOutOfBoundsException()
    }
    launchJob.join()
    println("2. Joined failed job")
    val deferred = GlobalScope.async {
        println("3. Exception created via async coroutine")
        throw ArithmeticException()
    }
    try {
        deferred.await()
        println("4. Unreachable, this statement is never executed")
    } catch (e: Exception) {
        println("5. Caught ${e.javaClass.simpleName}")
    }
}

private fun Example2() = runBlocking {
    val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        println("Caught $exception")
    }
    val job = GlobalScope.launch(exceptionHandler) {
        throw AssertionError("My Custom Assertion Error!")
    }
    val deferred = GlobalScope.async(exceptionHandler) {
        // Nothing will be printed,
        // relying on user to call deferred.await()
        throw ArithmeticException()
    }
    // This suspends current coroutine until all given jobs are
    joinAll(job, deferred)
}

private fun Example3() = runBlocking {
    val callAwaitOnDeferred = false
    val deferred = GlobalScope.async {
        // This statement will be printed with or without
        // a call to await()
        println("Throwing exception from async")
        throw ArithmeticException("Something Crashed")
        // Nothing is printed, relying on a call to await()
    }
    if (callAwaitOnDeferred) {
        try {
            deferred.await()
        } catch (e: ArithmeticException) {
            println("Caught ArithmeticException")
        }
    }
}

private fun Example4() = runBlocking {
    // Global Exception Handler
    val handler = CoroutineExceptionHandler { _, exception ->
        println("Caught $exception with suppressed ${exception.suppressed?.contentToString()}")
    }
    // Parent Job
    val parentJob = GlobalScope.launch(handler) {
        // Child Job 1
        launch {
            try {
                delay(Long.MAX_VALUE)
            } catch (e: Exception) {
                println("${e.javaClass.simpleName} in Child Job 1")
            } finally {
                throw ArithmeticException()
            }
        }
        // Child Job 2
        launch {
            delay(100)
            throw IllegalStateException()
        }
        // Delaying the parentJob
        delay(Long.MAX_VALUE)
    }
    // Wait until parentJob completes
    parentJob.join()
}

private fun Example5() = runBlocking {
    // 1
    val supervisor = SupervisorJob()
    with(CoroutineScope(coroutineContext + supervisor)) {
        // 2
        val firstChild = launch {
            println("First child throwing an exception")
            throw ArithmeticException()
        }
        // 3
        val secondChild = launch {
            println("First child is cancelled: ${firstChild.isCancelled}")
            try {
                delay(5000)
            } catch (e: CancellationException) {
                println("Second child cancelled because supervisor got cancelled.")
            }
        }
        // 4
        firstChild.join()
        println("Second child is active: ${secondChild.isActive}")
        supervisor.cancel()
        secondChild.join()
    }
}

private fun Example6() = runBlocking {
    val result = async {
        println("Throwing exception in async")
        throw IllegalStateException()
    }
    try {
        result.await()
    } catch (e: Exception) {
        println("Caught $e")
    }
}

private fun Example7() = runBlocking {
    supervisorScope { //Если не установить supervisorScope, то ошибка будет проброшена
        val result = async {
            println("Throwing exception in async")
            throw IllegalStateException()
        }
        try {
            result.await()
        } catch (e: Exception) {
            println("Caught $e")
        }
    }
}

fun main(args: Array<String>)  {
    Example7()
}