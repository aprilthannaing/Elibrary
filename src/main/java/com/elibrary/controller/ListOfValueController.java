package com.elibrary.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.elibrary.entity.Department;
import com.elibrary.entity.EntityStatus;
import com.elibrary.entity.Header;
import com.elibrary.entity.Hluttaw;
import com.elibrary.entity.Views;
import com.elibrary.entity.listOfValueObj;
import com.elibrary.service.ListOfValueService;
import com.fasterxml.jackson.annotation.JsonView;

@RestController
@RequestMapping("setUp")
public class ListOfValueController {
	
	@Autowired
	private ListOfValueService listOfValueService;
	
	@RequestMapping(value = "hluttawSetup", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public String hluttawSetup(@RequestBody listOfValueObj req){
		Header header = new Header();
		header = listOfValueService.checkData(req.getCode());
		if(header == null) {
			header = new Header();
			header.setName(req.getDescription());
			header.setEntityStatus(EntityStatus.ACTIVE);
			String boid = listOfValueService.save(header);
			for(int i = 0 ; i < req.getLov().length; i ++) {
				Hluttaw hluttaw = new Hluttaw();
				hluttaw.setHboId(boid);
				hluttaw.setEntityStatus(EntityStatus.ACTIVE);
				hluttaw.setCode(req.getLov()[i].getKey());
				hluttaw.setName(req.getLov()[i].getValue());
				listOfValueService.saveHluttaw(hluttaw);
			}
			return "Insert Successfully";
		}
		header.setName(req.getDescription());
		String boid = listOfValueService.save(header);
		List<Hluttaw> htawList = listOfValueService.checkHluttaw(boid);
		if(htawList.size() > 0) {
			for(int i = 0 ; i < req.getLov().length; i ++) {
				for(int j = 0 ; j < htawList.size(); j ++) {
					if(htawList.get(j).getBoId().equals(req.getLov()[i].getId())) {
						if(req.getLov()[i].getStatus().equals("002"))
							htawList.get(j).setEntityStatus(EntityStatus.INACTIVE);
						htawList.get(j).setCode(req.getLov()[i].getKey());
						htawList.get(j).setName(req.getLov()[i].getValue());
						listOfValueService.saveHluttaw(htawList.get(j));
					}
				}
				if(req.getLov()[i].getId().equals("")){
					Hluttaw hluttaw = new Hluttaw();
					hluttaw.setHboId(boid);
					hluttaw.setEntityStatus(EntityStatus.ACTIVE);
					hluttaw.setCode(req.getLov()[i].getKey());
					hluttaw.setName(req.getLov()[i].getValue());
					listOfValueService.saveHluttaw(hluttaw);
				}
			}
		}
		return "Update Successfully";
	}
	
	@RequestMapping(value = "hluttawRemove", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public String hluttawRemove(@RequestBody String req){
		Header header = listOfValueService.checkData(req);
		header.setEntityStatus(EntityStatus.INACTIVE);
		String boid = listOfValueService.save(header);
		return "";
	}
	
////////Department////////////////////
	@RequestMapping(value = "departmentSetup", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public String departmentSetup(@RequestBody listOfValueObj req){
		Department dept = new Department();
		List<Hluttaw> htawList = listOfValueService.checkHluttawById(req.getCode());
		if(htawList.size() > 0) {
			Hluttaw htaw = htawList.get(0);
			List<Department> deptList = listOfValueService.checkDepartment(htaw.getBoId());
			if(deptList.size() > 0) {
				for(int i = 0 ; i < req.getLov().length; i ++) {
					for(int j = 0 ; j < deptList.size(); j ++) {
						if(deptList.get(j).getBoId().equals(req.getLov()[i].getId())) {
							if(req.getLov()[i].getStatus().equals("002"))
								deptList.get(j).setEntityStatus(EntityStatus.INACTIVE);
							deptList.get(j).setCode(req.getLov()[i].getKey());
							deptList.get(j).setName(req.getLov()[i].getValue());
							listOfValueService.saveDepartment(deptList.get(j));
						}
					}
					if(req.getLov()[i].getId().equals("")){
						dept = new Department();
						dept.setHluttaw(htaw);
						dept.setEntityStatus(EntityStatus.ACTIVE);
						dept.setCode(req.getLov()[i].getKey());
						dept.setName(req.getLov()[i].getValue());
						listOfValueService.saveDepartment(dept);
					}
				}
				return "Update Successfully";
			}
			for(int i = 0 ; i < req.getLov().length; i ++) {
				dept = new Department();
				dept.setHluttaw(htaw);
				dept.setEntityStatus(EntityStatus.ACTIVE);
				dept.setCode(req.getLov()[i].getKey());
				dept.setName(req.getLov()[i].getValue());
				listOfValueService.saveDepartment(dept);
			}
			return "Insert Successfully";
		}
		return "Fail";
		
	}
	
	@RequestMapping(value = "departmentRemove", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public String departmentRemove(@RequestBody String req){
		Header header = listOfValueService.checkData(req);
		header.setEntityStatus(EntityStatus.INACTIVE);
		String boid = listOfValueService.save(header);
		return "";
	}
	
}
