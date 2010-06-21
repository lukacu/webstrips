package org.webstrips.core.comic.javascript;

import java.util.Vector;

import org.webstrips.core.WebStrips;

public class JavaScriptManager {

	private static JavaScriptManager manager;
	
	public static JavaScriptManager getManager() {
		
		if (manager == null)
			manager = new JavaScriptManager();
		
		return manager;
		
	}
	
	private Vector<JavaScriptManagerListener> listeners = new Vector<JavaScriptManagerListener>();
	
	public synchronized void addListener(JavaScriptManagerListener l) {
		
		if (l == null)
			return;
		
		if (listeners.contains(l))
			return;
		
		listeners.add(l);
		
	}
	
	public synchronized void removeListener(JavaScriptManagerListener l) {

		listeners.remove(l);
		
	}
	
	private synchronized void fireScriptEvent(JavaScriptEvent e) {
		
		for (JavaScriptManagerListener l : listeners) {
			try {
				l.scriptEvent(e);
			} catch (Exception ex) {
				WebStrips.getLogger().report(ex);
			}
		}
		
	}
	
	public void reportScriptEvent(JavaScriptEvent e) {
		fireScriptEvent(e);
	}


}
