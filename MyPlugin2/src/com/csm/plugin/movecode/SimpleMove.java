package com.csm.plugin.movecode;

import com.csm.plugin.shrink.IFileFilter;
import com.csm.plugin.shrink.Processor;

import java.io.File;
import java.util.HashSet;
import java.util.Scanner;

public class SimpleMove {
    public static void main(String[] args) {
        File aRootFile = new File("/Users/chengsimin/dev/walilive/walilive");
        File bRootFile = new File("/Users/chengsimin/dev/miliao/mitalk");
        Scanner scanner = new Scanner(System.in);
        System.out.println("输入Simple Move filename");
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            if (input.length() < 5) {
                System.err.println("error input="+input);
                continue;
            }
            if (input.startsWith("del ")) {
                String name = input.split(" ")[1];
                System.out.println(name);
                Utils.getInstance().digui(null, bRootFile, new IFileFilter() {
                            @Override
                            public boolean accept(String path) {
                                if (path.contains("/src/main/java-milive/") && path.endsWith("/"+name + ".java")) {
                                    return true;
                                }
                                return false;
                            }
                        },
                        new Processor() {
                            @Override
                            public String process(String path) {
                                File file = new File(path);
                                file.delete();
                                System.out.println("删除" + path);
                                return "";
                            }
                        });
                continue;
            }
            HashSet<String> copyPath = new HashSet<>();

            Utils.getInstance().digui(null, aRootFile, new IFileFilter() {
                        @Override
                        public boolean accept(String path) {
                            if (path.contains("/src/main/") && path.endsWith("/"+input)) {
                                return true;
                            }
                            return false;
                        }
                    },
                    new Processor() {
                        @Override
                        public String process(String path) {
                            copyPath.add(path);
                            return null;
                        }
                    });

            for (String path : copyPath) {
                String bPath = MoveJavaWithLayoutAndDrawableAction.mapPath(path);
                if(path.equals(bPath)){
                    continue;
                }
                if(new File(bPath).exists()){
                    System.out.println(bPath+"已经存在，输入yes，覆盖");
                    if(scanner.next().equals("yes")){
                        new File(bPath).delete();
                    }
                }
                Utils.copyFile(path, bPath);
            }
            System.out.println(input + " over");
        }
    }
}
