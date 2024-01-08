import kotlinx.coroutines.*

/**
 * Т.к. используем GlobalScop, то время жизни корутины привязано к времени жизни приложения,
 * поэтому мы его задаем, используя Thread.sleep()
 */
@OptIn(DelicateCoroutinesApi::class)
private suspend fun Examples1() {
    (1..10000).forEach {
        GlobalScope.launch {
            val threadName = Thread.currentThread().name
            println("$it printed on thread $threadName")
        }
    }
    Thread.sleep(1000)
}

@OptIn(DelicateCoroutinesApi::class)
private suspend fun Examples2() {
    GlobalScope.launch {
        println("Hello coroutine!")
        delay(500) //Приостанавливает выполнение корутины
        println("Right back at ya!")
    }
    Thread.sleep(1000)
}

@OptIn(DelicateCoroutinesApi::class)
private suspend fun Examples3() {
    val job1 = GlobalScope.launch(start = CoroutineStart.LAZY) {
        delay(200)
        println("Pong")
        delay(200)
    }
    GlobalScope.launch {
        delay(200)
        println("Ping")
        job1.join()
        println("Ping")
        delay(200)
    }
    Thread.sleep(1000)
}

@OptIn(DelicateCoroutinesApi::class)
private suspend fun Examples4() {
    with(GlobalScope) {
        val parentJob = launch {
            delay(200)
            println("I’m the parent")
            delay(200)
        }
        launch(context = parentJob) {
            delay(200)
            println("I’m a child")
            delay(200)
        }
        if (parentJob.children.iterator().hasNext()) {
            println("The Job has children!")
        } else {
            println("The Job has NO children")
        }
        Thread.sleep(1000)
    }
}

@OptIn(DelicateCoroutinesApi::class)
private suspend fun Examples5() {
    var isDoorOpen = false
    println("Unlocking the door... please wait.\n")
    GlobalScope.launch {
        delay(3000)
        isDoorOpen = true
    }
    GlobalScope.launch {
        repeat(4) {
            println("Trying to open the door...\n")
            delay(800)
            if (isDoorOpen) {
                println("Opened the door!\n")
            } else {
                println("The door is still locked\n")
            }
        }
    }
    Thread.sleep(5000)
}

fun main(args: Array<String>) = runBlocking {
    Examples5()
}
