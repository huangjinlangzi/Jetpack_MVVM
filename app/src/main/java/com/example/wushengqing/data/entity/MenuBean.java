package com.example.wushengqing.data.entity;

public class MenuBean {
    private String menuName;
    private int resource;

    public MenuBean(String menuName,int resource){
        this.menuName=menuName;
        this.resource=resource;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }
}
