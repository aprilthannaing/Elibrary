package com.elibrary.service.impl;

import java.util.ArrayList;
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
public class AdvertisementServiceImpl implements AdvertisementService {

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
	public long countActiveAdvertisement() {
		String query = "select count(*) from Advertisement where entityStatus='" + EntityStatus.ACTIVE + "'";
		return advertisementDao.findLongByQueryString(query).get(0);
	}

	@Override
	public List<Advertisement> getAll() {
		String query = "select advertisement from Advertisement advertisement where entityStatus='" + EntityStatus.ACTIVE + "'";
		List<Advertisement> advertisements = advertisementDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(advertisements))
			return new ArrayList<Advertisement>();
		return advertisements;
	}

	@Override
	public Advertisement findByBoId(String boId) {
		String query = "select advertisement from Advertisement advertisement where boId='" + boId + "'and entityStatus='" + EntityStatus.ACTIVE + "'";
		List<Advertisement> advertisement = advertisementDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(advertisement))
			return null;
		return advertisement.get(0);
	}
}
