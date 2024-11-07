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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!viewModel.isCalorieGoalSet()) {
            showCalorieGoalDialog()
        }
    }

    private fun setupPieCharts() {
        setupPieChart(binding.pieChartCalories, true)
        setupPieChart(binding.pieChartSteps, false)
        setupPieChart(binding.pieChartExercise, false)
    }

    private fun setupPieChart(chart: com.github.mikephil.charting.charts.PieChart, isMainChart: Boolean) {
        chart.apply {
            description.isEnabled = false
            setDrawEntryLabels(false)
            legend.isEnabled = false
            isRotationEnabled = false
            setTouchEnabled(false)
            setUsePercentValues(false)
            setDrawCenterText(true)
            setCenterTextSize(if (isMainChart) 18f else 14f)
            setHoleColor(Color.WHITE)
            holeRadius = 75f
            transparentCircleRadius = 80f
            setCenterTextColor(Color.BLACK)
            setMinimumHeight(if (isMainChart) 300 else 150)
        }
    }

    private fun setupButtons() {
        binding.updateStepsButton.setOnClickListener { showStepsDialog() }
        binding.updateExerciseButton.setOnClickListener { showExerciseDialog() }
    }

    private fun showCalorieGoalDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_update_progress, null)
        val goalInput = dialogView.findViewById<TextInputEditText>(R.id.goalInput)
        val description = dialogView.findViewById<TextView>(R.id.dialogDescription)

        description.text = "Enter your daily calorie goal"
        goalInput.hint = "Calorie Goal"

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Set Calorie Goal")
            .setView(dialogView)
            .setCancelable(false)
            .setPositiveButton("Set Goal", null) // Set to null initially
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val goalText = goalInput.text.toString()
                val goal = goalText.toIntOrNull()
                if (goal != null && goal > 0) {
                    viewModel.setTotalCalorieGoal(goal)
                    updateCaloriePieChart()
                    dialog.dismiss()
                } else {
                    goalInput.error = "Please enter a valid calorie goal"
                }
            }
        }

        dialog.show()
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
                    ?: viewModel.stepsGoal.value ?: 3444

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
                    ?: viewModel.exerciseGoal.value ?: 322

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
        viewModel.totalCalorieGoal.observe(viewLifecycleOwner) { updateCaloriePieChart() }

        // Observe steps data
        viewModel.stepsWalked.observe(viewLifecycleOwner) { updateStepsPieChart() }
        viewModel.stepsGoal.observe(viewLifecycleOwner) { updateStepsPieChart() }

        // Observe exercise data and update both exercise and calorie charts
        viewModel.exerciseCalories.observe(viewLifecycleOwner) {
            updateExercisePieChart()
            updateCaloriePieChart() // Update main chart when exercise changes
        }
        viewModel.exerciseGoal.observe(viewLifecycleOwner) { updateExercisePieChart() }
    }

    private fun updateCaloriePieChart() {
        val consumed = viewModel.caloriesConsumed.value?.toFloat() ?: 0f
        val goal = viewModel.totalCalorieGoal.value?.toFloat() ?: return
        val exercise = viewModel.exerciseCalories.value?.toFloat() ?: 0f
        // Calculate remaining by subtracting both consumed and exercise calories
        val remaining = (goal - consumed - exercise).coerceAtLeast(0f)

        // Update the calorie summary
        binding.calorieSummaryText.text = getString(
            R.string.calorie_summary,
            goal.toInt(),
            consumed.toInt(),
            exercise.toInt(),
            remaining.toInt()
        )

        val entries = if (consumed <= 0f && exercise <= 0f) {
            listOf(PieEntry(goal, getString(R.string.remaining)))
        } else {
            listOf(
                PieEntry(consumed + exercise, getString(R.string.progress)),
                PieEntry(remaining, getString(R.string.remaining))
            )
        }

        updatePieChartData(
            binding.pieChartCalories,
            entries,
            "${remaining.toInt()}\nRemaining",
            true
        )
    }

    private fun updateStepsPieChart() {
        val walked = viewModel.stepsWalked.value?.toFloat() ?: 0f
        val goal = viewModel.stepsGoal.value?.toFloat() ?: 3444f
        val remaining = (goal - walked).coerceAtLeast(0f)

        binding.stepsText.text = "Steps: ${walked.toInt()} / ${goal.toInt()}"

        val entries = if (walked <= 0f) {
            listOf(PieEntry(goal, getString(R.string.remaining)))
        } else {
            listOf(
                PieEntry(walked, getString(R.string.progress)),
                PieEntry(remaining, getString(R.string.remaining))
            )
        }

        updatePieChartData(
            binding.pieChartSteps,
            entries,
            "${walked.toInt()}\nSteps",
            false
        )
    }

    private fun updateExercisePieChart() {
        val burned = viewModel.exerciseCalories.value?.toFloat() ?: 0f
        val goal = viewModel.exerciseGoal.value?.toFloat() ?: 322f
        val remaining = (goal - burned).coerceAtLeast(0f)

        binding.exerciseText.text = "Calories: ${burned.toInt()} / ${goal.toInt()}"

        val entries = if (burned <= 0f) {
            listOf(PieEntry(goal, getString(R.string.remaining)))
        } else {
            listOf(
                PieEntry(burned, getString(R.string.progress)),
                PieEntry(remaining, getString(R.string.remaining))
            )
        }

        updatePieChartData(
            binding.pieChartExercise,
            entries,
            "${burned.toInt()}\nCal",
            false
        )
    }

    private fun updatePieChartData(
        chart: com.github.mikephil.charting.charts.PieChart,
        entries: List<PieEntry>,
        centerText: String,
        isMainChart: Boolean
    ) {
        val dataSet = PieDataSet(entries, "").apply {
            colors = if (entries.size == 1) {
                listOf(Color.parseColor("#E0E0E0")) // Empty chart color
            } else {
                listOf(
                    Color.parseColor("#4CAF50"), // Progress color
                    Color.parseColor("#E0E0E0")  // Remaining color
                )
            }
            valueTextSize = 0f
            setDrawValues(false)
        }

        chart.apply {
            data = PieData(dataSet)
            this.centerText = centerText
            setCenterTextSize(if (isMainChart) 18f else 14f)
            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}