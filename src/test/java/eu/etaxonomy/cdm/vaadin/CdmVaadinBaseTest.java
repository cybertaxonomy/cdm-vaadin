package eu.etaxonomy.cdm.vaadin;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.ContextLoaderListener;
import org.unitils.UnitilsJUnit4;
import org.unitils.database.DatabaseUnitils;
import org.unitils.database.annotations.Transactional;
import org.unitils.database.util.TransactionMode;

import com.vaadin.data.util.sqlcontainer.query.generator.filter.QueryBuilder;
import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletService;
import com.vaadin.server.VaadinSession;

import eu.etaxonomy.cdm.vaadin.util.CdmSQLStringDecorator;
import unitils.AlternativeUnitilsJUnit4TestClassRunner;

@Ignore
@RunWith(AlternativeUnitilsJUnit4TestClassRunner.class)
@Transactional(TransactionMode.DISABLED)
public class CdmVaadinBaseTest extends UnitilsJUnit4 {

    private static MockServletContext servletContext;
    private static VaadinServlet vaadinServlet;
    private static VaadinServletService vaadinService;
    private static VaadinSession vaadinSession;
    private static boolean isVaadinServletEnvCreated = false;


    @BeforeClass
    public static void setup() {
        DatabaseUnitils.disableConstraints();

    	if(!isVaadinServletEnvCreated) {
    		createNewServletEnvironment();
    	}
    	QueryBuilder.setStringDecorator(new CdmSQLStringDecorator());

    	Assert.assertNotNull(vaadinServlet);
    	Assert.assertEquals(vaadinServlet, VaadinServlet.getCurrent());

    	Assert.assertNotNull(servletContext);
        Assert.assertEquals(servletContext, VaadinServlet.getCurrent().getServletContext());

        Assert.assertNotNull(vaadinSession);
        Assert.assertEquals(vaadinSession, VaadinSession.getCurrent());

        Assert.assertNotNull(vaadinService);
        Assert.assertEquals(vaadinService, VaadinService.getCurrent());
    }


    public static void createNewServletEnvironment() {
		servletContext = new MockServletContext("/webapp");

        ServletContextListener listener = new ContextLoaderListener();
        ServletContextEvent event = new ServletContextEvent(servletContext);
		listener.contextInitialized(event);

		MockServletConfig servletConfig = new MockServletConfig(servletContext);
		vaadinServlet = new VaadinServlet();


		try {
			vaadinServlet.init(servletConfig);
		} catch (ServletException e) {
			throw new RuntimeException(e);
		}

		try {
			vaadinService = new VaadinServletService(vaadinServlet,
			        EasyMock.createMock(DeploymentConfiguration.class));
		} catch (ServiceException e) {
			throw new RuntimeException(e);
		}
		VaadinService.setCurrent(vaadinService);

		vaadinSession = new MockVaadinSession(vaadinService);
		VaadinSession.setCurrent(vaadinSession);

		isVaadinServletEnvCreated = true;
    }

    public static  class MockVaadinSession extends VaadinSession {
        MockVaadinSession(VaadinService service) {
            super(service);
        }

        @Override
        public boolean hasLock() {
            return true;
        }
    }
}
