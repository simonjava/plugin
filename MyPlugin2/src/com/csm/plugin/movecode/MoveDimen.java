package com.csm.plugin.movecode;

import com.csm.plugin.shrink.IFileFilter;
import com.csm.plugin.shrink.Processor;

import org.apache.http.util.TextUtils;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

public class MoveDimen {
    static class FF {
        boolean yange = false;
        String origin;
        String replace;

        public FF(boolean yange, String origin, String replace) {
            this.origin = origin;
            this.replace = replace;
        }
    }

    public static void main(String[] args) {
        HashSet<String> javaPathSet = new HashSet<>();
        HashSet<String> layoutSet = new HashSet<>();
        HashSet<String> drawableSet = new HashSet<>();

        HashSet<String> colorSet = new HashSet<>();

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
                    if (path.endsWith(".java")) {
                        javaPathSet.add(path);
                    } else if (path.contains("/layout/")) {
                        layoutSet.add(Utils.getInstance().getLayoutFileNameByPath(path));
                    } else if (path.contains("/drawable")) {
                        drawableSet.add(Utils.getInstance().getDrawableFileNameByPath(path));
                    }
                }

                return null;
            }
        });
        Utils.getInstance().getDimenByJavaSet(bRootFile, javaPathSet, colorSet);
        Utils.getInstance().getDimenByLayoutSet(bRootFile, layoutSet, colorSet);
        Utils.getInstance().getDimenByDrawableSet(bRootFile, drawableSet, colorSet);
        //得到 string 的map
        //System.out.println(colorSet);
        Utils.getInstance().digui(null, aRootFile, new IFileFilter() {
            @Override
            public boolean accept(String path) {
                return true;
            }
        }, new Processor() {
            @Override
            public String process(String path) {
                if(MoveAll.inFrom(path)){
                    if (path.endsWith("/dimens.xml")) {
                        // 得到有乐的string
                        LinkedHashMap<String, String> map1 = Utils.getInstance().jiexiDimenXml(path);
                        //System.out.println("map:"+map1);

                        // 得到米聊live相应的目录的string
                        String nPath = MoveJavaWithLayoutAndDrawableAction.mapPath(path);
                        LinkedHashMap<String, String> map2 = Utils.getInstance().jiexiDimenXml(nPath);

                        // 得到米聊talk相应的目录的string
                        String mPath = nPath.replace(MoveAll.toResNew,MoveAll.toResOrigin);
                        HashMap<String, String> map3 = Utils.getInstance().jiexiDimenXml(mPath);
                        for (String key : colorSet) {
                            if (!map2.containsKey(key) && !map3.containsKey(key)) {
                                String v = map1.get(key);
                                if (!TextUtils.isEmpty(v)) {
                                    map2.put(key, v);
                                }
                            }
                        }
                        Utils.getInstance().genDimenXmlFile(nPath, map2);
                    }
                }
                return null;
            }
        });
    }
}
