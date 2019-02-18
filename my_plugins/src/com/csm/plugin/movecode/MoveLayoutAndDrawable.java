package com.csm.plugin.movecode;

import com.csm.plugin.shrink.IFileFilter;
import com.csm.plugin.shrink.Processor;
import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.generation.actions.BaseGenerateAction;

import java.io.File;
import java.util.HashSet;

/**
 * Created by chengsimin on 2018/1/4.
 */

public class MoveLayoutAndDrawable extends BaseGenerateAction {
    public MoveLayoutAndDrawable(CodeInsightActionHandler handler) {
        super(handler);
    }

    public static void main(String[] args) {
        //找到目标路径的所有java类。
        File bRootFile = new File(MoveAll.toProjectPath);
        HashSet<String> javaPathSet = new HashSet();
        Utils.getInstance().digui(null, bRootFile, new IFileFilter() {
                    @Override
                    public boolean accept(String path) {
                        if(path.endsWith(".java") && MoveAll.inTo(path)){
                            javaPathSet.add(path);
                        }
                        return false;
                    }
                },
                new Processor() {
                    @Override
                    public String process(String path) {
                        return null;
                    }
                });


        // 映射成 from 中的java类
        HashSet<String> ss = new HashSet();
        for(String path : javaPathSet){
            MoveAll.mapTo2From(path);
        }
        MoveJavaWithLayoutAndDrawableAction.move(ss);
    }
}
