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
            FF ff = new FF(false, "BaseComponentActivity", "com.wali.live.video.BaseComponentActivity");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false, "com.wali.live.base.BaseAppActivity", "com.xiaomi.channel.base.BaseAppActivity");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false, "com.wali.live.activity.WebViewActivity", "com.xiaomi.channel.activity.WebViewActivity");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false, "com.wali.live.listener.FragmentDataListener", "com.wali.live.common.listener.FragmentDataListener");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false, "RechargeFragment", "com.xiaomi.channel.recharge.view.RechargeFragment");
            pairs.add(ff);
        }

        {
            FF ff = new FF(false, "RepeatScrollView", "com.xiaomi.channel.michannel.view.RepeatScrollView");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false, "com.wali.live.utils.Base64", "com.xiaomi.channel.utils.Base64");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false, "IScrollListener", "com.xiaomi.channel.michannel.view.IScrollListener");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false, "RelationUtils", "com.wali.live.communication.chat.common.relation.RelationUtils");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false, "IShareView", "com.xiaomi.channel.presentation.view.IShareView");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false, ".EventController", "com.xiaomi.channel.eventbus.EventController");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false, "NetworkReceiver", "com.xiaomi.channel.receiver.NetworkReceiver");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false, ".LiveShow;", "com.xiaomi.channel.data.LiveShow");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false, "com.wali.live.api.UserInfoManager", "com.xiaomi.channel.api.UserInfoManager");
            pairs.add(ff);
        }

        {
            FF ff = new FF(false, "AvatarUtils", "com.mi.live.data.Utils.AvatarUtils");
            pairs.add(ff);
        }

        {
            FF ff = new FF(false, "CallActionController", "com.xiaomi.channel.voip.controller.CallActionController");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false, "com.wali.live.data.UserListData", "com.mi.live.data.relation.model.UserListData");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false, "SmileyParser", "com.wali.live.common.smiley.originsmileypicker.SmileyParser");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false, "SmileyTranslateFilter", "com.wali.live.common.smiley.originsmileypicker.SmileyTranslateFilter");
            pairs.add(ff);
        }

        {
            FF ff = new FF(false, "SmileyInputFilter", "com.wali.live.common.smiley.originsmileypicker.SmileyInputFilter");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false, "SmileyPicker", "com.wali.live.common.smiley.view.SmileyPicker");
            pairs.add(ff);
        }

        {
            FF ff = new FF(false, "AppCommonUtils", "com.xiaomi.channel.utils.AppCommonUtils");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false, "IndexableRecyclerView", "com.wali.live.common.view.IndexableRecyclerView");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false, "com.wali.live.eventbus.EventClass", "com.xiaomi.channel.eventbus.EventClass");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false, "BackShowListData", "com.xiaomi.channel.data.BackShowListData");
            pairs.add(ff);
        }

        {
            FF ff = new FF(false
                    , "com.wali.live.fragment.BaseEventBusFragment"
                    , "com.xiaomi.channel.fragment.BaseEventBusFragment");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false
                    , "com.wali.live.fragment.BaseFragment"
                    , "com.xiaomi.channel.fragment.BaseFragment");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false
                    , "RechargeActivity"
                    , "com.xiaomi.channel.recharge.activity.RechargeActivity");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false
                    , "PushStatisticsDispatcher"
                    , "com.xiaomi.channel.pushtimestatistics.PushStatisticsDispatcher");
            pairs.add(ff);
        }

//        {
//            FF ff = new FF(false
//                    , ".Presenter"
//                    , "com.base.presenter.Presenter");
//            pairs.add(ff);
//        }
        {
            FF ff = new FF(false
                    , ".MyRxFragment"
                    , "com.xiaomi.channel.fragment.MyRxFragment");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false
                    , ".SchemeConstants"
                    , "com.xiaomi.channel.scheme.SchemeConstants");
            pairs.add(ff);
        }

        {
            FF ff = new FF(false
                    , ".BaseEventBusFragment"
                    , "com.xiaomi.channel.fragment.BaseEventBusFragment");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false
                    , ".CropImage"
                    , "com.wali.live.common.crop.CropImage");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false
                    , ".CropImageView"
                    , "com.wali.live.common.crop.CropImageView");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false
                    , ".RoomTag"
                    , "com.wali.live.video.viewmodel.RoomTag");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false
                    , "com.wali.live.utils.FragmentNaviUtils;"
                    , "com.xiaomi.channel.utils.FragmentNaviUtils");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false
                    , "EventController"
                    , "com.xiaomi.channel.eventbus.EventController");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false
                    , "PinyinUtils"
                    , "com.base.utils.pinyin.PinyinUtils");
            pairs.add(ff);
        }

        {
            FF ff = new FF(false
                    , "LiveLinearLayoutManager"
                    , "com.wali.live.common.view.IndexableRecyclerview.LiveLinearLayoutManager");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false
                    , "BalanceFragment"
                    , "com.xiaomi.channel.recharge.fragment.BalanceFragment");
            pairs.add(ff);
        }
