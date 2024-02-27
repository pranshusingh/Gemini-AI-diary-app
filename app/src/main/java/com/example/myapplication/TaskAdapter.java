package com.example.myapplication;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.util.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> mTasks;
    private OnTaskClickListener mListener;

    public TaskAdapter(OnTaskClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = mTasks.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return mTasks != null ? mTasks.size() : 0;
    }

    public void setTasks(List<Task> tasks) {
        mTasks = tasks;
        notifyDataSetChanged();
    }

    interface OnTaskClickListener {
        void onTaskLongClick(Task task);
        void onTaskClick(Task task);
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private TextView taskNameTextView;
        private TextView taskDateTextView;
        private TextView taskPriorityTextView;
        private CheckBox completedCheckBox;
        View view;
        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskNameTextView = itemView.findViewById(R.id.text_task_name);
            taskDateTextView = itemView.findViewById(R.id.text_task_date);
            taskPriorityTextView = itemView.findViewById(R.id.text_task_priority);
            completedCheckBox = itemView.findViewById(R.id.checkbox_completed);
            view=itemView.findViewById(R.id.view);
            //single click
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && mListener != null) {
                    mListener.onTaskClick(mTasks.get(position));
                }
            });
            //long click
            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && mListener != null) {
                    mListener.onTaskLongClick(mTasks.get(position));
                    return true;
                }
                return false;
            });

        }

        void bind(Task task) {
            taskNameTextView.setText(task.getName());
            String output = task.getDate().replace("/", " ➜ ");
            taskDateTextView.setText(output);
            switch (task.getPriority()) {
                case 1:
                    taskPriorityTextView.setText("Priority : HIGH");
                    view.setBackgroundColor(Color.parseColor("#C70039"));
                    break;
                case 2:
                    taskPriorityTextView.setText("Priority : MEDIUM");
                    view.setBackgroundColor(Color.parseColor("#FFA500"));
                    break;
                default:
                    taskPriorityTextView.setText("Priority : LOW");
                    view.setBackgroundColor(Color.parseColor("#C5DAFF"));
            }
            completedCheckBox.setChecked(task.isCompleted());
        }
    }
}
