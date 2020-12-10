package com.elibrary.controller;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.elibrary.service.SessionService;

@Service
public class AbstractController {

	@Autowired
	private SessionService sessionService;

	public final String authorization = "7M8N3SLQ8QIKDJOSEPXJKJDFOZIN1NBO";

	public static DecimalFormat df2 = new DecimalFormat("#.##");

	public boolean isTokenRight(String token) {
		return sessionService.findByBoId(token) != null;
	}

	public String dateFormat() {
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDateTime now = LocalDateTime.now();
		return dateFormat.format(now);
	}

	public static String getRandomNumberString() {
		Random rnd = new Random();
		int number = rnd.nextInt(999999);
		return String.format("%06d", number);
	}

	public String generateSession(Long id) {

		char[] chars = id.toString().toCharArray();
		String key = "S1S2S3";
		int iterations = 500;
		byte[] salt = key.getBytes();
		String hashPass = "";
		PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 2);
		SecretKeyFactory skf;
		try {
			skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			byte[] hash = skf.generateSecret(spec).getEncoded();
			hashPass = Hex.encodeHexString(hash);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
		return hashPass;
	}
	
	public String initialName(String fullname) {
		String output = "";
		
		if(!fullname.equals("")) {
			String[] names = fullname.split(" "); 
			if(names.length > 1) {
				String fname = names[0];
				String lname = names[names.length-1];
				output = fname.substring(0,1).toUpperCase() + lname.substring(0,1).toUpperCase();
			}else {
				output = names[0].substring(0,1).toUpperCase();
			}
			 
		}
		return output;
	}

}
