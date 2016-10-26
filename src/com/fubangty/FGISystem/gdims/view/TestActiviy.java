
package com.fubangty.FGISystem.gdims.view;

import java.io.File;

import com.fubangty.FGISystem.R;
import com.fubangty.FGISystem.gdims.base.BaseActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

/**
 * @author 李青松
 * @version 0.1
 * 
 */
public class TestActiviy extends BaseActivity {

	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main); // create new Intent
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		// fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO); // create a file
		// to save the video
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "camera.jpg"))); // set
																																	// the
																																	// image
																																	// file
																																	// name
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); // set the video
															// image quality to
															// high
		// start the Video Capture Intent
		startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) { // Image captured and saved to
											// fileUri specified in the Intent
				Toast.makeText(this, "Image saved to:\n" + data.getData(), Toast.LENGTH_LONG).show();
			} else if (resultCode == RESULT_CANCELED) {
				// User cancelled the image capture
			} else {
				// Image capture failed, advise user
			}

		}
	}
}
