
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import ru.yandex.qatools.ashot.Screenshot;



public class VisualTest extends BaseTest {

    @Test
    public void loginPageTest () throws Exception {

        /*//Take ScreenShot with AShot
        Screenshot loginPageScreenShot = takeScreenshot();

        //Write actual screenshot to the actual screenshot path
        writeScreenshotToFolder(loginPageScreenShot, actualImageFile);

        //Do image comparison
        doComparison(loginPageScreenShot);*/
        makeComparison();

    }

    //Close Driver
    @AfterClass
    public void quitDriver() {
        driver.quit();
    }
}