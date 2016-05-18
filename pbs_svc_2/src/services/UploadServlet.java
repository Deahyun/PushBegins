package services;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.ibatis.session.SqlSession;
import org.imgscalr.Scalr;
import org.json.JSONArray;
import org.json.JSONObject;

import utils.UserLog;

import com.site.config.ConnectionFactory;
import com.site.dao.UserMapper;
import com.site.entity.RegisterApnsInfo;

/**
 * Servlet implementation class UploadServlet
 */
@WebServlet("/UploadServlet")
public class UploadServlet extends HttpServlet {
	boolean bWindowsOS;
	//
	final String strLinuxCertPath = "/home/duckking/res/certs";
	final String strWindowCertPath = "C:\\Root\\Certs\\PushBegins";
	//
	String strDebug;
	String tag = "UploadServlet";
	
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor. 
	 */
	public UploadServlet() {
		// TODO Auto-generated constructor stub
        String strData = System.getProperty("os.name");
    	String strOS = strData.toLowerCase();
    	if ( strOS.contains("windows") ) {
    		// windows os
    		bWindowsOS = true;
    	} else {
    		// other os. like linux
    		bWindowsOS = false;
    	}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		////
		File dirs = new File(request.getServletContext().getRealPath("/")+"imgs");
		if (!dirs.exists()) {
			dirs.mkdirs();
		}
		//
		
		if (request.getParameter("getfile") != null && !request.getParameter("getfile").isEmpty()) {
			//
			System.out.println("doGet getfile");
			//
			File file = new File(request.getServletContext().getRealPath("/")+"imgs/"+request.getParameter("getfile"));
			if (file.exists()) {
				int bytes = 0;
				ServletOutputStream op = response.getOutputStream();

				response.setContentType(getMimeType(file));
				response.setContentLength((int) file.length());
				response.setHeader( "Content-Disposition", "inline; filename=\"" + file.getName() + "\"" );

				byte[] bbuf = new byte[1024];
				DataInputStream in = new DataInputStream(new FileInputStream(file));

				while ((in != null) && ((bytes = in.read(bbuf)) != -1)) {
					op.write(bbuf, 0, bytes);
				}

				in.close();
				op.flush();
				op.close();
			}
			
		} else if (request.getParameter("delfile") != null && !request.getParameter("delfile").isEmpty()) {
			System.out.println("doGet delfile");
			//
			File file = new File(request.getServletContext().getRealPath("/")+"imgs/"+ request.getParameter("delfile"));
			if (file.exists()) {
				file.delete(); // TODO:check and report success
			}
			
		} else if (request.getParameter("getthumb") != null && !request.getParameter("getthumb").isEmpty()) {
			System.out.println("doGet getthumb");
			//
			File file = new File(request.getServletContext().getRealPath("/")+"imgs/"+request.getParameter("getthumb"));
			if (file.exists()) {
				System.out.println(file.getAbsolutePath());
				String mimetype = getMimeType(file);
				if (mimetype.endsWith("png") || mimetype.endsWith("jpeg")|| mimetype.endsWith("jpg") || mimetype.endsWith("gif")) {
					BufferedImage im = ImageIO.read(file);
					if (im != null) {
						BufferedImage thumb = Scalr.resize(im, 75); 
						ByteArrayOutputStream os = new ByteArrayOutputStream();
						if (mimetype.endsWith("png")) {
							ImageIO.write(thumb, "PNG" , os);
							response.setContentType("image/png");
						} else if (mimetype.endsWith("jpeg")) {
							ImageIO.write(thumb, "jpg" , os);
							response.setContentType("image/jpeg");
						} else if (mimetype.endsWith("jpg")) {
							ImageIO.write(thumb, "jpg" , os);
							response.setContentType("image/jpeg");
						} else {
							ImageIO.write(thumb, "GIF" , os);
							response.setContentType("image/gif");
						}
						ServletOutputStream srvos = response.getOutputStream();
						response.setContentLength(os.size());
						response.setHeader( "Content-Disposition", "inline; filename=\"" + file.getName() + "\"" );
						os.writeTo(srvos);
						srvos.flush();
						srvos.close();
					}
				}
			} // TODO: check and report success
		} else {
			PrintWriter writer = response.getWriter();
			//writer.write("call POST with multipart form data");
			writer.write("invalid access");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		if (!ServletFileUpload.isMultipartContent(request)) {
			throw new IllegalArgumentException("Request is not multipart, please 'multipart/form-data' enctype for your form.");
		}
		//
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		////
//		File dirs = new File(request.getServletContext().getRealPath("/")+"imgs");
//		if (!dirs.exists()) {
//			dirs.mkdirs();
//		}
		//
		//System.out.println(dirs);
		//System.out.println("doPost upload?");

		ServletFileUpload uploadHandler = new ServletFileUpload(new DiskFileItemFactory());
		PrintWriter writer = response.getWriter();
		response.setContentType("application/json");
		JSONArray json = new JSONArray();
		//
		//
		try {
			System.out.println(request);
			//
			List<FileItem> items = uploadHandler.parseRequest(request);
			//
			String strEmail = null;
			String strPushName = null;
			String strAppId = null;
			String strSandbox = null;
			String strCertPwd = null;
			//
			for ( FileItem item : items ) {
				// get post parameters
				if ( item.isFormField() ) {
					// post param
					String fieldName = item.getFieldName();
	                String fieldValue = item.getString();
	                //
	                UserLog.Log(tag,  fieldName + " : " + fieldValue);
	                //
	                if ( fieldName.equals("user_id") ) {
	                	strEmail = fieldValue;
	                }
	                if ( fieldName.equals("push_name") ) {
	                	strPushName = fieldValue;
	                }
	                if ( fieldName.equals("app_id") ) {
	                	strAppId = fieldValue;
	                }
	                if ( fieldName.equals("sandbox") ) {
	                	strSandbox = fieldValue;
	                }
	                if ( fieldName.equals("cert_pwd") ) {
	                	strCertPwd = fieldValue;
	                }
				}
			} // for
			
			//
			if ( strEmail == null || strEmail.length() == 0 ) {
				System.out.println("email -> null");
				errorReturn(writer);
				return;
			}
			if ( strPushName == null || strPushName.length() == 0 ) {
				System.out.println("push_name -> null");
				errorReturn(writer);
				return;
			}
			if ( strAppId == null || strAppId.length() == 0 ) {
				System.out.println("app_id -> null");
				errorReturn(writer);
				return;
			}
			if ( strSandbox == null || strSandbox.length() == 0 ) {
				System.out.println("sandbox -> null");
				errorReturn(writer);
				return;
			}
			if ( strCertPwd == null || strCertPwd.length() == 0 ) {
				System.out.println("cert_pwd -> null");
				errorReturn(writer);
				return;
			}
			//
			////
			int nUserSeq = 0;
			SqlSession sqlSession = ConnectionFactory.getSession().openSession();
			//
			try {
				UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
				nUserSeq = userMapper.getPushUserSeq(strEmail);
				
			} catch ( Exception e ) {
				UserLog.Log(tag, e.getMessage());
			} finally {
				sqlSession.close();
			}
			////
			
			if ( nUserSeq == 0 ) {
				System.out.println("invalid user -> " + strEmail);
				errorReturn(writer);
				return;
			}
			
			//
			// data_type 에 따라서 상황에 맞게 사용자 폴더가 존재하지 않으면 폴더를 만든다.
			//File dirs = new File(request.getServletContext().getRealPath("/"));
			
			String realFolder = null;
			if ( bWindowsOS ) {
				realFolder = strWindowCertPath;
			} else {
				realFolder = strLinuxCertPath;
			}
			String userFolder = realFolder + File.separator + nUserSeq;
			String strDbFolder = String.format("%d",  nUserSeq);
			//if ( strDataType.equals("video") ) {
			//	userFolder += File.separator + "video";
			//}
			File user_dirs = new File(userFolder);
			if ( !user_dirs.exists() ) user_dirs.mkdirs();
			//
			System.out.println("업로드 경로 -> " + user_dirs);
			//
			String strCertFile = null;
			//
			for ( FileItem item : items ) {
				// upload file
				if ( !item.isFormField() ) {
					File file = new File(user_dirs, item.getName());
					strCertFile = strDbFolder + File.separator + item.getName();
					System.out.println("업로드 파일 -> " + strCertFile);
					//
					long nSize = item.getSize();
					long nMaxSize = 10 * 1024 * 1024; // 10 MB
					if ( nSize > nMaxSize ) {
						System.out.println("max size limit");
						errorReturn(writer);
						return;
					}
					//
					item.write(file);
					//
					//JSONObject jsono = new JSONObject();
					if ( false ) {
					//jsono.put("name", item.getName());
					//jsono.put("size", item.getSize());
					//jsono.put("url", "UploadServlet?getfile=" + item.getName());
					//jsono.put("thumbnail_url", "UploadServlet?getthumb=" + item.getName());
					//jsono.put("delete_url", "UploadServlet?delfile=" + item.getName());
					//jsono.put("delete_type", "GET");
					}
					//
					// jquery 에서 돌려 받는 값을 생성한다.
					//
					//jsono.put("result", "yes");
					//
					//json.put(jsono);
					//
					//System.out.println(json.toString());
				}
			} // for
			
			// APNS 등록정보를 database 에 저장한다.
			// CertFile 유효성 확인
			if ( strCertFile == null  || strCertFile.length() < 5 ) {
				errorReturn(writer);
				return;
			}
			
			//
			if ( !saveAPNSInfo(nUserSeq, strPushName, strAppId, strSandbox, strCertFile, strCertPwd) ) {
				errorReturn(writer);
				return;
			}
			JSONObject jsono = new JSONObject();
			jsono.put("result", "yes");
			json.put(jsono);
			System.out.println(json.toString());
			
/*
$('#fileupload').fileupload({
        dataType: 'json',
        done: function (e, data) {
            var response = data.result[0];
            alert(response.name);
        }, ... 			
 */
		} catch (FileUploadException e) {
			throw new RuntimeException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			writer.write(json.toString());
			writer.close();
		}
	}

	////
	private String getMimeType(File file) {
		String mimetype = "";
		if (file.exists()) {
			if (getSuffix(file.getName()).equalsIgnoreCase("png")) {
				mimetype = "image/png";
			}else if(getSuffix(file.getName()).equalsIgnoreCase("jpg")){
				mimetype = "image/jpg";
			}else if(getSuffix(file.getName()).equalsIgnoreCase("jpeg")){
				mimetype = "image/jpeg";
			}else if(getSuffix(file.getName()).equalsIgnoreCase("gif")){
				mimetype = "image/gif";
			}else {
				javax.activation.MimetypesFileTypeMap mtMap = new javax.activation.MimetypesFileTypeMap();
				mimetype  = mtMap.getContentType(file);
			}
		}
		return mimetype;
	}

	private String getSuffix(String filename) {
		String suffix = "";
		int pos = filename.lastIndexOf('.');
		if (pos > 0 && pos < filename.length() - 1) {
			suffix = filename.substring(pos + 1);
		}
		return suffix;
	}
	
	//
	void errorReturn(PrintWriter writer) {
		JSONArray json = new JSONArray();
		JSONObject jo = new JSONObject();
		try {
			jo.put("result", "no");
			json.put(jo);
			
		} catch ( Exception e ) {
			UserLog.Log(tag, e.getMessage());
		} finally {
			writer.write(json.toString());
			writer.close();
		}
		//
	}
	
	//
	boolean saveAPNSInfo(int nUserSeq, String strPushName, String strAppId, String strSandbox, String strCertFile, String strCertPwd) {
		boolean bResult = false;
		
		//public void registerApnsPush(RegisterApnsInfo info);
		RegisterApnsInfo info = new RegisterApnsInfo();
		info.seq = nUserSeq;
		info.push_name = strPushName;
		info.app_id = strAppId;
		info.sandbox = strSandbox;
		info.cert_file_path = strCertFile;
		info.cert_pwd = strCertPwd;
		//
		SqlSession sqlSession = ConnectionFactory.getSession().openSession();
		//
		try {
			UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
			userMapper.registerApnsPush(info);
			sqlSession.commit();
			//
			bResult = true;
			
		} catch ( Exception e ) {
			sqlSession.rollback();
			UserLog.Log(tag, e.getMessage());
		} finally {
			sqlSession.close();
		}
		////
		return bResult;
	}
	
	////
}
