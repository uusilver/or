package com.tmind.orpheus.rest;
/**
 * @COPYRIGHT (C) 2018 Schenker AG
 * <p>
 * All rights reserved
 */


import com.alibaba.fastjson.JSON;
import com.tmind.orpheus.model.ProunceTextResult;
import com.tmind.orpheus.model.VoiceUploadResult;
import com.tmind.orpheus.util.Constant;
import com.tmind.orpheus.util.VoiceUtil;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * TODO The class RestApi is supposed to be documented...
 *
 * @author Vani Li
 */
@RestController
@EnableAutoConfiguration
public class RestApi {
    private static final Logger logger = LoggerFactory.getLogger(RestApi.class);

    @GetMapping("/rest/getTextToPronounce/{username}/{pwd}/{textId}")
    public String getTextToPronounce(@PathVariable("username") String username,
                                                @PathVariable("pwd") String pwd,
                                                @PathVariable("textId") int textId,
                                                HttpServletRequest request){

        ProunceTextResult result = new ProunceTextResult();
        result.setId(compareAndSetTextId(textId));
        result.setText("你好");
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "/rest/upVoice")
    public String upVoice(@RequestPart(value = "voice", required = false) MultipartFile voiceFile, HttpServletRequest request, HttpServletResponse response) throws IOException, FileUploadException {
        //接收到的文件绑定到MultipartFile对象中
        if (!voiceFile.isEmpty()){  //如果文件不为空，那么将它存起来
//            String path=request.getServletContext().getRealPath("/images");  //接收的文件放在/images目录下，并获得文件系统目录
            //获取文件需要上传到的路径
            String userName = request.getParameter("requestUsername");
            String userPwd = request.getParameter("requestUserPwd");
            String currentId = request.getParameter("textId");

            String path = Constant.VOICE_PATH;
            String filename=UUID.randomUUID().toString()+".webm";//获取文件名
            File filepath=new File(path,filename);     //根据文件所在目录和文件名创建File对象
            if(!filepath.getParentFile().exists()){    //如果所在目录不存在，那么创建
                filepath.getParentFile().mkdirs();
            }
            String voiceRank = VoiceUtil.transferVoiceToWebm(voiceFile.getInputStream(), new File(path+File.separator+filename));
            //file.transferTo(filepath)                              //也可以用这条语句
            VoiceUploadResult result = new VoiceUploadResult();
            result.setStates(1);
            result.setMessage("success");
            if(voiceRank!=null) {
                result.setRank(voiceRank);
            }else {
                result.setRank("00.00");
            }
            result.setLevel("95%");
            return JSON.toJSONString(result);
        }else{
            return "error";
        }
    }

    private int compareAndSetTextId(int currentTextId){
        int count = currentTextId+1;
        return count>Constant.MAX_VOICE_COMPARE ?1:count;
    }
}
