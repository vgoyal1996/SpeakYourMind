package com.example.vipul.speakyourmind;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;


public class MessageKeyComparator implements Comparator<MessageKeyModel> {
    @Override
    public int compare(MessageKeyModel o1, MessageKeyModel o2) {
        String ct1 = o1.getCreationDateAndTime();
        String ct2 = o2.getCreationDateAndTime();
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
