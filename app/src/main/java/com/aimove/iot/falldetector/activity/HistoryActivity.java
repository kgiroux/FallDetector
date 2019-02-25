package com.aimove.iot.falldetector.activity;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.aimove.iot.falldetector.R;
import com.aimove.iot.falldetector.utils.HistoryItemAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class HistoryActivity extends AppCompatActivity {

    /**
     * View for adding the history
     */
    private RecyclerView recyclerView;

    /**
     * HistoryItemAdapter that will contains the data
     */
    private HistoryItemAdapter historyItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("History of Fall");
        setContentView(R.layout.activity_history);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        historyItemAdapter = new HistoryItemAdapter();
        readHistoryFile();
        recyclerView = findViewById(R.id.history_recycler_view);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLayoutManager.setReverseLayout(false);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(false);
        recyclerView.setNestedScrollingEnabled(false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                mLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        recyclerView.setAdapter(historyItemAdapter);


    }

    /**
     * read the history file
     */
    private void readHistoryFile(){
        FileInputStream is;
        BufferedReader reader;
        String filename = "historyOfFall.txt";
        String path = Objects.requireNonNull(this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)).getPath() + "/record_data/";
        File fileHistory = new File(path+filename);
        if (fileHistory.exists()) {
            try{
                is = new FileInputStream(fileHistory);
                reader = new BufferedReader(new InputStreamReader(is));
                String line = reader.readLine();
                while(line != null){
                    historyItemAdapter.addFall(line);
                    line = reader.readLine();
                }
                reader.close();
                is.close();
            }catch (IOException e){
                Log.e("Read", "Failed to read the file of history");
            }

        }
    }

}
