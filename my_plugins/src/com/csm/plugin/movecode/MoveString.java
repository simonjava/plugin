package com.csm.plugin.movecode;

import com.csm.plugin.shrink.IFileFilter;
import com.csm.plugin.shrink.Processor;

import org.apache.http.util.TextUtils;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MoveString {
    static class FF {
        boolean yange = false;
        String origin;
        String replace;

        public FF(boolean yange, String origin, String replace) {
            this.origin = origin;
            this.replace = replace;
        }
    }

    public static void move(String fromModule,String toModule) {
        {
            HashSet<String> javaPathSet = new HashSet<>();
            HashSet<String> layoutSet = new HashSet<>();
            HashSet<String> stringSet = new HashSet<>();

            File aRootFile = new File("/Users/chengsimin/dev/miliao/mitalk");
            File bRootFile = new File("/Users/chengsimin/dev/GameCenterPhone/gamecenter_knights/");

            Utils.getInstance().digui(null, bRootFile, new IFileFilter() {
                @Override
                public boolean accept(String path) {
                    return true;
                }
            }, new Processor() {
                @Override
                public String process(String path) {
                    if (toModule.equals("app")) {
                        if (path.contains(toModule + "/src/main/chat-java/") && path.endsWith(".java")) {
                            javaPathSet.add(path);
                        }
                        if (path.contains(toModule + "/src/main/chat-res/layout/")) {
                            layoutSet.add(Utils.getInstance().getLayoutFileNameByPath(path));
                        }
                    } else {
                        if (path.contains(toModule + "/src/main/java/") && path.endsWith(".java")) {
                            javaPathSet.add(path);
                        }
                        if (path.contains(toModule + "/src/main/res/layout/")) {
                            layoutSet.add(Utils.getInstance().getLayoutFileNameByPath(path));
                        }
                    }
                    return null;
                }
            });
            Utils.getInstance().getStringByJavaSet(bRootFile, javaPathSet, stringSet);
            Utils.getInstance().getStringByLayoutSet(bRootFile, layoutSet, stringSet);

            //得到 string 的map
            //System.out.println(stringSet);

            Utils.getInstance().digui(null, aRootFile, new IFileFilter() {
                @Override
                public boolean accept(String path) {
                    return true;
                }
            }, new Processor() {
                @Override
                public String process(String path) {
                    if (path.contains(fromModule + "/src/main/res/") && path.endsWith("/strings.xml")) {
                        // 得到有乐的string
                        HashMap<String, String> map1 = Utils.getInstance().jiexiStringXml(path);
                        //System.out.println("map:"+map1);

                        // 得到米聊live相应的目录的string
                        String nPath = MoveJavaWithLayoutAndDrawableAction.mapPath(path);
                        HashMap<String, String> map2 = Utils.getInstance().jiexiStringXml(nPath);

                        // 得到米聊talk相应的目录的string
                        String mPath = path.replace("/Users/chengsimin/dev/miliao/mitalk/communication","/Users/chengsimin/dev/GameCenterPhone/gamecenter_knights/app");
                        HashMap<String, String> map3 = Utils.getInstance().jiexiStringXml(mPath);
                        for (String key : stringSet) {
                            if (!map2.containsKey(key) && !map3.containsKey(key)) {
                                String v = map1.get(key);
                                if (!TextUtils.isEmpty(v)) {
                                    map2.put(key, v);
                                }
                            }
                        }
                        Utils.getInstance().genStringXmlFile(nPath, map2);
                    }
                    return null;
                }
            });
        }
        {
            HashSet<String> javaPathSet = new HashSet<>();
            HashSet<String> arraySet = new HashSet<>();

            File aRootFile = new File("/Users/chengsimin/dev/miliao/mitalk");
            File bRootFile = new File("/Users/chengsimin/dev/GameCenterPhone/gamecenter_knights/");


            Utils.getInstance().digui(null, bRootFile, new IFileFilter() {
                @Override
                public boolean accept(String path) {
                    return true;
                }
            }, new Processor() {
                @Override
                public String process(String path) {
                    if (toModule.equals("app")) {
                        if (path.contains(toModule + "/src/main/chat-java/") && path.endsWith(".java")) {
                            javaPathSet.add(path);
                        }
                    } else {
                        if (path.contains(toModule + "/src/main/java/") && path.endsWith(".java")) {
                            javaPathSet.add(path);
                        }
                    }
                    return null;
                }
            });
            Utils.getInstance().getArrayByJavaSet(bRootFile, javaPathSet, arraySet);

            //得到 string 的map
            Utils.getInstance().digui(null, aRootFile, new IFileFilter() {
                @Override
                public boolean accept(String path) {
                    return true;
                }
            }, new Processor() {
                @Override
                public String process(String path) {
                    if (path.contains(fromModule + "/src/main/res/") && path.endsWith("/arrays.xml")) {
                        // 得到有乐的string
                        LinkedHashMap<String, String> map1 = Utils.getInstance().jiexiArrayXmlFile(path);
                        //System.out.println("map:"+map1);

                        // 得到米聊live相应的目录的string
                        String nPath = MoveJavaWithLayoutAndDrawableAction.mapPath(path);
                        HashMap<String, String> map2 = Utils.getInstance().jiexiArrayXmlFile(nPath);

                        // 得到米聊talk相应的目录的string
                        // 得到米聊talk相应的目录的string
                        String mPath = path.replace("/Users/chengsimin/dev/miliao/mitalk/communication","/Users/chengsimin/dev/GameCenterPhone/gamecenter_knights/app");
                        HashMap<String, String> map3 = Utils.getInstance().jiexiArrayXmlFile(mPath);
                        for (String key : arraySet) {
                            if (!map2.containsKey(key) && !map3.containsKey(key)) {
                                String v = map1.get(key);
                                if (!TextUtils.isEmpty(v)) {
                                    map2.put(key, v);
                                }
                            }
                        }
                        Utils.getInstance().genArrayXmlFile(nPath, map2);
                    }
                    return null;
                }
            });
        }
    }

    public static void main(String[] args) {
        if (false) {
            Pattern pattern = Pattern.compile(".*?(plurals|string).(\\w+)(,|\\)|;| |)");

            Matcher matcher = pattern.matcher("mTitleInfo.setText(getResources().getQuantityString(R.plurals.game_comment_number, 0, 0));");
            if (matcher.find()) {
                matcher.reset();
                while (matcher.find()) {
                    for(int i =0;i<matcher.groupCount();i++){
                        System.out.println(matcher.group(i));
                    }
                }
            }
            return;
        }
        move("communication","app");
//        move("data");
    }
}
