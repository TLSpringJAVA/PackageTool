package com.anlu;

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
	public static String patchFile = "D:/changeLog.txt";// 补丁文件,由eclipse svn
	// plugin生成
	public static String pathPrefix = "M /06国网电商项目/02开发区/04实现/01代码/trunk/code/cgjy/";
	public static String ApathPrefixString = "A /06国网电商项目/02开发区/04实现/01代码/trunk/code/cgjy";

	public static String projectPath = "E:/Project/ztbProject";// 项目文件夹路径

	public static String webContent = "src/main/webapp";// web应用文件前缀
	public static String classContent = "src/main/java";// class文件路径前缀
	public static String resourceContent = "src/main/resources"; // 配置文件路径前缀
	public static String sqlContent = "src/main/sql";//sql文件的前缀

	public static String classPath = "E:/Project/ztbProject/target/classes";// class存放路径
	public static String resourcePath = "E:/Project/ztbProject/target/m2e-wtp/web-resources/WEB-INF/classes";// resource的存放路径

	public static String desPath = "D:/update_pkg";// 补丁文件包存放路径

	public static String version = "cgjy_20180404";// 补丁版本

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		copyFiles(getPatchFileList());
	}

	public static List<String> getPatchFileList() throws Exception {
		  String encoding="UTF-8"; 
		List<String> fileList = new ArrayList<String>();
		FileInputStream f = new FileInputStream(patchFile);
		BufferedReader dr = new BufferedReader(
				new InputStreamReader(f, encoding));
		String line;
		while ((line = dr.readLine()) != null) {
			if (line.indexOf(pathPrefix) != -1) {
				line = line.replaceAll(pathPrefix, "");
				line = line.trim();
				line = line.replace(" ", "");
				line = line.substring(line.indexOf(":") + 1, line.length());
				fileList.add(line);
			}else if(line.indexOf(ApathPrefixString)!=-1){
				line = line.replaceAll(ApathPrefixString, "");
				line = line.trim();
				line = line.replace(" ", "");
				line = line.substring(line.indexOf(":") + 1, line.length());
				fileList.add(line);
			}
		}
		return fileList;
	}

	public static void copyFiles(List<String> list) {
		for (String fullFileName : list) {
			if (fullFileName.indexOf(classContent) != -1) {// 对源文件目录下的文件处理
				String fileName = fullFileName.replace(classContent, "");
				fullFileName = classPath + fileName;
				if (fileName.endsWith(".java")) {
					fileName = fileName.replace(".java", ".class");
					fullFileName = fullFileName.replace(".java", ".class");
				}
				String tempDesPath = fileName.substring(0,
						fileName.lastIndexOf("/"));
				String desFilePathStr = desPath + "/" + version
						+ "/WEB-INF/classes" + tempDesPath;
				String desFileNameStr = desPath + "/" + version
						+ "/WEB-INF/classes" + fileName;
				File desFilePath = new File(desFilePathStr);
				if (!desFilePath.exists()) {
					desFilePath.mkdirs();
				}
				copyFile(fullFileName, desFileNameStr);
				System.out.println(fullFileName + "复制完成");
				
				  //遍历目录，是否存在内部类，如果有内部，则将所有的额内部类挑选出来放到
                copyInnerClassFile(fullFileName, desFileNameStr);
				
			} else if (fullFileName.indexOf(resourceContent) != -1) {// 如果是配置文件
				String fileName = fullFileName.replace(resourceContent, "");
				fullFileName = resourcePath + fileName;
				String tempDesPath = fileName.substring(0,
						fileName.lastIndexOf("/"));
				String desFilePathStr = desPath + "/" + version
						+ "/WEB-INF/classes" + tempDesPath;
				String desFileNameStr = desPath + "/" + version
						+ "/WEB-INF/classes" + fileName;
				File desFilePath = new File(desFilePathStr);
				if (!desFilePath.exists()) {
					desFilePath.mkdirs();
				}
				copyFile(fullFileName, desFileNameStr);
				System.out.println("配置文件:" + fullFileName + "复制完成");
			}
//			else if (fullFileName.indexOf(sqlContent) != -1) {// 如果是sql文件
//				String fileName = fullFileName.replace("/src/main", "");
//				fullFileName = projectPath  + fileName;
//				String tempDesPath = fileName.substring(0,
//						fileName.lastIndexOf("/"));
//				String desFilePathStr = desPath + "/" + version
//						+ "/WEB-INF/classes" + tempDesPath;
//				String desFileNameStr = desPath + "/" + version
//						+ "/WEB-INF/classes" + fileName;
//				File desFilePath = new File(desFilePathStr);
//				if (!desFilePath.exists()) {
//					desFilePath.mkdirs();
//				}
//				copyFile(fullFileName, desFileNameStr);
//				System.out.println("sql文件:" + fullFileName + "复制完成");
//			}
			else {// 对普通目录的处理 普通目录包含静态jsp文件,js,css图片等，也包括配置文件
				String desFileName = fullFileName.replaceAll(webContent, "");
				fullFileName = projectPath + "/" + fullFileName;// 将要复制的文件全路径
				String fullDesFileNameStr = desPath + "/" + version
						+ desFileName;
				String desFilePathStr = fullDesFileNameStr.substring(0,
						fullDesFileNameStr.lastIndexOf("/"));
				File desFilePath = new File(desFilePathStr);
				if (!desFilePath.exists()) {
					desFilePath.mkdirs();
				}
				copyFile(fullFileName, fullDesFileNameStr);
				System.out.println(fullDesFileNameStr + "复制完成");
			}

		}

	}
	
	/***
     * 处理内部类的情况
     * 解析源路径名称，遍历此文件路径下是否存在这个类的内部类
     * 内部类编译后的格式一般是 OuterClassName$InnerClassName.class
     * @param sourceFullFileName 原路径
     * @param desFullFileName 目标路径
     */
    private static void copyInnerClassFile(String sourceFullFileName,String desFullFileName){

        String sourceFileName = sourceFullFileName.substring(sourceFullFileName.lastIndexOf("/")+1);
        String sourcePackPath = sourceFullFileName.substring(0,sourceFullFileName.lastIndexOf("/"));
        String destPackPath = desFullFileName.substring(0,desFullFileName.lastIndexOf("/"));
        String tempFileName = sourceFileName.split("\\.")[0];
        File packFile = new File(sourcePackPath);
        if(packFile.isDirectory()){
            String[] listFiles = packFile.list();
            for(String fileName:listFiles){
                //可以采用正则表达式处理
                if(fileName.indexOf(tempFileName+"$")>-1 && fileName.endsWith(".class")){
                    String newSourceFullFileName = sourcePackPath+"/" +fileName;
                    String newDesFullFileName = destPackPath + "/" + fileName;
                    copyFile(newSourceFullFileName, newDesFullFileName);  
                    System.out.println(newSourceFullFileName+"复制完成");  
                }
            }
        }

    }

	private static void copyFile(String sourceFileNameStr, String desFileNameStr) {
		File srcFile = new File(sourceFileNameStr);
		File desFile = new File(desFileNameStr);
		try {
			copyFile(srcFile, desFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void copyFile(File sourceFile, File targetFile)
			throws IOException {
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
