/*
 * Copyright 2018-2020 KunMinX
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.wushengqing.data.repository;

import androidx.lifecycle.MutableLiveData;

import com.example.wushengqing.R;
import com.example.wushengqing.bus.event.SingleLiveEvent;
import com.example.wushengqing.data.entity.MenuBean;
import com.example.wushengqing.data.entity.Result;
import com.example.wushengqing.data.entity.UserBean;
import com.example.wushengqing.data.net.ApiService;
import com.example.wushengqing.data.net.NetWorkManager;
import com.example.wushengqing.data.net.RetrofitClient;


import java.util.ArrayList;
import java.util.Map;

import io.reactivex.observers.DisposableObserver;

/**
 * Create by KunMinX at 19/10/29
 */
public class DataRepository implements ILocalRequest, IRemoteRequest {
    public ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);

    private static final DataRepository S_REQUEST_MANAGER = new DataRepository();
    private MutableLiveData<String> responseCodeLiveData;


    private DataRepository() {
    }

    public static DataRepository getInstance() {
        return S_REQUEST_MANAGER;
    }

    public MutableLiveData<String> getResponseCodeLiveData() {
        if (responseCodeLiveData == null) {
            responseCodeLiveData = new MutableLiveData<>();
        }
        return responseCodeLiveData;
    }


    /**
     * @param liveData
     */

    public void login(Map<String,Object> param,MutableLiveData<Result<UserBean>> liveData) {
        NetWorkManager.getInstance().showDialog();
        NetWorkManager.getInstance().execute(apiService.loginApi(param), new DisposableObserver<Result<UserBean>>() {
            @Override
            public void onNext(Result<UserBean> t) {
                NetWorkManager.getInstance().dismissDialog();
                liveData.postValue(t);
            }

            @Override
            public void onError(Throwable e) {
                NetWorkManager.getInstance().dismissDialog();
            }

            @Override
            public void onComplete() {
                NetWorkManager.getInstance().dismissDialog();
            }
        });


    }

    @Override
    public void getMenu(SingleLiveEvent<ArrayList<MenuBean>> mutableLiveData) {
        //获取菜单数据，本地提供
        ArrayList<MenuBean> list = new ArrayList<>();
        list.add(new MenuBean("收货", R.mipmap.img_collect_commodity_icon));
        list.add(new MenuBean("上架", R.mipmap.img_put_away));
        list.add(new MenuBean("移库", R.mipmap.img_task_icon));
        list.add(new MenuBean("盘点", R.mipmap.stock));
        list.add(new MenuBean("拣货", R.mipmap.img_picking_icon));
        list.add(new MenuBean("复核", R.mipmap.img_singlepicking_icon));
        list.add(new MenuBean("发货", R.mipmap.img_scan_icon));
        mutableLiveData.setValue(list);
    }


}
