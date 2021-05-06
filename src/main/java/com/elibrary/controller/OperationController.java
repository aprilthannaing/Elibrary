package com.elibrary.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.elibrary.entity.Advertisement;
import com.elibrary.entity.Author;
import com.elibrary.entity.AuthorType;
import com.elibrary.entity.Book;
import com.elibrary.entity.Category;
import com.elibrary.entity.Comment;
import com.elibrary.entity.Department;
import com.elibrary.entity.EntityStatus;
import com.elibrary.entity.Feedback;
import com.elibrary.entity.Hluttaw;
import com.elibrary.entity.Journal;
import com.elibrary.entity.LinkType;
import com.elibrary.entity.Position;
import com.elibrary.entity.Publisher;
import com.elibrary.entity.Reply;
import com.elibrary.entity.State;
import com.elibrary.entity.SubCategory;
import com.elibrary.entity.SystemConstant;
import com.elibrary.entity.Type;
import com.elibrary.entity.User;
import com.elibrary.entity.UserRole;
import com.elibrary.entity.UserType;
import com.elibrary.entity.Views;
import com.elibrary.service.AdvertisementService;
import com.elibrary.service.AuthorService;
import com.elibrary.service.BookService;
import com.elibrary.service.CategoryService;
import com.elibrary.service.CommentService;
import com.elibrary.service.FeedbackService;
import com.elibrary.service.JournalService;
import com.elibrary.service.MailService;
import com.elibrary.service.PublisherService;
import com.elibrary.service.ReplyService;
import com.elibrary.service.SubCategoryService;
import com.elibrary.service.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.mchange.rmi.ServiceUnavailableException;
import com.spire.pdf.PdfDocument;

@RestController
@RequestMapping("operation")
public class OperationController extends AbstractController {

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
	private MailService mailService;

	@Autowired
	private FeedbackService feedbackService;

	@Autowired
	private AdvertisementService advertisementService;

	@Autowired
	private ReplyService replyService;

	@Value("${IMAGEUPLOADURL}")
	private String IMAGEUPLOADURL;

	@Value("${PDFPATH}")
	private String PDFPATH;

	private static Logger logger = Logger.getLogger(OperationController.class);

