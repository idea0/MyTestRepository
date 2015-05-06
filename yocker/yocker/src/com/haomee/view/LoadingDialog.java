package com.haomee.view;

import com.haomee.yocker.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;


public class LoadingDialog extends Dialog {

	private ImageView dialog_img;
	private String text;
	private Animation animation;
	private TextView dialog_txt;

	public LoadingDialog(Context context) {
		super(context, R.style.loading_dialog);
	}

	public LoadingDialog(Context context, int theme) {
		super(context, theme);
	}

	protected LoadingDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.dialog_loading);
		// LayoutInflater inflater = LayoutInflater.from(getContext());
		// View v = inflater.inflate(R.layout.dialog_loading, null);// 得到加载view
		// LinearLayout layout = (LinearLayout)
		// v.findViewById(R.id.dialog_view);// 加载布局
		dialog_img = (ImageView) this.findViewById(R.id.dialog_img);
		// 加载动画
		animation = AnimationUtils.loadAnimation(getContext(),
				R.anim.rotate_loading);

		dialog_txt = (TextView) this.findViewById(R.id.dialog_txt);// 提示文字
		if (text != null) {
			dialog_txt.setText(text);
		}

		// loadingDialog = new Dialog(context, R.style.loading_dialog);//
		// 创建自定义样式dialog

		/*
		 * this.setCancelable(false);// 不可以用“返回键”取消 this.setContentView(layout,
		 * new LinearLayout.LayoutParams(
		 * LinearLayout.LayoutParams.MATCH_PARENT,
		 * LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
		 * 
		 * setContentView(view);
		 */
	}

	// 设置加载信息
	public void setDialogText(String text) {
		this.text = text;
		if (dialog_txt != null) {
			dialog_txt.setText(text);
		}

	}

	@Override
	public void show() {
		try {
			super.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
		dialog_img.startAnimation(animation);
	}

	@Override
	public void cancel() {
		super.cancel();
		if (animation != null) {
			animation.cancel();
		}

	}

	@Override
	public void dismiss() {

		// Activity被销毁了，再调用dismiss就会报错，不处理
		try {
			super.dismiss();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}