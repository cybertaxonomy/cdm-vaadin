/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.session;

import java.io.Serializable;

import javax.sql.DataSource;

import org.hibernate.FlushMode;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate5.support.OpenSessionInViewFilter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;

import eu.etaxonomy.cdm.api.conversation.ConversationHolder;
import eu.etaxonomy.vaadin.mvp.AbstractEditorPresenter;

/**
 * The ViewScopeConversationHolder allows to span conversations over all request threads
 * that are involved in the creation, interaction and ending of a View.
 *
 * <p><b>NOTE</b>: Hibernate sessions created in the conversation created by this holder
 * will not fluish automatically. The flush mode is initially
 * set to {@code FlushMode.MANUAL}. It assumes to be used
 * in combination with service layer transactions that care for the flushing: The
 * active transaction manager will temporarily change the flush mode to
 * {@code FlushMode.AUTO} during a read-write transaction, with the flush
 * mode reset to {@code FlushMode.NEVER} at the end of each transaction.
 * This behavior is implemented consistently in the {@link {@link AbstractEditorPresenter} methods
 * {@link AbstractEditorPresenter#onEditorPreSaveEvent(eu.etaxonomy.vaadin.mvp.event.EditorPreSaveEvent) onEditorPreSaveEvent},
 * {@link AbstractEditorPresenter#onEditorSaveEvent(eu.etaxonomy.vaadin.mvp.event.EditorSaveEvent) onEditorSaveEvent} and
 * {@link AbstractEditorPresenter#onEditorDeleteEvent(eu.etaxonomy.vaadin.mvp.event.EditorDeleteEvent) onEditorDeleteEvent}
 * In this whole strategy this class follows the ideas of the {@link OpenSessionInViewFilter}.
 *
 * @author a.kohlbecker
 * @since Jun 30, 2017
 *
 */
@SpringComponent
@ViewScope
public class ViewScopeConversationHolder extends ConversationHolder implements Serializable {


    private static final long serialVersionUID = 1001768184000981106L;

    /**
     *
     */
    public ViewScopeConversationHolder() {
        super();
        applyDefaultSettings();

    }

    /**
     * @param dataSource
     * @param sessionFactory
     * @param transactionManager
     */
    public ViewScopeConversationHolder(DataSource dataSource, SessionFactory sessionFactory,
            PlatformTransactionManager transactionManager) {
        super(dataSource, sessionFactory, transactionManager, false);
        applyDefaultSettings();
    }

    private void applyDefaultSettings(){

        setDefaultFlushMode(FlushMode.MANUAL);
        TransactionDefinition definition = new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_NESTED);
        setDefinition(definition );
    }




}
