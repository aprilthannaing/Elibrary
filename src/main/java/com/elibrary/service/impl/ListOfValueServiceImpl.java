package com.elibrary.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.elibrary.dao.ConstituencyDao;
import com.elibrary.dao.DepartmentDao;
import com.elibrary.dao.ListOfValueDao;
import com.elibrary.dao.PositionDao;
import com.elibrary.dao.impl.BookDaoImpl;
import com.elibrary.entity.Constituency;
import com.elibrary.entity.Department;
import com.elibrary.entity.EntityStatus;
import com.elibrary.entity.header;
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
	
	@Autowired
	private ConstituencyDao constituencyDao;
	
	public static Logger logger = Logger.getLogger(BookDaoImpl.class);
	
	public String save(header req){
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
	
	public header checkData(String boid) {
		header header = new header();
		String query = "from Header where boId='" + boid + "' and entityStatus='" + EntityStatus.ACTIVE.toString() + "'";
		List<header> headerList = listOfValueDao.byQuery(query);
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
	
	public long saveDepartment(Department req){
		try {
			long id = 0;
			if (req.isIdRequired(req.getId())) {
				id = getIdByDepartment();
				req.setBoId("Dept" + id);
			}else 
				id = req.getId(); 
			
			if(departmentDao.checkSaveOrUpdate(req))
				return id;
			else 
				return 0;
			
		}catch(com.mchange.rmi.ServiceUnavailableException e){
			logger.error("Error: "+ e.getMessage());
			
		}
		return 0;
	}
	
	public long getIdByDepartment() {
		String query = "select max(id) from Department";
		List<Long> idList = departmentDao.findLongByQueryString(query);
		if(idList.get(0) == null)
			return 1;
		return idList.get(0) + 1;
	} 
	
	public List<Department> checkDepartment(long hboid) {
		String query = "from Department where hluttawboId=" + hboid;
		List<Department> deptList = departmentDao.byQuery(query);
		return deptList;
	}
	
	public List<Department> checkDepartmentAll() {
		String query = "from Department";
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
		public long savePosition(Position req){
			try {
				long id = 0;
				if (req.isIdRequired(req.getId())) {
					id = countPositionId();
					req.setBoId("P" + id);
				}
				if(positionDao.checkSaveOrUpdate(req))
					return id;
				else 
					return 0;
				
			}catch(com.mchange.rmi.ServiceUnavailableException e){
				logger.error("Error: "+ e.getMessage());
				
			}
			return 0;
		}
		
		public long countPositionId() {
			String query = "select max(id) from Position";
			List<Long> idList = positionDao.findLongByQueryString(query);
			if(idList.get(0) == null)
				return 1;
			return idList.get(0) + 1;
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
		
		public List<Constituency> checkConstituency(long hboid) {
			String query = "from Constituency where hluttawboId=" + hboid;
			List<Constituency> deptList = constituencyDao.byQuery(query);
			return deptList;
		}
		
		public long saveConstituency(Constituency req){
			try {
				long id = 0;
				if (req.isIdRequired(req.getId())) {
					id = getIdByConstituency();
					req.setBoId("Dept" + id);
				}else 
					id = req.getId(); 
				
				if(constituencyDao.checkSaveOrUpdate(req))
					return id;
				else 
					return 0;
				
			}catch(com.mchange.rmi.ServiceUnavailableException e){
				logger.error("Error: "+ e.getMessage());
				
			}
			return 0;
		}
		
		public long getIdByConstituency() {
			String query = "select max(id) from Constituency";
			List<Long> idList = departmentDao.findLongByQueryString(query);
			if(idList.get(0) == null)
				return 1;
			return idList.get(0) + 1;
		} 
		
		public List<Hluttaw> getHluttawByRepresentative() {
			String query = "from Hluttaw where boId <> 'H2'";
			List<Hluttaw> hluttawList = hluttawDao.byQuery(query);
			return hluttawList;
		}
		
		public Constituency getConstituencyById(long id) {
			String query = "from Constituency where id=" + id ;
			List<Constituency> constList = constituencyDao.byQuery(query);
			return constList.get(0);
		}
	
}
