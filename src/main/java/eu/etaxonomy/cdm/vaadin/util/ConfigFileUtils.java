/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import eu.etaxonomy.cdm.common.CdmUtils;

/**
 * @author a.kohlbecker
 * @since May 8, 2017
 *
 */
public class ConfigFileUtils extends CdmUtils {

    //TODO better store in VaadinSession?
    static Properties uiprops = null;

    public static File getPropertiesFile(String instanceName, String propertiesSet) {

        File configFolder = getCdmHomeSubDir(CdmUtils.SUBFOLDER_WEBAPP);
        return new File(configFolder, instanceName + (propertiesSet == null? "" : "-" + propertiesSet) + ".properties");

    }

    public static Properties getApplicationProperties(String instanceName) throws IOException {
        if(uiprops == null){
            uiprops = new Properties();
            File uiPropertiesFile = getPropertiesFile(instanceName, "app");
            if(uiPropertiesFile.exists()){
                try {
                    uiprops.load(new FileInputStream(uiPropertiesFile));
                } catch (FileNotFoundException e) {
                    // must not happen since we checked before
                }
            }
        }
        return uiprops;
    }
}
