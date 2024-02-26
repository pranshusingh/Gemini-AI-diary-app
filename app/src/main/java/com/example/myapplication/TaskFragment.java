package com.example.myapplication;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.myapplication.tasksql.TaskDataSource;
import com.example.myapplication.util.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TaskFragment extends Fragment implements TaskAdapter.OnTaskClickListener{
    private RecyclerView mRecyclerView;
    private TaskAdapter mAdapter;
    private TaskDataSource mDataSource;

    public TaskFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_task, container, false);
        mRecyclerView = root.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new TaskAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        mDataSource = new TaskDataSource(getContext());
        mDataSource.open();

        loadTasks();

        FloatingActionButton fab = root.findViewById(R.id.fab_add);
        fab.setOnClickListener(view -> showAddTaskDialog());

        return root;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mDataSource.close();
    }

    public void loadTasks() {
        List<Task> tasks = mDataSource.getAllTasks();
        // Sort tasks based on priority
        Collections.sort(tasks, Comparator.comparingInt(Task::getPriority));
        mAdapter.setTasks(tasks);
    }

    private void showAddTaskDialog() {
        TaskDialogFragment dialogFragment = new TaskDialogFragment();
        dialogFragment.show(getActivity().getSupportFragmentManager(), "TaskDialogFragment");
    }

    @Override
    public void onTaskLongClick(Task task) {
        showDeleteConfirmationDialog(task);
    }
    private void showDeleteConfirmationDialog(Task task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Delete", (dialog, which) -> deleteTask(task))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }
    private void deleteTask(Task task) {
        mDataSource.deleteTask(task);
        loadTasks();
        showUndoSnackbar(task);
    }

    private void showUndoSnackbar(Task task) {
        Snackbar snackbar = Snackbar.make(requireView(), "Task deleted", Snackbar.LENGTH_LONG);
        snackbar.setAction("Undo", v -> undoDelete(task));
        snackbar.show();
    }

    private void undoDelete(Task task) {
        mDataSource.addTask(task);
        loadTasks();
    }
}

