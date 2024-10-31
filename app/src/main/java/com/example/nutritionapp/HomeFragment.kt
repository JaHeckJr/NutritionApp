package com.example.nutritionapp.ui

import android.widget.TextView
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.nutritionapp.CalorieViewModel
import com.example.nutritionapp.R
import com.example.nutritionapp.databinding.FragmentHomeBinding
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.textfield.TextInputEditText

class HomeFragment : Fragment(R.layout.fragment_home) {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CalorieViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.bind(
            inflater.inflate(R.layout.fragment_home, container, false)
        )

        setupPieCharts()
        setupButtons()
        observeViewModel()

        return binding.root
    }

    private fun setupPieCharts() {
        // Setup main calorie chart
        setupPieChart(binding.pieChartCalories, 200f)

        // Setup steps chart
        setupPieChart(binding.pieChartSteps, 100f)

        // Setup exercise chart
        setupPieChart(binding.pieChartExercise, 100f)
    }

    private fun setupPieChart(chart: com.github.mikephil.charting.charts.PieChart, textSize: Float) {
        chart.apply {
            description.isEnabled = false
            setDrawEntryLabels(false)
            legend.isEnabled = false
            isRotationEnabled = false
            setTouchEnabled(false)
            setUsePercentValues(false)
            setDrawCenterText(true)
            setCenterTextSize(textSize * 0.08f)
            setHoleColor(Color.WHITE)
        }
    }

    private fun setupButtons() {
        binding.updateStepsButton.setOnClickListener { showStepsDialog() }
        binding.updateExerciseButton.setOnClickListener { showExerciseDialog() }
    }

    private fun showStepsDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_update_progress, null)
        val currentInput = dialogView.findViewById<TextInputEditText>(R.id.currentInput)
        val goalInput = dialogView.findViewById<TextInputEditText>(R.id.goalInput)
        val description = dialogView.findViewById<TextView>(R.id.dialogDescription)

        currentInput.hint = getString(R.string.steps_current_hint)
        goalInput.hint = getString(R.string.steps_goal_hint)
        description.text = getString(R.string.steps_dialog_description)

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.dialog_title_steps))
            .setView(dialogView)
            .setPositiveButton(R.string.dialog_update) { _, _ ->
                val current = currentInput.text.toString().toIntOrNull() ?: 0
                val goal = goalInput.text.toString().toIntOrNull()
                    ?: viewModel.stepsGoal.value ?: 10000

                viewModel.setStepsWalked(current)
                viewModel.setStepsGoal(goal)
            }
            .setNegativeButton(R.string.dialog_cancel, null)
            .show()
    }

    private fun showExerciseDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_update_progress, null)
        val currentInput = dialogView.findViewById<TextInputEditText>(R.id.currentInput)
        val goalInput = dialogView.findViewById<TextInputEditText>(R.id.goalInput)
        val description = dialogView.findViewById<TextView>(R.id.dialogDescription)

        currentInput.hint = getString(R.string.exercise_current_hint)
        goalInput.hint = getString(R.string.exercise_goal_hint)
        description.text = getString(R.string.exercise_dialog_description)

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.dialog_title_exercise))
            .setView(dialogView)
            .setPositiveButton(R.string.dialog_update) { _, _ ->
                val current = currentInput.text.toString().toIntOrNull() ?: 0
                val goal = goalInput.text.toString().toIntOrNull()
                    ?: viewModel.exerciseGoal.value ?: 500

                viewModel.setExerciseCalories(current)
                viewModel.setExerciseGoal(goal)
            }
            .setNegativeButton(R.string.dialog_cancel, null)
            .show()
    }

    private fun observeViewModel() {
        // Observe calorie data
        viewModel.caloriesConsumed.observe(viewLifecycleOwner) { updateCaloriePieChart() }
        viewModel.remainingCalories.observe(viewLifecycleOwner) { updateCaloriePieChart() }

        // Observe steps data
        viewModel.stepsWalked.observe(viewLifecycleOwner) { updateStepsPieChart() }
        viewModel.stepsGoal.observe(viewLifecycleOwner) { updateStepsPieChart() }

        // Observe exercise data
        viewModel.exerciseCalories.observe(viewLifecycleOwner) { updateExercisePieChart() }
        viewModel.exerciseGoal.observe(viewLifecycleOwner) { updateExercisePieChart() }
    }

    private fun updateCaloriePieChart() {
        val consumed = viewModel.caloriesConsumed.value?.toFloat() ?: 0f
        val remaining = viewModel.remainingCalories.value?.toFloat() ?: 0f
        updatePieChartData(
            binding.pieChartCalories,
            consumed,
            remaining,
            getString(R.string.remaining_format, remaining.toInt())
        )
    }

    private fun updateStepsPieChart() {
        val walked = viewModel.stepsWalked.value?.toFloat() ?: 0f
        val remaining = viewModel.getRemainingSteps().toFloat()
        binding.stepsText.text = getString(R.string.steps_format, walked.toInt(), viewModel.stepsGoal.value)
        updatePieChartData(
            binding.pieChartSteps,
            walked,
            remaining,
            getString(R.string.remaining_format, walked.toInt())
        )
    }

    private fun updateExercisePieChart() {
        val burned = viewModel.exerciseCalories.value?.toFloat() ?: 0f
        val remaining = viewModel.getRemainingExerciseCalories().toFloat()
        binding.exerciseText.text = getString(R.string.exercise_format, burned.toInt(), viewModel.exerciseGoal.value)
        updatePieChartData(
            binding.pieChartExercise,
            burned,
            remaining,
            getString(R.string.remaining_format, burned.toInt())
        )
    }

    private fun updatePieChartData(
        chart: com.github.mikephil.charting.charts.PieChart,
        progress: Float,
        remaining: Float,
        centerText: String
    ) {
        val entries = listOf(
            PieEntry(progress, getString(R.string.progress)),
            PieEntry(remaining, getString(R.string.remaining))
        )

        val dataSet = PieDataSet(entries, "").apply {
            colors = listOf(
                Color.parseColor("#4CAF50"),
                Color.parseColor("#E0E0E0")
            )
            valueTextSize = 0f
            setDrawValues(false)
        }

        chart.apply {
            data = PieData(dataSet)
            this.centerText = centerText
            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}