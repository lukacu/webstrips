package org.webstrips.android;

import java.io.File;

import org.webstrips.core.Comic;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.ImageView;
import android.widget.TextView;

public class ComicDetailsActivity extends Activity {

	private WebStripsService.ComicControl control = null;

	private ServiceConnection connection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder service) {

			control = (WebStripsService.ComicControl) service;

			if (control == null) {
				finish();
				return;
			}

			Comic comic = control.getComic();

			if (comic == null) {
				finish();
				return;
			}

			ImageView image = (ImageView) findViewById(R.id.details_image);
			
			TextView name = (TextView) findViewById(R.id.details_name);
			TextView author = (TextView) findViewById(R.id.details_author);
			TextView homepage = (TextView) findViewById(R.id.details_homepage);
			TextView description = (TextView) findViewById(R.id.details_description);
			TextView engine = (TextView) findViewById(R.id.details_engine);

			name.setText(comic.getDescription().comicName());
			author.setText(getString(R.string.details_author, comic
					.getDescription().comicAuthor()));
			homepage.setText(getString(R.string.details_homepage, comic
					.getDescription().comicHomepage()));
			description.setText(comic.getDescription().comicDescription());
			engine.setText(getString(R.string.details_engine, comic
					.getDescription().engineAuthor(), comic.getDescription()
					.engineMajorVersion(), comic.getDescription()
					.engineMinorVersion()));

			Bitmap bitmap = imageForComic(comic);
			
			if (bitmap != null)
				image.setImageBitmap(bitmap);
			else image.setImageResource(R.drawable.noimage);
			
		}

		public void onServiceDisconnected(ComponentName className) {

			control = null;

		}
	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.comicdetails);

	}

	@Override
	protected void onResume() {
		super.onResume();
		Intent i = getIntent();

		bindService(new Intent("org.webstrips.COMIC", i.getData(),
				ComicDetailsActivity.this, WebStripsService.class), connection,
				0);

	}

	@Override
	protected void onPause() {

		unbindService(connection);

		super.onPause();
	}
	
	private Bitmap imageForComic(Comic comic) {

		File file = comic.getImageFile();

		if (file == null)
			return null;

		return BitmapFactory.decodeFile(file.toString());

	}
}
