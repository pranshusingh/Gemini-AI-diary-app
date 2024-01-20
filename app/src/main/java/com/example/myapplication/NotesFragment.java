package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.diarysql.DiaryDataAccessor;
import com.example.myapplication.util.Diary;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 */
public class NotesFragment extends Fragment {

    private RecyclerView recyclerView;
    private MyRecyclerViewAdapter myRecyclerViewAdapter;
    private TextView noDiaryHintTextView;
    private List<Diary> diaries = new ArrayList<Diary>();



    private FloatingActionButton floatingActionButton;

    NotesFragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view= inflater.inflate(R.layout.fragment_write, container, false);

        noDiaryHintTextView=view.findViewById(R.id.nodiary);
        floatingActionButton=view.findViewById(R.id.floatingButton);
        // Floating button click listener
        floatingActionButton.setOnClickListener(v -> {
            // Redirect to other page
            Toast.makeText(getContext(), "Redirecting to other page", Toast.LENGTH_SHORT).show();
            Intent MoveToNext = new Intent (getContext(), WriteDiaryActivity.class);
            startActivity(MoveToNext);


        });

        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // ItemAnimator
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        initialize();


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy){
                if (dy<0 && !floatingActionButton.isShown())
                    floatingActionButton.show();
                else if(dy>0 && floatingActionButton.isShown())
                    floatingActionButton.hide();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        return view;
    }

    private void initialize() {
        loadData();
        refreshDiaryViews();
    }



    private void loadData() {

        diaries = new DiaryDataAccessor(getContext()).getAll();
        Collections.reverse(diaries);
    }

    private void startDiaryDetailActivityForNewDiary() {
        Intent intent = new Intent(getContext(), WriteDiaryActivity.class);
        startActivityForResult(intent, 1);
    }

    private void refreshDiaryViews() {
        //
        //Initialize a custom adapter
        myRecyclerViewAdapter = new MyRecyclerViewAdapter(getContext(), diaries);
        myRecyclerViewAdapter.setOnItemClickListener(new MyItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getContext(), WriteDiaryActivity.class);
                intent.putExtra("diaryId", diaries.get(position)._id);
                startActivityForResult(intent, 1);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                deleteDiary(view, position);
            }
        });

        // mRecyclerView
        recyclerView.setAdapter(myRecyclerViewAdapter);

        if (!diaries.isEmpty()) {
            noDiaryHintTextView.setVisibility(View.GONE);
        } else {
            noDiaryHintTextView.setVisibility(View.VISIBLE);
        }

    }

    private void deleteDiary(final View view, final int position) {
        new AlertDialog.Builder(getContext())
                .setMessage("Do you want to delete this note？")
                .setPositiveButton("delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final int deletingDiaryId = diaries.get(position)._id;
                        final Handler handler = new Handler();
                        final Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                new DiaryDataAccessor(getContext()).delete(deletingDiaryId);
//                                loadData();
//                                refreshDiaryViews();
                            }
                        };
                        handler.postDelayed(runnable, 3500);

                        Snackbar.make(view, "Note deleted", Snackbar.LENGTH_LONG)
                                .setAction("Revoke", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        handler.removeCallbacks(runnable);
                                        loadData();
                                        refreshDiaryViews();
                                    }
                                }).show();

                        diaries.remove(position);
                        refreshDiaryViews();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                }).setIcon(R.drawable.common_google_signin_btn_icon_dark)
                .create()
                .show();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            loadData();
            refreshDiaryViews();
        }
    }


}

interface MyItemClickListener {
    public void onItemClick(View view, int position);
    public void onItemLongClick(View view, int position);
}

class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyViewHolder>
{

    private List<Diary> diaries;
    private Context mContext;

    private MyItemClickListener mItemClickListener;

    public MyRecyclerViewAdapter( Context context , List<Diary> diaries)
    {
        this.mContext = context;
        this.diaries = diaries;
    }

    @Override
    public MyViewHolder onCreateViewHolder( ViewGroup viewGroup, int i )
    {
        // ViewHolder
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.diary_view, viewGroup, false);
        return new MyViewHolder(v, mItemClickListener);
    }

    @Override
    public void onBindViewHolder( MyViewHolder viewHolder, int i )
    {
        // ViewHolder
        Diary diary = diaries.get(i);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Log.d("time",dateFormat.format(diary.updatedAt));
        String date = dateFormat.format(diary.updatedAt);
        viewHolder.mMonthTextView.setText(date.substring(5, 7));
        viewHolder.mDayTextView.setText(date.substring(8, 10));
        viewHolder.mTitleTextView.setText(diary.title);
        viewHolder.mContentTextView.setText(diary.content);
        String Icon=date.substring(11, 13);
        if((Integer.parseInt(Icon)<=15)&&(Integer.parseInt(Icon)>=9)){
            //sun
            viewHolder.ImageIcon.setImageResource(R.drawable.day);
        }else if((Integer.parseInt(Icon)<=5)||(Integer.parseInt(Icon)>=20)){
            //moon
            viewHolder.ImageIcon.setImageResource(R.drawable.night);
            //c
        }else{
            viewHolder.ImageIcon.setImageResource(R.drawable.evening);
        }
    }

    @Override
    public int getItemCount()
    {
        return diaries == null ? 0 : diaries.size();
    }

    /**
     * Item
     * @param listener
     */
    public void setOnItemClickListener(MyItemClickListener listener){
        this.mItemClickListener = listener;
    }

}

class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    public TextView mMonthTextView;
    public TextView mDayTextView;
    public TextView mTitleTextView;
    public ImageView ImageIcon;
    public TextView mContentTextView;
    private MyItemClickListener mListener;

    public MyViewHolder(View rootView,MyItemClickListener listener) {
        super(rootView);
        mTitleTextView =  rootView.findViewById(R.id.title);
        mContentTextView =  rootView.findViewById(R.id.content);
        ImageIcon =  rootView.findViewById(R.id.imageView2);
        mMonthTextView =  rootView.findViewById(R.id.monthTextView);
        mDayTextView =  rootView.findViewById(R.id.dayTextView);
        this.mListener = listener;
        rootView.setOnClickListener(this);
        rootView.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(mListener != null){
            mListener.onItemClick(v, getPosition());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (mListener != null) {
            mListener.onItemLongClick(v, getPosition());
        }
        return true;
    }
}