package hr.algebra.carmanager.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class CarActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val message = intent.getStringExtra(EXTRA_MESSAGE) ?: "Promjena nad automobilom"

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val ACTION_CAR_CHANGED = "hr.algebra.carmanager.ACTION_CAR_CHANGED"
        const val EXTRA_MESSAGE = "extra_message"
    }
}