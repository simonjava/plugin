package com.csm.plugin.genbuilder;

import com.csm.plugin.findviewbyid.MyInputAreaDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;

import org.apache.http.util.TextUtils;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chengsimin on 2017/7/4.
 */
public class GenBuilderAction extends AnAction {


    ArrayList<Holder> list = new ArrayList<>();

    MyInputAreaDialog myInputAreaDialog;
    StringBuilder sb;

    @Override
    public void actionPerformed(AnActionEvent event) {
        // TODtfvfvO: insert action logic here

        Project project = event.getData(PlatformDataKeys.PROJECT);

        com.intellij.openapi.editor.Editor editor = event.getData(PlatformDataKeys.EDITOR);

//        module = Messages.showInputDialog(project, "dest module name?", "Input module name", Messages.getQuestionIcon(), module, null);

        SelectionModel selectionModel = editor.getSelectionModel();
        String selectedText = selectionModel.getSelectedText();

        String lines[] = selectedText.split("\n");
        List<Holder> holderList = new ArrayList<>();
        for (String l : lines) {
            holderList.add(getParamsName(l));
        }
        StringBuilder sb = new StringBuilder();
        sb.append("public static class Builder {\n" +
                "            Params mParams = new Params();\n" +
                "\n" +
                "            Builder() {}\n" +
                "\n");

        for (Holder holder : holderList) {
            if (holder != null && !TextUtils.isEmpty(holder.className) && !TextUtils.isEmpty(holder.paramsName)) {
                String f = String.format("  public Builder set%s(%s %s) {\n" +
                                "                mParams.set%s(%s);\n" +
                                "                return this;\n" +
                                "            }"
                        , holder.turnBig(holder.paramsName)
                        , holder.className
                        , holder.paramsName
                        , holder.turnBig(holder.paramsName)
                        , holder.paramsName
                );
                sb.append(f).append("\n").append("\n");
            }

        }
        sb.append("}");
        MyInputAreaDialog.showInputText(sb.toString());
    }

    static Holder getParamsName(String line) {
        if(line==null){
            return null;
        }
        line = line.trim();
        if("".equals(line) || line.startsWith("//")){
            return null;
        }

        String className = null;
        String paramsName = null;
        String[] b = line.split("[ |=|;]");
        int ll = 0;
        for (String bb : b) {
            if (!TextUtils.isEmpty(bb)) {
                ll++;
            }
        }
        String a[] = new String[ll];
        int ii = 0;
        for (String bb : b) {
            if (!TextUtils.isEmpty(bb)) {
                a[ii++] = bb;
            }
        }
        if (a[0].equals("public") || a[0].equals("private") || a[0].equals("protected")) {
            if (a.length >= 1) {
                className = a[1];
            }
            if (a.length >= 2) {
                paramsName = a[2];
            }
        } else {
            if (a.length >= 0) {
                className = a[0];
            }
            if (a.length >= 1) {
                paramsName = a[1];
            }
        }
        if (paramsName != null && paramsName.length() >= 1) {
            if (paramsName.endsWith(";")) {
                paramsName = paramsName.substring(0, paramsName.length() - 1);
            }
        }
        Holder holder = new Holder();
        holder.className = className;
        holder.paramsName = paramsName;
        return holder;
    }


    static class Holder {
        public String className;
        public String paramsName;


        String turnBig(String name) {
            String a = name.substring(0, 1);
            String b = name.substring(1, name.length());
            return a.toUpperCase() + b;
        }

        @Override
        public String toString() {
            return "Holder{" +
                    "className='" + className + '\'' +
                    ", paramsName='" + paramsName + '\'' +
                    '}';
        }
    }


    public static void main(String[] args) throws IOException {
        System.out.println(getParamsName("private int ssss=3;"));

    }
}
