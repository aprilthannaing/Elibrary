package com.elibrary.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.elibrary.dao.DepartmentDao;
import com.elibrary.dao.ListOfValueDao;
import com.elibrary.dao.PositionDao;
import com.elibrary.dao.impl.BookDaoImpl;
import com.elibrary.entity.Department;
import com.elibrary.entity.EntityStatus;
import com.elibrary.entity.Header;
import com.elibrary.entity.Hluttaw;
import com.elibrary.entity.Position;
import com.elibrary.service.ListOfValueService;

@Service("listOfValueService")
	public class ListOfValueServiceImpl implements ListOfValueService{
	private ListOfValueDao listOfValueDao;
	
	@Autowired
	private com.elibrary.dao.HluttawDao hluttawDao;
	
	@Autowired
	private DepartmentDao departmentDao;
	
	@Autowired
	private PositionDao positionDao;
	
	public static Logger logger = Logger.getLogger(BookDaoImpl.class);
	
	public String save(Header req){
		try {
			if (req.isIdRequired(req.getId())) {
				long[] longlist = getId();
				req.setId(longlist[0]);
				req.setBoId("H" + longlist[1]);
			}
			if(listOfValueDao.checkSaveOrUpdate(req))
				return req.getBoId();
			else 
				return "";
			
		}catch(com.mchange.rmi.ServiceUnavailableException e){
			logger.error("Error: "+ e.getMessage());
			
		}
		return "";
	}
	
	private long[] getId() {
		long[] longList = new long[2];
		long id = countId();
		longList[0] = id + 1;
		longList[1] = id + 1000;
		return  longList;
	}
	
	
	public long countId() {
		String query = "select count(*) from Header";
		return listOfValueDao.findLongByQueryString(query).get(0);
	}
	
	public String saveHluttaw(Hluttaw req){
		try {
			if (req.isIdRequired(req.getId())) {
				long[] longlist = getIdByHluttaw();
				req.setId(longlist[0]);
				req.setBoId("HT" + longlist[1]);
			}
			if(hluttawDao.checkSaveOrUpdate(req))
				return req.getBoId();
			else 
				return "";
			
		}catch(com.mchange.rmi.ServiceUnavailableException e){
			logger.error("Error: "+ e.getMessage());
			
		}
		return "";
	}
	
	private long[] getIdByHluttaw() {
		long[] longList = new long[2];
		long id = countIdByHluttaw();
		longList[0] = id + 1;
		longList[1] = id + 1000;
		return  longList;
	}
	
	public long countIdByHluttaw() {
		String query = "select count(*) from Hluttaw";
		return hluttawDao.findLongByQueryString(query).get(0);
	}
	
	public Header checkData(String boid) {
		Header header = new Header();
		String query = "from Header where boId='" + boid + "' and entityStatus='" + EntityStatus.ACTIVE.toString() + "'";
		List<Header> headerList = listOfValueDao.byQuery(query);
		if (headerList.size() > 0) {
			header = headerList.get(0);
		}else header = null;
		return header;
	}
	
	public List<Hluttaw> checkHluttaw(String hboid) {
		String query = "from Hluttaw where hboId='" + hboid + "'";
		List<Hluttaw> hluttawList = hluttawDao.byQuery(query);
		return hluttawList;
	}
	//department
	public String saveDepartment(Department req){
		try {
			if (req.isIdRequired(req.getId())) {
				long[] longlist = getIdByDepartment();
				//req.setId(longlist[0]);
				req.setBoId("Dept" + longlist[1]);
			}
			if(departmentDao.checkSaveOrUpdate(req))
				return req.getBoId();
			else 
				return "";
			
		}catch(com.mchange.rmi.ServiceUnavailableException e){
			logger.error("Error: "+ e.getMessage());
			
		}
		return "";
	}
	
	private long[] getIdByDepartment() {
		long[] longList = new long[2];
		long id = countIdByDepartment();
		longList[0] = id + 1;
		longList[1] = id + 1000;
		return  longList;
	}
	
	public long countIdByDepartment() {
		String query = "select count(*) from Department";
		return departmentDao.findLongByQueryString(query).get(0);
	}
	
	public List<Department> checkDepartment(long hboid) {
		String query = "from Department where hluttawboId=" + hboid;
		List<Department> deptList = departmentDao.byQuery(query);
		return deptList;
	}
	
	public List<Department> checkDepartmentbyBoid(String boid) {
		String query = "from Department where boId='" + boid + "'";
		List<Department> deptList = departmentDao.byQuery(query);
		return deptList;
	}
	
	public List<Hluttaw> checkHluttawByBoId(String boid) {
		String query = "from Hluttaw where boId='" + boid + "'";
		List<Hluttaw> hluttawList = hluttawDao.byQuery(query);
		return hluttawList;
	}
	
	public List<Position> checkPosition() {
		String query = "from Position";
		List<Position> deptList = positionDao.byQuery(query);
		return deptList;
	}
	//position
	//department
		public String savePosition(Position req){
			try {
				if (req.isIdRequired(req.getId())) {
					long[] longlist = getIdByPosition();
					//req.setId(longlist[0]);
					req.setBoId("P" + longlist[1]);
				}
				if(positionDao.checkSaveOrUpdate(req))
					return req.getBoId();
				else 
					return "";
				
			}catch(com.mchange.rmi.ServiceUnavailableException e){
				logger.error("Error: "+ e.getMessage());
				
			}
			return "";
		}
		
		private long[] getIdByPosition() {
			long[] longList = new long[2];
			long id = countIdByPosition();
			longList[0] = id + 1;
			longList[1] = id + 1000;
			return  longList;
		}
		
		public long countIdByPosition() {
			String query = "select count(*) from Position";
			return positionDao.findLongByQueryString(query).get(0);
		}
		
		public List<Hluttaw> getHluttaw() {
			String query = "from Hluttaw";
			List<Hluttaw> hluttawList = hluttawDao.byQuery(query);
			return hluttawList;
		}
		
		public List<Position> getPositionbyBoId(String boid) {
			String query = "from Position where boid='"+ boid + "'";
			List<Position> posList = positionDao.byQuery(query);
			return posList;
		}
		
		public Department checkDepartmentbyId(long id) {
			String query = "from Department where id=" + id ;
			List<Department> deptList = departmentDao.byQuery(query);
			return deptList.get(0);
		}
		
		public Hluttaw checkHluttawById(long id) {
			String query = "from Hluttaw where id=" + id;
			List<Hluttaw> hluttawList = hluttawDao.byQuery(query);
			return hluttawList.get(0);
		}
		
		public Position getPositionbyId(long id) {
			String query = "from Position where id=" + id;
			List<Position> posList = positionDao.byQuery(query);
			return posList.get(0);
		}
	
}
