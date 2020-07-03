package com.example.wushengqing.ui.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.library.baseAdapters.BR;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wushengqing.R;
import com.example.wushengqing.data.entity.MenuBean;
import com.example.wushengqing.databinding.ItemHomeMenuBinding;

import java.util.ArrayList;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MyViewHolder> {
    private ArrayList<MenuBean> list;
    private Activity context;
    public MenuAdapter (Activity context,ArrayList<MenuBean> list){
        this.list=list;
        this.context=context;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_home_menu,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding.setVariable(BR.menu,list.get(position));
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ItemHomeMenuBinding binding;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding= DataBindingUtil.bind(itemView);
        }
    }
}
