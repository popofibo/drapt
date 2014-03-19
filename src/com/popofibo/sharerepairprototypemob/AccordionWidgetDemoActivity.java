package com.popofibo.sharerepairprototypemob;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

import com.popofibo.sharerepairprototypemob.R;
import com.popofibo.sharerepairprototypemob.model.ImageModel;
import com.popofibo.sharerepairprototypemob.utils.ShareandrepairUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

@SuppressLint("NewApi")
public class AccordionWidgetDemoActivity extends Activity {
	private static final int IMAGE_CAPTURE = 0;
	private Button startBtn;
	private Button updateBtn;
	private Uri imageUri;
	private ImageView imageView;
	private Bitmap actualImage;
	private EditText username;
	private EditText userEmail;
	private Spinner productCategory;
	private EditText productComments;
	private String imagepath;
	private byte[] image;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		imageView = (ImageView) findViewById(R.id.img);
		startBtn = (Button) findViewById(R.id.startBtn);
		updateBtn = (Button) findViewById(R.id.updateBtn);
		username = (EditText) findViewById(R.id.EditTextName);
		userEmail = (EditText) findViewById(R.id.EditTextEmail);
		productCategory = (Spinner) findViewById(R.id.SpinnerCategoryType);
		productComments = (EditText) findViewById(R.id.EditTextCommentsBody);

		startBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startCamera();
			}
		});

	}

	private ImageModel pushDataToModel() {
		ImageModel model = new ImageModel();
		model.setImageName(imagepath);
		model.setImageSize(actualImage.getByteCount());
		model.setImage(image);
		model.setImageCategory((String) productCategory.getSelectedItem());
		model.setImageType(actualImage.getConfig().name());
		model.setProductComments(productComments.getText().toString());
		model.setUserName(username.getText().toString());
		model.setUserEmail(userEmail.getText().toString());
		model.setUserState("Not Implemented");
		return model;
	}

	public void startCamera() {
		DbImageTask task = new DbImageTask();
		task.execute(new String[] {});

		String fileName = ShareandrepairUtil.assignFilename();
		ContentValues values = new ContentValues();
		values.put(MediaStore.Images.Media.TITLE, fileName);
		values.put(MediaStore.Images.Media.DESCRIPTION,
				"Share & Repair image prototype");
		values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
		imageUri = getContentResolver().insert(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		startActivityForResult(intent, IMAGE_CAPTURE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == IMAGE_CAPTURE) {
			if (resultCode == RESULT_OK) {
				imageView.setImageBitmap(trimToThumbnail());
			}
		}
	}

	private class DbImageTask extends AsyncTask<String, Void, ImageModel> {

		@Override
		protected ImageModel doInBackground(String... params) {
			ImageModel model = new ImageModel();
			updateBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					ImageModel model = pushDataToModel();
					boolean isDataPushedToDb = ShareandrepairUtil
							.insertDataToDb(model);
					Log.d("TAGZIES", "Data inserted successfully -> "
							+ isDataPushedToDb);
				}
			});
			return model;

		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
			Toast.makeText(
					getBaseContext(),
					"Connection to Sordidcan DB successful? "
							+ (ShareandrepairUtil.connect() != null),
					Toast.LENGTH_LONG).show();
		}

	}

	public Bitmap trimToThumbnail() {
		Bitmap imgthumBitmap = null;
		try {

			final int THUMBNAIL_SIZE = 320;
			String[] proj = { MediaStore.Images.Media.DATA };

			Cursor cursor = getBaseContext().getContentResolver().query(
					imageUri, proj, null, null, null);
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			imagepath = cursor.getString(column_index);
			FileInputStream fis = new FileInputStream(imagepath);

			Log.d("TAGZIES", "imageURI set to " + imagepath);

			imgthumBitmap = BitmapFactory.decodeStream(fis);
			actualImage = imgthumBitmap;
			imgthumBitmap = Bitmap.createScaledBitmap(imgthumBitmap,
					THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);
			ByteArrayOutputStream bytearroutstream = new ByteArrayOutputStream();
			imgthumBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
					bytearroutstream);
			image = bytearroutstream.toByteArray();
		} catch (Exception ex) {
			Log.d("TAGZIES", "OOPSIES " + ex.getMessage());
		}
		return imgthumBitmap;
	}
}
