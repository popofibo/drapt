package com.popofibo.sharerepairprototypemob.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.popofibo.sharerepairprototypemob.model.ImageModel;

public class ShareandrepairUtil {

	public static Connection connect() {
		String url = "jdbc:mysql://sordid.db.6434967.hostedresource.com/";
		String dbName = "sordid";
		String driver = "com.mysql.jdbc.Driver";
		String userName = "xxxxx";
		String password = "xxxxxxx";
		try {
			Class.forName(driver).newInstance();
			Connection conn = DriverManager.getConnection(url + dbName,
					userName, password);

			return conn;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String assignFilename() {
		return "SNR"
				+ new SimpleDateFormat("ddMMyyy", Locale.US).format(new Date());
	}

	public static boolean insertDataToDb(ImageModel model) {
		Connection conn = connect();
		if (conn == null)
			return false;
		String insertSQL = "INSERT INTO testblob (image_type, image, image_size"
				+ ", image_ctgy, image_name, user_name, user_email, user_state"
				+ ", product_category, product_comments) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement preparedStatement;
		try {
			preparedStatement = conn.prepareStatement(insertSQL);
			preparedStatement.setString(1, model.getImageType());
			preparedStatement.setBytes(2, model.getImage());
			preparedStatement.setInt(3, model.getImageSize());
			preparedStatement.setString(4, model.getImageCategory());
			preparedStatement.setString(5, model.getImageName());
			preparedStatement.setString(6, model.getUserName());
			preparedStatement.setString(7, model.getUserEmail());
			preparedStatement.setString(8, model.getUserState());
			preparedStatement.setString(9, model.getProductCategory());
			preparedStatement.setString(10, model.getProductComments());
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;

	}

}
