/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.permission;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vaadin.navigator.View;
import com.vaadin.spring.access.ViewInstanceAccessControl;
import com.vaadin.ui.UI;

/**
 * Denies, allows access to view by evaluating the {@link AccessRestrictedView#isAccessDenied()} flag.
 *
 * @author a.kohlbecker
 */
public class AccessRestrictedViewControlBean implements ViewInstanceAccessControl, Serializable {

    private static final long serialVersionUID = -5192116600545639108L;

    private static final Logger logger = LogManager.getLogger();

    @Override
    public boolean isAccessGranted(UI ui, String beanName, View view) {

        if(AccessRestrictedView.class.isAssignableFrom(view.getClass())){
            AccessRestrictedView restricedView = (AccessRestrictedView)view;
            if(logger.isDebugEnabled()){
                logger.debug("Access to view " + view.getClass().getSimpleName() + (restricedView.isAccessDenied() ? " denied": " allowed"));
            }
            return !restricedView.isAccessDenied();
        }
        logger.debug("allowing view by fall through, no check performed");
        return true;
    }
}
