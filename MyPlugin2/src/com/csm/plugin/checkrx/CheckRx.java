package com.csm.plugin.checkrx;

import com.csm.plugin.movecode.Utils;
import com.csm.plugin.shrink.IFileFilter;
import com.csm.plugin.shrink.Processor;

import java.io.File;
import java.io.FileInputStream;
import java.util.Scanner;

public class CheckRx {

    public static void main(String[] args) {

        File aRootFile = new File("/Users/chengsimin/dev/walilive/walilive的副本/");
        Utils.getInstance().digui(null, aRootFile, new IFileFilter() {
                    @Override
                    public boolean accept(String path) {
                        if (path.endsWith(".java")
                                && !path.contains("build")) {
                            if (path.contains("java-gen") && path.contains("proto")) {
                                return false;
                            }
                            return true;
                        }
                        return false;
                    }
                },
                new Processor() {
                    @Override
                    public String process(String path) {
                        tryReplace(path);
                        return "";
                    }
                });

    }


    private static void tryReplace(String path) {
        File file = new File(path);
        String name = file.getName().split("\\.")[0];
        Scanner sc = null;
        boolean b = false;
        try {
            StringBuilder valueSb = new StringBuilder();
            sc = new Scanner(new FileInputStream(file));
            int f_num =0;
            int num=0;
            while (sc.hasNextLine()) {
                num++;
                String line = sc.nextLine();
                if (line.contains("public void call(Subscriber")) {
                    if(b){
                        System.out.println(path+" num:"+f_num);
                    }
                    b = true;
                    f_num = num;
                }
                if (line.contains("subscriber.onCompleted()") || line.contains("sub.onCompleted()")) {
                    b = false;
                }
            }
            sc.close();
            if(b){
                System.out.println(path);
            }
        } catch (Exception e) {

        }
    }

}
