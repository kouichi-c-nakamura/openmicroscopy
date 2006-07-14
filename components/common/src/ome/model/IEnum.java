/*
 * ome.model.IEnum
 *
 *------------------------------------------------------------------------------
 *
 *  Copyright (C) 2005 Open Microscopy Environment
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
package ome.model;

//Java imports

//Third-party libraries

//Application-internal dependencies

/** 
 * interface for all domain enumarations. Mostly a marker interface, but does
 * provide access to the single value associated with this instance (See 
 * {@link #getValue()})
 * 
 * @author  Josh Moore &nbsp;&nbsp;&nbsp;&nbsp;
 *               <a href="mailto:josh.moore@gmx.de">josh.moore@gmx.de</a>
 * @version 3.0
 * <small>
 * (<b>Internal version:</b> $Rev$ $Date$)
 * </small>
 * @since 3.0
 * @author josh
 *
 */
public interface IEnum extends IObject { 
	
    /** the single String value associated with this instance. This value is
     * a surrogate (business) key for this type of enumeration. Equality between
     * two instances for {@link #getValue() value} implies that there will 
     * also be equality for {@link IObject#getId() id}, at least in an 
     * controlled environment.
     */
	public String getValue();

	
}
