package com.example.wushengqing.ui.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.wushengqing.app.App;

public class HomeViewModelFactory implements ViewModelProvider.Factory {
    private int counterRestore;
    HomeViewModelFactory (int counterRestore){
        this.counterRestore=counterRestore;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new HomeViewModel(App.getContext(),counterRestore);
    }
}
