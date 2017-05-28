package com.example.vipul.speakyourmind.comparators;


import com.example.vipul.speakyourmind.model.StatusModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class StatusComparator implements Comparator<StatusModel> {

    @Override
    public int compare(StatusModel t1, StatusModel t2) {
        String ct1 = t1.getCreationDateAndTime();
        String ct2 = t2.getCreationDateAndTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date d1 = null,d2 = null;
        try{
            d1 = sdf.parse(ct1);
            d2 = sdf.parse(ct2);
        }catch(ParseException e){
            e.printStackTrace();
        }
        if(d1.getTime()<d2.getTime())
            return 1;
        else if(d1.getTime()==d2.getTime())
            return 0;
        else
            return -1;
    }
}
