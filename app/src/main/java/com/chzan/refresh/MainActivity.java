package com.chzan.refresh;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private List<Integer> lists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.list_view);
        lists = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            lists.add(i);
        }
        listView.setAdapter(new ListAdapter());
        listView.setDividerHeight(3);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(new Intent(MainActivity.this, RefreshActivity.class));
            }
        });
    }

    private class ListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return lists.size();
        }

        @Override
        public Object getItem(int i) {
            return lists.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list, viewGroup, false);
            TextView textView = (TextView) itemView.findViewById(R.id.text);
            textView.setText(lists.get(i) + "");
            return itemView;
        }
    }
}
