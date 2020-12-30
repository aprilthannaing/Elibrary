package com.elibrary.controller;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.elibrary.entity.Author;
import com.elibrary.entity.Book;
import com.elibrary.entity.Category;
import com.elibrary.entity.Publisher;
import com.elibrary.entity.Rating;
import com.elibrary.entity.Request;
import com.elibrary.entity.SubCategory;
import com.elibrary.entity.User;
import com.elibrary.service.AuthorService;
import com.elibrary.service.BookService;
import com.elibrary.service.CategoryService;
import com.elibrary.service.HistoryService;
import com.elibrary.service.RatingService;
import com.elibrary.service.SessionService;
import com.elibrary.service.SubCategoryService;
import com.elibrary.service.UserService;

@Service
public class AbstractController {

	@Autowired
	private SessionService sessionService;

	public static final String secretKey = "mykey@91mykey@91";

	@Autowired
	private AuthorService authorService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private SubCategoryService subCategoryService;

	@Autowired
	private UserService userService;

	@Autowired
	private RatingService ratingService;

	@Autowired
	private BookService bookService;

	@Autowired
	private HistoryService historyService;

	public final String authorization = "7M8N3SLQ8QIKDJOSEPXJKJDFOZIN1NBO";

	public final String secretKeyByMobile = "7M8N3SLQ8QIKDJOSEPXJKJDFOZIN1NBO";

	public static DecimalFormat df2 = new DecimalFormat("#.##");

	public static Logger logger = Logger.getLogger(AbstractController.class);

	public boolean isTokenRight(String token) {
		return sessionService.findByBoId(token) != null;
	}

	public String dateFormat() {
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDateTime now = LocalDateTime.now();
		return dateFormat.format(now);
	}

	public String dateTimeFormat() {
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		return dateFormat.format(now);
	}

	public static String getRandomNumberString() {
		Random rnd = new Random();
		int number = rnd.nextInt(999999);
		return String.format("%06d", number);
	}

	public String generateSession(Long id) {

		char[] chars = id.toString().toCharArray();
		String key = "S1S2S3";
		int iterations = 500;
		byte[] salt = key.getBytes();
		String hashPass = "";
		PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 2);
		SecretKeyFactory skf;
		try {
			skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			byte[] hash = skf.generateSecret(spec).getEncoded();
			hashPass = Hex.encodeHexString(hash);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
		return hashPass;
	}

	public String initialName(String fullname) {
		String output = "";

		if (!fullname.equals("")) {
			String[] names = fullname.split(" ");
			if (names.length > 1) {
				String fname = names[0];
				String lname = names[names.length - 1];
				output = fname.substring(0, 1).toUpperCase() + lname.substring(0, 1).toUpperCase();
			} else {
				output = names[0].substring(0, 1).toUpperCase();
			}

		}
		return output;
	}

	public User getUser(JSONObject json) {
		Object userId = json.get("user_id");
		if (userId == null || userId.toString().isEmpty())
			return null;

		return userService.findByBoId(userId.toString());
	}

	public Author getAuthor(JSONObject json) {
		Object authorObject = json.get("author_id");
		if (authorObject == null || authorObject.toString().isEmpty())
			return null;
		return authorService.findByBoId(authorObject.toString());
	}

	public Category getCategory(JSONObject json) {
		Object categoryObject = json.get("category_Id");
		if (categoryObject == null || categoryObject.toString().isEmpty())
			return null;

		return categoryService.findByBoId(categoryObject.toString());
	}

	public int getPage(JSONObject json) {
		Object page = json.get("page");
		if (page == null || page.toString().isEmpty())
			return 0;
		return Integer.parseInt(page.toString());
	}

	public SubCategory getSubCategory(JSONObject json) {
		Object subCategoryObject = json.get("sub_category_id");
		if (subCategoryObject == null || subCategoryObject.toString().isEmpty())
			return null;
		return subCategoryService.findByBoId(subCategoryObject.toString());
	}

	public List<Book> getBooksByPaganationWithBookIds(JSONObject json, List<Long> bookList, int pageNo) {
		User user = getUser(json);
		if (user == null)
			return null;
		List<Book> resultBookList = new ArrayList<Book>();
		int lastIndex = (bookList.size() - 1) - (pageNo * 10 - 10);
		int substract = lastIndex < 9 ? lastIndex : 9;
		int startIndex = lastIndex - substract;

		for (int i = lastIndex; i >= startIndex; i--) {
			Book book = bookService.findById(bookList.get(i));
			if (book == null)
				continue;
			setBookInfo(book, user);
			resultBookList.add(book);
		}
		return resultBookList;
	}

