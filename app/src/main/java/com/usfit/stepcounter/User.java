package com.usfit.stepcounter;

import android.content.Context;
import android.content.SharedPreferences;
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

    //Firebase
    private DatabaseReference db;

    public List<Friend> friendsList;

    public List<RequestInfo> friendRequests;

    public String mUsername, mEmail, mUID;

    public int mTop, mBot, mFoot, mTotalSteps;

    public User() {

    }

    public User(String nUsername, String nEmail, String nUID){
        //Variables
        mUsername = nUsername;
        mEmail = nEmail;
        mUID = nUID;
        mTotalSteps = 0;

        friendsList = new ArrayList<>();
        friendRequests = new ArrayList<>();

        //Firebase
        db = FirebaseDatabase.getInstance().getReference();
    }

    public void LoadOutfit(Context mContext){
        SharedPreferences outfitPrefs = mContext.getSharedPreferences("com.usfit.stepcounter.marketplace", Context.MODE_PRIVATE);
        mTop  = outfitPrefs.getInt("A_TOP", R.drawable.outfit_t00) - R.drawable.outfit_t00;
        mBot  = outfitPrefs.getInt("A_BOT", R.drawable.outfit_b00) - R.drawable.outfit_b00;
        mFoot = outfitPrefs.getInt("A_FOT", R.drawable.outfit_f00) - R.drawable.outfit_f00;
    }

    public void PublishUser(){
        if(db == null){
            db = FirebaseDatabase.getInstance().getReference();
        }
        Map<String, Object> publishMap = new HashMap<>();

        UserInfoPackage tempInfo = new UserInfoPackage(mUsername, mUID);
        tempInfo.AddCurrentOutfit();

        publishMap.put("users/" + mUID, this);
        publishMap.put("uids/" + mUsername, tempInfo);

        db.updateChildren(publishMap);
    }

    public void UpdateUser(){
        if(db == null){
            db = FirebaseDatabase.getInstance().getReference();
        }
        db.child("users").child(mUID).setValue(this);
    }

    public void FullUpdateUser(Context mContext){
        LoadOutfit(mContext);
        UpdateUser();
    }

    public void AddFriend(RequestInfo friend){
        if(friendsList == null)
         friendsList = new ArrayList<>();

        UserInfoPackage tempInfo = new UserInfoPackage(friend.userName, friend.userID);
        tempInfo.AddOutfit(friend);

        Friend tempFriend = new Friend(tempInfo);

        friendsList.add(tempFriend);
    }


    public void AddRequest(RequestInfo NewReq) {
        if(friendRequests == null)
            friendRequests = new ArrayList<>();
        friendRequests.add(NewReq);


    }
}
