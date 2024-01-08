import kotlinx.coroutines.*
import kotlin.concurrent.thread

private data class User(
    val userId: String,
    val name: String
)

private fun getUserStandard(userId: String): User {
    Thread.sleep(1000)
    return User(userId, "Filip")
}

private fun getUserFromNetworkCallback(
    userId: String,
    onUserReady: (User) -> Unit
) {
    thread {
        Thread.sleep(1000)
        val user = User(userId, "Filip")
        onUserReady(user)
    }
    println("end")
}

private fun getUserFromNetworkCallback(
    userId: String,
    onUserResponse: (User?, Throwable?) -> Unit
) {
    thread {
        try {
            Thread.sleep(1000)
            val user = User(userId, "Filip")
            onUserResponse(user, null)
        } catch (error: Throwable) {
            onUserResponse(null, error)
        }
    }
    println("end")
}

private suspend fun getUserSuspend(userId: String): User {
    delay(1000)
    return User(userId, "Filip")
}

private fun executeDefault(action: suspend () -> Unit) {
    GlobalScope.launch(context = Dispatchers.Default) { action() }
}

private fun Example1() {
    println(getUserStandard("1"))
}

private fun Example2() {
    getUserFromNetworkCallback("1") { user ->
        println(user)
    }
    println("main end")
}

private fun Example3() {
    getUserFromNetworkCallback("101") { user, throwable ->
        user?.run(::println)
        throwable?.printStackTrace()
    }
    println("main end")
}

private suspend fun Example4() {
    val user = getUserSuspend("12")
    println(user)
}

/**
 *  Tools ▶︎ Kotlin ▶︎ Show Kotlin Bytecode
 */
fun main(args: Array<String>) = runBlocking {
    executeDefault {
        Example4()
    }
    Thread.sleep(2000)
}
