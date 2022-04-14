package com.WPENG.box_full_of_surprise;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    boolean isExit=false;//点击两次退出的一个判断
    static SQLite Data;
    static SQLiteDatabase Database;//数据库对象
    static String insert_sql;

    @SuppressLint({"UseCompatLoadingForDrawables", "ResourceAsColor"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Request_permission();//权限判断和申请

        ConstraintLayout main=findViewById(R.id.mainActivity);
        Calendar cal = Calendar.getInstance();//设置日历
        Button scan_code=findViewById(R.id.button_scan_code);
        Drawable scan_code_drawable=scan_code.getBackground();
        Button create_blessing=findViewById(R.id.button_create_blessing);
        Drawable create_blessing_drawable=create_blessing.getBackground();
        Button settings=findViewById(R.id.button_settings);
        Drawable settings_drawable=settings.getBackground();

        if(6<=cal.get(Calendar.HOUR_OF_DAY)&&cal.get(Calendar.HOUR_OF_DAY)<=18){//获取当前小时
            main.setBackground(getDrawable(R.drawable.main));

            scan_code_drawable.setTint(R.color.myColor);
            create_blessing_drawable.setTint(R.color.myColor);
            settings_drawable.setTint(R.color.myColor);
        }else {
            main.setBackground(getDrawable(R.drawable.main_night));

            scan_code_drawable.setTint(Color.WHITE);
            create_blessing_drawable.setTint(Color.WHITE);
            settings_drawable.setTint(Color.WHITE);
        }



        Data=new SQLite(this,"Database",null,1);//构造数据库
        Database = Data.getWritableDatabase();//获取可写数据库


    }

    public void Request_permission(){//权限判断和申请
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
        ||ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.CAMERA)
        ||ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
        ||ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.RECORD_AUDIO)) {
            /*
             * 这个if中的条件如果是已经授权的话，就是false，如果没有授权就是true
             * */
            AlertDialog.Builder Dialog = new AlertDialog.Builder(MainActivity.this);
            Dialog.setTitle("权限申请");
            Dialog.setMessage("上传视频、音频需要手机的读写、录音权限\n扫描二维码需要摄像头权限\n请授权，否则软件将无法正常工作！");
            Dialog.setPositiveButton("好的", (dialog,which)->{
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.RECORD_AUDIO}, 1);//申请权限
            });
            Dialog.show();
        }
    }

    @Override
    public void onBackPressed(){
        if(isExit) {
            finish();
            isExit=false;
        }
        else{
            isExit=true;
            Toast.makeText(this, "再次点击返回键回到桌面",Toast.LENGTH_SHORT).show();
        }
    }
    public void To_Scan_QRCode(View view){//跳转到扫描二维码界面
        startActivityForResult(new Intent(getApplicationContext(), CaptureActivity.class), 200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {//返回二维码的内容
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("result");
            if(scanResult.startsWith("http://")
                    || scanResult.startsWith("https://")){
                //判断返回值是否为一个链接，是的话就直接访问
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(scanResult)));
            }
            else{//如果不是链接，就返回二维码的字符
                AlertDialog.Builder Dialog = new AlertDialog.Builder(MainActivity.this);
                Dialog.setTitle("二维码扫描结果");
                Dialog.setMessage("\n"+scanResult);
                Dialog.show();
            }
        }
    }

    public void To_create_blessing(View view){
        SQLite.SQLite_data.dataInit();
        Intent intent = new Intent(this, Create_blessing.class);//显示intent
        startActivity(intent);
    }

    public void To_settings(View view){
        Intent intent = new Intent(this, Settings.class);//显示intent
        startActivity(intent);
    }

    public void To_about(View view){
        AlertDialog.Builder Dialog = new AlertDialog.Builder(MainActivity.this);
        Dialog.setTitle("关于");
        Dialog.setMessage("述梦阁");
        Dialog.show();
    }
}