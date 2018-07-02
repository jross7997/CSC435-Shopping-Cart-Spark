package shop.control;

import shop.View;
import shop.classes.*;
import shop.utils.*;

import spark.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class CartController {

    final static String localHost = "jdbc:mysql://localhost:3306/itemshop?useSSL=false";


    public static Route getCart = (Request request, Response response) -> {

        Object toPrint = null;
        try (Connection conn = DriverManager.getConnection(localHost, "user", "1234");
             Statement stmt = conn.createStatement();) {
            request.session(true);
            ArrayList<Food> foodList = new ArrayList<Food>();
            TotalPrice cost = null;

            //If you're not logged in, check the session cart
            if (request.session().attribute("isLoggedIn") == null) {
                //Check if the cart exists or is empty
                ArrayList<Food> cart = (ArrayList<Food>)request.session().attribute("cart");
                if (cart == null || cart.isEmpty()) {
                    toPrint = "Empty";
                } else {
                    double total = 0;
                    for (Food i : cart) {
                        total += i.getPrice();
                    }
                    //cost = new TotalPrice(total);
                    response.status(200);
                    foodList = cart;
                    toPrint = new CartInfo(foodList,total);
                }
                //If logged in, check database
            } else {
                Helper helper = new Helper();
                QueryHelper query = new QueryHelper();
                query.startJDBC();
                User user = (User) request.session().attribute("isLoggedIn");
                ResultSet rset = query.cartInnerJoin(conn, stmt, user.getId());
                foodList = helper.prepareCart(rset);
                double total = 0;
                //If logged in, get better price
                for (Food i : foodList) {
                    total += i.getSalesPrice();
                }
                response.status(200);
                toPrint = new CartInfo(foodList,total);
            }

        } catch (Exception e) {
            response.status(422);
            toPrint = e.toString();
            e.printStackTrace();
        }
        View view = new View();
        return view.viewer(request,response,toPrint);
    };

    public static Route addToCart = (Request request, Response response) -> {
        Object toPrint = null;
        try (Connection conn = DriverManager.getConnection(localHost, "user", "1234");
             Statement stmt = conn.createStatement();) {
            request.session(true);
            Helper helper = new Helper();
            QueryHelper query = new QueryHelper();
            query.startJDBC();

            String cartid = request.params(":id");
            if(cartid != null) {
                boolean cid = helper.isInteger(cartid);
                if(cid) {

                    ResultSet rset = query.selectById(conn, stmt, Integer.parseInt(cartid));
                    Food inCart = helper.prepareItem(rset);
                    //Item inCart = helper.searchById(URI, request, response, context);
                    if (request.session().attribute("isLoggedIn") == null) {

                        synchronized (request.session()) {
                            //Add it to your session
                            ArrayList<Food> cart = (ArrayList<Food>) request.session().attribute("cart");
                            if (cart == null) {
                                cart = new ArrayList<Food>();
                                cart.add(inCart);
                                request.session().attribute("cart", cart);
                            } else {
                                cart.add(inCart);
                                request.session().attribute("cart", cart);
                            }
                        }
                    } else {

                        User sess = (User) request.session().attribute("isLoggedIn");
                        query.addCart(conn, stmt, inCart, sess.getId());
                    }
                    response.status(201);
                    toPrint = (String) ("201: Item " + inCart.getName() + " was added to your cart.");

                }else{
                    response.status(400);
                    toPrint = "Please Use Integers";
                }

            }else{
                response.status(400);
                toPrint = "Wrong Format";
            }
        } catch (Exception e) {
            response.status(422);
            toPrint = e.toString();
            e.printStackTrace();
        }

        View view = new View();
        return view.viewer(request,response,toPrint);

    };

    public static Route removeFromCart = (Request request, Response response) -> {
        Object toPrint = null;
        try (Connection conn = DriverManager.getConnection(localHost, "user", "1234");
             Statement stmt = conn.createStatement();) {
             request.session(true);
            //get the id parameter of the item to be removed
            String id = request.params(":id");
            if (id != null) {
                Helper helper = new Helper();
                boolean cid = helper.isInteger(id);
                if(cid) {
                    int idnum = Integer.parseInt(id);
                    Food del = null;

                    if (request.session().attribute("isLoggedIn") == null) {
                        synchronized (request.session()) {
                            //Remove the item from the cart
                            ArrayList<Food> cart = (ArrayList<Food>) request.session().attribute("cart");
                            for (Food i : cart) {
                                if (i.getID() == idnum) {
                                    del = i;
                                    cart.remove(i);
                                    break;
                                }
                            }
                        }
                    } else {
                        QueryHelper query = new QueryHelper();
                        query.startJDBC();
                        helper = new Helper();
                        ResultSet rset = query.selectCartById(conn, stmt, idnum);
                        del = helper.prepareItem(rset);
                        query.deleteCartById(conn, stmt, idnum);
                    }
                    //Tell User an item has been deleted
                    response.status(204);
                    toPrint = (String) ("204: Item " + del.getName() + " was deleted");

                }else{
                    response.status(400);
                    toPrint = "Please use Integers";
                }

            } else {
                response.status(400);
                toPrint = "Wrong Format";
            }
        } catch (Exception e) {
            response.status(422);
            toPrint = e.toString();
            e.printStackTrace();
        }

        View view = new View();
        return view.viewer(request,response,toPrint);
    };

}


