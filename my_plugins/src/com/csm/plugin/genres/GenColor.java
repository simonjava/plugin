package com.csm.plugin.genres;

public class GenColor {

    public static void main(String []args){
        for(int i=0;i<100;i++){
            int a = i*256/100;

            if(i%5==0) {
                String s = String.format("<color name=\"public_white_trans_%s\">#%sffffff</color>", i, Integer.toHexString(a));
                System.out.println(s);
            }
        }
    }
}
