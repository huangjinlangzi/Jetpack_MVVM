package com.example.wushengqing.data.entity;

import java.io.Serializable;

/**
 * @todo 统一数据基本模型
 */

public class Result<T> extends BaseBean  implements Serializable {

    public T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
