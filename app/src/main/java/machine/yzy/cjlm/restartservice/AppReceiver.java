package machine.yzy.cjlm.restartservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by xpp on 2018/3/17.
 */

public class AppReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        PackageManager pm = context.getPackageManager();
        if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_ADDED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            Log.d("xpp","packageName>>"+packageName);
            Log.d("xpp", "--------安装成功" + packageName);
            //Toast.makeText(context, "安装成功" + packageName, Toast.LENGTH_LONG).show();
        } else if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_REPLACED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            Log.d("xpp", "--------替换成功" + packageName);
            //Toast.makeText(context, "替换成功" + packageName, Toast.LENGTH_LONG).show();
        } else if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_REMOVED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            Log.d("xpp", "--------卸载成功" + packageName);
           // Toast.makeText(context, "卸载成功" + packageName, Toast.LENGTH_LONG).show();
        }
    }
}
