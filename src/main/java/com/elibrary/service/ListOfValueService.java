package com.elibrary.service;

import java.util.List;

import com.elibrary.entity.Constituency;
import com.elibrary.entity.Department;
import com.elibrary.entity.header;
import com.elibrary.entity.Hluttaw;
import com.elibrary.entity.Position;

public interface ListOfValueService {
	public String save(header header);
	public String saveHluttaw(Hluttaw req);
	public header checkData(String code);
	public List<Hluttaw> checkHluttaw(String hboid);
	public List<Department> checkDepartment(long hboid);
	public List<Hluttaw> checkHluttawByBoId(String boid);
	public long savePosition(Position req);
	public List<Position> checkPosition();
	public List<Hluttaw> getHluttaw();
	public List<Department> checkDepartmentbyBoid(String boid);
	public List<Position> getPositionbyBoId(String boid);
	public Department checkDepartmentbyId(long id);
	public Hluttaw checkHluttawById(long id);
	public Position getPositionbyId(long id) ;
	public long saveDepartment(Department req);
	public List<Department> checkDepartmentAll();
	public List<Constituency> checkConstituency(long hboid);
	public long saveConstituency(Constituency req);
	public List<Hluttaw> getHluttawByRepresentative();
	public Constituency getConstituencyById(long id);
}
