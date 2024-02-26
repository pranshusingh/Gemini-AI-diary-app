package com.example.myapplication.tasksql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myapplication.util.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskDataSource {
    private SQLiteDatabase database;
    private TaskDbHelper dbHelper;

    public TaskDataSource(Context context) {
        dbHelper = new TaskDbHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void addTask(Task task) {
        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.COLUMN_NAME, task.getName());
        values.put(TaskContract.TaskEntry.COLUMN_DATE, task.getDate());
        values.put(TaskContract.TaskEntry.COLUMN_PRIORITY, task.getPriority());
        values.put(TaskContract.TaskEntry.COLUMN_COMPLETED, task.isCompleted() ? 1 : 0);

        database.insert(TaskContract.TaskEntry.TABLE_NAME, null, values);
    }

    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();

        String[] projection = {
                TaskContract.TaskEntry._ID,
                TaskContract.TaskEntry.COLUMN_NAME,
                TaskContract.TaskEntry.COLUMN_DATE,
                TaskContract.TaskEntry.COLUMN_PRIORITY,
                TaskContract.TaskEntry.COLUMN_COMPLETED
        };

        Cursor cursor = database.query(
                TaskContract.TaskEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        int idColumnIndex = cursor.getColumnIndex(TaskContract.TaskEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_NAME);
        int dateColumnIndex = cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_DATE);
        int priorityColumnIndex = cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_PRIORITY);
        int completedColumnIndex = cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_COMPLETED);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(idColumnIndex);
            String name = cursor.getString(nameColumnIndex);
            String date = cursor.getString(dateColumnIndex);
            int priority = cursor.getInt(priorityColumnIndex);
            boolean completed = cursor.getInt(completedColumnIndex) == 1;

            Task task = new Task(id, name, date, priority, completed);
            tasks.add(task);
        }

        cursor.close();
        return tasks;


   }

    public void updateTask(Task task) {
        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.COLUMN_NAME, task.getName());
        values.put(TaskContract.TaskEntry.COLUMN_DATE, task.getDate());
        values.put(TaskContract.TaskEntry.COLUMN_PRIORITY, task.getPriority());
        values.put(TaskContract.TaskEntry.COLUMN_COMPLETED, task.isCompleted() ? 1 : 0);

        String selection = TaskContract.TaskEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(task.getId())};

        database.update(TaskContract.TaskEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    public void deleteTask(Task task) {
        String selection = TaskContract.TaskEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(task.getId())};

        database.delete(TaskContract.TaskEntry.TABLE_NAME, selection, selectionArgs);
    }
}
