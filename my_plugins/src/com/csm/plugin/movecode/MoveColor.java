package com.csm.plugin.movecode;

import com.csm.plugin.shrink.IFileFilter;
import com.csm.plugin.shrink.Processor;

import org.apache.http.util.TextUtils;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

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

        File aRootFile = new File("/Users/chengsimin/dev/miliao/mitalk/");
        File bRootFile = new File("/Users/chengsimin/dev/GameCenterPhone/gamecenter_knights/");

        Utils.getInstance().digui(null, bRootFile, new IFileFilter() {
            @Override
            public boolean accept(String path) {
                return true;
            }
        }, new Processor() {
            @Override
            public String process(String path) {
                if (path.contains("src/main/chat-java/") && path.endsWith(".java")){
                    javaPathSet.add(path);
                }
                if(path.contains("src/main/chat-res/layout/")){
                    layoutSet.add(Utils.getInstance().getLayoutFileNameByPath(path));
                }
                if(path.contains("src/main/chat-res/drawable")){
                    drawableSet.add(Utils.getInstance().getDrawableFileNameByPath(path));
                }
                if(path.contains("src/main/chat-res/color")){
                    colorXmlSet.add(Utils.getInstance().getColorFileNameByPath(path));
                }
                return null;
            }
        });
        Utils.getInstance().getColorByJavaSet(bRootFile,javaPathSet,colorSet);
        Utils.getInstance().getColorByLayoutSet(bRootFile,layoutSet,colorSet);
        Utils.getInstance().getColorByDrawableSet(bRootFile,drawableSet,colorSet);
        Utils.getInstance().getColorByColorXmlSet(bRootFile,colorXmlSet,colorSet);
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
                if(path.contains("/communication/src/main/res/") && path.endsWith("/colors.xml")){
                    // 得到有乐的string
                    HashMap<String,String> map1 = Utils.getInstance().jiexiColorXml(path);
                    //System.out.println("map:"+map1);

                    // 得到米聊live相应的目录的string
                    String nPath = MoveJavaWithLayoutAndDrawableAction.mapPath(path);
                    HashMap<String,String> map2 = Utils.getInstance().jiexiColorXml(nPath);

                    // 得到米聊talk相应的目录的string
                    String mPath = path.replace("/Users/chengsimin/dev/miliao/mitalk/communication","/Users/chengsimin/dev/GameCenterPhone/gamecenter_knights/app");
                    HashMap<String,String> map3 = Utils.getInstance().jiexiColorXml(mPath);
                    for(String key:colorSet){
                        if(!map2.containsKey(key) && !map3.containsKey(key)){
                            String v = map1.get(key);
                            if(!TextUtils.isEmpty(v)){
                                map2.put(key,v);
                            }
                        }
                    }
                    Utils.getInstance().genColorXmlFile(nPath,map2);
                }
                // 还有在color文件夹下的
                if(path.contains("/communication/src/main/res/color/")){
                    String fileName = Utils.getInstance().getColorFileNameByPath(path);
                    if(colorSet.contains(fileName)){
                        //需要拷贝的
                        String bPath = MoveJavaWithLayoutAndDrawableAction.mapPath(path);
                        String oPath = path.replace("/Users/chengsimin/dev/miliao/mitalk/communication","/Users/chengsimin/dev/GameCenterPhone/gamecenter_knights/app");
                        if(!new File(oPath).exists()){
                            Utils.copyFile(path, bPath);
                        }
                    }
                }
                return null;
            }
        });
    }
}
