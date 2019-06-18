import com.assertthat.selenium_shutterbug.core.Shutterbug;
import com.assertthat.selenium_shutterbug.core.Snapshot;
import com.assertthat.selenium_shutterbug.utils.web.ScrollStrategy;
import com.google.common.io.Files;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.io.FileUtils;
import org.im4java.core.CompareCmd;
import org.im4java.core.IMOperation;
import org.im4java.process.ProcessStarter;
import org.im4java.process.StandardStream;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.comparison.ImageDiff;
import ru.yandex.qatools.ashot.comparison.ImageDiffer;
import ru.yandex.qatools.ashot.coordinates.WebDriverCoordsProvider;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by onurb on 06-Feb-17.
 */
public class BaseTest {

    public WebDriver driver;
    public WebDriverWait wait;
    public JavascriptExecutor js;

    //JSWaiter object
    JSWaiter jsWaiter;

    //Test name
    public String testName;

    //Test Screenshot directory
    public String testScreenShotDirectory;

    //URL of the test website
    public String url = "https://kempinski.dev.concilioinsights.com";

    //Main Directory of the test code
    public String currentDir = System.getProperty("user.dir");

    //Main screenshot directory
    public String parentScreenShotsLocation = currentDir +"/"+ "ScreenShots";

    //Main differences directory
    public String parentDifferencesLocation = currentDir +"/"+ "Differences";

    //Element screenshot paths
    public String baselineScreenShotPath;
    public String actualScreenShotPath;
    public String differenceScreenShotPath;

    //Image files
    public File baselineImageFile;
    public File actualImageFile;
    public File differenceImageFile;
    public File differenceFileForParent;

    //Setup Driver
    @BeforeClass
    public void setupTestClass() throws IOException {
        //Declare Firefox driver
        //System.setProperty("webdriver.chrome.driver", "drivers/chromedriver");
        //driver = new ChromeDriver();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--start-maximized");
        //options.addArguments("--start-fullscreen");
        //options.addArguments("--headless");
        driver = new ChromeDriver(options);
       /* FirefoxOptions options = new FirefoxOptions();
        options.setBinary("/Applications/Firefox.app/Contents/MacOS/firefox-bin");
        DesiredCapabilities capabilities = DesiredCapabilities.firefox();
        capabilities.setCapability("moz:firefoxOptions", options);
        capabilities.setCapability("marionatte", false);
        System.setProperty("webdriver.gecko.driver", "drivers/geckodriver");
        driver = new FirefoxDriver(options);*/
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);


        //Maximize the browser
        //driver.manage().window().maximize();

        //Declare a 10 seconds wait time
        wait = new WebDriverWait(driver,10);

        //JS Executor
        js = (JavascriptExecutor) driver;

        //JSWaiter
        jsWaiter = new JSWaiter(wait);

        //Create screenshot and differences folders if they are not exist
        createFolder(parentScreenShotsLocation);
        createFolder(parentDifferencesLocation);

        //Clean Differences Root Folder
        File differencesFolder = new File(parentDifferencesLocation);
        FileUtils.cleanDirectory(differencesFolder);

        //Go to URL
        driver.navigate().to(url);

