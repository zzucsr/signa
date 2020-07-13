package com.superSign.controller;

import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ZipUtil;
import com.dd.plist.NSDictionary;
import com.dd.plist.PropertyListParser;
import com.superSign.config.Constant;
import com.superSign.entity.AppleEntity;
import com.superSign.entity.DeviceEntity;
import com.superSign.entity.pojo.Authorize;
import com.superSign.entity.pojo.Result;
import com.superSign.service.*;
import com.superSign.utils.AESUtils;
import com.superSign.utils.FileManager;
import com.superSign.utils.ITSUtils;
import com.superSign.utils.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName UDIDController
 * @Description TODO
 * @Author 陈思睿
 * @Date
 **/
@Controller
@RequestMapping("/udid")
public class UDIDController {
    private final static Logger logger = LoggerFactory.getLogger(UDIDController.class);

    @Value("{shell.command}")
    String shellCommand;
    @Autowired
    private FileManager fileManager;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private AppleService appleService;




    /**
     * 测试下载文件回调
     * @return
     */
    @RequestMapping(value = "/index")
    public String upload(@RequestParam(required = false) Integer type,@RequestParam(required = false)String encryptHex ,
                         @RequestParam(required = false) String downLoadUrl ,Model model) {
        model.addAttribute("type",type);
        model.addAttribute("encryptHex",encryptHex);
        model.addAttribute("downLoadUrl",downLoadUrl);
        return "upload";
    }


    /**
     * 配置文件获取udid
     * @param response
     * @param request
     */
    @RequestMapping(value = "/getUDID",method = RequestMethod.POST)
    public void getUDID(HttpServletResponse response, HttpServletRequest request) {
        response.setContentType("text/html;charset=UTF-8");
        request.getHeader(Constant.ua);
        String encryptHex = null;
        String udid = null;
        try{
            request.setCharacterEncoding("UTF-8");
            InputStream inputStream = request.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            StringBuilder stringBuilder = new StringBuilder();
            String buffer = null;
            while ((buffer = bufferedReader.readLine() )!= null ) {
                String str = new String(buffer.getBytes(),"gbk");
                stringBuilder.append(str);
            }
            String xml = stringBuilder.toString().substring(stringBuilder.toString().indexOf("<?xml"),stringBuilder.toString().indexOf("</plist>") + 8);
            NSDictionary pase = (NSDictionary) PropertyListParser.parse(xml.getBytes());
            udid = (String) pase.get("UDID").toJavaObject();
            encryptHex = AESUtils.encryptHex(udid);
            logger.info("当前设备uiid:{}",udid);
        } catch (Exception e) {
            logger.error("获取设备udid失败");
        }
        //需要进行重定向一下
        String redirect = "/udid/index?type=1&encryptHex=" + encryptHex;
        response.setHeader("Location", redirect);
        response.setStatus(301);
    }


    @RequestMapping(value = "/superSign")
    @ResponseBody
    public void superSign(String encryptHex, HttpServletResponse response) {
        String ipaPath = null;
        String udid = AESUtils.decryptHexStr(encryptHex);
        DeviceEntity deviceEntity = deviceService.queryDeviceByUdid(udid);
        if (deviceEntity == null) {
            //将设备绑定在开发者账号下
            AppleEntity appleEntity = appleService.queryUserNewAppleAccount();
            if (appleEntity == null) {
                logger.info("没有可用的开发者账号");
            } else {
                logger.info("将设备udid:{},绑定到appleId:{}账号下",udid,appleEntity);
                ipaPath = insertDevice(udid,appleEntity);
            }
        } else {
            //当前设备已经注册过
            AppleEntity appleEntity = appleService.queryAppleAccountById(deviceEntity.getAppId());
            logger.info("设备udid:{}对应的开发者账号信息:{}",udid,appleEntity.getBundleIds());
            File file = resignature(appleEntity,deviceEntity.getDeviceId());
            ipaPath = signIpa(file,appleEntity,udid);
        }
        String plistPath = software(ipaPath,"1","slots",udid);
        String location = Constant.domainName + plistPath;
        logger.info("item-servjces:{}",location);
        String redirect = "/udid/index?type=2&downLoadUrl=" + location;
        response.setHeader("Location", redirect);
        response.setStatus(301);
    }


