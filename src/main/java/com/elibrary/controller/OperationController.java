package com.elibrary.controller;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.elibrary.entity.Author;
import com.elibrary.entity.Book;
import com.elibrary.entity.Category;
import com.elibrary.entity.CategoryType;
import com.elibrary.entity.Department;
import com.elibrary.entity.EntityStatus;
import com.elibrary.entity.Hluttaw;
import com.elibrary.entity.Journal;
import com.elibrary.entity.Magazine;
import com.elibrary.entity.Position;
import com.elibrary.entity.State;
import com.elibrary.entity.User;
import com.elibrary.entity.UserRole;
import com.elibrary.entity.UserType;
import com.elibrary.entity.Views;
import com.elibrary.service.AuthorService;
import com.elibrary.service.BookService;
import com.elibrary.service.CategoryService;
import com.elibrary.service.JournalService;
import com.elibrary.service.MagazineService;
import com.elibrary.service.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import com.mchange.rmi.ServiceUnavailableException;

@RestController
@RequestMapping("operation")
public class OperationController {

	@Autowired
	private BookService bookService;

	@Autowired
	private MagazineService magazineService;

	@Autowired
	private JournalService journalService;

	@Autowired
	private UserService userService;

	@Autowired
	private CategoryService categoryService;

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

	@RequestMapping(value = "saveMagazine", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Detailed.class)
	public Magazine saveMagazine() throws ServiceUnavailableException {

		Magazine magazine = new Magazine();
		magazine.setId(001);
		magazine.setBoId("qro24y12u4o");
		magazine.setMonthlyNo("123");
		magazine.setTitle("The Sample");
		magazine.setCoverPhoto("magazine1.img");
		magazine.setPublishedDate("12.02.2018");
		magazine.setVolume(15);
		magazine.setState(State.Publish);
		magazine.setModifiedDate("10.03.2020");
		magazine.setCreatedDate("15.06.2017");
		magazine.setEntityStatus(EntityStatus.ACTIVE);
		magazineService.save(magazine);

		return magazine;

	}

	@RequestMapping(value = "saveJournal", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Detailed.class)
	public Journal saveJournal(@RequestBody JSONObject json) throws ServiceUnavailableException {
		
		Journal journal = new Journal();
		journal.setId(1);
		journal.setBoId("fksjaf;sj");
		journal.setWeeklyNo("3");
		journal.setTitle("Myanmar");
		journal.setCoverPhoto("journal1.img");
		journal.setPublishedDate("23.04.2020");
		journal.setState(State.Publish);
		journal.setModifiedDate("10.10.2020");
		journal.setCreatedDate("12.02.2020");
		journal.setEntityStatus(EntityStatus.ACTIVE);
		journalService.save(journal);
		
//		String journalId = json.get("id").toString();
//		String boId = json.get("boId").toString();
//		String weeklyNo = json.get("weeklyNo").toString();
//		String title = json.get("title").toString();
//		String coverPhoto = json.get("coverPhoto").toString();
//		String publishedDate = json.get("publishedDate").toString();
//		String volume = (String) json.get("volume");
//		String state = json.get("state").toString();
//		String modifiedDate = json.get("modifiedDate").toString();
//		String createdDate = json.get("createdDate").toString();
//		String entityStatus = json.get("entityStatus").toString();
//		
////	
		return journal;

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

	@RequestMapping(value = "saveCategory", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public Category saveCategory() throws ServiceUnavailableException {

		String categoryName = null;

		Category category = new Category();
		category.setId(001);
		category.setBoId("50843730");
		category.setType(CategoryType.Law);
		category.setEntityStatus(EntityStatus.ACTIVE);
		categoryService.save(category);

		return category;
	}

	@RequestMapping(value = "saveAuthor", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public Author saveAuthor() throws ServiceUnavailableException {

		Author author = new Author();
		author.setId(001);
		author.setBoId("fdishfosd");
		author.setEngName("Johnson");
		author.setMyanmarName("ပုညခင္");
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
