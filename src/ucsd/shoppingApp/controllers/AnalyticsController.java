package ucsd.shoppingApp.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import ucsd.shoppingApp.ConnectionManager;
import ucsd.shoppingApp.models.SalesAnalyticsModel;
import ucsd.shoppingApp.SalesAnalyticsDAO;

@WebServlet("/AnalyticsController")
public class AnalyticsController extends HttpServlet{

	private static final long serialVersionUID = 1L;
	private Connection con = null;
	private SalesAnalyticsDAO entity = null;
	
	public void init() {
		con = ConnectionManager.getConnection();
		entity = new SalesAnalyticsDAO(con);
	}
	
	public void destroy() {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		response.setContentType("test/html");
		boolean zeroresults = false;
		
		if(request.getParameter("action") != null) {	
			try {
				HttpSession session = request.getSession();
				String row_header = request.getParameter("row_header");
				String order = request.getParameter("order");
				int cate_id = Integer.parseInt(request.getParameter("category"));
			
				ArrayList<SalesAnalyticsModel> list = entity.filterList(row_header, order, cate_id);
				//System.out.println(list.size()); TODO remove debugging message
				if(list.size() == 0) zeroresults = true;
				if(zeroresults == false) request.setAttribute("pres", 1);
				if(zeroresults == true) request.setAttribute("zeroresults", 1);
				request.setAttribute("list", list);
				RequestDispatcher rd = request.getRequestDispatcher("salesAnalytics.jsp");
				rd.forward(request, response);
			} 	catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
}
