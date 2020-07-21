package com.example.ecommerce.Model;

public class Orders {

    private String CustomerAddress, CustomerCity, CustomerName,CustomerPhone, date, state, time ,totalPrice;

    public Orders() {
    }

    public Orders(String customerAddress, String customerCity, String customerName, String customerPhone, String date, String state, String time, String totalPrice) {
        CustomerAddress = customerAddress;
        CustomerCity = customerCity;
        CustomerName = customerName;
        CustomerPhone = customerPhone;
        this.date = date;
        this.state = state;
        this.time = time;
        this.totalPrice = totalPrice;
    }

    public String getCustomerAddress() {
        return CustomerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        CustomerAddress = customerAddress;
    }

    public String getCustomerCity() {
        return CustomerCity;
    }

    public void setCustomerCity(String customerCity) {
        CustomerCity = customerCity;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    public String getCustomerPhone() {
        return CustomerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        CustomerPhone = customerPhone;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }
}
