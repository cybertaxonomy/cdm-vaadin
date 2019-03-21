package eu.etaxonomy.cdm;


import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.codeborne.selenide.WebDriverRunner;

@RunWith(SpringJUnit4ClassRunner.class)
// @SpringBootTest only since spring 4.0
@SpringApplicationConfiguration(
        classes = {WebAppIntegrationTestConfig.class},
        locations=
        // "file:./src/main/webapp/WEB-INF/applicationContext-dbunit.xml"
        // loads the root context which also will provide the CdmVaadinConfiguration.CdmVaadinServlet
        "file:./src/main/webapp/WEB-INF/applicationContext.xml"
)
// the datasource to be used in the tests is defined in:
@TestPropertySource("classpath:selenide-integration-test.properties")
@WebIntegrationTest(randomPort=true, value={
    // "server.port=8087", // does not work this way , so it is hard coded in WebAppIntegrationTestConfig
    // #################################
    // The below way to set the properties for the test does not work with spring boot 1.3.1 spring 4.2.4
    // he only way to set the properties is to use the @TestPropertySource to load the properties from a location
    // "user.home=./target/",
    //"cdm.beanDefinitionFile=./src/test/resources/datasources.xml",
    //"cdm.datasource=h2_cdmTest"
    }
)
//@DirtiesContext // this is a reminder that this annotation can be used.
public class SelenideTestBase {

    // @Value("${local.server.port}")
    // this will not work since the AutowiredAnnotationBeanPostProcessor will
    // try resolve this value before ServerPortInfoApplicationContextInitializer has set it.
    private int localServerPort = -1 ;
    private int managementPort = -1;
    private String managementContextPath = null;

    // ------------------------------------------------------------------------
    // replacement for failing @Value("${local.server.port}", etc
    @Autowired
    Environment environment;
    protected int localServerPort() {
        if(localServerPort < 0){
            localServerPort = environment.getProperty("local.server.port", Integer.class);
        }
        return localServerPort;
    }
    protected int managementPort() {
        if(localServerPort < 0){
            managementPort = environment.getProperty("management.port", Integer.class);
        }
        return managementPort;
    }
    protected String managementContextPath() {
        if(managementContextPath == null) {
            managementContextPath = environment.getProperty("management.context-path");
        }
        return managementContextPath;
    }
    // ------------------------------------------------------------------------

    protected static WebDriver driver;

    @BeforeClass
    public static void init() {
        System.setProperty("webdriver.chrome.driver", "selenium/bin/linux/googlechrome/64bit/chromedriver");
        driver = new ChromeDriver();
        WebDriverRunner.setWebDriver(driver);
    }

    @AfterClass
    public static void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}