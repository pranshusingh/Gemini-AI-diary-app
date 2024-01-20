package com.example.myapplication;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


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
   Button startconvbt;
   ImageView progressBar;
    GenerativeModel gm;
    GenerativeModelFutures model;
    Markwon markwon;
    ImageButton bt_mic;
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


// For text-only input, use the gemini-pro model
        gm = new GenerativeModel( "gemini-pro", BuildConfig.ApiKey);
        model = GenerativeModelFutures.from(gm);


        bt_mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
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
            }
        });
// Send the message
        startconvbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startconvbt.setEnabled(false);

                progressBar.setVisibility(View.VISIBLE);
                String usertext=entryText.getText().toString().trim();
                String entry = "answer concisely about "+usertext;

                sendmessage(entry);

            }
        });

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
                String resultText = result.getText();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        startconvbt.setEnabled(true);
                        assert resultText != null;
                        markwon.setMarkdown(geminireply, resultText);
                    }
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