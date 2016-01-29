/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author natha
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            
            String user = request.getParameter("username");
            String pass = request.getParameter("password");
            
            LoginUtilities lu = new LoginUtilities();
            lu.init();
            
            try {
                boolean isvalid = lu.selectUser(user, pass);
                out.println("<!DOCTYPE html>");
                out.println("<html lang='en'>");
                out.println("<head>");
                out.println("  <meta charset='utf-8'>");
                out.println("  <meta http-equiv='X-UA-Compatible' content='IE=edge'>");
                out.println("  <meta name='viewport' content='width=device-width, initial-scale=1'>");
                out.println("  <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->");
                out.println("  <title>เข้าสู่ระบบ | ระบบกรอกข้อมูลผลงานนักศึกษา คณะเทคโนโลยีสารสนเทศ</title>");
                out.println("  ");
                out.println("  <!-- Bootstrap -->");
                out.println("  <link href='assets/css/bootstrap.min.css' rel='stylesheet' type='text/css' />");
                out.println("  <link href='assets/css/bootflat.min.css' rel='stylesheet' type='text/css' />");
                out.println("  <link href='assets/plugins/select2/select2.min.css' rel='stylesheet' type='text/css' />");
                out.println("  <link rel='stylesheet' href='https://maxcdn.bootstrapcdn.com/font-awesome/4.4.0/css/font-awesome.min.css'>");
                out.println("  <link href='assets/css/style.css' rel='stylesheet' type='text/css' />");
                out.println("  ");
                out.println("  <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->");
                out.println("  <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->");
                out.println("  <!--[if lt IE 9]>");
                out.println("    <script src='https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js'></script>");
                out.println("    <script src='https://oss.maxcdn.com/respond/1.4.2/respond.min.js'></script>");
                out.println("  <![endif]-->");
                out.println("  </head>");
                out.println("<body>");
                out.println("  <nav class='navbar navbar-default navbar-fixed-top navbar-inverse' role='navigation'>");
                out.println("      <div class='container'>");
                out.println("          <div class='navbar-header'>");
                out.println("              <button type='button' class='navbar-toggle' data-toggle='collapse'");
                out.println("                      data-target='#bs-example-navbar-collapse-5'>");
                out.println("                  <span class='sr-only'>Toggle navigation</span>");
                out.println("                  <span class='icon-bar'></span>");
                out.println("                  <span class='icon-bar'></span>");
                out.println("                  <span class='icon-bar'></span>");
                out.println("              </button>");
                out.println("              <a class='navbar-brand' href='#'><i class='fa fa-slack'></i>Shield System</a>");
                out.println("          </div>");
                out.println("          ");
                out.println("          <div class='collapse navbar-collapse' id='bs-example-navbar-collapse-5'>");
                out.println("              <ul class='nav navbar-nav'>");
                out.println("                  <li @if(Request::is('/')) class='active' @endif><a href='{{ route('index') }}'><i class='fa fa-home'></i>หน้าแรก</a></li>");
                out.println("              </ul>");
                out.println("              <ul class='nav navbar-nav navbar-right'>");
                out.println("              <li><a href='#'><i class='fa fa-user'></i>เข้าสู่ระบบ</a></li>");
                out.println("              </ul>");
                out.println("          </div><!-- /.navbar-collapse -->");
                out.println("      </div><!-- /.container-fluid -->");
                out.println("  </nav>");
                out.println("  <div class='jumbotron'>");
                out.println("    <div class='container'>");
                out.println("      <h1>ระบบกรอกข้อมูลผลงานนักศึกษา</h1>");
                out.println("      <p>คณะเทคโนโลยีสารสนเทศ - สถาบันเทคโนโลยีพระจอมเกล้าเจ้าคุณทหารลาดกระบัง</p>");
                out.println("    </div>");
                out.println("  </div>");
                out.println("  ");
                out.println("  <div id='content' class='container'>");
                out.println("    <div class='row'>");
                out.println("      <div class='col-md-8 col-md-offset-2'>");
                out.println("        <div class='panel'>");
                out.println("          <div class='panel-body'>");
                out.println("            <div class='row'>");
                out.println("              <div class='col-xs-12'>");
                if (isvalid) {
                    out.println("<h1>Hello, " + user + "!</h1>");
                    if (user.equals("user")) {
                        response.sendRedirect("create.html");
                    }
                } else {
                    out.println("<h1>Incorrect username or password.</h1>");
                }
                out.println("              </div>");
                out.println("            </div>");
                out.println("          </div>");
                out.println("        </div>");
                out.println("      </div>");
                out.println("    </div>");
                out.println("  </div>");
                out.println("  ");
                out.println("<script src='https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js'></script>");
                out.println("<script src='assets/js/bootstrap.min.js'></script>");
                out.println("<script src='assets/js/icheck.min.js'></script>");
                out.println("<script src='assets/js/jquery.fs.selecter.min.js'></script>");
                out.println("<script src='assets/js/jquery.fs.stepper.min.js'></script>");
                out.println("<script src='assets/plugins/select2/select2.min.js'></script>");
                out.println("</body>");
                out.println("</html>");
            } catch (SQLException ex) {
                Logger.getLogger(LoginServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
