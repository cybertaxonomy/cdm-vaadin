/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.vaadin.mvp;

import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.vaadin.model.CdmEntityAdapterDTO;

/**
 * Provides generic save operations of modified cdm entities.
 *
 * @author a.kohlbecker
 * @since Apr 5, 2017
 *
 */
public abstract class AbstractCdmDTOEditorPresenter<DTO extends CdmEntityAdapterDTO<CDM>, CDM extends CdmBase, V extends ApplicationView<?>>
    extends CdmEditorPresenterBase<DTO, CDM, V> {

    private static final long serialVersionUID = -6315824180341694825L;

    protected BeanInstantiator<CDM> cdmEntityInstantiator = null;

    @Override
    protected BeanInstantiator<DTO> defaultBeanInstantiator(){
        // not needed in the AbstractCdmDTOEditorPresenter since replaced by cdmEntityInstantiator
       return null;
    }


    @Override
    protected CDM cdmEntity(DTO dto) {
        return dto.cdmEntity();
    }


    /**
     * @param cdmEntityInstantiator the cdmEntityInstantiator to set
     */
    public void setCdmEntityInstantiator(BeanInstantiator<CDM> cdmEntityInstantiator) {
        this.cdmEntityInstantiator = cdmEntityInstantiator;
    }



}