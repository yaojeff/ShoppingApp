package ucsd.shoppingApp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import ucsd.shoppingApp.models.SalesAnalyticsModel;
import ucsd.shoppingApp.models.CustomerAnalyticsModel;
import ucsd.shoppingApp.models.StateAnalyticsModel;

public class SalesAnalyticsDAO {
	private Connection con;
	
	private static final String FILTER_CUSTOMER = "SELECT person.person_name, product.product_name, SUM("
			+ "products_in_cart.price * products_in_cart.quantity) as rs FROM "
			+ "person,products_in_cart, shopping_cart, product WHERE "
			+ "products_in_cart.cart_id = shopping_cart.id"
			+ " AND shopping_cart.person_id = person.id"
			+ " AND shopping_cart.is_purchased = true"
			+ " AND product.id = products_in_cart.product_id";
	
	private static final String FILTER_STATE = "SELECT state.state_name, product.product_name, SUM("
			+ "products_in_cart.price * products_in_cart.quantity) as rs FROM "
			+ "person,products_in_cart, shopping_cart, product, state WHERE "
			+ "products_in_cart.cart_id = shopping_cart.id"
			+ " AND shopping_cart.person_id = person.id"
			+ " AND shopping_cart.is_purchased = true"
			+ " AND product.id = products_in_cart.product_id"
			+ " AND state.id = person.state_id";
	
	public SalesAnalyticsDAO(Connection con) {
		this.con = con;
	}
	
	public ArrayList<SalesAnalyticsModel> filterList(String header, String order, int cate_id) 
		throws SQLException {
		StringBuilder sb;
		String group;
		if(header.equalsIgnoreCase("customer")) {
			sb = new StringBuilder(FILTER_CUSTOMER);
			if(order.equalsIgnoreCase("alph")) 
				group = " GROUP BY person.person_name, product.product_name ORDER BY person.person_name";
			else
				group = " GROUP BY person.person_name, product.product_name ORDER BY rs DESC";				
		}
		else {
			sb = new StringBuilder(FILTER_STATE);
			if(order.equalsIgnoreCase("alph")) 
				group = " GROUP BY state.state_name, product.product_name ORDER BY state.state_name";
			else
				group = " GROUP BY state.state_name, product.product_name ORDER BY rs DESC";			
			
		}
//		if(cate_id > 0) {
//			String cat_id_filter = " AND product.category_id = ?";
//			sb = sb.append(cat_id_filter);
//		}

		sb = sb.append(group);
		System.out.println(sb.toString());
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList<SalesAnalyticsModel> result = new ArrayList<SalesAnalyticsModel>();
		try {
			pstmt = con.prepareStatement(sb.toString());

//		if(cate_id > 0) 
//			pstmt.setInt(1, cate_id);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				if(header.equalsIgnoreCase("customer"))
					result.add(new CustomerAnalyticsModel(rs));
				else
					result.add(new StateAnalyticsModel(rs));
			}
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if(rs != null)
				rs.close();
			if(pstmt != null) {
				try {
					pstmt.close();
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
