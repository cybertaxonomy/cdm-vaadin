/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.vaadin.mvp;

import java.util.EnumSet;

import com.vaadin.ui.Layout;

import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.persistence.hibernate.permission.CRUD;
import eu.etaxonomy.cdm.vaadin.model.CdmEntityAdapterDTO;
import eu.etaxonomy.cdm.vaadin.view.PerEntityAuthorityGrantingEditor;

/**
 * @author a.kohlbecker
 * @since May 5, 2017
 *
 */
public abstract class AbstractCdmDTOPopupEditor<DTO extends CdmEntityAdapterDTO<CDM>, CDM extends CdmBase, P extends CdmEditorPresenterBase<DTO, CDM, ? extends ApplicationView>>
    extends AbstractPopupEditor<DTO, P> implements PerEntityAuthorityGrantingEditor {

    /**
     * @param layout
     * @param dtoType
     */
    public AbstractCdmDTOPopupEditor(Layout layout, Class<DTO> dtoType) {
        super(layout, dtoType);
    }

    private static final long serialVersionUID = -5025937489746256070L;

    @Override
    public void grantToCurrentUser(EnumSet<CRUD> crud){
        getPresenter().setGrantsForCurrentUser(crud);
    }

    public void setCdmEntityInstantiator(BeanInstantiator<CDM> cdmEntityInstantiator) {
        ((AbstractCdmDTOEditorPresenter)getPresenter()).setCdmEntityInstantiator(cdmEntityInstantiator);
    }



}
