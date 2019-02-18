package com.csm.plugin.gendrawablexml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GenDrawableXml {

    static void processQietuDir(File file) {
        System.out.println("处理"+file.getPath());
        for(File f1:file.listFiles()){
            String name1 = f1.getName().split("\\.")[0];
            if(f1.isFile() && name1.endsWith("_normal")){
                //找到normal
                for(File f2:file.listFiles()){
                    if(f2.isFile()){
                         String name2  = f2.getName().split("\\.")[0];
                         if(name2.endsWith("_pressed")){
                             if(getPre(name1, "_normal").equals(getPre(name2, "_pressed"))){
                                 System.out.println("命中"+name1+"---"+name2);
                                 genDrawableXmlByName(file,name1,name2);
                             }
                         }
                    }
                }
            }
        }
    }

    private static void genDrawableXmlByName(File file,String name1, String name2) {
        File ff = new File(file,"gen");
        if(!ff.exists()){
            ff.mkdirs();
        }
        File wf = new File(ff,getPre(name1, "_normal")+".xml");

        StringBuilder sb = new StringBuilder();

        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        sb.append("<selector xmlns:android=\"http://schemas.android.com/apk/res/android\">\n");
        sb.append(String.format("   <item android:drawable=\"@drawable/%s\" android:state_pressed=\"true\" />\n",name2));
        sb.append(String.format("   <item android:drawable=\"@drawable/%s\" />\n",name1));
        sb.append("</selector>\n");
        FileWriter fos = null;
        try {
            fos = new FileWriter(wf);
            fos.write(sb.toString());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static String getPre(String str,String pre){
        return str.substring(0, str.length() - pre.length());
    }

    static void findQietu(File file) {
        for (File subFile : file.listFiles()) {
            if (subFile.isDirectory()) {
                if (subFile.getName().equals("切图")) {
                    processQietuDir(subFile);
                } else {
                    findQietu(subFile);
                }
            }
        }
    }

    public static void main(String[] args) {
        findQietu(new File("/Users/chengsimin/Downloads/"));
    }
}
