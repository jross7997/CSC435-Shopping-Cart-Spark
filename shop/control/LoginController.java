package shop.control;

import shop.View;
import shop.classes.*;
import shop.utils.*;

import spark.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


public class LoginController {

    final static String localHost = "jdbc:mysql://localhost:3306/itemshop?useSSL=false";

    public static Route getLoginInfo = (Request request, Response response) -> {

        Object toPrint = null;

        try (Connection conn = DriverManager.getConnection(localHost, "user", "1234");
             Statement stmt = conn.createStatement();) {

             request.session(true);

            //If not logged in, print false
            if (request.session().attribute("isLoggedIn") == null) {
                toPrint = "Please Log In";
            } else {
                Helper helper = new Helper();
                QueryHelper query = new QueryHelper();
                //If logged in, print all of the users
                query.startJDBC();
                //Get all of the users from the MySQL database
                ResultSet rset = query.selectAllLogins(conn, stmt);
                ArrayList<User> users = helper.prepareUserList(rset);
                //Print the array List
                toPrint = users;
            }
        } catch (IOException | ClassNotFoundException | SQLException | NumberFormatException e) {
           toPrint = e.toString();
           e.printStackTrace();
        }

        View view = new View();
        return view.viewer(request,response,toPrint);

    };


    public static Route Login = (Request request, Response response) -> {

        Object toPrint = null;
        //Check to see if the user exists
        try (Connection conn = DriverManager.getConnection(localHost, "user", "1234");
             Statement stmt = conn.createStatement();) {
            //send in the query of the username
            String user = request.params(":user");
            request.session(true);

            if (user != null) {
                if (user.contains("-")) {
                    user = user.replace("-", " ");
                }

                Helper helper = new Helper();
                QueryHelper query = new QueryHelper();
                query.startJDBC();
                //Send a mySQL command to get each of the users that have the same name
                query.setUserName(user);
                ResultSet rset = query.selectUserByName(conn, stmt);

                String foundUser = null;
                String foundid = null;
                User loggedIn = null;
                while (rset.next()) {
                    foundUser = rset.getString("username");
                    foundid = rset.getString("id");
                }

                //check if the user exists, if it does, update the session with the user info
                if (foundUser != null) {
                    if (user.equals(foundUser)) {
                        synchronized (request.session()) {
                            loggedIn = new User(foundUser, Integer.parseInt(foundid));
                            request.session().attribute("isLoggedIn", loggedIn);
                        }
                    }
                }

                //transfer the cart from the session to the database
                ArrayList<Food> cart = (ArrayList<Food>)request.session().attribute("cart");
                if(cart != null){
                    for(Food f: cart){
                        query.addCart(conn, stmt, f, loggedIn.getId());
                    }
                }

                //If logged in, print your user info
                if (loggedIn != null) {
                    response.status(200);
                    toPrint = (String)("Welcome " + loggedIn.getUsername());
                    // if not, print "no such user"
                } else {
                    response.status(400);
                    toPrint = "No Such User";
                }
            } else {
                response.status(400);
               toPrint = "Wrong Format";
            }
        } catch (ClassNotFoundException | SQLException | NumberFormatException e) {
            response.status(422);
            toPrint = e.toString();
            e.printStackTrace();
        }

        View view = new View();
        return view.viewer(request,response,toPrint);
    };


    public static Route postNewLogin = (Request request, Response response) -> {

        Object toPrint = null;

        try (Connection conn = DriverManager.getConnection(localHost, "user", "1234");
             Statement stmt = conn.createStatement();) {
            //Class.forName("com.mysql.jdbc.Driver");
            Helper helper = new Helper();
            //Sign up by sending in a user parameter
            String name = request.params(":user");
            //Replace all "-" with spaces
            if (name != null) {
                if (name.contains("-")) {
                    name = name.replace("-", " ");
                }
                //Get each user with the same name
                QueryHelper query = new QueryHelper();
                query.startJDBC();
                query.setUserName(name);
                ResultSet rset = query.selectUserByName(conn, stmt);
                boolean exists = false;
                while (rset.next()) {
                    exists = true;
                }
                //If the user doesn't exist, assign it a new id and add to the database
                if (!exists) {
                    rset = query.selectAllLogins(conn, stmt);
                    int id = helper.getMaxID(rset);
                    id++;
                    query.setUserId(id);
                    query.insertUser(conn, stmt);
                    User add = new User(name, id);
                    response.status(201);
                    toPrint = (String)("201: User " + add.getUsername() + " Added");
                    //If the user does exist, inform the user
                } else {
                    response.status(206);
                    toPrint = "User already Exists";
                }
            } else {
                response.status(400);
                //Print Wrong Format Message
                toPrint = "Wrong Format";
            }
        } catch (Exception e) {
            response.status(400);
            toPrint = e.toString();
            e.printStackTrace();
        }

        View view = new View();
        return view.viewer(request,response,toPrint);
    };

    public static Route LogOut = (Request request, Response response) -> {

        String toPrint = null;
        try {
            //If the session is active, invalidate it

            if (request.session() != null) {
                synchronized (request.session()) {
                    request.session().invalidate();
                }
            }
            response.status(204);
            //alert the user that they logged out
            toPrint = "Logged Out";
        } catch (Exception e) {
            response.status(422);
            toPrint = e.toString();
            e.printStackTrace();
        }
        View view = new View();
        return view.viewer(request,response,toPrint);
    };

}
