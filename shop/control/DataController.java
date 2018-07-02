package shop.control;

import shop.View;
import shop.utils.*;
import shop.classes.*;

import static spark.Spark.*;
import spark.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.ArrayList;
import java.sql.*;



public class DataController {

    final static String localHost = "jdbc:mysql://localhost:3306/itemshop?useSSL=false";

    public static Route getData = (Request request, Response response) -> {

        Object toPrint = null;
        try (Connection conn = DriverManager.getConnection(localHost, "user", "1234");
             Statement stmt = conn.createStatement();) {
            Helper helper = new Helper();
            QueryHelper query = new QueryHelper();

            query.startJDBC();
            //Get all the items from the database
            ResultSet rset = query.selectAll(conn, stmt);
            ArrayList<Food> items = helper.prepareItemList(rset);

            response.status(200);
            toPrint = items;

        } catch (Exception e) {
            response.status(422);
           toPrint = e.toString();
            e.printStackTrace();
        }

        View view = new View();
        return view.viewer(request,response,toPrint);
    };


//    public static Route getById = (Request request, Response response) -> {
//        Object toPrint = null;
//
//        try (Connection conn = DriverManager.getConnection(localHost, "user", "1234");
//             Statement stmt = conn.createStatement();) {
//
//            String param = request.queryParams(":/input");
//            if(param != null){
//                String[] in = param.split("&");
//                String nme = null;
//                String calo = null;
//                String fatty = null;
//                String sod = null;
//
//                for(int i = 0; i< in.length; i++) {
//                    if (in[i].contains("name")) {
//                        String[] temp = in[i].split("=");
//                        nme = temp[0];
//                        if (nme.contains("-")) {
//                            nme = nme.replace("-", " ");
//                        }
//                    } else if (in[i].contains("calories")) {
//                        String[] temp = in[i].split("=");
//                        calo = temp[0];
//                    } else if (in[i].contains("fat")) {
//                        String[] temp = in[i].split("=");
//                        fatty = temp[0];
//                    } else if (in[i].contains("sodium")) {
//                        String[] temp = in[i].split("=");
//                        sod = temp[0];
//                    }
//                }
//                Helper helper = new Helper();
//
//
//
//                if(calo != null) {
//                    boolean numcal = helper.isInteger(calo);
//                }
//                if(fatty != null) {
//                    boolean numfat = helper.isInteger(fatty);
//                }
//                if(sod != null) {
//                    boolean numsod = helper.isInteger(sod);
//                }
//
//                if(numcal)
//
//            }else{
//                redirect.get("/data/:input", "/data");
//            }

//
//
//        }catch (Exception e) {
//            toPrint = e.toString();
//            e.printStackTrace();
//        }
//        View view = new View();
//        return view.viewer(request,response,toPrint);
//    };


    public static Route postNewData = (Request request, Response response) -> {

        Object toPrint = null;
        try (Connection conn = DriverManager.getConnection(localHost, "user", "1234");
             Statement stmt = conn.createStatement();) {
            Helper helper = new Helper();
            QueryHelper query = new QueryHelper();
             query.startJDBC();

            String name = request.params(":name");
            String calories = request.params(":calories");
            String fat = request.params(":fat");
            String sodium = request.params(":sodium");
            if(name != null && calories != null && fat != null && sodium != null){
                boolean cal = helper.isInteger(calories);
                boolean fa = helper.isInteger(fat);
                boolean sod = helper.isInteger(sodium);
                if(cal && fa && sod) {

                    if (name.contains("-")) {
                        name = name.replace("-", " ");
                    }

                    //Send in the mySQL command and execute it
                    ResultSet rset = query.selectAll(conn, stmt);
                    //Get the highest item ID

                    int newId = helper.getMaxID(rset);

                    newId += 1;
                    //create a new Item with the new Id
                    Food add = new Food(name, calories, fat, sodium, newId);
                    //update Query object
                    query.setId(newId);
                    query.setName(name);
                    query.setCaloiries(calories);
                    query.setFat(fat);
                    query.setSodium(sodium);
                    //Insert the new item to the database
                    query.insertShop(conn, stmt);

                    response.status(201);
                    toPrint = (String) ("201: Object " + name + " was created");
                }else{
                    response.status(400);
                    toPrint = "Please use Integers";
                }
            }else {
                response.status(400);
                toPrint = "Please use correct format";
            }
        } catch (Exception e) {
            response.status(422);
            toPrint = e.toString();
            e.printStackTrace();
        }

        View view = new View();
        return view.viewer(request,response,toPrint);
    };

