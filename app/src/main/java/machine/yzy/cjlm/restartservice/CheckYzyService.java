package machine.yzy.cjlm.restartservice;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by Administrator on 2018/2/3 0003.
 */

public class CheckYzyService extends Service {

    private Timer timer;
    private TimerTask timerTask;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if(timer!=null){
            timer.cancel();
            timer=null;
        }
        if(timerTask!=null){
            timerTask.cancel();
            timerTask=null;
        }
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        };
        timer.schedule(timerTask,160 * 1000,60*1000);

    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);//
            try {
                TopActivityInfo topActivityInfo = getTopActivityInfo();
                Log.d("xpp","topActivityInfo.packageName>>"+topActivityInfo.packageName);
                if(topActivityInfo.packageName.equals("machine.yzy.cjlm.yzy_vendingmachine")){
                    //Log.v("xpp","伊卓园程序正常运行中");
                    EventBus.getDefault().post(new MsgEvent("伊卓园程序被退出或在后台运行，正在重启伊卓园"));//
                }else{
                    EventBus.getDefault().post(new MsgEvent("伊卓园程序被退出或在后台运行，正在重启伊卓园"));
                    //Log.v("xpp","当前伊卓园程序被退出或在后台运行，进行重启伊卓园");
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                    ComponentName cn = new ComponentName("machine.yzy.cjlm.yzy_vendingmachine"
                            ,"machine.yzy.cjlm.yzy_vendingmachine.WelcomActivity");
                    intent.setComponent(cn);
                    startActivity(intent);
                }
            }catch (Exception e){
                Log.v("xpp",e.getLocalizedMessage());
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    public static class TopActivityInfo{
        public String packageName = "";
        public String topActivityName = "";
    }
    private TopActivityInfo getTopActivityInfo() {
        ActivityManager manager = ((ActivityManager)getSystemService(ACTIVITY_SERVICE));
        TopActivityInfo info = new TopActivityInfo();
        if (Build.VERSION.SDK_INT >= 21) {
            List<ActivityManager.RunningAppProcessInfo> pis = manager.getRunningAppProcesses();
            ActivityManager.RunningAppProcessInfo topAppProcess = pis.get(0);
            if (topAppProcess != null && topAppProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                info.packageName = topAppProcess.processName;
                info.topActivityName = "";
            }
        } else {
            List localList = manager.getRunningTasks(1);
            ActivityManager.RunningTaskInfo localRunningTaskInfo = (ActivityManager.RunningTaskInfo)localList.get(0);
            info.packageName = localRunningTaskInfo.topActivity.getPackageName();
            info.topActivityName = localRunningTaskInfo.topActivity.getClassName();
        }
        return info;
    }
}
