package com.example.nutritionapp

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import java.text.NumberFormat
import java.util.Locale
import com.example.nutritionapp.databinding.StatCardBinding

class StatCard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding: StatCardBinding = StatCardBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        orientation = VERTICAL
    }

    fun setCardData(value: Int, goal: Int, title: String, iconResId: Int) {
        with(binding) {
            // Set title
            textViewTitle.text = "You have walked ${title.lowercase()}"

            // Format numbers with commas
            val formatter = NumberFormat.getNumberInstance(Locale.US)

            // Set progress value
            textViewProgress.text = formatter.format(value)

            // Set progress bar
            progressBar.apply {
                max = goal
                progress = value
                progressDrawable = ContextCompat.getDrawable(context, R.drawable.horizontal_progress_bar)
            }

            // Set goal value
            textViewGoal.text = formatter.format(goal)
        }
    }
}