package com.lorenzogao.simple;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;

    private List<String> mItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = findViewById(R.id.list_view);

        mItems = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            mItems.add("i--->" + i);
        }
        ArrayAdapter<String> array=new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,mItems);
        mListView.setAdapter(array);
    }
}
