/*
 * org.openmicroscopy.shoola.agents.treeviewer.actions.CreateAction
 *
 *------------------------------------------------------------------------------
 *  Copyright (C) 2006 University of Dundee. All rights reserved.
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

package org.openmicroscopy.shoola.agents.treeviewer.actions;



//Java imports
import java.awt.event.ActionEvent;
import javax.swing.Action;

//Third-party libraries

//Application-internal dependencies
import org.openmicroscopy.shoola.agents.treeviewer.IconManager;
import org.openmicroscopy.shoola.agents.treeviewer.browser.Browser;
import org.openmicroscopy.shoola.agents.treeviewer.browser.TreeImageDisplay;
import org.openmicroscopy.shoola.agents.treeviewer.cmd.CreateCmd;
import org.openmicroscopy.shoola.agents.treeviewer.view.TreeViewer;
import org.openmicroscopy.shoola.util.ui.UIUtilities;
import pojos.CategoryData;
import pojos.CategoryGroupData;
import pojos.DatasetData;
import pojos.ExperimenterData;
import pojos.ProjectData;

/** 
 * Creates a new <code>DataObject</code> of the corresponding type.
 *
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * 				<a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @version 2.2
 * <small>
 * (<b>Internal version:</b> $Revision$ $Date$)
 * </small>
 * @since OME2.2
 */
public class CreateAction
    extends TreeViewerAction
{
    
    /** The default name of the action. */
    private static final String NAME = "New...";
    
    /** The name of the action for the creation of a <code>Dataset</code>. */
    private static final String NAME_DATASET = "New Dataset...";
    
    /** The name of the action for the creation of a <code>Category</code>. */
    private static final String NAME_CATEGORY = "New Category...";
    
    /** The name of the action for the creation of a <code>Image</code>. */
    private static final String NAME_IMAGE = "Import Image...";
    
    /** 
     * Description of the action if the selected node is a <code>Dataset</code>.
     */
    private static final String DESCRIPTION_DATASET = "Create a new dataset " +
    		"and add it to the selected project.";
    
    /** 
     * Description of the action if the selected node is a 
     * <code>Category</code>.
     */
    private static final String DESCRIPTION_CATEGORY = "Create a new category" +
    							" and add it to the selected category group.";
    
    /** 
     * Description of the action if the selected node is a <code>Image</code>.
     */
    private static final String DESCRIPTION_IMAGE = "Import image into the " +
    												"selected dataset.";
    
    /** Default Description of the action. */
    private static final String DESCRIPTION = "Create a new element.";
    
    /** The type of node to create. */
    private int nodeType;
    
    /** 
     * Sets the action enabled dependong on the state of the {@link Browser}.
     * @see TreeViewerAction#onBrowserStateChange(Browser)
     */
    protected void onBrowserStateChange(Browser browser)
    {
        if (browser == null) return;
        switch (browser.getState()) {
	        case Browser.LOADING_DATA:
	        case Browser.LOADING_LEAVES:
	        //case Browser.COUNTING_ITEMS:  
	            setEnabled(false);
	            break;
	        default:
                onDisplayChange(browser.getLastSelectedDisplay());
	            break;
        }
    }
    
    /**
     * Modifies the name of the action and sets it enabled depending on
     * the selected type.
     * @see TreeViewerAction#onDisplayChange(TreeImageDisplay)
     */
    protected void onDisplayChange(TreeImageDisplay selectedDisplay)
    {
        if (selectedDisplay == null) {
            setEnabled(false);
            name = NAME;  
            putValue(Action.SHORT_DESCRIPTION, 
                    UIUtilities.formatToolTipText(DESCRIPTION));
            return;
        }
        Object ho = selectedDisplay.getUserObject();
        if (ho instanceof String || ho instanceof ExperimenterData) { // root
                setEnabled(false);
                name = NAME;  
                putValue(Action.SHORT_DESCRIPTION, 
                        UIUtilities.formatToolTipText(DESCRIPTION));
        } else if (ho instanceof ProjectData) {
            setEnabled(model.isObjectWritable(ho));
            name = NAME_DATASET; 
            nodeType = CreateCmd.DATASET;
           
            putValue(Action.SHORT_DESCRIPTION, 
                    UIUtilities.formatToolTipText(DESCRIPTION_DATASET));
        } else if (ho instanceof CategoryGroupData) {
            setEnabled(model.isObjectWritable(ho));
            name = NAME_CATEGORY;
            putValue(Action.SHORT_DESCRIPTION, 
                    UIUtilities.formatToolTipText(DESCRIPTION_CATEGORY));
            nodeType = CreateCmd.CATEGORY;
        } else if (ho instanceof CategoryData) {
            setEnabled(model.isObjectWritable(ho));
            setEnabled(false); //TODO: remove when import
            name = NAME_IMAGE;
            putValue(Action.SHORT_DESCRIPTION, 
                    UIUtilities.formatToolTipText(DESCRIPTION_IMAGE));
        } else if (ho instanceof DatasetData) {
            setEnabled(model.isObjectWritable(ho));
            setEnabled(false);
            name = NAME_IMAGE;
            putValue(Action.SHORT_DESCRIPTION, 
                    UIUtilities.formatToolTipText(DESCRIPTION_IMAGE));
        } else {
            setEnabled(false);
            name = NAME;
            putValue(Action.SHORT_DESCRIPTION, 
                    UIUtilities.formatToolTipText(DESCRIPTION));
        }
        description = (String) getValue(Action.SHORT_DESCRIPTION);
    }
    
    /**
     * Creates a new instance.
     * 
     * @param model Reference to the Model. Mustn't be <code>null</code>.
     */
    public CreateAction(TreeViewer model)
    {
        super(model);
        name = NAME;  
        putValue(Action.SHORT_DESCRIPTION, 
                UIUtilities.formatToolTipText(DESCRIPTION));
        description = (String) getValue(Action.SHORT_DESCRIPTION);
        IconManager im = IconManager.getInstance();
        putValue(Action.SMALL_ICON, im.getIcon(IconManager.CREATE));
    } 

    /**
     * Creates a {@link CreateCmd} command to execute the action.
     * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
       CreateCmd cmd = new CreateCmd(model, nodeType);
       cmd.execute();
    }

}
