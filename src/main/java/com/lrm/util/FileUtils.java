package com.lrm.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件控制类
 *
 * @author 山水夜止
 */
public class FileUtils {

    /**
     * @param file 被删除的文件
     */
    public static void deleteFile(File file) {
        //判断文件是否存在
        if (file.exists()) {
            //判断是否是文件
            if (file.isFile()) {
                //删除文件
                file.delete();
                //否则如果它是一个目录
            } else if (file.isDirectory()) {
                //声明目录下所有的文件 files[];
                File[] files = file.listFiles();
                //遍历目录下所有的文件
                for (File value : files) {
                    //把每个文件用这个方法进行迭代
                    deleteFile(value);
                }
                //删除文件夹
                file.delete();
            }
        }
    }


    /**
     * 上传文件
     *
     * @param file     文件
     * @param path     文件存放路径
     * @param fileName 原文件名
     * @return 新文件名/null
     */
    public static String upload(MultipartFile file, String path, String fileName) {

        // 生成新的文件和目录名
        String newFileName = getFileName(fileName);

        String realPath = path + "/" + newFileName;
        File folder = new File(realPath);

        try {
            //保存文件
            file.transferTo(folder);
            return newFileName;
        } catch (IOException e) {
            return null;
        }

    }

    /**
     * 获取文件后缀
     *
     * @param fileName 源文件名
     * @return 得到如.jpg .png的后缀
     */
    public static String getSuffix(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }

    /**
     * 生成新的文件名
     *
     * @param fileOriginName 源文件名
     * @return 新文件名
     */
    public static String getFileName(String fileOriginName) {
        return UUID.randomUUID() + getSuffix(fileOriginName);
    }

    /**
     * @param path 需要重新建立的文件夹路径
     */
    public static void rebuildFolder(String path) {
        //需要重建建立的文件夹
        File folder = new File(path);

        //如果文件夹不存在，创建文件夹 否则删除文件夹，然后再创建文件夹
        if (folder.exists()) {
            FileUtils.deleteFile(folder);
        }

        folder.mkdirs();
    }


    /**
     * @param path 需要建立的文件夹
     */
    public static void buildFolder(String path) {
        //需要建立的文件夹
        File folder = new File(path);

        folder.mkdirs();
    }
}
