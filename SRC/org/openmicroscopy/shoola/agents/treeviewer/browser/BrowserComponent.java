/*
 * org.openmicroscopy.shoola.agents.treeviewer.browser.BrowserComponent
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

package org.openmicroscopy.shoola.agents.treeviewer.browser;

//Java imports
import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.tree.DefaultTreeModel;

//Third-party libraries

//Application-internal dependencies
import org.openmicroscopy.shoola.agents.treeviewer.HierarchyLoader;
import org.openmicroscopy.shoola.agents.treeviewer.IconManager;
import org.openmicroscopy.shoola.agents.treeviewer.TreeViewerTranslator;
import org.openmicroscopy.shoola.agents.treeviewer.util.FilterWindow;
import org.openmicroscopy.shoola.util.ui.UIUtilities;
import org.openmicroscopy.shoola.util.ui.component.AbstractComponent;
/** 
 * 
 *
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * 				<a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @version 2.2
 * <small>
 * (<b>Internal version:</b> $Revision$ $Date$)
 * </small>
 * @since OME2.2
 */
class BrowserComponent
    extends AbstractComponent
    implements Browser
{

    /** The Model sub-component. */
    private BrowserModel    model;
    
    /** The View sub-component. */
    private BrowserUI       view;
    
    /** The Controller sub-component. */
    private BrowserControl  controller;
    
    /**
     * Returns the frame hosting the {@link BrowserUI view}.
     * 
     * @param c The parent container.
     * @return See above.
     */
    private JFrame getViewParent(Container c)
    {
        if (c instanceof JFrame) return (JFrame) c;
        return getViewParent(c.getParent());
    }
    
    /**
     * Creates a new instance.
     * The {@link #initialize() initialize} method should be called straight 
     * after to complete the MVC set up.
     * 
     * @param model The Model sub-component.
     */
    BrowserComponent(BrowserModel model)
    {
        if (model == null) throw new NullPointerException("No model.");
        this.model = model;
        controller = new BrowserControl(this);
        view = new BrowserUI();
    }
    
    /** Links up the MVC triad. */
    void initialize()
    {
        model.initialize(this);
        controller.initialize(view);
        view.initialize(controller, model);
    }
    
    /**
     * Returns the Model sub-component.
     * 
     * @return See above.
     */
    BrowserModel getModel() { return model; }
    
    /**
     * Implemented as specified by the {@link Browser} interface.
     * @see Browser#getState()
     */
    public int getState() { return model.getState(); }

    /**
     * Implemented as specified by the {@link Browser} interface.
     * @see Browser#activate()
     */
    public void activate()
    {
        int state = model.getState();
        switch (state) {
            case NEW:
                
                break;
            case DISCARDED:
                throw new IllegalStateException(
                        "This method can't be invoked in the DISCARDED state.");
            default:
                break;
        }
    }

    /**
     * Implemented as specified by the {@link Browser} interface.
     * @see Browser#discard()
     */
    public void discard()
    {
        if (model.getState() != DISCARDED) {
            model.discard();
            fireStateChange();
        }
    }

    /**
     * Implemented as specified by the {@link Browser} interface.
     * @see Browser#getUI()
     */
    public JComponent getUI()
    { 
        if (model.getState() == DISCARDED)
            throw new IllegalStateException(
                    "This method cannot be invoked in the DISCARDED state.");
        return view;
    }

    /**
     * Implemented as specified by the {@link Browser} interface.
     * @see Browser#setNodes(Set)
     */
    public void setNodes(Set nodes)
    {
        if (model.getState() != LOADING_DATA)
            throw new IllegalStateException(
                    "This method can only be invoked in the LOADING_DATA "+
                    "state.");
        if (nodes == null) throw new NullPointerException("No nodes.");
        Set visNodes = TreeViewerTranslator.transformHierarchy(nodes);
        view.setViews(visNodes);
        model.setState(READY);
        fireStateChange();
    }

    /**
     * Implemented as specified by the {@link Browser} interface.
     * @see Browser#getBrowserType()
     */
    public int getBrowserType() { return model.getBrowserType(); }

    /**
     * Implemented as specified by the {@link Browser} interface.
     * @see Browser#cancel()
     */
    public void cancel()
    { 
        int state = model.getState();
        if ((state == LOADING_DATA) || (state == LOADING_LEAVES)) { 
            if (state == LOADING_DATA) view.cancelDataLoading();
            else if (state == LOADING_LEAVES) view.cancelLeavesLoading();
            model.cancel();
            fireStateChange();
        }
    }

    /**
     * Implemented as specified by the {@link Browser} interface.
     * @see Browser#setStatus(boolean)
     */
    public void setStatus(boolean done)
    {
        /*
        int state = model.getState();
        if ((state == LOADING_DATA) || (state == LOADING_LEAVES)) {
            if (done) 
                firePropertyChange(ON_END_LOADING_PROPERTY, Boolean.FALSE,
                                Boolean.TRUE);
        }
        */
    }
    
    /**
     * Implemented as specified by the {@link Browser} interface.
     * @see Browser#loadData()
     */
    public void loadData()
    {
        int state = model.getState();
        if ((state == DISCARDED) || (state == LOADING_LEAVES))
            throw new IllegalStateException(
                    "This method cannot be invoked in the DISCARDED or" +
                    "LOADING_LEAVES state.");
        model.fireDataLoading();
        fireStateChange();
    }

    /**
     * Implemented as specified by the {@link Browser} interface.
     * @see Browser#loadData()
     */
    public void loadLeaves()
    {
        int state = model.getState();
        if ((state == DISCARDED) || (state == LOADING_DATA))
            throw new IllegalStateException(
                    "This method cannot be invoked in the DISCARDED or" +
                    "LOADING_LEAVES state.");
        model.fireLeavesLoading();
        fireStateChange();
    }

    /**
     * Implemented as specified by the {@link Browser} interface.
     * @see Browser#setLeaves(Set)
     */
    public void setLeaves(Set leaves)
    {
        if (model.getState() != LOADING_LEAVES)
            throw new IllegalStateException(
                    "This method can only be invoked in the LOADING_LEAVES "+
                    "state.");
        if (leaves == null) throw new NullPointerException("No leaves.");
        Set visLeaves = TreeViewerTranslator.transformHierarchy(leaves);
        view.setLeavesViews(visLeaves);
        model.setState(READY);
        fireStateChange();
    }

    /**
     * Implemented as specified by the {@link Browser} interface.
     * @see Browser#setLeaves(Set)
     */
    public void setSelectedDisplay(TreeImageDisplay display)
    {
        int state = model.getState();
        switch (state) {
            case LOADING_DATA:
            case LOADING_LEAVES:
            case DISCARDED:
                throw new IllegalStateException(
                        "This method cannot be invoked in the LOADING_DATA, "+
                        " LOADING_LEAVES or DISCARDED state.");
        }
        TreeImageDisplay oldDisplay = model.getSelectedDisplay();
        if (oldDisplay != null && oldDisplay.equals(display)) return;
        model.setSelectedDisplay(display);
        firePropertyChange(SELECTED_DISPLAY_PROPERTY, oldDisplay, display);
    }

    /**
     * Implemented as specified by the {@link Browser} interface.
     * @see Browser#showPopupMenu()
     */
    public void showPopupMenu()
    {
        int state = model.getState();
        switch (state) {
            case LOADING_DATA:
            case LOADING_LEAVES:
            case DISCARDED:
                throw new IllegalStateException(
                        "This method can only be invoked in the LOADING_DATA, "+
                        " LOADING_LEAVES or DISCARDED state.");
        }
        firePropertyChange(POPUP_MENU_PROPERTY, null, view.getTreeDisplay());
    }

    /**
     * Implemented as specified by the {@link Browser} interface.
     * @see Browser#getClickPoint()
     */
    public Point getClickPoint() { return model.getClickPoint(); }

    /**
     * Implemented as specified by the {@link Browser} interface.
     * @see Browser#getSelectedDisplay()
     */
    public TreeImageDisplay getSelectedDisplay()
    {
        return model.getSelectedDisplay();
    }

    /**
     * Implemented as specified by the {@link Browser} interface.
     * @see Browser#close()
     */
    public void close()
    {
        switch (model.getState()) {
            case LOADING_DATA:
            case LOADING_LEAVES:
            case DISCARDED:
                throw new IllegalStateException(
                        "This method can only be invoked in the LOADING_DATA, "+
                        " LOADING_LEAVES or DISCARDED state.");
        }
        firePropertyChange(CLOSE_PROPERTY, null, this);
    }

    /**
     * Implemented as specified by the {@link Browser} interface.
     * @see Browser#collapse(TreeImageDisplay)
     */
    public void collapse(TreeImageDisplay node)
    {
        if (node == null) return;
        view.collapse(node);
    }

    /**
     * Implemented as specified by the {@link Browser} interface.
     * @see Browser#accept(TreeImageDisplayVisitor)
     */
    public void accept(TreeImageDisplayVisitor visitor)
    {
        DefaultTreeModel model = (DefaultTreeModel) 
                                    view.getTreeDisplay().getModel();
        TreeImageDisplay root = (TreeImageDisplay) model.getRoot();
        root.accept(visitor);
    }

    /**
     * Implemented as specified by the {@link Browser} interface.
     * @see Browser#accept(TreeImageDisplayVisitor, int)
     */
    public void accept(TreeImageDisplayVisitor visitor, int algoType)
    {
        DefaultTreeModel model = (DefaultTreeModel) 
        view.getTreeDisplay().getModel();
        TreeImageDisplay root = (TreeImageDisplay) model.getRoot();
        root.accept(visitor, algoType);
    }

    /**
     * Implemented as specified by the {@link Browser} interface.
     * @see Browser#getTitle()
     */
    public String getTitle() { return view.getBrowserTitle(); }

    /**
     * Implemented as specified by the {@link Browser} interface.
     * @see Browser#getIcon()
     */
    public Icon getIcon()
    {
        IconManager im = IconManager.getInstance();
        switch (model.getBrowserType()) {
            case HIERARCHY_EXPLORER:
                return im.getIcon(IconManager.HIERARCHY_EXPLORER);
            case CATEGORY_EXPLORER:
                return im.getIcon(IconManager.CATEGORY_EXPLORER);
            case IMAGES_EXPLORER:
                return im.getIcon(IconManager.IMAGES_EXPLORER);
        }
        return null;
    }

    /**
     * Implemented as specified by the {@link Browser} interface.
     * @see Browser#getIcon()
     */
    public void setSortedNodes(List nodes)
    {
        switch (model.getState()) {
            case LOADING_DATA:
            case LOADING_LEAVES:
            case DISCARDED:
                throw new IllegalStateException(
                        "This method cannot be invoked in the LOADING_DATA, "+
                        " LOADING_LEAVES or DISCARDED state.");
        }
        view.setSortedNodes(nodes);
    }

    /**
     * Implemented as specified by the {@link Browser} interface.
     * @see Browser#setFilterNodes(Set, int)
     */
    public void setFilterNodes(Set nodes, int type)
    {
        if (model.getState() != LOADING_DATA)
            throw new IllegalStateException(
                    "This method can only be invoked in the LOADING_DATA"+
                    "state.");
        if (nodes == null) throw new NullPointerException("No nodes.");
        int index = -1;
        if (type == HierarchyLoader.DATASET) index = FilterWindow.DATASET;
        else if (type == HierarchyLoader.CATEGORY) 
            index = FilterWindow.CATEGORY;
        if (index == -1) throw new IllegalStateException("Index not valid.");
        model.setFilterType(type);
        model.setState(READY);
        fireStateChange();
        JFrame frame = getViewParent(view.getParent());
        FilterWindow window = new FilterWindow(frame, index,nodes);
        window.addPropertyChangeListener(FILTER_NODES_PROPERTY, controller);
        UIUtilities.centerAndShow(window);
    }

    /**
     * Implemented as specified by the {@link Browser} interface.
     * @see Browser#loadFilterData(int)
     */
    public void loadFilterData(int type)
    {
        switch (model.getState()) {
            case LOADING_DATA:
            case LOADING_LEAVES:
            case DISCARDED:
                throw new IllegalStateException(
                        "This method can only be invoked in the LOADING_DATA, "+
                        " LOADING_LEAVES or DISCARDED state.");
        }
        if (model.getBrowserType() != Browser.IMAGES_EXPLORER) 
            throw new IllegalStateException(
                    "This method can only be invoked in the Image Explorer.");
        model.fireFilterDataLoading(type);
        fireStateChange();
    }

    /**
     * Implemented as specified by the {@link Browser} interface.
     * @see Browser#showFilterMenu(Component, Point)
     */
    public void showFilterMenu(Component c, Point p)
    {
        view.showFilterMenu(c, p);
    }

    /**
     * Implemented as specified by the {@link Browser} interface.
     * @see Browser#loadData(Set)
     */
    public void loadData(Set nodeIDs)
    {
        int state = model.getState();
        if ((state == DISCARDED) || (state == LOADING_LEAVES))
            throw new IllegalStateException(
                    "This method cannot be invoked in the DISCARDED or" +
                    "LOADING_LEAVES state.");
        if (nodeIDs == null || nodeIDs.size() == 0) return;
        model.fireDataLoading(nodeIDs);
        fireStateChange();
    }

    /**
     * Implemented as specified by the {@link Browser} interface.
     * @see Browser#refresh()
     */
    public void refresh()
    {
        switch (model.getState()) {
            case LOADING_DATA:
            case LOADING_LEAVES:
            case DISCARDED:
                throw new IllegalStateException(
                        "This method cannot be invoked in the LOADING_DATA, "+
                        " LOADING_LEAVES or DISCARDED state.");
        }
        TreeImageDisplay display = model.getSelectedDisplay();
        if (display == null) return;
        DefaultTreeModel dtm = (DefaultTreeModel) 
                                view.getTreeDisplay().getModel();
        TreeImageDisplay root = (TreeImageDisplay) dtm.getRoot();
        display.removeAllChildrenDisplay();
        view.loadAction(display);
        if (root.equals(display)) model.fireDataLoading();
        else model.refreshSelectedDisplay();
        fireStateChange();
    }

    /**
     * Implemented as specified by the {@link Browser} interface.
     * @see Browser#setContainerNodes(Set)
     */
    public void setContainerNodes(Set nodes)
    {
        int state = model.getState();
        System.out.println(state);
        if (state != LOADING_DATA && state != LOADING_LEAVES)
            throw new IllegalStateException(
                    "This method can only be invoked in the LOADING_DATA or "+
                    " LOADING_LEAVES state.");
        if (nodes == null) throw new NullPointerException("No nodes.");
        Set visNodes = TreeViewerTranslator.transformContainers(nodes);
        view.setViews(visNodes, model.getSelectedDisplay());
        model.setState(READY);
        fireStateChange();
    }

    /**
     * Implemented as specified by the {@link Browser} interface.
     * @see Browser#setCreatedNode(TreeImageDisplay)
     */
    public void setCreatedNode(TreeImageDisplay display)
    {
        if (display == null) 
            throw new IllegalArgumentException("No node");
        view.setCreatedNode(display, model.getSelectedDisplay());
    }

    /**
     * Implemented as specified by the {@link Browser} interface.
     * @see Browser#deleteNodes()
     */
    public void deleteNodes()
    {
        // TODO Auto-generated method stub
        
    }
    
}
