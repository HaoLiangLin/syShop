package com.jy2b.zxxfd;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20190711.SmsClient;
import com.tencentcloudapi.sms.v20190711.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20190711.models.SendSmsResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static com.jy2b.zxxfd.contants.SystemConstants.*;

@SpringBootTest
public class SendCodeTest {
    @Test
    void sendCode() {
        try {
            /* 必要步骤：
             * 实例化一个认证对象，入参需要传入腾讯云账户密钥对secretId，secretKey。
             * 这里采用的是从环境变量读取的方式，需要在环境变量中先设置这两个值。
             * 你也可以直接在代码中写死密钥对，但是小心不要将代码复制、上传或者分享给他人，
             * 以免泄露密钥对危及你的财产安全。
             * SecretId、SecretKey 查询: https://console.cloud.tencent.com/cam/capi */
            // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey
            Credential credential = new Credential(SECRET_ID, SECRET_KEY);
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("sms.tencentcloudapi.com");
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            SmsClient client = new SmsClient(credential, "ap-guangzhou", clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            SendSmsRequest req = new SendSmsRequest();

            // 短信应用ID
            req.setSmsSdkAppid(SDK_APP_ID);
            // 短信签名内容: 使用 UTF-8 编码，必须填写已审核通过的签名
            req.setSign(SIGN_NAME);
            // 短信签名内容: 使用 UTF-8 编码，必须填写已审核通过的签名
            req.setTemplateID(TEMPLATE_ID);

            // 模板参数: 模板参数的个数需要与 TemplateId 对应模板的变量个数保持一致，若无模板参数
            String[] templateParamSet = {"789999", "5"};
            req.setTemplateParamSet(templateParamSet);

            /* 下发手机号码，采用 E.164 标准，+[国家或地区码][手机号]
             * 示例如：+8613711112222， 其中前面有一个+号 ，86为国家码，13711112222为手机号，最多不要超过200个手机号 */
            String tel = "+8613071510830";
            String[] phoneNumberSet = {tel};
            req.setPhoneNumberSet(phoneNumberSet);

            // 返回的resp是一个SendSmsResponse的实例，与请求对象对应
            SendSmsResponse resp = client.SendSms(req);
            // 输出json格式的字符串回包
            System.out.println(SendSmsResponse.toJsonString(resp));

            /*
            {
                "SendStatusSet":[{
                    "SerialNo":"3369:302090257916792968412541083",
                    "PhoneNumber":"+8613071510830",
                    "Fee":1,
                    "SessionContext":"",
                    "Code":"Ok",
                    "Message":"send success",
                    "IsoCode":"CN"
                }],
                "RequestId":"b82bb18d-7a8e-4e66-b265-60f72f645f7d"}
             */
        } catch (TencentCloudSDKException e) {
            e.printStackTrace();
        }
    }
}
