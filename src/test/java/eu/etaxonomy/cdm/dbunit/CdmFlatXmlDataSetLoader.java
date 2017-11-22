/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.dbunit;

import java.io.InputStream;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.springframework.core.io.Resource;

import com.github.springtestdbunit.dataset.AbstractDataSetLoader;

import eu.etaxonomy.cdm.common.StreamUtils;

/**
 * This is just a copy of the {@link FlatXmlDataSetLoader} which strips all
 * [null] values from the loaded dataset.
 *
 * @author a.kohlbecker
 * @since Nov 21, 2017
 *
 */
public class CdmFlatXmlDataSetLoader extends AbstractDataSetLoader {

    @Override
    protected IDataSet createDataSet(Resource resource) throws Exception {
        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
        builder.setColumnSensing(true);
        return buildDataSetFromStream(builder, resource);
    }


    private IDataSet buildDataSetFromStream(FlatXmlDataSetBuilder builder, Resource resource) throws Exception {

        InputStream inputStream = resource.getInputStream();
        InputStream replacedStream = StreamUtils.streamReplaceAll(inputStream, "\\s[A-Z_]*=\"\\[null\\]\"", "");
        try {
            return builder.build(replacedStream);
        } finally {
            inputStream.close();
        }
    }

}
