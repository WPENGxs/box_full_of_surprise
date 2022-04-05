package com.WPENG.box_full_of_surprise;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.WPENG.box_full_of_surprise.zxing.encode.QRcode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        List<String> list = new ArrayList<>();
        Cursor cursor = MainActivity.Database.rawQuery("select * from user", null);
        int num = 1;
        while (cursor.moveToNext()) {
            String Title=cursor.getString(cursor.getColumnIndex("title"));
            list.add(Title);//将值加入list中
            num++;
        }
        cursor.close();

        RecyclerView recyclerView = findViewById(R.id.RecyclerView);//声明RecyclerView
        RecycleAdapter adapter = new RecycleAdapter(Settings.this,list);//声明适配器
        /*
        与ListView效果对应的可以通过LinearLayoutManager来设置
        与GridView效果对应的可以通过GridLayoutManager来设置
        与瀑布流对应的可以通过StaggeredGridLayoutManager来设置
        */
        LinearLayoutManager manager = new LinearLayoutManager(Settings.this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(Settings.this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {//TextView短按
                        //Log.d("onItemClick","onItemClick");
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {//TextView长按
                        onItemLongClickDialog(list.get(position),position);
                        //Log.d("onItemLongClick","onItemLongClick");
                    }
                })
        );

    }

    public void onItemLongClickDialog(String index,int numIndex){
        AlertDialog.Builder Dialog = new AlertDialog.Builder(this);
        Dialog.setTitle("系统提示");
        Dialog.setMessage("标题："+index+"\n"+"\n"+"您需要:");
        Dialog.setPositiveButton("删除此条祝福", (dialog, which) -> {
            Cursor cursor = MainActivity.Database.rawQuery("select * from user", null);
            cursor.moveToPosition(numIndex);
            String del_id=cursor.getString(cursor.getColumnIndex("userid"));

            String del_Html=cursor.getString(cursor.getColumnIndex("Html_path"));
            deleteSingleFile(del_Html);//删除Html文件

            /*
             *删除服务器上的Html
             */

            MainActivity.Database.execSQL("delete from user where userid="+del_id);
            cursor.close();
            Toast.makeText(this,"已删除",Toast.LENGTH_SHORT).show();
            refresh();
        });
        Dialog.setNegativeButton("查看详细数据", (dialog, which) -> {
            Cursor cursor = MainActivity.Database.rawQuery("select * from user", null);
            cursor.moveToPosition(numIndex);
            String title=cursor.getString(cursor.getColumnIndex("title"));
            String contents=cursor.getString(cursor.getColumnIndex("contents"));
            String audio_name=cursor.getString(cursor.getColumnIndex("audio_name"));
            String video_name=cursor.getString(cursor.getColumnIndex("video_name"));
            String password=cursor.getString(cursor.getColumnIndex("password"));
            String tips=cursor.getString(cursor.getColumnIndex("tips"));
            String tips_item=cursor.getString(cursor.getColumnIndex("tips_item"));
            String QRCode_url=cursor.getString(cursor.getColumnIndex("QRCode_url"));
            AlertDialog.Builder Dialog_detail=new AlertDialog.Builder(this);
            Dialog_detail.setTitle("详细数据");
            Dialog_detail.setMessage("标题:"+title+"\n"+
                    "文案:"+contents+"\n"+
                    "音频:"+audio_name+"\n"+
                    "视频:"+video_name+"\n"+
                    "密码:"+password+"\n"+
                    "提示方式:"+tips+"\n"+
                    "提示值:"+tips_item+"\n");
            Dialog_detail.setPositiveButton("查看二维码",(dialog_detail, which_detail) -> {
                ImageView QRCode_img = new ImageView(Settings.this);
                Bitmap QRCode_bitmap= QRcode.createQRCode(QRCode_url,1000);
                //设置size为1000是为了增大图片，避免图片过小被系统忽略
                if(QRCode_bitmap!=null){
                    QRCode_img.setImageBitmap(QRCode_bitmap);
                    android.app.AlertDialog.Builder Dialog_QRCode=new android.app.AlertDialog.Builder(Settings.this);
                    Dialog_QRCode.setTitle("二维码为");
                    Dialog_QRCode.setView(QRCode_img);
                    Dialog_QRCode.setPositiveButton("保存二维码",(dialog_QRCode, which_QRCode) -> {
                        Set_pack.saveImageToGallery(Settings.this,QRCode_bitmap,SQLite.SQLite_data.title);
                    });
                    Dialog_QRCode.show();
                }
                else{
                    Toast.makeText(Settings.this,"不知道为什么，生成二维码失败",Toast.LENGTH_SHORT).show();
                }
            });
            Dialog_detail.setNegativeButton("预览效果", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(QRCode_url), "text/html");
                    startActivity(intent);
                }
            });
            Dialog_detail.show();
            cursor.close();
        });
        Dialog.show();
    }

    /** 删除单个文件
     * @param filePath$Name 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteSingleFile(String filePath$Name) {
        File file = new File(filePath$Name);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                Log.i("delError","Copy_Delete.deleteSingleFile: 删除单个文件" + filePath$Name + "成功！");
                return true;
            } else {
                Log.e("delError","删除单个文件" + filePath$Name + "失败！");
                return false;
            }
        } else {
            Log.e("delError","删除单个文件失败：" + filePath$Name + "不存在！");
            return false;
        }
    }

    public void refresh(){//刷新函数——通过结束当前界面并新建界面的方式刷新
        finish();
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    public void To_MainActivity(View view){
        Intent intent = new Intent(this, MainActivity.class);//显示intent
        startActivity(intent);
    }
}