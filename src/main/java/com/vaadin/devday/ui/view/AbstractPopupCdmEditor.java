/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package com.vaadin.devday.ui.view;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.ui.AbstractOrderedLayout;

import eu.etaxonomy.cdm.api.application.CdmRepository;
import eu.etaxonomy.cdm.model.common.CdmBase;

/**
 * @author a.kohlbecker
 * @since Mar 1, 2017
 *
 */
public abstract class AbstractPopupCdmEditor<DTO extends CdmBase> extends AbstractPopupEditor<DTO> {

    private static final long serialVersionUID = -7293978747415508916L;

    @Autowired
    private CdmRepository repo;

    /**
     * @param layout
     * @param dtoType
     */
    public AbstractPopupCdmEditor(AbstractOrderedLayout layout, Class<DTO> dtoType) {
        super(layout, dtoType);
    }

    public AbstractPopupCdmEditor(Class<DTO> dtoType) {
        super(dtoType);
    }

}
