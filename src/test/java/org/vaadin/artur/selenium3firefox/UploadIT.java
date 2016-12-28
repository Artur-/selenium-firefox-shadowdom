package org.vaadin.artur.selenium3firefox;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.internal.WrapsElement;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;

public class UploadIT {

    private RemoteWebDriver driver;

    @Before
    public void setup() throws Exception {
        // Ensure a hub is running locally with Firefox 63 before running the
        // test
        // docker run -d -p 4444:4444 -v /dev/shm:/dev/shm
        // selenium/standalone-firefox:3.14.0-krypton
        String hubUrl = "http://localhost:4444/wd/hub";
        driver = new RemoteWebDriver(new URL(hubUrl), new FirefoxOptions());
    }

    @After
    public void teardown() {
        if (driver != null) {
            driver.close();
        }
        driver = null;
    }

    @Test
    public void upload() throws Exception {
        driver.get("http://host.docker.internal:8080/upload-in-shadow.html");

        WebElement uploadInShadow = (WebElement) executeScript(
                "return document.body.querySelector('vaadin-upload').shadowRoot.firstElementChild;");
        WebElement uploadInBody = (WebElement) executeScript(
                "return document.body.querySelector('#upload')");

        ((RemoteWebElement) unwrap(uploadInShadow))
                .setFileDetector(new LocalFileDetector());
        ((RemoteWebElement) unwrap(uploadInBody))
                .setFileDetector(new LocalFileDetector());

        byte[] file1Contents = "This is file 1"
                .getBytes(StandardCharsets.UTF_8);
        File file1 = createTempFile(file1Contents);

        uploadInBody.sendKeys(file1.getPath());
        uploadInShadow.sendKeys(file1.getPath());

    }

    private Object executeScript(String script) {
        return driver.executeScript(script);
    }

    private WebElement unwrap(WebElement e) {
        while (e instanceof WrapsElement) {
            e = ((WrapsElement) e).getWrappedElement();
        }
        return e;
    }

    private File createTempFile(byte[] contents) throws IOException {
        File tempFile = File.createTempFile("TestFileUpload", ".txt");
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            out.write(contents);
        }
        tempFile.deleteOnExit();
        return tempFile;
    }

}
