package com.chzan.refresh;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chzan.refresh.pullrefresh.RefreshContainerLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenzan on 2016/11/14.
 */

public class RefreshActivity extends Activity {
    private ListView listView;
    private List<Integer> lists;
    private RefreshContainerLayout refreshContainerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refresh);
        refreshContainerLayout = (RefreshContainerLayout) findViewById(R.id.refresh_layout);
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
                Toast.makeText(view.getContext(), lists.get(i) + "", Toast.LENGTH_SHORT).show();
            }
        });
        refreshContainerLayout.setOnRefreshListener(new RefreshContainerLayout.OnRefreshListener() {
            @Override
            public void onPullDownRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RefreshActivity.this, "downRefresh", Toast.LENGTH_SHORT).show();
                            }
                        });
                        try {
                            Thread.sleep(1000L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refreshContainerLayout.setRefreshComplete();
                                Toast.makeText(RefreshActivity.this, "wanCheng", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).start();

            }

            @Override
            public void onPullUpRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RefreshActivity.this, "upRefresh", Toast.LENGTH_SHORT).show();
                            }
                        });
                        try {
                            Thread.sleep(1000L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refreshContainerLayout.setRefreshComplete();
                                Toast.makeText(RefreshActivity.this, "wanCheng", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).start();
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
