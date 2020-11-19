package com.elibrary.controller;

import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.elibrary.entity.Department;
import com.elibrary.entity.EntityStatus;
import com.elibrary.entity.Hluttaw;
import com.elibrary.entity.Position;
import com.elibrary.entity.Views;
import com.elibrary.entity.listOfValueObj;
import com.elibrary.service.ListOfValueService;
import com.fasterxml.jackson.annotation.JsonView;

@RestController
@RequestMapping("setUp")
public class ListOfValueController {
	
	@Autowired
	private ListOfValueService listOfValueService;
	
////////Department////////////////////
	@RequestMapping(value = "departmentSetup", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public String departmentSetup(@RequestBody listOfValueObj req){
		Department dept = new Department();
		List<Hluttaw> htawList = listOfValueService.checkHluttawByBoId(req.getCode());
		if(htawList.size() > 0) {
			Hluttaw htaw = htawList.get(0);
			List<Department> deptList = listOfValueService.checkDepartment(htaw.getId());
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
	
	@RequestMapping(value = "positionSetup", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public String positionSetup(@RequestBody listOfValueObj req){
		Position position = new Position();
			List<Position> postitionList = listOfValueService.checkPosition();
			if(postitionList.size() > 0) {
				for(int i = 0 ; i < req.getLov().length; i ++) {
					for(int j = 0 ; j < postitionList.size(); j ++) {
						if(postitionList.get(j).getBoId().equals(req.getLov()[i].getId())) {
							if(req.getLov()[i].getStatus().equals("002"))
								postitionList.get(j).setEntityStatus(EntityStatus.INACTIVE);
							postitionList.get(j).setCode(req.getLov()[i].getKey());
							postitionList.get(j).setName(req.getLov()[i].getValue());
							listOfValueService.savePosition(postitionList.get(j));
						}
					}
					if(req.getLov()[i].getId().equals("")){
						position = new Position();
						position.setEntityStatus(EntityStatus.ACTIVE);
						position.setCode(req.getLov()[i].getKey());
						position.setName(req.getLov()[i].getValue());
						listOfValueService.savePosition(position);
					}
				}
				return "Update Successfully";
			}
			for(int i = 0 ; i < req.getLov().length; i ++) {
				position = new Position();
				position.setEntityStatus(EntityStatus.ACTIVE);
				position.setCode(req.getLov()[i].getKey());
				position.setName(req.getLov()[i].getValue());
				listOfValueService.savePosition(position);
			}
			return "Insert Successfully";
		
	}
	
	@RequestMapping(value = "getHluttaw", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public JSONObject getHluttaw(@RequestBody String req){
		JSONObject jsonResponse = new JSONObject();
		List<Hluttaw> htawList = listOfValueService.getHluttaw();
		JSONObject[] jsonArr = new JSONObject[htawList.size()];
		for(int i=0; i< htawList.size(); i++) {
			JSONObject json = new JSONObject();
			json.put("value", htawList.get(i).getId());
			json.put("caption", htawList.get(i).getName());
			jsonArr[i] = json;
		}
		jsonResponse.put("refHluttaw", jsonArr);
		return jsonResponse;
	}
	
	@RequestMapping(value = "getDepartment", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public JSONObject getDepartment(@RequestBody String req){
		JSONObject jsonResponse = new JSONObject();
		Hluttaw htawList = listOfValueService.checkHluttawById(Long.parseLong(req));
		List<Department> deptList = listOfValueService.checkDepartment(htawList.getId());
		JSONObject[] jsonArr = new JSONObject[deptList.size()];
		for(int i=0; i< deptList.size(); i++) {
			JSONObject json = new JSONObject();
			json.put("value", deptList.get(i).getId());
			json.put("caption", deptList.get(i).getName());
			jsonArr[i] = json;
		}
		jsonResponse.put("refDept", jsonArr);
		
		return jsonResponse;
	}
	
	@RequestMapping(value = "getPosition", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public JSONObject getPosition(@RequestBody String req){
		JSONObject jsonResponse = new JSONObject();
		List<Position> posList = listOfValueService.checkPosition();
		JSONObject[] jsonArr = new JSONObject[posList.size()];
		for(int i=0; i< posList.size(); i++) {
			JSONObject json = new JSONObject();
			json.put("value", posList.get(i).getId());
			json.put("caption", posList.get(i).getName());
			jsonArr[i] = json;
		}
		jsonResponse.put("refPosition", jsonArr);
		return jsonResponse;
	}
	
}
