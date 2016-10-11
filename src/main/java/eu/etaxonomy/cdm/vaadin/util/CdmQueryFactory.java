// $Id$
/**
* Copyright (C) 2015 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.util;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.vaadin.data.util.sqlcontainer.query.FreeformQuery;
import com.vaadin.data.util.sqlcontainer.query.QueryDelegate;

import eu.etaxonomy.cdm.model.common.Language;
import eu.etaxonomy.cdm.model.common.Representation;
import eu.etaxonomy.cdm.model.location.NamedArea;
import eu.etaxonomy.cdm.vaadin.statement.CdmStatementDelegate;

/**
 * @author cmathew
 * @date 1 Apr 2015
 *
 */
public class CdmQueryFactory {


	public static final String RANK_COLUMN = "Rank";
	public static final String TAXON_COLUMN = "Taxon";

    public static final String ID = "id";
    public static final String UUID_ID = "uuid";

    public static QueryDelegate generateTaxonBaseQuery(String name_id,
            String pb_id,
            String unp_id,
            String rank_id,
            String has_syn_id) throws SQLException {
        String FROM_QUERY = " FROM TaxonBase tb " +
                "INNER JOIN TaxonNode tn on tn.taxon_id=tb.id " +
                "INNER JOIN TaxonNameBase tnb on tb.name_id=tnb.id " +
                "INNER JOIN DefinedTermBase dtb on tnb.rank_id = dtb.id";
        String SELECT_QUERY="SELECT tb.id as " + ID +
                ", tb.uuid as " + UUID_ID +
                ", tnb.titleCache as " + name_id +
                ", tb.publish as " + pb_id +
                ", tb.unplaced as " + unp_id +
                ", dtb.titleCache as " + rank_id +
                ", (SELECT COUNT(*) FROM  SynonymRelationship sr WHERE tb.id = sr.relatedto_id) as " + has_syn_id +
                FROM_QUERY;
        String COUNT_QUERY = "SELECT count(*) " + FROM_QUERY;
        String CONTAINS_QUERY = "SELECT * FROM TaxonBase tb WHERE tb.id = ?";

        return generateQueryDelegate(SELECT_QUERY, COUNT_QUERY, CONTAINS_QUERY);
    }

    public static QueryDelegate generateTaxonDistributionQuery(List<Integer> taxonNodeIds, Collection<NamedArea> namedAreas, boolean abbreviatedLabels) throws SQLException {

    	String idString = "";
    	Iterator<Integer> nodeIterator = taxonNodeIds.iterator();
    	while (nodeIterator.hasNext()) {
			Integer integer = nodeIterator.next();
			idString += String.valueOf(integer);
			if(nodeIterator.hasNext()){
				idString += ", ";
			}
		}
        String FROM_QUERY =
                "FROM TaxonNameBase tnb "
                + "INNER JOIN TaxonBase tb on tnb.id = tb.name_id and tb.DTYPE='Taxon' " + // # name<->taxon
        "INNER JOIN TaxonNode tn on tn.taxon_id = tb.id "+
        "INNER JOIN DefinedTermBase rank on tnb.rank_id = rank.id "+// # rank <-> name
        "LEFT OUTER JOIN DescriptionBase descr on descr.taxon_id = tb.id "+// # taxon <-> taxon description (not every taxon has a description)
        "LEFT OUTER JOIN DescriptionElementBase descrEl on descrEl.indescription_id = descr.id and descrEl.DTYPE = 'Distribution' "+// # distribution <-> description
        "LEFT OUTER JOIN DefinedTermBase statusTerm on statusTerm.id = descrEl.status_id "+
        "LEFT OUTER JOIN DefinedTermBase area on area.id = descrEl.area_id "+
        "WHERE tn.id IN ("+ idString +") ";

        String GROUP_BY = " GROUP BY tb.uuid, tn.id ";

        String ORDER_BY = " ORDER BY tb.titleCache ";

        String SELECT_QUERY= "SELECT "
                + "tb.DTYPE, "
                + "tb.id, "
                + "tb.uuid, "
                + "tn.classification_id, "+
        		"tb.titleCache AS "+TAXON_COLUMN+", " +
        		"rank.titleCache AS "+RANK_COLUMN+", ";

        for(NamedArea namedArea : namedAreas){
            String label = null;
            Representation representation = namedArea.getRepresentation(Language.DEFAULT());
            if(representation!=null){
            	if(abbreviatedLabels){
            		label = representation.getAbbreviatedLabel();
            	}
            	else{
            		label = representation.getLabel();
            	}
            }
            if(label==null){
            	label = namedArea.getTitleCache();
            }
            SELECT_QUERY += "MAX( IF(area.titleCache = '"+ namedArea.getTitleCache() +"', statusTerm.titleCache, NULL) ) as '"+ label +"'," ;
        }
        SELECT_QUERY = StringUtils.stripEnd(SELECT_QUERY, ",")+" ";
        SELECT_QUERY= SELECT_QUERY + FROM_QUERY + GROUP_BY + ORDER_BY;
        String COUNT_QUERY = "SELECT count(DISTINCT tb.id)" + FROM_QUERY;
        String CONTAINS_QUERY = "SELECT * FROM TaxonBase tb WHERE tb.uuid = ?";
        //Escape SQL control character '
        Pattern p = Pattern.compile("(\\w+)'(\\w+)");
        Matcher m = p.matcher(SELECT_QUERY);
        if (m.find()) {
            SELECT_QUERY = m.replaceAll("$1\\\\'$2");
        }
        return generateQueryDelegate(SELECT_QUERY, COUNT_QUERY, CONTAINS_QUERY);
    }

