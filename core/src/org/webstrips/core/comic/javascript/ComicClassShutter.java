package org.webstrips.core.comic.javascript;

import org.mozilla.javascript.ClassShutter;

class ComicClassShutter implements ClassShutter {

	public boolean visibleToScripts(String fullClassName) {
		return false;
	}

}
