package ms.ralph.diffpacker.task.runner

abstract class TaskRunner : Runnable {
    internal var onMessageListener: ((String) -> Unit)? = null
    internal var onFinishListener: (() -> Unit)? = null
}