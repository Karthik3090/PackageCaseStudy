package com.packagecaseStudy.packer;

/**
 * Created by Karthikeyan Rajasekar
 */
public class PackageDetails {

    int index;
    Double weight;
    Float price;

    /**
     * Parameterized constructor
     *
     * @param index  the index value
     * @param weight the weight
     * @param price  the price
     */
    public PackageDetails(int index, Double weight, Float price) {

        this.index = index;
        this.weight = weight;
        this.price = price;
    }

    /**
     * @return the index value
     */
    public int getIndex() {
        return index;
    }

    /**
     * @param index set the index value
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * @return the weight value
     */
    public Double getWeight() {
        return weight;
    }

    /**
     * @param weight the weight value
     */
    public void setWeight(Double weight) {
        this.weight = weight;
    }

    /**
     * @return the price value
     */
    public Float getPrice() {
        return price;
    }

    /**
     * @param price the price value
     */
    public void setPrice(Float price) {
        this.price = price;
    }
}
