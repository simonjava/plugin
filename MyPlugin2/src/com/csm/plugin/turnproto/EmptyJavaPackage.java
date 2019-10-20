package com.csm.plugin.turnproto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class EmptyJavaPackage {

    static void jiexi(File file){
        String name = file.getName().split("\\.")[0];
        Scanner sc = null;
        try {
            StringBuilder valueSb = new StringBuilder();
            sc = new Scanner(new FileInputStream(file));
            valueSb.append(String.format("package com.wali.live.proto.%s;\n",name));
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if(line.contains("package com.wali.live.proto")){

                }else if (line.contains("option java_package")){

                }else{
                    valueSb.append(line).append("\n");
                }
            }
            sc.close();


            FileWriter fos = null;
            try {
                fos = new FileWriter(file);
                fos.write(valueSb.toString());
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }catch (Exception e){

        }
    }


    public static void main(String []args){
        File file = new File("/Users/chengsimin/dev/walilive/walilive/app/src/proto");
        for(File pb:file.listFiles()){
            if(pb.isFile()){
                String name = pb.getName();
                if(name.endsWith(".proto")){
                    jiexi(pb);

//                            String a = String.format("protoc --java_out=../../../data/src/main/java-gen/ ./%s",name);
//                    System.out.println(a);
                }
            }
        }
    }
}
