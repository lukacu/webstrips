/* 
 * Garfield comic engine for WebStrips

 * comic webpage: http://www.garfield.com/
 * 
 * author: Luka Cehovin
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

var FIRST_DATE = new Date("1978/06/19");

// length of day in miliseconds
var DAY = 1000 * 60 * 60 * 24;

function parseDate(str) {

	var m = str.match(/(\d\d)(\d\d)(\d\d)/);
	if (m) {

		return new Date((( (m[1] > 70) ? 1900 : 2000) + parseInt(m[1])) + "/" + m[2] + "/" + m[3]);
	
	} else return FIRST_DATE;
	
}

function title(cs) {
	
	return formatDate(parseDate(cs));
}

function image(cs) {
		
	var c = parseDate(cs);
		
	if (c == null) 
		return null;
						
	return format("http://images.ucomics.com/comics/ga/%1$tY/ga%1$ty%1$tm%1$td.gif", c);
		
}

function previous(cs) {
	var c = parseDate(cs);

	if (c == null) 
		return null;
	
	if (c.getTime() > FIRST_DATE.getTime()) {
		c.setTime(c.getTime() - DAY);
		return format("%1$ty%1$tm%1$td", c);	
	}
	
	return null;
}

function next(cs) {
	var c = parseDate(cs);

	if (c == null) return null;

	if (c.getTime() < (new Date()).getTime()) {
		c.setTime(c.getTime() + DAY);

		var s = format("http://images.ucomics.com/comics/ga/%1$tY/ga%1$ty%1$tm%1$td.gif", c);
		
		if (!exists(s)) 
			return null;
		
		return format("%1$ty%1$tm%1$td", c);	
	}
	
	return null;
}

function first() {
	return format("%1$ty%1$tm%1$td", FIRST_DATE);
}

function newest() {
	
	var c = new Date();

	var s = format("http://images.ucomics.com/comics/ga/%1$tY/ga%1$ty%1$tm%1$td.gif", c);
	
	if (!exists(s)) 
		c.setTime(c.getTime() - DAY);

	return format("%1$ty%1$tm%1$td", c);
}
