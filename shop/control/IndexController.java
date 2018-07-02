package shop.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.BufferedReader;

import shop.View;
import shop.classes.*;
import shop.utils.*;

import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import spark.*;

/**
 *
 * @author Justin
 */
public class IndexController {

    final static String localHost = "jdbc:mysql://localhost:3306/itemshop?useSSL=false";

    public static Route getIndexPage = (Request request, Response response) -> {
        Object toPrint = null;
        try {
            //List used for printing
            ArrayList<ApiInfo[]> list = new ArrayList<ApiInfo[]>();
            //HomePage
            ApiInfo home[] = new ApiInfo[3];
            home[0] = new ApiInfo("GET", "/home", "Shows the applications Functionality");
            home[1] = new ApiInfo("POST", "/home", "Populate the Database with external Food data");
            home[2] = new ApiInfo("DELETE", "/home", "Delete the entire Database");
            list.add(home);
            //Database
            ApiInfo[] data = new ApiInfo[4];
            data[0] = new ApiInfo("GET", "/data", "Shows the entire database");
            data[1] = new ApiInfo("POST", "/data/:name/:calories/:fat/:sodium", "Add a new item to the database");
            data[2] = new ApiInfo("PUT", "/data/:id/:name/:calories/:fat/:sodium", "Update the name and price of an existing item");
            data[3] = new ApiInfo("DELETE", "/data/:id", "Delete an item from the database");
            list.add(data);
            //Login
            ApiInfo[] login = new ApiInfo[4];
            login[0] = new ApiInfo("GET", "/login", "Determine if you're logged in, shows list of ");
            login[1] = new ApiInfo("POST", "/login/:user", "Sign up and add a new user to the database");
            login[2] = new ApiInfo("PUT", "/login/:user", "Log in");
            login[3] = new ApiInfo("DELETE", "/login", "Log Out and invalidate the session");
            list.add(login);
            //Shop
            ApiInfo[] shop = new ApiInfo[2];
            shop[0] = new ApiInfo("GET", "/shop", "Get a list of the items for sale");
            shop[1] = new ApiInfo("PUT", "/shop/:id", "Add an Item to the cart");
            list.add(shop);
            //Cart
            ApiInfo[] cart = new ApiInfo[3];
            cart[0] = new ApiInfo("GET", "/cart", "Get the User's cart, if logged in the price should be lower");
            cart[1] = new ApiInfo("PUT", "/cart/:id", "Add an item to the cart");
            cart[2] = new ApiInfo("DELETE", "/cart/:id", "Remove an Item from the cart");
            list.add(cart);
            //Print JSON with all of the Servlet Details
            response.status(200);
            toPrint = list;

        } catch (Exception e) {
            response.status(422);
            toPrint = e.toString();
        }
        View view = new View();
        return view.viewer(request,response,toPrint);
    };

    //post 20 items to the database
    public static Route post20Items = (Request request, Response response) -> {

        Object toPrint = null;

        try (Connection conn = DriverManager.getConnection(localHost, "user", "1234");
             Statement stmt = conn.createStatement();) {

            int min = 0;
            int max = 100;

            HttpClient client = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build()).build();
            QueryHelper query = new QueryHelper();
            Helper helper = new Helper();

            ResultSet rset = query.selectAll(conn, stmt);

            //get the number of items in the Database
            int maxId = helper.getMaxID(rset);
            maxId++;
            //This will add some variety to the database with multiple calls
            if (maxId > 0 && maxId < 20) {
                min = 0;
                max = 2000;
            } else if (maxId > 20 && maxId < 40) {
                min = 2000;
                max = 5000;
            } else {
                min = 0;
                max = 10000;
            }

            String myURL = "https://api.nutritionix.com/v1_1/search/?results=0%3A20&cal_min=" + min + "&cal_max=" + max + "&fields=item_name%2Cbrand_name%2Citem_id%2Cbrand_id%2Cnf_calories%2Cnf_fat%2Cnf_sodium&appId=88cf0044&appKey=90cbe0b7c7beeb26b938584e189be6fd";

            //Make the API call
            HttpGet req = new HttpGet(myURL);
            HttpResponse resp = client.execute(req);
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            // Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(
                    resp.getEntity().getContent()));

            //Parse the Data
            CallInfo temp = new CallInfo();
            temp = mapper.readValue(new URL(myURL), CallInfo.class);
            ArrayList<FoodInfo> hits = temp.getHitList();
            Fields t = new Fields();
            Food tempFood = new Food();
            ArrayList<Food> food = new ArrayList<Food>();
            for (FoodInfo f : hits) {
                t = f.getFields();
                String itemName = t.getItemName();
                tempFood.setName(itemName);

                String calories = t.getCalories();
                tempFood.setCalories(calories);

                String fat = t.getFat();
                tempFood.setFat(fat);

                String sodium = t.getSodium();
                tempFood.setSodium(sodium);
                food.add(tempFood);
                System.out.println(tempFood.getName());
                tempFood = new Food();
            }

            //Set Id's for each item that's about to enter the database
            for (int i = 0; i < food.size(); i++) {
                food.get(i).setID(maxId + i);
            }

            //Add each item to the shop

            query.insertAPIData(conn,stmt,food);

            response.status(201);
            toPrint = (String)"201: Twenty Objects Created";

        } catch (Exception e) {
            response.status(422);
            toPrint = (String)e.toString();
        }
        View view = new View();

        return view.viewer(request,response,toPrint);
    };

    //Delete entire database
    public static Route deleteAllData = (Request request, Response response) -> {

        String toPrint = null;

        try (Connection conn = DriverManager.getConnection(localHost, "user", "1234");
             Statement stmt = conn.createStatement();) {

            QueryHelper query = new QueryHelper();
            query.deleteAPIData(conn, stmt);

            //JSONUtil json = new JSONUtil();
            response.status(204);
            toPrint ="204: All Objects Deleted";

        } catch (Exception e) {
            response.status(422);
            toPrint = e.toString();
            e.printStackTrace();
        }
        View view = new View();
        return view.viewer(request,response,toPrint);
    };



}
