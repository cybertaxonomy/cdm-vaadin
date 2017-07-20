/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.vaadin.mvp;

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.spring.annotation.SpringBean;
import org.unitils.spring.annotation.SpringBeanByType;

import com.vaadin.ui.ListSelect;

import eu.etaxonomy.cdm.api.application.CdmRepository;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.vaadin.view.reference.ReferenceEditorPresenter;
import eu.etaxonomy.cdm.vaadin.view.reference.ReferencePopupEditorView;
import eu.etaxonomy.vaadin.component.ToOneRelatedEntityCombobox;
import eu.etaxonomy.vaadin.ui.view.PopupEditorFactory;

/**
 * @author a.kohlbecker
 * @since Jun 2, 2017
 *
 */
// @RunWith(SpringJUnit4ClassRunner.class)
// @ContextConfiguration(classes = { CdmVaadinTestConfiguration.class })
// @VaadinAppConfiguration
public class CdmEditorPresenterTest {

    private static final Logger logger = Logger.getLogger(CdmEditorPresenterTest.class);

    @SpringBeanByType
    protected ApplicationEventPublisher eventBus;

    @SpringBean("cdmRepository")
    private CdmRepository repo;

    @SpringBeanByType
    protected PopupEditorFactory factory;

    @DataSet
    // @Test test setup not jet working :(
    public void testSaveReference() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        ReferenceEditorPresenter referencePresenter = new ReferenceEditorPresenter();
        factory.injectPresenterBeans(ReferenceEditorPresenter.class, referencePresenter);

        TestReferenceEditorView testView = new TestReferenceEditorView();
        referencePresenter.init(testView);

    }

    class TestReferenceEditorView implements ReferencePopupEditorView {

        /**
         * {@inheritDoc}
         */
        @Override
        public ListSelect getTypeSelect() {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ToOneRelatedEntityCombobox<Reference> getInReferenceCombobox() {
            return null;
        }



    }

}
