package com.elibrary.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.elibrary.dao.AdvertisementDao;
import com.elibrary.dao.impl.AdvertisementDaoImpl;
import com.elibrary.entity.Advertisement;
import com.elibrary.entity.EntityStatus;
import com.elibrary.service.AdvertisementService;
import com.mchange.rmi.ServiceUnavailableException;

@Service("advertisementService")
public class AdvertisementServiceImpl implements AdvertisementService{
	
	@Autowired
	private AdvertisementDao advertisementDao;
	
	public static Logger logger = Logger.getLogger(AdvertisementDaoImpl.class);
	
	@Override
	public void save(Advertisement advertisement) throws ServiceUnavailableException {
		try {
			if (advertisement.isBoIdRequired(advertisement.getBoId()))
				advertisement.setBoId(getBoId());
			advertisementDao.saveOrUpdate(advertisement);
		} catch (com.mchange.rmi.ServiceUnavailableException e) {
			logger.error("Error: " + e.getMessage());
		}

	}

	private Long plus() {
		return countAdvertisement() + 10000;
	}
	
	@Override
	public long countAdvertisement() {
		String query = "select count(*) from Advertisement";
		return advertisementDao.findLongByQueryString(query).get(0);
	}

	public String getBoId() {
		return "Advertisement" + plus();
	}

	@Override
	public List<Advertisement> getall() {
		String query = "select advertisement from Advertisement advertisement where entityStatus='" + EntityStatus.ACTIVE + "'";
		List<Advertisement> advertisements = advertisementDao.getEntitiesByQuery(query); 
		if (CollectionUtils.isEmpty(advertisements))
			return null;
		return advertisements;
	}
}
