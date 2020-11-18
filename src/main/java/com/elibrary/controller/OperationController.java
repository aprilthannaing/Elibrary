package com.elibrary.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.elibrary.entity.Author;
import com.elibrary.entity.Book;
import com.elibrary.entity.Category;
import com.elibrary.entity.Department;
import com.elibrary.entity.EntityStatus;
import com.elibrary.entity.Hluttaw;
import com.elibrary.entity.Journal;
import com.elibrary.entity.Position;
import com.elibrary.entity.State;
import com.elibrary.entity.SubCategory;
import com.elibrary.entity.SystemConstant;
import com.elibrary.entity.User;
import com.elibrary.entity.UserRole;
import com.elibrary.entity.UserType;
import com.elibrary.entity.Views;
import com.elibrary.entity.listOfValue;
import com.elibrary.entity.listOfValueObj;
import com.elibrary.service.AuthorService;
import com.elibrary.service.BookService;
import com.elibrary.service.CategoryService;
import com.elibrary.service.JournalService;
import com.elibrary.service.SubCategoryService;
import com.elibrary.service.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import com.mchange.rmi.ServiceUnavailableException;

@RestController
@RequestMapping("operation")
public class OperationController {

	@Autowired
	private BookService bookService;

	@Autowired
	private JournalService journalService;

	@Autowired
	private UserService userService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private SubCategoryService subCategoryService;

	@Autowired
	private AuthorService authorService;

	private static Logger logger = Logger.getLogger(OperationController.class);

	@RequestMapping(value = "saveBook", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Detailed.class)
	public Book saveBook() throws ServiceUnavailableException {

		Category category = new Category();
		category.setBoId("50843731");

		Book book = new Book();
		book.setId(001);
		book.setBoId("qr4824");
		book.setCategory(category);
		book.setISBN("2034962472");
		book.setPublisher("John");
		book.setEdition(3);
		book.setPublishedYear("1999");
		book.setPdfLink("http://rwqyireow;irwhe4892784612");
		book.setTitle("Beyond the hills");
		book.setCoverPhoto("book1.img");
		book.setPublishedDate("12.01.1999");
		book.setVolume(3);
		book.setState(State.Publish);
		book.setModifiedDate("02.12.2000");
		book.setCreatedDate("16.10.2020");
		book.setEntityStatus(EntityStatus.ACTIVE);
		bookService.save(book);

		return book;

	}

	@RequestMapping(value = "saveUser", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public User saveUser() throws ServiceUnavailableException {

		Hluttaw hluttaw = new Hluttaw();
		hluttaw.setBoId("iruewiro");

		Department department = new Department();
		department.setBoId("94723824");

		Position position = new Position();
		position.setBoId("fiowurwp");

		User user = new User();
		user.setId(428);
		user.setBoId("502394");
		user.setHluttaw(hluttaw);
		user.setDepartment(department);
		user.setPosition(position);
		user.setName("Alex");
		user.setEmail("eieikhine@gmail.com");
		user.setPhoneNo("487239420");
		user.setPassword("fiskfshdfis");
		user.setRole(UserRole.Admin);
		user.setType(UserType.Staff);
		user.setModifiedDate("10.03.2020");
		user.setCreatedDate("15.06.2020");
		user.setEntityStatus(EntityStatus.ACTIVE);
		userService.save(user);

		return user;
	}

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "savecategory", method = RequestMethod.POST)
	@JsonView(Views.Summary.class)
	public void saveSubCategory(@RequestBody JSONObject json) throws ServiceUnavailableException {
		logger.info("json: " + json);
		Category category = new Category();
		category.setBoId(SystemConstant.BOID_REQUIRED);
		List<SubCategory> subCategoryList = new ArrayList<SubCategory>();		
		List<Object> subCategories = (List<Object>) json.get("subcategories");
		for(Object object : subCategories) {
			SubCategory subCategory = subCategoryService.findByBoId(object.toString());
			if(subCategory != null)
				subCategoryList.add(subCategory);
		}
		category.getSubCategories().addAll(subCategoryList);
		category.setName(json.get("category").toString());
		category.setEntityStatus(EntityStatus.ACTIVE);
		categoryService.save(category);
	}

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "savesubcategory", method = RequestMethod.POST)
	@JsonView(Views.Summary.class)
	public JSONObject saveCategory(@RequestBody JSONObject json) throws ServiceUnavailableException {
		JSONObject result = new JSONObject();
		SubCategory subCategory = new SubCategory();
		subCategory.setBoId(SystemConstant.BOID_REQUIRED);
		Object description = json.get("description");
		if (description == null || description.toString().isEmpty()) {
			result.put("status", "0");
			return result;
		}

		subCategory.setName(description.toString());
		subCategory.setEntityStatus(EntityStatus.ACTIVE);
		subCategoryService.save(subCategory);
		result.put("status", "1");
		return result;
	}

	@RequestMapping(value = "saveAuthor", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public Author saveAuthor() throws ServiceUnavailableException {

		Author author = new Author();
		author.setId(001);
		author.setBoId("fdishfosd");
		//author.setEngName("Johnson");
		//author.setMyanmarName("ပုညခင္");
		author.setEntityStatus(EntityStatus.ACTIVE);
		authorService.save(author);

		return author;
	}

	@RequestMapping(value = "deleteJournal", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public String deleteJournal() throws ServiceUnavailableException {
		String message = "success";
		Journal journal = new Journal();
//		journal.setId(1);
//		journal.setBoId("fksjaf;sj");
//		journal.setWeeklyNo("3");
//		journal.setTitle("Myanmar");
//		journal.setCoverPhoto("journal1.img");
//		journal.setPublishedDate("23.04.2020");
//		journal.setState(State.Publish);
//		journal.setModifiedDate("10.10.2020");
//		journal.setCreatedDate("12.02.2020");
//		journal.setEntityStatus(EntityStatus.INACTIVE);
		journalService.delete(journal);
		return message;
	}
}