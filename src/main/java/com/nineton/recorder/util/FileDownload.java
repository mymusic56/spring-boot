package com.nineton.recorder.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileDownload {
	/**
	 * @功能 下载临时素材接口
	 * @param filePath
	 *            文件将要保存的目录
	 * @param method
	 *            请求方法，包括POST和GET
	 * @param url
	 *            请求的路径
	 * @return
	 */

	public static String saveUrlAs(String url, String filePath, String filename, String method) {
		
		String localFile = "";
		// System.out.println("fileName---->"+filePath);
		// 创建不同的文件夹目录
		File file = new File(filePath);
		// 判断文件夹是否存在
		if (!file.exists()) {
			// 如果文件夹不存在，则创建新的的文件夹
			file.mkdirs();
		}
		FileOutputStream fileOut = null;
		HttpURLConnection conn = null;
		InputStream inputStream = null;
		try {
			// 建立链接
			URL httpUrl = new URL(url);
			conn = (HttpURLConnection) httpUrl.openConnection();
			// 以Post方式提交表单，默认get方式
			conn.setRequestMethod(method);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			// post方式不能使用缓存
			conn.setUseCaches(false);
			conn.connect();
			// 获取网络输入流
			inputStream = conn.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(inputStream);
			// 判断文件的保存路径后面是否以/结尾
			if (!filePath.endsWith("/")) {

				filePath += "/";

			}
			// 写入到文件（注意文件保存路径的后面一定要加上文件的名称）
			fileOut = new FileOutputStream(filePath + filename);
			BufferedOutputStream bos = new BufferedOutputStream(fileOut);

			byte[] buf = new byte[4096];
			//把读出来的数据放到字节数组里面，并返回字节长度
			int length = bis.read(buf);
			// 保存文件
			while (length != -1) {
				bos.write(buf, 0, length);
				length = bis.read(buf);
			}
			bos.flush();
			bos.close();
			bis.close();
			conn.disconnect();
			localFile = filePath + filename;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("抛出异常！！");
		}

		return localFile;
	}
}
