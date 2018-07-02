package shop.control;

import shop.classes.*;
import shop.utils.*;
import shop.View;

import spark.*;
import java.util.ArrayList;
import java.sql.*;


public class ShopController {

    final static String localHost = "jdbc:mysql://localhost:3306/itemshop?useSSL=false";

    public static Route getShop = (Request request, Response response) -> {
        Object toPrint = null;
        //Print the entire database
        try (Connection conn = DriverManager.getConnection(localHost, "user", "1234");
             Statement stmt = conn.createStatement();) {
            Helper helper = new Helper();
            QueryHelper query = new QueryHelper();
            query.startJDBC();
            //Get the Database
            ResultSet rset = query.selectAll(conn, stmt);
            ArrayList<Food> items = helper.prepareItemList(rset);
            toPrint = items;
            response.status(200);

        } catch (Exception e) {
            response.status(422);
            toPrint = e.toString();
            e.printStackTrace();
        }
        View view = new View();
        return view.viewer(request,response,toPrint);
    };


}
