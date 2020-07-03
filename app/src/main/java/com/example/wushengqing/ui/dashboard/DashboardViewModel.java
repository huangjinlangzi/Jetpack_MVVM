package com.example.wushengqing.ui.dashboard;

import android.app.Application;

import androidx.annotation.NonNull;

import com.example.wushengqing.base.BaseViewModel;
import com.example.wushengqing.bus.event.SingleLiveEvent;
import com.example.wushengqing.data.entity.MenuBean;
import com.example.wushengqing.data.repository.DataRepository;

import java.util.ArrayList;

public class DashboardViewModel extends BaseViewModel {

    private SingleLiveEvent<ArrayList<MenuBean>> listMutableLiveData;


    public DashboardViewModel(@NonNull Application application) {
        super(application);
        listMutableLiveData = new SingleLiveEvent<>();
    }

    public SingleLiveEvent<ArrayList<MenuBean>> getMenuData(){
        return listMutableLiveData;
    }

    public void requestMenuData(){
        DataRepository.getInstance().getMenu(listMutableLiveData);
    }

}