/**
 * Copyright (C) 2015 EDIT
 * European Distributed Institute of Taxonomy
 * http://www.e-taxonomy.eu
 *
 * The contents of this file are subject to the Mozilla Public License Version 1.1
 * See LICENSE.TXT at the top of this package for the full license terms.
 */

package eu.etaxonomy.cdm.vaadin.servlet;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.log4j.Logger;

import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionDestroyEvent;
import com.vaadin.server.SessionDestroyListener;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.server.SpringVaadinServlet;

import eu.etaxonomy.cdm.api.conversation.ConversationHolder;
import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;
import eu.etaxonomy.cdm.vaadin.util.DistributionEditorUtil;


/**
 * This class is part of the strategy to solve the lazy loading problem
 * and in more general to manage sessions properly.
 * <p>
 * Conversational Sessions essentially involve linking any UI which requires
 * long running sessions to a {@link CdmVaadinConversationalServlet}.
 * The servlet creates an instance of the ConversationHolder when a VaadinSession
 * is initialized and binds it on every service call, ensuring that a single
 * hibernate session is attached to a corresponding vaadin session.
 * <b>NOTE</b>: One major issue with this strategy is the bug (#4528) which
 * flushes the entire session even if a save / saveOrUpdate call is made on a
 * single CDM entity. This implies that this strategy is safe to use only in
 * 'session-save' UIs and not 'auto-save' UIs.
 *
 * @author c.mathew
 *
 */
public class CdmVaadinConversationalServlet extends SpringVaadinServlet implements SessionInitListener, SessionDestroyListener {

	private static final Logger logger = Logger.getLogger(CdmVaadinConversationalServlet.class);

	private static final long serialVersionUID = -2973231251266766766L;

	private ConversationHolder conversation;


	@Override
	protected void servletInitialized() throws ServletException {
		super.servletInitialized();
		getService().addSessionInitListener(this);
		getService().addSessionDestroyListener(this);
	}

	@Override
	public void sessionInit(SessionInitEvent event)
			throws ServiceException {
		conversation = (ConversationHolder) CdmSpringContextHelper.getCurrent().getBean("conversationHolder");
		conversation.bind();
		VaadinSession.getCurrent().setAttribute(DistributionEditorUtil.SATTR_CONVERSATION, conversation);
	}

	@Override
	public void sessionDestroy(SessionDestroyEvent event) {
		conversation.close();
	}

	@Override
	protected void service(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws ServletException, IOException {
		if(conversation != null) {
			logger.info("Servlet Service call - Binding Vaadin Session Conversation : " + conversation);
			conversation.bind();
		}

		super.service(request, response);
	}


}
