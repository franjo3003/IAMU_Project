package hr.algebra.carmanager.worker

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import hr.algebra.carmanager.R
import hr.algebra.carmanager.data.prefs.PreferenceManager
import hr.algebra.carmanager.data.repository.CarRepository
import hr.algebra.carmanager.utils.NotificationUtils
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import android.app.PendingIntent
import android.content.Intent
import hr.algebra.carmanager.MainActivity

class RegistrationReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val prefs = PreferenceManager(applicationContext)

        if (!prefs.areNotificationsEnabled()) {
            return Result.success()
        }

        NotificationUtils.createNotificationChannel(applicationContext)

        val repository = CarRepository(applicationContext)
        val cars = repository.getAllCars()

        val today = LocalDate.now()

        val expiringCars = cars.filter { car ->
            try {
                val expiryDate = LocalDate.parse(car.registrationExpiryDate)
                val daysUntilExpiry = ChronoUnit.DAYS.between(today, expiryDate)
                daysUntilExpiry in 0..30
            } catch (e: Exception) {
                false
            }
        }

        if (expiringCars.isEmpty()) {
            return Result.success()
        }

        val carId = if (expiringCars.size == 1) expiringCars.first().id else -1L
        showNotification(carId, expiringCars.size)

        return Result.success()
    }

    private fun showNotification(carId: Long, count: Int) {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            if (carId != -1L) {
                putExtra("open_car_id", carId)
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            carId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(
            applicationContext,
            NotificationUtils.CHANNEL_ID
        )
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("CarManager podsjetnik")
            .setContentText(
                if (count == 1)
                    "Jednom automobilu registracija uskoro istječe."
                else
                    "$count automobila ima registraciju koja uskoro istječe."
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = applicationContext.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        manager.notify(1001, notification)
    }
}