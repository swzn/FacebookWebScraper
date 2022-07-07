module FacebookWebScraper {
    requires javafx.graphics;
    requires javafx.controls;
    requires java.desktop;

    requires org.seleniumhq.selenium.chromium_driver;
    requires org.seleniumhq.selenium.json;
    requires org.seleniumhq.selenium.java;
    requires org.seleniumhq.selenium.support;
    requires org.seleniumhq.selenium.remote_driver;
    requires org.seleniumhq.selenium.http;
    requires org.seleniumhq.selenium.api;
    requires org.seleniumhq.selenium.devtools_v95;
    requires org.seleniumhq.selenium.chrome_driver;

    requires org.json;

    opens sajad.wazin.mcgill.ca;
}