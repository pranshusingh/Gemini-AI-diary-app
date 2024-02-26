package com.example.myapplication;

import android.app.Dialog;
import android.app.Notification;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.myapplication.tasksql.TaskDataSource;
import com.example.myapplication.util.Task;

public class TaskDialogFragment extends DialogFragment {
    private EditText taskNameEditText;
    private EditText taskDateEditText;
    private Spinner taskPrioritySpinner;
    private ArrayAdapter<CharSequence> priorityAdapter;
    private TaskDataSource mDataSource;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_task, null);
        mDataSource = new TaskDataSource(requireContext());
        builder.setView(view)
                .setTitle("Add Task")
                .setPositiveButton("Add", (dialog, which) -> {
                    // Add task
                    String taskName = taskNameEditText.getText().toString().trim();
                    String taskDate = taskDateEditText.getText().toString().trim();
                    int taskPriority = priority();
                    boolean isCompleted = false; // Assuming the task is initially not completed
                    // Call a method to save the task to the database
                    saveTask(taskName, taskDate, taskPriority, isCompleted);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // Cancel adding task
                    dialog.dismiss();
                });

        taskNameEditText = view.findViewById(R.id.edit_text_task_name);
        taskDateEditText = view.findViewById(R.id.edit_text_task_date);
        taskPrioritySpinner = view.findViewById(R.id.spinner_task_priority);
        priorityAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.priority_categories, android.R.layout.simple_spinner_item);
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        taskPrioritySpinner.setAdapter(priorityAdapter);
        return builder.create();
    }

    int priority(){
        String priorityStr = taskPrioritySpinner.getSelectedItem().toString();
        int priority;
        switch (priorityStr) {
            case "High":
                priority = 1;
                break;
            case "Medium":
                priority = 2;
                break;
            case "Low":
            default:
                priority = 3;
                break;
        }
        return priority;
    }
    private void saveTask(String name, String date, int priority, boolean isCompleted) {
        // Implement saving task to the database
        Task task = new Task(name, date, priority, isCompleted);
        mDataSource.open();
        mDataSource.addTask(task);
        mDataSource.close();
    }

}
