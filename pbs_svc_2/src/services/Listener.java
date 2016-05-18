package services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import network.APNSClient;
import network.APNS_Message;
import network.GCMClient;
import network.UserMail;

import org.apache.ibatis.session.SqlSession;
//import org.json.JSONArray;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import utils.RandomNumber;
import utils.UserFunc;
import utils.UserLog;

import com.site.config.ConnectionFactory;
import com.site.dao.UserMapper;
import com.site.entity.ApnsDeviceInfo;
import com.site.entity.CommonDeviceInfo;
import com.site.entity.CustomerMessageInfo;
import com.site.entity.DeviceIdInfo;
import com.site.entity.DeviceMapInfo;
import com.site.entity.DeviceTokenExInfo;
import com.site.entity.GcmDeviceInfo;
import com.site.entity.GroupWorkInfo;
import com.site.entity.PushMessageInfo;
import com.site.entity.PushRequest;
import com.site.entity.PushResponse;
import com.site.entity.RegisterApnsInfo;

/**
 * Servlet implementation class Listener
 */
@WebServlet("/Listener")
public class Listener extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	char szDeli = UserFunc.szDeli;
	String strDeli = UserFunc.strDeli;
	//
	boolean bWindowsOS;
	//
	// $ 안드로이드에서 원격으로 요청시 리턴되는 주소, 추후 주소가 변경될 경우 이 주소를 변경한다.
	//String strMyAddress = "http://www.pushbegins.com:18080/pushbegins_service/Listener";
	//String strMyAddress = "http://www.evinious.co.kr:8080/pushbegins_service/Listener";
	//String strMyAddress = "http://106.248.244.54:8080/pushbegins_service/Listener";
	//String strMyAddress = getBaseUrl();
	String strMyAddress = "some address";
	//
	final String strLinuxCertPath = "/home/duckking/res/certs";
	final String strWindowCertPath = "C:\\Root\\Certs\\PushBegins";
	//
	String strDebug;
	String tag = "Listener";

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Listener() {
        super();
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
    
    // config.prop 파일의 경로는 src 디렉토리에 있어야 한다.
    // 포팅후 서비스 가동시의 위치는  /var/lib/tomcat7/webapps/pushbegins_service/WEB-INF/classes 이다.
    String  getBaseUrl() {
    	String strResult = null;
    	
    	Properties prop = new Properties();
		try {
			String strPath = getClass().getClassLoader().getResource("/config.prop").getPath();
			//UserLog.Log(tag, strPath);
			//
			prop.load(new FileInputStream(strPath));
			//prop.load(Listener.class.getClassLoader().getResourceAsStream("config.prop"));
			//
			strResult = prop.getProperty("base_url");
 
        } catch ( Exception e ) {
            //e.printStackTrace();        	
        	String strDebug = String.format("%s\n%s", e.getStackTrace(), e.getMessage());
        	UserLog.Log(tag, strDebug);
        }
    	
		//UserLog.Log(tag, "getBaseUrl -> " + strResult );
    	return strResult;
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		process(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		process(request, response);
	}

	//////////
	//////////
	@SuppressWarnings("unchecked")
	void putEx(JSONObject res, String key, String value) {
		res.put(key, value);
	}
	
	@SuppressWarnings("unchecked")
	void putEx(JSONObject res, String key, int value) {
		res.put(key, value);
	}

	void errorReturn(JSONObject res, PrintWriter out, String errorCode, String errorMessage) {
		res.put("result", "no");
		res.put("error_code", errorCode);
		res.put("error_message", errorMessage);
		try {
			out.println(res);
			out.flush();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	// Non request/response functions
	public void removeTimeoutObject(ServletContext application) {

		Enumeration attrEnum = application.getAttributeNames();
		while (attrEnum.hasMoreElements()) {
			String name = (String) attrEnum.nextElement();
			if (name.length() > 4) {
				String cmp = name.substring(0, 4);
				if (cmp.compareTo("____") == 0) {
					try {
						Object value = application.getAttribute(name);
						String strData = (String) value;
						String[] arr = strData.split(":");
						String strNumber = arr[0];
						String strTime = arr[1];

						long now = System.currentTimeMillis();
						long nTime = now / 1000;
						String strCurrentTime = String.format("%d", nTime);

						//SimpleDateFormat df = new SimpleDateFormat("HHmm");
						//String strCurrentTime = df.format(new java.util.Date());

						// delete old than 10 minutes
						int nDelta = Integer.parseInt(strCurrentTime) - Integer.parseInt(strTime);
						if (nDelta > 600) {
							application.removeAttribute(name);
						}
						
					} catch (Exception error) {
						error.printStackTrace();
					}
				}
			}
		}
		// while
	}
	
	public boolean sendMail(ServletContext application, String email, String strProjNum) {
		//
		boolean bResult = false;
		//
		RandomNumber random = new RandomNumber();
		UserMail userMail = new UserMail();
		//
		long now = System.currentTimeMillis();
		long nTime = now / 1000;
		String strTime = String.format("%d", nTime);

		//SimpleDateFormat df = new SimpleDateFormat("HHmm");
		//String strTime = df.format(new java.util.Date());

		String strAppKey = "____" + email;
		String strRandom = random.getRandom();
		String strValue = strRandom + ":" + strTime;

		// application 에 저장되는 형식
		// [ ____이메일]     => [인증번호:시간숫자]
		// [____a@b.com] => [234567:123456789]
		application.setAttribute(strAppKey, strValue);

		//String strRandom = random.getRandom();
		//String contents = String.format(
		//		"인증코드: %s<br>\n이 코드값은 10분간 유효합니다.", strRandom);
		
		String strServiceAddress = "http://192.168.0.4:8080/minipush_service/email_validate.jsp";
				
		String contents = "<html><head></head><body>"
				+ "아래의 link 를 클릭해서 인증절차를 완료해 주세요<br>"
				+ "한번만 유효한 코드입니다.<br>"
				+ "1 시간 이내에 처리가 되지 않으면 앱을 삭제후 다시 설치 해주셔야 합니다.<br><br>"
				+ strServiceAddress +"?email="+email+"&"
				+ "validate_code="+strRandom+"&"
				+ "proj_num="+strProjNum + "<br>"
				+ "</body></html>";
		
		String res = userMail.sendMail(email, "설치확인 인증메일", contents);
		
		if ( res != null ) {
			if ( res.compareTo("yes") == 0 ) {
				bResult = true;
			}
		}
		
		return bResult;
	}
	
	// email_verify_req -> move to email_validate.jsp
	public boolean verifyEmail(ServletContext application, String email, String validate_code) {
		
		boolean bResult = false;
		
		//
		String strAppKey = "____" + email;
		Object objValue = application.getAttribute(strAppKey);
		if (objValue == null) {
			return false;
		}
		String strData = (String) objValue;
		String[] arr = strData.split(":");
		String strNumber = arr[0];
		String strTime = arr[1];
		//

		//
		if (strNumber.compareTo(validate_code) == 0) {
			application.removeAttribute(strAppKey);
			bResult = true;
		}
		//
		
		// 10분 동안 미사용 Application 항목 제거
		removeTimeoutObject(application);
		
		//
		return bResult;
	}
	
	/////
	public int get_push_user_seq(String strEmail) {
		//
		if ( strEmail == null || strEmail.length() == 0 )
			return 0;

		int nUserSeq = 0;	
		SqlSession sqlSession = ConnectionFactory.getSession().openSession();
		//
		try {
			UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
			nUserSeq = userMapper.getPushUserSeq(strEmail);

		} catch ( Exception e ) {

		} finally {
			sqlSession.close();
		}

		return nUserSeq;
	}
	
	long make_message_id() {
		return UserFunc.make_message_id();
	}
	
	boolean put_push_message(PushMessageInfo push_msg) {
		////////
//		// 메시지 전송하기 전 수신확인을 위한 message data 를 기록한다.
//		PushMessageInfo push_msg = new PushMessageInfo();
//		push_msg.m_id = make_message_id();
//		push_msg.push_name;
//		push_msg.group_id;
//		push_msg.seq = nUserSeq;
//		push_msg.total_cnt = vIosList.size();
//		push_msg.success_cnt = 0;
//		push_msg.message_type = "i";
//		push_msg.alert = msg.alert;
//		push_msg.user_data = msg.user_data;

		boolean bResult = false;
		////
		SqlSession sqlSession = ConnectionFactory.getSession().openSession();
		//
		try {
			UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
			userMapper.putMessage(push_msg);
			sqlSession.commit();
			bResult = true;

		} catch ( Exception e ) {
			sqlSession.rollback();
			UserLog.Log(tag, "put_push_message -> " + e.getMessage());
			//e.printStackTrace();
			//errorReturn(res, out, "9999", e.getMessage());
			//return null;

		} finally {
			sqlSession.close();
		}
		////////
		
		return bResult;
	}
	
	//
	boolean update_message_send_count(long message_id, int nSendCount) {
		boolean bResult = false;
		//
		if ( message_id == 0 || nSendCount == 0 )
			return bResult;

		SqlSession sqlSession = ConnectionFactory.getSession().openSession();
		//
		try {
			UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
			//
			userMapper.updateMessageSendCount(message_id, nSendCount);
			sqlSession.commit();
			bResult = true;
			
		} catch ( Exception e ) {
			sqlSession.rollback();

		} finally {
			sqlSession.close();
		}

		return true;
	}
	
	/////////////////////////////////////////////////////////// 
	// 개발중이나 미적용인 내용들...

//	// 안드로이드폰 / 아이폰 구분없이 값을 찾는다. 
//	ExtSiteDeviceInfo get_device_token_ex(String strSiteId) {
//		ExtSiteDeviceInfo info = new ExtSiteDeviceInfo();
//
//		if ( strSiteId == null || strSiteId.length() == 0 )
//			return info;
//		
//		SqlSession sqlSession = ConnectionFactory.getSession().openSession();
//		//
//		try {
//			UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
//			// 안드로이드폰이 더 많이 존재하므로 먼저 검색한다. 
//			info = userMapper.getDeviceTokenA(strSiteId);
//			if ( info.device_token == null || info.device_token.length() == 0 ) {
//				info = userMapper.getDeviceTokenI(strSiteId);
//			}
//
//		} catch ( Exception e ) {
//
//		} finally {
//			sqlSession.close();
//		}
//		
//		return info;
//	}
//	
	//
	List<String> get_ext_device_list_a(List<String> extIdArray) {
		List<String> lResult = new ArrayList<String>();
		
		SqlSession sqlSession = ConnectionFactory.getSession().openSession();
		//
		try {
			UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
			lResult = userMapper.getExtDeviceListA(extIdArray);

		} catch ( Exception e ) {

		} finally {
			sqlSession.close();
		}
		
		return lResult;
	}
	
	//
	List<String> get_ext_device_list_i(List<String> extIdArray) {
		List<String> lResult = new ArrayList<String>();
		
		SqlSession sqlSession = ConnectionFactory.getSession().openSession();
		//
		try {
			UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
			lResult = userMapper.getExtDeviceListI(extIdArray);

		} catch ( Exception e ) {

		} finally {
			sqlSession.close();
		}
		
		return lResult;
	}
	///////////////////////////////////////////////////////////
	
	CommonDeviceInfo get_device_token(String strExternalID) {
		//public CommonDeviceInfo getDeviceInfo(String strExternalID);
		//public String getDeviceToken(CommonDeviceInfo info);
		
		//
		CommonDeviceInfo info = new CommonDeviceInfo();
		CommonDeviceInfo infoRes = new CommonDeviceInfo();
		//
		SqlSession sqlSession = ConnectionFactory.getSession().openSession();
		//
		try {
			UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
			info = userMapper.getDeviceInfo(strExternalID);
			infoRes.device_type = info.device_type;
			infoRes.device_seq = info.device_seq;
			//UserLog.Log(tag, info.device_type + " / " + info.device_seq + " / " + info.device_token);
			//
			if ( info.device_type.equals("a") ) {
				infoRes.device_token = userMapper.getDeviceTokenA(info);
			} else if ( info.device_type.equals("i") ) {
				infoRes.device_token = userMapper.getDeviceTokenI(info);
			} else {
				// error
			}
			//strDeviceToken = userMapper.getDeviceToken(info);
			//UserLog.Log(tag, "token -> " + strDeviceToken);

		} catch ( Exception e ) {
			e.printStackTrace();
			
		} finally {
			sqlSession.close();
		}
		
		return infoRes;
	}
	
	//
	boolean V(String strData) {
		if ( strData == null || strData.length() <= 0 ) {
			return false;
		} else {
			return true;
		}
	}
	
	//
	PushResponse push_error(String error_code, String error_message) {
		PushResponse res = new PushResponse();
		//
		res.result = "no";
		res.error_code = error_code;
		res.erro_message = error_message;
		//
		return res;
	}

	////
	PushResponse send_gcm_push_req(PushRequest req) {
		PushResponse res = new PushResponse();
		res.result = "no";
		//res.error_code = "9999";
		//res.erro_message = "unknown error";
		//
		if ( !V(req.user_id) || !V(req.push_name) || !V(req.alert) || !V(req.user_data) ) {
			return push_error("1002", "some parameter invalid");
		}
		
		// external device_token list
		List<String> extDeviceTokenList = new ArrayList<String>();
		List<Integer> extDeviceSeqList = new ArrayList<Integer>();
		if ( req.token_list.size() > 0 ) {
			//extDeviceTokenList = get_ext_device_list_a(req.ext_id_list);
			extDeviceTokenList = req.token_list;
			extDeviceSeqList = req.seq_list;
		}
		
		//
		// 메시지 길이 유효성을 확인한다.
		int nUTF8LimitLength = 4000;
		try {
			//byte[] utf8_user_data = strJsonData.getBytes("UTF-8");
			byte[] utf8_user_data = req.user_data.getBytes("UTF-8");
			if ( utf8_user_data.length > nUTF8LimitLength ) {
				UserLog.Log(tag, "json_data has too long value");
				return push_error("2002", "json_data has too long value");
			}
		} catch ( Exception e ) {
			return push_error("9999", e.getMessage());
		}
		//
		int nUserSeq = get_push_user_seq(req.user_id);
		if ( nUserSeq == 0 )  {
			UserLog.Log(tag, "params failure.");
			return push_error("1003", "push user not found");
		}
		
		//
		Vector<String> vAndroidList = new Vector<String>();
		//
		try {
			// 보안상의 이유로 인증부분을 도입할 경우 
			//   user_seq 를 email(id)/pwd 의 인증으로 얻는 부분을 추가하면 된다.
			//int nUserSeq = 1;
			//String strPushName = "pushbegins";
			//
			String strApiKey = null;
			String strProjNum = null;
			List<String> deviceList = null;
			List<Integer> seqList = null;
			SqlSession sqlSession = ConnectionFactory.getSession().openSession();
			//
			try {
				UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
				//
				nUserSeq = userMapper.getPushUserSeq(req.user_id);
				//
				strApiKey = userMapper.getApiKey(nUserSeq, req.push_name);
				strProjNum = userMapper.getProjNum(nUserSeq, req.push_name);
				//
				//
				if ( extDeviceTokenList.size() > 0 ) {
					// 외부 id list 에서 device_token list 를 얻을 경우 -> 현재 사용중
					deviceList = extDeviceTokenList;
					//
					seqList = extDeviceSeqList;
					
				} else {
					// 내부 DB 에 등록된 device_token list 를 얻을 경우
					deviceList = userMapper.getAllGcmDeviceTokenList(strProjNum);
					// 내부 DB 값을 사용할 경우 seq list 얻는 추가 로직 필요
				}
				
			} catch ( Exception e ) {
				//sqlSession.rollback();
				return push_error("9999", e.getMessage());

			} finally {
				sqlSession.close();
			}
			
			//
			if ( nUserSeq == 0 ) {
				UserLog.Log(tag, "invalid user_seq.");
				return push_error("1004", "user information invalid");
			}
			
			// user_seq 에 연관된 사용자의 gcm push 전송건수는 여기서 계산할 수 있다.
			for ( int i = 0; i < deviceList.size(); i++ ) {
				vAndroidList.add(deviceList.get(i));
			}
			
			// Send GCM
			GCMClient gcm = new GCMClient();
			gcm.setApiKey(strApiKey);
			gcm.setDeviceList(vAndroidList);
			
			////////
			// 메시지 전송하기 전 수신확인을 위한 message data 를 기록한다.
			PushMessageInfo push_msg = new PushMessageInfo();
			push_msg.m_id = make_message_id();
			push_msg.seq = nUserSeq;
			push_msg.push_name = req.push_name;
			push_msg.group_seq = 0;
			push_msg.total_cnt = vAndroidList.size();
			push_msg.success_cnt = 0;
			push_msg.message_type = "a";
			push_msg.alert = req.alert;
			push_msg.user_data = req.user_data;
			////

			if ( !put_push_message(push_msg) ) {
				return push_error("1005", "record PushMessageInfo failure");
			}
			//
			JSONObject joData = new JSONObject();
			joData.put("alert", req.alert);
			joData.put("user_data", req.user_data);
			//
			String strJsonData = joData.toString();
			//
			int nSendCount = gcm.sendGCM(strJsonData, push_msg.m_id);
			//
			if ( nSendCount > 0 ) {
				if ( !update_message_send_count(push_msg.m_id, nSendCount) ) {
					UserLog.Log(tag, "update_message_send_count failure");
				}
				UserLog.Log(tag, "m_id: " + push_msg.m_id + " send: " + nSendCount);				
			}
			//
			res.result = "yes";
			res.error_code = "0000";
			res.erro_message = "";
			res.total_count = deviceList.size();
			res.send_count = nSendCount;		
			
		} catch (Exception e) {
			UserLog.Log(tag, "<<< JSP Block >>>");
			return push_error("8001", "db error");
		}

		return res;
	}
	
	PushResponse send_apns_push_req(PushRequest req) {
		////
		PushResponse res = new PushResponse();
		res.result = "no";
		//res.error_code = "9999";
		//res.erro_message = "unknown error";
		//
		if ( !V(req.user_id) || !V(req.push_name) || !V(req.alert) || !V(req.user_data) ) {
			return push_error("1002", "some parameter invalid");
		}
		
		// external device_token list
		List<String> extDeviceTokenList = new ArrayList<String>();
		if ( req.token_list.size() > 0 ) {
			extDeviceTokenList = get_ext_device_list_a(req.token_list);
		}
		
		//
		// 메시지 길이 유효성을 확인한다.
		int nUTF8LimitLength = 4000;
		try {
			//byte[] utf8_user_data = strJsonData.getBytes("UTF-8");
			byte[] utf8_user_data = req.user_data.getBytes("UTF-8");
			if ( utf8_user_data.length > nUTF8LimitLength ) {
				UserLog.Log(tag, "json_data has too long value");
				return push_error("2002", "json_data has too long value");
			}
		} catch ( Exception e ) {
			return push_error("9999", e.getMessage());
		}
		//
		int nUserSeq = get_push_user_seq(req.user_id);
		if ( nUserSeq == 0 )  {
			UserLog.Log(tag, "params failure.");
			return push_error("1003", "push user not found");
		}
		
		//
		Vector<String> vIosList = new Vector<String>();
		try {
			//
			String strAppId = null;
			List<String> deviceList = null;
			//
			RegisterApnsInfo info = null;
		
			SqlSession sqlSession = ConnectionFactory.getSession().openSession();
			//
			try {
				UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
				//
				nUserSeq = userMapper.getPushUserSeq(req.user_id);
				info = userMapper.getApnsCertInfo(nUserSeq, req.push_name);
				//
				if ( extDeviceTokenList.size() > 0 ) {
					// 외부 id list 에서 device_token list 를 얻을 경우
					deviceList = extDeviceTokenList;
				} else {
					// 내부 DB 에 등록된 device_token list 를 얻을 경우
					deviceList = userMapper.getAllApnsDeviceTokenList(info.app_id);
				}
				
			} catch ( Exception e ) {
				return push_error("9999", e.getMessage());

			} finally {
				sqlSession.close();
			}
			
			//
			if ( nUserSeq == 0 ) {
				UserLog.Log(tag, "invalid user_seq.");
				return push_error("1004", "user information invalid");
			}
			
			////
			// user_seq 에 연관된 사용자의 apns push 전송건수는 여기서 계산할 수 있다.
			for ( int i = 0; i < deviceList.size(); i++ ) {
				vIosList.add(deviceList.get(i));
				//UserLog.Log(tag, "token: " + deviceList.get(i));
			}
				
			// For APNS
			String strCertFile = null;
			String strBasicPath = null;
			String strCertPwd = info.cert_pwd;
			// 인증서를 저장할 local 경로의 prefix
			if ( bWindowsOS ) {
				strBasicPath = strWindowCertPath;
			} else {
				strBasicPath = strLinuxCertPath;
			}
			strCertFile = strBasicPath + File.separator + info.cert_file_path;
			UserLog.Log(tag, "cert file -> " + strCertFile);
			
			// Send APNS
			APNSClient apns = new APNSClient();
			if ( info.sandbox.equals("N") ) {
				apns.setSandboxMode(false);
			} else if  ( info.sandbox.equals("Y") ) {
				apns.setSandboxMode(true);
			} else {
				apns.setSandboxMode(true);
				UserLog.Log(tag, "get sandbox mode failure");
			}
			apns.setCertPath(strCertFile);
			apns.setPwd(strCertPwd);
			//

			APNS_Message msg = new APNS_Message();
			msg.alert = req.alert;
			//
			try {
				msg.badge = Integer.parseInt(req.badge);
			} catch ( Exception e ) {
				msg.badge = 0;
			}
			//
			try {
				msg.sound = req.sound;
			} catch ( Exception e ) {
				msg.sound = null;
			}
			//
			try {
				msg.user_data = req.user_data;
			} catch ( Exception e ) {
				msg.user_data = null;
			}

			
			////////
			// 메시지 전송하기 전 수신확인을 위한 message data 를 기록한다.
			PushMessageInfo push_msg = new PushMessageInfo();
			push_msg.m_id = make_message_id();
			push_msg.seq = nUserSeq;
			push_msg.push_name = req.push_name;
			push_msg.group_seq = 0;
			push_msg.total_cnt = vIosList.size();
			push_msg.success_cnt = 0;
			push_msg.message_type = "i";
			push_msg.alert = msg.alert;
			push_msg.user_data = msg.user_data;
			//
			
			if ( !put_push_message(push_msg) ) {
				return push_error("2004", "record PushMessageInfo failure");
			}
			////////

			//
			msg.m_id = push_msg.m_id;
			//
			int nSendCount = apns.push_multi(msg, vIosList);
			//UserLog.Log(tag, "total: " + vIosList.size() + " send_cnt: " + nSendCount);

			//
			if ( nSendCount > 0 ) {
				if ( !update_message_send_count(push_msg.m_id, nSendCount) ) {
					UserLog.Log(tag, "update_message_send_count failure");
				}
				//UserLog.Log(tag, "m_id: " + push_msg.m_id + " send: " + nSendCount);				
			}
			
			// 총 메시지와 전송 성공 메시지의 갯수가 다를 경우
			if ( false ) {
			if ( nSendCount < deviceList.size() ) {
				Vector<String> vResult = apns.feedback();
				for ( int i = 0; i < vResult.size(); i++ ) {
					UserLog.Log(tag, "feedback token: " + vResult.get(i));
				}
			}
			}

			//
			res.result = "yes";
			res.error_code = "0000";
			res.erro_message = "";
			res.total_count = deviceList.size();
			res.send_count = nSendCount;

		} catch (Exception e) {
			return push_error("8001", "db error");
		}

		return res;
	}
	
	////
	// external link push send API: 1:1 push
	PushResponse send_link_push_req(String strExternalID, String strAlert, String strMessage) {
		//
		CommonDeviceInfo info = get_device_token(strExternalID);
		if ( info == null ) {
			return null;
		}
		
		// site_id 가 없을 경우의 처리
		if ( info.device_type == null || info.device_type.length() == 0 ) {
			UserLog.Log(tag, "외부 id 와 연결되는 정보가 없습니다.");
			return null;
		}
		
		//
		PushRequest req = new PushRequest();
		req.token_list.add(info.device_token);
		
		// pushbegins 사이트에 등록될 user_id 와 push_name 으로 변경하면 처리된다.
		//	안드로이드 및 아이폰 따로 따로 처리가 되나, push_name 을 같게 하면
		//	push_name 의 이름이 같이 등록될 수도 있다.
		if ( info.device_type.equals("a") ) {
			//req.user_id = "teraget@gmail.com";
			//req.push_name = "pushbegins";
			req.user_id = "kimdg04@evinious.co.kr";
			req.push_name = "iteacher";
			req.alert = strAlert;
			req.user_data = strMessage;
			//
			PushResponse pr = send_gcm_push_req(req);
			//UserLog.Log(tag, pr.result);
			return pr;
			
		} else if ( info.device_type.equals("i") ) {
			//req.user_id = "teraget@gmail.com";
			//req.push_name = "pushbegins-dist";
			req.user_id = "kimdg04@evinious.co.kr";
			// $ 스토어에 등록시 개발버젼(iteacher-develop)에서 배포버젼(iteacher)으로 변경하면 된다.
			req.push_name = "iteacher";
			//req.push_name = "iteacher-develop";
			req.alert = strAlert;
			req.user_data = strMessage;
			//req.badge = "1";
			//req.sound = "default";
			//
			PushResponse pr = send_apns_push_req(req);
			//UserLog.Log(tag, pr.result);
			return pr;
			
		} else {
			return null;
		}
	}
	
	////
	List<DeviceTokenExInfo> getPushGroupDeviceTokenExList(String strGroupId) {
		//
		List<DeviceTokenExInfo> liResult = new ArrayList<DeviceTokenExInfo>();
		//
		SqlSession sqlSession = ConnectionFactory.getSession().openSession();
		//
		try {
			UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
			//
			liResult = userMapper.getPushGroupDeviceTokenExList(strGroupId);
			//
			
		} catch ( Exception e ) {
			return null;

		} finally {
			sqlSession.close();
		}
		
		return liResult;
	}
	
	List<DeviceTokenExInfo> getGcmGroupDeviceTokenExList(String strGroupId) {
		//
		List<DeviceTokenExInfo> liResult = new ArrayList<DeviceTokenExInfo>();
		//
		SqlSession sqlSession = ConnectionFactory.getSession().openSession();
		//
		try {
			UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
			//
			liResult = userMapper.getGcmGroupDeviceTokenExList(strGroupId);
			//
			
		} catch ( Exception e ) {
			return null;

		} finally {
			sqlSession.close();
		}
		
		return liResult;
	}
	
	List<DeviceTokenExInfo> getApnsGroupDeviceTokenExList(String strGroupId) {
		//
		List<DeviceTokenExInfo> liResult = new ArrayList<DeviceTokenExInfo>();
		//
		SqlSession sqlSession = ConnectionFactory.getSession().openSession();
		//
		try {
			UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
			//
			liResult = userMapper.getApnsGroupDeviceTokenExList(strGroupId);
			//
			
		} catch ( Exception e ) {
			return null;

		} finally {
			sqlSession.close();
		}
		
		return liResult;
	}
	
	
//	////
//	boolean createGcmGroupId(GroupWorkInfo info) {
//		boolean bResult = false;
//		SqlSession sqlSession = ConnectionFactory.getSession().openSession();
//		//
//		try {
//			UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
//			userMapper.createGcmGroupId(info);
//			sqlSession.commit();
//			bResult = true;
//			//
//			
//		} catch ( Exception e ) {
//			sqlSession.rollback();
//			UserLog.Log(tag, e.getMessage());
//
//		} finally {
//			sqlSession.close();
//		}
//		
//		return bResult;
//	}
//	
//	//
//	boolean updateGcmGroupData(GroupWorkInfo info) {
//		boolean bResult = false;
//		SqlSession sqlSession = ConnectionFactory.getSession().openSession();
//		//
//		try {
//			UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
//			userMapper.updateGcmGroupData(info);
//			sqlSession.commit();
//			bResult = true;
//			//
//			
//		} catch ( Exception e ) {
//			sqlSession.rollback();
//			UserLog.Log(tag, e.getMessage());
//
//		} finally {
//			sqlSession.close();
//		}
//		
//		return bResult;
//	}
//	
//	//
//	boolean deleteGcmGroupId(GroupWorkInfo info) {
//		boolean bResult = false;
//		SqlSession sqlSession = ConnectionFactory.getSession().openSession();
//		//
//		try {
//			UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
//			userMapper.deleteGcmGroupId(info);
//			sqlSession.commit();
//			bResult = true;
//			//
//			
//		} catch ( Exception e ) {
//			sqlSession.rollback();
//			UserLog.Log(tag, e.getMessage());
//
//		} finally {
//			sqlSession.close();
//		}
//		
//		return bResult;
//	}
//	
//	// info 에 있는 내용을 존재하는 group_id 에 add 한다.
//	boolean addGcmGroupData(GroupWorkInfo info) {
//		
//		return true;
//	}
//	
//	// info 에 있는 내용을 존재하는 group_id 에서 remove 한다.
//	boolean removeGcmGroupData(GroupWorkInfo info) {
//		
//		return true;
//	}
//	
//	////
//	boolean createApnsGroupId(GroupWorkInfo info) {
//		boolean bResult = false;
//		SqlSession sqlSession = ConnectionFactory.getSession().openSession();
//		//
//		try {
//			UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
//			userMapper.createApnsGroupId(info);
//			sqlSession.commit();
//			bResult = true;
//			//
//			
//		} catch ( Exception e ) {
//			sqlSession.rollback();
//			UserLog.Log(tag, e.getMessage());
//
//		} finally {
//			sqlSession.close();
//		}
//		
//		return bResult;
//	}
//	
//	//
//	boolean updateApnsGroupData(GroupWorkInfo info) {
//		boolean bResult = false;
//		SqlSession sqlSession = ConnectionFactory.getSession().openSession();
//		//
//		try {
//			UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
//			userMapper.updateApnsGroupData(info);
//			sqlSession.commit();
//			bResult = true;
//			//
//			
//		} catch ( Exception e ) {
//			sqlSession.rollback();
//			UserLog.Log(tag, e.getMessage());
//
//		} finally {
//			sqlSession.close();
//		}
//		
//		return bResult;
//	}
//	
//	//
//	boolean deleteApnsGroupId(GroupWorkInfo info) {
//		boolean bResult = false;
//		SqlSession sqlSession = ConnectionFactory.getSession().openSession();
//		//
//		try {
//			UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
//			userMapper.deleteApnsGroupId(info);
//			sqlSession.commit();
//			bResult = true;
//			//
//			
//		} catch ( Exception e ) {
//			sqlSession.rollback();
//			UserLog.Log(tag, e.getMessage());
//
//		} finally {
//			sqlSession.close();
//		}
//		
//		return bResult;
//	}
//	
//	////
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		//
		PrintWriter out = response.getWriter();
		//
		String type = request.getContentType();
		int len = request.getContentLength();

		ServletInputStream in = request.getInputStream();
		StringBuilder sb = new StringBuilder();

		// read data 4 kbytes with loop
		byte[] buf = new byte[4096];
		int nToRead = buf.length;
		int n = 0;

		while ( (n = in.read(buf, 0, nToRead)) > 0 ) {
			sb.append(new String(buf, 0, n));
		}
		//
		if ( true )
			UserLog.Log(tag, sb.toString());
		//
		//  json request -> {"io_kind":"test_req"}
		JSONObject jo = (JSONObject) JSONValue.parse(sb.toString());
		//
		// json response
		JSONObject res = new JSONObject();
		//	
		String strIoKind = null;
		String strResIoKind = null;
		//
		try {
			strIoKind = jo.get("io_kind").toString();

		} catch ( Exception e ) {
			UserLog.Log(tag, "io_kind not found");
			out.println("io_kind not found");
			out.flush();
			return;
		}
		//
		if ( strIoKind == null || strIoKind.length() == 0 ) {
			out.println("No io_kind");
			return;
		}
		//
		strResIoKind = strIoKind.replace("_req", "_res");
		putEx(res, "io_kind", strResIoKind);
		//
		if ( strIoKind.compareTo("test_req") == 0 ) {
			res = test_req(request, response, out, jo, res);
			if ( res == null )
				return;
			
		} else if ( strIoKind.compareTo("test_gcm_req") == 0 ) {
			res = test_gcm_req(request, response, out, jo, res);
			if ( res == null )
				return;			
			
		} else if ( strIoKind.compareTo("get_android_address_req") == 0 ) {
			res = get_android_address_req(request, response, out, jo, res);
			if ( res == null )
				return;
			
		} else if ( strIoKind.compareTo("get_ios_address_req") == 0 ) {
			res = get_ios_address_req(request, response, out, jo, res);
			if ( res == null )
				return;
			
		} else if ( strIoKind.compareTo("update_gcm_device_info_req") == 0 ) {
			res = update_gcm_device_info_req(request, response, out, jo, res);
			if ( res == null )
				return;
						
		} else if ( strIoKind.compareTo("get_gcm_user_seq_req") == 0 ) {
			res = get_gcm_user_seq_req(request, response, out, jo, res);
			if ( res == null )
				return;
			
		} else if ( strIoKind.compareTo("login_gcm_user_req") == 0 ) {
			res = login_gcm_user_req(request, response, out, jo, res);
			if ( res == null )
				return;
			
		} else if ( strIoKind.compareTo("email_verify_req") == 0 ) {
			res = email_verify_req(request, response, out, jo, res);
			if ( res == null )
				return;
						
		} else if ( strIoKind.compareTo("send_gcm_all_push_req") == 0 ) {
			res = send_gcm_all_push_req(request, response, out, jo, res);
			if ( res == null )
				return;

		} else if ( strIoKind.compareTo("send_push_req") == 0 ) {
			res = send_push_req(request, response, out, jo, res);
			if ( res == null )
				return;
			
		} else if ( strIoKind.compareTo("send_group_push_req") == 0 ) {
			res = send_group_push_req(request, response, out, jo, res);
			if ( res == null )
				return;
			
		} else if ( strIoKind.compareTo("send_gcm_group_push_req") == 0 ) {
			res = send_gcm_group_push_req(request, response, out, jo, res);
			if ( res == null )
				return;
			
		} else if ( strIoKind.compareTo("gcm_group_token_work_req") == 0 ) {
			res = gcm_group_token_work_req(request, response, out, jo, res);
			if ( res == null )
				return;
			
		} else if ( strIoKind.compareTo("gcm_group_id_work_req") == 0 ) {
			res = gcm_group_id_work_req(request, response, out, jo, res);
			if ( res == null )
				return;
			
		} else if ( strIoKind.compareTo("gcm_group_id_list_req") == 0 ) {
			res = gcm_group_id_list_req(request, response, out, jo, res);
			if ( res == null )
				return;
			
		
		} else if ( strIoKind.compareTo("get_gcm_device_list_req") == 0 ) {
			res = get_gcm_device_list_req(request, response, out, jo, res);
			if ( res == null )
				return;
			
		} else if ( strIoKind.compareTo("update_apns_device_info_req") == 0 ) {
			res = update_apns_device_info_req(request, response, out, jo, res);
			if ( res == null )
				return;
			
		} else if ( strIoKind.compareTo("send_apns_all_push_req") == 0 ) {
			res = send_apns_all_push_req(request, response, out, jo, res);
			if ( res == null )
				return;
			
		} else if ( strIoKind.compareTo("verify_message_id_req") == 0 ) {
			res = verify_message_id_req(request, response, out, jo, res);
			if ( res == null )
				return;
			
		} else if ( strIoKind.compareTo("confirm_message_req") == 0 ) {
			res = confirm_message_req(request, response, out, jo, res);
			if ( res == null )
				return;
			
		} else if ( strIoKind.compareTo("send_customer_message_a_req") == 0 ) {
			res = send_customer_message_a_req(request, response, out, jo, res);
			if ( res == null )
				return;
			
		} else if ( strIoKind.compareTo("send_customer_message_i_req") == 0 ) {
			res = send_customer_message_i_req(request, response, out, jo, res);
			if ( res == null )
				return;
			
		} else if ( strIoKind.compareTo("send_email_req") == 0 ) {
			res = send_email_req(request, response, out, jo, res);
			if ( res == null )
				return;
			
		//////////
		//
		// external link
		//
		} else if ( strIoKind.compareTo("send_ext_push_req") == 0 ) {
			res = send_ext_push_req(request, response, out, jo, res);
			if ( res == null )
				return;
			
//		} else if ( strIoKind.compareTo("delete_ext_id_req") == 0 ) {
//			res = delete_ext_id_req(request, response, out, jo, res);
//			if ( res == null )
//				return;
			
		//////////
		} else {
			UserLog.Log(tag, "<<< Invalid request -> no available request >>>");
			errorReturn(res, out, "9999", "Invalid request");
			return;
		}
		//
		out.print(res);
		out.flush();
		////////
	}
	//////////
	
	////////////////////////////////////////////////////////////////////////////////////////////
	//
	public JSONObject test_req(HttpServletRequest request, HttpServletResponse response, 
			PrintWriter out, JSONObject jo, JSONObject jsres) throws ServletException, IOException {

		JSONObject res = jsres;
		String strParam = null;
		try {
			strParam = jo.get("param").toString();
			//
			PushResponse pr = send_link_push_req("demo2", "제목임", "실제 내용");
			UserLog.Log(tag, pr.result + " / total: " + pr.total_count + " / send: " + pr.send_count);
			

			
			if ( true ) return res;

			//
			ArrayList arr1 = new ArrayList();
			ArrayList arr2 = new ArrayList();
			//
			arr2.add("common1");
			arr2.add("common2");
			arr2.add("notcommon");
			arr2.add("notcommon1");
			//
			arr1.add("common1");
			arr1.add("common2");
			arr1.add("notcommon2");
			//
			UserLog.Log(tag, arr1.toString());
			UserLog.Log(tag, arr2.toString());
			//arr1.removeAll(arr2);
			//UserLog.Log(tag, "after remove -> " + arr1.toString());
			
			arr1.addAll(arr2);
			UserLog.Log(tag, "after add -> " + arr1.toString());
			//
			HashSet<String> set1 = new HashSet<String>();
			//set1
			set1.addAll(arr1);
			//
			ArrayList arr3 = new ArrayList<String>(set1);
			UserLog.Log(tag, "after add -> " + arr3.toString());
			//
			//Collections.sort(arr3);
			//UserLog.Log(tag, "after add -> " + arr3.toString());
			//
			//Collections.reverse(arr3);
			//UserLog.Log(tag, "after add -> " + arr3.toString());

		} catch ( Exception e ) {
			UserLog.Log(tag, "<<< json param Block -> some json param not found >>>");
			errorReturn(res, out, "9001", "json param not found");
			return null;
		}

		putEx(res, "result", "yes");
		putEx(res, "param", "[ " + strParam + " ]");

		UserLog.Log(tag, res.toString());

		return res;
	}
	
	public JSONObject test_gcm_req(HttpServletRequest request, HttpServletResponse response, 
			PrintWriter out, JSONObject jo, JSONObject jsres) throws ServletException, IOException {

		JSONObject res = jsres;
		String strMsgId = null;
		String strJsonData = null;
		try {
			//strMsgId = jo.get("msg_id").toString();
			strJsonData = jo.get("json_data").toString();

		} catch (Exception e) {
			UserLog.Log(tag, "<<< json param Block -> some json param not found >>>");
			errorReturn(res, out, "9001", "some json param not found");
			return null;
		}
		
		if ( strJsonData == null || strJsonData.length() == 0 ) {
			UserLog.Log(tag, "get message failure.");
			errorReturn(res, out, "1001", "No messages");
			return null;
		}

		//
		Vector<String> vIosList = new Vector<String>();
		Vector<String> vAndroidList = new Vector<String>();
		//
		//UserDAO db = new UserDAO();
		try {
//			Vector<DeviceInfo> vInfo = db.getUserList();
//			//
//			for ( int i = 0; i < vInfo.size(); i++ ) {
//				if ( vInfo.get(i).device_type.compareTo("i") == 0  ) {
//					vIosList.add(vInfo.get(i).device_token);
//					
//				} else if ( vInfo.get(i).device_type.compareTo("a") == 0  ) {
//					vAndroidList.add(vInfo.get(i).device_token);
//				}
//			}
			
			if ( false ) {			
			// For APNS
			String certPath = null;
			//String password = "smart2345";
			String password = "Tmakxm2345";
			if ( bWindowsOS ) {
				//certPath = "C:\\Root\\Certs\\SmartPush\\smart_push_dev.p12";
				//certPath = "C:\\Root\\Certs\\SmartPush\\smart_push_dist.p12";
				certPath = "C:\\Root\\Certs\\SmartPush\\smart-think-dist.p12";
			} else {
				//certPath = "/home/duckking/certs/smart_push_dev.p12";
				//certPath = "/home/duckking/certs/smart_push_dist.p12";
				certPath = "/home/duckking/certs/smart-think-dist.p12";
			}
			
			// Send APNS
			APNSClient apns = new APNSClient();
			apns.setSandboxMode(true);
			apns.setCertPath(certPath);
			apns.setPwd(password);
			//
			apns.push_multi(strMsgId, strJsonData, vIosList);
			//
			Vector<String> vResult = apns.feedback();
			for ( int i = 0; i < vResult.size(); i++ ) {
				UserLog.Log(tag, "feedback token: " + vResult.get(i));
			}
			}
			
			
			//
			// For GCM
			//String strApiKey = "AIzaSyBmjK-xcYLhLQsNJNzszVbU9uCGmZmiRNY";
			//
			//String strDeviceToken = "APA91bFS3q3TAvla7kar7KIQeiSrwDjtWUUecZeIUJ_HB6EV8MVQaHwLqY9-kNhTtJM5Jh3A5vLegWi-6qbDuo4iRbgMJQ6xqocV6UWI1LUrkPa3i1aUndci7qjwlvAjyLLXFDMig_XdqogzlESqrTnF7QwcPi3uzIfGyGs16YEK_FKl2V4TFHU";
			//vAndroidList.add(strDeviceToken);
			////
			//public String getApiKey(@Param("user_seq") int nUserSeq, @Param("push_name") String strPushName);
			//public String getGcmDeviceTokenList(String strPushName);
			//
			
			// 보안상의 이유로 인증부분을 도입할 경우 
			//   email/pwd 에서 user_seq 를 얻는 부분을 추가하면 된다.
			int nUserSeq = 1;
			String strPushName = "mini-push";
			//
			String strApiKey = null;
			String strProjNum = null;
			List<String> deviceList = null;
			
//			SqlSession sqlSession = ConnectionFactory.getSession().openSession();
//			//
//			try {
//				UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
//				//
//				strApiKey = userMapper.getApiKey(nUserSeq, strPushName);
//				strProjNum = userMapper.getApiKey(nUserSeq, strPushName);
//				//
//				deviceList = userMapper.getAllGcmDeviceTokenList(strProjNum);
//				//sqlSession.commit();
//				
//			} catch ( Exception e ) {
//				//sqlSession.rollback();
//				errorReturn(res, out, "9999", e.getMessage());
//				return null;
//
//			} finally {
//				sqlSession.close();
//			}
//			
//			////
//			// user_seq 에 연관된 사용자의 gcm push 전송건수는 여기서 계산할 수 있다.
//			for ( int i = 0; i < deviceList.size(); i++ ) {
//				vAndroidList.add(deviceList.get(i));
//			}
			
			//APA91bEiPKlo7Fxn0F596Z_q8KC32XeIRaX_DbvNYQb4_OuiDfSABTrogsWHUL5nV7lEayIM5M_JgWPcM94sz_MiGtePro7Bno4iyHpyl3VMs97z7SX07LyNURHwKjUcKno6v4os4QT8aal65a4Z7HNkZsj9goHM3_Bagnigj5p2XumT_p4buwQ

			
			//
			strApiKey = "AIzaSyDLOjbrjAVQv3ewnblr3f0-7EnnxB14maw";
			String device_token = "APA91bEiPKlo7Fxn0F596Z_q8KC32XeIRaX_DbvNYQb4_OuiDfSABTrogsWHUL5nV7lEayIM5M_JgWPcM94sz_MiGtePro7Bno4iyHpyl3VMs97z7SX07LyNURHwKjUcKno6v4os4QT8aal65a4Z7HNkZsj9goHM3_Bagnigj5p2XumT_p4buwQ";
			vAndroidList.add(device_token);
			
			// Send GCM
			GCMClient gcm = new GCMClient();
			gcm.setApiKey(strApiKey);
			gcm.setDeviceList(vAndroidList);
			//gcm.sendGCM(strMsgId, strJsonData);
			gcm.sendGCM(strJsonData, 1);
			
//			JSONObject jo2 = new JSONObject();
//			jo2.put("msg_id", "1234");
//			jo2.put("message", "test push message from minipush service");
//			gcm.sendGCM(jo2.toString());
			
			//
			putEx(res, "result", "yes");

		} catch (Exception e) {
			UserLog.Log(tag, "<<< JSP Block >>>");
			e.printStackTrace();
			errorReturn(res, out, "8001", "db error");
			return null;
		}

		UserLog.Log(tag, res.toString());
		return res;
	}
	
	// get_android_address_req
	public JSONObject get_android_address_req(HttpServletRequest request, HttpServletResponse response, 
			PrintWriter out, JSONObject jo, JSONObject jsres) throws ServletException, IOException {

		JSONObject res = jsres;
		String proj_num = null;
		//
		try {
			proj_num = jo.get("proj_num").toString();

		} catch ( Exception e ) {
			//e.printStackTrace();
			UserLog.Log(tag, "<<< json param Block -> some json param not found >>>");
			errorReturn(res, out, "9001", "json param not found");
			return null;
		}
		
		//
		SqlSession sqlSession = ConnectionFactory.getSession().openSession();
		//
		try {
			// 서버의 주소를 master 서버의 db 에서 얻어오는 코드 추가예정.
			UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
			
		} catch ( Exception e ) {
			errorReturn(res, out, "9999", e.getMessage());
			return null;

		} finally {
			sqlSession.close();
		}
		
		putEx(res, "result", "yes");
		putEx(res, "address", strMyAddress);
		//
		return res;
	}
	
	// get_ios_address_req
	public JSONObject get_ios_address_req(HttpServletRequest request, HttpServletResponse response, 
			PrintWriter out, JSONObject jo, JSONObject jsres) throws ServletException, IOException {

		JSONObject res = jsres;
		String app_id = null;
		//
		try {
			app_id = jo.get("app_id").toString();

		} catch ( Exception e ) {
			//e.printStackTrace();
			UserLog.Log(tag, "<<< json param Block -> some json param not found >>>");
			errorReturn(res, out, "9001", "json param not found");
			return null;
		}

		SqlSession sqlSession = ConnectionFactory.getSession().openSession();
		//
		try {
			// 서버의 주소를 master 서버의 db 에서 얻어오는 코드 추가예정.
			UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

		} catch ( Exception e ) {
			errorReturn(res, out, "9999", e.getMessage());
			return null;

		} finally {
			sqlSession.close();
		}

		putEx(res, "result", "yes");
		putEx(res, "address", strMyAddress);
		//
		return res;
	}
	
	// register or update gcm device information
	public JSONObject update_gcm_device_info_req(HttpServletRequest request, HttpServletResponse response, 
			PrintWriter out, JSONObject jo, JSONObject jsres) throws ServletException, IOException {

		JSONObject res = jsres;
		GcmDeviceInfo info = new GcmDeviceInfo();
		int nDeviceSeq = 0;
		//
		try {
			// device_id 는 email, phone, cid 중 하나
			//info.id_kind = jo.get("id_kind").toString();
			String cid = jo.get("device_id").toString();
			info.device_token = jo.get("device_token").toString();
			info.proj_num = jo.get("proj_num").toString();
			//
			info.device_id = "a" + "|" + info.proj_num + "|" + cid;

		} catch ( Exception e ) {
			//e.printStackTrace();
			UserLog.Log(tag, "<<< json param Block -> some json param not found >>>");
			errorReturn(res, out, "9001", "json param not found");
			return null;
		}

		SqlSession sqlSession = ConnectionFactory.getSession().openSession();
		//
		try {
			UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
			// site_id 는 iPhone 에서 이미 사용중일 수 있으므로, 
			// tb_gcm_device 에서만 검색해서는 통합처리를 할 수가 없다.
			// tb_device_map 에서 site_id 존재여부를 확인후 진행해야 정상적인 처리가 가능하다.
			
			// 아이폰, 안드로이드가 하나의 table 에서 관리된다.
			
			//nDeviceSeq = userMapper.getGcmDeviceSeq(info.device_token);
			//UserLog.Log(tag, "update_gcm_device_info_req::getGcmDeviceSeq -> " + nDeviceSeq);
			int nSeq1 = userMapper.getGcmDeviceSeq(info.device_token);
			int nSeq2 = userMapper.getGcmDeviceSeq2(info.device_id);
			//if ( nDeviceSeq == 0 ) {
			if ( nSeq1 == 0 && nSeq2 == 0 ) {
				// 등록되지 않은 gcm device 의 처리.
				//userMapper.deleteGcmDevice(info.device_token);
				userMapper.insertGcmDevice(info);
				//sqlSession.commit();
				nDeviceSeq = userMapper.getGcmDeviceSeq(info.device_token);
				UserLog.Log(tag, "getGcmDeviceSeq -> " + nDeviceSeq + " / " + info.proj_num);
				
				//
				//DeviceMapInfo dm_info = userMapper.getGcmAppInfo(info.proj_num);
				
				// device_id == "a/i" + proj_num/app_id + cid 
				// 안드로이드/아이폰 에서 하나의 ID 만 허용할 경우는
				// device_id 에서 cid 부분을 unique 로 동작하게 처리해서 사용한다.
				DeviceMapInfo dm_info = new DeviceMapInfo();
				dm_info.device_id = info.device_id;
				dm_info.device_seq = nDeviceSeq;
				//
				userMapper.insertDeviceMap(dm_info);
				sqlSession.commit();
				//UserLog.Log(tag, "insert done");
				//
//				String[] arrItem = info.device_id.split("\\|");
//				if ( arrItem.length == 3 ) {
//					dm_info.app_info_id = arrItem[1];
//				}
				
			} else { // nDeviceSeq != 0
				// 이미 등록된 gcm device 의 처리.
				// device_token 보다 device_id 의 우선순위가 더 높게 정의한다.
				if ( nSeq1 != 0 ) {
					nDeviceSeq = nSeq1;
				}
				if ( nSeq2 != 0 ) {
					nDeviceSeq = nSeq2;
				}
				info.seq = nDeviceSeq;
				userMapper.updateGcmDevice(info);
				
				//
				// 안드로이드/아이폰 에서 하나의 ID 만 허용할 경우는
				// device_id 에서 cid 부분을 unique 로 동작하게 처리해서 사용한다.
				DeviceMapInfo dm_info = new DeviceMapInfo();
				dm_info.device_id = info.device_id;
				dm_info.device_seq = nDeviceSeq;
				//
				int bHasDeviceMap = userMapper.hasDeviceMap(dm_info);
				if ( bHasDeviceMap == 0 ) {
					userMapper.insertDeviceMap(dm_info);
				}
				sqlSession.commit();
				//UserLog.Log(tag, "update done");
			}
			//
			
		} catch ( Exception e ) {
			sqlSession.rollback();
			UserLog.Log(tag, e.getMessage());
			errorReturn(res, out, "9999", e.getMessage());
			return null;

		} finally {
			sqlSession.close();
		}
		
		putEx(res, "result", "yes");
		String strDeviceSeq = String.format("%d",  nDeviceSeq);
		putEx(res, "device_seq", strDeviceSeq);
		//
		UserLog.Log(tag, res.toString());

		return res;
	}
	
//	//send_gcm_validate_email
//	public JSONObject send_gcm_validate_email(HttpServletRequest request, HttpServletResponse response, 
//			PrintWriter out, JSONObject jo, JSONObject jsres) throws ServletException, IOException {
//
//		JSONObject res = jsres;
//		GcmUserInfo info = new GcmUserInfo();
//		int nUserSeq = 0;
//		//
//		try {
//			info.email = jo.get("email").toString();
//			info.proj_num = jo.get("proj_num").toString();
//
//		} catch ( Exception e ) {
//			UserLog.Log(tag, "<<< json param Block -> some json param not found >>>");
//			errorReturn(res, out, "9001", "json param not found");
//			return null;
//		}
//		
//		//
//		ServletContext sc = getServletContext();
//		if ( !sendMail(sc, info.email, info.proj_num) ) {
//			UserLog.Log(tag, "send validation email failure");
//		}
//		
//		putEx(res, "result", "yes");
//		String strUserSeq = String.format("%d",  nUserSeq);
//		putEx(res, "user_seq", strUserSeq);
//		//
//		UserLog.Log(tag, res.toString());
//
//		return res;
//	}
	
	//get_gcm_user_seq_req
	//
	public JSONObject get_gcm_user_seq_req(HttpServletRequest request, HttpServletResponse response, 
			PrintWriter out, JSONObject jo, JSONObject jsres) throws ServletException, IOException {

		JSONObject res = jsres;
		String email = null;
		int nUserSeq = 0;
		//
		try {
			email = jo.get("email").toString();

		} catch ( Exception e ) {
			UserLog.Log(tag, "<<< json param Block -> some json param not found >>>");
			errorReturn(res, out, "9001", "json param not found");
			return null;
		}
		
		SqlSession sqlSession = ConnectionFactory.getSession().openSession();
		//
		try {
			UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
			nUserSeq = userMapper.getUserSeq(email);
			
		} catch ( Exception e ) {
			sqlSession.rollback();
			errorReturn(res, out, "9999", e.getMessage());
			return null;

		} finally {
			sqlSession.close();
		}
		
		putEx(res, "result", "yes");
		putEx(res, "user_seq", nUserSeq);

		return res;
	}
	
	//login_gcm_user_req
	public JSONObject login_gcm_user_req(HttpServletRequest request, HttpServletResponse response, 
			PrintWriter out, JSONObject jo, JSONObject jsres) throws ServletException, IOException {

		JSONObject res = jsres;
		String email = null;
		//
		try {
			email = jo.get("email").toString();

		} catch ( Exception e ) {
			UserLog.Log(tag, "<<< json param Block -> some json param not found >>>");
			errorReturn(res, out, "9001", "json param not found");
			return null;
		}
		
		SqlSession sqlSession = ConnectionFactory.getSession().openSession();
		//
		try {
			UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
			userMapper.loginGcmUser(email);
			sqlSession.commit();
			
		} catch ( Exception e ) {
			sqlSession.rollback();
			errorReturn(res, out, "9999", e.getMessage());
			return null;

		} finally {
			sqlSession.close();
		}
		
		putEx(res, "result", "yes");

		return res;
	}
	
	// email_verify_req -> move to email_validate.jsp
	public JSONObject email_verify_req(HttpServletRequest request, HttpServletResponse response, 
			PrintWriter out, JSONObject jo, JSONObject jsres) throws ServletException, IOException {

		JSONObject res = jsres;
		String email = null;
		String validate_code = null;
		//
		try {
			email = jo.get("email").toString();
			validate_code = jo.get("validate_code").toString();

		} catch ( Exception e ) {
			UserLog.Log(tag, "<<< json param Block -> some json param not found >>>");
			errorReturn(res, out, "9001", "json param not found");
			return null;
		}
		
		// public boolean verifyEmail(ServletContext application, String email, String validate_code)
		ServletContext sc = getServletContext();
		if ( verifyEmail(sc, email, validate_code) ) {
			// update user activity 'Y'
		} else {
			
		}
		
		SqlSession sqlSession = ConnectionFactory.getSession().openSession();
		//
		try {
			UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
			userMapper.loginGcmUser(email);
			sqlSession.commit();
			
		} catch ( Exception e ) {
			sqlSession.rollback();
			errorReturn(res, out, "9999", e.getMessage());
			return null;

		} finally {
			sqlSession.close();
		}
		
		putEx(res, "result", "yes");

		return res;
	}
	
	//
	// Push API
	//
	
	// gcm_group_id_work_req
	public JSONObject gcm_group_id_work_req(HttpServletRequest request, HttpServletResponse response, 
			PrintWriter out, JSONObject jo, JSONObject jsres) throws ServletException, IOException {

		JSONObject res = jsres;
		//
		String strEmail = null;
		String strPushName = null;
		String strGroupId = null;
		// "create", "add", "remove"
		String strAction = null;
		// {"device_id_list":["phone1", "phone2", ..., "phoneN"]}
		//String strJsonDeviceIdList = null;
		List<String> lDeviceList = new ArrayList<String>();

		//
		try {
			strEmail = jo.get("user_id").toString();
			strPushName = jo.get("push_name").toString();
			strGroupId = jo.get("group_id").toString();
			strAction = jo.get("action").toString();

		} catch ( Exception e ) {
			UserLog.Log(tag, "<<< json param Block -> some json param not found >>>");
			errorReturn(res, out, "9001", "json param not found");
			return null;
		}
		// device_id 는 일련번호, 전화번호, email, device_token, ... 등의 중복되지 않는 값을 사용하는 것이 좋다.
		//   중복되더라도 사용은 가능하나, 느리게 동작할 수도 있다.
		try {
			JSONObject joSub = (JSONObject)jo.get("json_data");
			//strJsonDeviceIdList = joSub.get("device_id_list").toString();
			//
			JSONArray listObj = new JSONArray();
			listObj = (JSONArray)joSub.get("device_id_list");
			for ( int i = 0; i < listObj.size(); i++ ) {
				String strDeviceId = listObj.get(i).toString();
				//UserLog.Log(tag, strDeviceId);
				lDeviceList.add(strDeviceId);
			}

		} catch ( Exception e ) {
			//strJsonDeviceIdList = null;
		}
		//
		int nUserSeq = get_push_user_seq(strEmail);
		if ( nUserSeq == 0 ) {
			UserLog.Log(tag, "<<< get push_user seq failure >>>");
			errorReturn(res, out, "9002", "get push_user seq failure");
			return null;
		}

		//
		GroupWorkInfo info = new GroupWorkInfo();
		info.push_user_seq = nUserSeq;
		info.push_name = strPushName;
		info.group_id = strGroupId;
		//info.device_id_list = strJsonDeviceIdList;

		//
		SqlSession sqlSession = ConnectionFactory.getSession().openSession();
		//
		try {
			UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
			if ( strAction.equals("create") ) {
				userMapper.createGcmGroup(info);

			} else if ( strAction.equals("delete") ) {
				userMapper.deleteGcmGroup(info);
				
			} else if ( strAction.equals("add_to_list") ) {
				if ( lDeviceList.size() == 0 ) {
					UserLog.Log(tag, "<<< device_id_list has no data >>>");
					errorReturn(res, out, "9003", "device_id_list has no data");
					return null;
				}
				// 저장된 값 
				String strList = userMapper.getGcmGroupData(info);
				//UserLog.Log(tag, "strList -> " + strList);
				List<Integer> lDeviceSeq = null;
				if ( strList != null && strList.length() > 0 ) {
					lDeviceSeq = stringToIntList(strList);
					//for ( Integer n : lDeviceSeq ) {
					//	UserLog.Log(tag, "in db> seq -> " + n);
					//}
				}
				// 받은 device_id 배열에 매칭되는 device_seq list 를 찾는다.
				List<Integer> lAddSeq = userMapper.getGcmDeviceSeqList(lDeviceList);
				// 결과값
				List<Integer> lResultSeq = addToIntList(lDeviceSeq, lAddSeq);
				//for ( Integer n : lResultSeq ) {
				//	UserLog.Log(tag, "recv> seq -> " + n);
				//}
				// db update
				userMapper.updateGcmGroupData(info.push_user_seq, info.push_name, info.group_id, lResultSeq);

			} else if ( strAction.equals("remove_from_list") ) {
				if ( lDeviceList.size() == 0 ) {
					UserLog.Log(tag, "<<< device_id_list has no data >>>");
					errorReturn(res, out, "9003", "device_id_list has no data");
					return null;
				}
				// 저장된 값 
				String strList = userMapper.getGcmGroupData(info);
				//UserLog.Log(tag, "strList -> " + strList);
				List<Integer> lDeviceSeq = null;
				if ( strList != null && strList.length() > 0 ) {
					lDeviceSeq = stringToIntList(strList);
					//for ( Integer n : lDeviceSeq ) {
					//	UserLog.Log(tag, "in db> seq -> " + n);
					//}
				}
				// 받은 device_id 배열에 매칭되는 device_seq list 를 찾는다.
				List<Integer> lRemoveSeq = userMapper.getGcmDeviceSeqList(lDeviceList);
				// 결과값
				List<Integer> lResultSeq = removeFromIntList(lDeviceSeq, lRemoveSeq);
				//for ( Integer n : lResultSeq ) {
				//	UserLog.Log(tag, "recv> seq -> " + n);
				//}
				// db update
				userMapper.updateGcmGroupData(info.push_user_seq, info.push_name, info.group_id, lResultSeq);

				
			} else if ( strAction.equals("delete_list") ) {
				userMapper.deleteGcmGroupData(info);

			} else {

			}
			//
			sqlSession.commit();

		} catch ( Exception e ) {
			sqlSession.rollback();
			e.printStackTrace();
			errorReturn(res, out, "9999", e.getMessage());
			return null;

		} finally {
			sqlSession.close();
		}

		putEx(res, "result", "yes");

		return res;
	}
	
	// gcm_group_id_list_req
	public JSONObject gcm_group_id_list_req(HttpServletRequest request, HttpServletResponse response, 
			PrintWriter out, JSONObject jo, JSONObject jsres) throws ServletException, IOException {

		JSONObject res = jsres;
		//
		String strEmail = null;
		String strPushName = null;

		//
		try {
			strEmail = jo.get("user_id").toString();
			strPushName = jo.get("push_name").toString();

		} catch ( Exception e ) {
			UserLog.Log(tag, "<<< json param Block -> some json param not found >>>");
			errorReturn(res, out, "9001", "json param not found");
			return null;
		}
		//
		int nUserSeq = get_push_user_seq(strEmail);
		if ( nUserSeq == 0 ) {
			UserLog.Log(tag, "<<< get push_user seq failure >>>");
			errorReturn(res, out, "9002", "get push_user seq failure");
			return null;
		}

		//
		GroupWorkInfo info = new GroupWorkInfo();
		info.push_user_seq = nUserSeq;
		info.push_name = strPushName;
		
		//
		List<String> lResult = null;

		SqlSession sqlSession = ConnectionFactory.getSession().openSession();
		//
		try {
			UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
			lResult = userMapper.getGcmGroupList(info);
			//
		} catch ( Exception e ) {
			errorReturn(res, out, "9999", e.getMessage());
			return null;

		} finally {
			sqlSession.close();
		}

		putEx(res, "result", "yes");
		JSONArray listObj = new JSONArray();
		//for ( int i = 0; i < lResult.size(); i++ ) {
		for ( String item : lResult ) {
			listObj.add(item);
		}
		res.put("group_list", listObj);

		return res;
	}
	
	// gcm_group_token_work_req
	public JSONObject gcm_group_token_work_req(HttpServletRequest request, HttpServletResponse response, 
			PrintWriter out, JSONObject jo, JSONObject jsres) throws ServletException, IOException {

		JSONObject res = jsres;
		//
		String strEmail = null;
		String strPushName = null;
		String strGroupId = null;
		// "create", "add", "remove"
		String strAction = null;
		// {"device_token_list":["token1", "token2", ..., "tokenN"]}
		List<String> lTokenList = new ArrayList<String>();

		//
		try {
			strEmail = jo.get("user_id").toString();
			strPushName = jo.get("push_name").toString();
			strGroupId = jo.get("group_id").toString();
			strAction = jo.get("action").toString();

		} catch ( Exception e ) {
			UserLog.Log(tag, "<<< json param Block -> some json param not found >>>");
			errorReturn(res, out, "9001", "json param not found");
			return null;
		}
		// device_id 는 일련번호, 전화번호, email, device_token, ... 등의 중복되지 않는 값을 사용하는 것이 좋다.
		//   중복되더라도 사용은 가능하나, 느리게 동작할 수도 있다.
		try {
			JSONObject joSub = (JSONObject)jo.get("json_data");
			//strJsonDeviceIdList = joSub.get("device_id_list").toString();
			//
			JSONArray listObj = new JSONArray();
			listObj = (JSONArray)joSub.get("device_token_list");
			for ( int i = 0; i < listObj.size(); i++ ) {
				String strDeviceId = listObj.get(i).toString();
				lTokenList.add(strDeviceId);
			}

		} catch ( Exception e ) {
		}
		//
		int nUserSeq = get_push_user_seq(strEmail);
		if ( nUserSeq == 0 ) {
			UserLog.Log(tag, "<<< get push_user seq failure >>>");
			errorReturn(res, out, "9002", "get push_user seq failure");
			return null;
		}

		//
		GroupWorkInfo info = new GroupWorkInfo();
		info.push_user_seq = nUserSeq;
		info.push_name = strPushName;
		info.group_id = strGroupId;

		//
		SqlSession sqlSession = ConnectionFactory.getSession().openSession();
		//
		try {
			UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
			if ( strAction.equals("create") ) {
				userMapper.createGcmGroup(info);

			} else if ( strAction.equals("delete") ) {
				userMapper.deleteGcmGroup(info);
				
			} else if ( strAction.equals("add_to_list") ) {
				if ( lTokenList.size() == 0 ) {
					UserLog.Log(tag, "<<< device_token_list has no data >>>");
					errorReturn(res, out, "9003", "device_token_list has no data");
					return null;
				}
				// 저장된 값 
				String strList = userMapper.getGcmGroupData(info);
				List<Integer> lDeviceSeq = null;
				if ( strList != null && strList.length() > 0 ) {
					lDeviceSeq = stringToIntList(strList);
				}
				// 받은 device_token 배열에 매칭되는 device_seq list 를 찾는다.
				List<Integer> lAddSeq = userMapper.getGcmDeviceTokenList(lTokenList);
				// 결과값
				List<Integer> lResultSeq = addToIntList(lDeviceSeq, lAddSeq);
				// db update
				userMapper.updateGcmGroupData(info.push_user_seq, info.push_name, info.group_id, lResultSeq);

			} else if ( strAction.equals("remove_from_list") ) {
				if ( lTokenList.size() == 0 ) {
					UserLog.Log(tag, "<<< device_token_list has no data >>>");
					errorReturn(res, out, "9003", "device_token_list has no data");
					return null;
				}
				// 저장된 값 
				String strList = userMapper.getGcmGroupData(info);
				List<Integer> lDeviceSeq = null;
				if ( strList != null && strList.length() > 0 ) {
					lDeviceSeq = stringToIntList(strList);
				}
				// 받은 device_token 배열에 매칭되는 device_seq list 를 찾는다.
				List<Integer> lRemoveSeq = userMapper.getGcmDeviceTokenList(lTokenList);
				// 결과값
				List<Integer> lResultSeq = removeFromIntList(lDeviceSeq, lRemoveSeq);
				// db update
				userMapper.updateGcmGroupData(info.push_user_seq, info.push_name, info.group_id, lResultSeq);
				
			} else if ( strAction.equals("delete_list") ) {
				userMapper.deleteGcmGroupData(info);

			} else {

			}
			//
			sqlSession.commit();

		} catch ( Exception e ) {
			sqlSession.rollback();
			e.printStackTrace();
			errorReturn(res, out, "9999", e.getMessage());
			return null;

		} finally {
			sqlSession.close();
		}

		putEx(res, "result", "yes");

		return res;
	}
	
	
	/////////////////////////////////////////////////////////
	// send_gcm_all_push_req
	public JSONObject send_gcm_all_push_req(HttpServletRequest request, HttpServletResponse response, 
			PrintWriter out, JSONObject jo, JSONObject jsres) throws ServletException, IOException {

		JSONObject res = jsres;
		//
		String strEmail = null;
		String strPushName = null;
		String strJsonData = null;
		String strAlert = null;
		String strUserData = null;
		try {
			strEmail = jo.get("user_id").toString();
			strPushName = jo.get("push_name").toString();
			strJsonData = jo.get("json_data").toString();
			//
			JSONObject joSub = (JSONObject)jo.get("json_data");
			strAlert = joSub.get("alert").toString();
			strUserData = joSub.get("user_data").toString();

		} catch (Exception e) {
			UserLog.Log(tag, "<<< json param Block -> some json param not found >>>");
			errorReturn(res, out, "9001", "some json param not found");
			return null;
		}
		
		// debug
		UserLog.Log(tag, strAlert + " | " + strUserData);
		
		if ( strJsonData == null || strJsonData.length() == 0 ) {
			UserLog.Log(tag, "get message failure.");
			errorReturn(res, out, "1001", "No messages");
			return null;
		}
		
		// 메시지 길이 유효성을 확인한다.
		int nUTF8LimitLength = 4000;
		try {
			byte[] utf8_user_data = strJsonData.getBytes("UTF-8");
			if ( utf8_user_data.length > nUTF8LimitLength ) {
				UserLog.Log(tag, "json_data has too long value");
				errorReturn(res, out, "2002", "json_data has too long value");
				return null;
			}
		} catch ( Exception e ) {
			errorReturn(res, out, "9999", e.getMessage());
			return null;
		}
		////
		
		if ( strEmail == null || strPushName == null ||
				strEmail.length() == 0 || strPushName.length() == 0 ) {
			UserLog.Log(tag, "params failure.");
			errorReturn(res, out, "1002", "some param not found");
			return null;
		}
		
		int nUserSeq = get_push_user_seq(strEmail);
		if ( nUserSeq == 0 )  {
			UserLog.Log(tag, "params failure.");
			errorReturn(res, out, "1003", "push user not found");
			return null;
		}

		//
		Vector<String> vAndroidList = new Vector<String>();
		//
		try {
			// 보안상의 이유로 인증부분을 도입할 경우 
			//   user_seq 를 email/pwd 의 인증으로 얻는 부분을 추가하면 된다.
			//int nUserSeq = 1;
			//String strPushName = "mini-push";
			//
			String strApiKey = null;
			String strProjNum = null;
			List<String> deviceList = null;
			SqlSession sqlSession = ConnectionFactory.getSession().openSession();
			//
			try {
				UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
				//
				//nUserSeq = userMapper.getPushUserSeq(strEmail);
				//
				strApiKey = userMapper.getApiKey(nUserSeq, strPushName);
				strProjNum = userMapper.getProjNum(nUserSeq, strPushName);
				//
				//
//				if ( extDeviceList.size() > 0 ) {
//					deviceList = extDeviceList;
//				} else {
//					deviceList = userMapper.getAllGcmDeviceTokenList(strProjNum);
//				}
				deviceList = userMapper.getAllGcmDeviceTokenList(strProjNum);
				//sqlSession.commit();
				
			} catch ( Exception e ) {
				//sqlSession.rollback();
				errorReturn(res, out, "9999", e.getMessage());
				return null;

			} finally {
				sqlSession.close();
			}
			
			////
			// user_seq 에 연관된 사용자의 gcm push 전송건수는 여기서 계산할 수 있다.
			for ( int i = 0; i < deviceList.size(); i++ ) {
				vAndroidList.add(deviceList.get(i));
			}
			
			// Send GCM
			GCMClient gcm = new GCMClient();
			gcm.setApiKey(strApiKey);
			
			gcm.setDeviceList(vAndroidList);
			//gcm.sendGCM(strMsgId, strJsonData);
			
			////////
			// 메시지 전송하기 전 수신확인을 위한 message data 를 기록한다.
			PushMessageInfo push_msg = new PushMessageInfo();
			push_msg.m_id = make_message_id();
			push_msg.seq = nUserSeq;
			push_msg.push_name = strPushName;
			push_msg.group_seq = 0;
			push_msg.total_cnt = vAndroidList.size();
			push_msg.success_cnt = 0;
			push_msg.message_type = "a";
			push_msg.alert = strAlert;
			push_msg.user_data = strUserData;
			////

			if ( !put_push_message(push_msg) ) {
				errorReturn(res, out, "1004", "record PushMessageInfo failure");
				return null;
			}
			////////
			
//			if ( m_id == 0 ) {
//				UserLog.Log(tag, "record push message failure");
//				errorReturn(res, out, "2003", "record push message failure");
//				return null;
//			}
			//
			
			int nSendCount = gcm.sendGCM(strJsonData, push_msg.m_id);
			//
			if ( nSendCount > 0 ) {
				if ( !update_message_send_count(push_msg.m_id, nSendCount) ) {
					UserLog.Log(tag, "update_message_send_count failure");
				}
				UserLog.Log(tag, "m_id: " + push_msg.m_id + " send: " + nSendCount);				
			}
			//
			putEx(res, "result", "yes");
			putEx(res, "total", deviceList.size());
			putEx(res, "send_cnt", nSendCount);

		} catch (Exception e) {
			UserLog.Log(tag, "<<< JSP Block >>>");
			e.printStackTrace();
			errorReturn(res, out, "8001", "db error");
			return null;
		}

		UserLog.Log(tag, res.toString());
		return res;
	}
	
	// android/iphone 공통으로 사용
	public JSONObject send_push_req(HttpServletRequest request, HttpServletResponse response, 
			PrintWriter out, JSONObject jo, JSONObject jsres) throws ServletException, IOException {

		JSONObject res = jsres;
		//
		String strEmail = null;
		String strPushName = null;
		String strDeviceIdEx = null;
		String strJsonData = null;
		String strAlert = null;
		String strUserData = null;
		//
		DeviceIdInfo info = new DeviceIdInfo();
		try {
			strEmail = jo.get("user_id").toString();
			strPushName = jo.get("push_name").toString();
			//
			strDeviceIdEx = jo.get("device_id_ex").toString();
			String[] arrItem = strDeviceIdEx.split("\\|");
			if ( arrItem.length != 3 ) {
				UserLog.Log(tag, "<<< json param Block -> some json param invalid >>>");
				errorReturn(res, out, "9004", "some json param invalid");
				return null;
				
			} else {
				info.device_type = arrItem[0];
				info.app_info_id = arrItem[1];
				info.device_id = arrItem[2];
			}
			
			//
			strJsonData = jo.get("json_data").toString();
			//
			JSONObject joSub = (JSONObject)jo.get("json_data");
			strAlert = joSub.get("alert").toString();
			strUserData = joSub.get("user_data").toString();

		} catch (Exception e) {
			UserLog.Log(tag, "<<< json param Block -> some json param not found >>>");
			errorReturn(res, out, "9001", "some json param not found");
			return null;
		}
		
		// debug
		UserLog.Log(tag, strAlert + " | " + strUserData);
		
		if ( strJsonData == null || strJsonData.length() == 0 ) {
			UserLog.Log(tag, "get message failure.");
			errorReturn(res, out, "1001", "No messages");
			return null;
		}
		
		// 메시지 길이 유효성을 확인한다.
		int nUTF8LimitLength = 4000;
		try {
			byte[] utf8_user_data = strJsonData.getBytes("UTF-8");
			if ( utf8_user_data.length > nUTF8LimitLength ) {
				UserLog.Log(tag, "json_data has too long value");
				errorReturn(res, out, "2002", "json_data has too long value");
				return null;
			}
		} catch ( Exception e ) {
			errorReturn(res, out, "9999", e.getMessage());
			return null;
		}
		////
		
		if ( strEmail == null || strPushName == null ||
				strEmail.length() == 0 || strPushName.length() == 0 ) {
			UserLog.Log(tag, "params failure.");
			errorReturn(res, out, "1002", "some param not found");
			return null;
		}
		
		int nUserSeq = get_push_user_seq(strEmail);
		if ( nUserSeq == 0 )  {
			UserLog.Log(tag, "params failure.");
			errorReturn(res, out, "1003", "push user not found");
			return null;
		}

		if ( info.device_type.equals("a") ) {
			// android
			Vector<String> vAndroidList = new Vector<String>();
			//
			try {
				//
				String strApiKey = null;
				//String strProjNum = info.app_info_id;
				DeviceTokenExInfo tokenInfo = null;
				SqlSession sqlSession = ConnectionFactory.getSession().openSession();
				//
				try {
					UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
					//
					strApiKey = userMapper.getApiKey(nUserSeq, strPushName);
					tokenInfo = userMapper.getDeviceTokenEx(strDeviceIdEx);
					//sqlSession.commit();
					
				} catch ( Exception e ) {
					//sqlSession.rollback();
					errorReturn(res, out, "9999", e.getMessage());
					return null;

				} finally {
					sqlSession.close();
				}
				
				if ( tokenInfo == null ) {
					// 존재하지 않는 token
					UserLog.Log(tag, "invalid device_id");
					errorReturn(res, out, "1005", "device_id invalid");
					return null;
				}
				
				// 1건만 전송한다.
				vAndroidList.add(tokenInfo.device_token);
				
				// Send GCM
				GCMClient gcm = new GCMClient();
				gcm.setApiKey(strApiKey);
				
				gcm.setDeviceList(vAndroidList);
				//gcm.sendGCM(strMsgId, strJsonData);
				
				////////
				// 메시지 전송하기 전 수신확인을 위한 message data 를 기록한다.
				PushMessageInfo push_msg = new PushMessageInfo();
				push_msg.m_id = make_message_id();
				push_msg.seq = nUserSeq;
				push_msg.push_name = strPushName;
				push_msg.user_seq = tokenInfo.seq;
				push_msg.group_seq = 0;
				push_msg.total_cnt = vAndroidList.size();
				push_msg.success_cnt = 0;
				push_msg.message_type = "a";
				push_msg.alert = strAlert;
				push_msg.user_data = strUserData;
				////

				if ( !put_push_message(push_msg) ) {
					errorReturn(res, out, "1004", "record PushMessageInfo failure");
					return null;
				}
				////////
				
				int nSendCount = gcm.sendGCM(strJsonData, push_msg.m_id);
				//
				if ( nSendCount > 0 ) {
					if ( !update_message_send_count(push_msg.m_id, nSendCount) ) {
						UserLog.Log(tag, "update_message_send_count failure");
					}
					UserLog.Log(tag, "m_id: " + push_msg.m_id + " send: " + nSendCount);				
				}
				//
				putEx(res, "result", "yes");
				putEx(res, "total", 1);
				putEx(res, "send_cnt", nSendCount);

			} catch (Exception e) {
				UserLog.Log(tag, "<<< JSP Block >>>");
				e.printStackTrace();
				errorReturn(res, out, "8001", "db error");
				return null;
			}
			
		} else if ( info.device_type.equals("i") ) {
			// iphone
			//
			Vector<String> vIosList = new Vector<String>();
			//
			try {
				//
				String strAppId = info.app_info_id;
				DeviceTokenExInfo tokenInfo = null;
				RegisterApnsInfo raInfo = null;
			
				SqlSession sqlSession = ConnectionFactory.getSession().openSession();
				//
				try {
					UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
					//
					raInfo = userMapper.getApnsCertInfo(nUserSeq, strPushName);
					//UserLog.Log(tag, "cert_pwd -> " + raInfo.cert_pwd);
					//
					tokenInfo = userMapper.getDeviceTokenEx(strDeviceIdEx);
					
				} catch ( Exception e ) {
					e.printStackTrace();
					errorReturn(res, out, "9999", e.getMessage());
					return null;

				} finally {
					sqlSession.close();
				}
								
				////
				// 1건만 보낸다.
				vIosList.add(tokenInfo.device_token);
					
				// For APNS
				String strCertFileFullPath = null;
				String strBasicPath = null;
				String strCertPwd = raInfo.cert_pwd;
				String strCertFilePath = null;
				//
				if ( bWindowsOS ) {
					//strBasicPath = "C:\\Root\\Project_MiniPush\\TmpCerts";
					strBasicPath = strWindowCertPath;
					strCertFilePath = raInfo.cert_file_path.replace("/", File.separator);
				} else {
					//strBasicPath = "/home/duckking/pushbegins";
					strBasicPath = strLinuxCertPath;
					strCertFilePath = raInfo.cert_file_path.replace("\\", File.separator);
				}
				strCertFileFullPath = strBasicPath + File.separator + strCertFilePath;
				UserLog.Log(tag, "cert file -> " + strCertFileFullPath);
				
				// Send APNS
				APNSClient apns = new APNSClient();
				if ( raInfo.sandbox.equals("N") ) {
					apns.setSandboxMode(false);
				} else if  ( raInfo.sandbox.equals("Y") ) {
					apns.setSandboxMode(true);
				} else {
					apns.setSandboxMode(true);
					UserLog.Log(tag, "get sandbox mode failure");
				}
				apns.setCertPath(strCertFileFullPath);
				apns.setPwd(strCertPwd);
				//
				JSONObject json_data = (JSONObject) jo.get("json_data");
				
				APNS_Message msg = new APNS_Message();
				msg.alert = json_data.get("alert").toString();
				// 필요시 badge 와 sound 를 지정할 수 있다.
				try {
					msg.badge = Integer.parseInt(json_data.get("badge").toString());
				} catch ( Exception e ) {
					msg.badge = 0;
				}
				//
				try {
					msg.sound = json_data.get("sound").toString();
				} catch ( Exception e ) {
					msg.sound = "default";
				}
				//
				try {
					msg.user_data = json_data.get("user_data").toString();
				} catch ( Exception e ) {
					msg.user_data = null;
				}
				
				// 메시지 길이 유효성을 확인한다.
				nUTF8LimitLength = 200;
		        try {
		        	byte[] utf8_alert = msg.alert.getBytes("UTF-8");
		        	//byte[] utf8_user_data = msg.user_data.getBytes("UTF-8");
		        	if ( utf8_alert.length > nUTF8LimitLength ) {
		        		UserLog.Log(tag, "message.alert has too long value");
						errorReturn(res, out, "2001", "message.alert has too long value");
						return null;
					}
		        } catch ( Exception e ) {
		        	errorReturn(res, out, "9999", e.getMessage());
		        	return null;
		        }
		        //
		        if ( msg.user_data != null && msg.user_data.length() > 0 ) {
			        nUTF8LimitLength = 4000;
			        try {
			        	byte[] utf8_user_data = msg.user_data.getBytes("UTF-8");
			        	if ( utf8_user_data.length > nUTF8LimitLength ) {
			        		UserLog.Log(tag, "message.user_data has too long value");
							errorReturn(res, out, "2002", "message.user_data has too long value");
							return null;
						}
			        } catch ( Exception e ) {
			        	errorReturn(res, out, "9999", e.getMessage());
			        	return null;
			        }
		        }
		        ////
				
				////////
				// 메시지 전송하기 전 수신확인을 위한 message data 를 기록한다.
				PushMessageInfo push_msg = new PushMessageInfo();
				push_msg.m_id = make_message_id();
				push_msg.seq = nUserSeq;
				push_msg.push_name = strPushName;
				push_msg.user_seq = tokenInfo.seq;
				push_msg.group_seq = 0;
				push_msg.total_cnt = vIosList.size();
				push_msg.success_cnt = 0;
				push_msg.message_type = "i";
				push_msg.alert = msg.alert;
				push_msg.user_data = msg.user_data;
				//
				
				if ( !put_push_message(push_msg) ) {
					errorReturn(res, out, "2004", "record PushMessageInfo failure");
					return null;
				}
				////////
				
				//
				msg.m_id = push_msg.m_id;
				//
				int nSendCount = apns.push_multi(msg, vIosList);
				//UserLog.Log(tag, "total: " + vIosList.size() + " send_cnt: " + nSendCount);

				//
				if ( nSendCount > 0 ) {
					if ( !update_message_send_count(push_msg.m_id, nSendCount) ) {
						UserLog.Log(tag, "update_message_send_count failure");
					}
					//UserLog.Log(tag, "m_id: " + push_msg.m_id + " send: " + nSendCount);				
				}
				
				// 총 메시지와 전송 성공 메시지의 갯수가 다를 경우
				if ( nSendCount < 1 ) {
					//
					sqlSession = ConnectionFactory.getSession().openSession();
					try {
						UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
						
						////
						Vector<String> vResult = apns.feedback();
						for ( int i = 0; i < vResult.size(); i++ ) {
							// 사용할 수 없는 feedback device_token 을 삭제한다.
							UserLog.Log(tag, "feedback token: " + vResult.get(i));
							userMapper.deleteApnsDevice(vResult.get(i));
						}
						////
						
					}  catch ( Exception e ) {
						e.printStackTrace();
						errorReturn(res, out, "9999", e.getMessage());
						return null;

					} finally {
						sqlSession.close();
					} 
				}

				//
				putEx(res, "result", "yes");
				putEx(res, "total", 1);
				putEx(res, "send_cnt", nSendCount);

			} catch (Exception e) {
				UserLog.Log(tag, "<<< JSP Block >>>");
				e.printStackTrace();
				errorReturn(res, out, "8001", "db error");
				return null;
			}
			
		} else {
			// 
		}

		UserLog.Log(tag, res.toString());
		return res;
	}
	
	// android/iphone 공통으로 사용
	public JSONObject send_group_push_req(HttpServletRequest request, HttpServletResponse response, 
			PrintWriter out, JSONObject jo, JSONObject jsres) throws ServletException, IOException {
		//
		JSONObject res = jsres;
		//
		String strEmail = null;
		String strPushName = null;
		String strGroupId = null;
		String strJsonData = null;
		String strAlert = null;
		String strUserData = null;

		try {
			//nUserSeq = Integer.parseInt(jo.get("user_seq").toString());
			strEmail = jo.get("user_id").toString();
			strPushName = jo.get("push_name").toString();
			strGroupId = jo.get("group_id").toString();
			strJsonData = jo.get("json_data").toString();
			//
			JSONObject joSub = (JSONObject)jo.get("json_data");
			strAlert = joSub.get("alert").toString();
			strUserData = joSub.get("user_data").toString();

		} catch (Exception e) {
			//UserLog.Log(tag, "<<< json param Block -> some json param not found >>>");
			e.printStackTrace();
			errorReturn(res, out, "9001", "some json param not found");
			return null;
		}
		
		if ( strJsonData == null || strJsonData.length() == 0 ) {
			UserLog.Log(tag, "get message failure.");
			errorReturn(res, out, "1001", "No messages");
			return null;
		}
		
		if ( strGroupId == null || strGroupId.length() == 0 ) {
			UserLog.Log(tag, "get message failure.");
			errorReturn(res, out, "1002", "No GroupId");
			return null;
		}
		
		//public List<DeviceTokenExInfo> getPushGroupDeviceTokenExList(@Param("group_id") String strGroupId);
		//public List<DeviceTokenExInfo> getGcmGroupDeviceTokenExList(@Param("group_id") String strGroupId);
		//public List<DeviceTokenExInfo> getApnsGroupDeviceTokenExList(@Param("group_id") String strGroupId);

		List<DeviceTokenExInfo> liApns = getApnsGroupDeviceTokenExList(strGroupId);
		UserLog.Log(tag, "liApns count -> " + liApns.size());
		//
		PushRequest apnsReq = new PushRequest();
		//
		apnsReq.user_id = strEmail;
		apnsReq.push_name = strPushName;
		apnsReq.alert = strAlert;
		apnsReq.user_data = strUserData;
		//apnsReq.badge = "1";
		//apnsReq.sound = "default";
		//
		for ( int i = 0; i < liApns.size(); i++ ) {
			DeviceTokenExInfo info = liApns.get(i);
			apnsReq.token_list.add(info.device_token);
			//
			apnsReq.seq_list.add(info.seq);
		}
		
		//
		List<DeviceTokenExInfo> liGcm = getGcmGroupDeviceTokenExList(strGroupId);
		UserLog.Log(tag, "liGcm count -> " + liApns.size());
		//
		PushRequest gcmReq = new PushRequest();
		//
		gcmReq.user_id = strEmail;
		gcmReq.push_name = strPushName;
		gcmReq.alert = strAlert;
		gcmReq.user_data = strUserData;
		//
		for ( int i = 0; i < liGcm.size(); i++ ) {
			DeviceTokenExInfo info = liGcm.get(i);
			gcmReq.token_list.add(info.device_token);
			//
			gcmReq.seq_list.add(info.seq);
		}
		
		////
		PushResponse apnsPr = new PushResponse();
		PushResponse gcmPr = new PushResponse();
		// send APNS
		if ( apnsReq.token_list.size() > 0 ) {
			apnsPr = send_apns_push_req(apnsReq);
		}
		// send GCM
		if ( gcmReq.token_list.size() > 0 ) {
			gcmPr = send_gcm_push_req(gcmReq);
		}
		
		PushResponse pr = new PushResponse();
		pr.total_count = apnsPr.total_count + gcmPr.total_count;
		pr.send_count = apnsPr.send_count + gcmPr.send_count;
		
		//
		if ( apnsPr.result == null && gcmPr.result == null ) {
			pr.result = "no";
			pr.error_code = "9005";
			pr.erro_message = "No push message selected";
			
		} else {
			pr.result = "yes";
		}
		
//		//
//		if ( apnsPr.result == null || gcmPr.result == null ) {
//			pr.result = "no";
//			pr.error_code = "9005";
//			pr.erro_message = "No push message selected";
//			
//		} else {
//			//
//			if ( apnsPr.result.equals("no") || gcmPr.result.equals("no") ) {
//				pr.result = "no";
//				pr.error_code = apnsPr.error_code + " / " +  gcmPr.error_code;
//				pr.erro_message = apnsPr.erro_message + " / " +  gcmPr.erro_message;
//				
//			} else {
//				pr.result = "yes";
//			}
//		}
		
		putEx(res, "result", pr.result);
		putEx(res, "total", pr.total_count);
		putEx(res, "send_cnt", pr.send_count);
		if ( pr.result.equals("no") ) {
			putEx(res, "error_code", pr.error_code);
			putEx(res, "erro_message", pr.erro_message);
		}

		//
		UserLog.Log(tag, res.toString());
		return res;
	}
	
	//send_gcm_group_push_req
	public JSONObject send_gcm_group_push_req(HttpServletRequest request, HttpServletResponse response, 
			PrintWriter out, JSONObject jo, JSONObject jsres) throws ServletException, IOException {

		JSONObject res = jsres;
		//
		String strEmail = null;
		String strPushName = null;
		String strGroupId = null;
		String strJsonData = null;
		String strAlert = null;
		String strUserData = null;

		try {
			//nUserSeq = Integer.parseInt(jo.get("user_seq").toString());
			strEmail = jo.get("user_id").toString();
			strPushName = jo.get("push_name").toString();
			strGroupId = jo.get("group_id").toString();
			strJsonData = jo.get("json_data").toString();
			//
			JSONObject joSub = (JSONObject)jo.get("json_data");
			strAlert = joSub.get("alert").toString();
			strUserData = joSub.get("user_data").toString();

		} catch (Exception e) {
			//UserLog.Log(tag, "<<< json param Block -> some json param not found >>>");
			e.printStackTrace();
			errorReturn(res, out, "9001", "some json param not found");
			return null;
		}
		
		if ( strJsonData == null || strJsonData.length() == 0 ) {
			UserLog.Log(tag, "get message failure.");
			errorReturn(res, out, "1001", "No messages");
			return null;
		}
		
		// 메시지 길이 유효성을 확인한다.
		int nUTF8LimitLength = 4000;
		try {
			byte[] utf8_user_data = strJsonData.getBytes("UTF-8");
			if ( utf8_user_data.length > nUTF8LimitLength ) {
				UserLog.Log(tag, "json_data has too long value");
				errorReturn(res, out, "2002", "json_data has too long value");
				return null;
			}
		} catch ( Exception e ) {
			errorReturn(res, out, "9999", e.getMessage());
			return null;
		}
		////
		
		if ( strEmail == null || strPushName == null || strGroupId == null ||
				strEmail.length() == 0 || strPushName.length() == 0 || strGroupId.length() == 0 ) {
			UserLog.Log(tag, "params failure.");
			errorReturn(res, out, "1002", "some param not found");
			return null;
		}
		
		int nUserSeq = get_push_user_seq(strEmail);
		if ( nUserSeq == 0 )  {
			UserLog.Log(tag, "params failure.");
			errorReturn(res, out, "1003", "push user not found");
			return null;
		}
		
		//
		GroupWorkInfo info = new GroupWorkInfo();
		info.push_user_seq = nUserSeq;
		info.push_name = strPushName;
		info.group_id = strGroupId;
		
		//
		Vector<String> vAndroidList = new Vector<String>();
		//
		try {
			//
			String strApiKey = null;
			//String strProjNum = null;
			List<String> deviceList = null;
			SqlSession sqlSession = ConnectionFactory.getSession().openSession();
			//
			try {
				UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
				//
				nUserSeq = userMapper.getPushUserSeq(strEmail);
				//
				strApiKey = userMapper.getApiKey(nUserSeq, strPushName);
				//strProjNum = userMapper.getProjNum(nUserSeq, strPushName);
				// seq, push_name, group_id + push_message
				String strList = userMapper.getGcmGroupDeviceSeqList(info);
				List<Integer> lDeviceSeq = stringToIntList(strList);
				//
				
				// android 기기번호 배열에서 android 기기의 device_token 배열을 얻는다.
				deviceList = userMapper.getGcmGroupDeviceTokenList(lDeviceSeq);
				if ( false ) {
					for ( String token : deviceList ) {
						UserLog.Log(tag, "token -> " + token);
					}
				}
				//sqlSession.commit();
				
			} catch ( Exception e ) {
				//sqlSession.rollback();
				errorReturn(res, out, "9999", e.getMessage());
				return null;

			} finally {
				sqlSession.close();
			}
			
			//
			if ( nUserSeq == 0 ) {
				UserLog.Log(tag, "invalid user_seq.");
				errorReturn(res, out, "1003", "user information invalid");
				return null;
			}
			
			////
			// user_seq 에 연관된 사용자의 gcm push 전송건수는 여기서 계산할 수 있다.
			for ( int i = 0; i < deviceList.size(); i++ ) {
				vAndroidList.add(deviceList.get(i));
			}
			
			// Send GCM
			GCMClient gcm = new GCMClient();
			gcm.setApiKey(strApiKey);
			
			// android 기기의 device_token 배열을 gcm object 에 설정한다.
			gcm.setDeviceList(vAndroidList);
			//gcm.sendGCM(strMsgId, strJsonData);
			
			////////
			// 메시지 전송하기 전 수신확인을 위한 message data 를 기록한다.
			PushMessageInfo push_msg = new PushMessageInfo();
			push_msg.m_id = make_message_id();
			push_msg.seq = nUserSeq;
			push_msg.push_name = strPushName;
			//push_msg.group_id = strGroupId;
			push_msg.group_seq = 0;
			push_msg.total_cnt = vAndroidList.size();
			push_msg.success_cnt = 0;
			push_msg.message_type = "a";
			push_msg.alert = strAlert;
			push_msg.user_data = strUserData;
			////

			if ( !put_push_message(push_msg) ) {
				errorReturn(res, out, "1004", "record PushMessageInfo failure");
				return null;
			}
			////////
			
//			if ( m_id == 0 ) {
//				UserLog.Log(tag, "record push message failure");
//				errorReturn(res, out, "2003", "record push message failure");
//				return null;
//			}
			//
			
			int nSendCount = gcm.sendGCM(strJsonData, push_msg.m_id);
			//
			if ( nSendCount > 0 ) {
				if ( !update_message_send_count(push_msg.m_id, nSendCount) ) {
					UserLog.Log(tag, "update_message_send_count failure");
				}
				UserLog.Log(tag, "m_id: " + push_msg.m_id + " send: " + nSendCount);				
			}
			//
			putEx(res, "result", "yes");
			putEx(res, "total", deviceList.size());
			putEx(res, "send_cnt", nSendCount);

		} catch (Exception e) {
			UserLog.Log(tag, "<<< JSP Block >>>");
			e.printStackTrace();
			errorReturn(res, out, "8001", "db error");
			return null;
		}

		UserLog.Log(tag, res.toString());
		return res;
	}
	
	// get_gcm_device_list_req
	public JSONObject get_gcm_device_list_req(HttpServletRequest request, HttpServletResponse response, 
			PrintWriter out, JSONObject jo, JSONObject jsres) throws ServletException, IOException {

		JSONObject res = jsres;
		//
		String strEmail = null;
		String strPushName = null;

		try {
			strEmail = jo.get("user_id").toString();
			strPushName = jo.get("push_name").toString();

		} catch (Exception e) {
			//UserLog.Log(tag, "<<< json param Block -> some json param not found >>>");
			e.printStackTrace();
			errorReturn(res, out, "9001", "some json param not found");
			return null;
		}

		if ( strEmail == null || strPushName == null ||
				strEmail.length() == 0 || strPushName.length() == 0 ) {
			UserLog.Log(tag, "params failure.");
			errorReturn(res, out, "1002", "some param not found");
			return null;
		}
		
		int nUserSeq = get_push_user_seq(strEmail);
		if ( nUserSeq == 0 )  {
			UserLog.Log(tag, "params failure.");
			errorReturn(res, out, "1003", "push user not found");
			return null;
		}

		// 등록된 gcm 사용자들의 email:alias 의 값을 알려준다.
		// select email, alias from tb_gcm_user;
		try {
			//
			String strProjNum = null;
			List<GcmDeviceInfo> lInfo = null;
			SqlSession sqlSession = ConnectionFactory.getSession().openSession();
			//
			try {
				UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
				strProjNum = userMapper.getProjNum(nUserSeq, strPushName);
				//
				lInfo = userMapper.getGcmDeviceList(strProjNum);

			} catch ( Exception e ) {
				errorReturn(res, out, "9999", e.getMessage());
				return null;

			} finally {
				sqlSession.close();
			}

			//
			JSONArray listObj = new JSONArray();
			//
			for ( int i = 0; i < lInfo.size(); i++ ) {
				//UserLog.Log(tag, deviceList.get(i));
				JSONObject objItem = new JSONObject();
				GcmDeviceInfo data = (GcmDeviceInfo) lInfo.get(i);
				//
				objItem.put("seq", data.seq);
				objItem.put("device_id", data.device_id);
				objItem.put("device_token", data.device_token);
				//
				listObj.add(objItem);
			}
			
			putEx(res, "result", "yes");
			res.put("user_list", listObj);
			//
			
		} catch (Exception e) {
			UserLog.Log(tag, "<<< JSP Block >>>");
			e.printStackTrace();
			errorReturn(res, out, "8001", "db error");
			return null;
		}

		UserLog.Log(tag, res.toString());
		return res;
	}
	
	//////////
	
	// register or update gcm device information
		public JSONObject update_apns_device_info_req(HttpServletRequest request, HttpServletResponse response, 
				PrintWriter out, JSONObject jo, JSONObject jsres) throws ServletException, IOException {

			JSONObject res = jsres;
			ApnsDeviceInfo info = new ApnsDeviceInfo();
			int nDeviceSeq = 0;
			//
			try {
				String cid = jo.get("device_id").toString();
				info.device_token = jo.get("device_token").toString();
				info.app_id = jo.get("app_id").toString();
				//
				info.device_id = "i" + "|" + info.app_id + "|" + cid;

			} catch ( Exception e ) {
				UserLog.Log(tag, "<<< json param Block -> some json param not found >>>");
				errorReturn(res, out, "9001", "json param not found");
				return null;
			}

			SqlSession sqlSession = ConnectionFactory.getSession().openSession();
			//
			try {
				UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
				// 아이폰, 안드로이드가 하나의 table 에서 관리된다.
				
				// 아이폰은 재설치시 device_token 이 변경된다.
				// 재설치 후 기존 사용중인 device_token 정보는 apple server 의 응답인
				// APNSClient.feedback() 의 값을 사용해서 미사용 정보를 삭제한다. 
				
				//nDeviceSeq = userMapper.getApnsDeviceSeq(info.device_token);
				int nSeq1 = userMapper.getApnsDeviceSeq(info.device_token);
				int nSeq2 = userMapper.getApnsDeviceSeq2(info.device_id);
				//if ( nDeviceSeq == 0 ) {
				if ( nSeq1 == 0 && nSeq2 == 0 ) {
					// 등록되지 않은 apns device 의 처리.
					//userMapper.deleteApnsDevice(info.device_token);
					userMapper.insertApnsDevice(info);
					//sqlSession.commit();
					nDeviceSeq = userMapper.getApnsDeviceSeq(info.device_token);
					UserLog.Log(tag, "getApnsDeviceSeq -> " + nDeviceSeq + " / " + info.app_id);

					//
					// 안드로이드/아이폰 에서 하나의 ID 만 허용할 경우는
					// device_id 에서 cid 부분을 unique 로 동작하게 처리해서 사용한다.
					DeviceMapInfo dm_info = new DeviceMapInfo();
					dm_info.device_id = info.device_id;
					dm_info.device_seq = nDeviceSeq;
					//
					userMapper.insertDeviceMap(dm_info);
					sqlSession.commit();
					//
					//UserLog.Log(tag, "insert done");
					
				} else { // nDeviceSeq != 0
					// 이미 등록된 apns device 의 처리.
					// device_token 보다 device_id 의 우선순위가 더 높게 정의한다.
					if ( nSeq1 != 0 ) {
						nDeviceSeq = nSeq1;
					}
					if ( nSeq2 != 0 ) {
						nDeviceSeq = nSeq2;
					}
					info.seq = nDeviceSeq;
					userMapper.updateApnsDevice(info);
					
					//
					// 안드로이드/아이폰 에서 하나의 ID 만 허용할 경우는
					// device_id 에서 cid 부분을 unique 로 동작하게 처리해서 사용한다.
					DeviceMapInfo dm_info = new DeviceMapInfo();
					dm_info.device_id = info.device_id;
					dm_info.device_seq = nDeviceSeq;
					//
					int bHasDeviceMap = userMapper.hasDeviceMap(dm_info);
					if ( bHasDeviceMap == 0 ) {
						userMapper.insertDeviceMap(dm_info);
					}
					sqlSession.commit();
					//UserLog.Log(tag, "update done");
				}
				//
				
			} catch ( Exception e ) {
				sqlSession.rollback();
				errorReturn(res, out, "9999", e.getMessage());
				return null;

			} finally {
				sqlSession.close();
			}
			
			putEx(res, "result", "yes");
			String strDeviceSeq = String.format("%d",  nDeviceSeq);
			putEx(res, "device_seq", strDeviceSeq);
			//
			UserLog.Log(tag, res.toString());

			return res;
		}
	
	// send_apns_all_push_req
	public JSONObject send_apns_all_push_req(HttpServletRequest request, HttpServletResponse response, 
			PrintWriter out, JSONObject jo, JSONObject jsres) throws ServletException, IOException {
		
		JSONObject res = jsres;
		//
		int nUserSeq = 0;
		String strEmail = null;
		String strPushName = null;
		//
		String strJsonData = null;
		try {
			strEmail = jo.get("user_id").toString();
			strPushName = jo.get("push_name").toString();
			strJsonData = jo.get("json_data").toString();

		} catch (Exception e) {
			e.printStackTrace();
			UserLog.Log(tag, "<<< json param Block -> some json param not found >>>");
			errorReturn(res, out, "9001", "some json param not found");
			return null;
		}
		
//		///////////////////////////////////////////////////////////////////
//		// for external site id list
//		List<String> extDeviceList = new ArrayList<String>();
//		//
//		try {
//			List<String> extIdList = new ArrayList<String>();
//			//
//			JSONArray listObj = new JSONArray();
//			listObj = (JSONArray)jo.get("ext_id_list");
//			for ( int i = 0; i < listObj.size(); i++ ) {
//				String strExtId = listObj.get(i).toString();
//				//UserLog.Log(tag, strDeviceId);
//				extIdList.add(strExtId);
//			}
//			//
//			extDeviceList = get_ext_device_list_i(extIdList);
//			if ( false ) {
//				// Test block
//				if ( extDeviceList.size() > 0 ) {
//					for ( String token : extDeviceList ) {
//						UserLog.Log(tag, "device_token -> " + token);
//					}
//				}
//				//
//				errorReturn(res, out, "1001", "No messages");
//				return null;
//			}
//
//		} catch ( Exception e ) {
//			// ignore error
//		}
//		///////////////////////////////////////////////////////////////////
		
		if ( strJsonData == null || strJsonData.length() == 0 ) {
			UserLog.Log(tag, "get message failure.");
			errorReturn(res, out, "1001", "No messages");
			return null;
		}
		
		if ( strEmail == null || strPushName == null ||
				strEmail.length() == 0 || strPushName.length() == 0 ) {
			UserLog.Log(tag, "params failure.");
			errorReturn(res, out, "1002", "some param not found");
			return null;
		}

		//
		Vector<String> vIosList = new Vector<String>();
		//
		try {
			//
			String strAppId = null;
			List<String> deviceList = null;
			//
			RegisterApnsInfo info = null;
		
			SqlSession sqlSession = ConnectionFactory.getSession().openSession();
			//
			try {
				UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
				//
				nUserSeq = userMapper.getPushUserSeq(strEmail);
				//UserLog.Log(tag, "seq -> " + nUserSeq);
				//
				info = userMapper.getApnsCertInfo(nUserSeq, strPushName);
				//UserLog.Log(tag, "cert_pwd -> " + info.cert_pwd);
				//
				
//				if ( extDeviceList.size() > 0 ) {
//					deviceList = extDeviceList;
//				} else {
//					deviceList = userMapper.getAllApnsDeviceTokenList(info.app_id);
//				}
				deviceList = userMapper.getAllApnsDeviceTokenList(info.app_id);
				
			} catch ( Exception e ) {
				e.printStackTrace();
				errorReturn(res, out, "9999", e.getMessage());
				return null;

			} finally {
				sqlSession.close();
			}
			
			//
			if ( nUserSeq == 0 ) {
				UserLog.Log(tag, "invalid user_seq.");
				errorReturn(res, out, "1003", "user information invalid");
				return null;
			}
			
			////
			// user_seq 에 연관된 사용자의 apns push 전송건수는 여기서 계산할 수 있다.
			for ( int i = 0; i < deviceList.size(); i++ ) {
				vIosList.add(deviceList.get(i));
				//UserLog.Log(tag, "token: " + deviceList.get(i));
			}
				
			// For APNS
			String strCertFileFullPath = null;
			String strBasicPath = null;
			String strCertPwd = info.cert_pwd;
			String strCertFilePath = null;
			//
			if ( bWindowsOS ) {
				//strBasicPath = "C:\\Root\\Project_MiniPush\\TmpCerts";
				strBasicPath = strWindowCertPath;
				strCertFilePath = info.cert_file_path.replace("/", File.separator);
			} else {
				//strBasicPath = "/home/duckking/pushbegins";
				strBasicPath = strLinuxCertPath;
				strCertFilePath = info.cert_file_path.replace("\\", File.separator);
			}
			strCertFileFullPath = strBasicPath + File.separator + strCertFilePath;
			UserLog.Log(tag, "cert file -> " + strCertFileFullPath);
			
			// Send APNS
			APNSClient apns = new APNSClient();
			if ( info.sandbox.equals("N") ) {
				apns.setSandboxMode(false);
			} else if  ( info.sandbox.equals("Y") ) {
				apns.setSandboxMode(true);
			} else {
				apns.setSandboxMode(true);
				UserLog.Log(tag, "get sandbox mode failure");
			}
			apns.setCertPath(strCertFileFullPath);
			apns.setPwd(strCertPwd);
			//
			//strMsgId = "12345";
			//apns.push_multi(strMsgId, strJsonData, vIosList);
			//apns.push_multi(strJsonData, vIosList);
			//apns.push_multi("gogogo", vIosList);
			
			//
			JSONObject json_data = (JSONObject) jo.get("json_data");
			
			APNS_Message msg = new APNS_Message();
			msg.alert = json_data.get("alert").toString();
			//
			try {
				msg.badge = Integer.parseInt(json_data.get("badge").toString());
			} catch ( Exception e ) {
				msg.badge = 0;
			}
			//
			try {
				msg.sound = json_data.get("sound").toString();
			} catch ( Exception e ) {
				msg.sound = null;
			}
			//
			try {
				msg.user_data = json_data.get("user_data").toString();
			} catch ( Exception e ) {
				msg.user_data = null;
			}
			
			// 메시지 길이 유효성을 확인한다.
			int nUTF8LimitLength = 200;
	        try {
	        	byte[] utf8_alert = msg.alert.getBytes("UTF-8");
	        	//byte[] utf8_user_data = msg.user_data.getBytes("UTF-8");
	        	if ( utf8_alert.length > nUTF8LimitLength ) {
	        		UserLog.Log(tag, "message.alert has too long value");
					errorReturn(res, out, "2001", "message.alert has too long value");
					return null;
				}
	        } catch ( Exception e ) {
	        	errorReturn(res, out, "9999", e.getMessage());
	        	return null;
	        }
	        //
	        if ( msg.user_data != null && msg.user_data.length() > 0 ) {
		        nUTF8LimitLength = 4000;
		        try {
		        	byte[] utf8_user_data = msg.user_data.getBytes("UTF-8");
		        	if ( utf8_user_data.length > nUTF8LimitLength ) {
		        		UserLog.Log(tag, "message.user_data has too long value");
						errorReturn(res, out, "2002", "message.user_data has too long value");
						return null;
					}
		        } catch ( Exception e ) {
		        	errorReturn(res, out, "9999", e.getMessage());
		        	return null;
		        }
	        }
	        ////
			
			////////
			// 메시지 전송하기 전 수신확인을 위한 message data 를 기록한다.
			PushMessageInfo push_msg = new PushMessageInfo();
			push_msg.m_id = make_message_id();
			push_msg.seq = nUserSeq;
			push_msg.push_name = strPushName;
			//push_msg.group_id = "all";
			push_msg.group_seq = 0;
			push_msg.total_cnt = vIosList.size();
			push_msg.success_cnt = 0;
			push_msg.message_type = "i";
			push_msg.alert = msg.alert;
			push_msg.user_data = msg.user_data;
			//
			
			if ( !put_push_message(push_msg) ) {
				errorReturn(res, out, "2004", "record PushMessageInfo failure");
				return null;
			}
			////////
			
//			if ( push_msg.m_id == 0 ) {
//				UserLog.Log(tag, "record push message failure");
//				errorReturn(res, out, "2003", "record push message failure");
//				return null;
//			}
			
			//
			msg.m_id = push_msg.m_id;
			//
			int nSendCount = apns.push_multi(msg, vIosList);
			//UserLog.Log(tag, "total: " + vIosList.size() + " send_cnt: " + nSendCount);

			//
			if ( nSendCount > 0 ) {
				if ( !update_message_send_count(push_msg.m_id, nSendCount) ) {
					UserLog.Log(tag, "update_message_send_count failure");
				}
				//UserLog.Log(tag, "m_id: " + push_msg.m_id + " send: " + nSendCount);				
			}
			
			// 총 메시지와 전송 성공 메시지의 갯수가 다를 경우
			if ( nSendCount < deviceList.size() ) {
				Vector<String> vResult = apns.feedback();
				for ( int i = 0; i < vResult.size(); i++ ) {
					UserLog.Log(tag, "feedback token: " + vResult.get(i));
				}
			}

			//
			putEx(res, "result", "yes");
			putEx(res, "total", deviceList.size());
			putEx(res, "send_cnt", nSendCount);

		} catch (Exception e) {
			UserLog.Log(tag, "<<< JSP Block >>>");
			e.printStackTrace();
			errorReturn(res, out, "8001", "db error");
			return null;
		}

		UserLog.Log(tag, res.toString());
		return res;
	}
	
	// verify_message_id_req
	public JSONObject verify_message_id_req(HttpServletRequest request, HttpServletResponse response, 
			PrintWriter out, JSONObject jo, JSONObject jsres) throws ServletException, IOException {

		JSONObject res = jsres;
		long m_id = 0;
		//
		try {
			m_id = Long.parseLong(jo.get("m_id").toString());

		} catch ( Exception e ) {
			UserLog.Log(tag, "<<< json param Block -> some json param not found >>>");
			errorReturn(res, out, "9001", "json param not found");
			return null;
		}
		
		if ( m_id == 0 ) {
			UserLog.Log(tag, "message id invalid");
			errorReturn(res, out, "9002", "message id invalid");
			return null;
		}
		
		String strUserData = null;
		
		SqlSession sqlSession = ConnectionFactory.getSession().openSession();
		//
		try {
			UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
			userMapper.addMessageCount(m_id);
			sqlSession.commit();
			//
			strUserData = userMapper.getUserData(m_id);
			
		} catch ( Exception e ) {
			sqlSession.rollback();
			errorReturn(res, out, "9999", e.getMessage());
			return null;

		} finally {
			sqlSession.close();
		}
		
		putEx(res, "result", "yes");
		putEx(res, "user_data", strUserData);

		return res;
	}
	
	// confirm_message_req
	public JSONObject confirm_message_req(HttpServletRequest request, HttpServletResponse response, 
			PrintWriter out, JSONObject jo, JSONObject jsres) throws ServletException, IOException {

		JSONObject res = jsres;
		long m_id = 0;
		int device_seq = 0;
		//
		try {
			m_id = Long.parseLong(jo.get("m_id").toString());
			device_seq = Integer.parseInt(jo.get("device_seq").toString());

		} catch ( Exception e ) {
			UserLog.Log(tag, "<<< json param Block -> some json param not found >>>");
			errorReturn(res, out, "9001", "json param not found");
			return null;
		}
		
		if ( m_id == 0 ) {
			UserLog.Log(tag, "message id invalid");
			errorReturn(res, out, "9002", "message id invalid");
			return null;
		}
		
		if ( device_seq == 0 ) {
			UserLog.Log(tag, "device_seq invalid");
			errorReturn(res, out, "9002", "device_seq invalid");
			return null;
		}
		
		String strUserData = null;
		
		SqlSession sqlSession = ConnectionFactory.getSession().openSession();
		//
		try {
			UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
			userMapper.addMessageCount(m_id);
			//userMapper.confirmMessage(m_id, device_seq);
			sqlSession.commit();
			//
			//strUserData = userMapper.getUserData(m_id);
			
		} catch ( Exception e ) {
			sqlSession.rollback();
			errorReturn(res, out, "9999", e.getMessage());
			return null;

		} finally {
			sqlSession.close();
		}
		
		putEx(res, "result", "yes");

		return res;
	}
		
	public JSONObject send_customer_message_a_req(HttpServletRequest request, HttpServletResponse response, 
			PrintWriter out, JSONObject jo, JSONObject jsres) throws ServletException, IOException {

		JSONObject res = jsres;
		CustomerMessageInfo info = new CustomerMessageInfo();
		String strProjNum = null;
		
		//
		try {
			info.device_seq = Integer.parseInt(jo.get("device_seq").toString());
			//info.user_seq = Integer.parseInt(jo.get("user_seq").toString());
			strProjNum = jo.get("proj_num").toString();
			info.message = jo.get("message").toString();

		} catch ( Exception e ) {
			UserLog.Log(tag, "<<< json param Block -> some json param not found >>>");
			errorReturn(res, out, "9001", "json param not found");
			return null;
		}
		
		if ( info.message == null || info.message.length() == 0 || 
				strProjNum == null || strProjNum.length() == 0 ) {
			UserLog.Log(tag, "message invalid");
			errorReturn(res, out, "9002", "message invalid");
			return null;
		}
		
		SqlSession sqlSession = ConnectionFactory.getSession().openSession();
		//
		try {
			UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
			info.user_seq =userMapper.getPushUserSeqA(strProjNum); 
			//
			userMapper.addCustomerMessage(info);
			sqlSession.commit();
			//
		
		} catch ( Exception e ) {
			sqlSession.rollback();
			errorReturn(res, out, "9999", e.getMessage());
			return null;

		} finally {
			sqlSession.close();
		}
		
		putEx(res, "result", "yes");

		return res;
	}
	
	//
	public JSONObject send_customer_message_i_req(HttpServletRequest request, HttpServletResponse response, 
			PrintWriter out, JSONObject jo, JSONObject jsres) throws ServletException, IOException {

		JSONObject res = jsres;
		CustomerMessageInfo info = new CustomerMessageInfo();
		String strAppId = null;
		
		//
		try {
			info.device_seq = Integer.parseInt(jo.get("device_seq").toString());
			//info.user_seq = Integer.parseInt(jo.get("user_seq").toString());
			strAppId = jo.get("app_id").toString();
			info.message = jo.get("message").toString();

		} catch ( Exception e ) {
			UserLog.Log(tag, "<<< json param Block -> some json param not found >>>");
			errorReturn(res, out, "9001", "json param not found");
			return null;
		}
		
		if ( info.message == null || info.message.length() == 0 || 
				strAppId == null || strAppId.length() == 0 ) {
			UserLog.Log(tag, "message invalid");
			errorReturn(res, out, "9002", "message invalid");
			return null;
		}
		
		SqlSession sqlSession = ConnectionFactory.getSession().openSession();
		//
		try {
			UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
			info.user_seq =userMapper.getPushUserSeqI(strAppId); 
			//
			userMapper.addCustomerMessage(info);
			sqlSession.commit();
			//
		
		} catch ( Exception e ) {
			sqlSession.rollback();
			errorReturn(res, out, "9999", e.getMessage());
			return null;

		} finally {
			sqlSession.close();
		}
		
		putEx(res, "result", "yes");

		return res;
	}
	
	// send_email_req
	public JSONObject send_email_req(HttpServletRequest request, HttpServletResponse response, 
			PrintWriter out, JSONObject jo, JSONObject jsres) throws ServletException, IOException {

		JSONObject res = jsres;
		//
		return res;
//		//
//		int nUserSeq = 0;
//		String strEmail = null;
//		String strPushName = null;
//		//
//		String strJsonData = null;
//		try {
//			strEmail = jo.get("user_id").toString();
//			strPushName = jo.get("push_name").toString();
//			strJsonData = jo.get("json_data").toString();
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			UserLog.Log(tag, "<<< json param Block -> some json param not found >>>");
//			errorReturn(res, out, "9001", "some json param not found");
//			return null;
//		}
//		
//		String strUserData = null;
//		
//		SqlSession sqlSession = ConnectionFactory.getSession().openSession();
//		//
//		try {
//			UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
//			userMapper.addMessageCount(m_id);
//			sqlSession.commit();
//			//
//			strUserData = userMapper.getUserData(m_id);
//			
//		} catch ( Exception e ) {
//			sqlSession.rollback();
//			errorReturn(res, out, "9999", e.getMessage());
//			return null;
//
//		} finally {
//			sqlSession.close();
//		}
//		
//		putEx(res, "result", "yes");
//		putEx(res, "user_data", strUserData);
//
//		return res;
	}
	
	// send_ext_push_req
	public JSONObject send_ext_push_req(HttpServletRequest request, HttpServletResponse response, 
			PrintWriter out, JSONObject jo, JSONObject jsres) throws ServletException, IOException {

		JSONObject res = jsres;
		//
		String strSiteId = null;
		String strAlert = null;
		String strMessage = null;
		
		try {
			strSiteId = jo.get("site_id").toString();
			strAlert = jo.get("alert").toString();
			strMessage = jo.get("message").toString();

		} catch ( Exception e ) {
			UserLog.Log(tag, "<<< json param Block -> some json param not found >>>");
			errorReturn(res, out, "9001", "json param not found");
			return null;
		}
		
		//
		PushResponse pr = send_link_push_req(strSiteId, strAlert, strMessage);
		//UserLog.Log(tag, pr.result + " / total: " + pr.total_count + " / send: " + pr.send_count);
		
		//if ( !pr.result.equals("yes") ) {
		if ( pr == null || !pr.result.equals("yes") ) {
			//UserLog.Log(tag, pr.result + " / total: " + pr.total_count + " / send: " + pr.send_count);
			errorReturn(res, out, "3001", "send push failure");
			return null;
		}
		putEx(res, "result", "yes");
		putEx(res, "total_count", pr.total_count);
		putEx(res, "send_count", pr.send_count);
		//
		return res;
	}
	
//	// delete_ext_id_req
//	public JSONObject delete_ext_id_req(HttpServletRequest request, HttpServletResponse response, 
//			PrintWriter out, JSONObject jo, JSONObject jsres) throws ServletException, IOException {
//
//		JSONObject res = jsres;
//		//
//		String strSiteId = null;
//		
//		try {
//			strSiteId = jo.get("site_id").toString();
//
//		} catch ( Exception e ) {
//			UserLog.Log(tag, "<<< json param Block -> some json param not found >>>");
//			errorReturn(res, out, "9001", "json param not found");
//			return null;
//		}
//		
//		//
//		SqlSession sqlSession = ConnectionFactory.getSession().openSession();
//		//
//		try {
//			UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
//			userMapper.deleteDeviceMap(strSiteId);
//			sqlSession.commit();
//			
//		} catch ( Exception e ) {
//			sqlSession.rollback();
//			errorReturn(res, out, "9999", e.getMessage());
//			return null;
//
//		} finally {
//			sqlSession.close();
//		}
//		
//		putEx(res, "result", "yes");
//
//		return res;
//	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	List<Integer> stringToIntList(String strArray) {
		List<Integer> lResult = new ArrayList<Integer>();
		if ( strArray == null || strArray.length() < 3 )
			return lResult;

		String strParam = strArray;
		strParam = strParam.replace("{", "");
		strParam = strParam.replace("}", "");
		//strParam = strParam.replace(" ", "");
		String[] arrParam = strParam.split(",");
		//
		//UserLog.Log(tag, "stringToIntList -> [" + strParam + "]");
		if ( arrParam.length == 1 && arrParam[0].equals("") )
			return lResult;
		//
		for ( String strData : arrParam ) {
			int nParam = Integer.parseInt(strData);
			lResult.add(nParam);
		}

		// 순방향 정렬
		//Collections.sort(lResult);
		// 역방향 정렬
		//Collections.reverse(lResult);

		return lResult;
	}
	
	List<String> stringToStringList(String strArray) {
		List<String> lResult = new ArrayList<String>();
		if ( strArray == null || strArray.length() < 3 )
			return lResult;
				
		String strParam = strArray;
		strParam = strParam.replace("{", "");
		strParam = strParam.replace("}", "");
		//strParam = strParam.replace(" ", "");
		String[] arrParam = strParam.split(",");
		//
		//UserLog.Log(tag, "stringToStringList -> [" + strParam + "]");
		if ( arrParam.length == 1 && arrParam[0].equals("") )
			return lResult;
		//
		for ( String strData : arrParam ) {
			lResult.add(strData);
		}

		// 순방향 정렬
		//Collections.sort(lResult);
		// 역방향 정렬
		//Collections.reverse(lResult);

		return lResult;
	}
	
	// array add(+) 연산
	List<Integer> addToIntList(List<Integer> toArray, List<Integer> addArray) {
		//
		Set<Integer> sData = new HashSet<Integer>();
		if ( toArray != null )
			sData.addAll(toArray);
		if ( addArray != null )
			sData.addAll(addArray);
		//
		List<Integer> lResult = new ArrayList<Integer>(sData);
		
		return lResult;
	}
	
	List<String> addToList(List<String> toArray, List<String> addArray) {
		//
		Set<String> sData = new HashSet<String>();
		sData.addAll(toArray);
		sData.addAll(addArray);
		//
		List<String> lResult = new ArrayList<String>(sData);
		
		return lResult;
	}
	
	// array minus(-) 연산
	List<Integer> removeFromIntList(List<Integer> fromArray, List<Integer> removeArray) {
		if ( fromArray == null ) {
			return null;
		}
		List<Integer> lResult = new ArrayList<Integer>(fromArray);
		if ( removeArray != null )
			lResult.removeAll(removeArray);
		
		return lResult;
	}
	
	List<String> removeFromList(List<String> fromArray, List<String> removeArray) {
		List<String> lResult = new ArrayList<String>(fromArray);
		lResult.removeAll(removeArray);
		
		return lResult;
	}
	
	//////////
}
