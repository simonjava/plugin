package com.csm.plugin.movecode;

import com.csm.plugin.shrink.IFileFilter;
import com.csm.plugin.shrink.Processor;

import java.io.File;
import java.util.HashSet;
import java.util.Scanner;

public class RemoveDuplicateDrawableAndString {
    public static void main(String[] args) {
        File bRootFile = new File("/Users/chengsimin/dev/miliao/mitalk");
        HashSet<String> drawableSet = new HashSet<>();

        Utils.getInstance().digui(null, bRootFile, new IFileFilter() {
                    @Override
                    public boolean accept(String path) {
                        return true;
                    }
                },
                new Processor() {
                    @Override
                    public String process(String path) {
                        if(path.contains("/app/src/main/res-milive/drawable")){
                            String fileName = Utils.getInstance().getDrawableFileNameByPath(path);
                            drawableSet.add(fileName);
                        }
                        return "";
                    }
                });



        Utils.getInstance().digui(null, bRootFile, new IFileFilter() {
                    @Override
                    public boolean accept(String path) {
                            return true;
                    }
                },
                new Processor() {
                    @Override
                    public String process(String path) {
                        if(path.contains("/app/src/main/res/drawable")){
                            String fileName = Utils.getInstance().getDrawableFileNameByPath(path);
                            if(drawableSet.contains(fileName)){
                                System.out.println("删除"+path);
                                new File(path).delete();
                            }
                        }
                        return "";
                    }
                });




        HashSet<String> layoutSet = new HashSet<>();

        Utils.getInstance().digui(null, bRootFile, new IFileFilter() {
                    @Override
                    public boolean accept(String path) {
                            return true;
                    }
                },
                new Processor() {
                    @Override
                    public String process(String path) {
                        if(path.contains("/app/src/main/res-milive/layout")){
                            String fileName = Utils.getInstance().getLayoutFileNameByPath(path);
                            layoutSet.add(fileName);
                        }
                        return "";
                    }
                });

        Utils.getInstance().digui(null, bRootFile, new IFileFilter() {
                    @Override
                    public boolean accept(String path) {
                            return true;
                    }
                },
                new Processor() {
                    @Override
                    public String process(String path) {
                        if(path.contains("/app/src/main/res/layout")){
                            String fileName = Utils.getInstance().getLayoutFileNameByPath(path);
                            if(layoutSet.contains(fileName)){
                                System.out.println("删除"+path);
                                new File(path).delete();
                            }
                        }
                        return "";
                    }
                });

        //
    }
}
