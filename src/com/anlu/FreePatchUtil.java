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
	public static String patchFile = "D:/patch.txt";// �����ļ�,��eclipse svn
													// plugin����

	public static String projectPath = "E:/Project/ztbProject";// ��Ŀ�ļ���·��

	public static String webContent = "src/main/webapp";// webӦ���ļ�ǰ׺
	public static String classContent = "src/main/java";// class�ļ�·��ǰ׺
	public static String resourceContent = "src/main/resources"; // �����ļ�·��ǰ׺

	public static String classPath = "E:/Project/ztbProject/target/classes";// class���·��
	public static String resourcePath= "E:/Project/ztbProject/target/m2e-wtp/web-resources/WEB-INF/classes";//resource�Ĵ��·��

	public static String desPath = "D:/update_pkg";// �����ļ������·��

	public static String version = "20171203";// �����汾

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		copyFiles(getPatchFileList());
	}

	public static List<String> getPatchFileList() throws Exception {
		List<String> fileList = new ArrayList<String>();
		FileInputStream f = new FileInputStream(patchFile);
		BufferedReader dr = new BufferedReader(
				new InputStreamReader(f, "utf-8"));
		String line;
		while ((line = dr.readLine()) != null) {
			if (line.indexOf("Index:") != -1) {
				line = line.replaceAll(" ", "");
				line = line.substring(line.indexOf(":") + 1, line.length());
				fileList.add(line);
			}
		}
		return fileList;
	}

	public static void copyFiles(List<String> list) {

		for (String fullFileName : list) {
			if (fullFileName.indexOf(classContent) != -1) {// ��Դ�ļ�Ŀ¼�µ��ļ�����
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
				System.out.println(fullFileName + "�������");
			} else if (fullFileName.indexOf(resourceContent) != -1) {//����������ļ�
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
				System.out.println("�����ļ�:"+fullFileName + "�������");
			} else {// ����ͨĿ¼�Ĵ��� ��ͨĿ¼������̬jsp�ļ�,js,cssͼƬ�ȣ�Ҳ���������ļ�
				String desFileName = fullFileName.replaceAll(webContent, "");
				fullFileName = projectPath + "/" + fullFileName;// ��Ҫ���Ƶ��ļ�ȫ·��
				String fullDesFileNameStr = desPath + "/" + version
						+ desFileName;
				String desFilePathStr = fullDesFileNameStr.substring(0,
						fullDesFileNameStr.lastIndexOf("/"));
				File desFilePath = new File(desFilePathStr);
				if (!desFilePath.exists()) {
					desFilePath.mkdirs();
				}
				copyFile(fullFileName, fullDesFileNameStr);
				System.out.println(fullDesFileNameStr + "�������");
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
			// �½��ļ����������������л���
			inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

			// �½��ļ���������������л���
			outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

			// ��������
			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = inBuff.read(b)) != -1) {
				outBuff.write(b, 0, len);
			}
			// ˢ�´˻���������
			outBuff.flush();
		} finally {
			// �ر���
			if (inBuff != null)
				inBuff.close();
			if (outBuff != null)
				outBuff.close();
		}
	}
}
