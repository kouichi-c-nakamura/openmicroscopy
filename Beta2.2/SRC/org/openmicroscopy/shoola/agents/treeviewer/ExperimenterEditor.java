/*
 * org.openmicroscopy.shoola.agents.treeviewer.ExperimenterEditor 
 *
 *------------------------------------------------------------------------------
 *  Copyright (C) 2006-2007 University of Dundee. All rights reserved.
 *
 *
 * 	This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *------------------------------------------------------------------------------
 */
package org.openmicroscopy.shoola.agents.treeviewer;



//Java imports

//Third-party libraries

//Application-internal dependencies
import org.openmicroscopy.shoola.agents.treeviewer.profile.ProfileEditor;
import org.openmicroscopy.shoola.env.data.views.CallHandle;
import pojos.ExperimenterData;

/** 
 * Edits the logged-in user.
 * This class calls the <code>updateExperimenter</code> in the
 * <code>DataManagerView</code>.
 *
 *
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author Donald MacDonald &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:donald@lifesci.dundee.ac.uk">donald@lifesci.dundee.ac.uk</a>
 * @version 3.0
 * <small>
 * (<b>Internal version:</b> $Revision: $Date: $)
 * </small>
 * @since OME3.0
 */
public class ExperimenterEditor 
	extends ProfileEditorLoader
{

    /** Handle to the async call so that we can cancel it. */
    private CallHandle  		handle;
    
    /** The experimenter to update. */
    private ExperimenterData 	exp;
    
    /**
     * Creates a new instance.
     * 
     * @param viewer	The viewer this data loader is for.
     *                  Mustn't be <code>null</code>.
     * @param exp The experimenter data object to update.
     */
	public ExperimenterEditor(ProfileEditor viewer, ExperimenterData exp)
	{
		super(viewer);
		if (exp == null)
			throw new IllegalArgumentException("No experimenter to update.");
		this.exp = exp;
	}

    /**
     * Updates the experimenter.
     * @see ProfileEditorLoader#load()
     */
	public void load()
	{
		handle = dmView.updateExperimenter(exp, this);
	}
	
    /**
     * Cancels the ongoing data retrieval.
     * @see ProfileEditorLoader#cancel()
     */
    public void cancel() { handle.cancel(); }
    
    /** 
     * Feeds the result back to the viewer. 
     * @see ProfileEditorLoader#handleResult(Object)
     */
    public void handleResult(Object result)
    {
        if (viewer.getState() == ProfileEditor.DISCARDED) return;  //Async cancel.
        viewer.experimenterChanged((ExperimenterData) result);
    }
    
}
