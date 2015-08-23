package com.blazingphoenix.iprepared;

/**
 * Created by Vaibhav on 11/12/13.
 */
public class ListItemClass {
    private String name = "";
    private String list = "";
    private String expiry = null;
    private int selected = 0;
    private int notificationID = 0;
    //private int id = 99;

    public ListItemClass() {

    }

    public ListItemClass(String list, String name, String expiry, int selected) {
        super();
        this.name = name;
        this.list = list;
        this.expiry = expiry;
        this.selected = selected;
    }

    public ListItemClass(String list, String name, int selected) {
        super();
        this.name = name;
        this.list = list;
        this.selected = selected;
    }

    public String getList() {
        return this.list;
    }

    public void setList(String list) {
        this.list = list;
    }

    /*public int getId() {
        return id;
    }

    public void setId(int newId) {
        id = newId;
    }*/

    public String getName() {
        return name;
    }

    public void setName(String code) {
        this.name = code;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public String getString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getName() + ",");
        sb.append(this.getExpiry() + ",");
        sb.append(String.valueOf(this.isChecked()) + ",");
        sb.append(this.getList());
        return sb.toString();
    }

    public void setNotificationID(int notificationID) {
        this.notificationID = notificationID;
    }

    public int getNotificationID() {
        return notificationID;
    }

    public int isChecked() {
        return selected;
    }

    public void setChecked(int selected) {
        this.selected = selected;
    }
}
