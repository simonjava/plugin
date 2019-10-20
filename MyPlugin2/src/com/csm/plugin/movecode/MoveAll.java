package com.csm.plugin.movecode;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Scanner;

public class MoveAll {

    // 要支持 多个from 多个to。

    public static final String fromProjectPath = "/Users/chengsimin/dev/miliao/mitalk/";
    public static final String toProjectPath = "/Users/chengsimin/dev/walilive/walilive/";

    public static final String toResOrigin = "/res/";
//    public static final String toResNew = "/res/";
    public static final String toResNew = "/chat-res/";

    // 一个from只会对应一个to ，一个to可能来自多个from
    public static final ArrayList<P> pathMap = new ArrayList<>();

    static {
        pathMap.add(new P(fromProjectPath + "communication/src/main/java/", toProjectPath + "app/src/main/chat-java/"));
        pathMap.add(new P(fromProjectPath + "communication/src/main/res/", toProjectPath + "app/src/main/chat-res/"));
        pathMap.add(new P(fromProjectPath + "communication/src/main/java-gen/", toProjectPath + "app/src/main/chat-java-gen/"));
        pathMap.add(new P(fromProjectPath + "livecommon/src/main/java/", toProjectPath + "livecommon/src/main/java/"));
        pathMap.add(new P(fromProjectPath + "data/src/main/java/", toProjectPath + "data/src/main/java/"));
        pathMap.add(new P(fromProjectPath + "data/src/main/java-gen/", toProjectPath + "data/src/main/java-gen/"));
        pathMap.add(new P(fromProjectPath + "common/src/main/java/", toProjectPath + "common/src/main/java/"));
    }

    static class P {
        String from;
        String to;

        public P(String from, String to) {
            this.from = from;
            this.to = to;
        }
    }

    public static boolean inTo(String path) {
        for (P p : pathMap) {
            if (path.contains(p.to)) {
                return true;
            }
        }
        return false;
    }


    public static boolean inFrom(String path) {
        for (P p : pathMap) {
            if (path.contains(p.from)) {
                return true;
            }
        }
        return false;
    }

    public static String mapTo2From(String path) {
        for (P p : pathMap) {
            if (path.contains(p.to)) {
                return path.replace(p.to, p.from);
            }
        }
        return path;
    }

    public static String mapFrom2To(String path) {
        for (P p : pathMap) {
            if (path.contains(p.from)) {
                return path.replace(p.from, p.to);
            }
        }
        return path;
    }

    public static void main(String[] args) {
        MoveLayoutAndDrawable.main(null);
        SimpleReplace.main(null);
        System.out.println("SimpleReplace over");
        MoveString.main(null);
        System.out.println("MoveString over");
        MoveColor.main(null);
        System.out.println("MoveColor over");
        MoveDimen.main(null);
        System.out.println("MoveDimen over");
    }

}
