package shop.classes;

/**
 *
 * @author Justin
 */
public class User {

    //User Details
    private String username = null;
    private int id = 0;

    public User(String u, int i) {
        username = u;
        id = i;
    }

    public String getUsername() {
        return username;
    }

    public int getId() {
        return id;
    }

    public String toString() {
        return "Id:" + this.getId() + " Username: " + this.getUsername();
    }

}
