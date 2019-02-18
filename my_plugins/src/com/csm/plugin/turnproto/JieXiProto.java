package com.csm.plugin.turnproto;

import com.csm.plugin.movecode.Utils;
import com.csm.plugin.shrink.IFileFilter;
import com.csm.plugin.shrink.Processor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JieXiProto {

    static List<Proto> list = new ArrayList<>();
    static HashSet<String> noChangeList = new HashSet<>();

    static void jiexi(File file) {
        Proto proto = new Proto();
        String name = file.getName().split("\\.")[0];
        if(!name.contains("MiTalkChatMessage")){
            return;
        }
        Scanner sc = null;
        try {
            sc = new Scanner(new FileInputStream(file));
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.contains("option java_package")) {
                    String pa = getPackage(line);
                    proto.packageName = pa;
                } else if (line.contains("java_outer_classname")) {
                    proto.outClassName = getJavaOutClassName(line);

                } else if (line.contains("message")) {
                    proto.msgNameList.add(getMessageName(line));
                } else if (line.contains("enum")) {
                    proto.msgNameList.add(getEnumName(line));
                }
            }
            sc.close();
        } catch (Exception e) {

        }
        if (proto.packageName == null || proto.outClassName == null) {
            System.out.println(name);
        }
        list.add(proto);
//        System.out.println(proto);
    }

    static String getPackage(String line) {
        Pattern pattern = Pattern.compile("[ ]*option java_package[ ]?=[ ]?\"([\\w|\\.]+)\"");

        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            matcher.reset();
            while (matcher.find()) {
                String v1 = matcher.group(1);
                return v1;
            }
        }
        return "";
    }

    static String getJavaOutClassName(String line) {
        Pattern pattern = Pattern.compile("[ ]*option java_outer_classname[ ]?=[ ]?\"([\\w|\\.]+)\"");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            matcher.reset();
            while (matcher.find()) {
                String v1 = matcher.group(1);
                return v1;
            }
        }
        return null;
    }

    static String getMessageName(String line) {
        Pattern pattern = Pattern.compile("[ ]*message[ ]?(\\w+)[ ]?[{]?");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            matcher.reset();
            while (matcher.find()) {
                String v1 = matcher.group(1);
                return v1;
            }
        }
        return null;
    }

    static String getEnumName(String line) {
        Pattern pattern = Pattern.compile("[ ]*enum[ ]?(\\w+)[ ]?[{]?");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            matcher.reset();
            while (matcher.find()) {
                String v1 = matcher.group(1);
                return v1;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        int a = 0;
        File file = new File("/Users/chengsimin/dev/walilive/walilive/app/src/proto");
        for (File pb : file.listFiles()) {
            if (pb.isFile()) {
                String name = pb.getName();
                if (name.endsWith(".proto")) {
                    a++;
                    jiexi(pb);
                }
            }
        }

        int t = 0;
        for(Proto p:list){
            t+=p.msgNameList.size();
        }
        System.out.println(t);

        if(true){
            return;
        }
        System.out.println(a);

        Scanner sc = null;
        try {
            sc = new Scanner(new FileInputStream(new File("/Users/chengsimin/Downloads/nochange.txt")));
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                noChangeList.add(line);
            }
            sc.close();
        } catch (Exception e) {

        }
        System.out.println("加载nochange完成");

        File aRootFile = new File("/Users/chengsimin/dev/walilive/walilive/");
        Utils.getInstance().digui(null, aRootFile, new IFileFilter() {
                    @Override
                    public boolean accept(String path) {
                        if (path.endsWith(".java")
                                && !path.contains("build")){
                            if(path.contains("java-gen") && path.contains("proto")){
                                return false;
                            }
                            if(path.contains("chat-java")) {
                                return true;
                            }
                        }
                        return false;
                    }
                },
                new Processor() {
                    @Override
                    public String process(String path) {
                        System.out.println(path);
                        tryReplace(path);
                        return "";
                    }
                });


        StringBuilder stringBuilder = new StringBuilder();
        for (String s : noChangeList) {
            stringBuilder.append(s).append("\n");
        }

        FileWriter fos = null;
        try {
            fos = new FileWriter(new File("/Users/chengsimin/Downloads/nochange.txt"));
            fos.write(stringBuilder.toString());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void tryReplace(String path) {
        File file = new File(path);
        String name = file.getName().split("\\.")[0];
        Scanner sc = null;
        try {
            StringBuilder valueSb = new StringBuilder();
            sc = new Scanner(new FileInputStream(file));
            boolean nochange = true;
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String ori = line;

                for (Proto p : list) {
                    if (line.contains(p.outClassName)) {
                        for (String msgCls : p.msgNameList) {
                            String origin = p.outClassName + "\\." + msgCls;
                            String now = p.packageName + "\\." + msgCls;
                            line = line.replaceAll(origin, now);
                            line = line.replaceAll(now + "[\n]?\\.[\n]?newBuilder\\(\\)", "new " + now + "\\.Builder\\(\\)");
                        }
                    }
                }
                if (line.contains("import com.wali.live.proto.")) {
                    line = "";
                }
                line = line.replaceAll("InvalidProtocolBufferException", "IOException");
                if (line.contains("import com.google.protobuf.IOException;")) {
                    line = "";
                }
                valueSb.append(line).append("\n");
                if (!ori.equals(line)) {
                    nochange = false;
                }
            }
            sc.close();

            System.out.println("nochange=" + nochange);

            if (nochange) {
                noChangeList.add(path);
            } else {
                FileWriter fos = null;
                try {
                    fos = new FileWriter(file);
                    fos.write(valueSb.toString());
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {

        }
    }

    static class Proto {
        String packageName;
        String outClassName;
        List<String> msgNameList = new ArrayList<>();

        public String getOriginPackageName() {
            int a = packageName.lastIndexOf(".");
            return packageName.substring(0, a);
        }

        @Override
        public String toString() {
            return "Proto{" +
                    "packageName='" + packageName + '\'' +
                    ", outClassName='" + outClassName + '\'' +
                    ", msgNameList=" + msgNameList +
                    '}';
        }
    }
}
