package shop.classes;

/**
 *
 * @author Justin
 */
public class TotalPrice {
    //Used to show the total price in the cart
    private double total =0;

    public TotalPrice(double p){
        total = p;
    }

    public double getTotal(){
        return total;
    }

}
