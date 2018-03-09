package com.csm.plugin.shrink;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.xml.XmlAttributeImpl;
import com.intellij.psi.impl.source.xml.XmlTokenImpl;
import org.apache.http.util.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public void delete(File rootFile, HashSet<String> javaSet, HashSet<String> layoutSet, HashSet<String> drawableSet) {
        delete(rootFile, new IFileFilter() {
            Pattern pattern = Pattern.compile(".*/(\\w+)\\.java");
            Pattern pattern1 = Pattern.compile(".*/(\\w+)\\.(xml|png|webp|9\\.png|jpg|jepg|gif)");
            Pattern pattern2 = Pattern.compile(".*res/layout/(\\w+)\\.xml");

            @Override
            public boolean accept(String path) {
                if (path.endsWith(".java")) {
                    Matcher matcher = pattern.matcher(path);
                    if (matcher.find()) {
                        matcher.reset();
                        while (matcher.find()) {
                            String v1 = matcher.group(1);
                            // System.out.println("layout name:" + v1);
                            if (v1 != null) {
                                boolean has = javaSet.contains(v1);
                                if (has) {
                                    return false;
                                } else {
                                    return true;
                                }
                            }
                        }
                    }
                    return true;
                }

                // return true 就是该被删除的文件
                if (path.contains("/res/drawable")) {
                    // System.out.println(path + " is layout");
                    Matcher matcher = pattern1.matcher(path);
                    if (matcher.find()) {
                        matcher.reset();
                        while (matcher.find()) {
                            String v1 = matcher.group(1);
                            // System.out.println("layout name:" + v1);
                            if (v1 != null) {
                                boolean has = drawableSet.contains(v1);
                                if (has) {
                                    return false;
                                } else {
                                    return true;
                                }
                            }
                        }
                    }
                    return true;
                }

                if (path.contains("res/layout")) {
                    // System.out.println(path + " is layout");
                    Matcher matcher = pattern2.matcher(path);
                    if (matcher.find()) {
                        matcher.reset();
                        while (matcher.find()) {
                            String v1 = matcher.group(1);
                            // System.out.println("layout name:" + v1);
                            if (v1 != null) {
                                boolean has = layoutSet.contains(v1);
                                if (has) {
                                    return false;
                                } else {
                                    return true;
                                }
                            }
                        }
                    }
                    return true;
                }
                return false;
            }


        });
    }

    private static class UtilsHolder {
        private static final Utils INSTANCE = new Utils();
    }

    private Utils() {

    }

    public static final Utils getInstance() {
        return UtilsHolder.INSTANCE;
    }

    Pattern javaFilePattern = Pattern.compile(".*/(\\w+)\\.java");


    public String getJavaFileNameByPath(String path) {
        if (path.endsWith(".java")) {
//            System.out.println("path:" + path);
            Matcher matcher = javaFilePattern.matcher(path);
            if (matcher.find()) {
                matcher.reset();
                while (matcher.find()) {
                    String v1 = matcher.group(1);
//                    System.out.println("java name:" + v1);
                    return v1;
                }
            }
        }
        return null;
    }


    Pattern layoutFilePattern = Pattern.compile(".*res/layout/(\\w+)\\.xml");


    public String getLayoutFileNameByPath(String path) {
        if (path.contains("res/layout")) {
            // System.out.println(path + " is layout");
            Matcher matcher = layoutFilePattern.matcher(path);
            if (matcher.find()) {
                matcher.reset();
                while (matcher.find()) {
                    String v1 = matcher.group(1);
                    return v1;
                }
            }
        }
        return null;
    }

    Pattern drawableFilePattern = Pattern.compile(".*/(\\w+)\\.xml");

    public String getDrawableFileNameByPath(String path) {
        if (path.contains("/res/drawable")) {
            // System.out.println(path + " is layout");
            Matcher matcher = drawableFilePattern.matcher(path);
            if (matcher.find()) {
                matcher.reset();
                while (matcher.find()) {
                    String v1 = matcher.group(1);
                    return v1;
                }
            }
        }
        return null;
    }

    static Pattern vilidJavaFileNamePattern = Pattern.compile("^[A-Z]\\w*$");

    void tryAdd(HashSet<String> javaSet, String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return;
        }
        Matcher matcher = vilidJavaFileNamePattern.matcher(fileName);
        if (matcher.find()) {
            // 合法的文件名
            if (!javaSet.contains(fileName)) {
                System.out.println("add: " + fileName);
                javaSet.add(fileName);
            }
        }
    }

    public void getJavaByAndroidManifest(File rootFile, HashSet<String> javaMap) {
        /**
         * 先将androidmanifest中java加进去
         */
        ArrayList<Processor> processorList = new ArrayList<>();
        processorList.add(new Processor() {
            // .style
            Pattern pattern = Pattern.compile(".*name=\"([\\w|\\.]+)\"");

            @Override
            public String process(String line) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    matcher.reset();
                    while (matcher.find()) {
                        String v1 = matcher.group(1);
                        if (v1 != null) {
                            String s[] = v1.split("\\.");
                            // 为了兼容 EventClass这种内部静态类
                            for (String fileName : s) {
                                tryAdd(javaMap, fileName);
                            }
//                            System.out.println("java :" + v1);
                        }
                    }
                }
                return line;
            }
        });
        digui(rootFile, new IFileFilter() {
            @Override
            public boolean accept(String path) {
                if (path.endsWith("AndroidManifest.xml")) {
                    return true;
                }
                return false;
            }
        }, processorList);
    }


    public void getJavaByJava(Project project, File rootFile, HashSet<String> javaMap, HashSet<String> pathSet) {
        int size = 0;

        do {
            size = javaMap.size();
            System.out.println("size:" + size);
            digui(project, rootFile, new IFileFilter() {
                @Override
                public boolean accept(String path) {
                    String javaFileName = getJavaFileNameByPath(path);
                    if (!TextUtils.isEmpty(javaFileName)) {
                        boolean has = javaMap.contains(javaFileName);
                        if (has && !pathSet.contains(path)) {
                            //在map中但没有解析过，可以解析
                            pathSet.add(path);
                            return true;
                        } else {
                            return false;
                        }
                    }
                    return false;
                }
            }, javaMap);
        } while (javaMap.size() != size);
    }

    public void getLayoutAndDrawableSetByJavaSet(File rootFile, HashSet<String> javaSet, HashSet<String> javaPathSet
            , HashSet<String> layoutSet, HashSet<String> drawableSet) {


        ArrayList<Processor> processors = new ArrayList<>();
        if (layoutSet != null) {
            processors.add(new Processor() {
                // .java
                // 案例1
                // int resourceId = isForRelease ? R.layout.release_file_picker_item
                // : R.layout.file_picker_item; // 不能贪婪匹配，一行多次匹配
                // 案例2
                // return R.layout.release_file_picker_item ;

                Pattern pattern = Pattern.compile(".*?layout.(\\w+)(,|\\)|;| |)");

                @Override
                public String process(String line) {
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        matcher.reset();
                        while (matcher.find()) {
                            String v1 = matcher.group(1);
                            if (v1 != null) {
                                layoutSet.add(v1);
//                                System.out.println("layout:" + v1);
                            }
                        }
                    }
                    return line;
                }
            });
        }

        if (drawableSet != null) {
            processors.add(new Processor() {
                // .java
                /**
                 * 案例1
                 *     private static final int[] mCategoryImages=new int[]{
                 R.drawable.message_chat_file_icon_memory,
                 R.drawable.message_chat_file_icon_sd
                 };

                 */
                Pattern pattern = Pattern.compile(".*?drawable.(\\w+)(,|\\)|;|)");

                @Override
                public String process(String line) {
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        matcher.reset();
                        while (matcher.find()) {
                            String v1 = matcher.group(1);
                            if (v1 != null) {
                                drawableSet.add(v1);
//                                System.out.println("drawable:" + v1);
                            }
                        }
                    }
                    return line;
                }
            });
        }
        digui(rootFile, new IFileFilter() {

            @Override
            public boolean accept(String path) {
                String javaName = Utils.getInstance().getJavaFileNameByPath(path);
                if (!TextUtils.isEmpty(javaName)) {
                    boolean has = javaSet.contains(javaName);
                    if (has && !javaPathSet.contains(path)) {
                        javaPathSet.add(path);
                        return true;
                    } else {
                        return false;
                    }
                }
                return false;
            }
        }, processors);
    }


    public void getLayoutAndDrawableAndJavaSetByLayoutSet(File rootFile, HashSet<String> layoutSet, HashSet<String> layoutPathSet
            , HashSet<String> drawableSet, HashSet<String> javaSet) {
        ArrayList<Processor> processorList = new ArrayList<>();
        processorList.add(new Processor() {
            // .layout
            Pattern pattern = Pattern.compile(".*=\\s*\"@layout/(\\w+)\"");

            @Override
            public String process(String line) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    matcher.reset();
                    while (matcher.find()) {
                        String v1 = matcher.group(1);
                        if (v1 != null) {
                            layoutSet.add(v1);
//                            System.out.println("layout:" + v1);
                        }
                    }
                }
                return line;
            }
        });
        processorList.add(new Processor() {
            // .layout
            Pattern pattern = Pattern.compile(".*=\\s*\"@drawable/(\\w+)\"");

            @Override
            public String process(String line) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    matcher.reset();
                    while (matcher.find()) {
                        String v1 = matcher.group(1);
                        if (v1 != null) {
                            drawableSet.add(v1);
//                            System.out.println("drawable:" + v1);
                        }
                    }
                }
                return line;
            }
        });
        processorList.add(new Processor() {

            //            <com.xiaomi.channel.view.MaxHeightScrollView
            // .layout
            Pattern pattern = Pattern.compile(".*<com\\.([\\w|\\.]+)");

            @Override
            public String process(String line) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    matcher.reset();
                    while (matcher.find()) {
                        String v1 = matcher.group(1);
                        if (v1 != null) {
                            String s[] = v1.split("\\.");
                            // 为了兼容 EventClass这种内部静态类
                            for (String fileName : s) {
                                tryAdd(javaSet, fileName);
                            }
                            System.out.println("java:" + v1);
                        }
                    }
                }
                return line;
            }
        });
        int size = 0;
        do {
            size = layoutSet.size();
//            System.out.println("layoutSet.size:" + layoutSet.size());
            // 得到以上layout中drawable,因为layout中可能含有layout，粗鲁些递归直到尺寸不变
            digui(rootFile, new IFileFilter() {

                @Override
                public boolean accept(String path) {
                    String file = getLayoutFileNameByPath(path);
                    if (!TextUtils.isEmpty(file)) {
                        boolean has = layoutSet.contains(file);
                        if (has && !layoutPathSet.contains(path)) {
                            layoutPathSet.add(path);
                            return true;
                        } else {
                            return false;
                        }
                    }
                    return false;
                }
            }, processorList);
        } while (size != layoutSet.size());
    }


    public void getDrawableByStyle(File rootFile, HashSet<String> drawableSet) {
        ArrayList<Processor> processors = new ArrayList<>();
        processors.add(new Processor() {
            // .style
            Pattern pattern = Pattern.compile(".*@drawable/(\\w+)(<?)");

            @Override
            public String process(String line) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    matcher.reset();
                    while (matcher.find()) {
                        String v1 = matcher.group(1);
                        if (v1 != null) {
                            drawableSet.add(v1);
//                            System.out.println("drawable:" + v1);
                        }
                    }
                }
                return line;
            }
        });
        digui(rootFile, new IFileFilter() {

            @Override
            public boolean accept(String path) {
                if (path.endsWith("/styles.xml")) {
                    return true;
                }
                return false;
            }
        }, processors);
    }


    public void getDrawableByDrawable(File rootFile, HashSet<String> drawableSet, HashSet<String> drawablePathSet) {
        ArrayList<Processor> processors = new ArrayList<>();
        processors.add(new Processor() {
            Pattern pattern = Pattern.compile(".*=\\s*\"@drawable/(\\w+)(<?)");

            @Override
            public String process(String line) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    matcher.reset();
                    while (matcher.find()) {
                        String v1 = matcher.group(1);
                        if (v1 != null) {
                            drawableSet.add(v1);
//                            System.out.println("drawable:" + v1);
                        }
                    }
                }
                return line;
            }
        });
        digui(rootFile, new IFileFilter() {


            @Override
            public boolean accept(String path) {

                String file = getDrawableFileNameByPath(path);
                if (!TextUtils.isEmpty(file)) {
                    boolean has = drawableSet.contains(file);
                    if (has && !drawablePathSet.contains(path)) {
                        drawablePathSet.add(path);
                        return true;
                    } else {
                        return false;
                    }
                }
                return false;
            }
        }, processors);
    }

    void digui(File root, IFileFilter f, List<Processor> processors) {

        for (File file : root.listFiles()) {
            String path = file.getPath();
            // System.out.println("digui:" + path);
            if (file.isFile()) {
                if (f.accept(path)) {
                    // 是java文件
                    read(path, processors);
                }
            } else {
                if (!path.contains("/build/")) {
                    digui(file, f, processors);
                }
            }
        }
    }

    private void read(String filePath, List<Processor> processors) {
        File file = new File(filePath);
        try {
            Scanner scan = new Scanner(file);
            StringBuilder sb = new StringBuilder();
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                for (Processor p : processors) {
                    p.process(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void digui(Project project, File root, IFileFilter f, HashSet<String> javaSet) {

        for (File file : root.listFiles()) {
            String path = file.getPath();
            // System.out.println("digui:" + path);
            if (file.isFile()) {
                if (f.accept(path)) {
                    // 是java文件
//                    System.out.println("path:" + path);
                    /**
                     * 3. PSI 与 VFS的联系
                     // VirtualFile 转 PsiFile
                     PsiManager.getInstance(project).findFile(virtualFile);

                     // PsiFile 转 VirtualFile
                     VirtualFile virtualFile = psiFile.getVirtualFile();
                     */
                    VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(file);
                    PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);

                    jiexi(psiFile.getChildren(), javaSet, 0);
//                    System.out.println("over:" + path);
                }
            } else {
                if (!path.contains("/build/")) {
                    digui(project, file, f, javaSet);
                }
            }
        }
    }


    /**
     * 利用递归将这个文件解析到原子粒度
     *
     * @param childs
     * @param deep
     */
    void jiexi(PsiElement[] childs, HashSet<String> javaSet, int deep) {
        for (PsiElement psiElement : childs) {
            if (false) {
                for (int i = 0; i < deep; i++) {
                    System.out.print(" ");
                }
                System.out.println(psiElement);
            }
            if (psiElement instanceof PsiImportStatement) {
                String importName = ((PsiImportStatement) psiElement).getQualifiedName();
                String s[] = importName.split("\\.");
                // 为了兼容 EventClass这种内部静态类
                for (String fileName : s) {
                    tryAdd(javaSet, fileName);
                }

            }
            if (psiElement instanceof PsiTypeElement) {
                // 能得到 加上包名后的路径
                String importName = ((PsiTypeElement) psiElement).getType().getCanonicalText();
                String s[] = importName.split("\\.");
                // 为了兼容 EventClass这种内部静态类
                for (String fileName : s) {
                    tryAdd(javaSet, fileName);
                }
            }
            if (psiElement instanceof PsiJavaCodeReferenceElement) {
                // 能得到 加上包名后的路径
                String importName = ((PsiJavaCodeReferenceElement) psiElement).getQualifiedName();
                String s[] = importName.split("\\.");
                // 为了兼容 EventClass这种内部静态类
                for (String fileName : s) {
                    tryAdd(javaSet, fileName);
                }
            }
            if (psiElement instanceof XmlTokenImpl) {
                XmlTokenImpl xmlToken = (XmlTokenImpl) psiElement;
                System.out.println("text:" + xmlToken.getText());
            }
            if (psiElement instanceof XmlAttributeImpl) {
                XmlAttributeImpl xmlAttribute = (XmlAttributeImpl) psiElement;
                System.out.println("text:" + xmlAttribute.getText());
            }
            jiexi(psiElement.getChildren(), javaSet, deep + 1);
        }
    }


    void delete(File root, IFileFilter f) {

        for (File file : root.listFiles()) {
            String path = file.getPath();
            // System.out.println("digui:" + path);
            if (file.isFile()) {
                if (f.accept(path)) {
                    // 是java文件
                    System.out.println("删除" + path);
                    file.delete();
                }
            } else {
                if (!path.contains("/build/")) {
                    delete(file, f);
                }
            }
        }
    }
}
