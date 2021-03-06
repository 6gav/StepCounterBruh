package com.usfit.stepcounter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.widget.ImageView;
import android.widget.Toast;

public class GoalChecker{

    SharedPreferences GoalTracker,StepTracker,MoneyTracker;
    String[] Goals,Goal;
    String CurrentGoals;
    Activity activity;
    int StepsAtGoalCreate, CaloriesAtGoalCreate,GoalAtCreate,existingGoals;

    public GoalChecker(Activity _activity){
        activity = _activity;
        GoalTracker = _activity.getSharedPreferences("com.usfit.stepcounter.GoalTracker", Context.MODE_PRIVATE);
        StepTracker = _activity.getSharedPreferences("com.usfit.stepcounter.stepdata", Context.MODE_PRIVATE);
        MoneyTracker = _activity.getSharedPreferences("com.usfit.stepcounter.marketplace", Context.MODE_PRIVATE);

        CurrentGoals = GoalTracker.getString("CurrentGoals", "");
        if(!CurrentGoals.equals(""))
            Goals = CurrentGoals.split(";");
        else{
            Goals = new String[]{""};
        }
        existingGoals = GoalTracker.getInt("GoalCount",0);

    }

    int LoadType(String T){
        CaloriesAtGoalCreate = Math.round(StepTracker.getFloat("CalorieCount", 0));
        int goalAtCreate = StepsAtGoalCreate = StepTracker.getInt("StepCount", 0);

        switch (T){
            case "Steps":
                break;
            case "Calories":
                goalAtCreate = CaloriesAtGoalCreate;
                break;

        }
        return goalAtCreate;
    }

    public void Check(Context mContext){
        if(!CurrentGoals.equals("")) {

            String[] goalsCompleted = { "","","",};
            for(int i = 0;i < Goals.length;i++){
                Goals[i] = testGoal(Goals[i].split(","),CurrentGoals,mContext);
            }

        }
    }
    String testGoal(String[] _Goal,String temp,Context mContext){
        if(_Goal[0] == "" && _Goal.length == 1)return _Goal[0];
        Goal = _Goal;int StepGoal = Integer.valueOf(Goal[1]);
        int OrigSteps = Integer.valueOf(Goal[2]);
        //GoalType: 0 Steps, 1 Calories
        GoalAtCreate = LoadType(Goal[0]);
        int Difference = GoalAtCreate - OrigSteps;

        if (Difference >= StepGoal) {//goal was completed

            String text = "You have completed your goal of "+StepGoal+" "+Goal[0]+", and have been rewarded "+CompleteGoal(Goal[0],StepGoal)+" Cash";
            ///*
            Toast.makeText(activity.getApplicationContext(),text, Toast.LENGTH_SHORT).show();
            ImageView iv = new ImageView(mContext);
            iv.setImageDrawable(mContext.getResources().getDrawable(R.drawable.stickavatar));




            /*iv.setX(50);
            iv.setY(50);(iv,50,50,app_bar_main_menu_coordinator_layout,1000);

            int index = app_bar_main_menu_coordinator_layout.getChildCount();
            app_bar_main_menu_coordinator_layout.addView(iv,index);
            */
            return "";
        }
        return Goal[0] + "," + StepGoal + "," + OrigSteps ;
    }
    public int CompleteGoal(String type, int stepsTaken){
        int moneyEarned = (int)Math.pow(stepsTaken,1.3f),
                OrigMoney = MoneyTracker.getInt("MonValue",0);
        switch (type){
            case "Calories":
                moneyEarned*=2;
                break;
        }
        MoneyTracker.edit().putInt("MonValue",moneyEarned+OrigMoney);
        existingGoals--;
        if(existingGoals<0)existingGoals=0;
        GoalTracker.edit().putInt("GoalCount",existingGoals);
        return  moneyEarned;
    }

}
