package com.usfit.stepcounter;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class User {

    public List<UserInfoPackage> friendsList = new ArrayList<>();

    private static User currentUser;

    public UserInfoPackage myInfo;

    public String username, email, myKey;

    public int topWear, bottomWear, footWear;

    public User(){

    }

    public User(String username, String email){
        this.username = username;
        this.email = email;
        myKey = FirebaseAuth.getInstance().getCurrentUser().getUid();
        myInfo = new UserInfoPackage(myKey, username);
    }


    public void SetOutfit(int[] outfitArray){
        topWear = outfitArray[0];
        bottomWear = outfitArray[1];
        footWear = outfitArray[2];


    }

    public static void SetCurrentUser(User currentUser){
        User.currentUser = currentUser;
    }

    public static User GetCurrentUser(){
        return User.currentUser;
    }



    public void AddFriend(UserInfoPackage friend){
        friendsList.add(friend);
    }

    public void UpdateUserProfile(User newInfo){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = db.getReference();

        HashMap<String, Object> tempMap = new HashMap<>();
        tempMap.put(myKey, newInfo);

        dbRef.child("users").updateChildren(tempMap);

    }
}
