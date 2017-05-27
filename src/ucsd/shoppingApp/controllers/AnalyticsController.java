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
				String row_header;
				String order;
				int cate_id;	
				if(request.getParameter("cate") != null) {
					entity.clear();
					row_header = request.getParameter("row_header");
					order = request.getParameter("order");
					cate_id = Integer.parseInt(request.getParameter("cate"));
					session.setAttribute("row_header", row_header);
					session.setAttribute("order", order);
					session.setAttribute("sess_cate_id", cate_id);
				}
				else {
					if(request.getAttribute("sess_first_page") == null)
						request.setAttribute("sess_first_page", true);
					row_header = (String)session.getAttribute("row_header");
					order = (String)session.getAttribute("order");
					cate_id = (int)session.getAttribute("sess_cate_id");
				}
				if(request.getParameter("action").equalsIgnoreCase("More Products"))
					entity.updateOffset("colUpdate");
				else if(request.getParameter("action").equalsIgnoreCase("More Rows"))
					entity.updateOffset("rowUpdate");
				ArrayList<SalesAnalyticsModel> header = this.Filterhead(order, cate_id);			
				ArrayList<SalesAnalyticsModel> list = this.Filterbody(row_header, order, cate_id);
				//System.out.println(list.size()); TODO remove debugging message
				if(list.size() == 0) zeroresults = true;
				if(zeroresults == false) request.setAttribute("pres", 1);
				if(zeroresults == true) request.setAttribute("zeroresults", 1);
				if(entity.endCol()) request.setAttribute("endCol", 1);
				if(entity.endRow()) request.setAttribute("endRow", 1);
				request.setAttribute("body", list);
				request.setAttribute("header", header);
				RequestDispatcher rd = request.getRequestDispatcher("salesAnalytics.jsp");
				rd.forward(request, response);
			} catch(NumberFormatException e) {
				request.setAttribute("error", "Invalid Argument");
				e.printStackTrace();
			}
			catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	private ArrayList<SalesAnalyticsModel> Filterbody(String row_header, String order, int cate_id) throws SQLException{
		if(row_header.equalsIgnoreCase("customer"))
			entity.rowQueryCustomer(order, cate_id);
		else
			entity.rowQueryState(order, cate_id);
		ArrayList<SalesAnalyticsModel> list = new ArrayList<SalesAnalyticsModel>();

		
		list = entity.filterB(row_header);
		System.out.println("list size: " + list.size());
		return list;
	}
	private ArrayList<SalesAnalyticsModel> Filterhead(String order, int cate_id) throws SQLException{
		entity.reset();
		ArrayList<SalesAnalyticsModel> list = new ArrayList<SalesAnalyticsModel>();
		if(cate_id <= 0) {
			list = entity.filterH(order);
		} else {
			list = entity.filterHC(order, cate_id);
		}
		return list;
	}
}
