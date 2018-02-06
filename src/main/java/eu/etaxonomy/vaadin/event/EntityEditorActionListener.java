/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.vaadin.event;

/**
 * Implementations will usually look like:
 *
 * <pre>
    myComboboxSelect.setEditActionListener(e -> {

            Object fieldValue = e.getSource().getValue();
            Integer beanId = null;
            if(fieldValue != null){
                beanId = ((CdmBase)fieldValue).getId();

            }
            getViewEventBus().publish(this, new SomeEditorAction(e.getAction(), beanId, e.getSource(), this));
        });
  }
 *</pre>
 * @author a.kohlbecker
 * @since Jan 17, 2018
 *
 */
public interface EntityEditorActionListener {


    /**
     * see type level documentation for implementation hints.
     *
     * @param action
     */
    public void onEntityEditorActionEvent(EntityEditorActionEvent action);

}
