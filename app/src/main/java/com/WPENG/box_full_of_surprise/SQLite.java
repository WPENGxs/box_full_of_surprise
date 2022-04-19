package com.WPENG.box_full_of_surprise;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQLite extends SQLiteOpenHelper {
    public SQLite(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    /*
    * 建立SQLite数据库
    * */
    @Override
    public void onCreate(SQLiteDatabase Database) {
        String sql="CREATE TABLE user(userid integer primary key autoincrement,"+
                "title text not null,"+//标题
                "contents text not null,"+//文案
                "audio_name text ,"+//音频名称
                "cartoon text,"+//图片名称1
                "poem text,"+//图片名称2
                "music text,"+//图片名称3
                "video_name text ,"+//视频名称
                "audio_path text ,"+//音频路径
                "picture_path1 text,"+//图片路径1
                "picture_path2 text,"+//图片路径2
                "picture_path3 text,"+//图片路径3
                "video_path text ,"+//视频路径
                "password text ,"+//密码
                "tips text not null,"+//提示方式
                "tips_item text ,"+//提示方式的值
                "QRCode_url text ,"+//二维码的指向值
                "Html_path text"+//Html文件的路径
                ")";
        Database.execSQL(sql);
    }
    @Override
    public void onUpgrade(SQLiteDatabase Database, int oldVersion, int newVersion) {
    }

    public static class SQLite_data{//存放中转数据
        static String title="",contents="",audio_name="",cartoon="",poem="",music="",video_name="",
                audio_path="",picture_path1="",picture_path2="",picture_path3="",video_path="",
                password="",tips="",tips_item="",QRCode_url="",Html_path="";
        public static void dataInit(){
            title="";contents="";audio_name="";cartoon="";poem="";music="";video_name="";
                    audio_path="";picture_path1="";picture_path2="";picture_path3="";video_path="";
                    password="";tips="";tips_item="";QRCode_url="";Html_path="";
        }
    }
}
