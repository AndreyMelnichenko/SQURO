package Pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import utils.RandomIconRoute;
import utils.RandomMinMax;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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
    //private UserSettings userSettings = new UserSettings();

    public void enterToPersonalCabinet(String name, String password){
        login.login().setValue(name);
        login.password().setValue(password);
        login.enter().click();
        homePage.map().waitUntil(Condition.visible,15000);
    }

    public void exitFromPersonalCabinet(){
        homePage.menu().waitUntil(Condition.visible,2000).click();
        Selenide.sleep(1000);
        homePage.exit().waitUntil(Condition.visible,3000).click();
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
        Selenide.sleep(1000);
        Select selectDevice = new Select(registration.listTimeZone());
        selectDevice.selectByIndex(RandomMinMax.Go(2,19));
        Selenide.sleep(1000);
        registration.buttonCreate().shouldBe(Condition.visible).click();
        Selenide.sleep(1000);
    }

    public void goToSettingsPage(WebDriver driver){
        Actions shiftKey = new Actions(driver);
        Selenide.sleep(2000);
        shiftKey.keyDown(Keys.SHIFT).click(homePage.firstDeviceArea()).keyUp(Keys.SHIFT).perform();
        homePage.settings().waitUntil(Condition.visible,4000).click();
    }

    public void setViews(){
        settings.setViews().waitUntil(Condition.visible, 4000).click();
        Selenide.sleep(200);
    }

    public void setDevice(){
        settings.settingsDevice().waitUntil(Condition.visible, 2000).click();
        Selenide.sleep(200);
    }

    public void goOutSettingsPage(WebDriver driver){
        Actions shiftKey = new Actions(driver);
        shiftKey.keyDown(Keys.SHIFT).click(homePage.firstDeviceAreaSelected()).keyUp(Keys.SHIFT).perform();
        Selenide.sleep(1000);
    }

    public void goUsersItem(){
        settings.usersItem().waitUntil(Condition.visible, 3000).click();

    }

    public void checkChangeIcon(){
        settings.currentIco().shouldBe(Condition.visible).click();
        settings.saveButton().waitUntil(Condition.visible, 2000).click();
        Selenide.sleep(2000);
        settings.thirdIco().waitUntil(Condition.visible, 2000).click();
        settings.saveButton().waitUntil(Condition.visible, 2000).click();
        Selenide.sleep(2000);
        assertTrue(settings.deviceIcon().isDisplayed());
        settings.customIcon().shouldBe(Condition.visible).click();
        settings.saveButton().waitUntil(Condition.visible, 2000).click();
        Selenide.sleep(2000);
    }

    public void loadIcon(){
        settings.currentIco().hover();
        settings.deleteIcon().shouldBe(Condition.visible).click();
        Selenide.sleep(500);
        settings.uploadIcon().shouldBe(Condition.visible).click();
        Selenide.sleep(500);
        settings.uploadInput().uploadFile(new File(RandomIconRoute.getRoute()));
        Selenide.sleep(500);
        settings.acceptUpload().waitUntil(Condition.visible, 2000).click();
        Selenide.sleep(500);
        settings.saveButton().shouldBe(Condition.visible).click();
        Selenide.sleep(2000);
    }

    public void changeDeviceName(){
        //settings.deviceCurrentName().waitUntil(Condition.visible,3000).clear();
        Selenide.sleep(1000);
        String newName = "Test Device GT3101" + new SimpleDateFormat("_dd-MM-yyyy_HH:mm").format(Calendar.getInstance().getTime());
        //settings.deviceCurrentName().waitUntil(Condition.visible,3000).click();
        //System.out.println(settings.deviceCurrentName().waitUntil(Condition.visible,3000).isDisplayed());
        settings.deviceCurrentName().waitUntil(Condition.visible,3000).setValue(newName);
        settings.saveButton().shouldBe(Condition.visible).click();
        Selenide.sleep(4000);
        assertEquals(newName, homePage.firstDeviceName().getText());
        //settings.deviceOldName().shouldBe(Condition.visible).clear();
        Selenide.sleep(1000);
        settings.deviceOldName().shouldBe(Condition.visible).setValue("Test Device GT3101");
        settings.saveButton().shouldBe(Condition.visible).click();
        Selenide.sleep(2000);
    }

    public void goToUserSettings(){
        homePage.menu().waitUntil(Condition.visible, 5000).click();
        homePage.accountSettings().waitUntil(Condition.visible, 5000).click();
        homePage.userSettingsPage().waitUntil(Condition.visible,5000).click();
    }

    public void firstDeviceClick() {
        homePage.firstDeviceArea().waitUntil(Condition.visible,5000).click();
    }

    public void lastTripClick(){
        homePage.lastTrip().waitUntil(Condition.visible,5000).click();
    }

    public void hoverPointA(){
        //Actions mouseHover = new Actions(driver);
        homePage.pointA().waitUntil(Condition.visible,5000).hover();
    }
    public void hoverPointB(){
        //Actions mouseHover = new Actions(driver);
        homePage.pointB().waitUntil(Condition.visible,5000).hover();
    }

    public boolean popUpPointA(){
        return homePage.popUpPointA().waitUntil(Condition.visible,5000).isDisplayed();
    }

    public boolean popUpPointB(){
        return homePage.popUpPointB().waitUntil(Condition.visible,5000).isDisplayed();
    }

    public void tripDevice() {
        homePage.tripDeviceItem().waitUntil(Condition.visible,5000).click();
    }
}
