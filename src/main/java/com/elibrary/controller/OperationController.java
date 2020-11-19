package com.elibrary.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.elibrary.entity.Author;
import com.elibrary.entity.AuthorType;
import com.elibrary.entity.Book;
import com.elibrary.entity.Category;
import com.elibrary.entity.Comment;
import com.elibrary.entity.Department;
import com.elibrary.entity.EntityStatus;
import com.elibrary.entity.Hluttaw;
import com.elibrary.entity.Journal;
import com.elibrary.entity.Position;
import com.elibrary.entity.Publisher;
import com.elibrary.entity.State;
import com.elibrary.entity.SubCategory;
import com.elibrary.entity.SystemConstant;
import com.elibrary.entity.User;
import com.elibrary.entity.UserRole;
import com.elibrary.entity.UserType;
import com.elibrary.entity.Views;
import com.elibrary.service.AuthorService;
import com.elibrary.service.BookService;
import com.elibrary.service.CategoryService;
import com.elibrary.service.CommentService;
import com.elibrary.service.EmailService;
import com.elibrary.service.JournalService;
import com.elibrary.service.PublisherService;
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

	@Autowired
	private PublisherService publisherService;

	@Autowired
	private CommentService commentService;

	@Autowired
	private EmailService emailService;



	@Value("${IMAGEUPLOADURL}")
	private String IMAGEUPLOADURL;

	private static Logger logger = Logger.getLogger(OperationController.class);

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "saveBook", method = RequestMethod.POST)
	@JsonView(Views.Detailed.class)
	public JSONObject saveBook(@RequestBody JSONObject json) throws ServiceUnavailableException, IOException {
		// logger.info("json: " + json);
		JSONObject resultJson = new JSONObject();
		List<Author> authors = new ArrayList<Author>();
		List<Publisher> publishers = new ArrayList<Publisher>();

		Book book = new Book();
		book.setBoId(SystemConstant.BOID_REQUIRED);

		Object categoryObject = json.get("category");
		if (categoryObject == null || categoryObject.toString().isEmpty()) {
			resultJson.put("status", "0");
			resultJson.put("msg", "Please choose Category!");
			return resultJson;
		}
		Category category = categoryService.findByBoId(categoryObject.toString());
		if (category != null)
			book.setCategory(category);

		Object subCategoryObject = json.get("subCategory");
		if (subCategoryObject == null || subCategoryObject.toString().isEmpty()) {
			resultJson.put("status", "0");
			resultJson.put("msg", "Please choose Sub-Category!");
			return resultJson;
		}

		SubCategory subCategory = subCategoryService.findByBoId(subCategoryObject.toString());
		if (subCategory != null)
			book.setSubCategory(subCategory);

		List<Object> authorObjects = (List<Object>) json.get("authors");
		if (!CollectionUtils.isEmpty(authorObjects)) {
			for (Object boId : authorObjects) {
				Author author = authorService.findByBoId(boId.toString());
				if (author != null)
					authors.add(author);
			}
		}
		book.setAuthors(authors);

		List<Object> publisherObjects = (List<Object>) json.get("publishers");
		if (!CollectionUtils.isEmpty(publisherObjects)) {
			for (Object boId : publisherObjects) {
				Publisher publisher = publisherService.findByBoId(boId.toString());
				if (publisher != null)
					publishers.add(publisher);
			}
		}
		book.setPublishers(publishers);
		book.setCallNo(json.get("callNumber").toString());
		book.setDownloadApproval(json.get("downloadApproval").toString());
		book.setEdition(json.get("edition").toString());
		book.setPublishedDate(json.get("publishedDate").toString().split("T")[0]);
		book.setSeriesIndex(json.get("seriesIndex").toString());
		book.setSort(json.get("sort").toString());
		book.setTitle(json.get("title").toString());
		book.setVolume(json.get("volume").toString());
		book.setISBN(json.get("ISBN").toString());
		book.setState(State.PENDING);
		book.setEntityStatus(EntityStatus.ACTIVE);
		Date date = new Date();
		String createdDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
		book.setCreatedDate(createdDate);

		if (!setImage(json, book)) {
			resultJson.put("status", "0");
			resultJson.put("msg", "This Profile Picture is already registered!");
			return resultJson;
		}

		if (!setPDFFile(json, book)) {
			resultJson.put("status", "0");
			resultJson.put("msg", "This File is already registered!");
			return resultJson;
		}

		Comment comment = new Comment();
		comment.setBoId(SystemConstant.BOID_REQUIRED);
		comment.setDescription(json.get("description").toString());
		commentService.save(comment);
		book.setComment(comment);

		setPDFFile(json, book);
		bookService.save(book);
		resultJson.put("status", "1");
		resultJson.put("msg", "Success!");
		return resultJson;

	}

	private boolean setImage(JSONObject json, Book book) throws IOException {
		String imageSrc = json.get("imageSrc").toString();
		imageSrc = imageSrc.split("base64")[1];

		String filePath = IMAGEUPLOADURL.trim() + "BookProfile//";
		String profileName = json.get("profileName").toString().split("\\\\")[2];
		if (bookService.isDuplicateProfile("/BookProfile/" + profileName.trim())) {
			return false;
		}

		byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(imageSrc.replaceAll(" ", "+"));
		Path destinationFile = Paths.get(filePath, profileName);
		Files.write(destinationFile, imageBytes);
		book.setCoverPhoto("/BookProfile/" + profileName.trim());
		return true;
	}

	private boolean setPDFFile(JSONObject json, Book book) throws IOException {
		String pdf = json.get("pdf").toString();
		pdf = pdf.split("base64")[1];
		String pdfName = json.get("pdfName").toString().split("\\\\")[2];
		if (bookService.isDuplicatePDF("/BookFile/" + pdfName.trim())) {
			return false;
		}

		byte[] decodedBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(pdf);
		String pdfFilePath = IMAGEUPLOADURL.trim() + "BookFile//";

		File file = new File(pdfFilePath + pdfName);
		book.setSize((long)file.length()/ 1024 + "KB");
		FileOutputStream fop = new FileOutputStream(file);
		fop.write(decodedBytes);
		book.setPath("/BookFile/" + pdfName.trim());
		return true;
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
		for (Object object : subCategories) {
			SubCategory subCategory = subCategoryService.findByBoId(object.toString());
			if (subCategory != null)
				subCategoryList.add(subCategory);
		}
		category.getSubCategories().addAll(subCategoryList);
		category.setMyanmarName(json.get("myaName").toString());
		category.setEngName(json.get("engName").toString());
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

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "saveAuthor", method = RequestMethod.POST)
	@JsonView(Views.Summary.class)
	public JSONObject saveAuthor(@RequestBody JSONObject json) throws ServiceUnavailableException, IOException {
		JSONObject resultJson = new JSONObject();

		Author author = new Author();
		author.setBoId(SystemConstant.BOID_REQUIRED);
		author.setName(json.get("name").toString());
		author.setAuthorType(AuthorType.valueOf(json.get("authorType").toString().toUpperCase()));
		author.setSort(json.get("sort").toString());
		author.setEntityStatus(EntityStatus.ACTIVE);
		String imageSrc = json.get("imageSrc").toString();
		imageSrc = imageSrc.split("base64")[1];

		String filePath = IMAGEUPLOADURL.trim() + "AuthorProfile//";
		String pictureName = json.get("profilePicture").toString().split("\\\\")[2];
		if (authorService.isDuplicateProfile(filePath + pictureName)) {
			resultJson.put("status", "0");
			resultJson.put("msg", "This Profile Picture is already registered!");
			return resultJson;

		}

		byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(imageSrc.replaceAll(" ", "+"));
		Path destinationFile = Paths.get(filePath, pictureName);
		Files.write(destinationFile, imageBytes);

		/* to retrieve profile */
		author.setProfilePicture("/AuthorProfile/" + pictureName);
		authorService.save(author);

		resultJson.put("status", "1");
		resultJson.put("msg", "Success!");
		return resultJson;
	}

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "savePublisher", method = RequestMethod.POST)
	@JsonView(Views.Summary.class)
	public void savePublisher(@RequestBody JSONObject json) throws ServiceUnavailableException {
		Publisher publisher = new Publisher();
		publisher.setBoId(SystemConstant.BOID_REQUIRED);
		publisher.setName(json.get("name").toString());
		publisher.setSort(json.get("sort").toString());
		publisher.setEntityStatus(EntityStatus.ACTIVE);
		publisherService.save(publisher);
	}

	@RequestMapping(value = "deleteJournal", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public String deleteJournal() throws ServiceUnavailableException {
		String message = "success";
		Journal journal = new Journal();
		journalService.delete(journal);
		return message;
	}


	@RequestMapping(value = "hluttawSetup", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)

	public String hluttawSetup(@RequestBody JSONObject json) {
		String desc = json.get("description").toString();
		return "";
	}


	
	@RequestMapping(value = "deleteAuthor", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public JSONObject deleteAuthor(@RequestBody JSONObject json)throws ServiceUnavailableException{
	
		JSONObject resultJson = new JSONObject();
		String authorId = json.get("authorId").toString();
		Author author = authorService.findByBoId(authorId);
		if(author == null) {
			resultJson.put("status", "0");
			resultJson.put("msg", "Author Id is invalid!!");
			return resultJson;
		}
		author.setEntityStatus(EntityStatus.DELETED);
		authorService.save(author);
		resultJson.put("status", "1");
		resultJson.put("msg", "Your request is successful!!");
		return resultJson;
	}
	
	@RequestMapping(value = "deleteBook", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public JSONObject deleteBook(@RequestBody JSONObject json)throws ServiceUnavailableException{
	
		JSONObject resultJson = new JSONObject();
		String bookId = json.get("bookId").toString();
		Book book = bookService.findByBoId(bookId);
		if(book == null) {
			resultJson.put("status", "0");
			resultJson.put("msg", "Book Id is invalid!!");
			return resultJson;
		}
		book.setEntityStatus(EntityStatus.DELETED);
		bookService.save(book);
		resultJson.put("status", "1");
		resultJson.put("msg", "Your request is successful!!");
		return resultJson;
	}
	
	@RequestMapping(value = "deleteCategory", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public JSONObject deleteCategory(@RequestBody JSONObject json)throws ServiceUnavailableException{
	
		JSONObject resultJson = new JSONObject();
		String categoryId = json.get("categoryboId").toString();
		Category category = categoryService.findByBoId(categoryId);
		if(category == null) {
			resultJson.put("status", "0");
			resultJson.put("msg", "Category Id is invalid!!");
			return resultJson;
		}
		category.setEntityStatus(EntityStatus.DELETED);
		categoryService.save(category);
		resultJson.put("status", "1");
		resultJson.put("msg", "Your request is successful!!");
		return resultJson;
	}
	
	
	@RequestMapping(value = "deleteSubCategory", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public JSONObject deleteSubCategory(@RequestBody JSONObject json)throws ServiceUnavailableException{
	
		JSONObject resultJson = new JSONObject();
		String subCategoryId = json.get("subCategoryboId").toString();
		SubCategory subCategory = subCategoryService.findByBoId(subCategoryId);
		if(subCategory == null) {
			resultJson.put("status", "0");
			resultJson.put("msg", "Subcategory Id is invalid!!");
			return resultJson;
		}
		subCategory.setEntityStatus(EntityStatus.DELETED);
		subCategoryService.save(subCategory);
		resultJson.put("status", "1");
		resultJson.put("msg", "Your request is successful!!");
		return resultJson;
	}
	
	@RequestMapping(value = "deleteUser", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public JSONObject deleteUser(@RequestBody JSONObject json)throws ServiceUnavailableException{
	
		JSONObject resultJson = new JSONObject();
		String userId = json.get("userboId").toString();
		User user = userService.findByBoId(userId);
		if(user == null) {
			resultJson.put("status", "0");
			resultJson.put("msg", "User Id is invalid!!");
			return resultJson;
		}
		user.setEntityStatus(EntityStatus.DELETED);
		userService.save(user);
		resultJson.put("status", "1");
		resultJson.put("msg", "Your request is successful!!");
		return resultJson;
	}
	
	
}