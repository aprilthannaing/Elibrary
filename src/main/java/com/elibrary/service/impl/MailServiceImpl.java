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

	@Override
	public void sendMail(String to, String subject, String content) {

		Properties props = new Properties();
		props.put("mail.smtp.ssl.trust", "*");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		// get Session
		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("myanmarportal2@gmail.com", "portal12345#");// "myanmarportal2@gmail.com" , "portal12345#"

			}
		});
		session.setDebug(true);

		// compose message
		try {
			MimeMessage message = new MimeMessage(session);
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			message.setSender(new InternetAddress("myanmarportal2@gmail.com"));
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
