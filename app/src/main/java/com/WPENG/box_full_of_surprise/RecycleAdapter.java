package com.WPENG.box_full_of_surprise;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/*
① 创建一个继承RecyclerView.Adapter<VH>的Adapter类
② 创建一个继承RecyclerView.ViewHolder的静态内部类
③ 在Adapter中实现3个方法：
   onCreateViewHolder()
   onBindViewHolder()
   getItemCount()
*/
public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.MyViewHolder>{
    private final Context context;
    private final List<String> list;

    //构造方法，传入数据
    public RecycleAdapter(Context context, List<String> list){
        this.context = context;
        this.list = list;
    }

    @NonNull
    @SuppressLint("ResourceType")
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //创建ViewHolder，返回每一项的布局
        View inflater = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(inflater);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //将数据和控件绑定
        holder.textView.setText(list.get(position));
    }

    @Override
    public int getItemCount() {
        //返回Item总条数
        return list.size();
    }

    //内部类，绑定控件
    static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        public MyViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_view);
        }
    }

}

