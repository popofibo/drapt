package com.popofibo.sharerepairprototypemob;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

import com.popofibo.sharerepairprototypemob.R;
import com.popofibo.sharerepairprototypemob.model.ImageModel;
import com.popofibo.sharerepairprototypemob.utils.ShareandrepairUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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
	private Handler mHandler = new Handler(Looper.getMainLooper());

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

		updateBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				DbImageTask task = new DbImageTask();
				ImageModel model = pushDataToModel();
				task.execute(new ImageModel[] { model });
			}
		});
	}

	private ImageModel pushDataToModel() {
		ImageModel model = new ImageModel();
		model.setImageName(imagepath);
		model.setImageSize(actualImage.getByteCount());
		model.setImage(image);
		model.setProductCategory((String) productCategory.getSelectedItem());
		model.setImageType(actualImage.getConfig().name());
		model.setProductComments(productComments.getText().toString());
		// model.setProductCategory(productCategory.getText().toString());
		model.setUserName(username.getText().toString());
		model.setUserEmail(userEmail.getText().toString());
		model.setUserState("Not Implemented");
		return model;
	}

	public void startCamera() {

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

	private class DbImageTask extends AsyncTask<ImageModel, Void, ImageModel> {

		@Override
		protected ImageModel doInBackground(ImageModel... models) {
			ImageModel model = models[0];
			final boolean isDataPushedToDb = ShareandrepairUtil
					.insertDataToDb(model);
			Log.d("TAGZIES", "Data inserted successfully -> "
					+ isDataPushedToDb);

			mHandler.post(new Runnable() {
				public void run() {
					String alertTitle = "Failed";
					String alertMessage = "Data failed to update";

					if (isDataPushedToDb) {
						alertTitle = "Data Updated";
						alertMessage = "Data succesfully updated";
					}
					new AlertDialog.Builder(AccordionWidgetDemoActivity.this)
							.setTitle(alertTitle)
							.setMessage(alertMessage)
							.setPositiveButton(android.R.string.yes,
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											if (isDataPushedToDb) {
												Log.d("TAGZIES",
														"Invalidating the parent view");
												ViewGroup group = (ViewGroup) findViewById(R.id.repair_linear_layout);
												Log.d("TAGZIES", "Child Count "
														+ group.getChildCount());
												for (int i = 0, count = group
														.getChildCount(); i < count; ++i) {
													View view = group
															.getChildAt(i);
													if (view instanceof EditText) {
														Log.d("TAGZIES",
																"Inside views");
														((EditText) view)
																.setText("");
													}
												}
												AccordionWidgetDemoActivity.this
														.recreate();
												Log.d("TAGZIES",
														"Invalidating the parent view - success");
											}
										}
									}).setIcon(R.drawable.ic_dialog_alert)
							.show();
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
