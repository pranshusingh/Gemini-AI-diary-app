package com.example.myapplication;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.myapplication.tasksql.TaskDataSource;
import com.example.myapplication.util.Task;

public class UpdateTaskDialogFragment extends DialogFragment {
    private EditText taskNameEditText;
    private EditText taskDateEditText;
    private Spinner Priorityspinner;
    private ArrayAdapter<CharSequence> priorityAdapter;

    private CheckBox completedCheckBox;
    private Task taskToUpdate;
    private TaskDataSource mDataSource;
    private OnTaskDialogDismissedListener mListener;

    public UpdateTaskDialogFragment(Task task) {
        taskToUpdate = task;
    }
    // Define an interface for communication with the hosting activity or fragment
    public interface OnTaskDialogDismissedListener {
        void onTaskDialogDismissed();
    }
    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mListener != null) {
            mListener.onTaskDialogDismissed();
        }
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity(),R.style.CustomDialogFragmentTheme);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_update_task, null);
        mDataSource = new TaskDataSource(requireContext());

        builder.setView(view)
                .setTitle("Update Task")
                .setIcon(R.drawable.logo_white)
                .setPositiveButton(" Update", (dialog, which) -> {
                    // Update the task with new details
                    if (taskToUpdate != null) {
                        taskToUpdate.setName(taskNameEditText.getText().toString().trim());
                        taskToUpdate.setDate(taskDateEditText.getText().toString().trim());
                        taskToUpdate.setPriority(priority());
                        taskToUpdate.setCompleted(completedCheckBox.isChecked());
                        // Notify listener about task update
                       updateTask(taskToUpdate);
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        taskNameEditText = view.findViewById(R.id.uedit_text_task_name);
        taskDateEditText = view.findViewById(R.id.uedit_text_task_date);
        Priorityspinner = view.findViewById(R.id.uspinner_task_priority);
        priorityAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.priority_categories, android.R.layout.simple_spinner_item);
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Priorityspinner.setAdapter(priorityAdapter);
        completedCheckBox = view.findViewById(R.id.ucheckbox_completed);

        // Populate dialog fields with current task details
        if (taskToUpdate != null) {
            taskNameEditText.setText(taskToUpdate.getName());
            taskDateEditText.setText(taskToUpdate.getDate());
            Priorityspinner.setAdapter(priorityAdapter);
            completedCheckBox.setChecked(taskToUpdate.isCompleted());
            // Populate other task details as needed
        }
        return builder.create();
    }
    int priority(){
        String priorityStr = Priorityspinner.getSelectedItem().toString();
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
    private void updateTask(Task task) {
        // Implement saving task to the database
        mDataSource.open();
        mDataSource.updateTask(task);
        mDataSource.close();
    }
}