	public List<Book> setBookInfo(List<Book> bookList, User user) {
		bookList.forEach(book -> {
			setBookInfo(book, user);
		});
		return bookList;

	}

	public void setBookInfo(Book book, User user) {
		Long userId = user.getId();
		Long bookId = book.getId();
		book.setAverageRating(ratingService.getAverageRating(bookId));
		book.setOwnRating(ratingService.getOwnRating(userId, bookId));
		if (historyService.isFavourite(userId, bookId))
			book.setFavouriteStatus(true);
		if (historyService.isBookMark(userId, bookId))
			book.setBookMarkStatus(true);
		if (historyService.isRead(userId, bookId))
			book.setReadStatus(true);
	}

	public List<Book> getBooksByPaganation(JSONObject json, List<Book> bookList, int pageNo) {
		User user = getUser(json);
		if (user == null)
			return null;
		List<Book> resultBookList = new ArrayList<Book>();
		int lastIndex = (bookList.size() - 1) - (pageNo * 10 - 10);
		int substract = lastIndex < 9 ? lastIndex : 9;
		int startIndex = lastIndex - substract;

		for (int i = lastIndex; i >= startIndex; i--) {

			Book book = bookList.get(i);
			setBookInfo(book, user);
			resultBookList.add(book);
		}
		return resultBookList;
	}

	public List<User> getUsersByPagination(Request json, List<User> userList, int pageNo) {
		List<User> resultUserList = new ArrayList<User>();

		int lastIndex = (userList.size() - 1) - (pageNo * 10 - 10);
		int substract = lastIndex < 9 ? lastIndex : 9;
		int startIndex = lastIndex - substract;
		for (int i = lastIndex; i >= startIndex; i--) {

			User user = userList.get(i);

			resultUserList.add(user);
		}

		return resultUserList;

	}

	public List<Author> getAuthorByPaganation(List<Author> authorList, int pageNo) {
		List<Author> resultAuthorList = new ArrayList<Author>();
		int lastIndex = (authorList.size() - 1) - (pageNo * 10 - 10);
		int substract = lastIndex < 9 ? lastIndex : 9;
		int startIndex = lastIndex - substract;

		for (int i = lastIndex; i >= startIndex; i--) {
			Author author = authorList.get(i);
			resultAuthorList.add(author);
		}
		return resultAuthorList;
	}

	public List<Author> getAuthorsByPagination(List<Long> authorIdList, int pageNo) {
		List<Author> resultAuthorList = new ArrayList<Author>();
		int lastIndex = (authorIdList.size() - 1) - (pageNo * 10 - 10);
		int substract = lastIndex < 9 ? lastIndex : 9;
		int startIndex = lastIndex - substract;

		for (int i = lastIndex; i >= startIndex; i--) {
			Author author = authorService.findById(authorIdList.get(i));
			resultAuthorList.add(author);
		}
		return resultAuthorList;

	}

	public void writeValueinSpecificeCellWithColumn(Workbook workbook, String sheetName, String columnName, int rowNumber, String value, short fontSize, short color) {

		Sheet sheet = workbook.getSheet(sheetName);
		Row row = null;
		int columnNumber = CellReference.convertColStringToIndex(columnName);
		row = sheet.getRow(rowNumber);
		sheet.setColumnWidth(3, 25 * 256);

		Font font = workbook.createFont();
		font.setFontHeightInPoints(fontSize);
		font.setColor(color);

		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setFont(font);
	
		Cell cell;
		if (row == null)
			row = sheet.createRow(rowNumber);
		cell = row.getCell(columnNumber);

		if (cell == null)
			cell = row.createCell(columnNumber);

		cell.setCellStyle(cellStyle);
		cell.setCellValue(value);
	}

	public void writeValueinSpecificeCellWithBackGroundColor(Workbook workbook, String sheetName, String columnName, int rowNumber, String value, short fontSize, short color) {

		Sheet sheet = workbook.getSheet(sheetName);
		Row row = null;
		int columnNumber = CellReference.convertColStringToIndex(columnName);
		row = sheet.getRow(rowNumber);

		Font font = workbook.createFont();
		font.setFontHeightInPoints(fontSize);
		font.setColor(color);

		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		sheet.setColumnWidth(3, 25 * 256);
		cellStyle.setFont(font);

		Cell cell;
		if (row == null)
			row = sheet.createRow(rowNumber);
		cell = row.getCell(columnNumber);

		if (cell == null)
			cell = row.createCell(columnNumber);

		cell.setCellStyle(cellStyle);
		cell.setCellValue(value);
	}

