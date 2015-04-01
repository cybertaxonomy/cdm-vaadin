package eu.etaxonomy.cdm.vaadin.container;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.vaadin.data.Item;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.RowItem;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.QueryDelegate;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;

import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;

public class CdmSQLContainer extends SQLContainer {

    private static final Logger logger = Logger.getLogger(CdmSQLContainer.class);

    JDBCConnectionPool pool;

    private int contentChangedEventsDisabledCount = 0;

    private boolean contentsChangedEventPending;

    private final Map<RowId, RowItem> addedItems = new HashMap<RowId, RowItem>();

    public CdmSQLContainer(QueryDelegate delegate) throws SQLException {
        super(delegate);
    }

    public static CdmSQLContainer newInstance(String tableName) throws SQLException {
        // TODO : currently the sql generator is for h2, need to make this compatible for all flavours
        TableQuery tq = new TableQuery(tableName,CdmSpringContextHelper.getConnectionPool());
        tq.setVersionColumn("updated");
        return new CdmSQLContainer(tq);

    }

    @Override
    public Item getItem(Object itemId) {
        RowItem rowItem = addedItems.get(itemId);
        if(rowItem != null) {
            return rowItem;
        } else {
            return super.getItem(itemId);
        }
    }

    public void addRowItem(RowItem rowItem) {
        addedItems.put(rowItem.getId(), rowItem);
    }

    @Override
    protected void fireContentsChange() {
        if (contentsChangeEventsOn()) {
            disableContentsChangeEvents();
            try {
                super.fireContentsChange();
            } finally {
                enableContentsChangeEvents();
            }
        } else {
            contentsChangedEventPending = true;
        }
    }

    protected boolean contentsChangeEventsOn() {
        return contentChangedEventsDisabledCount == 0;
    }

    protected void disableContentsChangeEvents() {
        contentChangedEventsDisabledCount++;
    }

    protected void enableContentsChangeEvents() {
        if (contentChangedEventsDisabledCount <= 0) {
            logger.warn("Mismatched calls to disable and enable contents change events in HierarchicalContainer");
            contentChangedEventsDisabledCount = 0;
        } else {
            contentChangedEventsDisabledCount--;
        }
        if (contentChangedEventsDisabledCount == 0) {
            if (contentsChangedEventPending) {
                //fireContentsChange();
            }
            contentsChangedEventPending = false;
        }
    }

}
