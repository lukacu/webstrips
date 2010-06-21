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

var NEWEST_RE = new RegExp("<input type=\"hidden\" name=\"Date\" value=\"(\\d{4})(\\d{2})(\\d{2})\"");

var FIRST_RE = new RegExp("first\"><a href=\"/comic/(\\d{4})/(\\d{1,2})/(\\d{1,2})/\"");
	
var NEXT_RE = new RegExp("next\"><a href=\"/comic/(\\d{4})/(\\d{1,2})/(\\d{1,2})/\"");
	
var PREV_RE = new RegExp("back\"><a href=\"/comic/(\\d{4})/(\\d{1,2})/(\\d{1,2})/\"");

var PATTERN_RE = new RegExp("<div class=\"body\">[\n\t ]+<img src\\=\"([^\"]*)\" +alt\\=\"([^\"]*)\"");

var ARCHIVE_RE = new RegExp("option value=\"http\\://www\\.penny-arcade\\.com/comic/(\\d{4})/(\\d{2})/(\\d{2})\">[\n\t ]+[0-9\\/]+ ([^\n\t<]+)", "g");

var DATE_RE = new RegExp("(\\d\\d\\d\\d)-(\\d{1,2})-(\\d{1,2})");

var newestDate= null;

function parseDate(str) {

	var m = str.match(DATE_RE);

	if (m) {
		d = new Date(m[1] + "/" + m[2] + "/" + m[3] + " 00:00:00");
		d.setMilliseconds(0);
		return d;
	
	} else return new Date();
	
}

/*
 * Function title(id) should return a title for the comic strip with the given id.
 */
function title(id) {

	c = parseDate(id);
	
	s = get(format("http://www.penny-arcade.com/comic/%1$tY/%1$tm/%1$td", c));
	
	p = PATTERN_RE.exec(s);
	
	if (p) {
		return p[2];
	}

	return null;

}

/*
 * Function image(id) should return a URL of an image for the comic strip with the given id.
 */
function image(id) {
				
	c = parseDate(id);
	
	s = get(format("http://www.penny-arcade.com/comic/%1$tY/%1$tm/%1$td", c));
	
	p = PATTERN_RE.exec(s);
	
	if (p) {
		if (p[1].indexOf("http://") == 0)
			return p[1];

		// TODO: I do not know if that still works ... i hope that all urls are absolute now.
		return "http://art.penny-arcade.com" + p[1];

	}

	return null;
	
}

/*
 * Function previous(id) should return the id of a comic strip that is prior to the comic strip
 * with a given id or null if there is no such comic.
 */
function previous(id) {
	c = parseDate(id);
	
	s = get(format("http://www.penny-arcade.com/comic/%1$tY/%1$tm/%1$td", c));
	
	p = PREV_RE.exec(s);
	
	if (p) {
		return format("%1$tY-%1$tm-%1$td", parseDate(p[1] + "-" + p[2] + "-" + p[3]));
	}
	return null;

}

/*
 * Function next(id) should return the id of a comic strip that is next to the comic strip
 * with a given id or null if there is no such comic.
 */
function next(id) {

	// The problem here is that PennyArcade gives an url also for comics that do not exist
	// yet. And without this check that messes up the system.

	if (newestDate == null)
		newest();

	c = parseDate(id);
	if (c - newestDate > -43200000)
		return null;

	s = get(format("http://www.penny-arcade.com/comic/%1$tY/%1$tm/%1$td", c));

	n = NEXT_RE.exec(s);

	if (n) {
		return format("%1$tY-%1$tm-%1$td", parseDate(n[1] + "-" + n[2] + "-" + n[3]));
	}

	return null;
}

/*
 * Function first() should return the id of the first comic strip or null if that information
 * cannot be obtained for this comic.
 */
function first() {
	s = get("http://www.penny-arcade.com/comic");

	m = FIRST_RE.exec(s);

	if (m) {

		return format("%1$tY-%1$tm-%1$td", parseDate(m[1] + "-" + m[2] + "-" + m[3]));
	}
	
	return null;
}

/*
 * Function newest() should return the id of the most recent comic strip or null if that information
 * cannot be obtained for this comic.
 */
function newest() {
	
	s = get("http://www.penny-arcade.com/comic");
	
	m = NEWEST_RE.exec(s);
	
	if (m) {
		newestDate = parseDate(m[1] + "-" + m[2] + "-" + m[3]);
		return format("%1$tY-%1$tm-%1$td", newestDate);
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
	c = parseDate(id);
	
	return format("http://www.penny-arcade.com/comic/%1$tY/%1$tm/%1$td", c);
}


/*
 * Function archive(id) should process the archive data from the comic strip with the given id on
 * and return the possible new comics using archiveInsert(id, title) and archiveAppend(id, title)
 * functions that will only be allowed to call if the archive function is invoked. 
 * 
 * archiveInsert(id, title) - inserts the archive entry at the beginning of the newly generated archive
 * segment (The last call to this function will insert an entry to the start of the new segment). 
 *
 * archiveAppend(id, title) - inserts the archive entry at the end of the newly generated archive
 * segment (The last call to this function will insert an entry to the end of the new segment and thus
 * to the end of archive). 
 */

function archive(id) {

	s = get("http://www.penny-arcade.com/archive");

	ARCHIVE_RE.lastIndex = 0;

	while (m = ARCHIVE_RE.exec(s)) {
		
		j = m[1] + m[2] + m[3];

		archiveInsert(j, m[4]);

		if (j == id)
			break;
	}

}

