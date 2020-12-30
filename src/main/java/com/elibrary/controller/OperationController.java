package com.elibrary.controller;

import java.awt.image.BufferedImage;
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

import javax.imageio.ImageIO;

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
import com.elibrary.entity.AdvertisementType;
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
import com.elibrary.entity.Position;
import com.elibrary.entity.Publisher;
import com.elibrary.entity.Reply;
import com.elibrary.entity.State;
import com.elibrary.entity.SubCategory;
import com.elibrary.entity.SystemConstant;
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

	private void addWaterMark(String pdfName) {
		try {

			String pdfFilePath = IMAGEUPLOADURL.trim();
			PdfReader reader = new PdfReader(pdfFilePath + "BookFile/" + pdfName.trim());
			PdfReader.unethicalreading = true;
			int n = reader.getNumberOfPages();
			PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(pdfFilePath + "WaterMarkFile/" + pdfName.trim()));
			int i = 0;
			PdfContentByte under;
			Image img = Image.getInstance(pdfFilePath + "watermark.jpeg");
			img.setAbsolutePosition(0, 0);

			while (i < n) {
				i++;
				under = stamp.getOverContent(i);
				under.addImage(img);
			}
			stamp.close();

		} catch (Exception de) {
			de.printStackTrace();
		}

	}

	private boolean setPDFFile(JSONObject json, Book book) throws IOException {
		String pdf = json.get("pdf").toString();
		if (pdf == null || pdf.toString().isEmpty()) {
			return false;
		}

		try {
			pdf = pdf.split("base64")[1];
		} catch (Exception e) {
			logger.getLogger("error:" + e.getMessage());
			logger.info(pdf);
		}

		String pdfName = json.get("pdfName").toString().split("\\\\")[2];
		if (bookService.isDuplicatePDF("/BookFile/" + pdfName.trim())) {
			return false;
		}

		byte[] decodedBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(pdf);
		String pdfFilePath = IMAGEUPLOADURL.trim() + "BookFile//";

		File file = new File(pdfFilePath + pdfName);
		book.setSize(file.length() / 1024 + "KB");
		FileOutputStream fop = new FileOutputStream(file);
		fop.write(decodedBytes);
		addWaterMark(pdfName);

		book.setPath("/WaterMarkFile/" + pdfName.trim());
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
		if (category != null)
			book.setCategory(category);

		String accessionNo = json.get("accessionNo").toString();
		if (accessionNo == null || accessionNo.isEmpty()) {
			resultJson.put("status", "0");
			resultJson.put("msg", "Please enter the accession No!");
			return resultJson;
		}
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

		if (!setImage(json, book)) {
			resultJson.put("status", "0");
			resultJson.put("msg", "This Profile Picture is already registered!");
			return resultJson;
		}

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
		book.setAccessionNo(json.get("accessionNo") != null ? json.get("accessionNo").toString() : book.getAccessionNo());
		setBookInfo(book, json);

		logger.info("approve !!!!!!!!!!!!!" + book.getState());
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

	private JSONObject setAuthorProfile(Author author, JSONObject json) throws IOException {
		JSONObject errorMessage = new JSONObject();

		String imageSrc = json.get("imageSrc").toString();
		if (imageSrc == null || imageSrc.isEmpty()) {
			errorMessage.put("status", "0");
			errorMessage.put("msg", "Please select an image!");
			return errorMessage;
		}
		imageSrc = imageSrc.split("base64")[1];

		String filePath = IMAGEUPLOADURL.trim() + "AuthorProfile//";
		String profile = json.get("profilePicture").toString();
		if (profile == null || profile.isEmpty()) {
			errorMessage.put("status", "0");
			errorMessage.put("msg", "Please select a profile picture!");
			return errorMessage;
		}

		String pictureName = profile.split("\\\\")[2];
		String profilePicture = "/AuthorProfile/" + pictureName;
		if (authorService.isDuplicateProfile(profilePicture)) {
			errorMessage.put("status", "0");
			errorMessage.put("msg", "The profile picture is already registered!");
			return errorMessage;
		}

		byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(imageSrc.replaceAll(" ", "+"));
		Path destinationFile = Paths.get(filePath, pictureName);
		Files.write(destinationFile, imageBytes);

		/* to retrieve profile */
		author.setProfilePicture("/AuthorProfile/" + pictureName);
		return null;

	}

	private JSONObject setAuthorInfo(Author author, JSONObject json) {
		JSONObject errorMessage = new JSONObject();
		Object img = json.get("imageSrc").toString();
		if (img == null || img.toString().isEmpty()) {
			errorMessage.put("status", "0");
			errorMessage.put("msg", "Please select author profile picture!");
			return errorMessage;
		}

		String name = json.get("name").toString();
		if (name == null || name.isEmpty()) {
			errorMessage.put("status", "0");
			errorMessage.put("msg", "Please enter author name!");
			return errorMessage;
		}
		author.setName(name);

		Object sort = json.get("sort");
//		if (sort == null || sort.isEmpty()) {
//			errorMessage.put("status", "0");
//			errorMessage.put("msg", "Please enter sort!");
//			return errorMessage;
//		}

		String authorType = json.get("authorType").toString();
		if (authorType == null || authorType.isEmpty()) {
			errorMessage.put("status", "0");
			errorMessage.put("msg", "Please choose author type!");
			return errorMessage;
		}

		author.setAuthorType(AuthorType.valueOf(authorType.toUpperCase()));
		author.setSort(sort != null ? sort.toString() : "");
		author.setEntityStatus(EntityStatus.ACTIVE);
		return null;
	}

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "saveAuthor", method = RequestMethod.POST)
	@JsonView(Views.Summary.class)
	public JSONObject saveAuthor(@RequestBody JSONObject json) throws ServiceUnavailableException, IOException {
		JSONObject resultJson = new JSONObject();

		Author author = new Author();
		author.setBoId(SystemConstant.BOID_REQUIRED);
		resultJson = setAuthorInfo(author, json);
		if (resultJson != null)
			return resultJson;

		if (json.get("imageSrc").toString().contains("base64")) {
			resultJson = setAuthorProfile(author, json);
			if (resultJson != null)
				return resultJson;

			resultJson = new JSONObject();
			authorService.save(author);
			resultJson.put("status", "1");
			resultJson.put("msg", "Success!");
		}

		return resultJson;
	}

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "editAuthor", method = RequestMethod.POST)
	@JsonView(Views.Summary.class)
	public JSONObject editAuthor(@RequestBody JSONObject json) throws ServiceUnavailableException, IOException {
		JSONObject resultJson = new JSONObject();

		Author author = authorService.findByBoId(json.get("boId").toString());
		resultJson = setAuthorInfo(author, json);
		if (resultJson != null)
			return resultJson;

		if (json.get("imageSrc").toString().contains("base64")) {
			resultJson = setAuthorProfile(author, json);
			if (resultJson != null)
				return resultJson;

			authorService.save(author);

		}
		resultJson = new JSONObject();
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
		if (sort == null || sort.isEmpty()) {
			resultJson.put("status", "0");
			resultJson.put("msg", "Please enter sort!");
			return resultJson;
		}
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
		if (sort == null || sort.isEmpty()) {
			resultJson.put("status", "0");
			resultJson.put("msg", "Please enter sort!");
			return resultJson;
		}
		Publisher publisher = publisherService.findByBoId(boId);
		publisher.setName(name);
		publisher.setSort(sort);
		publisher.setEntityStatus(EntityStatus.ACTIVE);
		publisherService.save(publisher);
		resultJson.put("status", "1");
		resultJson.put("msg", "Success!");
		return resultJson;
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
	
	@RequestMapping(value = "uploadImage", method = RequestMethod.POST) // advertise
	@ResponseBody
	@JsonView(Views.Summary.class)
	public JSONObject uploadImage(@RequestBody JSONObject json) throws IOException, ServiceUnavailableException {
		JSONObject resultJson = new JSONObject();
		
		String image = json.get("image").toString();
		String imageName = json.get("imageName").toString();
		if(image == null || image.isEmpty() || imageName == null || imageName.isEmpty()) {
			resultJson.put("status", "0");
			resultJson.put("msg", "Please select an image!");
			return resultJson;
		}
		
		String pdf = json.get("pdf").toString();
		String pdfName = json.get("pdfName").toString();
		
		String filePath = IMAGEUPLOADURL.trim() + "Advertisement//";
		image = image.split("base64")[1];
		byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(image.replaceAll(" ", "+"));
		Path destinationFile = Paths.get(filePath, imageName);
		Files.write(destinationFile, imageBytes);
		
		BufferedImage bimg = ImageIO.read(new File(filePath + imageName));
		int width          = bimg.getWidth();
		int height         = bimg.getHeight();
		
		String pdfFilePath = IMAGEUPLOADURL.trim() + "Advertisement//";
		pdf = pdf.split("base64")[1];
		byte[] pdfBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(pdf.replaceAll(" ", "+"));
		Path destinationPDFFile = Paths.get(pdfFilePath, pdfName);
		Files.write(destinationPDFFile, pdfBytes);
		
		Advertisement advertisement = new Advertisement();
		advertisement.setBoId(SystemConstant.BOID_REQUIRED);
		advertisement.setName("Advertisement/" + imageName);
		advertisement.setPdf("Advertisement/" + pdfName);
		advertisement.setEntityStatus(EntityStatus.ACTIVE);
		if(width == 1170 && height == 268) {
			advertisement.setType(AdvertisementType.Web);
		}
		
		else if(width == 991 && height == 350 ) {
			advertisement.setType(AdvertisementType.Mobile);
		}
		
		advertisementService.save(advertisement);
		resultJson.put("status", "1");
		resultJson.put("msg", "success!");
		resultJson.put("width", width);
		resultJson.put("height", height);
		return resultJson;
	}
	
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
	
	@RequestMapping(value = "feedbackCount", method = RequestMethod.GET)
	@JsonView(Views.Summary.class)
	public String getCount() throws ServiceUnavailableException {
		return feedbackService.countFeedback() + "";
	}
	
	@RequestMapping(value = "advertisementCount", method = RequestMethod.GET)
	@JsonView(Views.Summary.class)
	public String getAdvertisementCount() throws ServiceUnavailableException {
		return advertisementService.countAdvertisement() + "";
	}
	
	
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
		return resultJson;	}
	
	

}