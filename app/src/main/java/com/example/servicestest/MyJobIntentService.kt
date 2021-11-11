package com.example.servicestest

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class MyJobIntentService : JobIntentService() {

    override fun onCreate() {
        super.onCreate()
        log("onCreate")
    }

    override fun onHandleWork(intent: Intent) {
        val page = intent.getIntExtra(PAGE, 0)
        countDown(page)
    }

    override fun onDestroy() {
        super.onDestroy()
        log("onDestroy")

    }

    private fun log(text: String) {
        Log.d("TEST_TAG", "MyJobIntentService: $text")
    }

    private fun countDown(page: Int) {
        for (i in 0 until 6) {
            log("timer is: $i, page = $page")
            Thread.sleep(1000)
        }
    }

    companion object {

        private const val PAGE = "page"
        private const val JOB_ID = 111

        fun enqueue(context: Context, page: Int) {
            enqueueWork(
                context,
                MyJobIntentService::class.java,
                JOB_ID,
                newIntent(context, page)
            )
        }

        private fun newIntent(context: Context, page: Int): Intent {
            return Intent(context, MyJobIntentService::class.java).apply {
                putExtra(PAGE, page)
            }
        }
    }

}