        //Add Cookie for top banner
        addCookieforTopBanner();
    }

    @BeforeMethod
    public void setupTestMethod (Method method) {
        //Get the test name to create a specific screenshot folder for each test.
        testName = method.getName();
        System.out.println("Test Name: " + testName + "\n");

        //Create a specific directory for a test
        testScreenShotDirectory = parentScreenShotsLocation + testName + "/";
        createFolder(testScreenShotDirectory);

        //Declare element screenshot paths
        //Concatenate with the test name.
        declareScreenShotPaths(testName+"_Baseline.png", testName+"_Actual.png", testName + "_Diff.png");
    }

    //Add Cookie not to see top banner animation
    public void addCookieforTopBanner () {
        //Get Next Month Last Date for cookie expiration
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date nextMonthLastDay = calendar.getTime();

        //Create/Build a cookie
        Cookie topBannerCloseCookie = new Cookie.Builder("AA-kobiBannerClosed","4") //Name & value pair of the cookie
                .domain("www.kariyer.net") //Domain of the cookie
                .path("/") //Path of the cookie
                .expiresOn(nextMonthLastDay) //Expiration date
                .build(); //Finally build it with .build() call

        //Add a cookie
        driver.manage().addCookie(topBannerCloseCookie);
    }

    //Create Folder Method
    public void createFolder (String path) {
        File testDirectory = new File(path);
        if (!testDirectory.exists()) {
            if (testDirectory.mkdir()) {
                System.out.println("Directory: " + path + " is created!" );
            } else {
                System.out.println("Failed to create directory: " + path);
            }
        } else {
            System.out.println("Directory already exists: " + path);
        }
    }

    //Close popup if exists
   /* public void handlePopup(String selector) throws InterruptedException {
        jsWaiter.waitJS();
        List<WebElement> popup = driver.findElements(By.cssSelector(selector));
        if(!popup.isEmpty()){
            popup.get(0).click();
            sleep(200);
        }
    }*/

   /* //Close Banner
    public void closeBanner () {
        jsWaiter.waitJS();
        List<WebElement> banner = driver.findElements(By.cssSelector("body > div.kobi-head-banner > div > a"));
        if (!banner.isEmpty()) {
            banner.get(0).click();
            //Wait for 2 second for closing banner
            sleep(2000);
        }
    }*/

   /* //Unhide an Element with JSExecutor
    public void unhideElement (String unhideJS) {
        js.executeScript(unhideJS);
        jsWaiter.waitJS();
        sleep(200);
    }*/

    /*//Move to Operation
    public void moveToElement (WebElement element){
        jsWaiter.waitJS();
        Actions actions = new Actions(driver);
        jsWaiter.waitJS();
        sleep(200);
        actions.moveToElement(element).build().perform();
    }*/


    //Take Screenshot with AShot
    public Screenshot takeScreenshot () {
        //Take screenshot with Ashot
        //Screenshot elementScreenShot = new AShot().takeScreenshot(driver, element);
        /*Screenshot elementScreenShot = new AShot()
                .coordsProvider(new WebDriverCoordsProvider())
                .takeScreenshot(driver,element);*/
        Screenshot elementScreenShot = new AShot().shootingStrategy(ShootingStrategies.viewportRetina(100, 0, 0, 2)).takeScreenshot(driver);


        //Print element size
        String size = "Height: " + elementScreenShot.getImage().getHeight() + "\n" +
                "Width: " + elementScreenShot.getImage().getWidth() + "\n";
        System.out.print("Size: " + size);

        return elementScreenShot;
    }
    public void takeNewScreenshot() throws Exception {
        //Take screenshot with Ashot
        //Screenshot elementScreenShot = new AShot().takeScreenshot(driver, element);
        /*Screenshot elementScreenShot = new AShot()
                .coordsProvider(new WebDriverCoordsProvider())
                .takeScreenshot(driver,element);*/
     //Snapshot screenshot = Shutterbug.shootPage(driver, ScrollStrategy.WHOLE_PAGE,500,true).withName(testName + "_Actual.png");
        File image = new File("/Users/vitaliybizilia/IdeaProjects/pixel_perfect/ScreenShots/Actual.png");
        BufferedImage expectedImage = ImageIO.read(image);
        boolean status = Shutterbug.shootPage(driver,ScrollStrategy.WHOLE_PAGE,500,true).withName("Expected").equals(expectedImage,0.1);
        Assert.assertTrue(status);



        //Print element size
        /*String size = "Height: " + elementScreenShot.getImage().getHeight() + "\n" +
                "Width: " + elementScreenShot.getImage().getWidth() + "\n";
        System.out.print("Size: " + size);*/

    }

    //Write
    public void writeScreenshotToFolder (Screenshot screenshot, File imageFile) throws IOException {
        ImageIO.write(screenshot.getImage(), "PNG", imageFile);
    }

    //Screenshot paths
    public void declareScreenShotPaths (String baseline, String actual, String diff) {
        //BaseLine, Actual, Difference Photo Paths
        baselineScreenShotPath = testScreenShotDirectory + baseline;
        actualScreenShotPath = testScreenShotDirectory + actual;
        differenceScreenShotPath = testScreenShotDirectory + diff;

        //BaseLine, Actual Photo Files
        baselineImageFile = new File(baselineScreenShotPath);
        actualImageFile = new File(actualScreenShotPath);
        differenceImageFile = new File (differenceScreenShotPath);

        //For copying difference to the parent Difference Folder
        differenceFileForParent = new File (parentDifferencesLocation + diff);
    }

    //ImageMagick Compare Method
    public void compareImagesWithImageMagick (String expected, String actual, String difference) throws Exception {
        // This class implements the processing of os-commands using a ProcessBuilder.
        // This is the core class of the im4java-library where all the magic takes place.
        //ProcessStarter.setGlobalSearchPath("C:\\Program Files\\ImageMagick-7.0.4-Q16");

        // This instance wraps the compare command
        CompareCmd compare = new CompareCmd();

        // Set the ErrorConsumer for the stderr of the ProcessStarter.
        compare.setErrorConsumer(StandardStream.STDERR);

        // Create ImageMagick Operation Object
        IMOperation cmpOp = new IMOperation();

        //Add option -fuzz to the ImageMagick commandline
        //With Fuzz we can ignore small changes
        cmpOp.fuzz(10.0);

        //The special "-metric" setting of 'AE' (short for "Absolute Error" count), will report (to standard error),
        //a count of the actual number of pixels that were masked, at the current fuzz factor.
        cmpOp.metric("AE");

        // Add the expected image
        cmpOp.addImage(expected);

        // Add the actual image
        cmpOp.addImage(actual);

        // This stores the difference
        cmpOp.addImage(difference);

        try {
            //Do the compare
            System.out.println ("Comparison Started!");
            compare.run(cmpOp);
            System.out.println ("Comparison Finished!");
        }
        catch (Exception ex) {
            System.out.print(ex);
            System.out.println ("Comparison Failed!");
            //Put the difference image to the global differences folder
            Files.copy(differenceImageFile,differenceFileForParent);
            throw ex;
        }
    }
    public void compareByAnotherService() throws IOException {
    BufferedImage expectedImage = ImageIO.read(new File(System.getProperty("user.dir") +"/"+testName+"_Baseline.png"));
    Screenshot elementScreenShot = new AShot().shootingStrategy(ShootingStrategies.viewportRetina(100, 0, 0, 2)).takeScreenshot(driver);
    BufferedImage actualImage = elementScreenShot.getImage();

    ImageDiffer imgDiff = new ImageDiffer();
    ImageDiff diff = imgDiff.makeDiff(actualImage, expectedImage);
        Assert.assertFalse(diff.hasDiff(),"Images are Same");}



    //Compare Operation
    public void doComparison (Screenshot elementScreenShot) throws Exception {
        //Did we capture baseline image before?
        if (baselineImageFile.exists()){
            //Compare screenshot with baseline
            System.out.println("Comparison method will be called!\n");

            System.out.println("Baseline: " + baselineScreenShotPath + "\n" +
                    "Actual: " + actualScreenShotPath + "\n" +
                    "Diff: " + differenceScreenShotPath);

            //Try to use IM4Java for comparison
            compareImagesWithImageMagick(baselineScreenShotPath, actualScreenShotPath, differenceScreenShotPath);
            //compareByAnotherService();
        } else {
            System.out.println("BaselineScreenshot is not exist! We put it into test screenshot folder.\n");
            //Put the screenshot to the specified folder
            ImageIO.write(elementScreenShot.getImage(), "PNG", baselineImageFile);
        }
    }

    //Sleep Function
    public void sleep (int milis) {
        Long milliseconds = (long) milis;
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void makeComparison() throws IOException, InterruptedException {
        BufferedImage expectedImage = ImageIO.read(new File(System.getProperty("user.dir")+"/ScreenShotsloginPageTest/loginPageTest_Baseline.png"));

        System.out.println("\n expectedImage= "+expectedImage );
        Thread.sleep(5000);

        Screenshot elementScreenShot = new AShot().shootingStrategy(ShootingStrategies.viewportRetina(100, 0, 0, 2)).takeScreenshot(driver);

        BufferedImage image = elementScreenShot.getImage();
        ImageIO.write(image, "PNG", new File(System.getProperty("user.dir")+"/ScreenShotsloginPageTest/loginPageTest_Actual.png"));



        BufferedImage actualImage = elementScreenShot.getImage();



        ImageDiffer imgDiff = new ImageDiffer();

        ImageDiff diff = imgDiff.makeDiff(expectedImage,actualImage);



        BufferedImage diffImage = diff.getDiffImage();
        ImageIO.write(image, "PNG", new File(System.getProperty("user.dir")+"/ScreenShotsloginPageTest/loginPageTest_Difference.png"));

        System.out.println("\n diffImage= "+diffImage.getColorModel() );


        if(diff.hasDiff()){
            System.out.println("Images are not same");
        }else {
            System.out.println("Images are same");
        }

        diff = imgDiff.makeDiff(diffImage,actualImage);
        //diff.getDiffImage()
        if(diff.hasDiff()){
            System.out.println(" diff Image & actual image are not same"+ diff.getDiffImage().getData());
        }else {
            System.out.println("Images are same");
        }

        diff = imgDiff.makeDiff(diffImage,expectedImage);

        if(diff.hasDiff()){
            System.out.println(" diff Image & xpected image are not same");
        }else {
            System.out.println(" expected & Diff Images are same");
        }
    }

}