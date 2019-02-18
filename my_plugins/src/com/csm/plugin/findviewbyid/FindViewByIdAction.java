package com.csm.plugin.findviewbyid;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.impl.ProjectImpl;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlToken;

import org.apache.http.util.TextUtils;

import java.util.ArrayList;

/**
 * Created by chengsimin on 2017/7/4.
 */
public class FindViewByIdAction extends AnAction {


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
        // 得到当前文件路径
        VirtualFile vf = event.getData(PlatformDataKeys.VIRTUAL_FILE);

        PsiFile psiFile = PsiManager.getInstance(project).findFile(vf);

        XmlFile xmlFile = (XmlFile) psiFile;
        XmlTag xmlTag = xmlFile.getRootTag();
        list.clear();
        sb = new StringBuilder();
        jiexi(xmlTag);
        for (int i = 0; i < list.size(); i++) {
            Holder holder = list.get(i);
            sb.append(holder.getDesc()).append("\r\n");
        }
        sb.append("\r\n");
        for (int i = 0; i < list.size(); i++) {
            Holder holder = list.get(i);
            sb.append(holder.getFieldStr()).append("\r\n");
        }
        for (int i = 0; i < list.size(); i++) {
            Holder holder = list.get(i);
            sb.append(holder.getFieldStr2()).append("\r\n");
        }
        MyInputAreaDialog.showInputText(sb.toString());
    }


    /**
     * 利用递归将这个文件解析到原子粒度
     */
    void jiexi(XmlTag tag) {
        Holder holder = new Holder();
        String[] tt = tag.getName().split("\\.");

        holder.className = tt[tt.length-1];
        String idStr = tag.getAttributeValue("android:id");
        if (idStr != null && !idStr.equals("")) {
            String a[] = idStr.split("/");
            if (a.length >= 2) {
                holder.idStr = a[1];
            }
        }
        if (holder.isValid()) {
            list.add(holder);
        }
//        sb.append("name:").append(tag.getName()).append("\r\n");
//        sb.append("value:").append(tag.getValue().getText()).append("\r\n");
//        sb.append("attr:").append(tag.getAttributeValue("android:id")).append("\r\n");
        for (XmlTag tag1 : tag.getSubTags()) {
            jiexi(tag1);
        }
    }

    /**
     * 利用递归将这个文件解析到原子粒度
     *
     * @param childs
     */
    void jiexi2(PsiElement[] childs, int deep) {
        for (PsiElement psiElement : childs) {

            for (int i = 0; i < deep; i++) {
                sb.append(" ");
            }
            if (deep > 1) {
                sb.append(psiElement.toString() + " text:" + psiElement.getText()).append("\r\n");
            }
            jiexi2(psiElement.getChildren(), deep + 1);
        }
    }

    static class Holder {
        public String className;
        public String idStr;

        public boolean isValid() {
            if (TextUtils.isEmpty(className)) {
                return false;
            }
            if (TextUtils.isEmpty(idStr)) {
                return false;
            }
            return true;
        }

        String turnBig(String name){
            String a = name.substring(0, 1);
            String b = name.substring(1, name.length());
            return a.toUpperCase()+b;
        }

        public String getVarName(boolean m) {
            StringBuilder sb = new StringBuilder();
            String[] varNames = idStr.split("_" );
            for(int i=0;i<varNames.length;i++){
                if(i==0){
                    if(m){
                        sb.append("m").append(turnBig(varNames[i]));
                    }else{
                        sb.append(varNames[i]);
                    }
                }else{
                    sb.append(turnBig(varNames[i]));
                }
            }
            return sb.toString();
        }

        public String getDesc() {
            String desc = String.format("%s %s = (%s)mRootView.findViewById(R.id.%s);", className, getVarName(false), className, idStr);
            return desc;
        }

        public String getFieldStr() {
            String desc = String.format("%s %s;", className, getVarName(true));
            return desc;
        }

        public String getFieldStr2() {
            String desc = String.format("%s = (%s)mRootView.findViewById(R.id.%s);", getVarName(true),className,idStr);
            return desc;
        }
    }
}
