package com.csm.plugin.movecode;

import com.csm.plugin.shrink.IFileFilter;
import com.csm.plugin.shrink.Processor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

import kotlin.Pair;

public class SimpleReplace {
    static class FF {
        boolean yange = false;
        String origin;
        String replace;

        public FF(boolean yange, String origin, String replace) {
            this.yange = yange;
            this.replace = replace;
            if (!yange) {
                if (!origin.endsWith(";")) {
                    origin += ";";
                }
                if (!origin.startsWith(".") && !origin.startsWith("com")) {
                    origin = "." + origin;
                }
            }
            this.origin = origin;
        }
    }

    public static void main(String[] args) {

        ArrayList<FF> pairs = new ArrayList<>();
//        {
//            FF ff = new FF(false, "UserAccountManager", "com.xiaomi.gamecenter.account.UserAccountManager");
//            pairs.add(ff);
//        }
//        {
//            FF ff = new FF(true, "MiLinkClientAdapter.getsInstance()", "MiLinkClientAdapter.getInstance()");
//            pairs.add(ff);
//        }


        File bRootFile = new File(MoveAll.toProjectPath);
        Utils.getInstance().digui(null, bRootFile, new IFileFilter() {
                    @Override
                    public boolean accept(String path) {
                        if (MoveAll.inTo(path) && path.endsWith(".java")) {
                            return true;
                        }
                        return false;
                    }
                },
                new Processor() {
                    @Override
                    public String process(String path) {

                        File fromFile = new File(path);
                        Scanner sc = null;
                        try {
                            sc = new Scanner(new FileInputStream(fromFile));
                            StringBuilder sb = new StringBuilder();
                            boolean hasReplace = false;
                            while (sc.hasNextLine()) {
                                String line1 = sc.nextLine();
                                String line2 = line1;


                                for (FF ff : pairs) {
                                    if (!ff.yange) {
                                        if (line1.startsWith("import ")) {
                                            if (line1.contains(ff.origin)) {
                                                line2 = "import " + ff.replace + ";";
                                            }
                                        }
                                    } else {
                                        if (line1.contains(ff.origin)) {
                                            line2 = line1.replace(ff.origin, ff.replace);
                                        }
                                    }
                                }
                                if (!line2.equals(line1)) {
                                    hasReplace = true;
                                }
                                line2 += "\r\n";
                                sb.append(line2);

                            }
                            sc.close();
                            if (hasReplace) {
                                System.out.println("更新" + fromFile);
                                FileWriter fos = new FileWriter(fromFile);
                                fos.write(sb.toString());
                                fos.close();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return "";
                    }
                });
    }
}
