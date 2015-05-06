package com.haomee.yocker;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haomee.chat.Utils.BitmapUtil;
import com.haomee.consts.PathConsts;
import com.haomee.util.FileDownloadUtil;
import com.haomee.util.NetworkUtil;
import com.haomee.yocker.cropper.CropImageView;

public class ImageCropActivity extends BaseActivity {
	private static final int DEFAULT_ASPECT_RATIO_VALUES = 10;

	private CropImageView cropImageView;
	private String pre_path; // 传递过来的图片路径
	private boolean pre_flag; // 传过来的标识位
	private int screen_width, screen_height;
	private TextView tv_ok, tv_canle;
	private int x;
	private int y;
	private TextView tv_crop;

	private RelativeLayout rl_layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_image_crop);
		tv_ok = (TextView) findViewById(R.id.tv_ok);
		tv_canle = (TextView) findViewById(R.id.tv_canle);

		tv_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				gotoNextStep();
			}
		});

		tv_canle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();

			}
		});
		rl_layout = (RelativeLayout) findViewById(R.id.rl_image);
		tv_crop = (TextView) findViewById(R.id.tv_crop);
		tv_crop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int switchsize = switchsize();
				switch (switchsize) {
				case 0:
					x = y = 1;
					break;
				case 1:
					x = 4;
					y = 3;
					break;
				case 2:
					x = 3;
					y = 4;

					break;
				case 3:
					x = 674;
					y = 250;
					break;
				default:
					break;
				}
				cropImageView.setAspectRatio(x, y);
				tv_crop.setText(x + ":" + y);
			}
		});

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screen_width = dm.widthPixels;
		screen_height = dm.heightPixels;
		cropImageView = (CropImageView) findViewById(R.id.CropImageView);

		// 有可能是从相册选择的
		Intent preIntent = this.getIntent();
		pre_path = preIntent.getStringExtra("path");
		pre_flag = preIntent.getBooleanExtra("flag", true);

		Bitmap bitmap = null;
		
			if (pre_path != null) {
				bitmap = BitmapUtil.getBitmapFromSDCard(pre_path);
			} else {
				bitmap = BitmapUtil.temp; // 来自照相
			}

			

		
		if (bitmap != null) {
			bitmap = resizeSurfaceWithScreen(bitmap, screen_width, screen_height);
			int degree = readPictureDegree(pre_path);
			bitmap = rotaingImageView(degree, bitmap);
			cropImageView.setImageBitmap(bitmap);
		}
		if (pre_flag) {
			cropImageView.setAspectRatio(DEFAULT_ASPECT_RATIO_VALUES, DEFAULT_ASPECT_RATIO_VALUES);

		} else {
			cropImageView.setAspectRatio(1, 1);// 規定裁剪的尺寸
			rl_layout.setVisibility(View.INVISIBLE);
		}
	}

	private int size_index = 0;

	public int switchsize() {
		size_index++;
		if (size_index >= 4) {
			size_index = 0;
		}
		return size_index;
	}

	// 根据屏幕大小调整显示大小
	private Bitmap resizeSurfaceWithScreen(Bitmap bitmap, int screen_width, int screen_height) {

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		// 太小了放大
		if (width < screen_width && height < screen_height) {
			float scale_width = screen_width * 1.0f / width;
			float scale_height = screen_height * 1.0f / height;
			float scale = scale_width > scale_height ? scale_height : scale_width; // 取较小者
			width *= scale;
			height *= scale;
		} else {
			// 太大了缩小
			if (width > screen_width) {
				height = height * screen_width / width;
				width = screen_width;
			}
			if (height > screen_height) {
				width = width * screen_height / height;
				height = screen_height;
			}
		}

		bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);

		bitmap = BitmapUtil.zoomBitmap(bitmap, width, height);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
		while (baos.toByteArray().length > 1024 * 1024) {
			baos.reset();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
		}

		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		Bitmap bitmap_new = BitmapFactory.decodeStream(isBm, null, null);

		return bitmap_new;

	}

	public String saveBitmap(Bitmap bm) {
		Long tolong = System.currentTimeMillis() / 1000;
		File f = new File(FileDownloadUtil.getDefaultLocalDir(PathConsts.DIR_TEMP), tolong.toString());
		if (f.exists()) {
			f.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int options = 100;
			while (baos.toByteArray().length / 1024 > 400) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
				baos.reset();// 重置baos即清空baos
				bm.compress(Bitmap.CompressFormat.JPEG, options, out);// 这里压缩options%，把压缩后的数据存放到baos中
				options -= 10;// 每次都减少10
			}
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return f.getAbsolutePath();
	}

	public void gotoNextStep() {

		try {
			Bitmap croppedImage = cropImageView.getCroppedImage();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			croppedImage.compress(Bitmap.CompressFormat.JPEG, 80, baos);
			byte[] bitmapByte = baos.toByteArray();

			int width = croppedImage.getWidth();
			int heigth = croppedImage.getHeight();

			Intent data = new Intent();
			data.putExtra("bitmap", bitmapByte);
			data.putExtra("path", saveBitmap(croppedImage));
			data.putExtra("width", width);
			data.putExtra("heigth", heigth);
			setResult(20, data);
			finish();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 旋转图片
	 * 
	 * @param angle
	 * @param bitmap
	 * @return Bitmap
	 */
	public Bitmap rotaingImageView(int angle, Bitmap bitmap) {
		// 旋转图片 动作
		Matrix matrix = new Matrix();
		;
		matrix.postRotate(angle);
		System.out.println("angle2=" + angle);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}

	public int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}
}
