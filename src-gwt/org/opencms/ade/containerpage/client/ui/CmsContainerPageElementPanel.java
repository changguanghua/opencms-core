/*
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (c) Alkacon Software GmbH (http://www.alkacon.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * For further information about Alkacon Software, please see the
 * company website: http://www.alkacon.com
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.opencms.ade.containerpage.client.ui;

import com.alkacon.acacia.client.I_InlineFormParent;
import com.alkacon.acacia.client.widgets.I_EditWidget;

import org.opencms.ade.containerpage.client.CmsContainerpageController;
import org.opencms.ade.containerpage.client.ui.css.I_CmsLayoutBundle;
import org.opencms.ade.containerpage.shared.CmsInheritanceInfo;
import org.opencms.ade.contenteditor.client.CmsContentEditor;
import org.opencms.gwt.client.dnd.I_CmsDraggable;
import org.opencms.gwt.client.dnd.I_CmsDropTarget;
import org.opencms.gwt.client.ui.CmsHighlightingBorder;
import org.opencms.gwt.client.util.CmsDomUtil;
import org.opencms.gwt.client.util.CmsDomUtil.Tag;
import org.opencms.gwt.client.util.CmsPositionBean;
import org.opencms.util.CmsStringUtil;
import org.opencms.util.CmsUUID;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Content element within a container-page.<p>
 * 
 * @since 8.0.0
 */
