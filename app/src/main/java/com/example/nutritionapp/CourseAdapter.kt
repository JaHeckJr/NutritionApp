package com.example.nutritionapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nutritionapp.R

class CourseAdapter(private val items: List<CourseItem>) :
    RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    inner class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.courseTitle)
        val description: TextView = itemView.findViewById(R.id.courseDescription)
        val duration: TextView = itemView.findViewById(R.id.courseDuration)
        val lessons: TextView = itemView.findViewById(R.id.courseLessons)
        val rating: TextView = itemView.findViewById(R.id.courseRating)
        val image: ImageView = itemView.findViewById(R.id.courseImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.course_item, parent, false)
        return CourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.description.text = item.description
        holder.duration.text = item.duration
        holder.lessons.text = "${item.lessons} Lessons"
        holder.rating.text = item.rating.toString()
        holder.image.setImageResource(item.imageResId)
    }

    override fun getItemCount() = items.size
}
