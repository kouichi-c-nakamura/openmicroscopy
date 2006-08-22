/*
 * org.openmicroscopy.shoola.util.ui.TableHeaderTextAndIcon
 *
 *------------------------------------------------------------------------------
 *
 *  Copyright (C) 2004 Open Microscopy Environment
 *      Massachusetts Institute of Technology,
 *      National Institutes of Health,
 *      University of Dundee
 *
 *
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *------------------------------------------------------------------------------
 */

package org.openmicroscopy.shoola.util.ui.table;



//Java imports
import javax.swing.Icon;

//Third-party libraries

//Application-internal dependencies

/** 
 * 
 *
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * 				<a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author  <br>Andrea Falconi &nbsp;&nbsp;&nbsp;&nbsp;
 * 				<a href="mailto:a.falconi@dundee.ac.uk">
 * 					a.falconi@dundee.ac.uk</a>
 * @version 2.2 
 * <small>
 * (<b>Internal version:</b> $Revision$ $Date$)
 * </small>
 * @since OME2.2
 */
public class TableHeaderTextAndIcon
{

	/** Text of the column in the header. */
	private String		text;
	
	/** Tooltip of the column. */
	private String		toolTipTxt;
	
	/** Icon displayed in the header. */
	private Icon		iconUp, iconDown;
	
	/** control is ascending or descending sorting. */
	private boolean 	ascending;
	
	public TableHeaderTextAndIcon(String text, Icon iconUp, Icon iconDown, 
									String toolTipTxt)
	{
            this.text = text;
            this.iconUp = iconUp;
			this.iconDown = iconDown;
            this.toolTipTxt = toolTipTxt;
            ascending = false;
	}

	public Icon getIconDown() {	return iconDown; }
	
	public Icon getIconUp() {	return iconUp; }

	public String getText() { return text; }
	
	public String getToolTipTxt() { return toolTipTxt; }


	public boolean isAscending() { return ascending; }

	public void setAscending(boolean ascending) { this.ascending = ascending; }

}
