package com.csm.plugin.gendimens;

/**
 * Created by chengsimin on 2018/10/25.
 */

public class GenDimens {

    public static void main(String []args){
        for(int i=0;i<100;i++){

            if(i%3!=0) {
                String a  = String.format("%.2f",i/3.0);
                a = a.replace(".","_");
                String str = String.format("<dimen name=\"textsize_%s_dp\">%.2fdp</dimen>",a , i / 3.0);
                System.out.println(str);
            }else{
                String str = String.format("<dimen name=\"textsize_%s_dp\">%sdp</dimen>", i / 3, i / 3);
                System.out.println(str);
            }

        }
    }
}
