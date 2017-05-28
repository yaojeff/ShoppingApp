package ucsd.shoppingApp.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ucsd.shoppingApp.ConnectionManager;
import ucsd.shoppingApp.ProductPairDAO;
import ucsd.shoppingApp.models.ProductPairModel;

@WebServlet("/SimilarProductsController")
public class SimilarProductsController extends HttpServlet{

	private static final long serialVersionUID = 1L;
	private Connection con = null;
	//private Product
	
	public void destroy() {
		if(con != null) {
			try {
				con.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public SimilarProductsController() {
		con = ConnectionManager.getConnection();
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String forward = "./similarProducts.jsp";
		ArrayList<ProductPairModel> result = null;
		ProductPairDAO pair = new ProductPairDAO(con);
		try {
			if(request.getParameter("action") != null) {
				 result = pair.getProductPair();
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			request.setAttribute("productPair", result);
			RequestDispatcher view = request.getRequestDispatcher(forward);
			view.forward(request, response);
		}

	}

	
}
