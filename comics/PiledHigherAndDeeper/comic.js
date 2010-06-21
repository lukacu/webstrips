/* 
 * A template for JavaScript comic plugin for WebStrips
 * 
 * License:
 * 
 * This program is free software; you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation; either version 2 of the License, or (at 
 * your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General 
 * Public License for more details.
 * 
 * http://www.opensource.org/licenses/gpl-license.php
 * 
 * You should have received a copy of the GNU General Public License along 
 * with this program; if not, write to the Free Software Foundation, 
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */

/* 
 * place any global variables and support functions here ...
 */

var ANCHOR_RE = new RegExp("archive\\_print\\.php\\?comicid=(\\d*)");
	
var NEXT_RE = new RegExp("<a\\ href\\=archive\\.php\\?comicid\\=(\\d*)><img[^>]*next");
	
var PREV_RE = new RegExp("<a\\ href\\=archive\\.php\\?comicid\\=(\\d*)><img[^>]*prev");

var PATTERN_RE = new RegExp("http\\://www\\.phdcomics\\.com/comics/archive/phd([^\\.]*)\\.gif");

/*
 * Function title(id) should return a title for the comic strip with the given id.
 */
function title(id) {

	return id;

}

/*
 * Function image(id) should return a URL of an image for the comic strip with the given id.
 */
function image(id) {
				
	s = get("http://www.phdcomics.com/comics/archive.php?comicid=" + id);
	
	if (s.length == 0) return null;
	
	f = PATTERN_RE.exec(s);

	if (f) {
		return "http://www.phdcomics.com/comics/archive/phd" + f[1] + ".gif";
	} else 
		return null;
		
}

/*
 * Function previous(id) should return the id of a comic strip that is prior to the comic strip
 * with a given id or null if there is no such comic.
 */
function previous(id) {
	s = get("http://www.phdcomics.com/comics/archive.php?comicid=" + id);
	
	p = PREV_RE.exec(s);
	
	if (p) {
		return p[1];
	}
	
	return null;

}

/*
 * Function next(id) should return the id of a comic strip that is next to the comic strip
 * with a given id or null if there is no such comic.
 */
function next(id) {

	s = get("http://www.phdcomics.com/comics/archive.php?comicid=" + id);
	
	p = NEXT_RE.exec(s);
	
	if (p) {
		return p[1];
	}
	
	return null;
}

/*
 * Function first() should return the id of the first comic strip or null if that information
 * cannot be obtained for this comic.
 */
function first() {
	
	return 1;
}

/*
 * Function newest() should return the id of the most recent comic strip or null if that information
 * cannot be obtained for this comic.
 */
function newest() {
	
	s = get("http://www.phdcomics.com/comics.php");
	
	m = ANCHOR_RE.exec(s);

	if (m) {
		return m[1];
	}
	
	return null; 

}

/* 
 * if the information is present you can also implement the following functionality currently 
 * supported by WebStrips (uncoment the functions)
 */

/*
 * Function link(id) should return an url pointing to the page where the comic strip with a
 * given id was retrieved from.
 */

function link(id) {
	return "http://www.phdcomics.com/comics/archive.php?comicid=" + id;
}


