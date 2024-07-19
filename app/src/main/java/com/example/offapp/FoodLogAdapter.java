package com.example.offapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FoodLogAdapter extends RecyclerView.Adapter<FoodLogAdapter.FoodLogViewHolder> {
    private List<FoodLog> foodLogList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(FoodLog foodLog);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public static class FoodLogViewHolder extends RecyclerView.ViewHolder {
        public TextView mealTextView;
        public TextView foodNameTextView;
        public TextView caloriesTextView;

        public FoodLogViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            mealTextView = itemView.findViewById(R.id.mealTextView);
            foodNameTextView = itemView.findViewById(R.id.foodNameTextView);
            caloriesTextView = itemView.findViewById(R.id.caloriesTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick((FoodLog) v.getTag());
                    }
                }
            });
        }
    }

    public FoodLogAdapter(List<FoodLog> foodLogList) {
        this.foodLogList = foodLogList;
    }

    @NonNull
    @Override
    public FoodLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_log_item, parent, false);
        return new FoodLogViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodLogViewHolder holder, int position) {
        FoodLog currentItem = foodLogList.get(position);
        holder.mealTextView.setText(currentItem.getMeal());
        holder.foodNameTextView.setText(currentItem.getFoodName());
        holder.caloriesTextView.setText(String.valueOf(currentItem.getCalories()));
        holder.itemView.setTag(currentItem);
    }

    @Override
    public int getItemCount() {
        return foodLogList.size();
    }

    public void setFoodLogList(List<FoodLog> foodLogList) {
        this.foodLogList = foodLogList;
        notifyDataSetChanged();
    }
}