   public String insertDevice(String udid, AppleEntity appleEntity) {
        String deviceId = null;
        String ipaPath = null;
        Authorize authorize = new Authorize(appleEntity.getP8(),appleEntity.getIss(),appleEntity.getKid());
        try{
            deviceId = ITSUtils.insertDevice(udid,authorize);
        } catch (Exception e) {
            logger.error("添加设备失败e:{}",e);
        }
        if (deviceId != null) {
            Map<String,String> map = null;
            logger.info("设备udid:{}添加成功,更新对应开发者账号剩余次数和设备信息", udid);
            boolean b = deviceService.insertDevice(udid,appleEntity.getId(),deviceId);
            appleService.updateDeviceCountById(appleEntity.getId());
            if (b) {
                map = getCerIdAndBundleIds(appleEntity);
                logger.info("获取开发者账号信息:{}",map);
                appleEntity.setBundleIds(map.get("bundleIds"));
                appleEntity.setCerId(map.get("cerId"));
                File file = resignature(appleEntity,deviceId);
                logger.info("profile文件地址{}",file.getAbsolutePath());
                //执行脚本命令
                ipaPath = signIpa(file,appleEntity,udid);
            }
        } else {
            logger.info("账号:{}不可用",appleEntity);
            boolean b = appleService.updateAppleIsUse(appleEntity.getId());
            logger.info("开始找新的账号");
            appleEntity = appleService.queryNewApple();
            if (appleEntity != null) {
                insertDevice(udid,appleEntity);
            }
        }
        if (appleEntity == null) {
            return null;
        }
        return ipaPath;
   }

   public Map getCerIdAndBundleIds( AppleEntity appleEntity) {
       Map res = new HashMap();
       Authorize authorize = new Authorize(appleEntity.getP8(),appleEntity.getIss(),appleEntity.getKid(),appleEntity.getCsr());
        try {
            res = ITSUtils.getCerIdAndBundleIds(authorize,appleEntity.getBundleIds() == null);
        } catch (Exception e) {
            logger.info("获取证书和bundleId错误:{}",e);
        }
        return res;

   }

   public File resignature(AppleEntity apple, String deviceId ) {
       File mobileprovision = null;
       String profile = null;
       try{
           profile = ITSUtils.insertProfile(apple,deviceId);
       } catch (Exception e) {
           logger.error("失败");
       }
       logger.info("证书内容{}：",profile);
       if (profile != null && !profile.equalsIgnoreCase(Constant.errors)) {
           logger.info("证书创建成功");
           mobileprovision = fileManager.base64ToFile(profile, System.getProperty("user.dir") + "/" +  deviceId + ".mobileprovision");

       }
       return mobileprovision;
   }

   public String signIpa(File mobileprovision ,AppleEntity appleEntity ,String udid) {
       Result result = new Result();
       String command ;
       File file = new File(Constant.outPutPath + udid + ".ipa");
       command = String.format(shellCommand,Constant.rootPath + mobileprovision.getName(),Constant.rootPath + file.getName());
       logger.info("shell命令:{}",command);
       try {
           Shell.run(command);
           result.setMsg(Constant.success);
       } catch (Exception e) {
           logger.info("设备uiid:{}签名失败e:{}",udid,e);
           result.setMsg(Constant.errors);
       }
       return file.getName();

   }

    public String software(String ipaUrl, String version, String title,String udid) {
        String id = "org.cocos2d.slots.";
        ipaUrl = Constant.domainName + ipaUrl;
        String plist = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>  \n" +
                "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">  \n" +
                "<plist version=\"1.0\">  \n" +
                "<dict>  \n" +
                "    <key>items</key>  \n" +
                "    <array>  \n" +
                "        <dict>  \n" +
                "            <key>assets</key>  \n" +
                "            <array>  \n" +
                "                <dict>  \n" +
                "                    <key>kind</key>  \n" +
                "                    <string>software-package</string>  \n" +
                "                    <key>url</key>  \n" +
                "                    <string>"+ ipaUrl +"</string>  \n" +
                "                </dict>  \n" +
                "            </array>  \n" +
                "            <key>metadata</key>  \n" +
                "            <dict>  \n" +
                "                <key>bundle-identifier</key>  \n" +
                "                <string>"+ id +"</string>  \n" +
                "                <key>bundle-version</key>  \n" +
                "                <string>" + version + "</string>  \n" +
                "                <key>kind</key>  \n" +
                "                <string>software</string>  \n" +
                "                <key>title</key>  \n" +
                "                <string>" + title + "</string>  \n" +
                "            </dict>  \n" +
                "        </dict>  \n" +
                "    </array>  \n" +
                "</dict>  \n" +
                "</plist> ";
        String filePath = Constant.outPutPath + "/" + udid +".plist";
        cn.hutool.core.io.file.FileWriter writer = new FileWriter(filePath);
        writer.write(plist);
        logger.info("文件地址{}",writer.getFile().getName());
        //需要将文件上传到文件服务器
        return writer.getFile().getName();
    }

}
