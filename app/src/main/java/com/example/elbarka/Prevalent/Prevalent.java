package com.example.elbarka.Prevalent;


import com.example.elbarka.Model.Users;

// to get ( forget password and remember me feature in this app )
public class Prevalent {

    // to access users use to login and retrive only once data of user in loginactivity without retrive it again and again
    public static Users currentOnlineUsers;

    // store customer number and password in phone memory  to be used in rememeber me checkbox

    public static final String UserPhoneKey = " UserPhone";
    public static final String UserPasswordKey = " UserPassword";


}
