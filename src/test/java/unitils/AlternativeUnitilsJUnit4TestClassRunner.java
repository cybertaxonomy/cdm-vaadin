/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package unitils;

import java.io.IOException;
import java.io.InputStream;

import org.junit.internal.runners.InitializationError;
import org.junit.runner.notification.RunNotifier;
import org.unitils.UnitilsJUnit4TestClassRunner;

/**
 * A runner which enables all vaadin UIs for the tests
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

        loadSystemPropertiesFrom("spring-environment.mock.properties");
        super.run(notifier);
    }

    /**
     * @param propFile
     */
    protected void loadSystemPropertiesFrom(String propFile) {
        InputStream inStream = this.getClass().getClassLoader().getResourceAsStream(propFile);
        //Properties props = new Properties();
        try {
            // props.load(inStream);
            System.getProperties().load(inStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



}
