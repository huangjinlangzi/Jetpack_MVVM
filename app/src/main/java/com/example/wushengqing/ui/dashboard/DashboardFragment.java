package com.example.wushengqing.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;

import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.library.baseAdapters.BR;

import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;


import com.example.wushengqing.R;

import com.example.wushengqing.base.BaseFragment;
import com.example.wushengqing.data.entity.MenuBean;
import com.example.wushengqing.databinding.FragmentDashboardBinding;
import com.example.wushengqing.ui.adapter.MenuAdapter;

import java.util.ArrayList;

public class DashboardFragment extends BaseFragment<FragmentDashboardBinding,DashboardViewModel> {
    private MenuAdapter adapter;
    private ArrayList<MenuBean> arrayList;

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_dashboard;
    }

    @Override
    public int initVariableId() {
        return BR.vm;
    }

    @Override
    protected void initData() {
        arrayList = new ArrayList<>();
        adapter = new MenuAdapter(getActivity(),arrayList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),3);
        binding.recycleView.setLayoutManager(gridLayoutManager);
        binding.recycleView.setAdapter(adapter);
        viewModel.requestMenuData();
        binding.include.tvTitle.setText("标准业务");
    }

    @Override
    protected void initViewObservable() {
        viewModel.getMenuData().observe(getViewLifecycleOwner(), new Observer<ArrayList<MenuBean>>() {
            @Override
            public void onChanged(ArrayList<MenuBean> list) {
                arrayList.clear();
                arrayList.addAll(list);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void initParam() {

    }
}