public class CmsContainerPageElementPanel extends AbsolutePanel
implements I_CmsDraggable, HasClickHandlers, I_InlineFormParent {

    /** The height necessary for a container page element. */
    public static int NECESSARY_HEIGHT = 24;

    /** Highlighting border for this element. */
    protected CmsHighlightingBorder m_highlighting;

    /** A flag which indicates whether the height has already been checked. */
    private boolean m_checkedHeight;

    /** Flag indicating the the editables are currently being checked. */
    private boolean m_checkingEditables;

    /** The elements client id. */
    private String m_clientId;

    /** The direct edit bar instances. */
    private Map<Element, CmsListCollectorEditor> m_editables;

    /** The editor click handler registration. */
    private HandlerRegistration m_editorClickHandlerRegistration;

    /** The option bar, holding optional function buttons. */
    private CmsElementOptionBar m_elementOptionBar;

    /** The overlay for expired elements. */
    private Element m_expiredOverlay;

    /** Indicates whether this element has settings to edit. */
    private boolean m_hasSettings;

    /** The inheritance info for this element. */
    private CmsInheritanceInfo m_inheritanceInfo;

    /** The is new element type. */
    private String m_newType;

    /** The registered node insert event handler. */
    private JavaScriptObject m_nodeInsertHandler;

    /** The no edit reason, if empty editing is allowed. */
    private String m_noEditReason;

    /** The parent drop target. */
    private I_CmsDropContainer m_parent;

    /** Flag indicating if the element resource is currently released and not expired. */
    private boolean m_releasedAndNotExpired;

    /** The element resource site-path. */
    private String m_sitePath;

    /**
     * Indicates if the current user has view permissions on the element resource. 
     * Without view permissions, the element can neither be edited, nor moved. 
     **/
    private boolean m_viewPermission;

    /**
     * Flag which indicates whether the new editor is disabled for this element.<p>
     */
    private boolean m_disableNewEditor;

    /**
     * Constructor.<p>
     * 
     * @param element the DOM element
     * @param parent the drag parent
     * @param clientId the client id
     * @param sitePath the element site-path
     * @param noEditReason the no edit reason, if empty, editing is allowed
     * @param hasSettings should be true if the element has settings which can be edited 
     * @param hasViewPermission indicates if the current user has view permissions on the element resource
     * @param releasedAndNotExpired <code>true</code> if the element resource is currently released and not expired
     * @param disableNewEditor flag to disable the new editor for this element 
     */
    public CmsContainerPageElementPanel(
        Element element,
        I_CmsDropContainer parent,
        String clientId,
        String sitePath,
        String noEditReason,
        boolean hasSettings,
        boolean hasViewPermission,
        boolean releasedAndNotExpired,
        boolean disableNewEditor) {

        super((com.google.gwt.user.client.Element)element);
        m_clientId = clientId;
        m_sitePath = sitePath;
        m_noEditReason = noEditReason;
        m_hasSettings = hasSettings;
        m_parent = parent;
        m_disableNewEditor = disableNewEditor;
        setViewPermission(hasViewPermission);
        setReleasedAndNotExpired(releasedAndNotExpired);
        getElement().addClassName(I_CmsLayoutBundle.INSTANCE.dragdropCss().dragElement());
    }

    /**
     * Returns the necessary height as a CSS height string in pixels.<p>
     * 
     * @return the necessary height as a CSS string 
     */
    public static String getNecessaryHeight() {

        return NECESSARY_HEIGHT + "px !important";
    }

    /**
     * @see com.google.gwt.event.dom.client.HasClickHandlers#addClickHandler(com.google.gwt.event.dom.client.ClickHandler)
     */
    public HandlerRegistration addClickHandler(ClickHandler handler) {

        return addDomHandler(handler, ClickEvent.getType());
    }

    /**
     * @see com.alkacon.acacia.client.I_InlineFormParent#adoptWidget(com.alkacon.acacia.client.widgets.I_EditWidget)
     */
    public void adoptWidget(I_EditWidget widget) {

        getChildren().add(widget.asWidget());
        adopt(widget.asWidget());
    }

    /**
     * @see org.opencms.gwt.client.dnd.I_CmsDraggable#getDragHelper(org.opencms.gwt.client.dnd.I_CmsDropTarget)
     */
    public Element getDragHelper(I_CmsDropTarget target) {

        Element helper = CmsDomUtil.clone(getElement());
        target.getElement().appendChild(helper);
        // preparing helper styles
        String width = CmsDomUtil.getCurrentStyle(helper, CmsDomUtil.Style.width);
        Style style = helper.getStyle();
        style.setPosition(Position.ABSOLUTE);
        style.setMargin(0, Unit.PX);
        style.setProperty(CmsDomUtil.Style.width.name(), width);
        style.setZIndex(I_CmsLayoutBundle.INSTANCE.constants().css().zIndexDND());
        helper.addClassName(I_CmsLayoutBundle.INSTANCE.dragdropCss().dragging());
        helper.addClassName(org.opencms.gwt.client.ui.css.I_CmsLayoutBundle.INSTANCE.generalCss().shadow());
        if (!CmsDomUtil.hasBackground(helper)) {
            helper.addClassName(I_CmsLayoutBundle.INSTANCE.dragdropCss().dragElementBackground());
        }

        if (!CmsDomUtil.hasBorder(helper)) {
            helper.addClassName(I_CmsLayoutBundle.INSTANCE.dragdropCss().dragElementBorder());
        }
        return helper;
    }

    /**
     * Returns the option bar of this element.<p>
     * 
     * @return the option bar widget
     */
    public CmsElementOptionBar getElementOptionBar() {

        return m_elementOptionBar;
    }

    /**
     * @see org.opencms.gwt.client.dnd.I_CmsDraggable#getId()
     */
    public String getId() {

        return m_clientId;
    }

    /**
     * Returns the inheritance info for this element.<p>
     *
     * @return the inheritance info for this element
     */
    public CmsInheritanceInfo getInheritanceInfo() {

        return m_inheritanceInfo;
    }

    /**
     * Returns the new element type.
     * 
     * @return the new element type
     */
    public String getNewType() {

        return m_newType;
    }

    /**
     * Returns the no edit reason.<p>
     *
     * @return the no edit reason
     */
    public String getNoEditReason() {

        return m_noEditReason;
    }

    /**
     * @see org.opencms.gwt.client.dnd.I_CmsDraggable#getParentTarget()
     */
    public I_CmsDropContainer getParentTarget() {

        return m_parent;
    }

    /**
     * @see org.opencms.gwt.client.dnd.I_CmsDraggable#getPlaceholder(org.opencms.gwt.client.dnd.I_CmsDropTarget)
     */
    public Element getPlaceholder(I_CmsDropTarget target) {

        Element placeholder = CmsDomUtil.clone(getElement());
        placeholder.addClassName(I_CmsLayoutBundle.INSTANCE.dragdropCss().dragPlaceholder());
        return placeholder;
    }

    /**
     * Returns if the element resource is currently released and not expired.<p>
     * 
     * @return <code>true</code> if the element resource is currently released and not expired
     */
    public boolean getReleasedAndNotExpired() {

        return m_releasedAndNotExpired;
    }

    /**
     * Returns the site-path.<p>
     *
     * @return the site-path
     */
    public String getSitePath() {

        return m_sitePath;
    }

    /**
     * Returns the structure id of the element.<p>
     * 
     * @return the structure id of the element 
     */
    public CmsUUID getStructureId() {

        if (m_clientId == null) {
            return null;
        }
        return new CmsUUID(CmsContainerpageController.getServerId(m_clientId));
    }

    /**
     * Returns true if the element has settings to edit.<p>
     * 
     * @return true if the element has settings to edit 
     */
    public boolean hasSettings() {

        return m_hasSettings;
    }

    /**
     * Returns if the current user has view permissions for the element resource.<p>
     *
     * @return <code>true</code> if the current user has view permissions for the element resource
     */
    public boolean hasViewPermission() {

        return m_viewPermission;
    }

    /**
     * Hides list collector direct edit buttons, if present.<p>
     */
    public void hideEditableListButtons() {

        if (m_editables != null) {
            for (CmsListCollectorEditor editor : m_editables.values()) {
                editor.getElement().getStyle().setDisplay(Display.NONE);
            }
        }
    }

    /**
     * Puts a highlighting border around the element.<p>
     */
    public void highlightElement() {

        if (m_highlighting == null) {
            m_highlighting = new CmsHighlightingBorder(CmsPositionBean.generatePositionInfo(this), isNew()
            ? CmsHighlightingBorder.BorderColor.blue
            : CmsHighlightingBorder.BorderColor.red);
            RootPanel.get().add(m_highlighting);
        } else {
            m_highlighting.setPosition(CmsPositionBean.generatePositionInfo(this));
        }
    }

    /**
     * Initializes the editor click handler.<p>
     * 
     * @param controller the container page controller instance
     */
    public void initInlineEditor(final CmsContainerpageController controller) {

        if (CmsStringUtil.isEmptyOrWhitespaceOnly(m_noEditReason)
            && !m_disableNewEditor
            && CmsContentEditor.setEditable(getElement(), true)) {
            if (m_editorClickHandlerRegistration != null) {
                m_editorClickHandlerRegistration.removeHandler();
            }
            m_editorClickHandlerRegistration = addClickHandler(new ClickHandler() {

                public void onClick(ClickEvent event) {

                    // if another content is already being edited, don't start another editor
                    if (controller.isContentEditing()) {
                        return;
                    }
                    Element eventTarget = event.getNativeEvent().getEventTarget().cast();
                    Element linkTag = CmsDomUtil.getAncestor(eventTarget, Tag.a);
                    if ((linkTag != null) && linkTag.hasAttribute("href")) {
                        String linkTarget = linkTag.getAttribute("href");
                        Window.Location.assign(linkTarget);
                        return;
                    }
                    Element target = event.getNativeEvent().getEventTarget().cast();
                    while ((target != null) && (target != getElement())) {
                        if ("true".equals(target.getAttribute("contentEditable"))) {
                            controller.getHandler().openEditorForElement(CmsContainerPageElementPanel.this, true);
                            removeEditorHandler();
                            break;
                        } else {
                            target = target.getParentElement();
                        }
                    }
                }
            });
        }
    }

    /**
     * Returns if this is e newly created element.<p>
     * 
     * @return <code>true</code> if the element is new
     */
    public boolean isNew() {

        return m_newType != null;
    }

    /**
     * Returns true if the new content editor is disabled for this element.<p>
     * 
     * @return true if the new editor is disabled for this element
     */
    public boolean isNewEditorDisabled() {

        return m_disableNewEditor;
    }

    /**
     * @see org.opencms.gwt.client.dnd.I_CmsDraggable#onDragCancel()
     */
    public void onDragCancel() {

        clearDrag();
        resetOptionbar();
    }

    /**
     * @see org.opencms.gwt.client.dnd.I_CmsDraggable#onDrop(org.opencms.gwt.client.dnd.I_CmsDropTarget)
     */
    public void onDrop(I_CmsDropTarget target) {

        clearDrag();
    }

    /**
     * @see org.opencms.gwt.client.dnd.I_CmsDraggable#onStartDrag(org.opencms.gwt.client.dnd.I_CmsDropTarget)
     */
    public void onStartDrag(I_CmsDropTarget target) {

        CmsDomUtil.addDisablingOverlay(getElement());
        getElement().getStyle().setOpacity(0.5);
        removeHighlighting();
    }

    /**
     * @see com.google.gwt.user.client.ui.Widget#removeFromParent()
     */
    @Override
    public void removeFromParent() {

        removeHighlighting();
        super.removeFromParent();
    }

    /**
     * Removes the highlighting border.<p>
     */
    public void removeHighlighting() {

        if (m_highlighting != null) {
            m_highlighting.removeFromParent();
            m_highlighting = null;
        }
    }

    /**
     * Removes the inline editor.<p>
     */
    public void removeInlineEditor() {

        CmsContentEditor.setEditable(getElement(), false);
        removeEditorHandler();
    }

    /**
     * Sets the elementOptionBar.<p>
     *
     * @param elementOptionBar the elementOptionBar to set
     */
    public void setElementOptionBar(CmsElementOptionBar elementOptionBar) {

        if ((m_elementOptionBar != null) && (getWidgetIndex(m_elementOptionBar) >= 0)) {
            m_elementOptionBar.removeFromParent();
        }
        m_elementOptionBar = elementOptionBar;
        insert(m_elementOptionBar, 0);
        if (isOptionbarIFrameCollision(m_elementOptionBar.getCalculatedWidth())) {
            m_elementOptionBar.getElement().getStyle().setPosition(Position.RELATIVE);
            int marginLeft = getElement().getOffsetWidth() - m_elementOptionBar.getCalculatedWidth();
            m_elementOptionBar.getElement().getStyle().setMarginLeft(marginLeft, Unit.PX);
        }

    }

    /**
     * Sets the element id.<p>
     * 
     * @param id the id
     */
    public void setId(String id) {

        m_clientId = id;
    }

    /**
     * Sets the inheritance info for this element.<p>
     *
     * @param inheritanceInfo the inheritance info for this element to set
     */
    public void setInheritanceInfo(CmsInheritanceInfo inheritanceInfo) {

        m_inheritanceInfo = inheritanceInfo;
    }

    /**
     * Sets the new-type of the element.<p>
     * 
     * @param newType the new-type
     */
    public void setNewType(String newType) {

        m_newType = newType;
    }

    /**
     * Sets the no edit reason.<p>
     *
     * @param noEditReason the no edit reason to set
     */
    public void setNoEditReason(String noEditReason) {

        m_noEditReason = noEditReason;
    }

    /**
     * Sets if the element resource is currently released and not expired.<p>
     * 
     * @param releasedAndNotExpired <code>true</code> if the element resource is currently released and not expired
     */
    public void setReleasedAndNotExpired(boolean releasedAndNotExpired) {

        m_releasedAndNotExpired = releasedAndNotExpired;
        if (m_releasedAndNotExpired) {
            getElement().removeClassName(I_CmsLayoutBundle.INSTANCE.containerpageCss().expired());
            if (m_expiredOverlay != null) {
                m_expiredOverlay.removeFromParent();
                m_expiredOverlay = null;
            }

        } else {
            getElement().addClassName(I_CmsLayoutBundle.INSTANCE.containerpageCss().expired());
            m_expiredOverlay = DOM.createDiv();
            m_expiredOverlay.setTitle("Expired resource");
            m_expiredOverlay.addClassName(I_CmsLayoutBundle.INSTANCE.containerpageCss().expiredOverlay());
            getElement().appendChild(m_expiredOverlay);
        }
    }

    /**
     * Sets the site path.<p>
     *
     * @param sitePath the site path to set
     */
    public void setSitePath(String sitePath) {

        m_sitePath = sitePath;
    }

    /**
     * Sets if the current user has view permissions for the element resource.<p>
     *
     * @param viewPermission the view permission to set
     */
    public void setViewPermission(boolean viewPermission) {

        m_viewPermission = viewPermission;
    }

    /**
     * Shows list collector direct edit buttons (old direct edit style), if present.<p>
     */
    public void showEditableListButtons() {

        m_checkingEditables = true;
        if (m_editables == null) {
            m_editables = new HashMap<Element, CmsListCollectorEditor>();
            List<Element> editables = CmsDomUtil.getElementsByClass("cms-editable", Tag.div, getElement());
            if ((editables != null) && (editables.size() > 0)) {
                for (Element editable : editables) {
                    CmsListCollectorEditor editor = new CmsListCollectorEditor(editable, m_clientId);
                    add(editor, (com.google.gwt.user.client.Element)editable.getParentElement());
                    if (CmsDomUtil.hasDimension(editable.getParentElement())) {
                        editor.setPosition(CmsDomUtil.getEditablePosition(editable), getElement());
                    } else {
                        editor.getElement().getStyle().setDisplay(Display.NONE);
                    }
                    m_editables.put(editable, editor);
                }

            }
        } else {

            Iterator<Entry<Element, CmsListCollectorEditor>> it = m_editables.entrySet().iterator();
            while (it.hasNext()) {
                Entry<Element, CmsListCollectorEditor> entry = it.next();
                if (!entry.getValue().isValid()) {
                    entry.getValue().removeFromParent();
                    it.remove();
                } else if (CmsDomUtil.hasDimension(entry.getValue().getElement().getParentElement())) {
                    entry.getValue().getElement().getStyle().clearDisplay();
                    entry.getValue().setPosition(
                        CmsDomUtil.getEditablePosition(entry.getValue().getMarkerTag()),
                        getElement());
                }
            }
            List<Element> editables = CmsDomUtil.getElementsByClass("cms-editable", Tag.div, getElement());
            if (editables.size() > m_editables.size()) {
                for (Element editable : editables) {
                    if (!m_editables.containsKey(editable)) {
                        CmsListCollectorEditor editor = new CmsListCollectorEditor(editable, m_clientId);
                        add(editor, (com.google.gwt.user.client.Element)editable.getParentElement());
                        if (CmsDomUtil.hasDimension(editable.getParentElement())) {
                            editor.setPosition(CmsDomUtil.getEditablePosition(editable), getElement());
                        } else {
                            editor.getElement().getStyle().setDisplay(Display.NONE);
                        }
                        m_editables.put(editable, editor);

                    }
                }
            }

        }
        m_checkingEditables = false;
        resetNodeInsertedHandler();
    }

    /**
     * Checks for changes in the list collector direct edit content.<p>
     */
    protected void checkForEditableChanges() {

        if (!m_checkingEditables) {
            m_checkingEditables = true;
            Timer timer = new Timer() {

                @Override
                public void run() {

                    showEditableListButtons();
                }
            };
            timer.schedule(500);
        }
    }

    /**
     * Returns if the list collector direct edit content has changed.<p>
     * 
     * @return <code>true</code> if the list collector direct edit content has changed
     */
    protected boolean hasChangedEditables() {

        if (m_editables == null) {
            return true;
        }
        for (CmsListCollectorEditor editor : m_editables.values()) {
            if (!editor.isValid()) {
                return true;
            }
        }
        return CmsDomUtil.getElementsByClass("cms-editable", Tag.div, getElement()).size() > m_editables.size();
    }

    /**
     * @see com.google.gwt.user.client.ui.Widget#onLoad()
     */
    @Override
    protected void onLoad() {

        if (!m_checkedHeight) {
            m_checkedHeight = true;
            if (getOffsetHeight() < NECESSARY_HEIGHT) {
                m_checkedHeight = true;
                if (m_parent instanceof CmsContainerPageContainer) {
                    CmsContainerpageController.get().getHandler().enableShowSmallElements();
                    //            CmsContainerpageController.get().enableShowSmallElementsButton();
                    addStyleName(org.opencms.ade.containerpage.client.ui.css.I_CmsLayoutBundle.INSTANCE.containerpageCss().smallElement());
                }
            }
        }
        resetOptionbar();
    }

    /**
     * Removes the inline editor handler.<p>
     */
    protected void removeEditorHandler() {

        if (m_editorClickHandlerRegistration != null) {
            m_editorClickHandlerRegistration.removeHandler();
            m_editorClickHandlerRegistration = null;
        }
    }

    /**
     * Removes all styling done during drag and drop.<p>
     */
    private void clearDrag() {

        CmsDomUtil.removeDisablingOverlay(getElement());
        m_elementOptionBar.getElement().removeClassName(
            org.opencms.gwt.client.ui.css.I_CmsLayoutBundle.INSTANCE.stateCss().cmsHovering());
        // using own implementation as GWT won't do it properly on IE7-8
        CmsDomUtil.clearOpacity(getElement());
        getElement().getStyle().clearDisplay();
    }

    /**
     * Returns if the option bar position collides with any iframe child elements.<p>
     * 
     * @param optionWidth the option bar witdh 
     * 
     * @return <code>true</code> if there are iframe child elements located no less than 25px below the upper edge of the element
     */
    private boolean isOptionbarIFrameCollision(int optionWidth) {

        if (RootPanel.getBodyElement().isOrHasChild(getElement())) {
            int elementTop = getElement().getAbsoluteTop();

            NodeList<Element> frames = getElement().getElementsByTagName(CmsDomUtil.Tag.iframe.name());
            for (int i = 0; i < frames.getLength(); i++) {

                if (((frames.getItem(i).getAbsoluteTop() - elementTop) < 25)
                    && ((frames.getItem(i).getAbsoluteRight() - getElement().getAbsoluteRight()) < optionWidth)) {
                    return true;
                }

            }
        }
        return false;
    }

    /**
     * Resets the node inserted handler.<p>
     */
    private native void resetNodeInsertedHandler()/*-{
        var $this = this;
        var element = $this.@org.opencms.ade.containerpage.client.ui.CmsContainerPageElementPanel::getElement()();
        var handler = $this.@org.opencms.ade.containerpage.client.ui.CmsContainerPageElementPanel::m_nodeInsertHandler;
        if (handler == null) {
            handler = function(event) {
                $this.@org.opencms.ade.containerpage.client.ui.CmsContainerPageElementPanel::checkForEditableChanges()();
            };
            $this.@org.opencms.ade.containerpage.client.ui.CmsContainerPageElementPanel::m_nodeInsertHandler = handler;
        } else {
            if (element.removeEventLister) {
                element.removeEventListener("DOMNodeInserted", handler);
            } else if (element.detachEvent) {
                // IE specific
                element.detachEvent("onDOMNodeInserted", handler);
            }
        }
        if (element.addEventListener) {
            element.addEventListener("DOMNodeInserted", handler, false);
        } else if (element.attachEvent) {
            // IE specific
            element.attachEvent("onDOMNodeInserted", handler);
        }
    }-*/;

    /**
     * This method removes the option-bar widget from DOM and re-attaches it at it's original position.<p>
     * Use to avoid mouse-over and mouse-down malfunction.<p> 
     */
    private void resetOptionbar() {

        if (m_elementOptionBar != null) {
            if (getWidgetIndex(m_elementOptionBar) >= 0) {
                m_elementOptionBar.removeFromParent();
            }
            if (isOptionbarIFrameCollision(m_elementOptionBar.getCalculatedWidth())) {
                m_elementOptionBar.getElement().getStyle().setPosition(Position.RELATIVE);
                int marginLeft = getElement().getClientWidth() - m_elementOptionBar.getCalculatedWidth();
                if (marginLeft > 0) {
                    m_elementOptionBar.getElement().getStyle().setMarginLeft(marginLeft, Unit.PX);
                }
            } else {
                m_elementOptionBar.getElement().getStyle().clearPosition();
                m_elementOptionBar.getElement().getStyle().clearMarginLeft();
            }
            insert(m_elementOptionBar, 0);
        }
    }

}
