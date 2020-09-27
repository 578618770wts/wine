package com.personal.wine.utils;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jsms.api.JSMSClient;
import cn.jsms.api.SendSMSResult;
import cn.jsms.api.common.SMSClient;
import cn.jsms.api.common.model.SMSPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ResourceBundle;

public class SendSMSUtil {

	protected static final Logger LOG = LoggerFactory.getLogger(SendSMSUtil.class);


	static ResourceBundle jiGuang = ResourceBundle.getBundle("JiGuang");
    
    private final static String appkey = jiGuang.getString("appKey");
 
    private final static String masterSecret =  jiGuang.getString("masterSecret");

    private static final String devKey = "b4c95b1fca232ada19eea761";
    private static final String devSecret = "b404133fd52ca46e0505ce60";;

    //发送手机验证码
    public static String sendSMSCode(String phone,int tempId) {
    	SMSClient client = new SMSClient(masterSecret, appkey);
    	SMSPayload payload = SMSPayload.newBuilder()
				.setMobileNumber(phone)
				.setTempId(tempId)
				.build();
    	String msg="";
    	try {
			SendSMSResult res = client.sendSMSCode(payload);
			msg = res.getMessageId();
            System.out.println(res.toString());
			LOG.info(res.toString());
		} catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
        }
    	return msg;
    }
    
    //验证手机验证码是否匹配
    public static boolean verify(String msgId,String code){
    	boolean b =false;
    	try {
			b = new JSMSClient(masterSecret, appkey).sendValidSMSCode(msgId,code).getIsValid();
		} catch (APIConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (APIRequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return b;
    }

}
