package RestAssured_Homework;

import HomeworkModels.CreateToken;
import com.google.gson.Gson;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;


public class Homework {

    Response response;
    String token;
    int bookingID;

    @BeforeClass
    public  void CreateToken() {

        CreateToken createToken = new CreateToken("admin", "password123");
        String request = new Gson().toJson(createToken);

        response = RestAssured.given()
                .contentType("application/json")
                .body(request)
                .when()
                .post("https://restful-booker.herokuapp.com/auth")
                .then()
                .statusCode(200)
                .log().all()
                .extract().response();
        token = response.path("token").toString();
        System.out.println(token);
    }

    @Test
    public void Test01_GetBookingIds(){

        given()
                .log().all().
                when()
                .get("https://restful-booker.herokuapp.com/booking").
                then()
                .statusCode(200)
                .log().all();

    }

    @Test
    public void Test02_CreateBooking(){
        String postData = "{\n" +
                "    \"firstname\" : \"Jim\",\n" +
                "    \"lastname\" : \"Brown\",\n" +
                "    \"totalprice\" : 111,\n" +
                "    \"depositpaid\" : true,\n" +
                "    \"bookingdates\" : {\n" +
                "        \"checkin\" : \"2018-01-01\",\n" +
                "        \"checkout\" : \"2019-01-01\"\n" +
                "    },\n" +
                "    \"additionalneeds\" : \"Breakfast\"\n" +
                "}";

        response = RestAssured.given()
                .body(postData)
                .contentType(ContentType.JSON)
                .log().all().
                when().post("https://restful-booker.herokuapp.com/booking").
                then()
                .statusCode(200)
                .log().all()
                .extract().response();

        bookingID = response.path("bookingid");
        System.out.println(bookingID);

    }

    @Test
    public void Test03_UpdateBooking(){

        String postData2 = "{\n" +
                "    \"firstname\" : \"Esra\",\n" +
                "    \"lastname\" : \"Brown\",\n" +
                "    \"totalprice\" : 111,\n" +
                "    \"depositpaid\" : true,\n" +
                "    \"bookingdates\" : {\n" +
                "        \"checkin\" : \"2018-01-01\",\n" +
                "        \"checkout\" : \"2019-01-01\"\n" +
                "    },\n" +
                "    \"additionalneeds\" : \"Breakfast\"\n" +
                "}";

        response = RestAssured.given()
                .contentType("application/json")
                .accept("application/json")
                .header("Cookie", "token="+token)
                .body(postData2)
                .pathParam("id",bookingID)
                .when()
                .put("https://restful-booker.herokuapp.com/booking/{id}")
                .then()
                .statusCode(200)
                .log().all()
                .extract().response();
    }

    @DataProvider(name = "dataProvider")
    public Object[][] dataProvider(){
        return new Object[][]{
                {bookingID,200},
                {2,200},
                {3,200},
                {4,200},
        };
    }

    @Test (dataProvider = "dataProvider")
    public void Test04_GetBooking(int Id, int statusCode){

        response = RestAssured.given()
                .contentType("application/json").
                when()
                .get("https://restful-booker.herokuapp.com/booking/"+Id).
                then()
                .statusCode(statusCode)
                .log().all()
                .extract().response();
    }

    @Test
    public void Test05_partialUpdateBooking(){

        String postData3 = "{\n" +
                "    \"firstname\" : \"James\",\n" +
                "    \"lastname\" : \"Brown\"\n" +
                "}";

        response = RestAssured.given()
                .contentType("application/json")
                .accept("application/json")
                .header("Cookie", "token="+token)
                .body(postData3)
                .pathParam("id",bookingID)
                .when()
                .patch("https://restful-booker.herokuapp.com/booking/{id}")
                .then()
                .statusCode(200)
                .log().all()
                .extract().response();
    }

    @AfterClass
    public void DeleteBooking(){

        response = RestAssured.given()
                .contentType("application/json")
                .accept("application/json")
                .header("Cookie", "token="+token)
                .pathParam("id",bookingID)
                .when()
                .delete("https://restful-booker.herokuapp.com/booking/{id}")
                .then()
                .statusCode(201)
                .log().all()
                .extract().response();

    }

    @AfterClass
    public void HealthCheck(){

        response = RestAssured.given()
                .log().all()
                .when()
                .get("https://restful-booker.herokuapp.com/ping")
                .then()
                .statusCode(201)
                .log().all()
                .extract().response();
    }

}
