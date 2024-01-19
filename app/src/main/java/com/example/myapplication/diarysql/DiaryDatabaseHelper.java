package com.example.myapplication.diarysql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DiaryDatabaseHelper extends SQLiteOpenHelper {

    public DiaryDatabaseHelper(Context context) {
        super(context, "diary.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                "CREATE TABLE diary(_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, content TEXT, createdAt INTEGER, updatedAt INTEGER)"
        );
        long time = System.currentTimeMillis();
        sqLiteDatabase.execSQL(
                "INSERT INTO diary(title, content, createdAt, updatedAt) values ('Welcome To Deer Dairy', 'It is a simple ,both offline as well as online featured dairy for no boundation on your mood of writing', ?, ?)",
                new Object[] { time-70L*1000*60*60*24, time-70L*1000*60*60*24 });
        sqLiteDatabase.execSQL(
                "INSERT INTO diary(title, content, createdAt, updatedAt) values ('Basics', 'The home page contains all the daily entries of the user in descending according to the date they were written\nDashboard page fives the view of daily life of user with images with details , like the storys\nNotification page gives the upcoming notifications/events /assissments ', ?, ?)",
                new Object[] { time-25L*1000*60*60*24, time-25L*1000*60*60*24 });
        sqLiteDatabase.execSQL(
                "INSERT INTO diary(title, content, createdAt, updatedAt) values ('Adding data', 'You can click on the write button on the bottom right corner of the screen', ?, ?)",
                new Object[] { time, time });
        sqLiteDatabase.execSQL(
                "INSERT INTO diary(title, content, createdAt, updatedAt) values ('Deleting Data', 'Long press on the data entry will prompt option to delete the data \nNote: deleted data is permanentally removed from your device', ?, ?)",
                new Object[] { time, time });
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {}
}
