package shop.classes;

import java.util.ArrayList;

public class CartInfo {
    private double total;
    private ArrayList<Food> cart;

    public CartInfo(ArrayList<Food> f,double t){
        cart = f;
        total = t;
    }

    public double getTotal() {
        return total;
    }

    public ArrayList<Food> getCart() {
        return cart;
    }
}