	private void setBookInfo(Book book, JSONObject json) throws ServiceUnavailableException, IOException {
		List<Author> authors = new ArrayList<Author>();
		List<Object> authorObjects = (List<Object>) json.get("authors");
		if (CollectionUtils.isEmpty(authorObjects)) {
			Author author = new Author();
			author.setBoId(SystemConstant.BOID_REQUIRED);
			author.setName(json.get("authorName").toString());
			author.setAuthorType(AuthorType.valueOf(json.get("authorType").toString().toUpperCase()));
			author.setProfilePicture("AuthorProfile/author1.png");
			author.setEntityStatus(EntityStatus.ACTIVE);
			authorService.save(author);
		}

		else {
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
		book.setPublishedDate(json.get("publishedDate").toString());
		book.setSeriesIndex(json.get("seriesIndex").toString());
		book.setSort(json.get("sort").toString());
		book.setISBN(json.get("ISBN").toString());
		book.setState(json.get("state") != null ? State.APPROVE : State.PENDING);
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
		if (pdf == null || pdf.toString().isEmpty()) {
			return false;
		}

		try {
			pdf = pdf.split("base64")[1];
		} catch (Exception e) {
			logger.error("error:" + e.getMessage());
		}

		String pdfName = json.get("pdfName").toString().split("\\\\")[2];
		if (bookService.isDuplicatePDF("/BookFile/" + pdfName.trim())) {
			return false;
		}

		byte[] decodedBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(pdf);
		String pdfFilePath = IMAGEUPLOADURL.trim();

		String fileStr = pdfFilePath + "BookFile/" + pdfName;
		logger.info("fileStr !!!!!!!!!!" + fileStr);
		File file = new File(fileStr);
		book.setSize(file.length() / 1024 + "KB");

		FileOutputStream fop = new FileOutputStream(file);
		fop.write(decodedBytes);

		// set image
		setImage(fileStr, book);

		addWaterMark(pdfFilePath + "BookFile/" + pdfName.trim(), pdfFilePath + "NewWaterMarkFile/" + pdfName.trim());
		book.setPath("/NewWaterMarkFile/" + pdfName.trim());
		return true;
	}

	private void setImage(String fileStr, Book book) {
		PdfDocument doc = new PdfDocument();
		doc.loadFromFile(fileStr);
		BufferedImage image = doc.saveAsImage(0);
		String pdfFilePath = IMAGEUPLOADURL.trim();
		String fileName = "BookProfile/" + "coverPhoto" + bookService.countBook() + ".png";

		String fullPath = pdfFilePath + fileName;
		File file = new File(String.format(fullPath, 0));
		try {
			ImageIO.write(image, "PNG", file);
		} catch (IOException e) {
			logger.error("Can't write cover photo!");
		}
		doc.close();
		book.setCoverPhoto(fileName);
	}

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "pdftoimg", method = RequestMethod.POST)
	@JsonView(Views.Detailed.class)
	public void pdfToImg() throws IOException {
		PdfDocument doc = new PdfDocument();
		doc.loadFromFile("D://ThawDarSwe_ABrandeAndFamousShortStories.pdf");
		BufferedImage image = doc.saveAsImage(0);
		File file = new File(String.format("D://coverPhoto.png", 0));
		ImageIO.write(image, "PNG", file);
		doc.close();
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

		Object categoryBoId = json.get("category");
		if (categoryBoId == null || categoryBoId.toString().isEmpty()) {
			resultJson.put("status", "0");
			resultJson.put("msg", "Please choose Category!");
			return resultJson;
		}
		Category category = categoryService.findByBoId(categoryBoId.toString());
		if (category != null)
			book.setCategory(category);

		String accessionNo = json.get("accessionNo").toString();
		if (accessionNo != null || !accessionNo.isEmpty())
			book.setAccessionNo(accessionNo);

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
		String createdDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(date);
		book.setCreatedDate(createdDate);

		Object pdf = json.get("pdfName").toString();
		if (pdf == null || pdf.toString().isEmpty()) {
			resultJson.put("status", "0");
			resultJson.put("msg", "Please select PDF file!");
			return resultJson;
		}

		if (!setPDFFile(json, book)) {
			resultJson.put("status", "0");
			resultJson.put("msg", "This Book is already registered!");
			return resultJson;
		}

		String title = json.get("title").toString();
		if (bookService.isDuplicateTitle(title)) {
			resultJson.put("status", "0");
			resultJson.put("msg", "This title is duplicated!");
			return resultJson;
		}
		book.setTitle(title);

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

		String title = json.get("title").toString();

		book.setTitle(json.get("title").toString() != null ? title : book.getTitle());
		book.setCallNo(json.get("callNumber") != null ? json.get("callNumber").toString() : book.getCallNo());
		book.setEdition(json.get("edition") != null ? json.get("edition").toString() : book.getEdition());
		book.setVolume(json.get("volume") != null ? json.get("volume").toString() : book.getVolume());
		book.setAccessionNo(json.get("accessionNo") != null ? json.get("accessionNo").toString() : book.getAccessionNo());
		setBookInfo(book, json);

		logger.info("approve !!!!!!!!!!!!!" + book.getState());
		bookService.save(book);
		resultJson.put("status", "1");
		resultJson.put("msg", "Success!");
		return resultJson;

	}

	@CrossOrigin(origins = "*")
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

