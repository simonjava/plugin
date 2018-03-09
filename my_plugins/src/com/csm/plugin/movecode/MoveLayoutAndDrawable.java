package com.csm.plugin.movecode;

import com.csm.plugin.shrink.IFileFilter;
import com.csm.plugin.shrink.Processor;
import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.generation.actions.BaseGenerateAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtilBase;

import java.io.File;
import java.util.HashSet;
import java.util.Scanner;

/**
 * Created by chengsimin on 2018/1/4.
 */

public class MoveLayoutAndDrawable extends BaseGenerateAction {
    public MoveLayoutAndDrawable(CodeInsightActionHandler handler) {
        super(handler);
    }

    public static void main(String[] args) {
        File bRootFile = new File("/Users/chengsimin/dev/miliao/mitalk");
        //具体路径
        HashSet<String> javaPathSet = new HashSet();

        Utils.getInstance().digui(null, bRootFile, new IFileFilter() {
                    @Override
                    public boolean accept(String path) {
                        if (path.contains("src/main/java-milive/") && path.endsWith(".java")) {
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

        HashSet<String> ss = new HashSet();
        for(String path : javaPathSet){
            String nPath = path.replace("/miliao/mitalk/app/src/main/java-milive/","/walilive/walilive/app/src/main/java/");
            ss.add(nPath);
        }
        MoveJavaWithLayoutAndDrawableAction.move(ss);
    }
}
