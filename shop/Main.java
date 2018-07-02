package shop;

import shop.control.*;

import static spark.Spark.*;

public class Main {

    public static void main(String[] args) {
	// write your code here

        port(4567);

        //HomePage
        get("/home",  IndexController.getIndexPage);
        post("/home", IndexController.post20Items);
        delete("/home", IndexController.deleteAllData);
        //DataBase Page
        get("/data",  DataController.getData);
        post("/data/:name/:calories/:fat/:sodium", DataController.postNewData);
        delete("/data/:id", DataController.deleteOneData);
        put("/data/:id/:name/:calories/:fat/:sodium",DataController.upDateData);

        //Login Page
        get("/login",  LoginController.getLoginInfo);
        post("/login/:user", LoginController.postNewLogin);
        delete("/login", LoginController.LogOut);
        put("/login/:user",LoginController.Login);

        //Shopping Page
        get("/shop",  ShopController.getShop);
        put("/shop/:id",CartController.addToCart);

        //Cart
        get("/cart",  CartController.getCart);
        delete("/cart/:id", CartController.removeFromCart);
        put("/cart/:id",CartController.addToCart);

    }
}
