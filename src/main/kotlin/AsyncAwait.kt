import kotlinx.coroutines.*
import java.io.DataInput
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future

private val executor = Executors.newSingleThreadExecutor()

private fun parse(input: String): Future<Int> {
    return executor.submit(
        Callable {
            Thread.sleep(1000)
            input.toInt()
        }
    )
}

private suspend fun getFirstName() = GlobalScope.async {
    delay(1000)
    "Danil"
}

private suspend fun getLastName() = GlobalScope.async {
    delay(2000)
    "Bychkov"
}

private fun printName(firstName: String, lastName: String) {
    println("$firstName $lastName")
}

private suspend fun getUserByIdFromNetwork(userId: Int) =
    GlobalScope.async {
        println("Retrieving user from network")
        delay(3000)
        println("Still in the coroutine")
        "Filip Babic $userId" // we simulate the network call
    }

private suspend fun getUserByIdFromNetworkAndCheckStateOfScope(
    userId: Int,
    parentScope: CoroutineScope
) = parentScope.async {
    if (!isActive) {
        return@async "null"
    }
    println("Retrieving user from network")
    delay(3000)
    println("Still in the coroutine")
    "Filip Babic $userId" // we simulate the network call
}

private fun Example1() {
    val parse = parse("101")

    while (parse.isDone) {
        //waiting to parse
    }

    println(parse.get())
}

private fun Example2() {
    GlobalScope.launch {
        val firstName = getFirstName()
        val lastName = getLastName()
        printName(firstName.await(), lastName.await())
    }
    Thread.sleep(2100)
}

/**
 * Если мы передаем в качестве parentScope-а скоуп в котором он выполняется, то при отмене отменится и вополнение программы
 * Если передать новый объект GlobalScope, то программа выполнится доконца несмотря на отмену родительского скоупа
 */
private fun Example3() {
    val launch = GlobalScope.launch {
        //val dataDeferred = getUserByIdFromNetworkAndCheckStateOfScope(1312, GlobalScope)
        val dataDeferred = getUserByIdFromNetworkAndCheckStateOfScope(1312, this)
        println("Not cancelled")
        // do something with the data

        println(dataDeferred.await())
    }
    Thread.sleep(50)
    launch.cancel()
    while (true) { // stops the program from finishing
    }
}

fun main(args: Array<String>) {
    Example1()
}
