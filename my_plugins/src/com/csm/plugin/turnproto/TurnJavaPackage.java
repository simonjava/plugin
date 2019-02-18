package com.csm.plugin.turnproto;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TurnJavaPackage {

    static void jiexi(File file){
        String name = file.getName().split("\\.")[0];
        Scanner sc = null;
        try {
            StringBuilder valueSb = new StringBuilder();
            sc = new Scanner(new FileInputStream(file));

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if(line.contains("option java_package =")){
                    if(line.contains(".proto\";")){
                        String a = line.split("\\.proto\";")[0];
                        line = a+".proto."+name+"\";";
                        System.out.println(line);
                        valueSb.append(line).append("\r\n");
                    }else{

                        valueSb.append(line).append("\r\n");
                    }
                }else{
                    valueSb.append(line).append("\r\n");
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
                }
            }
        }
    }
}
