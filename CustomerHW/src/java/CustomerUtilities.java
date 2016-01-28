/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Pichai Sivawat
 */
public class CustomerUtilities {
    private Connection c;
    private Statement s;
    
    public void init() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            
            String url = "jdbc:mysql://database.it.kmitl.ac.th:3306/it_31";
            String user = "it_31";
            String pwd = "rL7PR4hP";
            c = DriverManager.getConnection(url, user, pwd);
            s = c.createStatement();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(CustomerUtilities.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public ArrayList getCustomer() throws SQLException {
        String cmd = "SELECT * FROM customer";
        ResultSet rs = s.executeQuery(cmd);
        
        ArrayList<Customer> arr = new ArrayList<>();
        
        while (rs.next()) {
            arr.add(new Customer(rs.getString("firstname"), rs.getString("lastname"), rs.getString("company"), rs.getString("mobile"), rs.getString("email"), rs.getString("address")));
        }
        
        System.out.print(arr);
        rs.close();
        c.close();
        
        return arr;
    }
    
    public int insertCustomer(Customer customer) {
        PreparedStatement ps;
        
        String cmd = "INSERT INTO customer(firstname, lastname, company, mobile, email, address) VALUES(?,?,?,?,?,?)";
        
        int eft = 0;
        
        try {
            ps = c.prepareStatement(cmd);
            ps.setString(1, customer.getFname());
            ps.setString(2, customer.getLname());
            ps.setString(3, customer.getCompany());
            ps.setString(4, customer.getMobile());
            ps.setString(5, customer.getEmail());
            ps.setString(6, customer.getAddress());
            
            eft = ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(CustomerUtilities.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return eft;
    }
}
