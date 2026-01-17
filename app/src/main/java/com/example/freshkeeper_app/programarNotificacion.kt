package com.example.freshkeeper_app

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("ScheduleExactAlarm")
fun programarNotificacion(productoNombre: String, fechaVencimiento: String, context: Context) {
    val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val fechaVencimientoDate = formato.parse(fechaVencimiento) ?: return

    val calendarVencimiento = Calendar.getInstance().apply {
        time = fechaVencimientoDate
        set(Calendar.HOUR_OF_DAY, 9)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }

    val calendarSemanaAntes = Calendar.getInstance().apply {
        time = fechaVencimientoDate
        add(Calendar.DAY_OF_MONTH, -7)
        set(Calendar.HOUR_OF_DAY, 9)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager


    val intentVencimiento = Intent(context, NotificacionReceiver::class.java).apply {
        putExtra("productoNombre", productoNombre)
        putExtra("mensaje", "¡Este producto vence hoy!")
    }
    val pendingIntentVencimiento = PendingIntent.getBroadcast(
        context, System.currentTimeMillis().toInt(), intentVencimiento,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendarVencimiento.timeInMillis, pendingIntentVencimiento)


    val intentSemanaAntes = Intent(context, NotificacionReceiver::class.java).apply {
        putExtra("productoNombre", productoNombre)
        putExtra("mensaje", "¡Este producto vence esta semana!")
    }
    val pendingIntentSemanaAntes = PendingIntent.getBroadcast(
        context, System.currentTimeMillis().toInt() + 1, intentSemanaAntes,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendarSemanaAntes.timeInMillis, pendingIntentSemanaAntes)
}
