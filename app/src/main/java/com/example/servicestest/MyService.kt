package com.example.servicestest

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.*

class MyService : Service() {
    private val scope = CoroutineScope(Dispatchers.Main)
    override fun onCreate() {
        super.onCreate()
        log("onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        scope.launch {
            countDown()
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO()
    }


    override fun onDestroy() {
        super.onDestroy()
        log("onDestroy")
        scope.cancel()
    }

    private fun log(text: String) {
        Log.d("TEST_TAG", "MyService: $text")
    }

    private suspend fun countDown() {
        for (i in 0 until 60) {
            log("timer is: $i")
            delay(1000)
        }
    }

    companion object {
        fun newInstance(context: Context): Intent {
            return Intent(context, MyService::class.java)
        }
    }

}