package com.elibrary.service.impl;

import java.security.Security;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.elibrary.entity.MailEvent;
import com.elibrary.service.MailService;
import com.sun.mail.smtp.SMTPTransport;

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

		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", true);
		props.put("mail.smtp.port", "465");
		props.put("mail.smtp.starttls.required", true);
		
		// get Session
		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("apriltn19@gmail.com", "April123!@#");
			}
		});
		session.setDebug(true);
		
		// compose message
		try {
			MimeMessage message = new MimeMessage(session);
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			message.setSender(new InternetAddress("apriltn19@gmail.com"));
			message.setSubject("", "UTF-8");
			message.setText("", "UTF-8");

			// send message
			logger.info("To Email: " + message.getRecipients(Message.RecipientType.TO).toString());
			logger.info("From Email: " + message.getSender().toString());
			Transport.send(message);
			logger.info("Mail sent successfully...");

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
//	public void sendMail(String to, String subject, String content){
//
//	    String mailAddress = "aprilthannaing@securelinkmm.com";
//	    String mailPassword = "Aprilthannaing@2020";
//	        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
//	        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
//
//	        // Get a Properties object
//	        Properties props = System.getProperties();
//	        props.setProperty("mail.smtps.host", "smtp.securelinkmm.com");
//	        props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
//	        props.setProperty("mail.smtp.socketFactory.fallback", "false");
//	        props.setProperty("mail.smtp.port", "587");
//	        props.setProperty("mail.smtp.socketFactory.port", "587");
//	        props.setProperty("mail.smtps.auth", "true");
//
//	        props.put("mail.smtps.quitwait", "false");
//
//	        Session session = Session.getInstance(props, null);
//
//	        // -- Create a new message --
//	        final MimeMessage msg = new MimeMessage(session);
//
//	        // -- Set the FROM and TO fields --
//	        try {
//				msg.setFrom(new InternetAddress(mailAddress));
//			
//				msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
//
//				msg.setSubject(subject);
//				msg.setText(content, "utf-8");
//				msg.setSentDate(new Date());
//
//				SMTPTransport t = (SMTPTransport) session.getTransport("smtps");
//
//				t.connect("gateway30.websitewelcome.com", mailAddress, mailPassword);
//				t.sendMessage(msg, msg.getAllRecipients());
//				t.close();
//	        } catch (AddressException e) {
//				e.printStackTrace();
//			} catch (MessagingException e) {
//				e.printStackTrace();
//			}
//
//	}

}
