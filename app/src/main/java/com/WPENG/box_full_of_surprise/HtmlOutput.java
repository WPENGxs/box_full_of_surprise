package com.WPENG.box_full_of_surprise;

import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class HtmlOutput {

    public void CreateHtml(String Title, String Contents, String audio,String video,File HtmlFile){
        StringBuilder HtmlOuter=new StringBuilder();
        PrintStream printStream=null;
        try {
            printStream=new PrintStream(new FileOutputStream(HtmlFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        HtmlOuter.append("<!DOCTYPE html>");//开头

        HtmlOuter.append("<html lang=\"zh-CN\">");
        HtmlOuter.append("<head>");
        HtmlOuter.append("<meta charset=\"utf-8\">");
        HtmlOuter.append("<meta name=\"keywords\" content=\"述梦阁\">");
        HtmlOuter.append("<meta name=\"description\" content=\"\">");
        HtmlOuter.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        HtmlOuter.append("<title>述梦阁</title>");
        HtmlOuter.append("<link rel=\"stylesheet\" href=\"style/style.css\">");
        HtmlOuter.append("<script type=\"text/javascript\" src=\"style/JS_style.js\"></script>");
        HtmlOuter.append("</head>");

        HtmlOuter.append("<body background=\"https://www.wpengxs.cn/shu_meng_ge/shu_meng_ge_background/").append(Set_pack.Background).append("\"");
        if(!SQLite.SQLite_data.password.equals("")){
            HtmlOuter.append(" onload=\"init(),show()\"");
        }
        HtmlOuter.append(">");
        //选择背景图片

        if(!SQLite.SQLite_data.password.equals("")){//如果密码不为空值
            HtmlOuter.append("<div class=\"login\" id=\"login\">\n" +
                    "            <div class=\"tips_word\">\n" +
                    "                <p>此条祝福需要密码才能访问</p>\n" +
                    "                <p>请输入密码</p>\n" +
                    "            </div>\n" +
                    "            <div>\n" +
                    "                <input id=\"password\" name=\"password\" \n" +
                    "                tabindex=\"2\" accesskey=\"\" value=\"\" \n" +
                    "                autocomplete=\"off\" type=\"password\" \n" +
                    "                class=\"password\" placeholder=\"密码 Password\">\n" +
                    "            </div>\n" +
                    "        \n" +
                    "            <div>\n" +
                    "                <button type=\"submit\" class=\"pass_btn\" \n" +
                    "                name=\"submit\" id=\"passbutton\" accesskey=\"l\" \n" +
                    "                tabindex=\"6\" onclick=\"PassLogin();\">登录</button>\n" +
                    "            </div>\n" +
                    "            <div class=\"tips_word\">\n" +
                    "                <p>提示:");
            if(SQLite.SQLite_data.tips.equals("\"不设置提示\"")){
                HtmlOuter.append("无提示");
            }
            else if(SQLite.SQLite_data.tips.equals("问题提示")){
                HtmlOuter.append(SQLite.SQLite_data.tips_item);
            }
            HtmlOuter.append("</p>\n" +
                    "            </div>\n" +
                    "        </div>");
            HtmlOuter.append("<div class=\"over\" id=\"over\"></div>");
        }


        HtmlOuter.append("<div class=\"centered\">");
        HtmlOuter.append("<h1>").append(Title).append("</h1>");
        HtmlOuter.append("<p>").append(Contents).append("</p>");
        HtmlOuter.append("</div>");

        HtmlOuter.append("<div class=\"centered\">");
        HtmlOuter.append("<audio controls=\"controls\">");
        HtmlOuter.append("<source src=\"").append(audio).append("\" type=\"audio/mp4\">");
        HtmlOuter.append("<p>您的浏览器不支持 audio 元素。</p>");
        HtmlOuter.append("</audio>");
        HtmlOuter.append("</div>");

        HtmlOuter.append("<div class=\"centered\">");
        HtmlOuter.append("<video controls=\"controls\" preload=\"metadata\">");
        HtmlOuter.append("<source src=\"").append(video).append("\" type=\"video/mp4\">");
        HtmlOuter.append("<p>您的浏览器不支持 video 元素。</p>");
        HtmlOuter.append("</video>");
        HtmlOuter.append("</div>");

        HtmlOuter.append("</body>");
        //HtmlOuter.append("");

        HtmlOuter.append("</html>");//结尾

        if (printStream != null) {
            printStream.println(HtmlOuter.toString());
        }
        else{
            Log.e("HtmlOutputError","HtmlOutputError");
        }
    }
}
