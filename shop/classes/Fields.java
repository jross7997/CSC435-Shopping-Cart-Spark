package shop.classes;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Justin
 */
//Just used for the API
public class Fields {

    @JsonProperty("item_id")
    private String itemID;

    @JsonProperty("item_name")
    private String itemName;

    @JsonProperty("brand_id")
    private String brandId;

    @JsonProperty("brand_name")
    private String brandName;

    @JsonProperty("nf_serving_size_qty")
    private String servingUnit;

    @JsonProperty("nf_serving_size_unit")
    private String servingSize;

    @JsonProperty("nf_calories")
    private String calories;

    @JsonProperty("nf_total_fat")
    private String fat;

    @JsonProperty("nf_sodium")
    private String sodium;

    public String getItemId() {
        return itemID;
    }

    public void setItemId(String i) {
        itemID = i;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String i) {
        itemName = i;
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String i) {
        brandId = i;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String i) {
        brandName = i;
    }

    public String getServingUnit() {
        return servingUnit;
    }

    public void setServingUnit(String i) {
        servingUnit = i;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String i) {
        calories = i;
    }

    public String getFat() {
        return fat;
    }

    public void setFat(String i) {
        fat = i;
    }

    public String getSodium() {
        return sodium;
    }

    public void setSodium(String i) {
        sodium = i;
    }

    public String getServingSize() {
        return servingSize;
    }

    public void setServingSize(String i) {
        servingSize = i;
    }

}
