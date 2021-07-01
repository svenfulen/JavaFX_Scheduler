package objects;

public class Contact {
    private int contact_ID;
    private String contact_name;
    private String contact_email;

    /**
     * Create a Contact object
     * @param id The ID of the contact
     * @param name The Name of the contact
     * @param email The Email of the contact
     */
    public Contact(int id, String name, String email){
        this.contact_ID = id;
        this.contact_name = name;
        this.contact_email = email;
    }

    public int getID(){ return contact_ID; }
    public String getName() { return contact_name; }
    public String getEmail() { return contact_email; }

    public void setID(int id){ this.contact_ID = id; }
    public void setName(String name){ this.contact_name = name; }
    public void setEmail(String email){ this.contact_email = email; }

    @Override
    public String toString() { return (contact_name); }

}
