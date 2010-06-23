package org.webstrips.android;

import java.io.File;
import java.io.InputStream;

import org.webstrips.core.WebStrips;
import org.webstrips.core.bundle.ComicBundle;
import org.webstrips.core.comic.ComicException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ComicImportActivity extends Activity {

	private File importBundleFile = null;
	
	private WebStripsService.ComicManagerControl control = null;
	
	private ServiceConnection connection = new ServiceConnection() {
		
        public void onServiceConnected(ComponentName className, IBinder service) {

        	control = (WebStripsService.ComicManagerControl) service;
        	
        }

        public void onServiceDisconnected(ComponentName className) {

            control = null;

        }
    };
	
	
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	setContentView(R.layout.comicimport);
    	
    	Button buttonYes = (Button)findViewById(R.id.import_yes);    	
    	Button buttonNo = (Button)findViewById(R.id.import_no);

    	buttonNo.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				finish();
			}
    		
    	});
    	
    	buttonYes.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				
				if (control != null && importBundleFile != null) {
					if (!control.importComic(importBundleFile)) {
						AlertDialog.Builder dialog = new AlertDialog.Builder(ComicImportActivity.this);
					
						dialog.setTitle(R.string.import_error_title);
						dialog.setMessage(R.string.import_error_body);
						dialog.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								finish();
							}
							
						});
						
						dialog.show();
						
					} else finish();
				}
			}
    		
    	});
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        Intent i = getIntent();
        
        if (i == null) {
        	finish();
        	return;
        }
        
        Uri file = i.getData();
        
        if (file == null || !"file".equalsIgnoreCase(file.getScheme())) {
        	finish();
        	return;
        }
        
    	importBundleFile = new File(file.getPath());
    	
    	if (!importBundleFile.exists() || !importBundleFile.canRead() || !importBundleFile.isFile())  {
        	finish();
        	return;
        }
        
        try {
        	
	        ComicBundle bundle = new ComicBundle(importBundleFile);
	        
	        TextView question = (TextView) findViewById(R.id.import_question); 
	        
	        ImageView image = (ImageView) findViewById(R.id.import_image);
	        
	        question.setText(getString(R.string.import_question, bundle.getDescription().comicName(),
	        		bundle.getDescription().comicAuthor()));
	        
	        bindService(new Intent("org.webstrips.MANAGER", null, this, 
	                WebStripsService.class), connection, 0);
            
	        InputStream imageStream = bundle.imageDataStream();
	        
	        if (imageStream != null) {
	        	Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
	        	if (bitmap != null)
	        		image.setImageBitmap(bitmap);
	        }
	        
	        
        } catch (ComicException e) {
        	WebStrips.getLogger().report(e);
        	finish();
        }
        
    }
    
    @Override
    protected void onPause() {
    	
    	unbindService(connection);
    	
    	
    	super.onPause();
    }
}
