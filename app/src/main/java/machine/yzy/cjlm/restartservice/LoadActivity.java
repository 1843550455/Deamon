package machine.yzy.cjlm.restartservice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;

import cxy.com.loadviewlib.LoadingView;
import machine.yzy.cjlm.restartservice.view.Titanic;
import machine.yzy.cjlm.restartservice.view.TitanicTextView;

/**
 * Created by Administrator on 2018/2/3 0003.
 */

public class LoadActivity extends Activity {

    private TitanicTextView txt_load;
    private Titanic titanic;
    private LoadingView loadingview;
    private ProgressBar pb;
    private Timer timer;
    private TimerTask timerTask;
    private int timeProgress=0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,WindowManager.LayoutParams. FLAG_FULLSCREEN);
        //
        pb=findViewById(R.id.pb);
        loadingview=findViewById(R.id.loadingview);
        loadingview.start();
        txt_load=findViewById(R.id.txt_load);
        Intent sayHelloIntent=new Intent(this,CheckYzyService.class);
        startService(sayHelloIntent);
        //
        pb.setMax(160);
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                timeProgress++;
                handler.sendEmptyMessage(0);
            }
        };
        timer.schedule(timerTask,500,1000);
        titanic = new Titanic();
        titanic.start(txt_load);
        //
        txt_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoadActivity.this,DemoActivity.class));
            }
        });
        //打开系统无障碍设置界面
        //Intent accessibleIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        //startActivity(accessibleIntent);
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0){
                if(timeProgress>=160){
                    timeProgress=0;
                    //txt_load.setText("正在实时监听伊卓园售卖程序");

                    loadingview.stop();
                    if(timer!=null){
                        timer.cancel();
                        timer=null;
                    }
                    if(timerTask!=null){
                        timerTask.cancel();
                        timerTask=null;
                    }
                }
                pb.setProgress(timeProgress);
            }else if(msg.what == 1){
                txt_load.setText(eventMsg);
            }
        }
    };
    private String eventMsg;
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void checkVersion(MsgEvent msgEvent){
        eventMsg = msgEvent.getMsg();
        handler.sendEmptyMessage(1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        titanic.cancel();
        EventBus.getDefault().unregister(this);
    }
}
