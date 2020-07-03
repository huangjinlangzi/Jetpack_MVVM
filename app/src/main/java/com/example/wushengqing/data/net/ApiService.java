package com.example.wushengqing.data.net;


import com.example.wushengqing.data.entity.Result;
import com.example.wushengqing.data.entity.UserBean;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {
    @POST("user/login")
    @FormUrlEncoded
    Observable<Result<UserBean>> loginApi(@FieldMap Map<String,Object> map);
}
