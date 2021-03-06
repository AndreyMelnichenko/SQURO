package ApiTests;


import ResponseMessages.*;
import UserData.UserSingUp;
import core.ApiTestBase;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;
import static utils.PropertiesCache.getProperty;


@Epic("API tests")
public class RefreshToken extends ApiTestBase {

    @Test(description = "Refresh Token")
    @Description("Refresh Token")
    public void EditUser() {
        UserSingUp expectedUser = new UserSingUp(getProperty("user.email"), getProperty("user.password"));
        UserRS actualUser = given()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .spec(spec).body(expectedUser)
                .expect().statusCode(200)
                .when()
                .post(baseURL + "users/sign-in")
                .thenReturn().as(UserRS.class);
        String token = actualUser.getResult().getAuth_token();
        System.out.println("Auth token 1: " + token);
        System.out.println("Refresh_token 1: " + actualUser.getResult().getRefresh_token());

        EditUserRK editUserRK = new EditUserRK("+7");
        EditUserRS editUserRS = given()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization", "Bearer " + token)
                .spec(spec).body(editUserRK)
                .expect().statusCode(200)
                .when()
                .post(baseURL + "users/edit-user?id=" + getProperty("user.id"))
                .thenReturn().as(EditUserRS.class);
        assertEquals(editUserRS.getSuccess(), "true");
        assertEquals(editUserRS.getResult(), "true");

        try {
            Thread.sleep(301000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        EditUserRS editUserRS2 = given()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization", "Bearer " + token)
                .spec(spec).body(editUserRK)
                .expect().statusCode(401)
                .when()
                .post(baseURL + "users/edit-user?id=" + getProperty("user.id"))
                .thenReturn().as(EditUserRS.class);
        assertEquals(editUserRS2.getSuccess(), "false");
        assertEquals(editUserRS2.getEditUserError().getMessage(), "Your request was made with invalid credentials.");
    }
}
