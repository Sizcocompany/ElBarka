package com.example.elbarka.Model;

public class Cart {

    // now will right all what we need to retive from database under cart list  as written as in data base

    private String pid , pname  , discount , price , quantity  ;

    public Cart() {
    }

    public Cart(String pid, String pname, String discount, String price, String quantity) {
        this.pid = pid;
        this.pname = pname;
        this.discount = discount;
        this.price = price;
        this.quantity = quantity;
    }


    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
