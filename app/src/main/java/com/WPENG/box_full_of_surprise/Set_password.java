package com.WPENG.box_full_of_surprise;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class Set_password extends AppCompatActivity {

    String[] tips= {"Tip:想一想都有哪些祝福的话语？","Tip:要不设置一个“我爱你”试试？","Tip:lalaland"};
    //最好不要超过12个字

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);

        TextView tips_text=findViewById(R.id.textView_password_tips);
        tips_text.setOnClickListener(new View.OnClickListener() {//设置text的点击事件
            @Override
            public void onClick(View v) {
                tips_text.setText(tips[(int)(Math.random()*3)]);//设置tips的随机替换
            }
        });
    }

    @Override
    public void onBackPressed(){//重写返回事件防止跳到后一步
        Intent intent = new Intent(this, Create_blessing.class);//显示intent
        startActivity(intent);
    }

    public void To_create_blessing(View view){//返回
        Intent intent = new Intent(this, Create_blessing.class);//显示intent
        startActivity(intent);
    }

    public void To_set_tips(View view){
        AlertDialog.Builder Dialog = new AlertDialog.Builder(Set_password.this);//一个确认密码输入的弹窗
        Dialog.setTitle("确认密码");
        Dialog.setMessage("确认密码要这样设置么");
        Dialog.setPositiveButton("确认", (dialog,which)->{
            EditText editText_password=findViewById(R.id.editText_password);
            EditText editText_password_confirm=findViewById(R.id.editText_password_confirm);
            String edit_password=editText_password.getText().toString();
            String edit_password_confirm=editText_password_confirm.getText().toString();
            if(edit_password.equals(edit_password_confirm)){
                SQLite.SQLite_data.password=edit_password;

                Intent intent = new Intent(this, Set_tips.class);//显示intent
                startActivity(intent);
            }
            else{
                AlertDialog.Builder Dialog_confirm=new AlertDialog.Builder(Set_password.this);
                Dialog_confirm.setTitle("警告");
                Dialog_confirm.setMessage("两次密码输入不一致\n请核对后再试");
                Dialog_confirm.show();
            }
        });
        Dialog.setNegativeButton("不，我要再改改",(dialog,which)->{

        });
        Dialog.show();
    }
}