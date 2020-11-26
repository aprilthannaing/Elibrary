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
	public listOfValueObj departmentSetup(@RequestBody listOfValueObj req){
		Department dept = new Department();
		Hluttaw htaw = listOfValueService.checkHluttawById(Long.parseLong(req.getCode()));
			List<Department> deptList = listOfValueService.checkDepartment(htaw.getId());
			if(deptList.size() > 0) {
				for(int i = 0 ; i < req.getLov().length; i ++) {
					for(int j = 0 ; j < deptList.size(); j ++) {
						String id = String.valueOf(deptList.get(j).getId());
						if(id.equals(req.getLov()[i].getValue())) {
							if(req.getLov()[i].getStatus().equals("INACTIVE"))
								deptList.get(j).setEntityStatus(EntityStatus.INACTIVE);
							deptList.get(j).setCode(req.getLov()[i].getCode());
							deptList.get(j).setName(req.getLov()[i].getCaption());
							long value = listOfValueService.saveDepartment(deptList.get(j));
							req.getLov()[i].setValue(value+"");
						}
					}
					if(req.getLov()[i].getValue().equals("")){
						dept = new Department();
						dept.setHluttaw(htaw);
						dept.setEntityStatus(EntityStatus.ACTIVE);
						dept.setCode(req.getLov()[i].getCode());
						dept.setName(req.getLov()[i].getCaption());
						long value = listOfValueService.saveDepartment(dept);
						req.getLov()[i].setValue(value+"");
						req.getLov()[i].setStatus(dept.getEntityStatus().name());
					}
				}
				return req;
			}
			for(int i = 0 ; i < req.getLov().length; i ++) {
				dept = new Department();
				dept.setHluttaw(htaw);
				dept.setEntityStatus(EntityStatus.ACTIVE);
				dept.setCode(req.getLov()[i].getCode());
				dept.setName(req.getLov()[i].getCaption());
				long value = listOfValueService.saveDepartment(dept);
				req.getLov()[i].setValue(value+"");
			}
			return req;
		
	}
	
	@RequestMapping(value = "positionSetup", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public listOfValueObj positionSetup(@RequestBody listOfValueObj req){
		Position position = new Position();
			List<Position> postitionList = listOfValueService.checkPosition();
			if(postitionList.size() > 0) {
				for(int i = 0 ; i < req.getLov().length; i ++) {
					for(int j = 0 ; j < postitionList.size(); j ++) {
						String id = String.valueOf(postitionList.get(j).getId());
						if(id.equals(req.getLov()[i].getValue())) {
							if(req.getLov()[i].getStatus().equals("INACTIVE"))
								postitionList.get(j).setEntityStatus(EntityStatus.INACTIVE);
							postitionList.get(j).setCode(req.getLov()[i].getCode());
							postitionList.get(j).setName(req.getLov()[i].getCaption());
							long value = listOfValueService.savePosition(postitionList.get(j));
							req.getLov()[i].setValue(value+"");
						}
					}
					if(req.getLov()[i].getValue().equals("")){
						position = new Position();
						position.setEntityStatus(EntityStatus.ACTIVE);
						position.setCode(req.getLov()[i].getCode());
						position.setName(req.getLov()[i].getCaption());
						long value = listOfValueService.savePosition(position);
						req.getLov()[i].setValue(value+"");
						req.getLov()[i].setStatus(position.getEntityStatus().name());
					}
				}
				return req;
			}
			for(int i = 0 ; i < req.getLov().length; i ++) {
				position = new Position();
				position.setEntityStatus(EntityStatus.ACTIVE);
				position.setCode(req.getLov()[i].getCode());
				position.setName(req.getLov()[i].getCaption());
				long value = listOfValueService.savePosition(position);
				req.getLov()[i].setValue(value+"");
			}
			return req;
		
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
			json.put("code", deptList.get(i).getCode());
			json.put("status", deptList.get(i).getEntityStatus().name());
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
			json.put("code", posList.get(i).getCode());
			json.put("status", posList.get(i).getEntityStatus().name());
			jsonArr[i] = json;
		}
		jsonResponse.put("refPosition", jsonArr);
		return jsonResponse;
	}
	@RequestMapping(value = "getDepartmentAll", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public JSONObject getDepartmentAll(@RequestBody String req){
		JSONObject jsonResponse = new JSONObject();
		List<Department> deptList = listOfValueService.checkDepartmentAll();
		JSONObject[] jsonArr = new JSONObject[deptList.size()];
		for(int i=0; i< deptList.size(); i++) {
			JSONObject json = new JSONObject();
			json.put("value", deptList.get(i).getId());
			json.put("caption", deptList.get(i).getName());
			json.put("code", deptList.get(i).getCode());
			json.put("status", deptList.get(i).getEntityStatus().name());
			json.put("joinid",deptList.get(i).getHluttaw().getId());
			jsonArr[i] = json;
		}
		jsonResponse.put("refDept", jsonArr);
		
		return jsonResponse;
	}
	
}
