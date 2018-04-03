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
        {
            FF ff = new FF(false, "UserAccountManager", "com.xiaomi.gamecenter.account.UserAccountManager");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false, "MyUserInfoManager", "com.xiaomi.gamecenter.account.user.MyUserInfoManager");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false, "MiLinkClientAdapter", "com.xiaomi.gamecenter.milink.MiLinkClientAdapter");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false, "PermissionUtils", "com.xiaomi.gamecenter.util.PermissionUtils");
            pairs.add(ff);
        }
        {
            FF ff = new FF(true, "MiLinkClientAdapter.getsInstance()", "MiLinkClientAdapter.getInstance()");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false, "FileUtils", "com.xiaomi.gamecenter.util.FileUtils");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false, "MessageType", "com.xiaomi.gamecenter.data.MessageType");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false, "SDCardUtils", "com.xiaomi.gamecenter.util.SDCardUtils");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false, "MD5", "com.xiaomi.gamecenter.util.MD5");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false, "Constants", "com.xiaomi.gamecenter.Constants");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false, "R", "com.xiaomi.gamecenter.R");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false, "DisplayUtils", "com.xiaomi.gamecenter.util.DisplayUtils");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false, "CommonUtils", "com.xiaomi.gamecenter.util.CommonUtils");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false, "Attachment", "com.xiaomi.gamecenter.data.Attachment");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false, "AttachmentUtils", "com.xiaomi.gamecenter.util.AttachmentUtils");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false, "BaseActivity", "com.base.activity.BaseActivity");
            pairs.add(ff);
        }

        {
            FF ff = new FF(false, "KeyboardUtils", "com.xiaomi.gamecenter.util.KeyboardUtils");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false, "AlertDialog", "com.base.dialog.MyAlertDialog");
            pairs.add(ff);
        }
//        {
//            FF ff = new FF(false, "FileUploadSenderWorker", "com.wali.live.upload.FileUploadSenderWorker");
//            pairs.add(ff);
//        }
//        {
//            FF ff = new FF(false, "BackTitleBar", "com.base.view.BackTitleBar");
//            pairs.add(ff);
//        }

//        {
//            FF ff = new FF(false, "PreferenceUtils","com.mi.live.data.preference.MLPreferenceUtils");
//            pairs.add(ff);
//        }


        File bRootFile = new File("/Users/chengsimin/dev/GameCenterPhone/gamecenter_knights");
        Utils.getInstance().digui(null, bRootFile, new IFileFilter() {
                    @Override
                    public boolean accept(String path) {
                        if (path.contains("app/src/main/chat-java/") && path.endsWith(".java")) {
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
