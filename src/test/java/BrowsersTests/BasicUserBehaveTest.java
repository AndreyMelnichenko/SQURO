package BrowsersTests;

import Pages.*;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import core.WebDriverTestBase;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;
import utils.CursorRobot;
import utils.CustomWait;
import utils.DataProperties;
import utils.RandomMinMax;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.testng.AssertJUnit.assertFalse;
import static utils.PropertiesCache.getProperty;

@Epic("UI tests")
public class BasicUserBehaveTest extends WebDriverTestBase {

    private Login login = new Login();
    private HomePage homePage = new HomePage();
    private ErrPage errPage = new ErrPage();
    private Recovery recovery = new Recovery();
    private Registration registration = new Registration();
    private AccountSettings accountSettings = new AccountSettings();

    @Test (description = "Login page")
    @Description("Login page")
    public void logo() {
        open(baseUrl);
        login.logo().shouldBe(Condition.visible);
        AssertJUnit.assertEquals(login.title().getText(),DataProperties.dataProperty("data.properties", "login.page.title"));

    }
    @Test(dependsOnMethods = "logo", description = "Sing-Up Error Messages")
    @Description("Sing-Up Error Messages Validation")
    public void SingUpErrMessages(){
        login.login().setValue("qatest@email.my");
        login.password().setValue("123qwwedsa");
        login.enter().click();
        AssertJUnit.assertEquals(login.errorLogin().getText(), DataProperties.dataProperty("data.properties", "login.wrong.email"));
        AssertJUnit.assertEquals(login.errorPassword().getText(), DataProperties.dataProperty("data.properties", "login.wrong.password"));
    }

    @Test(dependsOnMethods = "SingUpErrMessages", description = "Recovery Password")
    @Description("Recovery Password")
    public void recoveryPassword(){
        login.recoverPass().shouldBe(Condition.visible).click();
        recovery.title().should(Condition.matchesText(DataProperties.dataProperty("data.properties","recovery.page.title")));
        recovery.emailField().shouldBe(Condition.visible).setValue(getProperty("user.email"));
        recovery.recoveryButton().shouldBe(Condition.visible).click();
        recovery.textArea().waitUntil(Condition.visible,5000).shouldHave(text("Данные для восстановления пароля были отправлены на почту "));
    }

    @Test(dependsOnMethods = "recoveryPassword", description = "Re-SingIn")
    @Description("Re-SingIn")
    public void badRegistration(){
        open(baseUrl);
        login.registration().shouldBe(Condition.visible).click();
        registration.emailField().shouldBe(Condition.visible).setValue(getProperty("user.email"));
        registration.passwordField().shouldBe(Condition.visible).setValue(getProperty("user.password"));
        registration.passwordConfirmField().shouldBe(Condition.visible).setValue("1q2w3e4rRTfd");
        registration.buttonCreate().shouldBe(Condition.visible).click();
        System.out.println(registration.errorMessageEmail().getText());
        registration.errorMessageEmail().shouldBe(Condition.visible).should(Condition.matchesText("Значение «dima.laktionov5@gmail.com» для «Электронная почта» уже занято."));
        registration.errorMessageTimeZone().waitUntil(Condition.visible,5000).should(Condition.matchesText("Необходимо заполнить «Time Zone»."));
    }

    @Test(dependsOnMethods = "badRegistration", description = "SingIn")
    @Description("Sing In")
    public void registration(){
        open(baseUrl);
        login.registration().shouldBe(Condition.visible).click();
        registration.emailField().shouldBe(Condition.visible).setValue(getProperty("new.user.email"));
        registration.passwordField().shouldBe(Condition.visible).setValue(getProperty("new.user.password"));
        registration.passwordConfirmField().shouldBe(Condition.visible).setValue(getProperty("new.user.password"));
        Select selectDevice = new Select(registration.listTimeZone());
        selectDevice.selectByIndex(RandomMinMax.Go(1,20));
        CustomWait.getOneSecondWait();
        registration.buttonCreate().shouldBe(Condition.visible).click();
        CustomWait.getTwoSecondWait();
        homePage.map().waitUntil(Condition.visible,10000);
        homePage.menu().shouldBe(Condition.visible).click();
        homePage.exit().shouldBe(Condition.visible).click();
        login.logo().waitUntil(Condition.visible, 6000);
    }

