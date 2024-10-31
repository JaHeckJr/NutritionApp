package com.example.nutritionapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nutritionapp.R

class LearnFragment : Fragment(R.layout.learn_fragment) {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // The layout is already set by Fragment(R.layout.learn_fragment)
        val view = super.onCreateView(inflater, container, savedInstanceState)

        // Sample placeholder data
        val sampleItems = listOf(
            CourseItem("Healthy Plate Principles", "Balanced Diet", "2h 15m", 12, 4.8, R.drawable.placeholder_image),
            CourseItem("Smart Snacking Strategies", "Healthy Snack", "1h 05m", 4, 4.3, R.drawable.placeholder_image),
            CourseItem("Diabetic-Friendly Food", "Blood Sugar Control", "1h 15m", 5, 4.6, R.drawable.placeholder_image),
            CourseItem("Balanced Meals for Diabetics", "Meal Planning", "1h 55m", 6, 4.7, R.drawable.placeholder_image)
        )

        // Recommended RecyclerView setup
        val recommendedRecyclerView = view?.findViewById<RecyclerView>(R.id.recyclerViewRecommended)
        recommendedRecyclerView?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recommendedRecyclerView?.adapter = CourseAdapter(sampleItems)

        // Diabetics RecyclerView setup
        val diabeticsRecyclerView = view?.findViewById<RecyclerView>(R.id.recyclerViewDiabetics)
        diabeticsRecyclerView?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        diabeticsRecyclerView?.adapter = CourseAdapter(sampleItems)

        // Explore RecyclerView setup
        val exploreRecyclerView = view?.findViewById<RecyclerView>(R.id.recyclerViewExplore)
        exploreRecyclerView?.layoutManager = LinearLayoutManager(requireContext())
        exploreRecyclerView?.adapter = CourseAdapter(sampleItems)

        return view
    }
}
