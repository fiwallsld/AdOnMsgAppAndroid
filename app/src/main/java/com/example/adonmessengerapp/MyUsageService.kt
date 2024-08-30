package com.example.adonmessengerapp

import android.app.Service
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.app.usage.UsageEvents
import android.content.Context
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.util.Log
import android.os.Handler
import android.os.IBinder
import android.os.Looper

class MyUsageService : Service() {
    private val handler = Handler(Looper.getMainLooper())
    private var isAdPlaying = false  // Biến trạng thái để theo dõi xem quảng cáo đang phát hay không

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startListeningForMessengerAppOpen(applicationContext)
        return START_STICKY
    }

    private fun startListeningForMessengerAppOpen(context: Context) {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (!isAdPlaying) {
                    checkMessengerAppOpen(context)
                }
                handler.postDelayed(this, 60000) // Kiểm tra mỗi 1 phút
            }
        }, 1000)
    }

    private fun checkMessengerAppOpen(context: Context) {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val currentTime = System.currentTimeMillis()
        val events = usageStatsManager.queryEvents(currentTime - 60000, currentTime) // Lấy sự kiện trong 10 phút gần nhất
        val event = UsageEvents.Event()

        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            if (event.packageName == "com.facebook.orca" && !isAdPlaying) {
                Log.d("MyUsageService", "Messenger được mở. Khởi động adonmessengerapp!")

                // Khởi động app adonmessengerapp để phát quảng cáo
                isAdPlaying = true
                val launchIntent = context.packageManager.getLaunchIntentForPackage("com.example.adonmessengerapp")
                launchIntent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                context.startActivity(launchIntent)

                // Bắt đầu lắng nghe xem video đã phát xong chưa
                monitorAdCompletion(context)
                break
            }
        }
    }

    private fun monitorAdCompletion(context: Context) {
        // Giả sử bạn có logic phát hiện khi nào quảng cáo kết thúc, ví dụ thông qua BroadcastReceiver hoặc callback từ video player
        handler.postDelayed({
            // Giả sử quảng cáo đã phát xong
            Log.d("MyUsageService", "Quảng cáo đã phát xong!")
            isAdPlaying = false

            // Đóng app sau khi quảng cáo xong
            closeApp(context, "com.example.adonmessengerapp")
        }, 15000) // Giả định quảng cáo kéo dài 30 giây
    }

    private fun closeApp(context: Context, packageName: String) {
        val closeIntent = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
        context.sendBroadcast(closeIntent)
        Log.d("MyUsageService", "Đóng app: $packageName")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}