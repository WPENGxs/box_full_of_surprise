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
        HtmlOuter.append("<!DOCTYPE html>\n" +
                "<html lang=\"zh-CN\">\n" +
                "    <head>\n" +
                "        <meta charset=\"utf-8\">\n" +
                "        <meta name=\"keywords\" content=\"述梦阁\">\n" +
                "        <meta name=\"description\" content=\"\">\n" +
                "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "        <title>述梦阁</title>\n" +
                "        <link rel=\"stylesheet\" href=\"style/bootstrap.css\">\n" +
                "        <script type=\"text/javascript\" src=\"style/JS_style.js\"></script>\n" +
                "    </head>");//开头

        HtmlOuter.append("<body background=\"https://www.wpengxs.cn/shu_meng_ge/shu_meng_ge_html/shu_meng_ge_background/").append(Set_pack.Background).append("\"");
        if(!SQLite.SQLite_data.password.equals("")){
            HtmlOuter.append(" onload=\"init(),show()\"");
        }
        HtmlOuter.append(">");
        //选择背景图片

        if(!SQLite.SQLite_data.password.equals("")){//如果密码不为空值
            HtmlOuter.append("<div class=\"modal\" id=\"login\">\n" +
                    "    <div class=\"modal-dialog\" role=\"document\">\n" +
                    "      <div class=\"modal-content\">\n" +
                    "        <div class=\"modal-header\">\n" +
                    "          <h5 class=\"modal-title\">注意</h5>\n" +
                    "        </div>\n" +
                    "        <div class=\"modal-body\">\n" +
                    "          <p>你需要输入密码才能访问哦</p>\n" +
                    "        </div>\n" +
                    "        <div class=\"modal-body\">\n" +
                    "          <input id=\"password\" name=\"password\" \n" +
                    "          tabindex=\"2\" accesskey=\"\" value=\"\" \n" +
                    "          autocomplete=\"off\" type=\"password\" \n" +
                    "          class=\"password\" placeholder=\"密码 Password\">\n" +
                    "        </div>\n" +
                    "        <div class=\"modal-body\">\n" +
                    "          <p>提示:");
            if(SQLite.SQLite_data.tips.equals("\"不设置提示\"")){
                HtmlOuter.append("无提示");
            }
            else if(SQLite.SQLite_data.tips.equals("问题提示")){
                HtmlOuter.append(SQLite.SQLite_data.tips_item);
            }
            HtmlOuter.append("</p>\n" +
                    "        </div>\n" +
                    "        <div class=\"modal-footer\">\n" +
                    "          <button type=\"submit\" name=\"submit\" class=\"btn btn-primary\" \n" +
                    "          id=\"passbutton\" accesskey=\"l\" tabindex=\"6\" onclick=\"PassLogin();\">确认</button>\n" +
                    "        </div>\n" +
                    "      </div>\n" +
                    "    </div>\n" +
                    "  </div>");
            HtmlOuter.append("<div class=\"over\" id=\"over\"></div>");
        }


        HtmlOuter.append("<div class=\"centered\">");
        HtmlOuter.append("<h1>").append(Title).append("</h1>");
        HtmlOuter.append("<p>").append(Contents).append("</p>");
        HtmlOuter.append("</div>");

        if(!SQLite.SQLite_data.audio_path.equals("")) {
            HtmlOuter.append("<div class=\"centered\">");
            HtmlOuter.append("<audio controls=\"controls\">");
            HtmlOuter.append("<source src=\"").append(audio).append("\" type=\"audio/mp4\">");
            HtmlOuter.append("<p>您的浏览器不支持 audio 元素。</p>");
            HtmlOuter.append("</audio>");
            HtmlOuter.append("</div>");
        }
        if(!SQLite.SQLite_data.video_path.equals("")) {
            HtmlOuter.append("<div class=\"centered\">");
            HtmlOuter.append("<video controls=\"controls\" preload=\"metadata\">");
            HtmlOuter.append("<source src=\"").append(video).append("\" type=\"video/mp4\">");
            HtmlOuter.append("<p>您的浏览器不支持 video 元素。</p>");
            HtmlOuter.append("</video>");
            HtmlOuter.append("</div>");
        }

        HtmlOuter.append("</div>");
        HtmlOuter.append("</body>");
        //HtmlOuter.append("");

        HtmlOuter.append("</html>");//结尾

        if (printStream != null) {
            printStream.println(HtmlOuter);
        }
        else{
            Log.e("HtmlOutputError","HtmlOutputError");
        }
    }
}
