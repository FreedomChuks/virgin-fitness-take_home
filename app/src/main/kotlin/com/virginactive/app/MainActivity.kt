package com.virginactive.app

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.virginactive.core.designsystem.ActiveTheme
import com.virginactive.core.model.FitnessClass
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { ActiveTheme { ActiveApp(addToCalendar = ::addToCalendar) } }
    }

    private fun addToCalendar(fitnessClass: FitnessClass) {
        val intent =
            Intent(Intent.ACTION_INSERT, CalendarContract.Events.CONTENT_URI).apply {
                putExtra(CalendarContract.Events.TITLE, fitnessClass.title)
                putExtra(
                    CalendarContract.Events.DESCRIPTION,
                    "Virgin Active class with ${fitnessClass.trainer}. Set your preferred reminder in your calendar.")
                putExtra(CalendarContract.Events.EVENT_TIMEZONE, fitnessClass.timezone)
                putExtra(
                    CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                    fitnessClass.startsAt.toInstant().toEpochMilli())
                putExtra(
                    CalendarContract.EXTRA_EVENT_END_TIME,
                    fitnessClass.endsAt.toInstant().toEpochMilli())
            }
        try {
            startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(
                    this,
                    "No calendar app is installed. Install one to add this class and set a reminder.",
                    Toast.LENGTH_LONG)
                .show()
        }
    }
}
