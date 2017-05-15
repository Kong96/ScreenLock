package com.example.administrator.mylock2;


import android.app.Activity;
import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StudyInformation extends Activity implements huadong.RemoveListener, View.OnClickListener {
    private huadong slideCutListView ;
    private ArrayAdapter<String> adapter;
    private static int hourOfDay=0;
    private static int minute=20;
    private DevicePolicyManager policyManager;
    private ComponentName componentName;
    private List<String> dataSourceList = new ArrayList<String>();
    EditText etTask;
    EditText etTime;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_information);
        policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(this, AdminReceiver.class);
//          init(null);
        try {
            syncData();
            init();
        } catch (IOException e) {
            e.printStackTrace();
        }
        etTask=(EditText) findViewById(R.id.Edit_editTask);
        etTime= (EditText) findViewById(R.id.Edit_editTime);
        //按键监听
        findViewById(R.id.But_AddTask).setOnClickListener(this);

        findViewById(R.id.btnabout).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1_0();
            }
        });

    }


    private void dialog1_1() {

        //先new出一个监听器，设置好监听
        DialogInterface.OnClickListener dialogOnclicListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {

                    case Dialog.BUTTON_NEUTRAL:
                        break;
                    case Dialog.BUTTON_POSITIVE:
                        if (!policyManager.isAdminActive(componentName))
                        {
                            Toast.makeText(StudyInformation.this, "请在主界面获取必要的权限！", Toast.LENGTH_SHORT).show();
                            dialog1_1();
                        }
                        if(policyManager.isAdminActive(componentName)) {
                            Toast.makeText(StudyInformation.this, "快速开始", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(StudyInformation.this, ATY2.class);
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

        builder.create().show();
    }



    private static final String TAG="看看有没有用咯";
    public void syncData() throws IOException {
        InputStream is= openFileInput("task");
        InputStreamReader isr=new InputStreamReader(is,"UTF-8");
        BufferedReader br=new BufferedReader(isr);
        String in="";
        while((in=br.readLine())!=null)
        {
            Log.i(TAG, in);
            System.out.println("我叫换行。。。。。。");
            dataSourceList.add(in);
        }
    }

    public void outputData() throws IOException {
        FileOutputStream fos=openFileOutput("task", Context.MODE_PRIVATE);
        OutputStreamWriter osw=new OutputStreamWriter(fos,"UTF-8");
        for(int i=0;i<=dataSourceList.size()-1;i++)
        {
            osw.write(dataSourceList.get(i).toString()+"\n");
        }
        osw.flush();
        fos.flush();
        osw.close();
        fos.close();
    }


    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.But_AddTask:

                if(etTask.getText().length()!=0)
                {
                    dataSourceList.add("时间： "+etTime.getText().toString()+" 任务:  " + etTask.getText().toString());
                    init();
                    //dialog1_1();
                }
                break;
        }
    }



    private void init() {
        slideCutListView = (huadong) findViewById(R.id.slideCutListView);
        slideCutListView.setRemoveListener(this);


        adapter = new ArrayAdapter<String>(this, R.layout.listview_item, R.id.list_item, dataSourceList);
        slideCutListView.setAdapter(adapter);

        slideCutListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(StudyInformation.this, dataSourceList.get(position), Toast.LENGTH_SHORT).show();
                System.out.println(dataSourceList.get(position));
                String Infer =dataSourceList.get(position);
                Pattern p = Pattern.compile("\\d+");
                Matcher m = p.matcher(Infer);
                String str=null;
                while(m.find()) {
                    str = m.group();
                }
                int time = Integer.parseInt(str);
                hourOfDay = time / 60;
                minute = time % 60;
                dialog1_1();
            }
        });
    }


    //滑动删除之后的回调方法
    @Override
    public void removeItem(huadong.RemoveDirection direction, int position) {
        adapter.remove(adapter.getItem(position));
        switch (direction) {
            case RIGHT:
                Toast.makeText(this, "向右删除  ", Toast.LENGTH_SHORT).show();
                break;
            case LEFT:
                Toast.makeText(this, "向左删除  ", Toast.LENGTH_SHORT).show();
                break;

            default:
                break;
        }

    }

    public void onBackPressed() {
        try {
            outputData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        startActivity(new Intent(StudyInformation.this, MainActivity.class));
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

