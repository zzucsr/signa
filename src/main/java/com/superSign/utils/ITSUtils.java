package com.superSign.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.superSign.config.Constant;
import com.superSign.entity.AppleEntity;
import com.superSign.entity.pojo.Authorize;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.security.ec.ECPrivateKeyImpl;

import java.security.InvalidKeyException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName ITSUtils
 * @Description TODO
 * @Author 陈思睿
 * @Date
 **/
public class ITSUtils {
    static Logger logger = LoggerFactory.getLogger(ITSUtils.class);

    public static String insertDevice(String udid, Authorize authorize) throws InvalidKeyException {
        Map body = new HashMap();
        body.put("type", "devices");
        Map attributes = new HashMap();
        attributes.put("name", udid);
        attributes.put("udid", udid);
        attributes.put("platform", "IOS");
        body.put("attributes", attributes);
        Map data = new HashMap();
        data.put("data", body);
        String url = "https://api.appstoreconnect.apple.com/v1/devices";
        //令牌信息
        String result = HttpRequest.post(url).
                addHeaders(getToken(authorize.getP8(), authorize.getIss(), authorize.getKid())).
                body(JSON.toJSONString(data)).execute().body();
        logger.info("udid:{}对应的开发者账号后台数据:{}",udid,result);
        Map map = JSON.parseObject(result, Map.class);
        JSONArray errors = (JSONArray)map.get(Constant.errors);
        if (errors != null) {
            return Constant.errors;
        }
        Map data1 = (Map) map.get("data");
        String id = (String)data1.get("id");
        return id;
    }

    static Map getToken(String p8, String iss, String kid) throws InvalidKeyException {
        String s = p8.
                replace("-----BEGIN PRIVATE KEY-----", "").
                replace("-----END PRIVATE KEY-----", "");
        byte[] encodeKey = Base64.decode(s);
        String token = token = Jwts.builder().
                setHeaderParam(JwsHeader.ALGORITHM, "ES256").
                setHeaderParam(JwsHeader.KEY_ID,kid).
                setHeaderParam(JwsHeader.TYPE, "JWT").
                setIssuer(iss).
                claim("exp", System.currentTimeMillis()/1000 +  60 * 10).
                setAudience("appstoreconnect-v1").
                signWith(SignatureAlgorithm.ES256, new ECPrivateKeyImpl(encodeKey)).
                compact();
        Map map = new HashMap();
        map.put("Content-Type", "application/json");
        map.put(Constant.Authorization, "Bearer " + token);
        return map;
    }

    /**
     * 创建个人资料
     * @param apple
     * @param devId
     * @return
     * @throws InvalidKeyException
     */
    public static String insertProfile(AppleEntity apple, String devId) throws InvalidKeyException {
        Map body = new HashMap();
        body.put("type", "profiles");
        String name = IdUtil.simpleUUID();
        Map attributes = new HashMap();
        attributes.put("name", name);
        attributes.put("profileType", "IOS_APP_ADHOC");
        body.put("attributes", attributes);
        Map relationships = new HashMap();
        Map bundleId = new HashMap();


        Map data2 = new HashMap();
        data2.put("id", apple.getBundleIds());
        data2.put("type", "bundleIds");
        bundleId.put("data", data2);
        relationships.put("bundleId", bundleId);
        Map certificates = new HashMap();
        Map data3 = new HashMap();
        data3.put("id", apple.getCerId());
        data3.put("type", "certificates");
        List list = new LinkedList();
        list.add(data3);
        certificates.put("data", list);
        relationships.put("certificates", certificates);
        Map devices = new HashMap();
        Map data4 = new HashMap();
        data4.put("id", devId);

        data4.put("type", "devices");
        List list2 = new LinkedList();
        list2.add(data4);
        devices.put("data", list2);
        relationships.put("devices", devices);
        body.put("relationships", relationships);
        Map data = new HashMap();
        data.put("data", body);
        String url = "https://api.appstoreconnect.apple.com/v1/profiles";
        String result = HttpRequest.post(url).
                addHeaders(getToken(apple.getP8(), apple.getIss(), apple.getKid())).
                body(JSON.toJSONString(data)).execute().body();
        logger.info("证书创建执行结果:{}",result);
        Map map = JSON.parseObject(result, Map.class);
        JSONArray errors = (JSONArray)map.get(Constant.errors);
        if (errors != null) {
            return Constant.errors;
        }
        Map o = (Map) map.get("data");
        Map o2 = (Map) o.get("attributes");
        String profileContent = (String) o2.get("profileContent");
        return profileContent;
    }

