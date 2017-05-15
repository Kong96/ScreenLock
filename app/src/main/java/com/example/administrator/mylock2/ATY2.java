package com.example.administrator.mylock2;
import android.app.Activity;
import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import java.util.Timer;
import java.util.TimerTask;
public class ATY2 extends Activity {
    private SoundPool soundPool;
    static long Pretime;
    long StudyTime;
    private DevicePolicyManager policyManager;
    private ComponentName componentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        soundPool= new SoundPool(1,AudioManager.STREAM_SYSTEM,5);
        soundPool.load(this, R.raw.ring, 1);

        super.onCreate(savedInstanceState);
        policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(this, AdminReceiver.class);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
         //无title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);  //全屏
        setContentView(R.layout.activity_aty2);

        findViewById(R.id.btnreturn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ATY2.this, MainActivity.class));
            }
        });

        findViewById(R.id.btnabout).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1_0();
            }
        });

        Intent i=getIntent();
        Pretime=i.getLongExtra("StartTime", System.currentTimeMillis());         //获取MainActivity传来的当前时间

        StudyTime=(i.getLongExtra("StudyTime",(long)233));
        final Button func = (Button) findViewById(R.id.btnShare);

        // 启动设备管理(隐式Intent) - 在AndroidManifest.xml中设定相应过滤器
        if (policyManager.isAdminActive(componentName)) {
            final Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    policyManager.lockNow();
                    if(timeup()>StudyTime+3000)
                    {
                        soundPool.play(1,1, 1, 0, 0, 1);
                        timer.cancel();// 停止定时器
                    }
                }
            };
            timer.schedule(task, 0, 100);
        }
    }

    //判断时间是否已经完结
    public long timeup()
    {
        return(System.currentTimeMillis() - Pretime);
    }
    /*
    about 的dialog
     */
    private void dialog1_0() {
        //先new出一个监听器，设置好监听
        DialogInterface.OnClickListener dialogOnclicListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case Dialog.BUTTON_NEGATIVE:
                        break;
                }
            }

        };
        //dialog参数设置
        AlertDialog.Builder builder = new AlertDialog.Builder(this);  //先得到构造器
        builder.setTitle("             关于"); //设置标题
        builder.setMessage("效率锁屏V1.0\n \n        ©2015 中南民族大学. 版权所有"); //设置内容
//        builder.setIcon();//设置图标，图片id即可


        builder.create().show();
    }

}
