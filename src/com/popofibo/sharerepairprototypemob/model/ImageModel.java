package com.popofibo.sharerepairprototypemob.model;

import java.util.Calendar;

public class ImageModel {

	private int imageId;
	private String imageType;
	private byte[] image;
	private int imageSize;
	private String imageCategory;
	private String imageName;
	private Calendar imageTime;
	private String userName;
	private String userEmail;

	public int getImageId() {
		return imageId;
	}

	public void setImageId(int imageId) {
		this.imageId = imageId;
	}

	public String getImageType() {
		return imageType;
	}

	public void setImageType(String imageType) {
		this.imageType = imageType;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public int getImageSize() {
		return imageSize;
	}

	public void setImageSize(int i) {
		this.imageSize = i;
	}

	public String getImageCategory() {
		return imageCategory;
	}

	public void setImageCategory(String imageCategory) {
		this.imageCategory = imageCategory;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public Calendar getImageTime() {
		return imageTime;
	}

	public void setImageTime(Calendar imageTime) {
		this.imageTime = imageTime;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserState() {
		return userState;
	}

	public void setUserState(String userState) {
		this.userState = userState;
	}

	public String getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
	}

	public String getProductComments() {
		return productComments;
	}

	public void setProductComments(String productComments) {
		this.productComments = productComments;
	}

	private String userState;
	private String productCategory;
	private String productComments;
}