	public void writeTitle(XSSFWorkbook workbook, XSSFSheet sheet, String title) {

		writeValueinSpecificeCellWithColumn(workbook, sheet.getSheetName(), "H", 0, title, (short) 20, IndexedColors.BLUE.index);
		writeValueinSpecificeCellWithBackGroundColor(workbook, sheet.getSheetName(), "A", 2, " ISBN ", (short) 13, IndexedColors.BLACK.index);
		writeValueinSpecificeCellWithBackGroundColor(workbook, sheet.getSheetName(), "B", 2, " Title ", (short) 13, IndexedColors.BLACK.index);
		writeValueinSpecificeCellWithBackGroundColor(workbook, sheet.getSheetName(), "C", 2, " Edition ", (short) 13, IndexedColors.BLACK.index);
		writeValueinSpecificeCellWithBackGroundColor(workbook, sheet.getSheetName(), "D", 2, " Publisher ", (short) 13, IndexedColors.BLACK.index);
		writeValueinSpecificeCellWithBackGroundColor(workbook, sheet.getSheetName(), "E", 2, " Call No. ", (short) 13, IndexedColors.BLACK.index);
		writeValueinSpecificeCellWithBackGroundColor(workbook, sheet.getSheetName(), "F", 2, " Accession No. ", (short) 13, IndexedColors.BLACK.index);
		writeValueinSpecificeCellWithBackGroundColor(workbook, sheet.getSheetName(), "G", 2, " Author Name ", (short) 13, IndexedColors.BLACK.index);
		writeValueinSpecificeCellWithBackGroundColor(workbook, sheet.getSheetName(), "H", 2, " Subcategory ", (short) 13, IndexedColors.BLACK.index);
		writeValueinSpecificeCellWithBackGroundColor(workbook, sheet.getSheetName(), "I", 2, " Category ", (short) 13, IndexedColors.BLACK.index);
		writeValueinSpecificeCellWithBackGroundColor(workbook, sheet.getSheetName(), "J", 2, " Published Date ", (short) 13, IndexedColors.BLACK.index);
		writeValueinSpecificeCellWithBackGroundColor(workbook, sheet.getSheetName(), "K", 2, " State ", (short) 13, IndexedColors.BLACK.index);
		writeValueinSpecificeCellWithBackGroundColor(workbook, sheet.getSheetName(), "L", 2, " Modified Date ", (short) 13, IndexedColors.BLACK.index);
		writeValueinSpecificeCellWithBackGroundColor(workbook, sheet.getSheetName(), "M", 2, " Created Date ", (short) 13, IndexedColors.BLACK.index);
		writeValueinSpecificeCellWithBackGroundColor(workbook, sheet.getSheetName(), "N", 2, " Sort ", (short) 13, IndexedColors.BLACK.index);
		writeValueinSpecificeCellWithBackGroundColor(workbook, sheet.getSheetName(), "O", 2, " Path ", (short) 13, IndexedColors.BLACK.index);
		writeValueinSpecificeCellWithBackGroundColor(workbook, sheet.getSheetName(), "P", 2, " Series Index ", (short) 13, IndexedColors.BLACK.index);
		writeValueinSpecificeCellWithBackGroundColor(workbook, sheet.getSheetName(), "Q", 2, " Size ", (short) 13, IndexedColors.BLACK.index);
		writeValueinSpecificeCellWithBackGroundColor(workbook, sheet.getSheetName(), "R", 2, " Download Approval ", (short) 13, IndexedColors.BLACK.index);
		writeValueinSpecificeCellWithBackGroundColor(workbook, sheet.getSheetName(), "S", 2, " Uploader ", (short) 13, IndexedColors.BLACK.index);
		writeValueinSpecificeCellWithBackGroundColor(workbook, sheet.getSheetName(), "T", 2, " Own Rating ", (short) 13, IndexedColors.BLACK.index);
		writeValueinSpecificeCellWithBackGroundColor(workbook, sheet.getSheetName(), "U", 2, " Average Rating ", (short) 13, IndexedColors.BLACK.index);
	}

