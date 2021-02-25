/**
* Copyright (C) 2021 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.name;

import java.util.Optional;

import eu.etaxonomy.cdm.model.name.TypeDesignationStatusBase;

/**
 * @author a.kohlbecker
 * @since Feb 25, 2021
 */
public interface NomenclaturalActContext {


    /**
     * see {@link #isInTypedesignationOnlyAct()}.
     */
    public void setInTypedesignationOnlyAct(Optional<Boolean> isInTypedesignationOnlyAct);

    /**
     * Possible values of the Optional:
     *
     * <ul>
     * <li><code>!isPresent()</code> (value = NULL): undecided, is treated by {@link #checkInTypeDesignationOnlyAct()} like <code>false</code>.
     * This for example can happen in cases when the typified name misses the nomenclatural reference.</li>
     * <li><code>TRUE</code>: the typification is published in an nomenclatural act in which no new name or new combination is being published.
     * Consequently for example {@link TypeDesignationStatusBase} terms available in an editor should be limited to those with
     * <code>{@link TypeDesignationStatusBase#hasDesignationSource() hasDesignationSource} == true</code>. Other consequences depend on the
     * type of designation and editor.</li>
     * <li><code>FALSE</code>: In the example from above all with <code>{@link TypeDesignationStatusBase}</code> terms are allowed.</li>
     * </ul>
     */
    public Optional<Boolean> isInTypedesignationOnlyAct();

    /**
     * Interprets the value of the Optional according to the rules described in {@link #isInTypedesignationOnlyAct()} and returns
     * an easy to use <code>boolean</code> value.
     */
    public default boolean checkInTypeDesignationOnlyAct() {
        return !isInTypedesignationOnlyAct().isPresent()
                || isInTypedesignationOnlyAct().get();
    }

}