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

public class MoveJavaWithLayoutAndDrawableAction extends BaseGenerateAction {
    public MoveJavaWithLayoutAndDrawableAction() {
        super(null);
    }

    public MoveJavaWithLayoutAndDrawableAction(CodeInsightActionHandler handler) {
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
        File aRootFile = new File("/Users/chengsimin/dev/walilive/walilive");
        File bRootFile = new File("/Users/chengsimin/dev/miliao/mitalk");

        String input = Messages.showInputDialog(project,
                "What is your name?",
                "Input your name",
                Messages.getQuestionIcon());

        // 在A工程下找到这个txt类

        // 接收输入

        Utils.getInstance().digui(project, aRootFile, new IFileFilter() {
                    @Override
                    public boolean accept(String path) {
                        if (path.contains("/app/src/main/java/") && path.endsWith("/" + input + ".java")) {
                            return true;
                        }
                        return false;
                    }
                },
                new Processor() {
                    @Override
                    public String process(String path) {
                        String bPath = mapPath(path);
                        Utils.copyFile(path, bPath);
                        return null;
                    }
                });
    }

    static String mapPath(String path) {
        return MoveAll.mapFrom2To(path);
//        String bPath = path.replace("/Users/chengsimin/dev/miliao/mitalk/communication/src/main/java/", "/Users/chengsimin/dev/GameCenterPhone/gamecenter_knights/app/src/main/chat-java/");
//        bPath = bPath.replace("/Users/chengsimin/dev/miliao/mitalk/communication/src/main/res/", "/Users/chengsimin/dev/GameCenterPhone/gamecenter_knights/app/src/main/chat-res/");
//        bPath = bPath.replace("/Users/chengsimin/dev/miliao/mitalk/communication/src/main/java-gen/", "/Users/chengsimin/dev/GameCenterPhone/gamecenter_knights/app/src/main/chat-java-gen/");
//
//        bPath = bPath.replace("/Users/chengsimin/dev/miliao/mitalk/livecommon/src/main/java/", "/Users/chengsimin/dev/GameCenterPhone/gamecenter_knights/app/src/main/chat-java/");
//        bPath = bPath.replace("/Users/chengsimin/dev/miliao/mitalk/livecommon/src/main/res/", "/Users/chengsimin/dev/GameCenterPhone/gamecenter_knights/app/src/main/chat-res/");
//
//        bPath = bPath.replace("/Users/chengsimin/dev/miliao/mitalk/data/src/main/java/", "/Users/chengsimin/dev/GameCenterPhone/gamecenter_knights/app/src/main/chat-java/");
//        bPath = bPath.replace("/Users/chengsimin/dev/miliao/mitalk/data/src/main/java-gen/", "/Users/chengsimin/dev/GameCenterPhone/gamecenter_knights/app/src/main/chat-java-gen/");
//        bPath = bPath.replace("/Users/chengsimin/dev/miliao/mitalk/data/src/main/res/", "/Users/chengsimin/dev/GameCenterPhone/gamecenter_knights/app/src/main/chat-res/");
//
//        bPath = bPath.replace("/Users/chengsimin/dev/miliao/mitalk/common/src/main/java/", "/Users/chengsimin/dev/GameCenterPhone/gamecenter_knights/app/src/main/chat-java/");
//        bPath = bPath.replace("/Users/chengsimin/dev/miliao/mitalk/common/src/main/res/", "/Users/chengsimin/dev/GameCenterPhone/gamecenter_knights/app/src/main/chat-res/");
//        return bPath;
    }

    void print(String tag, HashSet<String> javaSet, HashSet<String> layoutSet, HashSet<String> drawableSet) {
        System.out.println(tag + "javaSet.size:" + javaSet.size());
        System.out.println(tag + "layoutSet.size:" + layoutSet.size());
        System.out.println(tag + "drawableSet.size:" + drawableSet.size());
    }

    public static void move(HashSet<String> inputs) {
        File aRootFile = new File(MoveAll.fromProjectPath);
        HashSet<String> copyPath = new HashSet<>();

        //具体路径
        HashSet<String> javaPathSet = new HashSet();

        HashSet<String> layoutSet = new HashSet<>();
        //具体路径
        HashSet<String> layoutPathSet = new HashSet();

        HashSet<String> drawableSet = new HashSet<>();
        //具体路径
        HashSet<String> drawablePathSet = new HashSet();

        Utils.getInstance().digui(null, aRootFile, new IFileFilter() {
                    @Override
                    public boolean accept(String path) {
                        if (MoveAll.inFrom(path)) {
                            for (String finalInput : inputs) {
                                if (path.equals(finalInput)) {
                                    return true;
                                }
                                if (path.endsWith("/" + finalInput + ".java")) {
                                    return true;
                                }
                            }
                        }
                        return false;
                    }
                },
                new Processor() {
                    @Override
                    public String process(String path) {
                        javaPathSet.add(path);
                        return null;
                    }
                });


        Utils.getInstance().getLayoutAndDrawableSetByJavaSet(aRootFile, javaPathSet, layoutSet, drawableSet);
        Utils.getInstance().getLayoutAndDrawableAndJavaSetByLayoutSet(aRootFile, layoutSet, layoutPathSet, drawableSet, null);
        Utils.getInstance().getDrawableByDrawable(aRootFile, drawableSet, drawablePathSet);
        Utils.getInstance().getDrawableByDrawable(aRootFile, drawableSet, drawablePathSet);

        copyPath.addAll(javaPathSet);
        copyPath.addAll(layoutPathSet);
        copyPath.addAll(drawablePathSet);
        for (String path : copyPath) {
            String bPath = mapPath(path);
            //String oPath = path.replace("/walilive/walilive/","/miliao/mitalk/");
            //if(!new File(oPath).exists()){
            Utils.copyFile(path, bPath);
            //}
        }

        /**
         * 字符串，
         * */
        System.out.println(inputs.size() + " over");
    }

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            if (input.startsWith("import ")) {
                String aa[] = input.split(" ");

                if (aa.length == 2) {
                    String pack = aa[1].substring(0, aa[1].length() - 1);
                    if (pack.startsWith("com.wali.live.") && !pack.endsWith(".R")) {
                        input = pack.replace('.', '/');

                    } else {
                        System.err.println(input + "不需要导入的!!!");
                        continue;
                    }
                } else {
                    System.err.println("import格式错误!!! input:" + input);
                    continue;
                }
            }
            System.out.println("input=" + input);
            HashSet<String> ss = new HashSet<String>();
            ss.add(input);
            move(ss);
        }
    }
}
