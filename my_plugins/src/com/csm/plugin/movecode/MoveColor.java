package com.csm.plugin.movecode;

import com.csm.plugin.shrink.IFileFilter;
import com.csm.plugin.shrink.Processor;

import org.apache.http.util.TextUtils;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

public class MoveColor {
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
        HashSet<String> colorXmlSet = new HashSet<>();

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
                    } else if (path.contains("/color")) {
                        colorXmlSet.add(Utils.getInstance().getColorFileNameByPath(path));
                    }
                }
                return null;
            }
        });
        Utils.getInstance().getColorByJavaSet(bRootFile, javaPathSet, colorSet);
        Utils.getInstance().getColorByLayoutSet(bRootFile, layoutSet, colorSet);
        Utils.getInstance().getColorByDrawableSet(bRootFile, drawableSet, colorSet);
        Utils.getInstance().getColorByColorXmlSet(bRootFile, colorXmlSet, colorSet);
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
                    if (path.endsWith("/colors.xml")) {
                        // 得到 from 的 color
                        LinkedHashMap<String, String> map1 = Utils.getInstance().jiexiColorXml(path);
                        //System.out.println("map:"+map1);

                        // 得到 to 相应的目录的 color
                        String nPath = MoveJavaWithLayoutAndDrawableAction.mapPath(path);
                        LinkedHashMap<String, String> map2 = Utils.getInstance().jiexiColorXml(nPath);

                        // 得到 to 额外目录的 color
                        String mPath = nPath.replace(MoveAll.toResNew,MoveAll.toResOrigin);
                        LinkedHashMap<String, String> map3 = Utils.getInstance().jiexiColorXml(mPath);
                        for (String key : colorSet) {
                            if (!map2.containsKey(key) && !map3.containsKey(key)) {
                                String v = map1.get(key);
                                if (!TextUtils.isEmpty(v)) {
                                    map2.put(key, v);
                                }
                            }
                        }
                        Utils.getInstance().genColorXmlFile(nPath, map2);
                    }

                    // 还有在color文件夹下的
                    if (path.contains("/color/")) {
                        String fileName = Utils.getInstance().getColorFileNameByPath(path);
                        if (colorSet.contains(fileName)) {
                            //需要拷贝的
                            String nPath = MoveJavaWithLayoutAndDrawableAction.mapPath(path);
                            String mPath = nPath.replace(MoveAll.toResNew,MoveAll.toResOrigin);
                            if (!new File(mPath).exists()) {
                                Utils.copyFile(path, nPath);
                            }
                        }
                    }
                }
                return null;
            }
        });
    }
}
