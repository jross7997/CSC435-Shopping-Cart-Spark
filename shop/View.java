package shop;


import shop.utils.JSONUtil;
import spark.Request;
import spark.Response;
import spark.Route;

public class View
{
    public String viewer(Request req, Response resp, Object o){
        resp.type("application/json");
        JSONUtil json = new JSONUtil();
        return json.toJSON(o);
    }


}
