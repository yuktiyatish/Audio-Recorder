package com.scribie.recorder.audiorecorder;

import java.util.ArrayList;

/**
 * Created by yukti on 11/5/16.
 */
public class Utility {

    static private Utility instance = new Utility();

    public static Utility getInstance(){
        return instance;
    }
    public ArrayList getArrListHistory() {
        return arrListHistory;
    }

    public void setArrListHistory(ArrayList arrListHistory) {
        this.arrListHistory = arrListHistory;
    }

    private ArrayList<String> arrListHistory = new ArrayList<String>();

}
