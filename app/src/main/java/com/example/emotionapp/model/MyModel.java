package com.example.emotionapp.model;

import android.util.Log;

import java.util.Map;

public class MyModel {
    private Map<Integer, Double> list;
    private static String smile;
    private static String rightEye;
    private static String leftEye;
    private static String headRotate;

    public Map<Integer, Double> getList() {
        return list;
    }

    public void setList(Map<Integer, Double> list) {
        this.list = list;
    }

    public static String getSmile() {
        return smile;
    }

    public static void setSmile(String smile) {
        MyModel.smile = smile;
    }

    public static String getRightEye() {
        return rightEye;
    }

    public static void setRightEye(String rightEye) {
        MyModel.rightEye = rightEye;
    }

    public static String getLeftEye() {
        return leftEye;
    }

    public static void setLeftEye(String leftEye) {
        MyModel.leftEye = leftEye;
    }

    public static String getHeadRotate() {
        return headRotate;
    }

    public static void setHeadRotate(String headRotate) {
        MyModel.headRotate = headRotate;
    }

    public static String getSideWays() {
        return sideWays;
    }

    public static void setSideWays(String sideWays) {
        MyModel.sideWays = sideWays;
    }

    private static String sideWays;

    public MyModel(Map<Integer, Double> list) {
        this.list = list;
        mymodel(list);
    }

    public void mymodel(Map list){
        for(Object db: list.keySet()){
            Log.w("TAGK",db.toString()+" "+db);
            String a = db.toString();
            try{
                switch(a){
                    case "1" -> setSmile(list.get(db).toString());
                    case "2" -> setRightEye(list.get(db).toString());
                    case "3" -> setLeftEye(list.get(db).toString());
                    case "4" -> setHeadRotate(list.get(db).toString());
                    case "5" -> setSideWays(list.get(db).toString());
                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }
    }

    public MyModel() {
    }

}

