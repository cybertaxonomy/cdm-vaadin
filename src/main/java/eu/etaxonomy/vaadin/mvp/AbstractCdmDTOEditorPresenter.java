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
import eu.etaxonomy.cdm.vaadin.model.CdmEntityDecoraterDTO;

/**
 * Provides generic save operations of modified cdm entities.
 *
 * @author a.kohlbecker
 * @since Apr 5, 2017
 *
 */
public abstract class AbstractCdmDTOEditorPresenter<DTO extends CdmEntityDecoraterDTO<CDM>, CDM extends CdmBase, V extends ApplicationView<?>>
    extends CdmEditorPresenterBase<DTO, CDM, V> {

    private static final long serialVersionUID = -6315824180341694825L;


    @Override
    protected CDM cdmEntity(DTO dto) {
        return dto.cdmEntity();
    }



}