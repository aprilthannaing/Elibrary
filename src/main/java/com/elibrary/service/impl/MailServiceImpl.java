package com.elibrary.service.impl;

import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.elibrary.entity.MailEvent;
import com.elibrary.service.MailService;

@Service("mailService")
@Transactional(readOnly = true)
public class MailServiceImpl implements MailService {

	private static Logger logger = Logger.getLogger(MailServiceImpl.class);

	ConcurrentLinkedQueue<MailEvent> queue = new ConcurrentLinkedQueue<MailEvent>();
	ConcurrentLinkedQueue<MailEvent> taxReportQueue = new ConcurrentLinkedQueue<MailEvent>();

	public void sentMail(MailEvent mailEvent) {
		sendMail(mailEvent.getTo(), mailEvent.getSubject(), mailEvent.getContent());
	}

	public void sendMail(String to, String subject, String content) {

		// Get properties object
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.securelinkmm.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");

		// get Session
		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("khainglaelaesoe@securelinkmm.com", "Kh@!ng1851987");
			}
		});
		session.setDebug(true);
		
		// compose message
		try {
			MimeMessage message = new MimeMessage(session);
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			message.setSender(new InternetAddress("khainglaelaesoe@securelinkmm.com"));
			message.setSubject(subject, "UTF-8");
			message.setText(content, "UTF-8");

			// send message
			logger.info("To Email: " + message.getRecipients(Message.RecipientType.TO).toString());
			logger.info("From Email: " + message.getSender().toString());
			Transport.send(message);
			logger.info("Mail sent successfully...");

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

}
