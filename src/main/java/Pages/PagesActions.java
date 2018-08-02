package Pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import utils.CustomWait;
import utils.RandomMinMax;

import java.io.File;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static utils.DataProperties.dataProperty;
import static utils.PropertiesCache.getProperty;

public class PagesActions {
    private Login login = new Login();
    private HomePage homePage = new HomePage();
    private Recovery recovery = new Recovery();
    private Registration registration = new Registration();
    private Settings settings = new Settings();

    public void enterToPersonalCabinet(String name, String password){
        login.login().setValue(name);
        login.password().setValue(password);
        login.enter().click();
        homePage.map().shouldBe(Condition.visible);
    }

    public void exitFromPersonalCabinet(){
        homePage.menu().shouldBe(Condition.visible).click();
        homePage.exit().shouldBe(Condition.visible).click();
        login.logo().waitUntil(Condition.visible, 6000);
    }

    public void checkLogoPage(){
        login.logo().shouldBe(Condition.visible);
        assertEquals(login.title().getText(),dataProperty("data.properties", "login.page.title"));
    }

    public void checkAuthorizationPage(){
        login.logo().shouldBe(Condition.visible);
        assertEquals(login.authorisation().shouldBe(Condition.visible).getText(),dataProperty("data.properties", "auth.title"));
    }

    public void checkRecoverPage(){
        login.recoverPass().shouldBe(Condition.visible).click();
        recovery.title().should(Condition.matchesText(dataProperty("data.properties","recovery.page.title")));
    }

    public void checkresetPage(){
        assertEquals(recovery.resetTitle().getText(),dataProperty("data.properties","recovery.page.title"));
    }

    public void checkRegistrationPage(){
        login.registration().shouldBe(Condition.visible).click();
        registration.emailField().shouldBe(Condition.visible).setValue(getProperty("new.user.email"));
        registration.passwordField().shouldBe(Condition.visible).setValue(getProperty("new.user.password"));
        registration.passwordConfirmField().shouldBe(Condition.visible).setValue(getProperty("new.user.password"));
        Select selectDevice = new Select(registration.listTimeZone());
        selectDevice.selectByIndex(RandomMinMax.Go(1,20));
        CustomWait.getOneSecondWait();
        registration.buttonCreate().shouldBe(Condition.visible).click();
        CustomWait.getTwoSecondWait();
    }

    public void goToSettingsPage(WebDriver driver){
        Actions shiftKey = new Actions(driver);
        shiftKey.keyDown(Keys.SHIFT).click(homePage.firstDeviceArea()).keyUp(Keys.SHIFT).perform();
        homePage.settings().waitUntil(Condition.visible,4000).click();
        settings.setViews().waitUntil(Condition.visible, 4000).click();
        Selenide.sleep(200);
    }

    public void goOutSettingsPage(WebDriver driver){
        Actions shiftKey = new Actions(driver);
        shiftKey.keyDown(Keys.SHIFT).click(homePage.firstDeviceArea()).keyUp(Keys.SHIFT).perform();
        Selenide.sleep(200);
    }

    public void checkChangeIcon(){
        settings.currentIco().shouldBe(Condition.visible).click();
        settings.saveButton().waitUntil(Condition.visible, 2000).click();
        Selenide.sleep(200);
        settings.thirdIco().waitUntil(Condition.visible, 2000).click();
        settings.saveButton().waitUntil(Condition.visible, 2000).click();
        Selenide.sleep(200);
        assertTrue(settings.deviceIcon().isDisplayed());
        settings.customIcon().shouldBe(Condition.visible).click();
        settings.saveButton().waitUntil(Condition.visible, 2000).click();
        Selenide.sleep(200);
    }

    public void loadIcon(){
        settings.currentIco().hover();
        settings.deleteIcon().shouldBe(Condition.visible).click();

        settings.uploadIcon().shouldBe(Condition.visible).click();
        Selenide.sleep(1000);
        settings.uploadInput().uploadFile(new File("src/main/resources/car1.png"));
        Selenide.sleep(1000);

        settings.acceptUpload().waitUntil(Condition.visible, 2000).click();

        Selenide.sleep(1000);
        settings.saveButton().shouldBe(Condition.visible).click();
    }
}
