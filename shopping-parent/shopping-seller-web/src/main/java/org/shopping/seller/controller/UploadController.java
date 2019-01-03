package org.shopping.seller.controller;

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
    public @ResponseBody String uploadOK(@RequestParam("imgFile") CommonsMultipartFile[] files, HttpServletRequest request, Map<String, Object> model, HttpServletResponse response){
        JSONObject jb=new JSONObject();
        jb.put("error", 0);
        //文件保存目录路径
        
        //定义允许上传的文件扩展名
        HashMap<String, String> extMap = new HashMap<String, String>();
        extMap.put("image", "gif,jpg,jpeg,png,bmp");
        extMap.put("media", "swf,flv,mp3,wav,wma,wmv,mid,avi,mpg,asf,rm,rmvb");
        extMap.put("file", "doc,docx,xls,xlsx,ppt,htm,html,txt,zip,rar,gz,bz2");

        //最大文件大小
        long maxSize = 1024 * 1024 *2;
        if(!ServletFileUpload.isMultipartContent(request)){
            jb.put("error", 1);
            jb.put("message", "请选择文件");
            return jb.toJSONString();
        }
        String dirName  =request.getParameter("dir");
        if (dirName == null) {
            dirName = "image";
        }
        if(!extMap.containsKey(dirName)){
            jb.put("error", 1);
            jb.put("message", "目录名不正确");
            return jb.toJSONString();
        }
        try {
            if (files!=null&&files.length>0) {
                for (CommonsMultipartFile commonsMultipartFile : files) {
                	
                	String url=load(commonsMultipartFile);
                	if (url!=null) {
                		jb.put("error", 0);
                        jb.put("message", "上传成功！");
                        jb.put("url",url);
                        return jb.toJSONString();	
					}
                }
            }
        } catch (Exception e1) {
            jb.put("error", 1);
            jb.put("message", e1.getMessage());
            return jb.toJSONString();
        }
        return jb.toJSONString();
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
