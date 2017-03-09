package ms.ralph.diffpacker.task

import ms.ralph.diffpacker.task.runner.TaskRunner

class Task(val runner: TaskRunner) {

    private val thread: Thread = Thread(runner)

    fun start() {
        thread.start()
    }

    fun setOnMessageListener(onMessageListener: ((String) -> Unit)?) {
        runner.onMessageListener = onMessageListener
    }

    fun setOnFinishListener(onFinishListener: (() -> Unit)?) {
        runner.onFinishListener = onFinishListener
    }
}