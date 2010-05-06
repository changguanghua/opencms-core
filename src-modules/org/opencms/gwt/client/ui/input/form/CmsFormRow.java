/*
 * File   : $Source: /alkacon/cvs/opencms/src-modules/org/opencms/gwt/client/ui/input/form/Attic/CmsFormRow.java,v $
 * Date   : $Date: 2010/05/06 09:51:37 $
 * Version: $Revision: 1.1 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (C) 2002 - 2009 Alkacon Software (http://www.alkacon.com)
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

package org.opencms.gwt.client.ui.input.form;

import org.opencms.gwt.client.ui.css.I_CmsInputCss;
import org.opencms.gwt.client.ui.css.I_CmsInputLayoutBundle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A row in a properties form.<p>
 * 
 * This widget contains both a label and a panel into which an input widget for the form field can be placed. 
 * These widgets are next to each other horizontally.
 * 
 * @author Georg Westenberger 
 * 
 * @version $Revision: 1.1 $
 * 
 * @since 8.0.0
 */
public class CmsFormRow extends Composite {

    /** The ui binder interface for this widget. */
    interface I_CmsFormRowUiBinder extends UiBinder<Widget, CmsFormRow> {
    }

    /** The CSS bundle used for this widget. */
    protected static I_CmsInputCss CSS = I_CmsInputLayoutBundle.INSTANCE.inputCss();

    /** The ui binder instance for this form row. */
    private static I_CmsFormRowUiBinder uiBinder = GWT.create(I_CmsFormRowUiBinder.class);

    /** The label for the form row. */
    @UiField
    protected Label m_label;

    /** The widget container for the form row. */
    @UiField
    protected Panel m_widgetContainer;

    /**
     * The default constructor. 
     */
    public CmsFormRow() {

        initWidget(uiBinder.createAndBindUi(this));
        m_label.addStyleName(CSS.inlineBlock());
        m_widgetContainer.addStyleName(CSS.inlineBlock());
    }

    /**
     * Returns the label for the form row.<p>
     * 
     * @return the label for the form row 
     */
    public Label getLabel() {

        return m_label;
    }

    /**
     * Returns the widget container for the form row.<p>
     * 
     * @return the widget container for the form row
     */
    public Panel getWidgetContainer() {

        return m_widgetContainer;
    }

}