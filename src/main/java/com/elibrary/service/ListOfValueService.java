package com.elibrary.service;

import java.util.List;

import com.elibrary.entity.Department;
import com.elibrary.entity.Header;
import com.elibrary.entity.Hluttaw;

public interface ListOfValueService {
	public String save(Header header);
	public String saveHluttaw(Hluttaw req);
	public Header checkData(String code);
	public List<Hluttaw> checkHluttaw(String hboid);
	public String saveDepartment(Department req);
	public List<Department> checkDepartment(String hboid);
	public List<Hluttaw> checkHluttawById(String boid);
}
