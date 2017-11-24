/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package unitils;

import org.junit.internal.runners.InitializationError;
import org.junit.runner.notification.RunNotifier;
import org.unitils.UnitilsJUnit4TestClassRunner;

import eu.etaxonomy.cdm.addon.config.CdmVaadinConfiguration;

/**
 * A runner which causes the {@link org.unitils.core.ConfigurationLoader} to load <code>unitils-alternativeRunner.properties</code>
 * instead of <code>unitils.properties</code>.
 *
 * @author a.kohlbecker
 * @since Nov 23, 2017
 *
 */
public class AlternativeUnitilsJUnit4TestClassRunner extends UnitilsJUnit4TestClassRunner {

    /**
     * @param testClass
     * @throws InitializationError
     */
    public AlternativeUnitilsJUnit4TestClassRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(RunNotifier notifier) {
        System.setProperty(CdmVaadinConfiguration.CDM_VAADIN_UI_ACTIVATED, "concept,distribution,editstatus,registration");
        System.setProperty("unitils.configuration.customFileName", "unitils-alternativeRunner.properties");
        super.run(notifier);
    }



}
