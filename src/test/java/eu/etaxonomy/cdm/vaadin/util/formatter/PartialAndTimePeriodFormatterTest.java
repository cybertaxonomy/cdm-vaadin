/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.util.formatter;

import org.joda.time.DateTimeFieldType;
import org.joda.time.Partial;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import eu.etaxonomy.cdm.model.common.TimePeriod;

/**
 * @author a.kohlbecker
 * @since Apr 28, 2017
 *
 */
public class PartialAndTimePeriodFormatterTest extends Assert {

    Partial dmy;
    Partial my;
    Partial y;

    @Before
    public void init(){
        dmy = new Partial(new DateTimeFieldType[]{
                DateTimeFieldType.year(),
                DateTimeFieldType.monthOfYear(),
                DateTimeFieldType.dayOfMonth()

        }, new int[] {1969, 04, 12});

        my = new Partial(new DateTimeFieldType[]{
                DateTimeFieldType.year(),
                DateTimeFieldType.monthOfYear()

        }, new int[] {1969, 04});

        y = new Partial(new DateTimeFieldType[]{
                DateTimeFieldType.year()

        }, new int[] {1969});
    }

    @Test
    public void test_Partial_ISO8601_DATE() {
        PartialFormatter f = new PartialFormatter(DateTimeFormat.ISO8601_DATE);
        assertEquals("1969-04-12", f.print(dmy));
        assertEquals("1969-04", f.print(my));
        assertEquals("1969", f.print(y));
    }

    @Test
    public void test_Partial_DMY_DOT() {
        PartialFormatter f = new PartialFormatter(DateTimeFormat.DMY_DOT);
        assertEquals("12.04.1969", f.print(dmy));
        assertEquals("04.1969", f.print(my));
        assertEquals("1969", f.print(y));
    }

    @Test
    public void test_TimePeriod_ISO8601_DATE() {
        TimePeriodFormatter f = new TimePeriodFormatter(DateTimeFormat.ISO8601_DATE);

        TimePeriod p1 = TimePeriod.NewInstance();
        p1.setStart(my);
        assertEquals("1969-04", f.print(p1));

        TimePeriod p2 = TimePeriod.NewInstance();
        p2.setEnd(dmy);
        assertEquals("1969-04-12", f.print(p2));

        TimePeriod p3 = TimePeriod.NewInstance(my, dmy);
        assertEquals("1969-04 - 1969-04-12", f.print(p3));

        TimePeriod p4 = TimePeriod.NewInstance(my, dmy);
        p4.setFreeText("in April 1969");
        assertEquals("in April 1969", f.print(p4));
    }

    @Test
    public void test_TimePeriod_DMY_DOT() {
        TimePeriodFormatter f = new TimePeriodFormatter(DateTimeFormat.DMY_DOT);

        TimePeriod p1 = TimePeriod.NewInstance();
        p1.setStart(my);
        assertEquals("04.1969", f.print(p1));

        TimePeriod p2 = TimePeriod.NewInstance();
        p2.setEnd(dmy);
        assertEquals("12.04.1969", f.print(p2));

        TimePeriod p3 = TimePeriod.NewInstance(my, dmy);
        assertEquals("04.1969 - 12.04.1969", f.print(p3));

        TimePeriod p4 = TimePeriod.NewInstance(my, dmy);
        p4.setFreeText("in April 1969");
        assertEquals("in April 1969", f.print(p4));
    }

}
