/*
 * File   : $Source: /alkacon/cvs/opencms/src/org/opencms/security/Messages.java,v $
 * Date   : $Date: 2005/04/24 11:20:32 $
 * Version: $Revision: 1.2 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Mananagement System
 *
 * Copyright (C) 2002 - 2005 Alkacon Software (http://www.alkacon.com)
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

package org.opencms.security;

import org.opencms.i18n.A_CmsMessageBundle;
import org.opencms.i18n.I_CmsMessageBundle;

/**
 * Convenience class to access the localized messages of this OpenCms package.<p> 
 * 
 * @author Alexander Kandzior (a.kandzior@alkacon.com)
 * @since 5.7.3
 */
public final class Messages extends A_CmsMessageBundle {

    /** Name of the used resource bundle. */
    public static final String ERR_NOT_IN_ROLE_2 = "ERR_NOT_IN_ROLE_2";

    /** Name of the used resource bundle. */
    public static final String ERR_NOT_MANAGER_OF_PROJECT_2 = "ERR_NOT_MANAGER_OF_PROJECT_2";

    /** Name of the used resource bundle. */
    public static final String ERR_ROLE_VIOLATION_1 = "ERR_ROLE_VIOLATION_1";

    /** Name of the used resource bundle. */
    public static final String ERR_STARTUP_FINISHED_0 = "ERR_STARTUP_FINISHED_0";

    /** Name of the used resource bundle. */
    public static final String GUI_ROLENAME_ADMINISTRATOR_0 = "GUI_ROLENAME_ADMINISTRATOR_0";

    /** Name of the used resource bundle. */
    public static final String GUI_ROLENAME_DEVELOPER_0 = "GUI_ROLENAME_DEVELOPER_0";

    /** Name of the used resource bundle. */
    public static final String GUI_ROLENAME_EXPORT_DATABASE_0 = "GUI_ROLENAME_EXPORT_DATABASE_0";

    /** Name of the used resource bundle. */
    public static final String GUI_ROLENAME_HISTORY_MANAGER_0 = "GUI_ROLENAME_HISTORY_MANAGER_0";

    /** Name of the used resource bundle. */
    public static final String GUI_ROLENAME_IMPORT_DATABASE_0 = "GUI_ROLENAME_IMPORT_DATABASE_0";

    /** Name of the used resource bundle. */
    public static final String GUI_ROLENAME_MODULE_MANAGER_0 = "GUI_ROLENAME_MODULE_MANAGER_0";

    /** Name of the used resource bundle. */
    public static final String GUI_ROLENAME_PROJECT_MANAGER_0 = "GUI_ROLENAME_PROJECT_MANAGER_0";

    /** Name of the used resource bundle. */
    public static final String GUI_ROLENAME_PROPERTY_MANAGER_0 = "GUI_ROLENAME_PROPERTY_MANAGER_0";

    /** Name of the used resource bundle. */
    public static final String GUI_ROLENAME_RESOURCE_TYPE_MANAGER_0 = "GUI_ROLENAME_RESOURCE_TYPE_MANAGER_0";

    /** Name of the used resource bundle. */
    public static final String GUI_ROLENAME_SCHEDULER_MANAGER_0 = "GUI_ROLENAME_SCHEDULER_MANAGER_0";

    /** Name of the used resource bundle. */
    public static final String GUI_ROLENAME_USER_MANAGER_0 = "GUI_ROLENAME_USER_MANAGER_0";

    /** Name of the used resource bundle. */
    public static final String GUI_ROLENAME_VFS_MANAGER_0 = "GUI_ROLENAME_VFS_MANAGER_0";

    /** Name of the used resource bundle. */
    public static final String GUI_ROLENAME_WORKPLACE_USER_0 = "GUI_ROLENAME_WORKPLACE_USER_0";

    /** Name of the used resource bundle. */
    public static final String GUI_ROLENAME_ROOT_FOLDER_ACCESS_0 = "GUI_ROLENAME_ROOT_FOLDER_ACCESS_0";
    
    /** Name of the used resource bundle. */
    public static final String GUI_ROLENAME_WORKPLACE_MANAGER_0 = "GUI_ROLENAME_WORKPLACE_MANAGER_0";
    
    /** Name of the used resource bundle. */
    private static final String BUNDLE_NAME = "org.opencms.security.messages";

    /** Static instance member. */
    private static final I_CmsMessageBundle INSTANCE = new Messages();

    /**
     * Hides the public constructor for this utility class.<p>
     */
    private Messages() {

        // hide the constructor
    }

    /**
     * Returns an instance of this localized message accessor.<p>
     * 
     * @return an instance of this localized message accessor
     */
    public static I_CmsMessageBundle get() {

        return INSTANCE;
    }

    /**
     * Returns the bundle name for this OpenCms package.<p>
     * 
     * @return the bundle name for this OpenCms package
     */
    public String getBundleName() {

        return BUNDLE_NAME;
    }
}
