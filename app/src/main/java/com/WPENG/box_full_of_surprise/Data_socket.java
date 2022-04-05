package com.WPENG.box_full_of_surprise;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Data_socket {
    Socket socket=null;
    InputStream is;
    BufferedReader br;
    /**
     *
     * @param ip
     *          服务器的ip
     * @param port
     *          端口号
     * @throws IOException
     */
    public Data_socket(String ip, int port) throws IOException {//创建连接
        socket=new Socket(ip,port);
    }

    /**
     *
     * @return
     *          返回服务器的数据
     * @throws IOException
     */
    public String GetData() throws IOException {
        if(socket!=null) {
            is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            return br.readLine();
        }
        else{
            return null;
        }
    }

    /**
     *
     * @param str
     *              发送到服务器的数据
     * @throws IOException
     */
    public void SentData(String str) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write((str+"\n").getBytes(StandardCharsets.UTF_8));
        // 特别注意：数据的结尾加上换行符才可让服务器端的readline()停止阻塞
        outputStream.flush();
    }

    /**
     *          断开服务器的连接
     * @throws IOException
     */
    public void Close() throws IOException {
        //is.close();
        //br.close();
        //软件中不需要服务器返回数据，所以将这两行注释掉，避免is和br为空导致程序崩溃
        socket.close();

    }
}
