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

	private ArrayList<Integer> header = new ArrayList<Integer>();
	private ArrayList<SalesAnalyticsModel> rowH = new ArrayList<SalesAnalyticsModel>();
	
	private static final String TOPK_H = "SELECT p.product_name as name, p.id, COALESCE(a.rs,0) as rs FROM"
			+ " product p LEFT JOIN (SELECT p.product_name, p.id, SUM(pr.price * pr.quantity) as rs"
		    + " FROM product as p, products_in_cart as pr,"
		    + " shopping_cart as sh WHERE p.id = pr.product_id AND pr.cart_id = sh.id"
		    + " AND sh.is_purchased = true GROUP BY p.product_name, p.id) a on a.product_name ="
		    + " p.product_name ORDER BY rs DESC NULLS LAST";
	
	private static final String TOPK_C_ROW = "SELECT p.person_name as name, COALESCE(a.rs,0) as rs FROM person as p LEFT JOIN("
			+ " SELECT p.person_name as name, SUM(pr.price * pr.quantity) as rs"
            + " FROM person as p, shopping_cart as s, products_in_cart as pr"
            + " WHERE p.id = s.person_id AND s.is_purchased = true AND pr.cart_id = s.id"
            + " GROUP BY p.id) a on p.person_name = a.name ORDER BY rs DESC NULLS LAST";
	
	private static final String TOPK_S_ROW = "SELECT st.state_name as name, COALESCE(a.rs,0) as rs FROM"
			+ " state st LEFT JOIN (SELECT st.state_name as name, SUM(pr.price * pr.quantity) as rs"
            + " FROM person as p, shopping_cart as s, products_in_cart as pr, state as st"
            + " WHERE p.id = s.person_id AND s.is_purchased = true AND pr.cart_id = s.id AND p.state_id = st.id"
            + " GROUP BY st.state_name) a on st.state_name = a.name ORDER BY rs DESC NULLS LAST";
	
	private static final String ALPH_C_ROW = "SELECT p.person_name as name, COALESCE(a.rs,0) as rs FROM"
			+ " person as p LEFT JOIN (SELECT p.person_name as name, SUM(pr.price * pr.quantity) as rs"
            + " FROM person as p, shopping_cart as s, products_in_cart as pr"
            + " WHERE p.id = s.person_id AND s.is_purchased = true AND pr.cart_id = s.id"
            + " GROUP BY p.id) a on p.person_name = a.name ORDER BY p.person_name";
	
	private static final String ALPH_S_ROW = "SELECT st.state_name as name, COALESCE(a.rs, 0) as rs FROM"
			+ " state st LEFT JOIN (SELECT st.state_name as name, SUM(pr.price * pr.quantity) as rs"
            + " FROM person as p, shopping_cart as s, products_in_cart as pr, state as st"
            + " WHERE p.id = s.person_id AND s.is_purchased = true AND pr.cart_id = s.id AND p.state_id = st.id"
            + " GROUP BY st.state_name) a on st.state_name = a.name ORDER BY st.state_name";
	
	private static final String ALPH_H = "SELECT p.product_name as name, p.id, COALESCE(a.rs,0) as rs FROM"
			+ " product p LEFT JOIN (SELECT p.product_name, p.id, SUM(pr.price * pr.quantity) as rs"
		    + " FROM product as p, products_in_cart as pr,"
		    + " shopping_cart as sh WHERE p.id = pr.product_id AND pr.cart_id = sh.id"
		    + " AND sh.is_purchased = true GROUP BY p.product_name, p.id) a on p.id = a.id"
		    + " ORDER BY p.product_name";
	
	private static final String TOPK_CATE_H = "SELECT p.product_name as name, p.id, COALESCE(a.rs,0) as rs FROM"
			+ " product p LEFT JOIN (SELECT p.product_name, p.id, SUM(pr.price * pr.quantity) as rs"
		    + " FROM product as p, products_in_cart as pr,"
		    + " shopping_cart as sh WHERE p.id = pr.product_id AND pr.cart_id = sh.id"
		    + " AND sh.is_purchased = true AND p.category_id = ? GROUP BY p.product_name, p.id) a  on p.id = a.id"
		    + " ORDER BY rs DESC NULLS LAST";
	
	private static final String TOPK_CATE_S_ROW = "SELECT st.state_name as name, COALESCE(a.rs, 0) as rs FROM"
			+ " state st LEFT JOIN (SELECT st.state_name as name, SUM(pr.price * pr.quantity) as rs"
            + " FROM person as p, shopping_cart as s, products_in_cart as pr, state as st, product as pro"
            + " WHERE p.id = s.person_id AND s.is_purchased = true AND pr.cart_id = s.id AND p.state_id = st.id"
            + " AND pro.id = pr.product_id AND pro.category_id = ?"
            + " GROUP BY st.state_name) a on st.state_name = a.name ORDER BY rs DESC NULLS LAST";
	
	private static final String ALPH_CATE_H = "SELECT p.product_name as name, p.id, COALESCE(a.rs,0) as rs FROM"
			+ " product p LEFT JOIN (SELECT p.product_name, p.id, SUM(pr.price * pr.quantity) as rs"
		    + " FROM product as p, products_in_cart as pr,"
		    + " shopping_cart as sh WHERE p.id = pr.product_id AND pr.cart_id = sh.id"
		    + " AND sh.is_purchased = true AND p.category_id = ? GROUP BY p.product_name, p.id) a on"
		    + " a.id = p.id) a on p.id = a.id ORDER BY p.product_name";
	
	private static final String ALPH_CATE_S_ROW = "SELECT st.state_name as name, COALESCE(a.rs, 0) as rs FROM"
			+ " state st LEFT JOIN (SELECT st.state_name as name, SUM(pr.price * pr.quantity) as rs"
            + " FROM person as p, shopping_cart as s, products_in_cart as pr, state as st, product as pro"
            + " WHERE p.id = s.person_id AND s.is_purchased = true AND pr.cart_id = s.id AND p.state_id = st.id"
            + " AND pro.id = pr.product_id AND pro.category_id = ?"
            + " GROUP BY st.state_name) a on st.state_name = a.name ORDER BY st.state_name";
	
	private static final String TOPK_CATE_C_ROW = "SELECT p.person_name as name, COALESCE(a.rs,0) as rs FROM"
			+ " person as p LEFT JOIN (SELECT p.person_name as name, SUM(pr.price * pr.quantity) as rs"
            + " FROM person as p, shopping_cart as s, products_in_cart as pr, product as pro"
            + " WHERE p.id = s.person_id AND s.is_purchased = true AND pr.cart_id = s.id AND"
            + " pro.id = pr.product_id AND pro.category_id = ?"
            + " GROUP BY p.id) a on a.name = p.person_name ORDER BY rs DESC NULLS LAST";
	
	private static final String ALPH_CATE_C_ROW = "SELECT p.person_name as name, COALESCE(a.rs,0) as rs FROM"
			+ " person as p LEFT JOIN (SELECT p.person_name as name, SUM(pr.price * pr.quantity) as rs"
            + " FROM person as p, shopping_cart as s, products_in_cart as pr, product as pro"
            + " WHERE p.id = s.person_id AND s.is_purchased = true AND pr.cart_id = s.id AND"
            + " pro.id = pr.product_id AND pro.category_id = ?"
            + " GROUP BY p.id) a on a.name = p.person_name ORDER BY p.person_name";
	
	private static final String BODY = "SELECT pro.product_name as name, COALESCE(SUM(pr.price * pr.quantity),0) as rs "
			+ " FROM person as p, shopping_cart as s, products_in_cart as pr, product as pro WHERE p.id = s.person_id"
			+ " AND s.is_purchased = true AND pr.cart_id = s.id AND pro.id = pr.product_id"
			+ " AND pro.id = ? AND p.person_name = ? GROUP by p.id, pro.id";
	
	public SalesAnalyticsDAO(Connection con) {
		this.con = con;
	}
	
	public ArrayList<SalesAnalyticsModel> filterB() throws SQLException{
		ArrayList<SalesAnalyticsModel> list = new ArrayList<SalesAnalyticsModel>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		System.out.println(BODY);
		for(SalesAnalyticsModel entity : rowH) {
			list.add(new SalesAnalyticsModel(entity.getName(), entity.getSum()));
			for(Integer t : header) {
				try {
					pstmt = con.prepareStatement(BODY);
					pstmt.setInt(1, t);
					pstmt.setString(2, entity.getName());
					rs = pstmt.executeQuery();
					while(rs.next()) {
						list.add(new SalesAnalyticsModel(rs));
					}
				} catch(SQLException e) {
					e.printStackTrace();
					throw e;
				}
			}
			list.add(new SalesAnalyticsModel(entity.getName(), entity.getSum()));
		}
		if(rs != null) rs.close();
		if(pstmt != null) {
			try {
				pstmt.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	
	
	public ArrayList<SalesAnalyticsModel> filterH(String order) throws SQLException{
		ArrayList<SalesAnalyticsModel> list = new ArrayList<SalesAnalyticsModel>();
		StringBuilder sb;
		if(order.equalsIgnoreCase("topK"))
			sb = new StringBuilder(TOPK_H);
		else
			sb = new StringBuilder(ALPH_H);
		
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.createStatement();
			System.out.println(sb.toString()); //TODO remove debugging message
			rs = stmt.executeQuery(sb.toString());
			while(rs.next()) {
				header.add(rs.getInt("id"));
				list.add(new SalesAnalyticsModel(rs));
			}
			return list;
		}catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (rs != null) 
				rs.close();
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public ArrayList<SalesAnalyticsModel> filterHC(String order, int cate_id) throws SQLException{
		ArrayList<SalesAnalyticsModel> list = new ArrayList<SalesAnalyticsModel>();
		StringBuilder sb;
		if(order.equalsIgnoreCase("topK"))
			sb = new StringBuilder(TOPK_CATE_H);
		else
			sb = new StringBuilder(ALPH_CATE_H);
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = con.prepareStatement(sb.toString());
			pstmt.setInt(1, cate_id);
			System.out.println(sb.toString()); //TODO remove debugging message
			rs = pstmt.executeQuery();
			while(rs.next()) {
				header.add(rs.getInt("id"));
				list.add(new SalesAnalyticsModel(rs));
			}
			return list;
		}catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (rs != null) 
				rs.close();
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}		
	}
	
	public void rowQueryCustomer(String order, int cate_id) throws SQLException{
		StringBuilder sb;
		if(order.equalsIgnoreCase("topK") && cate_id > 0)
			sb = new StringBuilder(TOPK_CATE_C_ROW);
		else if (order.equalsIgnoreCase("alph") && cate_id > 0)
			sb = new StringBuilder(ALPH_CATE_C_ROW);
		else if (order.equalsIgnoreCase("topK") && cate_id <= 0)
			sb = new StringBuilder(TOPK_C_ROW);
		else
			sb = new StringBuilder(ALPH_C_ROW);
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = con.prepareStatement(sb.toString());
			if(cate_id > 0)
				pstmt.setInt(1, cate_id);
			System.out.println(sb.toString()); //TODO remove debugging message
			rs = pstmt.executeQuery();
			while(rs.next()) {
				rowH.add(new SalesAnalyticsModel(rs));
			}
		}catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (rs != null) 
				rs.close();
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		for(SalesAnalyticsModel t : rowH) {
			System.out.println(t.getName());
		}
	}
	
	public void rowQueryState(String order, int cate_id) throws SQLException{
		StringBuilder sb;
		if(order.equalsIgnoreCase("topK") && cate_id > 0)
			sb = new StringBuilder(TOPK_CATE_S_ROW);
		else if (order.equalsIgnoreCase("alph") && cate_id > 0)
			sb = new StringBuilder(ALPH_CATE_S_ROW);
		else if (order.equalsIgnoreCase("topK") && cate_id <= 0)
			sb = new StringBuilder(TOPK_S_ROW);
		else
			sb = new StringBuilder(ALPH_S_ROW);
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = con.prepareStatement(sb.toString());
			if(cate_id > 0)
				pstmt.setInt(1, cate_id);
			System.out.println(sb.toString()); //TODO remove debugging message
			rs = pstmt.executeQuery();
			while(rs.next()) {
				rowH.add(new SalesAnalyticsModel(rs));
			}
		}catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (rs != null) 
				rs.close();
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}			
	}

}
