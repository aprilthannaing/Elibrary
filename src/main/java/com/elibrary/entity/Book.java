package com.elibrary.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "book")
public class Book extends AbstractReadingObject implements Serializable {

	@JsonView(Views.Thin.class)
	private String ISBN;

	@JsonView(Views.Thin.class)
	private String edition;

	@JsonView(Views.Thin.class)
	private String sort;

	@JsonView(Views.Thin.class)
	private String path;

	@JsonView(Views.Thin.class)
	private String seriesIndex;

	@JsonView(Views.Thin.class)
	private String callNo;

	@JsonView(Views.Thin.class)
	private String accessionNo;

	@JsonView(Views.Thin.class)
	private int currentPage;

	// @JsonView(Views.Thin.class)
	private String size;

	@JsonView(Views.Thin.class)
	private String downloadApproval;

	@JsonView(Views.Thin.class)
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = "book_subcategory", joinColumns = @JoinColumn(name = "bookId"), inverseJoinColumns = @JoinColumn(name = "subcategoryId"))
	private SubCategory subCategory;

	@JsonView(Views.Thin.class)
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = "book_category", joinColumns = @JoinColumn(name = "bookId"), inverseJoinColumns = @JoinColumn(name = "categoryId"))
	private Category category;

	@JsonView(Views.Thin.class)
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = "book_author", joinColumns = @JoinColumn(name = "bookId"), inverseJoinColumns = @JoinColumn(name = "authorId"))
	private List<Author> authors = new ArrayList<Author>();

	@JsonView(Views.Thin.class)
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = "book_publisher", joinColumns = @JoinColumn(name = "bookId"), inverseJoinColumns = @JoinColumn(name = "publisherId"))
	private List<Publisher> publishers = new ArrayList<Publisher>();

	@JsonView(Views.Summary.class)
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = "book_rating", joinColumns = @JoinColumn(name = "bookId"), inverseJoinColumns = @JoinColumn(name = "ratingId"))
	private List<Rating> ratings = new ArrayList<Rating>();

	@JsonView(Views.Thin.class)
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = "book_comment", joinColumns = @JoinColumn(name = "bookId"), inverseJoinColumns = @JoinColumn(name = "commentId"))
	private Comment comment;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "uploader")
	private User uploader;

	@Transient
	@JsonView(Views.Thin.class)
	private boolean favouriteStatus;

	@Transient
	@JsonView(Views.Thin.class)
	private boolean bookMarkStatus;

	@Transient
	@JsonView(Views.Thin.class)
	private boolean readStatus;

	@JsonView(Views.Thin.class)
	private double ownRating;

	@JsonView(Views.Thin.class)
	private double averageRating;

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public boolean isFavouriteStatus() {
		return favouriteStatus;
	}

	public void setFavouriteStatus(boolean favouriteStatus) {
		this.favouriteStatus = favouriteStatus;
	}

	public boolean isBookMarkStatus() {
		return bookMarkStatus;
	}

	public void setBookMarkStatus(boolean bookMarkStatus) {
		this.bookMarkStatus = bookMarkStatus;
	}

	public boolean isReadStatus() {
		return readStatus;
	}

	public void setReadStatus(boolean readStatus) {
		this.readStatus = readStatus;
	}

	public double getOwnRating() {
		return ownRating;
	}

	public void setOwnRating(double ownRating) {
		this.ownRating = ownRating;
	}

	public double getAverageRating() {
		return averageRating;
	}

	public void setAverageRating(double averageRating) {
		this.averageRating = averageRating;
	}

	public User getUploader() {
		return uploader;
	}

	public void setUploader(User uploader) {
		this.uploader = uploader;
	}

	public String getISBN() {
		return ISBN;
	}

	public void setISBN(String iSBN) {
		ISBN = iSBN;
	}

	public String getEdition() {
		return edition;
	}

	public void setEdition(String edition) {
		this.edition = edition;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getSeriesIndex() {
		return seriesIndex;
	}

	public void setSeriesIndex(String seriesIndex) {
		this.seriesIndex = seriesIndex;
	}

	public String getCallNo() {
		return callNo;
	}

	public void setCallNo(String callNo) {
		this.callNo = callNo;
	}

	public String getAccessionNo() {
		return accessionNo;
	}

	public void setAccessionNo(String accessionNo) {
		this.accessionNo = accessionNo;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getDownloadApproval() {
		return downloadApproval;
	}

	public void setDownloadApproval(String downloadApproval) {
		this.downloadApproval = downloadApproval;
	}

	public SubCategory getSubCategory() {
		if (subCategory == null)
			subCategory = new SubCategory();
		return subCategory;
	}

	public void setSubCategory(SubCategory subCategory) {
		this.subCategory = subCategory;
	}

	public Category getCategory() {
		if (category == null)
			category = new Category();
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public List<Author> getAuthors() {
		if (authors == null)
			authors = new ArrayList<Author>();
		return authors;
	}

	public void setAuthors(List<Author> authors) {
		this.authors = authors;
	}

	public List<Publisher> getPublishers() {
		if (publishers == null)
			publishers = new ArrayList<Publisher>();

		return publishers;
	}

	public void setPublishers(List<Publisher> publishers) {
		this.publishers = publishers;
	}

	public List<Rating> getRatings() {
		return ratings;
	}

	public void setRatings(List<Rating> ratings) {
		this.ratings = ratings;
	}

	public Comment getComment() {
		if (comment == null)
			comment = new Comment();
		return comment;
	}

	public void setComment(Comment comment) {
		this.comment = comment;
	}

}
