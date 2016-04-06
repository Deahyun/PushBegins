package network;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class UserMail {

	String strDebug = null;
	
	public UserMail() {
	}
	
	//public String sendMail(String mail_from, String mail_to, String title, String contents) {
	public String sendMail(String mail_to, String title, String contents) {
		String strResult = "no";
		
		////
        try {
            mail_to = new String(mail_to.getBytes("UTF-8"), "UTF-8");
            
            String strPort = "465";
            Properties props = new Properties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", strPort);
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.socketFactory.port", strPort);
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.fallback", "false");
            props.put("mail.smtp.auth", "true");
 
            Authenticator auth = new SMTPAuthenticator();
 
            Session sess = Session.getDefaultInstance(props, auth);
 
            MimeMessage msg = new MimeMessage(sess);
 
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(mail_to));
            msg.setSubject(title, "UTF-8");
            msg.setContent(contents, "text/html; charset=UTF-8");
            msg.setHeader("Content-type", "text/html; charset=UTF-8");
 
            Transport.send(msg);
            
            strResult = "yes";
 
            //response.sendRedirect("request_complete.jsp");
            
        } catch (Exception e) {
        	e.printStackTrace();
            //response.sendRedirect("request_failed.jsp");
        	
        } finally {
 
        }
		////
		
		return strResult; 
	}
}
