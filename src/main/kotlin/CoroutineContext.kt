import kotlinx.coroutines.*
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

private interface CoroutineContextProvider {

    fun context(): CoroutineContext
}

private class CoroutineContextProviderImpl(
    private val context: CoroutineContext
) : CoroutineContextProvider {

    override fun context(): CoroutineContext = context
}

/**
 * Создание контекста
 */
private fun Example1() {
    val defaultDispatcher = Dispatchers.Default
    val coroutineErrorHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("Problems with Coroutine: $throwable")
    }
    val emptyParentJob = Job()
    val combinedContext = defaultDispatcher + coroutineErrorHandler + emptyParentJob
    val contextProvider: CoroutineContextProvider = CoroutineContextProviderImpl(combinedContext)

    GlobalScope.launch(context = contextProvider.context()) {
        println(Thread.currentThread().name)
    }

    Thread.sleep(50)
}

/**
 * Создание кастомного диспатчера
 */
private fun Example2() {
    val executorDispatcher = Executors
        .newWorkStealingPool()
        .asCoroutineDispatcher()

    GlobalScope.launch(context = executorDispatcher) {
        println(Thread.currentThread().name)
    }
    Thread.sleep(50)
}

fun main(args: Array<String>) {
    Example2()
}