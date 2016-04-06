package network;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;


public class SMTPAuthenticator extends Authenticator {
	public SMTPAuthenticator() {
		super();
	}

	public PasswordAuthentication getPasswordAuthentication() {
		String username = "push.sending@gmail.com";
		String password = "push2345";
		//
		return new PasswordAuthentication(username, password);
	}
}