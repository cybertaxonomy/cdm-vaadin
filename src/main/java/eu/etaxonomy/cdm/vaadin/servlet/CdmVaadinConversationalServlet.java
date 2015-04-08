package eu.etaxonomy.cdm.vaadin.servlet;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.log4j.Logger;

import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionDestroyEvent;
import com.vaadin.server.SessionDestroyListener;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;

import eu.etaxonomy.cdm.api.conversation.ConversationHolder;
import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;


public class CdmVaadinConversationalServlet extends VaadinServlet implements SessionInitListener, SessionDestroyListener {

	private static final Logger logger = Logger.getLogger(CdmVaadinConversationalServlet.class);

	/**
	 *
	 */
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
		VaadinSession.getCurrent().setAttribute("conversation", conversation);
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
