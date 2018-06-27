/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.vaadin.ui.navigation;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.ui.Field;
import com.vaadin.ui.Window;

import eu.etaxonomy.vaadin.mvp.ApplicationView;
import eu.etaxonomy.vaadin.ui.view.PopupView;

/**
 * @author a.kohlbecker
 * @since May 16, 2018
 *
 */
public class PopupViewRegistration {

    private Map<PopupView, Window> popupWindowMap = new HashMap<>();

    private Map<ApplicationView<?>, Map<PopupView, Field<?>>> popupViewFieldMap = new HashMap<>();

    /**
     * @param view
     * @param popup
     * @param field can be <code>null</code>
     * @return the previous Field associated with the popup that has been opened in the view
     */
    public Field<?> put(Window window, ApplicationView parentView, PopupView popup, Field<?> field){

        popupWindowMap.put(popup, window);

        if(!popupViewFieldMap.containsKey(parentView)){
            popupViewFieldMap.put(parentView, new HashMap<>());
        }
        Map<PopupView, Field<?>> popupFieldMap = popupViewFieldMap.get(parentView);
        return popupFieldMap.put(popup, field);
    }

    public Field<?> get(ApplicationView view, PopupView popup){
        if(!popupViewFieldMap.containsKey(view)){
            popupViewFieldMap.get(view).get(popup);
        }
        return null;
    }

    public Field<?> get(PopupView popup){
        for(Map<PopupView, Field<?>> popupFieldMap : popupViewFieldMap.values()){
            if(popupFieldMap.containsKey(popup)){
                return popupFieldMap.get(popup);
            }
        }
        return null;
    }

    /**
     * @param popup
     */
    public void remove(PopupView popup) {

        popupWindowMap.remove(popup);

        for(Map<PopupView, Field<?>> popupFieldMap : popupViewFieldMap.values()){
            if(popupFieldMap.containsKey(popup)){
                popupFieldMap.remove(popup);
            }
        }
    }

    /**
     * @param popup
     * @return
     */
    public Window getWindow(PopupView popup) {
        return popupWindowMap.get(popup);
    }

    /**
     *
     */
    public void removeOrphan() {
        // unimplemented, only needed in case of memory leaks due to lost popups
    }

}
