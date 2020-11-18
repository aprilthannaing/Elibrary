package com.elibrary.service;

import com.elibrary.entity.Email;
import com.mchange.rmi.ServiceUnavailableException;

public interface EmailService {
	
	public void save(Email email)throws ServiceUnavailableException;

}
