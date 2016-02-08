/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Pichai Sivawat
 */
public class LoginUtilities {
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
            Logger.getLogger(LoginUtilities.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public int selectUser(String user, String password) throws SQLException {
        String cmd = "SELECT * FROM users WHERE user=?";
        PreparedStatement ps = c.prepareStatement(cmd);
        
        ps.setString(1, user);
        
        
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            String u = rs.getString("user");
            String p = rs.getString("password");
            
            if (p.equals(password)) {
                return rs.getInt("role");
            }
        }
        
        return -1;
    }
}
