/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.vaadin.mvp.event;

import eu.etaxonomy.vaadin.mvp.AbstractView;

/**
 * @author a.kohlbecker
 * @since Jun 1, 2017
 *
 */
public class EditorDeleteEvent<DTO extends Object> implements EditorViewEvent, EditorBeanEvent<DTO> {

    // FIXME this is only a iterim solution for the problem described in https://dev.e-taxonomy.eu/redmine/issues/6562
    private AbstractView<?> view;

    private DTO bean;

    public EditorDeleteEvent(AbstractView<?> view, DTO bean){
        this.view = view;
        this.bean = bean;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractView<?> getView() {
        return view;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DTO getBean() {
        return bean;
    }

}
