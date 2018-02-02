package com.tmind.orpheus.util;
/**
 * @COPYRIGHT (C) 2018 Schenker AG
 * <p>
 * All rights reserved
 */


import com.musicg.fingerprint.FingerprintSimilarity;
import com.musicg.wave.Wave;
import com.sun.org.apache.xml.internal.security.utils.Base64;

import java.io.*;
import java.util.List;
import java.util.logging.Logger;

/**
 * TODO The class VoiceUtil is supposed to be documented...
 *
 * @author Vani Li
 */
public class VoiceUtil {

    public static String transferVoiceToWebm(InputStream inputStream, File saveFile) {
        try {
            String encoding = "utf-8";
            boolean base64Flag = false;
            int index = 0;
            InputStreamReader read = new InputStreamReader(inputStream, encoding);// 考虑到编码格式
            BufferedReader bufferedReader = new BufferedReader(read);
            StringBuilder lineTxt = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                lineTxt.append(line);
                if (index == 0 && line.contains("base64")) {
                    base64Flag = true;
                }
                index++;
            }
            read.close();

            String olddata = lineTxt.toString();
            if (base64Flag) {  //如果使用了base64加密
                olddata = olddata.replace("data:audio/webm;base64,", "");
            }
            try {
                byte[] bt = Base64.decode(olddata);
                FileOutputStream in = new FileOutputStream(saveFile);
                try {
                    in.write(bt, 0, bt.length);
                    in.close();
                    // boolean success=true;
                    // System.out.println("写入文件成功");
                }
                catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String fullPath = saveFile.getName();
            String prefix = fullPath.substring(0, fullPath.lastIndexOf("."));
            return executeTransWebmToWav(prefix);
        }
        catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
        return null;
    }

    private static String executeTransWebmToWav(String prefix) {
        Runtime run = Runtime.getRuntime();
        Process process = null;
        List<String> commands = new java.util.ArrayList<String>();
        commands.add(Constant.FFMPEG_PATH);
        commands.add("-i");
        commands.add(Constant.VOICE_PATH + prefix+".webm");
        commands.add(Constant.VOICE_PATH + prefix+".wav");
        ProcessBuilder builder = new ProcessBuilder();
        builder.command(commands);
        InputStream error = null;
        InputStream is = null;
        try {
            process = builder.start();
            //process.waitFor();//等待进程执行完毕
            //防止ffmpeg进程塞满缓存造成死锁
            error = process.getErrorStream();
            is = process.getInputStream();
            byte[] b = new byte[1024];
            int readbytes = -1;

            while ((readbytes = error.read(b)) != -1) {
//                System.out.println("FFMPEG转换失败");
            }
            while ((readbytes = is.read(b)) != -1) {
//                System.out.println("FFMPEG转换成功");
            }
            File voiceFile = new File(Constant.VOICE_PATH+prefix+".wav");
            if(voiceFile.exists()){
                String track1 = Constant.STANDARD_VOICE_PATH+"1.wav", track2 = voiceFile.getAbsolutePath();
                Wave wave1 = new Wave(track1), wave2 = new Wave(track2);

                FingerprintSimilarity similarity;

                // compare fingerprints:
                similarity = wave1.getFingerprintSimilarity(wave2);
                float result = similarity.getSimilarity();
                return String.valueOf(result*1000);
            }
        }
        catch (IOException e2) {

        }
        finally {
            try {
                error.close();
                is.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }
}