package com.elibrary.service;

import java.util.List;

import com.elibrary.entity.Department;
import com.elibrary.entity.Header;
import com.elibrary.entity.Hluttaw;
import com.elibrary.entity.Position;

public interface ListOfValueService {
	public String save(Header header);
	public String saveHluttaw(Hluttaw req);
	public Header checkData(String code);
	public List<Hluttaw> checkHluttaw(String hboid);
	public String saveDepartment(Department req);
	public List<Department> checkDepartment(long hboid);
	public List<Hluttaw> checkHluttawByBoId(String boid);
	public String savePosition(Position req);
	public List<Position> checkPosition();
	public List<Hluttaw> getHluttaw();
	public List<Department> checkDepartmentbyBoid(String boid);
	public List<Position> getPositionbyBoId(String boid);
	public Department checkDepartmentbyId(long id);
	public Hluttaw checkHluttawById(long id);
	public Position getPositionbyId(long id) ;
}
