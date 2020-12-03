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

	private void setBookInfo(Book book, JSONObject json) throws ServiceUnavailableException, IOException {
		List<Author> authors = new ArrayList<Author>();
		List<Object> authorObjects = (List<Object>) json.get("authors");
		if (!CollectionUtils.isEmpty(authorObjects)) {
			for (Object boId : authorObjects) {
				Author author = authorService.findByBoId(boId.toString());
				if (author != null)
					authors.add(author);
			}
		}
		book.setAuthors(authors);
		List<Publisher> publishers = new ArrayList<Publisher>();
		List<Object> publisherObjects = (List<Object>) json.get("publishers");
		if (!CollectionUtils.isEmpty(publisherObjects)) {
			for (Object boId : publisherObjects) {
				Publisher publisher = publisherService.findByBoId(boId.toString());
				if (publisher != null)
					publishers.add(publisher);
			}
		}

		book.setPublishers(publishers);
		book.setDownloadApproval(json.get("downloadApproval").toString());
		book.setPublishedDate(json.get("publishedDate").toString().split("T")[0]);
		book.setSeriesIndex(json.get("seriesIndex").toString());
		book.setSort(json.get("sort").toString());
		book.setTitle(json.get("title").toString());
		book.setISBN(json.get("ISBN").toString());
		book.setState(State.PENDING);
		book.setEntityStatus(EntityStatus.ACTIVE);
		Comment comment = new Comment();
		comment.setBoId(SystemConstant.BOID_REQUIRED);
		comment.setDescription(json.get("description").toString());
		commentService.save(comment);
		book.setComment(comment);

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
		book.setSize((long) file.length() / 1024 + "KB");
		FileOutputStream fop = new FileOutputStream(file);
		fop.write(decodedBytes);
		
		book.setPath("/BookFile/" + pdfName.trim());
		return true;
	}

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "saveBook", method = RequestMethod.POST)
	@JsonView(Views.Detailed.class)
	public JSONObject saveBook(@RequestBody JSONObject json) throws ServiceUnavailableException, IOException {

		JSONObject resultJson = new JSONObject();
		Book book = new Book();
		book.setBoId(SystemConstant.BOID_REQUIRED);

		Object uploader = json.get("userId");
		if (uploader == null || uploader.toString().isEmpty()) {
			resultJson.put("status", "0");
			resultJson.put("msg", "Please login first!");
			return resultJson;
		}

		User user = userService.findByBoId(uploader.toString());
		if (user == null) {
			resultJson.put("status", "0");
			resultJson.put("msg", "Not registered User!");
			return resultJson;
		}
		book.setUploader(user);
		Object image = json.get("imageSrc");
		if (image == null || image.toString().isEmpty()) {
			resultJson.put("status", "0");
			resultJson.put("msg", "Please select cover photo!");
			return resultJson;
		}

		Object categoryObject = json.get("category");
		if (categoryObject == null || categoryObject.toString().isEmpty()) {
			resultJson.put("status", "0");
			resultJson.put("msg", "Please choose Category!");
			return resultJson;
		}
		Category category = categoryService.findByBoId(categoryObject.toString());
		if (category != null) {
			book.setCategory(category);
			book.setAccessionNo(book.getCategory().getEngName().substring(0, 1) + (bookService.countBook() + 1));
		}

		Object subCategoryObject = json.get("subCategory");
		if (subCategoryObject == null || subCategoryObject.toString().isEmpty()) {
			resultJson.put("status", "0");
			resultJson.put("msg", "Please choose Sub-Category!");
			return resultJson;
		}

		SubCategory subCategory = subCategoryService.findByBoId(subCategoryObject.toString());
		if (subCategory != null)
			book.setSubCategory(subCategory);

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

		setBookInfo(book, json);
		book.setCallNo(json.get("callNumber").toString());
		book.setEdition(json.get("edition").toString());
		book.setVolume(json.get("volume").toString());
		bookService.save(book);
		resultJson.put("status", "1");
		resultJson.put("msg", "Success!");
		return resultJson;

	}

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "editBook", method = RequestMethod.POST)
	@JsonView(Views.Detailed.class)
	public JSONObject editBook(@RequestBody JSONObject json) throws ServiceUnavailableException, IOException {
		JSONObject resultJson = new JSONObject();
		Book book = bookService.findByBoId(json.get("boId").toString());

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

		Date date = new Date();
		String modifiedDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
		book.setModifiedDate(modifiedDate);

		if (json.get("imageSrc").toString().contains("base64")) {
			if (!setImage(json, book)) {
				resultJson.put("status", "0");
				resultJson.put("msg", "This Profile Picture is already registered!");
				return resultJson;
			}
		}

		Object pdf = json.get("pdf");
		if (pdf != null && pdf.toString().contains("base64")) {
			if (!setPDFFile(json, book)) {
				resultJson.put("status", "0");
				resultJson.put("msg", "This File is already registered!");
				return resultJson;
			}
		}

		book.setCallNo(json.get("callNumber") != null ? json.get("callNumber").toString() : book.getCallNo());
		book.setEdition(json.get("edition") != null ? json.get("edition").toString() : book.getEdition());
		book.setVolume(json.get("volume") != null ? json.get("volume").toString() : book.getVolume());
		setBookInfo(book, json);
		bookService.save(book);
		resultJson.put("status", "1");
		resultJson.put("msg", "Success!");
		return resultJson;

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

	private void setCategoryInfo(Category category, JSONObject json) {

		List<SubCategory> subCategoryList = new ArrayList<SubCategory>();
		List<Object> subCategories = (List<Object>) json.get("categories");
		for (Object object : subCategories) {
			SubCategory subCategory = subCategoryService.findByBoId(object.toString());
			if (subCategory != null)
				subCategoryList.add(subCategory);
		}
		category.setSubCategories(subCategoryList);
		category.setMyanmarName(json.get("myanmarName").toString());
		category.setEngName(json.get("engName").toString());
		category.setPriority(Double.parseDouble(json.get("priority").toString()));
		category.setEntityStatus(EntityStatus.ACTIVE);
	}

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "savecategory", method = RequestMethod.POST)
	@JsonView(Views.Summary.class)
	public void saveCategory(@RequestBody JSONObject json) throws ServiceUnavailableException {
		Category category = new Category();
		category.setBoId(SystemConstant.BOID_REQUIRED);
		setCategoryInfo(category, json);
		categoryService.save(category);
	}

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "editcategory", method = RequestMethod.POST)
	@JsonView(Views.Summary.class)
	public void editCategory(@RequestBody JSONObject json) throws ServiceUnavailableException {
		Category category = categoryService.findByBoId(json.get("boId").toString());
		setCategoryInfo(category, json);
		categoryService.save(category);
	}

	private boolean setSubCategoryInfo(SubCategory subCategory, JSONObject json) {
		Object myanmarName = json.get("myanmarName");
		Object engName = json.get("engName");
		if (myanmarName == null || myanmarName.toString().isEmpty() || engName ==null || engName.toString().isEmpty())
			return false;
		
		Object priority = json.get("priority");
		if (priority == null || priority.toString().isEmpty())
			return false;

		subCategory.setMyanmarName(myanmarName.toString());
		subCategory.setEngName(engName.toString());
		subCategory.setPriority(Double.parseDouble(json.get("priority").toString()));
		subCategory.setEntityStatus(EntityStatus.ACTIVE);
		return true;
	}

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "savesubcategory", method = RequestMethod.POST)
	@JsonView(Views.Summary.class)
	public JSONObject saveSubCategory(@RequestBody JSONObject json) throws ServiceUnavailableException {
		JSONObject result = new JSONObject();
		SubCategory subCategory = new SubCategory();
		subCategory.setBoId(SystemConstant.BOID_REQUIRED);
		if (!setSubCategoryInfo(subCategory, json)) {
			result.put("status", "0");
			return result;
		}
		subCategoryService.save(subCategory);
		result.put("status", "1");
		return result;
	}

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "editsubcategory", method = RequestMethod.POST)
	@JsonView(Views.Summary.class)
	public JSONObject editSubCategory(@RequestBody JSONObject json) throws ServiceUnavailableException {
		JSONObject result = new JSONObject();
		SubCategory subCategory = subCategoryService.findByBoId(json.get("boId").toString());
		if (!setSubCategoryInfo(subCategory, json)) {
			result.put("status", "0");
			return result;
		}
		subCategoryService.save(subCategory);
		result.put("status", "1");
		return result;
	}

	private boolean setAuthorProfile(Author author, JSONObject json) throws IOException {
		String imageSrc = json.get("imageSrc").toString();
		imageSrc = imageSrc.split("base64")[1];

		String filePath = IMAGEUPLOADURL.trim() + "AuthorProfile//";
		String pictureName = json.get("profilePicture").toString().split("\\\\")[2];
		String profilePicture = "/AuthorProfile/" + pictureName;
		if (authorService.isDuplicateProfile(profilePicture))
			return false;

		byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(imageSrc.replaceAll(" ", "+"));
		Path destinationFile = Paths.get(filePath, pictureName);
		Files.write(destinationFile, imageBytes);

		/* to retrieve profile */
		author.setProfilePicture("/AuthorProfile/" + pictureName);
		return true;

	}

	private void setAuthorInfo(Author author, JSONObject json) {

		author.setName(json.get("name").toString());
		author.setAuthorType(AuthorType.valueOf(json.get("authorType").toString().toUpperCase()));
		author.setSort(json.get("sort").toString());
		author.setEntityStatus(EntityStatus.ACTIVE);
	}

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "saveAuthor", method = RequestMethod.POST)
	@JsonView(Views.Summary.class)
	public JSONObject saveAuthor(@RequestBody JSONObject json) throws ServiceUnavailableException, IOException {
		JSONObject resultJson = new JSONObject();

		Author author = new Author();
		author.setBoId(SystemConstant.BOID_REQUIRED);
		setAuthorInfo(author, json);

		if (json.get("imageSrc").toString().contains("base64")) {
			if (!setAuthorProfile(author, json)) {
				resultJson.put("status", "0");
				resultJson.put("msg", "This Profile Picture is already registered!");
				return resultJson;
			}
		}

		authorService.save(author);

		resultJson.put("status", "1");
		resultJson.put("msg", "Success!");
		return resultJson;
	}

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "editAuthor", method = RequestMethod.POST)
	@JsonView(Views.Summary.class)
	public JSONObject editAuthor(@RequestBody JSONObject json) throws ServiceUnavailableException, IOException {
		JSONObject resultJson = new JSONObject();

		Author author = authorService.findByBoId(json.get("boId").toString());
		setAuthorInfo(author, json);

		if (json.get("imageSrc").toString().contains("base64")) {
			if (!setAuthorProfile(author, json)) {
				resultJson.put("status", "0");
				resultJson.put("msg", "This Profile Picture is already registered!");
				return resultJson;
			}
		}

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

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "editPublisher", method = RequestMethod.POST)
	@JsonView(Views.Summary.class)
	public void editPublisher(@RequestBody JSONObject json) throws ServiceUnavailableException {
		Publisher publisher = publisherService.findByBoId(json.get("boId").toString());
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

	@ResponseBody
	@CrossOrigin(origins = "*")
	@JsonView(Views.Summary.class)
	@RequestMapping(value = "deleteAuthor", method = RequestMethod.POST)
	public JSONObject deleteAuthor(@RequestBody JSONObject json) throws ServiceUnavailableException {

		JSONObject resultJson = new JSONObject();
		String authorId = json.get("authorId").toString();
		Author author = authorService.findByBoId(authorId);
		if (author == null) {
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

	@ResponseBody
	@CrossOrigin(origins = "*")
	@JsonView(Views.Summary.class)
	@RequestMapping(value = "deletePublisher", method = RequestMethod.POST)
	public JSONObject deletePublisher(@RequestBody JSONObject json) throws ServiceUnavailableException {

		JSONObject resultJson = new JSONObject();
		String publisherboId = json.get("publisherboId").toString();
		Publisher publisher = publisherService.findByBoId(publisherboId);
		if (publisher == null) {
			resultJson.put("status", "0");
			resultJson.put("msg", "publisherboId Id is invalid!!");
			return resultJson;

		}
		publisher.setEntityStatus(EntityStatus.DELETED);
		publisherService.save(publisher);
		resultJson.put("status", "1");
		resultJson.put("msg", "Your request is successful!!");
		return resultJson;
	}

	@ResponseBody
	@CrossOrigin(origins = "*")
	@JsonView(Views.Summary.class)
	@RequestMapping(value = "deleteBook", method = RequestMethod.POST)
	public JSONObject deleteBook(@RequestBody JSONObject json) throws ServiceUnavailableException {

		JSONObject resultJson = new JSONObject();
		String bookId = json.get("bookId").toString();
		Book book = bookService.findByBoId(bookId);
		if (book == null) {
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

	@ResponseBody
	@CrossOrigin(origins = "*")
	@JsonView(Views.Summary.class)
	@RequestMapping(value = "deleteCategory", method = RequestMethod.POST)
	public JSONObject deleteCategory(@RequestBody JSONObject json) throws ServiceUnavailableException {

		JSONObject resultJson = new JSONObject();
		String categoryId = json.get("categoryboId").toString();
		Category category = categoryService.findByBoId(categoryId);
		if (category == null) {
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

	@ResponseBody
	@CrossOrigin(origins = "*")
	@JsonView(Views.Summary.class)
	@RequestMapping(value = "deleteSubCategory", method = RequestMethod.POST)
	public JSONObject deleteSubCategory(@RequestBody JSONObject json) throws ServiceUnavailableException {

		JSONObject resultJson = new JSONObject();
		String subCategoryId = json.get("subCategoryboId").toString();
		SubCategory subCategory = subCategoryService.findByBoId(subCategoryId);
		if (subCategory == null) {
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
	public JSONObject deleteUser(@RequestBody JSONObject json) throws ServiceUnavailableException {

		JSONObject resultJson = new JSONObject();
		String userId = json.get("userboId").toString();
		User user = userService.findByBoId(userId);
		if (user == null) {
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