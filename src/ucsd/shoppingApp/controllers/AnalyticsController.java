package ucsd.shoppingApp.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
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
	
	public String prepareString(String name) {
		return "do $$ begin"
				+ " DELETE FROM log_tracing l USING OBSERVED_USER o WHERE l.observed_user_id = o.id AND o.user_name = '" + name + "';"
				+ " IF NOT EXISTS (SELECT u.id FROM OBSERVED_USER u WHERE u.user_name = '" + name + "') THEN"
				+ " INSERT INTO OBSERVED_USER (user_name) VALUES ('"+name+"');"
				+ " ALTER TABLE log_table ENABLE TRIGGER update_logTracing; END IF; end $$";
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		response.setContentType("test/html");
		boolean zeroresults = false;

		if(request.getParameter("action").equalsIgnoreCase("Run Query")) {
			try {
				HttpSession session = request.getSession();
				int cate_id;
				if(request.getParameter("cate") != null) {

					cate_id = Integer.parseInt(request.getParameter("cate"));
					session.setAttribute("sess_cate_id", cate_id);
				}
				else {

					if(request.getAttribute("sess_first_page") == null)
						request.setAttribute("sess_first_page", true);
					//System.out.println("In doGet");
					cate_id = (int)session.getAttribute("sess_cate_id");

				}
				Statement stmt = con.createStatement();
				String name = session.getAttribute("personName").toString();
				name = prepareString(name);
				System.out.println(name);
				stmt.executeUpdate(name);
				//System.out.println("In doGet");
				ArrayList<SalesAnalyticsModel> list = this.Filter(cate_id);
				//System.out.println(list.size()); TODO remove debugging message
				if(list.size() == 0) zeroresults = true;
				if(zeroresults == false) request.setAttribute("pres", 1);
				if(zeroresults == true) request.setAttribute("zeroresults", 1);
				request.setAttribute("body", list);
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
	
	private ArrayList<SalesAnalyticsModel> Filter(int cate_id) throws SQLException{
		ArrayList<SalesAnalyticsModel> list = new ArrayList<SalesAnalyticsModel>();
		//System.out.println("In doGet");
		list = entity.filter(cate_id);
		System.out.println(list.size());
		return list;
	}
}