	public boolean writeBookSheet(XSSFWorkbook workbook, List<Book> bookList, String startDate, String endDate) throws SQLException {
		XSSFSheet sheet = workbook.getSheetAt(0);

		int count = 3;
		for (Book book : bookList) {
			if (book == null)
				continue;

			List<Publisher> publishers = book.getPublishers();
			for (Publisher publisher : publishers) {
				logger.info("Publisher Name: " + publisher.getName());
			}
			List<Author> authors = book.getAuthors();
			for (Author author : authors) {
				logger.info("Author Name: " + author.getName());
			}
			SubCategory subcategory = book.getSubCategory();
			Category category = book.getCategory();
			User user = book.getUploader();
			String uploader = "";
			if (user == null || user.toString().isEmpty()) {

				uploader = "";
			}

			writeValueinSpecificeCellWithColumn(workbook, sheet.getSheetName(), "A", count, book.getISBN(), (short) 13, IndexedColors.BLACK.index);
			writeValueinSpecificeCellWithColumn(workbook, sheet.getSheetName(), "B", count, book.getTitle(), (short) 13, IndexedColors.BLACK.index);
			writeValueinSpecificeCellWithColumn(workbook, sheet.getSheetName(), "C", count, book.getEdition(), (short) 13, IndexedColors.BLACK.index);
			String publisherNames = "";
			for (Publisher publisher : publishers) {
				String publisherName = publisher.getName();
				logger.info(publisherName);

				publisherNames += publisherName + ",";

			}
			writeValueinSpecificeCellWithColumn(workbook, sheet.getSheetName(), "D", count, publisherNames, (short) 13, IndexedColors.BLACK.index);

			writeValueinSpecificeCellWithColumn(workbook, sheet.getSheetName(), "E", count, book.getCallNo(), (short) 10, IndexedColors.BLACK.index);
			writeValueinSpecificeCellWithColumn(workbook, sheet.getSheetName(), "F", count, book.getAccessionNo() + "", (short) 10, IndexedColors.BLACK.index);
			String authorNames = "";
			for (Author author : authors) {
				String authorName = author.getName();

				authorNames += authorName + ",";
			}
			writeValueinSpecificeCellWithColumn(workbook, sheet.getSheetName(), "G", count, authorNames + "\n", (short) 10, IndexedColors.BLACK.index);
			writeValueinSpecificeCellWithColumn(workbook, sheet.getSheetName(), "H", count, subcategory.getMyanmarName(), (short) 10, IndexedColors.BLACK.index);
			writeValueinSpecificeCellWithColumn(workbook, sheet.getSheetName(), "I", count, category.getMyanmarName(), (short) 10, IndexedColors.BLACK.index);
			writeValueinSpecificeCellWithColumn(workbook, sheet.getSheetName(), "J", count, book.getPublishedDate(), (short) 10, IndexedColors.BLACK.index);
			writeValueinSpecificeCellWithColumn(workbook, sheet.getSheetName(), "K", count, book.getState().toString(), (short) 10, IndexedColors.BLACK.index);
			writeValueinSpecificeCellWithColumn(workbook, sheet.getSheetName(), "L", count, book.getModifiedDate(), (short) 10, IndexedColors.BLACK.index);
			writeValueinSpecificeCellWithColumn(workbook, sheet.getSheetName(), "M", count, book.getCreatedDate(), (short) 10, IndexedColors.BLACK.index);
			writeValueinSpecificeCellWithColumn(workbook, sheet.getSheetName(), "N", count, book.getSort(), (short) 10, IndexedColors.BLACK.index);
			writeValueinSpecificeCellWithColumn(workbook, sheet.getSheetName(), "O", count, book.getPath(), (short) 10, IndexedColors.BLACK.index);
			writeValueinSpecificeCellWithColumn(workbook, sheet.getSheetName(), "P", count, book.getSeriesIndex(), (short) 10, IndexedColors.BLACK.index);
			writeValueinSpecificeCellWithColumn(workbook, sheet.getSheetName(), "Q", count, book.getSize(), (short) 10, IndexedColors.BLACK.index);
			writeValueinSpecificeCellWithColumn(workbook, sheet.getSheetName(), "R", count, book.getDownloadApproval(), (short) 10, IndexedColors.BLACK.index);
			writeValueinSpecificeCellWithColumn(workbook, sheet.getSheetName(), "S", count, uploader, (short) 10, IndexedColors.BLACK.index);

			Double rate = book.getOwnRating();
			String rating = rate.toString();
			writeValueinSpecificeCellWithColumn(workbook, sheet.getSheetName(), "T", count, rating, (short) 10, IndexedColors.BLACK.index);

			Double averageRate = book.getAverageRating();
			String averageRating = averageRate.toString();
			writeValueinSpecificeCellWithColumn(workbook, sheet.getSheetName(), "U", count, averageRating, (short) 10, IndexedColors.BLACK.index);
			count++;
		}

		if (sheet.getLastRowNum() == 0 && sheet.getRow(0) == null) {
			return false;
		} else
			return true;

	}

	public String parseMonthToInt(String month) {
		switch (month) {
		case "Jan":
			return "01";
		case "Feb":
			return "02";
		case "Mar":
			return "03";
		case "Apr":
			return "04";
		case "May":
			return "05";
		case "June":
			return "06";
		case "July":
			return "07";
		case "Aug":
			return "08";
		case "Sep":
			return "09";
		case "Oct":
			return "10";
		case "Nov":
			return "11";
		case "Dec":
			return "12";
		}
		return "-1";
	}
}
