import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.comparison.ImageDiff;
import ru.yandex.qatools.ashot.comparison.ImageDiffer;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;
import ru.yandex.qatools.ashot.util.ImageTool;

public class FullScreenshot {

    public static void main(String[] args) throws IOException, InterruptedException {
        // TODO Auto-generated method stub


        //System.setProperty("webdriver.chrome.driver", "D:\\SeleniumDriver\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();

        driver.manage().timeouts().implicitlyWait(150, TimeUnit.SECONDS);

        driver.get("https://kempinski.dev.concilioinsights.com");


        BufferedImage expectedImage = ImageIO.read(new File(System.getProperty("user.dir") + "/imgs/aShot/colorChange.png"));

        System.out.println("\n expectedImage= " + expectedImage);
        Thread.sleep(5000);

        Screenshot screenshot = new AShot().shootingStrategy(
                ShootingStrategies.viewportPasting(1000)).takeScreenshot(driver);
        BufferedImage image = screenshot.getImage();
        ImageIO.write(image, "PNG", new File(System.getProperty("user.dir") + "/imgs/aShot/localDemo.png"));


        BufferedImage actualImage = screenshot.getImage();


        ImageDiffer imgDiff = new ImageDiffer();

        ImageDiff diff = imgDiff.makeDiff(expectedImage, actualImage);


        BufferedImage diffImage = diff.getDiffImage();
        ImageIO.write(image, "PNG", new File(System.getProperty("user.dir") + "/imgs/aShot/diffImage.png"));

        System.out.println("\n diffImage= " + diffImage.getColorModel());


        if (diff.hasDiff()) {
            System.out.println("Images are not same");
        } else {
            System.out.println("Images are same");
        }

        diff = imgDiff.makeDiff(diffImage, actualImage);
        //diff.getDiffImage()
        if (diff.hasDiff()) {
            System.out.println(" diff Image & actual image are not same" + diff.getDiffImage().getData());
        } else {
            System.out.println("Images are same");
        }

        diff = imgDiff.makeDiff(diffImage, expectedImage);

        if (diff.hasDiff()) {
            System.out.println(" diff Image & xpected image are not same");
        } else {
            System.out.println(" expected & Diff Images are same");
        }


        driver.quit();

    }
}
