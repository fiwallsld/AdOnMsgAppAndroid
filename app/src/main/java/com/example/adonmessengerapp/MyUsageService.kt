package com.example.adonmessengerapp

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.app.usage.UsageEvents
import android.content.Context
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.util.Log
import android.os.Handler
import android.os.Looper

class MyUsageService : NotificationListenerService() {

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d("MyUsageService", "Đã kết nối Listener Service")
        startListeningForMessengerAppOpen(applicationContext)
    }

    private fun startListeningForMessengerAppOpen(context: Context) {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(object : Runnable {
            override fun run() {
                checkMessengerAppOpen(context)
                handler.postDelayed(this, 1000) // Thực thi mỗi giây
            }
        }, 1000) // Bắt đầu sau 1 giây
    }

    private fun checkMessengerAppOpen(context: Context) {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val currentTime = System.currentTimeMillis()
        val events = usageStatsManager.queryEvents(currentTime - 100000, currentTime)
        val usageEvent = UsageEvents.Event()

        while (events.hasNextEvent()) {
            events.getNextEvent(usageEvent)
            if (usageEvent.packageName == "com.facebook.orca") { // Tên gói của Messenger
                Log.d("MyUsageService", "Ứng dụng Messenger đã được mở!")
                
                // Khởi chạy ứng dụng của bạn
                val launchIntent = context.packageManager.getLaunchIntentForPackage("com.example.adonmessengerapp")
                launchIntent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntent)
            } else {
                Log.d("MyUsageService", "Ứng dụng khác đã được mở: ${usageEvent.packageName}")
            }
        }
    }
}
