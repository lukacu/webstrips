package org.webstrips.android;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.webstrips.core.StorageManager;

import android.content.Context;

public class AndroidStorageManager extends StorageManager {

	@Override
	public void delete(String resource) throws IOException {
		WebStripsAndroid.getApplication().getApplicationContext().deleteFile(resource);
	}

	@Override
	public InputStream getInputStream(String resource) throws IOException {
		return WebStripsAndroid.getApplication().getApplicationContext().openFileInput(resource);
	}

	@Override
	public OutputStream getOutputStream(String resource) throws IOException {
		return WebStripsAndroid.getApplication().getApplicationContext().openFileOutput(resource, Context.MODE_PRIVATE);
	}

	@Override
	public File getStorageDirectory() {
		return WebStripsAndroid.getApplication().getApplicationContext().getFilesDir();
	}

	@Override
	public File getCacheDirectory() {
		return WebStripsAndroid.getApplication().getApplicationContext().getCacheDir();
	}

}
