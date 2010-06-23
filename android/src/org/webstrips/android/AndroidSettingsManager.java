package org.webstrips.android;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.coffeeshop.settings.AbstractSettings;
import org.webstrips.core.SettingsManager;

import android.content.Context;
import android.content.SharedPreferences;

public class AndroidSettingsManager extends AbstractSettings implements SettingsManager {

	private SharedPreferences storage;

	private Map<String, String> cache = new HashMap<String, String>();
	
	private boolean modified;
	
	public AndroidSettingsManager() {
		super(null);

		this.storage = WebStripsAndroid.getApplication().getSharedPreferences("webstrips", Context.MODE_PRIVATE);
		
	}

	@Override
	protected String setProperty(String key, String value) {

		modified = true;
		cache.put(key, value);
		
		return null;
	}

	@Override
	protected String getProperty(String key) {

		String v = cache.get(key);
		
		if (v == null) {
			v = storage.getString(key, null);
			cache.put(key, v);
		}
		
		return v;
	}

	public boolean isModified() {
		return modified;
	}

	public void remove(String key) {
		cache.remove(key);
	}

	public void touch() {
		modified = true;
	}

	public Set<String> getKeys() {

		return cache.keySet();
	}

	public void commit() {
		
		SharedPreferences.Editor editor = storage.edit();
		
		for (String key : cache.keySet()) {
			editor.putString(key, cache.get(key));
		}
		
		editor.commit();
	}
	
}
