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

    public static void move() {
        {
            HashSet<String> javaPathSet = new HashSet<>();
            HashSet<String> layoutSet = new HashSet<>();
            HashSet<String> stringSet = new HashSet<>();

            // 先找到to的所有string

            File aRootFile = new File(MoveAll.fromProjectPath);
            File bRootFile = new File(MoveAll.toProjectPath);

            Utils.getInstance().digui(null, bRootFile, new IFileFilter() {
                @Override
                public boolean accept(String path) {
                    return true;
                }
            }, new Processor() {
                @Override
                public String process(String path) {
                    if(MoveAll.inTo(path)){
                        if(path.endsWith(".java")){
                            javaPathSet.add(path);
                        }
                        else if(path.contains("/layout/")&&path.endsWith(".xml")){
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
                    if (MoveAll.inFrom(path) && path.endsWith("/strings.xml")) {
                        // 得到from的string
                        HashMap<String, String> map1 = Utils.getInstance().jiexiStringXml(path);
                        //System.out.println("map:"+map1);

                        // 得到to相应的目录的string
                        String nPath = MoveJavaWithLayoutAndDrawableAction.mapPath(path);
                        HashMap<String, String> map2 = Utils.getInstance().jiexiStringXml(nPath);

                        // 得到to的本身目录的string
                        String mPath = nPath.replace(MoveAll.toResNew,MoveAll.toResOrigin);
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

            File aRootFile = new File(MoveAll.fromProjectPath);
            File bRootFile = new File(MoveAll.toProjectPath);


            Utils.getInstance().digui(null, bRootFile, new IFileFilter() {
                @Override
                public boolean accept(String path) {
                    return true;
                }
            }, new Processor() {
                @Override
                public String process(String path) {
                    if (MoveAll.inTo(path)) {
                        if ( path.endsWith(".java")) {
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
                    if (MoveAll.inFrom(path) && path.endsWith("/arrays.xml")) {
                        // 得到from array
                        LinkedHashMap<String, String> map1 = Utils.getInstance().jiexiArrayXmlFile(path);
                        //System.out.println("map:"+map1);

                        // 得到to相应的目录的array
                        String nPath = MoveJavaWithLayoutAndDrawableAction.mapPath(path);
                        HashMap<String, String> map2 = Utils.getInstance().jiexiArrayXmlFile(nPath);

                        // 得到to额外相应的目录的string
                        String mPath = nPath.replace(MoveAll.toResNew,MoveAll.toResOrigin);
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
        move();
//        move("data");
    }
}
