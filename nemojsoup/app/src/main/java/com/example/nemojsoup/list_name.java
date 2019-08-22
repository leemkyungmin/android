package com.example.nemojsoup;

public class list_name {
    public String name;
    public String address;
    public String img;
    public String http;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getHttp() {
        return http;
    }

    public void setHttp(String http) {
        this.http = http;
    }

    public list_name(String name, String address, String img,String http)
    {
        this.name=name;
        this.address=address;
        this.img=img;
        this.http=http;


    }

}
