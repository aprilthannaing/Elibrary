package com.elibrary.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.elibrary.dao.EmailDao;
import com.elibrary.dao.impl.EmailDaoImpl;
import com.elibrary.entity.Email;
import com.elibrary.service.EmailService;
import com.mchange.rmi.ServiceUnavailableException;


@Service("emailService")
public class EmailServiceImpl implements EmailService{
	
	@Autowired
	private EmailDao emailDao;
	
	public static Logger logger = Logger.getLogger(EmailDaoImpl.class);

	public void save(Email email) throws ServiceUnavailableException {
		try {
			if (email.isIdRequired(email.getId()))
				email.setId(getId());

			if (email.isBoIdRequired(email.getBoId()))
				email.setBoId(getBoId());

			emailDao.saveOrUpdate(email);
		} catch (com.mchange.rmi.ServiceUnavailableException e) {
			logger.error("Error: " + e.getMessage());
		}
	}
	
	private long getId() {
		return countEmail() + 1;
	}

	private Long plus() {
		return countEmail() + 10000;
	}

	public long countEmail() {
		String query = "select count(*) from Email";
		return emailDao.findLongByQueryString(query).get(0);
	}

	public String getBoId() {
		return "Email" + plus();
	}



}