    public static Map getCerIdAndBundleIds(Authorize authorize,boolean bundleIdIsNull) throws InvalidKeyException {
        Map header = getToken(authorize.getP8(), authorize.getIss(), authorize.getKid());
        String result = isVailAppleInfo(header);
        Map map = JSON.parseObject(result, Map.class);
        Map res = new HashMap();
        JSONArray errors = (JSONArray)map.get(Constant.errors);
        if (errors!=null) {
            res.put("msg", "与苹果建立连接失败, 帐号无法使用或p8文件不正确：errors: " + result);
        }else {
            JSONArray data = (JSONArray)map.get("data");
            List devices = new LinkedList();
            for (Object datum : data) {
                Map device = new HashMap();
                Map m = (Map) datum;
                String id = (String)m.get("id");
                Map attributes = (Map)m.get("attributes");
                String udid = (String)attributes.get("udid");
                device.put("deviceId", id);
                device.put("udid", udid);
                devices.add(device);
            }
            Map <String, String>cerMap = findCertificates(header);
            if (cerMap.get("msg") != null) {
                res.put("msg", "获取cer文件失败: errors:" + cerMap.get("msg"));
            }else {
                String bundleIds;
                if (bundleIdIsNull) {
                    bundleIds = insertBundleIds(header);
                } else {
                    bundleIds = findBundleIds(header);
                }
                logger.info("bundleIds:{}",bundleIds);
                Map meta = (Map) map.get("meta");
                Map paging = (Map)meta.get("paging");
                int total = (int) paging.get("total");
                res.put("number", Constant.total-total);
                res.put("devices", devices);
                res.put("cerId", cerMap.get("id"));
                res.put("bundleIds", bundleIds);
                logger.info("res:{}",res);
            }
        }
        return res;
    }

    /**
     * 校验是否需
     * @param header
     * @return
     * @throws InvalidKeyException
     */
    static  String isVailAppleInfo(Map header) throws InvalidKeyException{
        String url = "https://api.appstoreconnect.apple.com/v1/devices";
        Map body = new HashMap();
        body.put("limit", 200);
        String result = HttpRequest.get(url).
                addHeaders(header).form(body).
                execute().body();
        return result;
    }

    static Map<String, String> findCertificates(Map header) {
        HashMap hashMap = new HashMap();
        String url = "https://api.appstoreconnect.apple.com/v1/certificates";
        String result = HttpRequest.
                get(url).
                addHeaders(header).
                execute().
                body();
        logger.info("当前证书内容:{}",result);
        Map map = JSON.parseObject(result, Map.class);
        JSONArray errors = (JSONArray)map.get(Constant.errors);
        if (errors != null) {
            logger.error("证书创建失败");
            hashMap.put("msg", result);
            return hashMap;
        }
        String id = null;
        JSONArray array = JSON.parseObject(result).getJSONArray("data");
        for (int i = 0 ; i < array.size(); i ++) {
            JSONObject obj = array.getJSONObject(i);
            if (JSON.parseObject(obj.get("attributes").toString()).get("certificateType").equals("DISTRIBUTION")) {
                id = (String)JSON.parseObject(result).getJSONArray("data").getJSONObject(i).get("id");
            }
        }
        hashMap.put("id", id);
        return hashMap;
    }




    static String insertBundleIds(Map header) {
        logger.info("开始创建bundleId");
        String url = "https://api.appstoreconnect.apple.com/v1/bundleIds";
        Map body = new HashMap();
        body.put("type", "bundleIds");
        Map attributes = new HashMap();
        attributes.put("identifier", "org.cocos2d.slots");
        attributes.put("name", "IOS Distribution");
        attributes.put("platform", "IOS");
        body.put("attributes", attributes);
        Map data = new HashMap();
        data.put("data", body);
        logger.info("data信息:{}",data);
        String result = HttpRequest.get(url).
                addHeaders(header).
                execute().
                body();
        /*String result = HttpRequest.post(url).
                addHeaders(header).
                body(JSON.toJSONString(data)).
                execute().body();*/
        logger.info("bundleId创建完:{}",result);
        Map map = JSON.parseObject(result, Map.class);
        Map data1 = (Map) map.get("data");
        String id = (String)data1.get("id");
        return id;
    }

    static String findBundleIds(Map header) {
        String url = "https://api.appstoreconnect.apple.com/v1/bundleIds";
        String id = null;
        String result = HttpRequest.get(url).
                addHeaders(header).
                execute().
                body();
        logger.info("获取到的bundleId:{}",result);
        JSONArray array = JSON.parseObject(result).getJSONArray("data");
        for (int i = 0 ; i < array.size(); i ++) {
            JSONObject obj = array.getJSONObject(i);
            if (JSON.parseObject(obj.get("attributes").toString()).get("identifier").equals("org.cocos2d.slots")) {
                id = (String)JSON.parseObject(result).getJSONArray("data").getJSONObject(i).get("id");
            }
        }
        return id;
    }

    public static void main(String [] args) {
        String p8 = "-----BEGIN PRIVATE KEY-----\n" +
                "MIGTAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBHkwdwIBAQQgbP5hP5h9ZG7awKZL\n" +
                "xGI8w4R8cL93rzJv7bZu7SB/5JCgCgYIKoZIzj0DAQehRANCAARPallDzlX93lvf\n" +
                "i4ew0LaMTIZXJ2Dn2boismuubwA+cGUz2aHTAQD0ffOPKRYAv/3e3E6tex7u49kT\n" +
                "p7u9cr9+\n" +
                "-----END PRIVATE KEY-----";
        String iss = "d7337f14-13f4-4d06-8239-1e00a40330c5";
        String kid = "NTP65R6TV4";
        try {
            Map header = getToken(p8,iss,kid);
            //String bundleId = insertBundleIds(header);
            String bundleId = findBundleIds(header);
            System.out.println(bundleId);
        } catch (Exception e) {

        }
    }
}
