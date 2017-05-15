package com.example.administrator.mylock2;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static int hourOfDay=0;
    private static int minute=20;
    private DevicePolicyManager policyManager;
    private ComponentName componentName;
    private static final int MY_REQUEST_CODE = 9999;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(this, AdminReceiver.class);
        setContentView(R.layout.activity_main);


        /*
         *设置时间按钮
        */
        findViewById(R.id.btnSetTime).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setAlpha(0.5f);
                if (tpd == null) {
                    tpd_init();
                    v.setAlpha(1f);
                }
                tpd.show();
            }
        });

        /*
         *指定任务的按钮
        */
        findViewById(R.id.btnAddTask).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,StudyInformation.class));
            }
        });

        /*
        *开始锁屏的按钮
        */
        findViewById(R.id.btnStart).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1_1();
            }
        });
        findViewById(R.id.btnabout).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1_0();
            }
        });

    }

    /*
    *重写返回键
    */
    public void onBackPressed() {
        new AlertDialog.Builder(this).setTitle("确认退出吗？")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“确认”后的操作
                        MainActivity.this.finish();
                    }
                })
                .setNegativeButton("返回", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
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










    /*
    *开始的Dialong
    */
    private void dialog1_1() {
        //先new出一个监听器，设置好监听
        DialogInterface.OnClickListener dialogOnclicListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {

                    case
                        Dialog.BUTTON_NEUTRAL:
                        if (policyManager.isAdminActive(componentName))
                        {
                            Toast.makeText(MainActivity.this, "您已经成功激活", Toast.LENGTH_SHORT).show();
                            dialog1_1();
                        }
                        else{
                            Toast.makeText(MainActivity.this, "%>_<% 没有权限 %>_<%", Toast.LENGTH_SHORT).show();
                            activeManage();
                            dialog1_1();
                        }
                          break;
                    case Dialog.BUTTON_POSITIVE:
                        if (!policyManager.isAdminActive(componentName))
                        {
                            Toast.makeText(MainActivity.this, "请先点击左侧，获取必要的权限！", Toast.LENGTH_SHORT).show();
                            dialog1_1();
                        }
                        if(policyManager.isAdminActive(componentName)) {
                            Toast.makeText(MainActivity.this, "确认" + which, Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(MainActivity.this, ATY2.class);
                            // i.putExtra("StartTime",System.currentTimeMillis());
                            i.putExtra("StartTime", System.currentTimeMillis());
                            i.putExtra("StudyTime", (long) ((hourOfDay * 60 + minute) * 1000 * 60));
                            startActivityForResult(i, 1);
                        }

                        break;
                    case Dialog.BUTTON_NEGATIVE:
                        break;
                }
            }

        };
        //dialog参数设置
        AlertDialog.Builder builder = new AlertDialog.Builder(this);  //先得到构造器
        builder.setTitle("警告："); //设置标题
        builder.setMessage("确定开始锁屏?（无法撤销）"); //设置内容
        builder.setIcon(R.mipmap.ic_launcher);//设置图标，图片id即可
        builder.setPositiveButton("确认", dialogOnclicListener);
        builder.setNegativeButton("取消", dialogOnclicListener);
        builder.setNeutralButton("获取权限",dialogOnclicListener);

        builder.create().show();
    }

    /*
    *获取设备管理权限的方法
    */
    private void activeManage()
    {
        // 启动设备管理(隐式Intent) - 在AndroidManifest.xml中设定相应过滤器
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        //权限列表
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
        //描述(additional explanation)
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "必要的系统权限：");
        startActivityForResult(intent, MY_REQUEST_CODE);
        Toast.makeText(MainActivity.this, "请点激活按钮", Toast.LENGTH_SHORT).show();
    }

    /*
    时间设置的dialog
     */
    private TimePickerDialog tpd = null;
    void tpd_init() {
        TimePickerDialog.OnTimeSetListener otsl = new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                EditText ed = (EditText) findViewById(R.id.showTime);
                ed.setText(hourOfDay + "时" + minute + "分");
                MainActivity.hourOfDay=hourOfDay;
                MainActivity.minute=minute;
                ImageButton ib = (ImageButton) findViewById(R.id.btnSetTime);
                ib.setAlpha(1f);
                tpd.dismiss();
            }
        };
        tpd = new TimePickerDialog(this, otsl, hourOfDay, minute, true);
    }


}
