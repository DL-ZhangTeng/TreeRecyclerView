package com.zhangteng.treerecyclerview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhangteng.treerecyclerview.adapter.MyAdapter;
import com.zhangteng.treerecyclerview.adapter.TreeRecyclerViewAdapter;
import com.zhangteng.treerecyclerview.model.CityNo;
import com.zhangteng.treerecyclerview.tree.Node;
import com.zhangteng.treerecyclerview.utils.AssetsUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    private MyAdapter adapter;
    private List<CityNo> mData = new ArrayList<>();
    private TreeRecyclerViewAdapter.OnTreeNodeClickListener mOnTreeNodeClickListener = new TreeRecyclerViewAdapter.OnTreeNodeClickListener() {
        @Override
        public void onClick(Node node, int position) {
            if (node.isLeaf()) {
                Toast.makeText(MainActivity.this, node.getName(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.mRecyclerView);
        initView();
    }

    private void initView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        layoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.getItemAnimator().setChangeDuration(300);
        mRecyclerView.getItemAnimator().setMoveDuration(300);
        addDatas();
        try {
            adapter = new MyAdapter(mRecyclerView, this, mData, mData.size());
            mRecyclerView.setAdapter(adapter);
            adapter.setOnTreeNodeClickListener(mOnTreeNodeClickListener);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void addDatas() {
        String json = AssetsUtils.getJson("cityno.json", this);
        Type listType = new TypeToken<List<CityNo>>() {
        }.getType();
        List<CityNo> list = new Gson().fromJson(json, listType);
        forTreeList(list);
    }

    private void forTreeList(List<CityNo> list) {
        mData.addAll(list);
        for (CityNo cityNo : list) {
            if (cityNo.getRegionEntitys() != null) {
                forTreeList(cityNo.getRegionEntitys());
            }
        }
    }
}
