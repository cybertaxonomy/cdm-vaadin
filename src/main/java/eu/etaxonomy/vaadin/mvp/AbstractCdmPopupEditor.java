/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.vaadin.mvp;

import com.vaadin.ui.Layout;

import eu.etaxonomy.cdm.model.common.CdmBase;

/**
 * @author a.kohlbecker
 * @since May 5, 2017
 *
 */
public abstract class AbstractCdmPopupEditor<DTO extends CdmBase, P extends AbstractEditorPresenter<DTO>>
    extends AbstractPopupEditor<DTO, P> {

    private static final long serialVersionUID = -5025937489746256070L;

    /**
     * @param layout
     * @param dtoType
     */
    public AbstractCdmPopupEditor(Layout layout, Class<DTO> dtoType) {
        super(layout, dtoType);
    }

}
