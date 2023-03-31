package com.jy2b.zxxfd;

import com.jy2b.zxxfd.utils.EmailUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class EmailUtilsTest {
    @Resource
    private EmailUtils emailUtils;

    @Test
    void sendEmail01() {
        emailUtils.sendSimpleEmail("2786794141@qq.com", "发送邮件测试", "你好，Java发送QQ邮件");
    }

    @Test
    void sendEmail02() {
        List<Integer> list = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            list.add(i);
        }
        // 写html开始内容
        String start = "<!DOCTYPE html>\r\n" +
                "<html>\r\n" +
                "<head>\r\n" +
                "<meta charset=\"UTF-8\">\r\n" +
                "<title>Insert title here</title>\r\n" +
                "</head>\r\n" +
                "<body>\r\n" +
                "<h1>序号列表</h1>" +
                "<table border=\"1\">";
        StringBuilder content = new StringBuilder("<tr><th style=\"font-size: 18px\">序号</th></tr>");
        String end = "</table>" +
                "</body>\r\n" +
                "</html>";
        for (Integer index : list) {
            content.append("<tr><td style=\"font-size: 18px\">").append(index).append("</td></tr>");
        }
        String html = start + content + end;

        emailUtils.sendHtmlMail("2786794141@qq.com", "发送HTML邮件测试", html);
    }

    @Test
    void sendEmail03() {
        // 1、写html格式内容
        String html = "<!DOCTYPE html>\r\n" +
                "<html>\r\n" +
                "<head>\r\n" +
                "<meta charset=\"UTF-8\">\r\n" +
                "<title>Insert title here</title>\r\n" +
                "</head>\r\n" +
                "<body>\r\n" +
                "    <font color=\"red\">发送html</font>\r\n" +
                "</body>\r\n" +
                "</html>";
        String file = "E:\\存放文件\\驿站\\工作目录\\实验设备管理系统（广州医科大学生物医学工程学院）\\资料\\UML图设计\\设备管理系统流程图.jpg";
        emailUtils.sendFileMail("2786794141@qq.com", "发送HTML邮件测试", html, file);
    }
}
