package com.example.wushengqing.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.blankj.utilcode.util.LogUtils;
import com.example.wushengqing.R;
import com.example.wushengqing.app.ConstantCode;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        final TextView textView = root.findViewById(R.id.text_notifications);
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        ConstantCode.mutableLiveData.observe(getViewLifecycleOwner(), new Observer() {
            @Override
            public void onChanged(Object o) {
                LogUtils.d("TEST","NotificationsFragment");

            }
        });

        ConstantCode.unPeekLiveData.observe(getViewLifecycleOwner(), new Observer() {
            @Override
            public void onChanged(Object o) {
                LogUtils.d("TEST","NotificationsFragment_peek");
            }
        });

        ConstantCode.singleLiveEvent.observe(getViewLifecycleOwner(), new Observer() {
            @Override
            public void onChanged(Object o) {
                LogUtils.d("TEST","NotificationsFragment_single");
            }
        });
        return root;
    }
}