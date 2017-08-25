package com.blanke.mdwechat;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.res.XModuleResources;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedBridge.log;

public class WechatHook extends XC_MethodHook
        implements IXposedHookZygoteInit, IXposedHookLoadPackage, IXposedHookInitPackageResources {

    private static String MODULE_PATH = null;

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        MODULE_PATH = startupParam.modulePath;
        WeChatHelper.initPrefs();
    }

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals(Common.WECHAT_PACKAGENAME)) {
            return;
        }
        Context context = (Context) XposedHelpers.callMethod(
                XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", null),
                        "currentActivityThread"), "getSystemContext");
        PackageInfo wechatPackageInfo = context.getPackageManager().getPackageInfo(Common.WECHAT_PACKAGENAME, 0);
        String versionName = wechatPackageInfo.versionName;
        log("wechat version=" + versionName);
        WeChatHelper.init(versionName, lpparam);
    }

    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) throws Throwable {
        if (!resparam.packageName.equals(Common.WECHAT_PACKAGENAME)) {
            return;
        }
//        log("handleInitPackageResources");
        XModuleResources modRes = XModuleResources.createInstance(MODULE_PATH, resparam.res);
//        WeChatHelper.WCId.ic_add = resparam.res.addResource(modRes, R.drawable.ic_add);


//        resparam.res.setReplacement(Common.WECHAT_PACKAGENAME,
//                "color", "z", Color.RED);
//        resparam.res.setReplacement(Common.WECHAT_PACKAGENAME,
//                "drawable", WeChatHelper.WCDrawable.Conference_ListView_Item_Background,
//                modRes.fwd(R.drawable.selector_item));
    }
}
