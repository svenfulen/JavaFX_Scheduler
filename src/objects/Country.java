package objects;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Country {
    final private int country_id;
    final private String country_name;

    /**
     * Create a Country object
     * @param country_id Country Code in database
     * @param country_name Name of the country
     */
    public Country(int country_id, String country_name){
        this.country_id = country_id;
        this.country_name = country_name;
    }

    public int getId(){ return this.country_id; }
    public String getName() { return this.country_name; }

    /**
     * Only contains countries used for this application, using the country codes in this specific database.
     * @return An ObservableList of Country objects for US, UK, CA
     */
    public static ObservableList<Country> getCountries(){
        ObservableList<Country> countries = FXCollections.observableArrayList();
        Country CA = new Country(3, "Canada");
        Country US = new Country(1, "United States");
        Country UK = new Country(2, "United Kingdom");
        countries.add(CA);
        countries.add(US);
        countries.add(UK);
        return countries;
    }

    /**
     * Display the country name instead of the object instance memory value.
     * @return String, name of the country.
     */
    @Override
    public String toString(){
        return(country_name);
    }

}
