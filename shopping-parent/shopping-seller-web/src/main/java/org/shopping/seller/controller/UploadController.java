package org.shopping.seller.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.shopping.common.Enumeration;
import org.shopping.common.FastDFSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.alibaba.fastjson.JSONObject;

import entity.Result;

/**
 * 文件上传
 *
 */
@RestController
@RequestMapping("/Upload")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UploadController {

	@Value("${FILE_SERVER_URL}")
	private String FILE_SERVER_URL;// 文件服务器地址

	@RequestMapping("/upload")
	public Result upload(MultipartFile file) {
		/*// 1、取文件的扩展名
		String originalFilename = file.getOriginalFilename();
		String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
		try {
			// 2、创建一个 FastDFS 的客户端
			FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fdfs_client.conf");
			// 3、执行上传处理
			String path = fastDFSClient.uploadFile(file.getBytes(), extName);
			// 4、拼接返回的 url 和 ip 地址，拼装成完整的 url
			String url = FILE_SERVER_URL + path;
			return new Result(Enumeration.CODE_SUCCESS, true, url);
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(Enumeration.CODE_SUCCESS, false, Enumeration.UPLOAD_FAIL);
		}*/
		String url=load(file);
		if (url==null) {
			return new Result(Enumeration.CODE_SUCCESS, false, Enumeration.UPLOAD_FAIL);
		}
		return new Result(Enumeration.CODE_SUCCESS, true, url);
	}
	
	
	@RequestMapping(value="uploadOK")
    public void   uploadOK(@RequestParam String callBackPath,@RequestParam("imgFile") MultipartFile file,HttpServletResponse response){
       String url=load(file);
       if (url!=null) {
    	   url =  "?error=0&url="+url;
		}else{
			 url =  "?error=1&message="+Enumeration.UPLOAD_FAIL;
		}
       try {
		response.sendRedirect(callBackPath+url);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    }
	
	
	private String load(MultipartFile file){
		String originalFilename = file.getOriginalFilename();
		String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
		try {
			// 2、创建一个 FastDFS 的客户端
			FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fdfs_client.conf");
			// 3、执行上传处理
			String path = fastDFSClient.uploadFile(file.getBytes(), extName);
			// 4、拼接返回的 url 和 ip 地址，拼装成完整的 url
			String url = FILE_SERVER_URL + path;
			return url;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
