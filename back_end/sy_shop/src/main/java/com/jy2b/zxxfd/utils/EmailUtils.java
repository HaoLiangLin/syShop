package com.jy2b.zxxfd.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

@Component
public class EmailUtils {
    private final Logger logger = LoggerFactory.getLogger(EmailUtils.class);

    @Resource
    private JavaMailSender sender;

    @Value("${spring.mail.username}")
    private String fromMail;

    /**
     * 发送普通邮件
     *
     * @param toMail 收件人
     * @param subject 标题
     * @param content 邮件内容
     */
    public void sendSimpleEmail(String toMail, String subject, String content) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(fromMail);
        simpleMailMessage.setTo(toMail);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(content);

        try {
            sender.send(simpleMailMessage);
            logger.info("发送给:{}简单邮件已发送==》subject:{}", toMail, subject);
        } catch (Exception e) {
            logger.info("发送给:{}send mail error subject:{}", toMail, subject);
            e.printStackTrace();
        }

    }

    /**
     * 发送带HTML格式邮件
     *
     * @param toMail 收件方
     * @param subject 标题
     * @param content 邮件内容
     */
    public void sendHtmlMail(String toMail, String subject, String content) {
        // 封装数据
        MimeMessage mimeMessage = sender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            mimeMessageHelper.setTo(toMail);
            mimeMessageHelper.setFrom(fromMail);
            mimeMessageHelper.setText(content, true);
            mimeMessageHelper.setSubject(subject);

            // 发送邮件
            sender.send(mimeMessage);
            logger.info("发送给:{}html邮件已发送==》subject:{}", toMail, subject);
        } catch (Exception e) {
            logger.info("发送给:{}html send mail error subject:{}", toMail, subject);
            e.printStackTrace();
        }
    }

    public void sendFileMail(String toMail, String subject, String content, String filePath) {

        // 编写发送的数据
        MimeMessage mimeMessage = sender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(fromMail);
            mimeMessageHelper.setTo(toMail);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(content, true);
            // 添加文件数据
            File file = new File(filePath);
            // 获取文件数据
            FileSystemResource fileSystemResource = new FileSystemResource(file);
            // 获取文件名称
            String fileName = file.getName();
            // 添加数据
            mimeMessageHelper.addAttachment(fileName, fileSystemResource);
            // 发送邮件
            sender.send(mimeMessage);
            logger.info("发送给:{}带附件的邮件已发送", toMail);
        } catch (Exception e) {
            logger.info("发送给:{}带附件的邮件时发生异常", toMail);
            e.printStackTrace();
        }
    }
}
