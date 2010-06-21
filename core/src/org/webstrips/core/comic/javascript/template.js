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

/*
 * Function title(id) should return a title for the comic strip with the given id.
 */
function title(id) {

	return "";

}

/*
 * Function image(id) should return a URL of an image for the comic strip with the given id.
 */
function image(id) {
				
	return "";	
	
}

/*
 * Function previous(id) should return the id of a comic strip that is prior to the comic strip
 * with a given id or null if there is no such comic.
 */
function previous(id) {

	return null;

}

/*
 * Function next(id) should return the id of a comic strip that is next to the comic strip
 * with a given id or null if there is no such comic.
 */
function next(id) {

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
/*
function link(id) {
	
}
*/

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
/*
function archive(id) {
	
}
*/
