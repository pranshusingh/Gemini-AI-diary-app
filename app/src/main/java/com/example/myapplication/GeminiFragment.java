package com.example.myapplication;

import static android.app.Activity.RESULT_OK;

import static com.example.myapplication.MainActivity.resultText;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.myapplication.diarysql.DiaryDatabaseHelper;
import com.example.myapplication.util.Diary;
import com.example.myapplication.util.DiarySaveStatus;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.noties.markwon.Markwon;

public class GeminiFragment extends Fragment {

   TextView geminireply;
   EditText entryText;
   Button startconvbt, savebt;
   ImageView progressBar;
    GenerativeModel gm;
    GenerativeModelFutures model;
    Markwon markwon;
    ImageButton bt_mic;
    String resultt;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1;
    public GeminiFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_gemini, container, false);
        bt_mic = view.findViewById(R.id.speechtotext);
        markwon = Markwon.create(requireContext());
        geminireply=view.findViewById(R.id.geminireply);
        entryText=view.findViewById(R.id.entertext);
        startconvbt=view.findViewById(R.id.startConv);
        progressBar=view.findViewById(R.id.progress);
        savebt=view.findViewById(R.id.saveConv);

        // For text-only input, use the gemini-pro model
        gm = new GenerativeModel( "gemini-pro", BuildConfig.ApiKey);
        model = GenerativeModelFutures.from(gm);

        //use microphone
        bt_mic.setOnClickListener(v -> {
            Intent intent
                    = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                    Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");

            try {
                startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
            }
            catch (Exception e) {
                Toast
                        .makeText(getContext(), " " + e.getMessage(),
                                Toast.LENGTH_SHORT)
                        .show();
            }
        });
        // Send the message
        startconvbt.setOnClickListener(v -> {
            startconvbt.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            String usertext=entryText.getText().toString().trim();
            String entry = "answer concisely about "+usertext;

            sendmessage(entry);

        });

        //save content
        savebt.setOnClickListener(this::doOnSaveButtonClick);


        return view;
    }


// Initialize the chat
// Create a new user message
    private void sendmessage(String string) {

        Content userMessage = new Content.Builder()
                .addText(string)
                .build();
        Executor executor = Executors.newSingleThreadExecutor();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(userMessage);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                resultText = result.getText();
                resultt=resultText;
                new Handler(Looper.getMainLooper()).post(() -> {
                    progressBar.setVisibility(View.GONE);
                    startconvbt.setEnabled(true);
                    assert resultText != null;
                    markwon.setMarkdown(geminireply, resultText);
                });

                System.out.println(resultText);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        startconvbt.setEnabled(true);
                    }
                });
            }
        }, executor);
    }

    void doOnSaveButtonClick(View v){
        //Send the message (your logic here)
        Diary diary = new Diary(entryText.getText().toString().trim(),geminireply.getText().toString());
        put(diary);
        //Disable button
        savebt.setEnabled(false);

        //enable button after 1000 millisecond
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            savebt.setEnabled(true);
        }, 1000);
    }

    public DiarySaveStatus put(Diary diary) {

            SQLiteDatabase db =  new DiaryDatabaseHelper(getContext()).getReadableDatabase();
            String countQuery = "SELECT COUNT(*) FROM diary";
            Cursor cursor = db.rawQuery(countQuery, null);
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            cursor.close();
            if (count!=0) {
                ContentValues values = new ContentValues();
                values.put("title", diary.title);
                values.put("content", diary.content);
                values.put("updatedAt", diary.updatedAt);
                db.insert("diary", null, values);

            }return DiarySaveStatus.CREATED;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                entryText.setText(
                        Objects.requireNonNull(result).get(0));
            }
        }
    }

}