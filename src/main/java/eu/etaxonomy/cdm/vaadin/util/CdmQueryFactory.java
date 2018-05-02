/**
* Copyright (C) 2015 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.vaadin.data.util.sqlcontainer.query.FreeformQuery;
import com.vaadin.data.util.sqlcontainer.query.QueryDelegate;

import eu.etaxonomy.cdm.common.CdmUtils;
import eu.etaxonomy.cdm.model.common.Language;
import eu.etaxonomy.cdm.model.common.Representation;
import eu.etaxonomy.cdm.model.location.NamedArea;
import eu.etaxonomy.cdm.vaadin.statement.CdmStatementDelegate;

/**
 * @author cmathew
 * @since 1 Apr 2015
 *
 */
public class CdmQueryFactory {


	public static final String DTYPE_COLUMN = "DTYPE";
	public static final String ID_COLUMN = "ID";
	public static final String UUID_COLUMN = "UUID";
	public static final String CLASSIFICATION_COLUMN = "CLASSIFICATION";
	public static final String RANK_COLUMN = "RANK";
	public static final String TAXON_COLUMN = "TAXON";

    public static final String ID = "ID";
    public static final String UUID_ID = "UUID";

    public static QueryDelegate generateTaxonTreeQuery(String name_id, String classificationId)  {
        String FROM_QUERY = " FROM TaxonBase tb " +
                "INNER JOIN TaxonNode tn on tn.taxon_id=tb.id " +
                "INNER JOIN TaxonName n on tb.name_id=n.id " +
                "INNER JOIN Classification cl on cl.id=tn.classification_id and cl.id='"+classificationId+"'";
        String SELECT_QUERY="SELECT tn.id as " + ID +
                ", tb.uuid as " + UUID_ID +
                ", n.titleCache as " + name_id +
                ", tn.parent_id as parent" +
                FROM_QUERY;
        String COUNT_QUERY = "SELECT count(*) " + FROM_QUERY;
        String CONTAINS_QUERY = "SELECT * FROM TaxonBase tb WHERE tb.id = ?";

        return generateQueryDelegate(SELECT_QUERY, COUNT_QUERY, CONTAINS_QUERY);
    }

    public static QueryDelegate generateTaxonBaseQuery(String name_id,
            String pb_id,
            String unp_id,
            String rank_id,
            String has_syn_id)  {
        String FROM_QUERY = " FROM TaxonBase tb " +
                "INNER JOIN TaxonNode tn on tn.taxon_id=tb.id " +
                "INNER JOIN TaxonName n on tb.name_id=n.id " +
                "INNER JOIN DefinedTermBase dtb on n.rank_id = dtb.id";
        String SELECT_QUERY="SELECT tb.id as " + ID +
                ", tb.uuid as " + UUID_ID +
                ", n.titleCache as " + name_id +
                ", tb.publish as " + pb_id +
                ", tn.unplaced as " + unp_id +
                ", dtb.titleCache as " + rank_id +
                ", (SELECT COUNT(*) FROM TaxonBase syn WHERE tb.id = syn.acceptedTaxon_id) as " + has_syn_id +
                FROM_QUERY;
        String COUNT_QUERY = "SELECT count(*) " + FROM_QUERY;
        String CONTAINS_QUERY = "SELECT * FROM TaxonBase tb WHERE tb.id = ?";

        return generateQueryDelegate(SELECT_QUERY, COUNT_QUERY, CONTAINS_QUERY);
    }

