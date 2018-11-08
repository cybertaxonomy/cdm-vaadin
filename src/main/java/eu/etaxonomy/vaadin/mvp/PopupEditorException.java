/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.vaadin.mvp;

import eu.etaxonomy.cdm.vaadin.event.EditorActionContextFormat;
import eu.etaxonomy.cdm.vaadin.event.EditorActionContextFormat.TargetInfoType;
import eu.etaxonomy.cdm.vaadin.event.EditorActionContextFormatter;

/**
 * @author a.kohlbecker
 * @since Nov 8, 2018
 *
 */
public class PopupEditorException extends RuntimeException {

    private static final long serialVersionUID = -4242789621156408545L;

    private String contextInfo;

    /**
     * @param message
     */
    public PopupEditorException(String message, AbstractPopupEditor<?,?> editor, Throwable e) {
        super(message, e);

        EditorActionContextFormatter formatter = new EditorActionContextFormatter();
        EditorActionContextFormat format = new EditorActionContextFormat(true, true, true, true, TargetInfoType.PROPERTIES, null);
        format.tagName = null;
        format.doTargetEntity = true;
        contextInfo = formatter.format(editor.getEditorActionContext(), format);
    }

    public PopupEditorException(String message, AbstractPopupEditor<?,?> editor) {
        super(message);

        EditorActionContextFormatter formatter = new EditorActionContextFormatter();
        EditorActionContextFormat format = new EditorActionContextFormat(true, true, true, true, TargetInfoType.PROPERTIES, null);
        format.tagName = null;
        format.doTargetEntity = true;
        contextInfo = formatter.format(editor.getEditorActionContext(), format);
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " - context:" + contextInfo;
    }


    @Override
    public String getLocalizedMessage() {
        return getMessage();
    }

}