    @Test (dependsOnMethods = "registration", description = "Sing Up")
    @Description("Sing-Up")
    public void enterPersonalCabinet(){
        open(baseUrl);
        login.login().setValue(getProperty("user.email"));
        login.password().setValue(getProperty("user.password"));
        login.enter().click();
        homePage.map().shouldBe(Condition.visible);
    }



    @Test(dependsOnMethods = "enterPersonalCabinet", description = "404 page")
    @Description("404 page")
    public void errorPage(){
        open(baseUrl+"/eeuheirf");
        AssertJUnit.assertEquals(errPage.errTitle().getText(), "404");
        $(By.xpath("//a")).click();
        homePage.map().shouldBe(Condition.visible);
    }

    @Test(dependsOnMethods = "errorPage", description = "Add New User")
    @Description("Add New User")
    public void addUser(){
        homePage.menu().shouldBe(Condition.visible).click();
        homePage.accountSettings().shouldBe(Condition.visible).click();
        accountSettings.createNewUserButton().shouldBe(Condition.visible).click();
        accountSettings.emailNewUser().shouldBe(Condition.visible).setValue(getProperty("new.user.email"));
        accountSettings.nameNewUser().shouldBe(Condition.visible).setValue(getProperty("new.user.fio"));
        accountSettings.passNewUser().shouldBe(Condition.visible).setValue(getProperty("new.user.password"));
        accountSettings.passNewUserConfirm().shouldBe(Condition.visible).setValue(getProperty("new.user.password"));
        accountSettings.phoneNewUser().shouldBe(Condition.visible).setValue(getProperty("new.user.phone"));
        accountSettings.roleNewUser().shouldBe(Condition.visible).click();
        accountSettings.acceptCreateNewUser().shouldBe(Condition.visible).click();
        CustomWait.getTwoSecondWait();
        Selenide.refresh();
        accountSettings.mainArea().waitUntil(Condition.visible,10000);
        accountSettings.createdUserEmail().should(Condition.matchesText(getProperty("new.user.email")));
        accountSettings.createdUserName().should(Condition.matchesText(getProperty("new.user.fio")));
        accountSettings.createdUserPhone().should(Condition.matchesText(getProperty("new.user.phone")));
    }

    @Test(dependsOnMethods = "addUser", description = "Send Invite")
    @Description("Send Invite")
    public void invitetoUser(){
        accountSettings.inviteButton().shouldBe(Condition.visible).click();
        accountSettings.emailForImvite().shouldBe(Condition.visible).setValue(getProperty("user.gmail"));
        accountSettings.messageForInvite().shouldBe(Condition.visible).setValue("Welcome to PIT Service");
        accountSettings.simpleRoleInvite().shouldBe(Condition.visible).click();
        accountSettings.acceptSendInvite().shouldBe(Condition.visible).click();
        accountSettings.mainArea().waitUntil(Condition.visible,10000);
        CustomWait.getOneSecondWait();
        Selenide.refresh();
        accountSettings.secondUserInviteAlert().shouldBe(Condition.visible).should(Condition.matchesText("Приглашение истекает через 6 дней"));
    }

    @Test//(dependsOnMethods = "invitetoUser", description = "User change info")
    @Description("User change info")
    public void userChangeInfo(){
        open(baseUrl);
        login.login().setValue(getProperty("user.email"));
        login.password().setValue(getProperty("user.password"));
        login.enter().click();
        homePage.menu().shouldBe(Condition.visible).click();
        homePage.accountSettings().shouldBe(Condition.visible).click();

        String oldName = accountSettings.firstUserName().getText();
        accountSettings.firstUserThreeDots().shouldBe(Condition.visible).click();
        Selenide.sleep(200);
        accountSettings.firstUserEdit().click();
        accountSettings.fitstUserOldEmail().should(Condition.visible).setValue(getProperty("user.email"));
        String newName = "Dima" + new SimpleDateFormat("_dd-MM-yyyy_HH:mm").format(Calendar.getInstance().getTime());
        accountSettings.firstUserOldName().should(Condition.visible).setValue(newName);
        accountSettings.firstUserOldPhone().should(Condition.visible).setValue(getProperty("new.user.phone"));
        accountSettings.firstUserTimeZone().should(Condition.visible).click();
        Selenide.sleep(200);
        accountSettings.firstUserTimeZone().should(Condition.visible).click();
        accountSettings.firstUserAcceptNewInfo().should(Condition.visible).click();
        accountSettings.mainArea().waitUntil(Condition.visible, 2000);
        assertFalse(oldName.equals(accountSettings.firstUserName()));

    }