    public static QueryDelegate generateSynonymofTaxonQuery(String name_id) throws SQLException {
    	String FROM_QUERY = " FROM TaxonBase tb " +
    			"INNER JOIN TaxonNameBase tnb on tb.name_id=tnb.id " +
    			"INNER JOIN SynonymRelationship sr on tb.id=sr.relatedfrom_id ";
    	String SELECT_QUERY="SELECT tb.id as " + ID +
    			", tnb.titleCache as " + name_id +
    			FROM_QUERY;
    	String COUNT_QUERY = "SELECT count(*) " + FROM_QUERY;
    	String CONTAINS_QUERY = "SELECT * FROM SynonymRelationship sr WHERE sr.relatedfrom_id = ?";

    	return generateQueryDelegate(SELECT_QUERY, COUNT_QUERY, CONTAINS_QUERY);
    }

    public static QueryDelegate generateTaxonRelatedToQuery(String reluuid_id,
            String reltype_id,
            String to_id,
            String touuid_id,
            String toname_id) throws SQLException {
        String FROM_QUERY = "     FROM TaxonRelationship tr " +
                "INNER JOIN TaxonBase tb on tr.relatedto_id = tb.id " +
                "INNER JOIN TaxonNode tn on tb.id = tn.taxon_id ";
        String SELECT_QUERY= "SELECT tr.id as " + ID +
                ", tr.uuid as " + reluuid_id +
                ", tr.type_id as " + reltype_id +
                ", tr.relatedto_id as " + to_id +
                ", tb.uuid as " + touuid_id +
                ", tb.titleCache as " + toname_id +
                FROM_QUERY;
        String COUNT_QUERY = "SELECT count(*) " + FROM_QUERY;
        String CONTAINS_QUERY = "SELECT * FROM TaxonRelationship tr where tr.relatedfrom_id = ?";

        return generateQueryDelegate(SELECT_QUERY, COUNT_QUERY, CONTAINS_QUERY);
    }

    /**
     * Creates a FreeformQuery which mimics a TableQuery.
     * This method works around the bug at http://dev.vaadin.com/ticket/12370
     *
     * @param tableName
     * @return
     * @throws SQLException
     */
    public static QueryDelegate generateTableQuery(String tableName) throws SQLException {
        String FROM_QUERY = " FROM " + tableName;
        String SELECT_QUERY=" SELECT * " +
                FROM_QUERY;
        String COUNT_QUERY = "SELECT count(*) " + FROM_QUERY;
        String CONTAINS_QUERY = "SELECT * FROM " + tableName + "  WHERE id = ?";

        return generateQueryDelegate(SELECT_QUERY, COUNT_QUERY, CONTAINS_QUERY);
    }

    public static QueryDelegate generateQueryDelegate(String SELECT_QUERY, String COUNT_QUERY, String CONTAINS_QUERY) throws SQLException {
        FreeformQuery query = new FreeformQuery("This query is not used", CdmSpringContextHelper.getCurrent().getConnectionPool(), ID);
        CdmStatementDelegate delegate = new CdmStatementDelegate(SELECT_QUERY, COUNT_QUERY, CONTAINS_QUERY);
        query.setDelegate(delegate);
        return query;
    }

}