    public static Route deleteOneData = (Request request, Response response) -> {
        Object toPrint = null;
        try (Connection conn = DriverManager.getConnection(localHost, "user", "1234");
             Statement stmt = conn.createStatement();) {

            Helper helper = new Helper();
            QueryHelper query = new QueryHelper();
            query.startJDBC();

            //Get the id of the item to be deleted
            String delId = request.params(":id");
            if(delId != null) {
                boolean idc = helper.isInteger(delId);
                if(idc) {
                    //get info of Item to be Deleted
                    ResultSet rset = query.selectAll(conn,stmt);
                     int maxId = helper.getMaxID(rset);
                     if(Integer.parseInt(delId) <= maxId) {
                         rset = query.selectById(conn, stmt, Integer.parseInt(delId));
                         Food del = helper.prepareItem(rset);
                         //Delete the item
                         query.deleteById(conn, stmt, Integer.parseInt(delId));
                         rset = query.selectAll(conn, stmt);
                         ArrayList<Food> temp = helper.prepareItemList(rset);
                         temp = helper.fixIds(temp);
                         query.deleteAPIData(conn, stmt);
                         query.insertAPIData(conn, stmt, temp);
                         //prepare for view
                         response.status(204);
                         toPrint = (String) ("204: Object " + del.getName() + " was deleted");
                         System.out.println(toPrint);
                     }else{
                         response.status(400);
                         toPrint = "Item does not exist";
                     }
                }else{
                    response.status(400);
                    toPrint = "Please use Integers";
                }
            }else{
                response.status(400);
                toPrint = "Please use correct format";
            }
        } catch (SQLException s) {
            response.status(400);
            toPrint = "Item " + request.params(":id") + "doesn't exist";
            s.printStackTrace();
        }catch(NumberFormatException | JsonProcessingException e) {
            response.status(422);
            toPrint = e.toString();
            e.printStackTrace();
        }

        View view = new View();
        return view.viewer(request,response,toPrint);
    };

    public static Route upDateData = (Request request, Response response) -> {

        Object toPrint = null;

        try (Connection conn = DriverManager.getConnection(localHost, "user", "1234");
             Statement stmt = conn.createStatement();) {

        String updateId = request.params(":id");
        String newname = request.params(":name");
        String newCalorie = request.params(":calories");
        String newFat = request.params(":fat");
        String newSodium = request.params(":sodium");

        if(updateId != null && newname != null && newCalorie != null && newFat != null && newSodium != null) {
            Helper helper = new Helper();
            boolean idc = helper.isInteger(updateId);
            boolean cal = helper.isInteger(newCalorie);
            boolean fa = helper.isInteger(newFat);
            boolean sod = helper.isInteger(newSodium);
            if (idc && cal && fa && sod) {

                if (newname.contains("-")) {
                    newname = newname.replace("-", " ");
                }
                Food f = new Food(newname, newCalorie, newFat, newSodium, Integer.parseInt(updateId));

                QueryHelper query = new QueryHelper(f.getID(), f.getName(), f.getCalories(), f.getFat(), f.getSodium());
                query.startJDBC();
                //Update the database
                query.updateShop(conn, stmt);
                response.status(202);
                toPrint = (String) ("202: Updated to " + f.getName());
            } else {
                response.status(400);
                toPrint = "Please use Integers";
            }
        }else{
            response.status(400);
            toPrint = "Please use correct format";
        }

    } catch (SQLException | NumberFormatException  e) {
            response.status(422);
        toPrint = e.toString();
        e.printStackTrace();
    }
    View view = new View();
        return view.viewer(request,response,toPrint);
};

}