    public static QueryDelegate generateTaxonDistributionQuery(List<Integer> taxonNodeIds, Collection<NamedArea> namedAreas) {

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
                " FROM TaxonName n "
                + "INNER JOIN TaxonBase tb on n.id = tb.name_id and tb.DTYPE='Taxon' " + // # name<->taxon
        "INNER JOIN TaxonNode tn on tn.taxon_id = tb.id "+
        "INNER JOIN DefinedTermBase rank on n.rank_id = rank.id "+// # rank <-> name
        "LEFT OUTER JOIN DescriptionBase descr on descr.taxon_id = tb.id "+// # taxon <-> taxon description (not every taxon has a description)
        "LEFT OUTER JOIN DescriptionElementBase descrEl on descrEl.indescription_id = descr.id and descrEl.DTYPE = 'Distribution' "+// # distribution <-> description
        "LEFT OUTER JOIN DefinedTermBase statusTerm on statusTerm.id = descrEl.status_id "+
        "LEFT OUTER JOIN DefinedTermBase area on area.id = descrEl.area_id ";
        if(CdmUtils.isNotBlank(idString)){
        	FROM_QUERY += " WHERE tn.id IN ("+ idString +") ";
        }

        String GROUP_BY = " GROUP BY tb.uuid, tn.id ";

        String ORDER_BY = " ORDER BY tb.titleCache ";

        String SELECT_QUERY= "SELECT "
                + "tb.DTYPE AS "+DTYPE_COLUMN+", "
                + "tb.id AS "+ID_COLUMN+", "
                + "tb.uuid AS "+UUID_COLUMN+", "
                + "tn.classification_id AS "+CLASSIFICATION_COLUMN+", "+
        		"tb.titleCache AS "+TAXON_COLUMN+", " +
        		"rank.titleCache AS "+RANK_COLUMN+", ";

        Map<String, Integer> labels = new HashMap<>();
        for(NamedArea namedArea : namedAreas){
            String label = null;
            String fullLabel = null;
            String abbreviatedLabel = null;
            Representation representation = namedArea.getRepresentation(Language.DEFAULT());
            if(representation!=null){
            	fullLabel = representation.getLabel();
				abbreviatedLabel = representation.getAbbreviatedLabel();
				if(DistributionEditorUtil.isAbbreviatedLabels()){
            		label = abbreviatedLabel;
            	}
            	else{
            		label = fullLabel;
            	}
            }
            //fallback
            if(label==null){
            	label = namedArea.getTitleCache();
            }

            //check if label already exists
            Integer count = labels.get(label);
            if(count!=null){
            	//combine label and abbreviated and check again
            	if(abbreviatedLabel!=null && fullLabel!= null){
            		label = abbreviatedLabel+"-"+fullLabel;
            	}
            }
            count = labels.get(label);
            if(count==null){
            	labels.put(label, 1);
            }
            else{
            	labels.put(label, count+1);
            	label += "("+count+")";
            }
            SELECT_QUERY += "MAX( IF(area.uuid = '"+ namedArea.getUuid() +"', statusTerm.uuid, NULL) ) as '"+ label +"'," ;
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

    public static QueryDelegate generateSynonymOfTaxonQuery(String name_id)  {
    	String FROM_QUERY = " FROM TaxonBase tb " +
    			"INNER JOIN TaxonName n on tb.name_id=n.id " +
    			"INNER JOIN TaxonBase acc on tb.acceptedTaxon_id = acc.id "; //or s.id = ?
    	String SELECT_QUERY="SELECT tb.id as " + ID +
    			", n.titleCache as " + name_id +
    			FROM_QUERY;
    	String COUNT_QUERY = "SELECT count(*) " + FROM_QUERY;
    	String CONTAINS_QUERY = "SELECT * FROM TaxonBase syn WHERE syn.id = ?"; //or s.id = ?

    	return generateQueryDelegate(SELECT_QUERY, COUNT_QUERY, CONTAINS_QUERY);
    }

    public static QueryDelegate generateTaxonRelatedToQuery(String reluuid_id,
            String reltype_id,
            String to_id,
            String touuid_id,
            String toname_id)  {
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
     */
    public static QueryDelegate generateTableQuery(String tableName) {
        String FROM_QUERY = " FROM " + tableName;
        String SELECT_QUERY=" SELECT * " +
                FROM_QUERY;
        String COUNT_QUERY = "SELECT count(*) " + FROM_QUERY;
        String CONTAINS_QUERY = "SELECT * FROM " + tableName + "  WHERE id = ?";

        return generateQueryDelegate(SELECT_QUERY, COUNT_QUERY, CONTAINS_QUERY);
    }

    public static QueryDelegate generateQueryDelegate(String SELECT_QUERY, String COUNT_QUERY, String CONTAINS_QUERY) {
        FreeformQuery query = new FreeformQuery("This query is not used", CdmSpringContextHelper.getCurrent().getConnectionPool(), ID);
        CdmStatementDelegate delegate = new CdmStatementDelegate(SELECT_QUERY, COUNT_QUERY, CONTAINS_QUERY);
        query.setDelegate(delegate);
        return query;
    }
}
