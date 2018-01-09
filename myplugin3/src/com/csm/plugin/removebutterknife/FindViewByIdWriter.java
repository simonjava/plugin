package com.csm.plugin.removebutterknife;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;

import java.util.List;

/**
 * Created by xiaolei on 2016/6/17.
 */
public class FindViewByIdWriter extends WriteCommandAction.Simple {
    PsiClass mClass;
    private PsiElementFactory mFactory;
    List<String> code;
    Project mProject;

    protected FindViewByIdWriter(Project project, PsiFile file, PsiClass psiClass, List<String> code, PsiElementFactory mFactory) {
        super(project, file);
        mClass = psiClass;
        this.code = code;
        this.mFactory = mFactory;
        mProject = project;
    }

    @Override
    protected void run() {
        try {
            generateInjects(mProject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void generateInjects(Project mProject) {
        {
            PsiMethod[] methods = mClass.getMethods();
            String viewName = "this.";
            if(mClass.getName().contains("Fragment")){
                viewName = "mRootView.";
            }
            for (PsiMethod onCreate : methods) {
                if (onCreate.getBody() != null) {
                    for (PsiStatement statement : onCreate.getBody().getStatements()) {
                        // Search for setContentView()
                        if (statement.getFirstChild() instanceof PsiMethodCallExpression) {
                            PsiReferenceExpression methodExpression
                                    = ((PsiMethodCallExpression) statement.getFirstChild())
                                    .getMethodExpression();
                            // Insert ButterKnife.inject()/ButterKnife.bind() after setContentView()
                            if (methodExpression.getText().equals("ButterKnife.bind")) {
                                for (int i = code.size() - 1; i >= 0; i--) {
                                    StringBuffer buffer = new StringBuffer(code.get(i));
                                    int num = buffer.indexOf(")");
                                    buffer.insert(num + 1, viewName);
                                    onCreate.getBody().addAfter(mFactory.createStatementFromText(
                                            buffer.toString(), mClass), statement);
                                }
                                return;
                            }
                        }
                    }
                }
            }
        }
        {
            // Check for Activity class
            PsiMethod[] onCreateMethods = mClass.findMethodsByName("onCreate", false);
            if (onCreateMethods.length > 0) {
                PsiMethod onCreate = onCreateMethods[0];
                for (PsiStatement statement : onCreate.getBody().getStatements()) {
                    // Search for setContentView()
                    if (statement.getFirstChild() instanceof PsiMethodCallExpression) {
                        PsiReferenceExpression methodExpression
                                = ((PsiMethodCallExpression) statement.getFirstChild())
                                .getMethodExpression();
                        // Insert ButterKnife.inject()/ButterKnife.bind() after setContentView()
                        if (methodExpression.getText().equals("setContentView")) {
                            for (int i = code.size() - 1; i >= 0; i--) {
                                onCreate.getBody().addAfter(mFactory.createStatementFromText(
                                        code.get(i) + "\n", mClass), statement);
                            }
                            return;
                        }
                    }
                }

            }
        }
        {
            // Check for Fragment class
            PsiMethod[] onCreateViewMethods = mClass.findMethodsByName("onCreateView", false);
            if (onCreateViewMethods.length > 0) {
                PsiMethod onCreateView = onCreateViewMethods[0];
                for (PsiStatement statement : onCreateView.getBody().getStatements()) {
                    String returnValue = statement.getText();
                    if (returnValue.contains("R.layout")) {
                        String viewName = returnValue.trim().split(" ")[1] + ".";
                        for (int i = 0; i < code.size(); i++) {
                            StringBuffer buffer = new StringBuffer(code.get(i));
                            int num = buffer.indexOf(")");
                            buffer.insert(num + 1, viewName);
                            try {
                                statement.addAfter(mFactory.createStatementFromText(
                                        buffer.toString(), mClass), statement);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                        return;
                    }
                }
            }
        }
        {
            PsiMethod[] bindViewViewMethods = mClass.findMethodsByName("bindView", false);
            if (bindViewViewMethods.length > 0) {
                PsiMethod bindView = bindViewViewMethods[0];
                String viewName = "mRootView.";
                for (int i = 0; i < code.size(); i++) {
                    StringBuffer buffer = new StringBuffer(code.get(i));
                    int num = buffer.indexOf(")");
                    buffer.insert(num + 1, viewName);
                    if (bindView.getBody() != null) {
                        PsiElement first = bindView.getBody().getFirstBodyElement();
                        if (first == null) {
                            try {
                                bindView.getBody().add(mFactory.createStatementFromText(
                                        buffer.toString(), mClass));
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        } else {
                            try {
                                bindView.getBody().addBefore(mFactory.createStatementFromText(
                                        buffer.toString(), mClass), first);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                }
                return;
            }
        }
        {
            // 父亲类的也能拿到
//        PsiMethod[] methods = mClass.getAllMethods();
            PsiMethod[] methods = mClass.getMethods();
            if (methods.length > 0) {
                PsiMethod method = methods[0];
                String viewName = "mRootView.";
                for (int i = 0; i < code.size(); i++) {
                    StringBuffer buffer = new StringBuffer(code.get(i));
                    int num = buffer.indexOf(")");
                    buffer.insert(num + 1, viewName);
                    try {
                        method.getBody().add(mFactory.createStatementFromText(
                                buffer.toString(), mClass));
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
                return;
            }
        }
    }
}
