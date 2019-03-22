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
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.spring.annotation.SpringBean;
import org.unitils.spring.annotation.SpringBeanByType;
import org.vaadin.spring.events.EventBus.ViewEventBus;

import com.vaadin.ui.NativeSelect;

import eu.etaxonomy.cdm.api.application.CdmRepository;
import eu.etaxonomy.cdm.model.agent.Institution;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.vaadin.component.common.FilterableAnnotationsField;
import eu.etaxonomy.cdm.vaadin.component.common.TeamOrPersonField;
import eu.etaxonomy.cdm.vaadin.view.reference.ReferenceEditorPresenter;
import eu.etaxonomy.cdm.vaadin.view.reference.ReferencePopupEditorView;
import eu.etaxonomy.vaadin.component.ToOneRelatedEntityCombobox;

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
    protected ViewEventBus viewEventBus;

    @SpringBeanByType
    private ReferenceEditorPresenter referencePresenter;

    @SpringBean("cdmRepository")
    private CdmRepository repo;

    @DataSet
    // @Test test setup not jet working :(
    public void testSaveReference() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {


        TestReferenceEditorView testView = new TestReferenceEditorView();
        referencePresenter.init(testView);

    }

    class TestReferenceEditorView implements ReferencePopupEditorView {

        /**
         * {@inheritDoc}
         */
        @Override
        public NativeSelect getTypeSelect() {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ToOneRelatedEntityCombobox<Reference> getInReferenceCombobox() {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public TeamOrPersonField getAuthorshipField() {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public FilterableAnnotationsField getAnnotationsField() {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ToOneRelatedEntityCombobox<Institution> getInstitutionCombobox() {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ToOneRelatedEntityCombobox<Institution> getSchoolCombobox() {
            return null;
        }



    }

}
