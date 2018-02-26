package com.csm.plugin.shrink;

import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.generation.actions.BaseGenerateAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtilBase;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

/**
 * Created by chengsimin on 2018/1/4.
 */

public class RemoveResAction extends BaseGenerateAction {
    public RemoveResAction() {
        super(null);
    }

    public RemoveResAction(CodeInsightActionHandler handler) {
        super(handler);
    }

    @Override
    public void actionPerformed(AnActionEvent event) {

        Project project = event.getData(PlatformDataKeys.PROJECT);
        // 当前编辑器
        Editor editor = event.getData(PlatformDataKeys.EDITOR);

        // 当前文件
        PsiFile file = PsiUtilBase.getPsiFileInEditor(editor, project);

        // 当前的class 解析类
        PsiClass mClass = getTargetClass(editor, file);
        // 当前文本
        Document document = editor.getDocument();
        // 当前文本按行分割
        String[] strs = document.getText().split("\n");

        /**
         * 所以都是一个 file
         *
         包声明
         PsiPackageStatement:com.a.aa
         空行
         PsiWhiteSpace
         导入
         PsiImportList
         PsiWhiteSpace
         类
         PsiClass:AA
         PsiWhiteSpace
         又一个类 非 public
         PsiClass:AAa3
         PsiWhiteSpace
         */
        System.out.println("try");
        File rootFile = new File("/Users/chengsimin/dev/miliao/mitalk的副本");
        HashSet<String> javaSet = new HashSet();
        HashSet<String> javaPathset = new HashSet();
        HashSet<String> javaPathset2 = new HashSet();
        HashSet<String> layoutSet = new HashSet<>();
        HashSet<String> layoutPathSet = new HashSet();
        HashSet<String> drawableSet = new HashSet<>();
        HashSet<String> drawablePathSet = new HashSet();


        if(false){

            layoutSet.add("micro_broadcast");
            Utils.getInstance().getLayoutAndDrawableAndJavaSetByLayoutSet(rootFile, layoutSet, layoutPathSet, drawableSet, javaSet);
            return;
        }
        print("getJavaByAndroidManifest begin ", javaSet, layoutSet, drawableSet);
        Utils.getInstance().getJavaByAndroidManifest(rootFile, javaSet);
        System.out.println(javaSet);
        javaSet.add("LiveApplicationEx");
        javaSet.add("MiTalkMainActivity");
        // 反射
        javaSet.add("PersonalPageZoomBehavior");
        //layout中
        javaSet.add("MaxHeightScrollView");

        print("getDrawableByStyle begin ", javaSet, layoutSet, drawableSet);
        Utils.getInstance().getDrawableByStyle(rootFile, drawableSet);

        while (true) {
            int size1 = javaSet.size() + layoutSet.size() + drawableSet.size();
            print("getJavaByJava begin ", javaSet, layoutSet, drawableSet);
            Utils.getInstance().getJavaByJava(project, rootFile, javaSet, javaPathset);

            // 对于已有的java集合，选择layout，因为layout中也有java
            print("getLayoutAndDrawableSetByJavaSet begin ", javaSet, layoutSet, drawableSet);
            Utils.getInstance().getLayoutAndDrawableSetByJavaSet(rootFile, javaSet, javaPathset2, layoutSet, drawableSet);

            print("getLayoutAndDrawableAndJavaSetByLayoutSet begin ", javaSet, layoutSet, drawableSet);
            Utils.getInstance().getLayoutAndDrawableAndJavaSetByLayoutSet(rootFile, layoutSet, layoutPathSet, drawableSet, javaSet);

            print("getDrawableByDrawable begin ", javaSet, layoutSet, drawableSet);
            Utils.getInstance().getDrawableByDrawable(rootFile, drawableSet, drawablePathSet);
            int size2 = javaSet.size() + layoutSet.size() + drawableSet.size();
            System.out.println("size1:" + size1 + " size2:" + size2);
            if (size1 == size2) {
                break;
            }
        }

        //先找到所有manifest
//        jiexi(file.getChildren(), 0);

        try {
            FileWriter fos = new FileWriter("/Users/chengsimin/Downloads/del.file");
            for (String line : javaSet) {
                System.out.println(line);
                line += "\r\n";
                fos.write(line);
            }
            fos.write("\r\n");
            for (String line : layoutSet) {
                System.out.println(line);
                line += "\r\n";
                fos.write(line);
            }
            fos.write("\r\n");
            for (String line : drawableSet) {
                System.out.println(line);
                line += "\r\n";
                fos.write(line);
            }
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("写入完成");
        Utils.getInstance().delete(rootFile, javaSet, layoutSet, drawableSet);
        System.out.println("删除完成");
    }

    void print(String tag, HashSet<String> javaSet, HashSet<String> layoutSet, HashSet<String> drawableSet) {
        System.out.println(tag + "javaSet.size:" + javaSet.size());
        System.out.println(tag + "layoutSet.size:" + layoutSet.size());
        System.out.println(tag + "drawableSet.size:" + drawableSet.size());
    }
}
