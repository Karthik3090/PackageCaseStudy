package com.packagecaseStudy.packer;

import com.packagecaseStudy.exception.APIException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Class to process the file ,Read line by line to get the package details and select the index of the best package to send
 * Created by Karthikeyan Rajasekar
 */
public class Packer {

    /**
     * Method accepts the filepath as an input and return string as output <br/>
     * Reads the text file line by line and find the best package based on below condition <br/>
     * <p>
     * 1.)Max weight that a package can take is ≤ 100 <br/>
     * 2.)There might be up to 15 items you need to choose from <br/>
     * 3.)Max weight and cost of an item is ≤ 100 <br/>
     * <p>
     * <p>
     * Note: would prefer to send a package which weights less in case there is more than one package with the same price
     * <p>
     * Our goal is to find a package with high cost and weight which should be nearly equal to totalweight provided, To achieve that the first process is to<br/>
     * sort the package which has high value and minimum weight(to cover if price is equal pick low weight). Then check if the high price package is equal to totalweight<br/>
     * if its lesser then add the next package, follow the same untill you reached the totalweight.
     *
     * @param filepath the path of file
     * @return the String value
     */
    public static String pack(String filepath) {
        FileReader fileToRead = null;
        try {
            fileToRead = new FileReader(filepath);
            BufferedReader bufferedReader = new BufferedReader(fileToRead);
            String lineToManipulate;
            StringBuilder result = new StringBuilder(15);
            // Below while process the file line by line to identify the best package
            while ((lineToManipulate = bufferedReader.readLine()) != null) {
                // Make a substring to filter out the totalweight from line
                Double totalWeight = Double.valueOf(StringUtils.substringBefore(lineToManipulate, ":"));
                // Covers first contraint "Max weight that a package can take is ≤ 100"
                if (totalWeight <= 100) {
                    // Take out the remaining string after "totalWeight" of package for fruther processing
                    lineToManipulate = StringUtils.substringAfter(lineToManipulate, ":");
                    lineToManipulate = lineToManipulate.replaceAll("\\s", "");

                    List<PackageDetails> packageList = getFilterPackages(lineToManipulate);

                    if (CollectionUtils.isNotEmpty(packageList))
                        populateIndexFromFilteredPackage(result, totalWeight, packageList);
                    else {
                        result.append("-");
                    }
                }
            }
            fileToRead.close();

            System.out.println(result.toString());
            return result.toString();
        }

        catch (NumberFormatException exception) {
            throw new APIException("Exception occured due to incompitable parameters value:", exception);
        }
        catch (IOException e) {
            System.out.println("Please the file given as input :" + e.getMessage());
        }
        return null;
    }

    private static void populateIndexFromFilteredPackage(StringBuilder result, Double totalWeight, List<PackageDetails> packageList) {
        if (packageList.size() > 1) {
            final List<Double> actualWeight = new ArrayList<>();
            // When there are more then one package sort the package in an order where weight is low and price is high, <br/>
            // this covers "would prefer to send a package which weights less in case there is more than one package with the same price"
            sortParameterBasedPackage(packageList);

            // Once the package is sorted try to remove all the package which weight is greater than totalweight and price is more than 100 "Max weight and cost of an item is ≤ 100"
            packageList = packageList.stream()//
                            .filter(packageToFilter -> packageToFilter.getWeight() <= totalWeight)//
                            .filter(packageToFilter -> packageToFilter.getPrice() <= 100)//
                            .collect(Collectors.toList());

            actualWeight.add(packageList.get(0).getWeight());
            result.append(packageList.get(0).getIndex());
            // How many ever package we have ,should select only upto 15package to send "There might be up to 15 items you need to choose from"
            for (int count = 1; count < (packageList.size() > 15 ? 15 : packageList.size()); count++) {
                PackageDetails packagedetail = packageList.get(count);
                Double addedWeight = actualWeight.get(0) + (packagedetail.getWeight());
                if (count != 0 && addedWeight <= totalWeight) {
                    result.append(",");
                    result.append(packagedetail.getIndex());
                    actualWeight.set(0, addedWeight);
                }
            }
        }
        else if (packageList.get(0).getWeight() <= totalWeight) {
            result.append(packageList.get(0).getIndex());
        }
        else {
            result.append("-");
        }
        result.append("\n");
    }

    /**
     * Below method sort the package based on price and Weight ,Price should be larger and the weight lesser
     *
     * @param packageList the List of PackageDetails
     */
    private static void sortParameterBasedPackage(List<PackageDetails> packageList) {
        packageList.sort((o1, o2) -> {
            Float x1 = o1.getPrice();
            Float x2 = o2.getPrice();
            int sComp = x2.compareTo(x1);

            if (sComp != 0) {
                return sComp;
            }
            else {
                Double weight1 = o1.getWeight();
                Double weight2 = o2.getWeight();
                return weight1.compareTo(weight2);
            }
        });
    }

    /**
     * Filter the text based on regex to identify the contents between "()" and build a package object with parameters
     *
     * @param lineToManipulate the line to manipulate
     * @return the list of package
     */
    private static List<PackageDetails> getFilterPackages(String lineToManipulate) {
        List<PackageDetails> packageList = new ArrayList<>();
        //Below code will manipulate the line and form a package object to form a list
        Pattern.compile("\\(|\\)")//
                        .splitAsStream(lineToManipulate).forEach(line -> {
            if (StringUtils.isNotBlank(line)) {
                String[] parameters = line.split("\\,");
                if (parameters.length == 3 && checkIfParametersNonNegative(parameters[1], parameters[2])) {
                    packageList.add(new PackageDetails(Integer.parseInt(parameters[0]), Double.valueOf(parameters[1]), Float.valueOf(parameters[2].replace("€", ""))));
                }
                else {
                    throw new APIException("Check the parameters given as input in line(Value may be negative or more parameters than expected):" + lineToManipulate);
                }
            }
        });
        return packageList;
    }

    private static boolean checkIfParametersNonNegative(String weight, String price) {
        return Double.valueOf(weight) > 0 && Float.valueOf(price.replace("€", "")) > 0;
    }

    public static void main(String args[]) {
        if (args.length > 0 && args[0] != null) {
            Packer.pack(args[0]);
        }
        else {
            throw new APIException("Please pass filepath as first argument");
        }
    }
}
