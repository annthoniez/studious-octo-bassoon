/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Pichai Sivawat
 */
public class Customer {
    private String fname;
    private String lname;
    private String company;
    private String mobile;
    private String email;
    private String address;
    
    public Customer(String fname, String lname, String company, String mobile, String email, String address) {
        this.fname = fname;
        this.lname = lname;
        this.company = company;
        this.mobile = mobile;
        this.email = email;
        this.address = address;
    }

    /**
     * @return the fname
     */
    public String getFname() {
        return fname;
    }

    /**
     * @return the lname
     */
    public String getLname() {
        return lname;
    }

    /**
     * @return the company
     */
    public String getCompany() {
        return company;
    }

    /**
     * @return the mobile
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }
    
    
}
