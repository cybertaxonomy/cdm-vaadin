/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.util;

import org.joda.time.DateTimeFieldType;
import org.joda.time.Partial;
import org.junit.Assert;
import org.junit.Test;

import eu.etaxonomy.cdm.vaadin.util.converter.JodaTimePartialConverter;

/**
 * @author a.kohlbecker
 * @since Apr 10, 2017
 *
 */
public class JodaTimePartialConverterTest extends Assert {

    Partial y = new Partial(
            new DateTimeFieldType[]{
                    DateTimeFieldType.year()
                    },
            new int[]{
                    2012
                    }
            );
    Partial ym = new Partial(
            new DateTimeFieldType[]{
                    DateTimeFieldType.year(),
                    DateTimeFieldType.monthOfYear()
                    },
            new int[]{
                    2013,
                    04
                    }
            );
    Partial ymd = new Partial(
            new DateTimeFieldType[]{
                    DateTimeFieldType.year(),
                    DateTimeFieldType.monthOfYear(),
                    DateTimeFieldType.dayOfMonth()
                    },
            new int[]{
                    2014,
                    04,
                    12
                    }
            );


    @Test
    public void toISO8601() {
        JodaTimePartialConverter conv = new JodaTimePartialConverter(JodaTimePartialConverter.DateFormat.ISO8601);
        assertEquals("2012", conv.convertToPresentation(y, String.class, null));
        assertEquals("2013-04", conv.convertToPresentation(ym, String.class, null));
        assertEquals("2014-04-12", conv.convertToPresentation(ymd, String.class, null));
    }


    @Test
    public void toDAY_MONTH_YEAR_DOT() {
        JodaTimePartialConverter conv = new JodaTimePartialConverter(JodaTimePartialConverter.DateFormat.DAY_MONTH_YEAR_DOT);
        assertEquals("2012", conv.convertToPresentation(y, String.class, null));
        assertEquals("04.2013", conv.convertToPresentation(ym, String.class, null));
        assertEquals("12.04.2014", conv.convertToPresentation(ymd, String.class, null));
    }

    @Test
    public void fromDAY_MONTH_YEAR_DOT() {
        // using null as format since the conversion should work for all formats
        JodaTimePartialConverter conv = new JodaTimePartialConverter(null);
        assertEquals(y, conv.convertToModel("2012", Partial.class, null));
        assertEquals(ym, conv.convertToModel("2013-04", Partial.class, null));
        assertEquals(ym, conv.convertToModel("04.2013", Partial.class, null));
        assertEquals(ymd, conv.convertToModel("2014-04-12", Partial.class, null));
        assertEquals(ymd, conv.convertToModel("12.04.2014", Partial.class, null));
        assertEquals(ymd, conv.convertToModel("12.4.2014", Partial.class, null));
    }

}