//        {
//            FF ff = new FF(false
//                    , "LocationHelper"
//                    , "com.xiaomi.channel.utils.LocationHelper");
//            pairs.add(ff);
//        }

        {
            FF ff = new FF(false
                    , "RxCountDown"
                    , "com.xiaomi.channel.utils.RxCountDown");
            pairs.add(ff);
        }


        {
            FF ff = new FF(false
                    , ".OnItemLongClickListener;"
                    , "com.xiaomi.channel.listener.OnItemLongClickListener");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false
                    , "LoginActivity"
                    , "com.xiaomi.channel.activity.LoginActivity");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false
                    , "WebViewListener"
                    , "com.xiaomi.channel.view.webview.WebViewListener");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false
                    , "InjectedWebViewClient"
                    , "com.xiaomi.channel.view.webview.jsbridge.InjectedWebViewClient");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false
                    , "SpecialLinearLayoutManager"
                    , "com.xiaomi.channel.view.SpecialLinearLayoutManager");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false
                    , "VideoAction"
                    , "com.xiaomi.channel.action.VideoAction");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false
                    , "RelationDaoAdapter"
                    , "com.xiaomi.channel.greendao.adapter.RelationDaoAdapter");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false
                    , "LiveWebViewClient"
                    , "com.xiaomi.channel.view.webview.LiveWebViewClient");
            pairs.add(ff);
        }
        {
            FF ff = new FF(true
                    , "onEventMainThread(EventClass.NetWorkChangeEvent"
                    , "onEventMainThread(com.xiaomi.channel.eventbus.EventClass.NetWorkChangeEvent");
            pairs.add(ff);
        }
        {
            FF ff = new FF(true
                    , "onEventMainThread(EventClass.OrientEvent"
                    , "onEventMainThread(com.xiaomi.channel.eventbus.EventClass.OrientEvent");
            pairs.add(ff);
        }
        {
            FF ff = new FF(true
                    , "LiveApplication.getInstance()"
                    , "GlobalData.app()");
            pairs.add(ff);
        }


        {
            FF ff = new FF(false
                    , "MakeCallController"
                    , "com.xiaomi.channel.voip.controller.MakeCallController");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false
                    , "TaskCallBackWrapper"
                    , "com.xiaomi.channel.task.TaskCallBackWrapper");
            pairs.add(ff);
        }

        {
            FF ff = new FF(false
                    , "RotatedSeekBar"
                    , "com.wali.live.video.widget.RotatedSeekBar");
            pairs.add(ff);
        }

        {
            FF ff = new FF(false
                    , "SwitchButton"
                    , "com.base.view.SwitchButton");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false
                    , "OnItemLongClickListener"
                    , "com.xiaomi.channel.listener.OnItemLongClickListener");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false
                    , "DateTimeUtils"
                    , "com.mi.live.data.Utils.DateTimeUtils");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false
                    , "SnsShareHelper"
                    , "com.wali.live.video.view.bottom.SnsShareHelper");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false
                    , "WatchActivity"
                    , "com.wali.live.video.WatchActivity");
            pairs.add(ff);
        }
        {
            FF ff = new FF(false
                    , "MessageType"
                    , "com.wali.live.utils.MessageType");
            pairs.add(ff);
        }

        File bRootFile = new File("/Users/chengsimin/dev/miliao/mitalk");
        Utils.getInstance().digui(null, bRootFile, new IFileFilter() {
                    @Override
                    public boolean accept(String path) {
                        if (path.contains("/src/main/java-milive/") && path.endsWith(".java")) {
                            return true;
                        }
                        if (path.contains("app/src/main/java-mitalk/") && path.endsWith(".java")) {
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
