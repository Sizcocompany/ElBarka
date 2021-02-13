package com.example.elbarka.Model;

public class Products {

     // make sure that you add same name in data base when added new parameter
     private String pName , description , price  , image , category , pid , time , date , productState  ;

     public Products() {
     }

     public Products(String pName, String description, String price, String image, String category, String pid, String time, String date, String productState) {
          this.pName = pName;
          this.description = description;
          this.price = price;
          this.image = image;
          this.category = category;
          this.pid = pid;
          this.time = time;
          this.date = date;
          this.productState = productState;
     }

     public String getpName() {
          return pName;
     }

     public void setpName(String pName) {
          this.pName = pName;
     }

     public String getDescription() {
          return description;
     }

     public void setDescription(String description) {
          this.description = description;
     }

     public String getPrice() {
          return price;
     }

     public void setPrice(String price) {
          this.price = price;
     }

     public String getImage() {
          return image;
     }

     public void setImage(String image) {
          this.image = image;
     }

     public String getCategory() {
          return category;
     }

     public void setCategory(String category) {
          this.category = category;
     }

     public String getPid() {
          return pid;
     }

     public void setPid(String pid) {
          this.pid = pid;
     }

     public String getTime() {
          return time;
     }

     public void setTime(String time) {
          this.time = time;
     }

     public String getDate() {
          return date;
     }

     public void setDate(String date) {
          this.date = date;
     }

     public String getProductState() {
          return productState;
     }

     public void setProductState(String productState) {
          this.productState = productState;
     }
}
