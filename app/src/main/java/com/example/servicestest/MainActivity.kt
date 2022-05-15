package com.example.servicestest

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.app.job.JobWorkItem
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import androidx.core.content.ContextCompat
import com.example.servicestest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private var page = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.simpleService.setOnClickListener {
            stopService(MyForegroundService.newInstance(this))
            val intent = MyService.newInstance(this)
            startService(intent)
        }
        binding.foregroundService.setOnClickListener {
            ContextCompat.startForegroundService(
                this,
                MyForegroundService.newInstance(this)
            )
        }
        binding.intentService.setOnClickListener {
            startService(
                MyIntentService.newInstance(this)
            )
        }
        binding.jobScheduler.setOnClickListener {
            val name = ComponentName(this, MyJobService::class.java)

            val jobInfo = JobInfo.Builder(MyJobService.JOB_ID, name)
                // .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setRequiresCharging(true)
                .build()

            val jobScheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val intent = MyJobService.newIntent(page++)
                jobScheduler.enqueue(jobInfo, JobWorkItem(intent))
            } else {
                val intent = MyIntentService2.newInstance(this, page++)
                startService(intent)
            }
        }
        binding.jobIntentService.setOnClickListener {
            MyJobIntentService.enqueue(this, page++)
        }
        binding.workManager.setOnClickListener {
//            val workManager = WorkManager.getInstance(applicationContext)
//            workManager.enqueueUniqueWork(
//                "testWork",
//                ExistingWorkPolicy.APPEND,
//                MyWorkManager.oneTimeRequest(page)
//            )
            MyWorkManager.work(application, page++)
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val myLocalBinder = (service as? MyForegroundService.LocalBinder) ?: return
            val myService = myLocalBinder.getService()
            myService.onProgressChange = MyForegroundService.OnProgressChange { progress ->
                binding.progressBar.progress = progress
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
        }
    }

    override fun onStart() {
        super.onStart()
        bindService(MyForegroundService.newInstance(this), serviceConnection, 0)
    }

    override fun onStop() {
        super.onStop()
        unbindService(serviceConnection)
    }
}