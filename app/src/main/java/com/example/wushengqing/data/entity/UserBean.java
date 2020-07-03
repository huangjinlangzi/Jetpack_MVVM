package com.example.wushengqing.data.entity;

import java.io.Serializable;

public class UserBean  implements Serializable {

    /**
     * token : 2347a9a67d15f483
     * uid : 60777
     * username : rf.test
     * fullname : rf测试账号
     * manageOrgCode : null
     * manageOrgName : null
     */

    private String token;
    private String uid;
    private String username;
    private String fullname;



    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

}
