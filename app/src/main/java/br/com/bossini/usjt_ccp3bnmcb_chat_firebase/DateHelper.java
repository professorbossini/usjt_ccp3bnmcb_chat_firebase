package br.com.bossini.usjt_ccp3bnmcb_chat_firebase;

import java.text.SimpleDateFormat;
import java.util.Date;

class DateHelper {

    public static String format (Date date){
        return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(date);
    }
}
