package com.aadharmachine.rssolution;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by SonuShaikh on 19/12/2017.
 */

public class Utility {


    public static String getCurrentDate(){
        Date curDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");   // 19/12/2017 12:41
        String DateToStr = format.format(curDate);
        return  DateToStr;
    }
}
