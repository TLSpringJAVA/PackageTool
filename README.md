# PackageTool
增量打包的Demo



很多情况下，项目是不允许全量发布的，所以你得把有做修改的文件一个个挑出来，如果有成千上百的文件，你是不是要头大了? 前提是你是用装有svn plugin的eclipse上做开发。 这样减少了一些琐碎，重复，没有任何技术含量的工作了，避免开发人员的宝贵时间浪费在一个个挑增量文件的痛苦中。下面会介绍利用svn的增量补丁文件如何实现自动化增量打包的原理及实现方法



### 实现原理

讲简单点，主要包括几个步骤 
1、生成增量的补丁文件 
2、读补丁文件，遍历文件，如果是java文件，需要找到对应的class文件，其他文件如jsp,config配置文件 ，然后复制一份到指定的目标路径下即可



### 编写java代码实现

**1. 创建patch.txt增量文件**



![1](http://img.blog.csdn.net/20170908164052015?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQveGlvbmd5b3VxaWFuZw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)



![这里写图片描述](http://img.blog.csdn.net/20170908164305334?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQveGlvbmd5b3VxaWFuZw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)





**2. 编写java代码**

```
package com.xyq.maventest;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FreePatchUtil {


    public static String patchFile="E:\\workplaces\\EC_TAOBAO\\patch.txt";//补丁文件,由eclipse svn plugin生成  

    public static String projectPath="E:\\workplaces\\EC_TAOBAO";//项目文件夹路径  

    public static String webContent="WebContent";//web应用文件夹名  

    public static String classPath="E:\\workplaces\\EC_TAOBAO\\WebContent\\WEB-INF\\classes";//class存放路径  

    public static String desPath="C:\\Users\\youqiang.xiong\\Desktop\\update_pkg";//补丁文件包存放路径  

    public static String version="20170908";//补丁版本  


    /** 
     * @param args 
     * @throws Exception  
     */  
    public static void main(String[] args) throws Exception {  
        copyFiles(getPatchFileList());  
    }  

    /****
     * 读取补丁配置文件解析出修改的文件并返回到list集合
     * @return
     * @throws Exception
     */
    public static List<String> getPatchFileList() throws Exception{  
        List<String> fileList=new ArrayList<String>();  
        FileInputStream f = new FileInputStream(patchFile);   
        BufferedReader dr=new BufferedReader(new InputStreamReader(f,"utf-8"));  
        String line;  
        while((line=dr.readLine())!=null){   
            if(line.indexOf("Index:")!=-1){  
                line=line.replaceAll(" ","");  
                line=line.substring(line.indexOf(":")+1,line.length());  
                fileList.add(line);  
            }  
        }  
        dr.close();
        return fileList;  
    }  

    /***
     * 
     * @param list 修改的文件
     */
    public static void copyFiles(List<String> list){  

        for(String fullFileName:list){  
            if(fullFileName.indexOf("src/")!=-1){//对源文件目录下的文件处理  
                String fileName=fullFileName.replace("src","");  
                fullFileName=classPath+fileName;  
                if(fileName.endsWith(".java")){  
                    fileName=fileName.replace(".java",".class");  
                    fullFileName=fullFileName.replace(".java",".class");  
                }  
                String tempDesPath=fileName.substring(0,fileName.lastIndexOf("/"));  
                String desFilePathStr=desPath+"/"+version+"/WEB-INF/classes"+tempDesPath;  
                String desFileNameStr=desPath+"/"+version+"/WEB-INF/classes"+fileName;  
                File desFilePath=new File(desFilePathStr);  
                if(!desFilePath.exists()){  
                    desFilePath.mkdirs();  
                } 
                copyFile(fullFileName, desFileNameStr);  
                System.out.println(fullFileName+"复制完成");  
            }else{//对普通目录的处理  
                String desFileName=fullFileName.replaceAll(webContent,"");  
                fullFileName=projectPath+"/"+fullFileName;//将要复制的文件全路径  
                String fullDesFileNameStr=desPath+"/"+version+desFileName;  
                String desFilePathStr=fullDesFileNameStr.substring(0,fullDesFileNameStr.lastIndexOf("/"));  
                File desFilePath=new File(desFilePathStr);  
                if(!desFilePath.exists()){  
                    desFilePath.mkdirs();  
                }  
                copyFile(fullFileName, fullDesFileNameStr);  
                System.out.println(fullFileName+"复制完成");  

            }  

        }  

    }  



    private static void copyFile(String sourceFileNameStr, String desFileNameStr) {  
        File srcFile=new File(sourceFileNameStr);  
        File desFile=new File(desFileNameStr);  
        try {  
            copyFile(srcFile, desFile);  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  




    public static void copyFile(File sourceFile, File targetFile) throws IOException {  
        BufferedInputStream inBuff = null;  
        BufferedOutputStream outBuff = null;  
        try {  
            // 新建文件输入流并对它进行缓冲  
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));  

            // 新建文件输出流并对它进行缓冲  
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));  

            // 缓冲数组  
            byte[] b = new byte[1024 * 5];  
            int len;  
            while ((len = inBuff.read(b)) != -1) {  
                outBuff.write(b, 0, len);  
            }  
            // 刷新此缓冲的输出流  
            outBuff.flush();  
        } finally {  
            // 关闭流  
            if (inBuff != null)  
                inBuff.close();  
            if (outBuff != null)  
                outBuff.close();  
        }  
    }  
}
```



*这段配置根据实际情况改编*



```
public static String patchFile="E:\\workplaces\\EC_TAOBAO\\patch.txt";//补丁文件,由eclipse svn plugin生成  

    public static String projectPath="E:\\workplaces\\EC_TAOBAO";//项目文件夹路径  

    public static String webContent="WebContent";//web应用文件夹名  

    public static String classPath="E:\\workplaces\\EC_TAOBAO\\WebContent\\WEB-INF\\classes";//class存放路径  

    public static String desPath="C:\\Users\\youqiang.xiong\\Desktop\\update_pkg";//补丁文件包存放路径  

    public static String version="20170908";//补丁版本  
```

