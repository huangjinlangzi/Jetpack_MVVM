package com.example.wushengqing.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.wushengqing.R;
import com.example.wushengqing.app.ConstantCode;
import com.example.wushengqing.base.BaseFragment;
import com.example.wushengqing.databinding.FragmentHomeBinding;

public class HomeFragment extends BaseFragment<FragmentHomeBinding,HomeViewModel> {
    private SharedPreferences sharedPreferences;
    private HomeViewModel homeViewModel;
    private ViewModelProvider viewModelProvider;


    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_home;
    }

    @Override
    public HomeViewModel initViewModel() {
        sharedPreferences= getActivity().getPreferences(Context.MODE_PRIVATE);
        int counterRestore = sharedPreferences.getInt("counterRestore",0);
        viewModelProvider = new ViewModelProvider(this,new HomeViewModelFactory(counterRestore));

        homeViewModel = viewModelProvider.get(HomeViewModel.class);

        return homeViewModel;
    }

    @Override
    public int initVariableId() {
        return BR.vm;
    }

    @Override
    protected void initData() {

        binding.btPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeViewModel.plusOne();
            }
        });

        binding.btClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeViewModel.clear();
            }
        });

        binding.btError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConstantCode.singleLiveEvent.setValue(null);
                ConstantCode.mutableLiveData.setValue(null);
                ConstantCode.unPeekLiveData.setValue(null);
            }
        });

    }

    @Override
    protected void initViewObservable() {
        homeViewModel.getCounter().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                binding.textHome.setText(integer.toString());
            }
        });
    }

    @Override
    protected void initParam() {

    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("counterRestore",homeViewModel.getCounter().getValue());
        editor.commit();
    }
}