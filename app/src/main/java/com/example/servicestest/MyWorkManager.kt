package com.example.servicestest

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.work.*
import java.util.concurrent.TimeUnit

class MyWorkManager(context: Context, private val workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result {
        log("doWork")
        val page = workerParams.inputData.getInt(PAGE, 0)
        countDown(page)
        Result.retry()
        Result.failure()
        return Result.success()
    }

    private fun countDown(page: Int) {
        for (i in 0 until 6) {
            log("timer is: $i, page = $page")
            Thread.sleep(1000)
        }
    }

    private fun log(text: String) {
        Log.d("TEST_TAG", "MyWorkManager: $text")
    }

    companion object {
        private const val PAGE = "page"
        private const val WORK_NAME = "testWork"

        fun work(application: Application, page: Int) {
            val workManager = WorkManager.getInstance(application)
            workManager.enqueueUniqueWork(
                WORK_NAME,
                ExistingWorkPolicy.APPEND,
                oneTimeRequest(page)
            )
        }

        fun oneTimeRequest(page: Int): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<MyWorkManager>().setInputData(
                workDataOf(PAGE to page)
            )
                .setConstraints(
                    Constraints.Builder()
                        .setRequiresCharging(true)
                        .build()
                )
                .setInitialDelay(2, TimeUnit.SECONDS)
                .build()
        }
    }
}