    @Test//(enabled = false, dependsOnMethods = "userChangeInfo", description = "Add new Device TK-116")
    @Description("Add new Device TK-116")
    public void addDevice(){
        open(baseUrl);
        login.login().setValue(getProperty("user.email"));
        login.password().setValue(getProperty("user.password"));
        login.enter().click();
        homePage.menu().shouldBe(Condition.visible).click();
        homePage.accountSettings().shouldBe(Condition.visible).click();

        accountSettings.devicesButton().should(Condition.visible).click();
        accountSettings.addDeviceButton().should(Condition.visible).click();
        System.out.println(" ");
        accountSettings.newDeviceName().setValue(DataProperties.dataProperty("data.properties","TK116.name"));
        accountSettings.newDeviceImei().setValue(DataProperties.dataProperty("data.properties","TK116.imei"));
        Select selectDevice = new Select(accountSettings.newDeviceType());
        selectDevice.selectByIndex(4);
        accountSettings.newDevicePhone().setValue(DataProperties.dataProperty("data.properties","TK116.sim"));
        accountSettings.newDevicePass().setValue(DataProperties.dataProperty("data.properties","TK116.pass"));
        accountSettings.newDeviceShowPass().click();
        accountSettings.newDeviceApn().setValue(DataProperties.dataProperty("data.properties","TK116.apn"));
        System.out.println(" ");
        accountSettings.newDeviceAccept().should(Condition.visible).click();
        System.out.println(" ");
        Selenide.refresh();
        System.out.println(" ");
        accountSettings.newDeviceItem().waitUntil(Condition.visible,10000);
        System.out.println(" ");
    }

    @Test(dependsOnMethods = "addDevice", description = "Remove device")
    @Description("Remove device")
    public void removeDevice(){
        accountSettings.mainArea().waitUntil(Condition.visible, 2000);
        accountSettings.removeNewDevice().waitUntil(Condition.visible,2000).click();
        accountSettings.removeNewDeviceButton().waitUntil(Condition.visible,2000).click();
        accountSettings.removeNewDeviceConfirm().waitUntil(Condition.visible,2000).click();
        accountSettings.mainArea().waitUntil(Condition.visible, 2000);
        System.out.println(" ");
        Selenide.refresh();
        System.out.println(accountSettings.contentField().parent());
        System.out.println(" ");
        accountSettings.contentField().should(Condition.matchesText(""));
        System.out.println(" ");
        open(baseUrl);
        homePage.map().should(Condition.visible);
        homePage.menu().should(Condition.visible).click();
        homePage.exit().should(Condition.visible).click();
    }

    @Test(enabled = false, dependsOnMethods = "removeDevice", description = "Check device GT3101")
    @Description("Check device GT3101")
    public void checkDevice(){
        open(baseUrl);
        login.login().should(Condition.visible).setValue(getProperty("user2.email"));
        login.password().should(Condition.visible).setValue(getProperty("user2.password"));
        login.enter().should(Condition.visible).click();
        Selenide.sleep(200);
        homePage.menu().waitUntil(Condition.visible,5000);
        for(int i=0; i<3;i++) {homePage.firstDeviceItem().should(Condition.visible).click();Selenide.sleep(200);}
        homePage.allarmPic().should(Condition.visible).hover();
        Selenide.sleep(200);
        homePage.lockPic().should(Condition.visible).hover();
        Selenide.sleep(200);
        homePage.speedPic().should(Condition.visible).hover();
        Selenide.sleep(200);
        homePage.infoPic().should(Condition.visible).hover();
        Selenide.sleep(200);
        homePage.showInMap().should(Condition.visible).click();
        Selenide.sleep(1000);
        homePage.showInMap().should(Condition.visible).click();
    }

    @Test(enabled = false, dependsOnMethods = "checkDevice", description = "Check right widget")
    @Description("Check right widget")
    public void checkRightWidget(){
        Selenide.refresh();
        for(int i=0; i<7;i++) {
            homePage.firstActiveFilterRightWidget().shouldBe(Condition.visible).click();
            Selenide.sleep(200);
        }
    }

