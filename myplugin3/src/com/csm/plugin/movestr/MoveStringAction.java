package com.csm.plugin.movestr;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chengsimin on 2017/7/4.
 */
public class MoveStringAction extends AnAction {

    public void test(AnActionEvent event) {
        // TODO: insert action logic here

        Project project = event.getData(PlatformDataKeys.PROJECT);
        com.intellij.openapi.editor.Editor editor = event.getData(PlatformDataKeys.EDITOR);

        module = Messages.showInputDialog(project, "dest module name?", "Input module name", Messages.getQuestionIcon(), module, null);

        StringBuilder sb = new StringBuilder();
        SelectionModel selectionModel = editor.getSelectionModel();
        sb.append("\r\n").append("selectionModel.getSelectionStart()").append(selectionModel.getSelectionStart());
        sb.append("\r\n").append("selectionModel.getSelectionEnd()").append(selectionModel.getSelectionEnd());

        sb.append("\r\n").append("光标开始行数").append(selectionModel.getSelectionStartPosition().getLine());
        // 得到当前文件路径
        VirtualFile vf = event.getData(PlatformDataKeys.VIRTUAL_FILE);
        sb.append("\r\n").append("vf.getPath()").append(vf.getPath());
        sb.append("\r\n").append("project.getProjectFile().getPath()").append(project.getProjectFile().getPath());
        sb.append("\r\n").append("project.getBasePath()").append(project.getBasePath());

        Document originDocument = FileDocumentManager.getInstance().getDocument(vf);

        for (int i = selectionModel.getSelectionEndPosition().getLine(); i >= selectionModel.getSelectionStartPosition().getLine(); i--) {
            int b = originDocument.getLineStartOffset(i);
            int e = originDocument.getLineEndOffset(i) + 1;
            String loadString = originDocument.getText(TextRange.create(b, e));
            sb.append("\r\n").append("loadString").append(i + ":" + loadString.trim());

            // 得到目标路径
            String moveModulePath = getMoveModulePath(project.getBasePath(), vf.getPath(), module);
            VirtualFile vf2 = LocalFileSystem.getInstance().findFileByPath(moveModulePath);
            Document document = FileDocumentManager.getInstance().getDocument(vf2);
            // 加到最后
            int wb = document.getLineStartOffset(document.getLineCount() - 1);
            CommandProcessor.getInstance().executeCommand(project, new Runnable() {
                @Override
                public void run() {
                    document.insertString(wb, loadString);
                    originDocument.deleteString(b, e);
                }
            }, "快速移动字符串到其他module", null);
        }

//
//        Messages.showMessageDialog(project, sb.toString(), "Information", Messages.getInformationIcon());
    }


    String module;

    @Override
    public void actionPerformed(AnActionEvent event) {
        // TODO: insert action logic here

        Project project = event.getData(PlatformDataKeys.PROJECT);
        com.intellij.openapi.editor.Editor editor = event.getData(PlatformDataKeys.EDITOR);

        module = Messages.showInputDialog(project, "dest module name?", "Input module name", Messages.getQuestionIcon(), module, null);

        SelectionModel selectionModel = editor.getSelectionModel();
        // 得到当前文件路径
        VirtualFile vf = event.getData(PlatformDataKeys.VIRTUAL_FILE);

        List<String> keys = move(project,vf.getPath(),selectionModel.getSelectionStartPosition().getLine(),selectionModel.getSelectionEndPosition().getLine());

        if (vf.getPath().contains("/values/")) {
            // 如果values时，把其余文件夹的也给拷贝了
            String dirs[] = new String[]{
                    "/values-bo-rCN/",
                    "/values-zh-rCN/",
                    "/values-zh-rTW/"
            };
            for(String dir:dirs){
                String moveModulePath = vf.getPath().replace("/values/",dir);
                for(String key:keys){
                    if(key!=null){
                        int line = findLineByKey(moveModulePath,key);
                        if(line!=-1){
                            move(project,moveModulePath,line,line);
                        }
                    }
                }
            }
        }
//
//        Messages.showMessageDialog(project, sb.toString(), "Information", Messages.getInformationIcon());
    }

    private int findLineByKey(String moveModulePath, String key) {
        VirtualFile vf = LocalFileSystem.getInstance().findFileByPath(moveModulePath);;
        if(vf==null){
            return -1;
        }
        Document document1 = FileDocumentManager.getInstance().getDocument(vf);
        for(int i =0;i<document1.getLineCount();i++){
            int b = document1.getLineStartOffset(i);
            int e = document1.getLineEndOffset(i);
            String con = document1.getText(TextRange.create(b,e));
            if(con.contains(key)){
                return i;
            }
        }
        return -1;
    }

    List<String> move(Project project,String originPath,int lineBegin,int LineEnd){

        // 得到当前文件路径
        VirtualFile vf = LocalFileSystem.getInstance().findFileByPath(originPath);;
        Document document1 = FileDocumentManager.getInstance().getDocument(vf);

        // 得到目标路径
        String moveModulePath = getMoveModulePath(project.getBasePath(), vf.getPath(), module);
        VirtualFile vf2 = LocalFileSystem.getInstance().findFileByPath(moveModulePath);
        if (vf2 == null) {
            Messages.showMessageDialog(project, moveModulePath + "不存在", "Information", Messages.getInformationIcon());
            return new ArrayList<>();
        }
        Document document2 = FileDocumentManager.getInstance().getDocument(vf2);

        List<String> keys = new ArrayList<>();

        for (int i = lineBegin; i <= LineEnd; i++) {
            String key = move(project, document1, document2, i, document2.getLineCount() - 1);
            keys.add(key);
        }

        for (int i = LineEnd; i >= lineBegin; i--) {
            delete(project, document1, i);
        }
        return keys;
    }

    public String move(Project project, Document document1, Document document2, int line1, int line2) {
        int b = document1.getLineStartOffset(line1);
        int e = document1.getLineEndOffset(line1) + 1;
        String loadString = document1.getText(TextRange.create(b, e));
        // 得到目标路径
        // 加到最后
        int wb = document2.getLineStartOffset(line2);
        CommandProcessor.getInstance().executeCommand(project, new Runnable() {
            @Override
            public void run() {
                document2.insertString(wb, loadString);
            }
        }, "快速移动字符串到其他module", null);
        return getResourceKey(loadString);
    }

    private String getResourceKey(String loadString) {
        int index = loadString.indexOf("name=");
        if (index == -1) {
            return null;
        }
        loadString = loadString.substring(index);
        loadString = loadString.substring(0, loadString.indexOf(">"));
        return loadString;
    }

    public void delete(Project project, Document document1, int line1) {
        int b = document1.getLineStartOffset(line1);
        int e = document1.getLineEndOffset(line1) + 1;
        CommandProcessor.getInstance().executeCommand(project, new Runnable() {
            @Override
            public void run() {
                document1.deleteString(b, e);
            }
        }, "快速移动字符串到其他module", null);
    }

    String getMoveModulePath(String basepath, String originPath, String moduleName) {
        originPath = originPath.substring(basepath.length() + 1);
        originPath = originPath.substring(originPath.indexOf("/"));
        return basepath + "/" + moduleName + originPath;
    }
}
