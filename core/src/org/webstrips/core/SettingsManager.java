package org.webstrips.core;

import java.io.IOException;

import org.coffeeshop.settings.ReadableSettings;
import org.coffeeshop.settings.WriteableSettings;

public interface SettingsManager extends ReadableSettings, WriteableSettings {

	public abstract void commit() throws IOException;
	
}