    @Test(enabled = false, dependsOnMethods = "checkRightWidget", description = "Car on Map")
    @Description("Car on Map")
    public void checkMapZoom(){
        homePage.firstDeviceItem().shouldBe(Condition.visible).click();
        homePage.carOnMap().shouldBe(Condition.visible).hover();
        Selenide.sleep(500);
        homePage.carOnMapDescription().shouldBe(Condition.visible).shouldHave(exactText("Test Device GT3101"));
        for(int i=0; i<10;i++) {homePage.mapZoomOut().shouldBe(Condition.visible).click();Selenide.sleep(200);}
        homePage.mapSettings().shouldBe(Condition.visible).click();
    }

    @Test(enabled = false, dependsOnMethods = "checkMapZoom", description = "Calendar")
    @Description("Calendar")
    public void calendar(){
        homePage.calendarPeriod().shouldBe(Condition.visible).click();
        homePage.calendarHeadFirst().shouldBe(Condition.visible).shouldHave(exactText("Not selected"));
        homePage.calendarHeadSecond().shouldBe(Condition.visible).shouldHave(exactText("Not selected"));
        homePage.calendarPrev().shouldBe(Condition.visible).click();
        Selenide.sleep(100);
        homePage.calendarPrev().shouldBe(Condition.visible).click();
        homePage.startDate().shouldBe(Condition.visible).click();
        homePage.calendarHeadFirst().shouldBe(Condition.visible).shouldNotHave(exactText("Not selected"));
        homePage.calendarHeadSecond().shouldBe(Condition.visible).shouldHave(exactText("Not selected"));
        homePage.calendarNext().shouldBe(Condition.visible).click();
        Selenide.sleep(100);
        homePage.calendarNext().shouldBe(Condition.visible).click();
        homePage.endDate().shouldBe(Condition.visible).click();
        homePage.calendarHeadFirst().shouldBe(Condition.visible).shouldNotHave(exactText("Not selected"));
        homePage.calendarHeadSecond().shouldBe(Condition.visible).shouldNotHave(exactText("Not selected"));
        homePage.applyPeriod().shouldBe(Condition.visible).click();
        homePage.chosedPeriod().shouldBe(Condition.visible).shouldNot(exactText(""));
    }

    @Test(enabled = false, dependsOnMethods = "calendar", description = "Add Device group")
    @Description("Add Device group")
    public void addGroup() {
        Selenide.sleep(1000);
        homePage.menu().waitUntil(Condition.visible, 5000);
        //-------Create Group
        homePage.createDeviceGroup().shouldBe(Condition.visible).click();
        homePage.addGroupPopUpTitle().shouldBe(Condition.visible).shouldBe(Condition.matchesText("Добавить группу"));
        homePage.groupName().shouldBe(Condition.visible).setValue("Test Group");
        homePage.acceptCreateGroup().shouldBe(Condition.visible).click();
        Selenide.sleep(2000);
    }
    @Test(enabled = false, dependsOnMethods = "addGroup", description = "Edit Device group")
    @Description("Edit Device group")
    public void editGroup() {
        Selenide.refresh();
        homePage.editGroup().click();
        homePage.inputNewGroupName().setValue("My Group");
        homePage.acceptNewGroupName().shouldBe(Condition.visible).click();
        Selenide.refresh();
        CursorRobot.moveMouse();
    }
    @Test(enabled = false, dependsOnMethods = "editGroup", description = "Delete Device group")
    @Description("Delete Device group")
    public void deleteGroup(){
        homePage.editGroup().click();
        homePage.deleteNewGroupName().click();
        homePage.deleteNewGroupPopUpTitle().shouldBe(Condition.matchesText("Удалить группу?"));
        homePage.acceptDeleteNewGroup().shouldBe(Condition.visible).click();
        Selenide.sleep(2000);
    }

    @Test(enabled = false, dependsOnMethods = "deleteGroup", description = "Exit from personal cabinet")
    @Description("Exit from personal cabinet")
    public void exitPersonalCabinet(){
        accountSettings.menuButton().should(Condition.visible).click();
        accountSettings.exitButton().shouldBe(Condition.visible).click();
        login.logo().waitUntil(Condition.visible, 2000);
    }
}
