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


var PATTERN_RE = new RegExp("<a href=\"/cartoons/\\?id\\=(\\d{8})\"><img.*src\\=\"(http://www\\.userfriendly\\.org/cartoons/archives/[^\"]*)\"\\ ");

var ANCHOR_RE = new RegExp("http://ars\\.userfriendly\\.org/cartoons/\\?id\\=(\\d{8})\"><IMG ALT=\"Latest Strip\"");

var PREV_RE = new RegExp("<area[^>]*href=\"/cartoons/\\?id\\=(\\d{8})[^\"]*\" coords=\"[0-9, ]+\" alt=\"Prev[^>]*>");

var NEXT_RE = new RegExp("<area[^>]*href=\"/cartoons/\\?id\\=(\\d{8})[^\"]*\" coords=\"[0-9, ]+\" alt=\"\"[^>]*>");

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
				
	page = "http://ars.userfriendly.org/cartoons/?id=" + id + "&mode=classic";

	s = get(page);

	m = PATTERN_RE.exec(s);

	if (m)
		return m[2];

	return null;
	
}

/*
 * Function previous(id) should return the id of a comic strip that is prior to the comic strip
 * with a given id or null if there is no such comic.
 */
function previous(id) {

	page = "http://ars.userfriendly.org/cartoons/?id=" + id + "&mode=classic";

	s = get(page);

	m = PREV_RE.exec(s);

	if (m) {
		if (m[1] != id) {
			return m[1];
		}
	}
	return null;

}

/*
 * Function next(id) should return the id of a comic strip that is next to the comic strip
 * with a given id or null if there is no such comic.
 */
function next(id) {

	page = "http://ars.userfriendly.org/cartoons/?id=" + id	+ "&mode=classic";

	s = get(page);

	m = NEXT_RE.exec(s);

	if (m) {
		if (m[1] != id){
			return m[1];
		}
	}
	return null;
}

/*
 * Function first() should return the id of the first comic strip or null if that information
 * cannot be obtained for this comic.
 */
function first() {

	return null;

}

/*
 * Function newest() should return the id of the most recent comic strip or null if that information
 * cannot be obtained for this comic.
 */
function newest() {
	
	s = get("http://www.userfriendly.org");

	m = ANCHOR_RE.exec(s);

	if (m) {
		newestId = m[1];
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
	return  "http://ars.userfriendly.org/cartoons/?id=" + id + "&mode=classic";
}


