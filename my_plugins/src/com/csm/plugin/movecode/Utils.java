package com.csm.plugin.movecode;

import com.csm.plugin.shrink.IFileFilter;
import com.csm.plugin.shrink.Processor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiImportStatement;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiTypeElement;
import com.intellij.psi.impl.source.xml.XmlAttributeImpl;
import com.intellij.psi.impl.source.xml.XmlTokenImpl;

import org.apache.http.util.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
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


    Pattern layoutFilePattern = Pattern.compile(".*/layout/(\\w+)\\.xml");


    public String getLayoutFileNameByPath(String path) {
        if (path.contains("/layout")) {
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

    Pattern drawableFilePattern = Pattern.compile(".*/(\\w+)\\.[xml|png|9.png|jpg|jepg|gif|webp]");

    public String getDrawableFileNameByPath(String path) {
        if (path.contains("/drawable")) {
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

    public String getColorFileNameByPath(String path) {
        if (path.contains("/color")) {
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

    public void getLayoutAndDrawableSetByJavaSet(File rootFile, HashSet<String> javaPathSet
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
                boolean has = javaPathSet.contains(path);
                if (has) {
                    return true;
                }
                return false;
            }
        }, processors);
    }

    public void getStringByJavaSet(File rootFile, HashSet<String> javaPathSet
            , HashSet<String> stringSet) {

        ArrayList<Processor> processors = new ArrayList<>();
        if (stringSet != null) {
            processors.add(new Processor() {
                // .java
                // 案例1
                // int resourceId = isForRelease ? R.layout.release_file_picker_item
                // : R.layout.file_picker_item; // 不能贪婪匹配，一行多次匹配
                // 案例2
                // return R.layout.release_file_picker_item ;

                Pattern pattern = Pattern.compile(".*?(string|plurals).(\\w+)(,|\\)|;| |)");

                @Override
                public String process(String line) {
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        matcher.reset();
                        while (matcher.find()) {
                            String v1 = matcher.group(2);
                            if (v1 != null) {
                                stringSet.add(v1);
//                                System.out.println("layout:" + v1);
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
                boolean has = javaPathSet.contains(path);
                if (has) {
                    return true;
                }
                return false;
            }
        }, processors);
    }

    public void getArrayByJavaSet(File rootFile, HashSet<String> javaPathSet
            , HashSet<String> arraySet) {

        ArrayList<Processor> processors = new ArrayList<>();
        if (arraySet != null) {
            processors.add(new Processor() {
                // .java
                // 案例1
                // int resourceId = isForRelease ? R.layout.release_file_picker_item
                // : R.layout.file_picker_item; // 不能贪婪匹配，一行多次匹配
                // 案例2
                // return R.layout.release_file_picker_item ;

                Pattern pattern = Pattern.compile(".*?(array).(\\w+)(,|\\)|;| |)");

                @Override
                public String process(String line) {
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        matcher.reset();
                        while (matcher.find()) {
                            String v1 = matcher.group(2);
                            if (v1 != null) {
                                arraySet.add(v1);
//                                System.out.println("layout:" + v1);
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
                boolean has = javaPathSet.contains(path);
                if (has) {
                    return true;
                }
                return false;
            }
        }, processors);
    }

    public void getStringByLayoutSet(File rootFile, HashSet<String> layoutSet, HashSet<String> stringSet) {
        ArrayList<Processor> processorList = new ArrayList<>();
        processorList.add(new Processor() {
            // .layout
            Pattern pattern = Pattern.compile(".*=\\s*\"@(string|plurals)/(\\w+)\"");

            @Override
            public String process(String line) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    matcher.reset();
                    while (matcher.find()) {
                        String v1 = matcher.group(2);
                        if (v1 != null) {
                            stringSet.add(v1);
//                            System.out.println("layout:" + v1);
                        }
                    }
                }
                return line;
            }
        });
//            System.out.println("layoutSet.size:" + layoutSet.size());
        // 得到以上layout中drawable,因为layout中可能含有layout，粗鲁些递归直到尺寸不变
        digui(rootFile, new IFileFilter() {

            @Override
            public boolean accept(String path) {
                String file = getLayoutFileNameByPath(path);
                if (!TextUtils.isEmpty(file)) {
                    boolean has = layoutSet.contains(file);
                    if (has) {
                        return true;
                    } else {
                        return false;
                    }
                }
                return false;
            }
        }, processorList);
    }

    public void getColorByJavaSet(File rootFile, HashSet<String> javaPathSet
            , HashSet<String> colorSet) {

        ArrayList<Processor> processors = new ArrayList<>();
        if (colorSet != null) {
            processors.add(new Processor() {
                // .java
                // 案例1
                // int resourceId = isForRelease ? R.layout.release_file_picker_item
                // : R.layout.file_picker_item; // 不能贪婪匹配，一行多次匹配
                // 案例2
                // return R.layout.release_file_picker_item ;

                Pattern pattern = Pattern.compile(".*?color.(\\w+)(,|\\)|;| |)");

                @Override
                public String process(String line) {
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        matcher.reset();
                        while (matcher.find()) {
                            String v1 = matcher.group(1);
                            if (v1 != null) {
                                colorSet.add(v1);
//                                System.out.println("layout:" + v1);
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
                boolean has = javaPathSet.contains(path);
                if (has) {
                    return true;
                }
                return false;
            }
        }, processors);
    }

    public void getColorByLayoutSet(File rootFile, HashSet<String> layoutSet, HashSet<String> colorSet) {
        ArrayList<Processor> processorList = new ArrayList<>();
        processorList.add(new Processor() {
            // .layout
            Pattern pattern = Pattern.compile(".*=\\s*\"@color/(\\w+)\"");

            @Override
            public String process(String line) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    matcher.reset();
                    while (matcher.find()) {
                        String v1 = matcher.group(1);
                        if (v1 != null) {
                            colorSet.add(v1);
//                            System.out.println("layout:" + v1);
                        }
                    }
                }
                return line;
            }
        });
//            System.out.println("layoutSet.size:" + layoutSet.size());
        // 得到以上layout中drawable,因为layout中可能含有layout，粗鲁些递归直到尺寸不变
        digui(rootFile, new IFileFilter() {

            @Override
            public boolean accept(String path) {
                String file = getLayoutFileNameByPath(path);
                if (!TextUtils.isEmpty(file)) {
                    boolean has = layoutSet.contains(file);
                    if (has) {
                        return true;
                    } else {
                        return false;
                    }
                }
                return false;
            }
        }, processorList);
    }

    public void getColorByDrawableSet(File rootFile, HashSet<String> drawableSet, HashSet<String> colorSet) {
        ArrayList<Processor> processorList = new ArrayList<>();
        processorList.add(new Processor() {
            // .layout
            Pattern pattern = Pattern.compile(".*=\\s*\"@color/(\\w+)\"");

            @Override
            public String process(String line) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    matcher.reset();
                    while (matcher.find()) {
                        String v1 = matcher.group(1);
                        if (v1 != null) {
                            colorSet.add(v1);
//                            System.out.println("layout:" + v1);
                        }
                    }
                }
                return line;
            }
        });
//            System.out.println("layoutSet.size:" + layoutSet.size());
        // 得到以上layout中drawable,因为layout中可能含有layout，粗鲁些递归直到尺寸不变
        digui(rootFile, new IFileFilter() {

            @Override
            public boolean accept(String path) {
                if (path.endsWith(".xml")) {
                    String file = getDrawableFileNameByPath(path);
                    if (!TextUtils.isEmpty(file)) {
                        boolean has = drawableSet.contains(file);
                        if (has) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
                return false;
            }
        }, processorList);
    }

    public void getColorByColorXmlSet(File rootFile, HashSet<String> colorXmlSet, HashSet<String> colorSet) {
        ArrayList<Processor> processorList = new ArrayList<>();
        processorList.add(new Processor() {
            // .layout
            Pattern pattern = Pattern.compile(".*=\\s*\"@color/(\\w+)\"");

            @Override
            public String process(String line) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    matcher.reset();
                    while (matcher.find()) {
                        String v1 = matcher.group(1);
                        if (v1 != null) {
                            colorSet.add(v1);
//                            System.out.println("layout:" + v1);
                        }
                    }
                }
                return line;
            }
        });
//            System.out.println("layoutSet.size:" + layoutSet.size());
        // 得到以上layout中drawable,因为layout中可能含有layout，粗鲁些递归直到尺寸不变
        digui(rootFile, new IFileFilter() {

            @Override
            public boolean accept(String path) {
                if (path.endsWith(".xml")) {
                    String file = getColorFileNameByPath(path);
                    if (!TextUtils.isEmpty(file)) {
                        boolean has = colorXmlSet.contains(file);
                        if (has) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
                return false;
            }
        }, processorList);
    }



    public void getDimenByJavaSet(File rootFile, HashSet<String> javaPathSet
            , HashSet<String> colorSet) {

        ArrayList<Processor> processors = new ArrayList<>();
        if (colorSet != null) {
            processors.add(new Processor() {
                // .java
                // 案例1
                // int resourceId = isForRelease ? R.layout.release_file_picker_item
                // : R.layout.file_picker_item; // 不能贪婪匹配，一行多次匹配
                // 案例2
                // return R.layout.release_file_picker_item ;

                Pattern pattern = Pattern.compile(".*?dimen.(\\w+)(,|\\)|;| |)");

                @Override
                public String process(String line) {
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        matcher.reset();
                        while (matcher.find()) {
                            String v1 = matcher.group(1);
                            if (v1 != null) {
                                colorSet.add(v1);
//                                System.out.println("layout:" + v1);
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
                boolean has = javaPathSet.contains(path);
                if (has) {
                    return true;
                }
                return false;
            }
        }, processors);
    }

    public void getDimenByLayoutSet(File rootFile, HashSet<String> layoutSet, HashSet<String> colorSet) {
        ArrayList<Processor> processorList = new ArrayList<>();
        processorList.add(new Processor() {
            // .layout
            Pattern pattern = Pattern.compile(".*=\\s*\"@dimen/(\\w+)\"");

            @Override
            public String process(String line) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    matcher.reset();
                    while (matcher.find()) {
                        String v1 = matcher.group(1);
                        if (v1 != null) {
                            colorSet.add(v1);
//                            System.out.println("layout:" + v1);
                        }
                    }
                }
                return line;
            }
        });
//            System.out.println("layoutSet.size:" + layoutSet.size());
        // 得到以上layout中drawable,因为layout中可能含有layout，粗鲁些递归直到尺寸不变
        digui(rootFile, new IFileFilter() {

            @Override
            public boolean accept(String path) {
                String file = getLayoutFileNameByPath(path);
                if (!TextUtils.isEmpty(file)) {
                    boolean has = layoutSet.contains(file);
                    if (has) {
                        return true;
                    } else {
                        return false;
                    }
                }
                return false;
            }
        }, processorList);
    }

    public void getDimenByDrawableSet(File rootFile, HashSet<String> drawableSet, HashSet<String> colorSet) {
        ArrayList<Processor> processorList = new ArrayList<>();
        processorList.add(new Processor() {
            // .layout
            Pattern pattern = Pattern.compile(".*=\\s*\"@dimen/(\\w+)\"");

            @Override
            public String process(String line) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    matcher.reset();
                    while (matcher.find()) {
                        String v1 = matcher.group(1);
                        if (v1 != null) {
                            colorSet.add(v1);
//                            System.out.println("layout:" + v1);
                        }
                    }
                }
                return line;
            }
        });
//            System.out.println("layoutSet.size:" + layoutSet.size());
        // 得到以上layout中drawable,因为layout中可能含有layout，粗鲁些递归直到尺寸不变
        digui(rootFile, new IFileFilter() {

            @Override
            public boolean accept(String path) {
                if (path.endsWith(".xml")) {
                    String file = getDrawableFileNameByPath(path);
                    if (!TextUtils.isEmpty(file)) {
                        boolean has = drawableSet.contains(file);
                        if (has) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
                return false;
            }
        }, processorList);
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
        if (javaSet != null) {
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
        }
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

    HashMap<String, String> jiexiStringXml(String stringxmlPath) {
        HashMap<String, String> map = new HashMap<>();
        File fromFile = new File(stringxmlPath);
        if (!fromFile.exists()){
            return map;
        }
        Scanner sc = null;
        try {
            sc = new Scanner(new FileInputStream(fromFile));

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String line1 = line.trim();
                String key = null;
                StringBuilder valueSb = new StringBuilder();
                if (line1.startsWith("<string")) {
                    Pattern pattern = Pattern.compile(".*name=\"(\\w+)\"");

                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        matcher.reset();
                        while (matcher.find()) {
                            String v1 = matcher.group(1);
                            key = v1;
                        }
                    }
                    while (true) {
                        valueSb.append(line).append("\r\n");

                        if (line1.contains("</string>")) {
                            break;
                        }
                        line = sc.nextLine();
                        line1 = line.trim();
                    }
                    if (!TextUtils.isEmpty(key)) {
                        map.put(key, valueSb.toString());
                    }
                }

                if (line1.startsWith("<plurals")) {
                    Pattern pattern = Pattern.compile(".*name=\"(\\w+)\"");

                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        matcher.reset();
                        while (matcher.find()) {
                            String v1 = matcher.group(1);
                            key = v1;
                        }
                    }
                    while (true) {
                        valueSb.append(line).append("\r\n");

                        if (line1.contains("</plurals>")) {
                            break;
                        }
                        line = sc.nextLine();
                        line1 = line.trim();
                    }
                    if (!TextUtils.isEmpty(key)) {
                        map.put(key, valueSb.toString());
                    }
                }
            }
            sc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public void genStringXmlFile(String nPath, HashMap<String, String> map2) {
        File file = new File(nPath);
        if(!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>").append("\r\n");
        sb.append("<resources xmlns:xliff=\"urn:oasis:names:tc:xliff:document:1.2\">").append("\r\n");

        for (String s : map2.values()) {
            sb.append(s);
        }
        sb.append("</resources>").append("\r\n");

        FileWriter fos = null;
        try {
            fos = new FileWriter(new File(nPath));
            fos.write(sb.toString());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    HashMap<String, String> jiexiArrayXmlFile(String arrayXmlPath) {
        HashMap<String, String> map = new HashMap<>();
        File fromFile = new File(arrayXmlPath);
        if (!fromFile.exists()){
            return map;
        }
        Scanner sc = null;
        try {
            sc = new Scanner(new FileInputStream(fromFile));

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String line1 = line.trim();
                String key = null;
                StringBuilder valueSb = new StringBuilder();
                if (line1.startsWith("<string-array")) {
                    Pattern pattern = Pattern.compile(".*name=\"(\\w+)\"");

                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        matcher.reset();
                        while (matcher.find()) {
                            String v1 = matcher.group(1);
                            key = v1;
                        }
                    }
                    while (true) {
                        valueSb.append(line).append("\r\n");

                        if (line1.contains("</string-array>")) {
                            break;
                        }
                        line = sc.nextLine();
                        line1 = line.trim();
                    }
                    if (!TextUtils.isEmpty(key)) {
                        map.put(key, valueSb.toString());
                    }
                }

            }
            sc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public void genArrayXmlFile(String nPath, HashMap<String, String> map2) {
        File file = new File(nPath);
        if(!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>").append("\r\n");
        sb.append("<resources>").append("\r\n");

        for (String s : map2.values()) {
            sb.append(s);
        }
        sb.append("</resources>").append("\r\n");

        FileWriter fos = null;
        try {
            fos = new FileWriter(new File(nPath));
            fos.write(sb.toString());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    HashMap<String, String> jiexiColorXml(String colorXmlPath) {
        HashMap<String, String> map = new HashMap<>();
        File fromFile = new File(colorXmlPath);
        if (!fromFile.exists()){
            return map;
        }
        Scanner sc = null;
        try {
            sc = new Scanner(new FileInputStream(fromFile));

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String line1 = line.trim();
                String key = null;
                StringBuilder valueSb = new StringBuilder();
                if (line1.startsWith("<color")) {
                    Pattern pattern = Pattern.compile(".*name=\"(\\w+)\"");

                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        matcher.reset();
                        while (matcher.find()) {
                            String v1 = matcher.group(1);
                            key = v1;
                        }
                    }
                    while (true) {
                        //System.out.println("额:"+line);
                        valueSb.append(line).append("\r\n");

                        if (line1.contains("</color>")) {
                            break;
                        }
                        line = sc.nextLine();
                        line1 = line.trim();
                    }
                    if (!TextUtils.isEmpty(key)) {
                        map.put(key, valueSb.toString());
                    }
                }

            }
            sc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public void genColorXmlFile(String nPath, HashMap<String, String> map2) {
        File file = new File(nPath);
        if(!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>").append("\r\n");
        sb.append("<resources>").append("\r\n");

        for (String s : map2.values()) {
            sb.append(s);
        }
        sb.append("</resources>").append("\r\n");

        FileWriter fos = null;
        try {
            fos = new FileWriter(new File(nPath));
            fos.write(sb.toString());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    HashMap<String, String> jiexiDimenXml(String colorXmlPath) {
        HashMap<String, String> map = new HashMap<>();
        File fromFile = new File(colorXmlPath);
        if (!fromFile.exists()){
            return map;
        }
        Scanner sc = null;
        try {
            sc = new Scanner(new FileInputStream(fromFile));

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String line1 = line.trim();
                String key = null;
                StringBuilder valueSb = new StringBuilder();
                if (line1.startsWith("<dimen")) {
                    Pattern pattern = Pattern.compile(".*name=\"(\\w+)\"");

                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        matcher.reset();
                        while (matcher.find()) {
                            String v1 = matcher.group(1);
                            key = v1;
                        }
                    }
                    while (true) {
                        //System.out.println("额:"+line);
                        valueSb.append(line).append("\r\n");

                        if (line1.contains("</dimen>")) {
                            break;
                        }
                        line = sc.nextLine();
                        line1 = line.trim();
                    }
                    if (!TextUtils.isEmpty(key)) {
                        map.put(key, valueSb.toString());
                    }
                }

            }
            sc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public void genDimenXmlFile(String nPath, HashMap<String, String> map2) {
        File file = new File(nPath);
        if(!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>").append("\r\n");
        sb.append("<resources>").append("\r\n");

        for (String s : map2.values()) {
            sb.append(s);
        }
        sb.append("</resources>").append("\r\n");

        FileWriter fos = null;
        try {
            fos = new FileWriter(new File(nPath));
            fos.write(sb.toString());
            fos.close();
        } catch (IOException e) {
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


    void digui(Project project, File root, IFileFilter f, Processor processor) {

        for (File file : root.listFiles()) {
            String path = file.getPath();
            // System.out.println("digui:" + path);
            if (file.isFile()) {
                if (f.accept(path)) {
                    processor.process(path);
                }
            } else {
                if (!path.contains("/build/")) {
                    digui(project, file, f, processor);
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


    public static void copyFile(String oldPath, String newPath) {
        System.out.println("copy " + oldPath + " to " + newPath);
        File aFile = new File(oldPath);
        File bFile = new File(newPath);
        if (!aFile.exists()) {
            System.out.println("源文件不存在" + oldPath);
            return;
        }
        if (bFile.exists()) {
            System.out.println("目标文件已存在" + newPath);
            return;
        }
        if (!bFile.getParentFile().exists()) {
            bFile.getParentFile().mkdirs();
        }
        InputStream inStream = null;
        FileOutputStream fs = null;
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                inStream = new FileInputStream(oldPath); //读入原文件
                fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    fs.write(buffer, 0, byteread);
                }
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }
            } catch (Exception e) {
            }
            try {
                if (fs != null) {
                    fs.close();
                }
            } catch (Exception e) {
            }
        }
    }
}
