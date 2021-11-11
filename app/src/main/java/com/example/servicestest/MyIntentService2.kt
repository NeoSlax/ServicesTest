package com.example.servicestest

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class MyIntentService2 : IntentService(SERVICE_NAME) {

    override fun onCreate() {
        super.onCreate()
        log("onCreate")
        setIntentRedelivery(true)
    }

    override fun onHandleIntent(intent: Intent?) {
        val page = intent?.getIntExtra(PAGE, 0) ?: 0
        countDown(page)
    }

    override fun onDestroy() {
        super.onDestroy()
        log("onDestroy")

    }

    private fun log(text: String) {
        Log.d("TEST_TAG", "MyIntentService2: $text")
    }

    private fun countDown(page: Int) {
        for (i in 0 until 6) {
            log("timer is: $i, page = $page")
            Thread.sleep(1000)
        }
    }

    companion object {

        private const val SERVICE_NAME = "MyIntentService2"
        private const val PAGE = "page"

        fun newInstance(context: Context, page: Int): Intent {
            return Intent(context, MyIntentService2::class.java).apply {
                putExtra(PAGE, page)
            }
        }
    }

}