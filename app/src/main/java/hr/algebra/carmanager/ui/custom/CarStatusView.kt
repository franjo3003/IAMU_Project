package hr.algebra.carmanager.ui.custom

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import hr.algebra.carmanager.R
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class CarStatusView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    init {
        textSize = 16f
        setPadding(24, 16, 24, 16)
    }

    fun setRegistrationExpiryDate(expiryDateText: String) {
        try {
            val today = LocalDate.now()
            val expiryDate = LocalDate.parse(expiryDateText)
            val daysLeft = ChronoUnit.DAYS.between(today, expiryDate)

            when {
                daysLeft < 0 -> {
                    text = context.getString(R.string.registration_expired)
                    setBackgroundColor(Color.parseColor("#FFCDD2"))
                    setTextColor(Color.parseColor("#B71C1C"))
                }

                daysLeft <= 30 -> {
                    text = context.getString(R.string.registration_expiring_soon, daysLeft)
                    setBackgroundColor(Color.parseColor("#FFF9C4"))
                    setTextColor(Color.parseColor("#795548"))
                }

                else -> {
                    text = context.getString(R.string.registration_valid)
                    setBackgroundColor(Color.parseColor("#C8E6C9"))
                    setTextColor(Color.parseColor("#1B5E20"))
                }
            }
        } catch (e: Exception) {
            text = context.getString(R.string.registration_unknown)
            setBackgroundColor(Color.LTGRAY)
            setTextColor(Color.DKGRAY)
        }
    }
}