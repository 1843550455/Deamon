package machine.yzy.cjlm.restartservice;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by xpp on 2018/3/14.
 */

public class AcceptService extends Service{

    private File dir;
    public static final String filePath = SDCardUtil.getSDCardPath() + "yzyApk";
    private String apkUrl;
    private String apkPath = "";//下载好的apk路径

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void onDestroy() {
        super.onDestroy();
    }
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }
    ControlAidlInterface.Stub mBinder = new ControlAidlInterface.Stub() {
        @Override
        public void basicTypes(String yzyMsg) throws RemoteException {
            //新版本url
            apkUrl = yzyMsg;
            handler.sendEmptyMessage(1);
        }
    };
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(SDCardUtil.isSDCardEnable()){// 如果sd卡可用
                dir = new File(filePath);
                if(!dir.exists()){
                    dir.mkdirs();
                }
            }
            EventBus.getDefault().post(new MsgEvent("伊卓园程序正在升级中.."));
            //
            DownloadUtil.getInstance().download(apkUrl, dir.getAbsolutePath(), new DownloadUtil.OnDownloadListener() {
                @Override
                public void onDownloadSuccess(String path) {
                    Log.d("xpp","下载成功>>"+path);
                    apkPath = path;
                    Uri uri = Uri.fromFile(new File(apkPath));
                    Intent localIntent = new Intent(Intent.ACTION_VIEW);
                    localIntent.setDataAndType(uri, "application/vnd.android.package-archive");
                    localIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                    startActivity(localIntent);
                }
                @Override
                public void onDownloading(int progress) {
                    //Log.d("xpp","下载中>>"+progress+"%");
                }
                @Override
                public void onDownloadFailed() {
                    //Log.d("xpp","下载失败");
                }
            });
        }
    };
}
