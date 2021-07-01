package objects;

public class Division {
    private final String division_name;
    private final int division_id;
    private final int country_id;

    /**
     * Create a Division object
     * @param division_name The Name of the division
     * @param division_id The Division_ID
     * @param country_id The Country_ID of the country that this division is associated with
     */
    public Division (String division_name, int division_id, int country_id){
        this.division_name = division_name;
        this.division_id = division_id;
        this.country_id = country_id;
    }

    public String getName(){ return this.division_name; }
    public int getId(){ return this.division_id; }
    public int getCountryId(){ return this.country_id; }

    /**
     * Display the Division name instead of the object and memory location
     * @return Division.division_name
     */
    @Override
    public String toString(){
        return(division_name);
    }

}
