
package org.webstrips.core.bundle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.coffeeshop.io.Files;
import org.coffeeshop.io.Streams;

// TODO: javadoc

public class ZipUnpacker {

	private ZipFile zip;
	
	public ZipUnpacker(ZipFile file) {
		if (file == null)
			throw new IllegalArgumentException("Null");
		
		this.zip = file;
		
	}
	
	public boolean extractFile(String file, File toDir, boolean overwrite) throws FileNotFoundException, IOException {
		
		File destFile = Files.join(toDir, file);
		
		if (destFile.exists() && !overwrite)
			return false;
		
		InputStream in = zip.getInputStream(new ZipEntry(file));
		
		if (in == null)
			throw new FileNotFoundException("File not found in archive:" + file);
		
		FileOutputStream out = new FileOutputStream(destFile);
		
		Streams.copyStream(in, out);
		
		in.close();
		out.close();
		return true;
		
	}
	
	

	
	
}