	private JSONObject setCategoryInfo(Category category, JSONObject json) {
		JSONObject errorMessage = new JSONObject();
		List<SubCategory> subCategoryList = new ArrayList<SubCategory>();
		List<Object> subCategories = (List<Object>) json.get("categories");
		for (Object object : subCategories) {
			SubCategory subCategory = subCategoryService.findByBoId(object.toString());
			if (subCategory != null)
				subCategoryList.add(subCategory);
		}
		category.setSubCategories(subCategoryList);
		String myanmarName = json.get("myanmarName").toString();
		String engName = json.get("engName").toString();
		if ((myanmarName == null && engName == null) || (myanmarName.toString().isEmpty() && engName.toString().isEmpty())) {
			errorMessage.put("status", "0");
			errorMessage.put("msg", "Please enter myanmar name or english name for category!");
			return errorMessage;
		}
		String priority = json.get("priority").toString();
		if (priority == null || priority.isEmpty()) {
			errorMessage.put("status", "0");
			errorMessage.put("msg", "Please enter priority!");
			return errorMessage;
		}
		category.setMyanmarName(myanmarName);
		category.setEngName(engName);
		category.setPriority(Double.parseDouble(priority));
		category.setIcon("/BlackIcon/origanization.png");
		category.setEntityStatus(EntityStatus.ACTIVE);
		return null;
	}

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "savecategory", method = RequestMethod.POST)
	@JsonView(Views.Summary.class)
	public JSONObject saveCategory(@RequestBody JSONObject json) throws ServiceUnavailableException {
		JSONObject resultJson = new JSONObject();
		Category category = new Category();
		category.setBoId(SystemConstant.BOID_REQUIRED);
		resultJson = setCategoryInfo(category, json);
		if (resultJson != null)
			return resultJson;
		resultJson = new JSONObject();
		categoryService.save(category);
		resultJson.put("status", "1");
		resultJson.put("msg", "Success!");
		return resultJson;

	}

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "editcategory", method = RequestMethod.POST)
	@JsonView(Views.Summary.class)
	public JSONObject editCategory(@RequestBody JSONObject json) throws ServiceUnavailableException {
		JSONObject resultJson = new JSONObject();
		Category category = categoryService.findByBoId(json.get("boId").toString());
		resultJson = setCategoryInfo(category, json);
		if (resultJson != null)
			return resultJson;
		resultJson = new JSONObject();
		categoryService.save(category);
		resultJson.put("status", "1");
		resultJson.put("msg", "Success!");
		return resultJson;

	}

	private JSONObject setSubCategoryInfo(SubCategory subCategory, JSONObject json) {
		JSONObject errorMessage = new JSONObject();

		Object myanmarName = json.get("myanmarName");
		Object engName = json.get("engName");
		if ((myanmarName == null && engName == null) || (myanmarName.toString().isEmpty() && engName.toString().isEmpty())) {
			errorMessage.put("stutus", "0");
			errorMessage.put("msg", "Please enter myanmar name or english name for subcategory!");
			return errorMessage;
		}

		Object priority = json.get("priority");
		if (priority == null || priority.toString().isEmpty()) {
			errorMessage.put("status", "0");
			errorMessage.put("msg", "Please enter priority!");
			return errorMessage;
		}

		Object categoryId = json.get("categoryBoId");

		if (categoryId == null || categoryId.toString().isEmpty()) {
			errorMessage.put("status", "0");
			errorMessage.put("msg", "Please choose category!");
			return errorMessage;
		}

		Category category = categoryService.findByBoId(categoryId.toString());

		subCategory.setMyanmarName(myanmarName.toString());
		subCategory.setEngName(engName.toString());
		subCategory.setPriority(Double.parseDouble(priority.toString()));
		subCategory.setEntityStatus(EntityStatus.ACTIVE);
		subCategory.setCategoryBoId(categoryId.toString());

		category.getSubCategories().add(subCategory);

		return null;
	}

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "savesubcategory", method = RequestMethod.POST)
	@JsonView(Views.Summary.class)
	public JSONObject saveSubCategory(@RequestBody JSONObject json) throws ServiceUnavailableException {
		JSONObject result = new JSONObject();
		SubCategory subCategory = new SubCategory();
		subCategory.setBoId(SystemConstant.BOID_REQUIRED);

		result = setSubCategoryInfo(subCategory, json);
		if (result != null)
			return result;

		result = new JSONObject();
		subCategoryService.save(subCategory);
		result.put("status", "1");
		result.put("msg", "Success!");
		return result;
	}

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "editsubcategory", method = RequestMethod.POST)
	@JsonView(Views.Summary.class)
	public JSONObject editSubCategory(@RequestBody JSONObject json) throws ServiceUnavailableException {
		JSONObject result = new JSONObject();
		SubCategory subCategory = subCategoryService.findByBoId(json.get("boId").toString());

		result = setSubCategoryInfo(subCategory, json);
		if (result != null)
			return result;
		result = new JSONObject();
		subCategoryService.save(subCategory);
		result.put("status", "1");
		result.put("msg", "Success!");
		return result;

	}

	private void setAuthorProfile(Author author, JSONObject json) throws IOException {

		String imageSrc = json.get("imageSrc").toString();
		imageSrc = imageSrc.split("base64")[1];

		String filePath = IMAGEUPLOADURL.trim() + "AuthorProfile//";
		String profile = json.get("profilePicture").toString();

		String pictureName = profile.split("\\\\")[2];
		byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(imageSrc.replaceAll(" ", "+"));
		Path destinationFile = Paths.get(filePath, pictureName);
		Files.write(destinationFile, imageBytes);

		/* to retrieve profile */
		author.setProfilePicture("/AuthorProfile/" + pictureName);

	}

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "saveAuthor", method = RequestMethod.POST)
	@JsonView(Views.Summary.class)
	public JSONObject saveAuthor(@RequestBody JSONObject json) throws ServiceUnavailableException, IOException {
		JSONObject resultJson = new JSONObject();

		JSONObject errorMessage = new JSONObject();

		Author author = new Author();
		author.setBoId(SystemConstant.BOID_REQUIRED);

		Object img = json.get("imageSrc").toString();
		if (img == null || img.toString().isEmpty())
			author.setProfilePicture("/AuthorProfile/author1.png");

		else if (json.get("imageSrc").toString().contains("base64"))
			setAuthorProfile(author, json);

		String name = json.get("name").toString();
		if (name == null || name.isEmpty()) {
			errorMessage.put("status", "0");
			errorMessage.put("msg", "Please enter author name!");
			return errorMessage;
		}
		author.setName(name);

		String authorType = json.get("authorType").toString();
		if (authorType == null || authorType.isEmpty()) {
			errorMessage.put("status", "0");
			errorMessage.put("msg", "Please choose author type!");
			return errorMessage;
		}

		Object sort = json.get("sort");
		author.setAuthorType(AuthorType.valueOf(authorType.toUpperCase()));
		author.setSort(sort != null ? sort.toString() : "");
		author.setEntityStatus(EntityStatus.ACTIVE);
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
		JSONObject errorMessage = new JSONObject();
		Author author = authorService.findByBoId(json.get("boId").toString());

		Object img = json.get("imageSrc").toString();
		if (img == null || img.toString().isEmpty())
			author.setProfilePicture("/AuthorProfile/author1.png");

		else if (json.get("imageSrc").toString().contains("base64"))
			setAuthorProfile(author, json);

		String name = json.get("name").toString();
		if (name == null || name.isEmpty()) {
			errorMessage.put("status", "0");
			errorMessage.put("msg", "Please enter author name!");
			return errorMessage;
		}
		author.setName(name);

		String authorType = json.get("authorType").toString();
		if (authorType == null || authorType.isEmpty()) {
			errorMessage.put("status", "0");
			errorMessage.put("msg", "Please choose author type!");
			return errorMessage;
		}

		Object sort = json.get("sort");
		author.setAuthorType(AuthorType.valueOf(authorType.toUpperCase()));
		author.setSort(sort != null ? sort.toString() : "");
		authorService.save(author);
		resultJson.put("status", "1");
		resultJson.put("msg", "Success!");
		return resultJson;

	}

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "savePublisher", method = RequestMethod.POST)
	@JsonView(Views.Summary.class)
	public JSONObject savePublisher(@RequestBody JSONObject json) throws ServiceUnavailableException {
		JSONObject resultJson = new JSONObject();
		Publisher publisher = new Publisher();
		publisher.setBoId(SystemConstant.BOID_REQUIRED);
		String name = json.get("name").toString();
		if (name == null || name.isEmpty()) {
			resultJson.put("status", "0");
			resultJson.put("msg", "Please enter publisher name!");
			return resultJson;
		}
		String sort = json.get("sort").toString();
		publisher.setName(name);
		publisher.setSort(sort);
		publisher.setEntityStatus(EntityStatus.ACTIVE);
		publisherService.save(publisher);
		resultJson.put("status", "1");
		resultJson.put("msg", "Success!");
		return resultJson;
	}

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "editPublisher", method = RequestMethod.POST)
	@JsonView(Views.Summary.class)
	public JSONObject editPublisher(@RequestBody JSONObject json) throws ServiceUnavailableException {
		JSONObject resultJson = new JSONObject();

		String boId = json.get("boId").toString();

		String name = json.get("name").toString();
		if (name == null || name.isEmpty()) {
			resultJson.put("status", "0");
			resultJson.put("msg", "Please enter the name!");
			return resultJson;
		}

		String sort = json.get("sort").toString();

		Publisher publisher = publisherService.findByBoId(boId);
		publisher.setName(name);
		publisher.setSort(sort);
		publisher.setEntityStatus(EntityStatus.ACTIVE);
		publisherService.save(publisher);
		resultJson.put("status", "1");
		resultJson.put("msg", "Success!");
		return resultJson;
	}

	@CrossOrigin(origins = "*")
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

	@CrossOrigin(origins = "*")
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

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "watermark", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public String testWaterMark() throws ServiceUnavailableException {
		try {

			PdfReader reader = new PdfReader("C:\\Users\\DELL\\Project\\Elibrary\\80_NanDarMoeKyal_BoGyokeAungSan.pdf");
			PdfReader.unethicalreading = true;
			int n = reader.getNumberOfPages();
			PdfStamper stamp = new PdfStamper(reader, new FileOutputStream("C:\\Users\\DELL\\Project\\Elibrary\\PDFWithWatermarkImage4.pdf"));
			int i = 0;
			PdfContentByte under;
			Image img = Image.getInstance("C:\\Users\\DELL\\Project\\Elibrary\\images.jpg");
			img.setAbsolutePosition(0, 0);

			while (i < n) {
				i++;
				under = stamp.getOverContent(i);
				under.addImage(img);
			}
			stamp.close();

//			File file = new File("C:\\Users\\DELL\\Project\\Elibrary\\ThawDarSwe_ABrandeAndFamousShortStories.pdf");
//			PDDocument doc = PDDocument.load(file);
//			PDPage page = doc.getPage(0);
//			PDImageXObject pdImage = PDImageXObject.createFromFile("C:\\Users\\DELL\\Project\\Elibrary\\images.png", doc);
//			PDPageContentStream contents = new PDPageContentStream(doc, page);
//			contents.drawImage(pdImage, 70, 250);
//			contents.close();
//			doc.save("C:\\Users\\DELL\\Project\\Elibrary\\PDFWithWatermarkImage6.pdf");
//			doc.close();

		} catch (Exception de) {
			de.printStackTrace();
		}

		return "success";
	}

	private String getMessage(JSONObject json) {
		Object message = json.get("message");
		if (message == null || message.toString().isEmpty())
			return null;
		return message.toString();
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "feedback", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public JSONObject sendFeedback(@RequestHeader("token") String token, @RequestBody JSONObject json) throws ServiceUnavailableException {
		JSONObject resultJson = new JSONObject();

		if (!isTokenRight(token)) {
			resultJson.put("status", false);
			resultJson.put("message", "Unauthorized Request");
			return resultJson;
		}

		User user = getUser(json);
		if (user == null) {
			resultJson.put("status", false);
			resultJson.put("message", "User is not valid!");
			return resultJson;
		}

		String content = getMessage(json);
		if (content == null) {
			resultJson.put("status", false);
			resultJson.put("message", "Message must not empty!");
			return resultJson;
		}

		Feedback feedback = new Feedback();
		feedback.setBoId(SystemConstant.BOID_REQUIRED);
		feedback.setMessage(content);
		feedback.setEntityStatus(EntityStatus.ACTIVE);
		feedback.setUserId(user);
		feedbackService.save(feedback);

		resultJson.put("status", true);
		resultJson.put("message", "success!");
		return resultJson;
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "banners", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public JSONObject getImage(@RequestHeader("token") String token) throws IOException, ServiceUnavailableException {

		JSONObject resultJson = new JSONObject();
		if (!isTokenRight(token)) {
			resultJson.put("status", false);
			resultJson.put("message", "Unauthorized Request");
			return resultJson;
		}

		resultJson.put("status", true);
		resultJson.put("msg", "Success!");
		resultJson.put("advertisements", advertisementService.getAll());
		return resultJson;

	}

	private Feedback getFeedback(JSONObject json) {
		Object feedbackId = json.get("feedbackId");
		if (feedbackId == null || feedbackId.toString().isEmpty())
			return null;
		return feedbackService.findByBoId(feedbackId.toString());
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "reply", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public JSONObject reply(@RequestHeader("token") String token, @RequestBody JSONObject json) throws ServiceUnavailableException {
		JSONObject resultJson = new JSONObject();

		if (!isTokenRight(token)) {
			resultJson.put("status", false);
			resultJson.put("message", "Unauthorized Request");
			return resultJson;
		}

		Feedback feedback = getFeedback(json);
		if (feedback == null) {
			resultJson.put("status", false);
			resultJson.put("message", "This feedback is not found!");
			return resultJson;
		}

		String message = getMessage(json);
		if (message == null) {
			resultJson.put("status", false);
			resultJson.put("message", "Message must not empty!");
			return resultJson;
		}

		Reply reply = new Reply();
		reply.setBoId(SystemConstant.BOID_REQUIRED);
		Date date = new Date();
		String createdDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(date); // dd-M-yyyy hh:mm:ss
		reply.setDateTime(createdDate);
		reply.setMessage(message);
		reply.setEntityStatus(EntityStatus.ACTIVE);
		replyService.save(reply);

		feedback.setReplyId(reply);
		feedbackService.save(feedback);

		resultJson.put("status", true);
		resultJson.put("message", "success!");
		return resultJson;
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "replyNoti", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public JSONObject replyNoti(@RequestHeader("token") String token, @RequestBody JSONObject json) throws ServiceUnavailableException {
		JSONObject resultJson = new JSONObject();

		if (!isTokenRight(token)) {
			resultJson.put("status", false);
			resultJson.put("message", "Unauthorized Request");
			return resultJson;
		}

		User user = getUser(json);
		if (user == null) {
			resultJson.put("status", false);
			resultJson.put("message", "User is not valid!");
			return resultJson;
		}

		resultJson.put("feedbacks", feedbackService.findByUserId(user.getId()));
		resultJson.put("status", true);
		resultJson.put("message", "success!");
		return resultJson;
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "viewall", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public JSONObject viewAll(@RequestHeader("token") String token, @RequestBody JSONObject json) throws ServiceUnavailableException {
		JSONObject resultJson = new JSONObject();

		if (!isTokenRight(token)) {
			resultJson.put("status", false);
			resultJson.put("message", "Unauthorized Request");
			return resultJson;
		}

		User user = getUser(json);
		if (user == null) {
			resultJson.put("status", false);
			resultJson.put("message", "User is not valid!");
			return resultJson;
		}

		feedbackService.findByUserId(user.getId()).forEach(feedback -> {
			Reply reply = feedback.getReplyId();
			reply.setViewStatus(true);
			try {
				replyService.save(reply);
			} catch (ServiceUnavailableException e) {
				logger.error("Error: " + e);
			}
		});

		resultJson.put("status", true);
		resultJson.put("message", "success!");
		return resultJson;
	}

	@RequestMapping(value = "uploadImage", method = RequestMethod.POST) // advertise
	@ResponseBody
	@CrossOrigin(origins = "*")
	@JsonView(Views.Summary.class)
	public JSONObject uploadImage(@RequestBody JSONObject json) throws IOException, ServiceUnavailableException {
		JSONObject resultJson = new JSONObject();

		String mobileImageName = json.get("mobileImageName").toString();
		String mobileImage = json.get("mobileImage").toString();

		String image = json.get("image").toString();
		String imageName = json.get("imageName").toString();

		if (image == null || image.isEmpty() || imageName == null || imageName.isEmpty()) {
			resultJson.put("status", "0");
			resultJson.put("msg", "Please select an image!");
			return resultJson;
		}

		String pdf = json.get("pdfLink").toString();

		String pdfName = json.get("pdfName").toString();

		String filePath = IMAGEUPLOADURL.trim() + "Advertisement//";
		logger.info("filePath !!!!!!!!!!!!!!!" + filePath);

		image = image.split("base64")[1];
		byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(image.replaceAll(" ", "+"));
		Path destinationFile = Paths.get(filePath, imageName);
		Files.write(destinationFile, imageBytes);

		String mobileFilePath = IMAGEUPLOADURL.trim() + "Advertisement//";
		mobileImage = mobileImage.split("base64")[1];
		byte[] mobileImageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(mobileImage.replaceAll(" ", "+"));
		Path mobileDestinationFile = Paths.get(mobileFilePath, mobileImageName);
		Files.write(mobileDestinationFile, mobileImageBytes);

		if (!pdfName.isEmpty()) {
			String pdfFilePath = IMAGEUPLOADURL.trim() + "Advertisement//";
			pdf = pdf.split("base64")[1];
			byte[] pdfBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(pdf.replaceAll(" ", "+"));

			Path destinationPDFFile = Paths.get(pdfFilePath, pdfName);
			Files.write(destinationPDFFile, pdfBytes);
		}

		Advertisement mobileAdvertisement = new Advertisement();
		mobileAdvertisement.setBoId(SystemConstant.BOID_REQUIRED);
		mobileAdvertisement.setName("/Advertisement/" + mobileImageName);
		if (pdfName.isEmpty()) {
			mobileAdvertisement.setPdf(pdf);
		} else {
			mobileAdvertisement.setPdf("/Advertisement/" + pdfName);
		}
		mobileAdvertisement.setEntityStatus(EntityStatus.ACTIVE);

		Advertisement advertisement = new Advertisement();
		advertisement.setBoId(SystemConstant.BOID_REQUIRED);
		advertisement.setName("/Advertisement/" + imageName);
		if (pdfName.isEmpty()) {
			advertisement.setPdf(pdf);
		} else {
			advertisement.setPdf("/Advertisement/" + pdfName);
		}
		advertisement.setEntityStatus(EntityStatus.ACTIVE);
		Boolean is_pdf = pdfName.contains(".pdf");

		logger.info("is pdf:" + is_pdf);

		if (is_pdf == true) {
			mobileAdvertisement.setLinkType(LinkType.pdf);
			advertisement.setLinkType(LinkType.pdf);
			advertisement.setType(Type.Mobile);
		}

		else {
			mobileAdvertisement.setLinkType(LinkType.web);
			advertisement.setLinkType(LinkType.web);
			advertisement.setType(Type.Web);
		}

		advertisementService.save(mobileAdvertisement);
		advertisementService.save(advertisement);
		resultJson.put("status", "1");
		resultJson.put("msg", "success!");
		return resultJson;
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "getAdvertisements", method = RequestMethod.GET)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public JSONObject getAdvertisements(@RequestHeader("token") String token) throws IOException, ServiceUnavailableException {
		JSONObject resultJson = new JSONObject();

		if (!isTokenRight(token)) {
			resultJson.put("status", false);
			resultJson.put("message", "Unauthorized Request");
			return resultJson;
		}

		List<Advertisement> advertisements = advertisementService.getAll();
		resultJson.put("status", "1");
		resultJson.put("advertisements", advertisements);
		return resultJson;

	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "getFeedbacks", method = RequestMethod.GET)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public JSONObject getFeedbacks(@RequestHeader("token") String token) throws IOException, ServiceUnavailableException {
		JSONObject resultJson = new JSONObject();

		if (!isTokenRight(token)) {
			resultJson.put("status", false);
			resultJson.put("message", "Unauthorized Request");
			return resultJson;
		}

		List<Feedback> feedbacks = feedbackService.getAll();
		resultJson.put("status", "1");
		resultJson.put("feedbacks", feedbacks);
		return resultJson;

	}

	private String getFeedbackBoId(JSONObject json) {
		Object feedback = json.get("feedback_id");
		if (feedback == null || feedback.toString().isEmpty())
			return "";
		return feedback.toString();
	}

	private Long getUserId(JSONObject json) {
		Object user = json.get("user_id");
		if (user == null || user.toString().isEmpty())
			return (long) 0;
		return Long.parseLong(user.toString());
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "view", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public JSONObject getFeedbacks(@RequestHeader("token") String token, @RequestBody JSONObject json) throws IOException, ServiceUnavailableException {
		JSONObject resultJson = new JSONObject();

		if (!isTokenRight(token)) {
			resultJson.put("status", false);
			resultJson.put("message", "Unauthorized Request");
			return resultJson;
		}

		Feedback feedback = feedbackService.findByBoId(getFeedbackBoId(json));
		Reply reply = feedback.getReplyId();
		reply.setViewStatus(true);
		replyService.save(reply);
		resultJson.put("status", true);
		resultJson.put("message", "success");
		return resultJson;

	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "feedbackCount", method = RequestMethod.GET)
	@JsonView(Views.Summary.class)
	public String getCount() throws ServiceUnavailableException {
		return feedbackService.countFeedback() + "";
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "advertisementCount", method = RequestMethod.GET)
	@JsonView(Views.Summary.class)
	public String getAdvertisementCount() throws ServiceUnavailableException {
		return advertisementService.countActiveAdvertisement() + "";
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "deleteAdvertisement", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public JSONObject deleteAdvertisement(@RequestHeader("token") String token, @RequestBody JSONObject json) throws IOException, ServiceUnavailableException {
		JSONObject resultJson = new JSONObject();
		if (!isTokenRight(token)) {
			resultJson.put("status", false);
			resultJson.put("message", "Unauthorized Request");
			return resultJson;
		}

		String advertisementId = json.get("boId").toString();
		Advertisement advertisement = advertisementService.findByBoId(advertisementId);
		if (advertisement == null) {
			resultJson.put("status", "0");
			resultJson.put("msg", "Advertisement Id is invalid!!");
			return resultJson;

		}

		advertisement.setEntityStatus(EntityStatus.DELETED);
		advertisementService.save(advertisement);
		resultJson.put("status", "1");
		resultJson.put("msg", "Your request is successful!!");
		return resultJson;
	}

	/* run for data migration */
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "setCategory", method = RequestMethod.GET)
	@JsonView(Views.Thin.class)
	public String setCategory() {
		List<SubCategory> subCategoryList = subCategoryService.getAll();
		subCategoryList.forEach(sub -> {
			Category category = categoryService.findByCategoryId(categoryService.findBySubCategoryId(sub.getId()));
			sub.setCategoryBoId(category.getBoId());
			try {
				subCategoryService.save(sub);
			} catch (ServiceUnavailableException e) {
				logger.info("Error: " + e);
			}
		});
		return "success";
	}

	private boolean addWaterMark(String file, String watermarkFile) {
		try {

			String pdfFilePath = IMAGEUPLOADURL.trim();
			PdfReader reader = new PdfReader(file);
			PdfReader.unethicalreading = true;
			long n = reader.getNumberOfPages();

			PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(watermarkFile));
			long i = 0;
			PdfContentByte under;
			Image img = Image.getInstance(pdfFilePath + "watermark.png");
			img.setAbsolutePosition(0, 0);

			while (i < n) {
				i++;
				under = stamp.getOverContent((int) i);
				under.addImage(img);
			}

			stamp.close();

		} catch (Exception e) {
			logger.error("Error: " + e + " File: " + file);
			return false;
		}
		return true;

	}

	/* run for pdf files migration with water mark */
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "uploadfiles", method = RequestMethod.POST)
	@JsonView(Views.Thin.class)
	public JSONObject uploadfiles() throws URISyntaxException, IOException {
		JSONObject json = new JSONObject();

		List<Book> books = bookService.getBooks();

		int count = 12274;
		int errorCount = 0;

		for (Book book : books) {
			try {
				URIBuilder ub = new URIBuilder("http://localhost:8080/");
				String name = book.getName();
				String path = book.getPath();

				/* read pdf */
				String originalFilePath = PDFPATH.trim();
				String pdfPath = originalFilePath + path + "/" + name;
				ub.addParameter("q", pdfPath);
				String pdf = ub.toString().replace("?q=", "").replace("+", " ").replace("%2F", "/") + ".pdf";
				// pdfs.add(pdf);

				/* write pdf */
				String pdfFilePath = IMAGEUPLOADURL.trim();
				String watermarkFileName = "WaterMarkFile/" + "wartermark" + count + ".pdf";

				logger.info("read  : " + pdfPath + ".pdf");
				logger.info("write: " + pdfFilePath + watermarkFileName);

				if (!addWaterMark(pdfPath + ".pdf", pdfFilePath + watermarkFileName)) {
					errorCount++;
					count++;
					logger.info("Error Count !!!!!!!!!!!!!!" + errorCount);
					logger.info("Book Count !!!!!!!!!!!!!!" + count);
				} else {
					logger.info("Error Count !!!!!!!!!!!!!!" + errorCount);

					/* write image */
					URIBuilder ub2 = new URIBuilder("http://localhost:8080/");
					String coverPath = originalFilePath + path + "/" + "cover.jpg";
					// covers.add(coverPath);

					try {
						File initialImage = new File(coverPath);
						BufferedImage bImage = ImageIO.read(initialImage);
						logger.info("initialImage!!!!!!!" + initialImage);
						ImageIO.write(bImage, "jpg", new File(pdfFilePath + "BookProfile/cover" + count + ".jpg"));
					} catch (Exception e) {
						logger.error("Error: " + e);
					}

					/* set pdf and image */
					book.setCoverPhoto("/BookProfile/cover" + count + ".jpg");
					book.setPath("/" + watermarkFileName);
					bookService.save(book);
					count++;

					logger.info("Book Count !!!!!!!!!!!!!!" + count);
				}

			} catch (URISyntaxException | ServiceUnavailableException e) {
				logger.error("Error: " + e);
			}
		}

		logger.info("Book Count !!!!!!!!!!!!!!" + count);
		logger.info("bookService.getPaths()!!!!!!!!!!!" + books.size());

		return json;

	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "version", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Thin.class)
	private JSONObject getVersion() {
		JSONObject resultJson = new JSONObject();
		resultJson.put("appLink", "Cj0KCQiA0fr_BRDaARIsAABw4Ev3AxP89DaZnQ6TvWbfAlv08HjR_ny3gfYDE22MGx_RxwfpR3XQWcEaAmo_EALw_wcB");
		resultJson.put("version", 1);
		return resultJson;
	}

}