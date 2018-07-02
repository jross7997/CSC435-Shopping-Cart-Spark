package shop.classes;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;

/**
 *
 * @author Justin
 */
public class CallInfo {
    //Just used for the API

    @JsonProperty("total_hits")
    private int hitsTotal;
    @JsonProperty("max_score")
    private int maxScore;

    @JsonProperty("hits")
    private ArrayList<FoodInfo> hitList;
    //ha

    public int getTotal() {
        return hitsTotal;
    }

    public void setTotal(int i) {
        hitsTotal = i;
    }

    public int getMax() {
        return maxScore;
    }

    public void setMax(int i) {
        maxScore = i;
    }

    public ArrayList<FoodInfo> getHitList(){
        return hitList;
    }

    public void setHitList(ArrayList<FoodInfo> h){
        hitList = h;
    }



}
