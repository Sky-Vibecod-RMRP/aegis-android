package io.github.dovecoteescapee.byedpi.core

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ByeDpiProxy {
    companion object {
        init {
            System.loadLibrary("byedpi")
        }
    }

    private val mutex = Mutex()
    private var fd = -1

    suspend fun startProxy(cmdArgs: Array<String>): Int = mutex.withLock {
        if (fd >= 0) {
            return 0
        }
        val socketFd = jniCreateSocketWithCommandLine(cmdArgs)
        if (socketFd < 0) {
            return -1
        }
        this.fd = socketFd
        return jniStartProxy(socketFd)
    }

    suspend fun stopProxy(): Int = mutex.withLock {
        if (fd < 0) {
            return 0
        }
        val result = jniStopProxy(fd)
        fd = -1
        return result
    }

    private external fun jniCreateSocketWithCommandLine(args: Array<String>): Int
    private external fun jniStartProxy(fd: Int): Int
    private external fun jniStopProxy(fd: Int): Int
}
