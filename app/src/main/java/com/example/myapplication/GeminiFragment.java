package com.example.myapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.noties.markwon.Markwon;

public class GeminiFragment extends Fragment {


   TextView geminireply;
   EditText entryText;
   Button startconv;
   ImageView progressBar;
    GenerativeModel gm;
    GenerativeModelFutures model;
    Markwon markwon;

    public GeminiFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_gemini, container, false);

        markwon = Markwon.create(requireContext());
        geminireply=view.findViewById(R.id.geminireply);
        entryText=view.findViewById(R.id.entertext);
        startconv=view.findViewById(R.id.startConv);
        progressBar=view.findViewById(R.id.progress);


// For text-only input, use the gemini-pro model
        gm = new GenerativeModel( "gemini-pro", BuildConfig.ApiKey);
        model = GenerativeModelFutures.from(gm);

// Send the message
        startconv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);
                String usertext=entryText.getText().toString().trim();
                String entry = "answer in at most 200 words about "+usertext;
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
                        assert resultText != null;
                        markwon.setMarkdown(geminireply, resultText);
                    }
                });


                System.out.println(resultText);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();


            }
        }, executor);
    }

}