package com.iscgar.android.xposed.calllog.unlimited;

import android.net.Uri;
import android.provider.CallLog;
import android.util.Log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Unlimited implements IXposedHookLoadPackage {
    private class CallLogProviderDeleteHook extends XC_MethodHook {
        @Override
        protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
            Uri uri = (Uri)param.args[0];
            if (!uri.getAuthority().equals(CallLog.AUTHORITY) &&
                !uri.getAuthority().equals("call_log_shadow")) {
                return;
            }
            if (!uri.getPath().equals("/calls")) {
                return;
            }
            String selection = (String)param.args[1];
            if (selection.contains(" LIMIT -1 OFFSET 500")) {
                param.setResult(0);
            }
        }
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.android.providers.contacts")) {
            return;
        }

        try {
            XposedHelpers.findAndHookMethod(
                "com.android.providers.contacts.CallLogProvider",
                    lpparam.classLoader,
                    "delete",
                    new Object[]{Uri.class, String.class, String[].class, new CallLogProviderDeleteHook()});
        } catch (NoSuchMethodError e) {
            Log.e("UnlimitedCallLog", "Failed to hook method: " + e.toString());
        } catch (XposedHelpers.ClassNotFoundError e) {
            Log.e("UnlimitedCallLog", "Failed to hook class: " + e.toString());
        }
    }
}
