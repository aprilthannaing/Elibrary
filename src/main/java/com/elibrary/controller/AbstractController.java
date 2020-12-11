package com.elibrary.controller;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.elibrary.entity.Author;
import com.elibrary.entity.Book;
import com.elibrary.entity.Category;
import com.elibrary.entity.Request;
import com.elibrary.entity.SubCategory;
import com.elibrary.entity.User;
import com.elibrary.service.AuthorService;
import com.elibrary.service.CategoryService;
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

	public final String authorization = "7M8N3SLQ8QIKDJOSEPXJKJDFOZIN1NBO";

	public static DecimalFormat df2 = new DecimalFormat("#.##");

	public boolean isTokenRight(String token) {
		return sessionService.findByBoId(token) != null;
	}

	public String dateFormat() {
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
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
		
		if(!fullname.equals("")) {
			String[] names = fullname.split(" "); 
			if(names.length > 1) {
				String fname = names[0];
				String lname = names[names.length-1];
				output = fname.substring(0,1).toUpperCase() + lname.substring(0,1).toUpperCase();
			}else {
				output = names[0].substring(0,1).toUpperCase();
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
		Object categoryObject = json.get("category_id");
		if (categoryObject == null || categoryObject.toString().isEmpty())
			return null;
		return categoryService.findByBoId(categoryObject.toString());
	}

	public SubCategory getSubCategory(JSONObject json) {
		Object subCategoryObject = json.get("sub_category_id");
		if (subCategoryObject == null || subCategoryObject.toString().isEmpty())
			return null;
		return subCategoryService.findByBoId(subCategoryObject.toString());
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
			book.setAverageRating(ratingService.getAverageRating(book.getId()));
			book.setOwnRating(ratingService.getOwnRating(user.getId(), book.getId()));
			resultBookList.add(book);
		}
		return resultBookList;
	}
	
	public List<User> getUsersByPagination(Request json, List<User> userList, int pageNo){
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

}
