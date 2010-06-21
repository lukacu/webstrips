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

var NEWEST_RE = new RegExp("http\\://www\\.girlgeniusonline\\.com/ggmain/strips/ggmain(\\d{8})\\.jpg");

var FIRST_RE = new RegExp("<a href\\=(?:\"|')http\\://www\\.girlgeniusonline\\.com/comic\\.php\\?date\\=(\\d{8})(?:\"|') *><img[^>]*first");

var PREV_RE = new RegExp("<a href\\=(?:\"|')http\\://www\\.girlgeniusonline\\.com/comic\\.php\\?date\\=(\\d{8})(?:\"|') *><img[^>]*prev");

var NEXT_RE = new RegExp("<a href\\=(?:\"|')http\\://www\\.girlgeniusonline\\.com/comic\\.php\\?date\\=(\\d{8})(?:\"|') *><img[^>]*next");

// BUGFIX: quick and dirty solution to redirect pages bug 
function getHtml(url) {
	
	for (i = 0; i < 10; i++) {
		
		s = get(url);
		
		if (s == null)
			return null;
		
		if (s.length < 200) {
			sleep(200);
		}
		return s;
	}
	return null;
}

function parseDate(str) {

	var m = str.match(/(\d\d\d\d)(\d\d)(\d\d)/);
	if (m) {

		return new Date(m[1] + "/" + m[2] + "/" + m[3]);
	
	} else return new Date();
	
}

/*
 * Function title(id) should return a title for the comic strip with the given id.
 */
function title(id) {

	return formatDate(parseDate(id));

}

/*
 * Function image(id) should return a URL of an image for the comic strip with the given id.
 */
function image(id) {

	return "http://www.girlgeniusonline.com/ggmain/strips/ggmain" + id + ".jpg";
	
}

/*
 * Function previous(id) should return the id of a comic strip that is prior to the comic strip
 * with a given id or null if there is no such comic.
 */
function previous(id) {

	page = "http://www.girlgeniusonline.com/comic.php?date=" + id;

	s = getHtml(page);

	m = PREV_RE.exec(s);

	if (m) {

		return m[1];

	}
	return null;

}

/*
 * Function next(id) should return the id of a comic strip that is next to the comic strip
 * with a given id or null if there is no such comic.
 */
function next(id) {

	page = "http://www.girlgeniusonline.com/comic.php?date=" + id;

	s = getHtml(page);

	m = NEXT_RE.exec(s);

	if (m) {
		return m[1];

	}
	return null;
}

/*
 * Function first() should return the id of the first comic strip or null if that information
 * cannot be obtained for this comic.
 */
function first() {

		s = getHtml("http://www.girlgeniusonline.com/comic.php");
		
		m = FIRST_RE.exec(s);
		
		if (m) {

			return m[1];
		}
		
		return null;
}

/*
 * Function newest() should return the id of the most recent comic strip or null if that information
 * cannot be obtained for this comic.
 */
function newest() {
	
	s = getHtml("http://www.girlgeniusonline.com/comic.php");

	m = NEWEST_RE.exec(s);

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
	return "http://www.girlgeniusonline.com/comic.php?date=" + id;
}


