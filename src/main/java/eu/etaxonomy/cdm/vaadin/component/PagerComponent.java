/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.component;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.themes.ValoTheme;

import eu.etaxonomy.cdm.api.service.pager.Pager;

/**
 * @author a.kohlbecker
 * @since Jul 3, 2018
 *
 */
public class PagerComponent extends GridLayout {

    private static final long serialVersionUID = 3097302030025769906L;

    // String viewStateTemplate;

    private PagerClickListener pagerClickListener;

    /**
     *
     * @param viewStateBase
     *            template where <code>$1%d</code> is being used as placeholder
     *            for the page index. E.g. <code>list/all/$1%d</code>
     */
    public PagerComponent(PagerClickListener pagerClickListener){
        super(1, 1);
        this.pagerClickListener = pagerClickListener;
        // this.viewStateTemplate = viewStateTemplate;
    }

    public void updatePager(Pager<?> pager){
        List<Component> pagerComponents = new ArrayList<>();

        if(pager.getCurrentIndex() > 0){
            pagerComponents.add(pagerButton(0, null, FontAwesome.ANGLE_DOUBLE_LEFT));
            pagerComponents.add(pagerButton(pager.getCurrentIndex() - 1, null, FontAwesome.ANGLE_LEFT));
        }
        for(Integer index : pager.getIndices()){
            Button button = pagerButton(index, index.toString(), null);
            if(index.intValue() == pager.getCurrentIndex()){
                button.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
            }
            pagerComponents.add(button);
        }

        if(pager.getCurrentIndex() != pager.getPagesAvailable()-1){
            pagerComponents.add(pagerButton(pager.getCurrentIndex() + 1, null, FontAwesome.ANGLE_RIGHT));
            pagerComponents.add(pagerButton(pager.getPagesAvailable() - 1, null, FontAwesome.ANGLE_DOUBLE_RIGHT));
        }
        Button rangeIndicator = new Button("[ " + pager.getFirstRecord() + " - " + pager.getLastRecord() + " / " + pager.getCount() + " ]");
        rangeIndicator.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        rangeIndicator.setEnabled(false); // just a label
        pagerComponents.add(rangeIndicator);

        removeAllComponents();
        setColumns(pagerComponents.size() + 1);
        addComponents(pagerComponents.toArray(new Component[pagerComponents.size()]));
    }

    /**
     * @param i
     * @param string
     * @param backward
     * @return
     */
    private Button pagerButton(int index, String caption, Resource icon) {
        Button b = new Button(caption, icon);
        b.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        b.addClickListener(e -> pagerClickListener.pageIndexClicked(index));
        return b;
    }

    public interface PagerClickListener {

        public void pageIndexClicked(Integer index);
    }
}
