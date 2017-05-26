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
	/*
	private class Header {
		private String rowName;
		private String colName;
		private Double rTotal;
		private Double cTotal;
		
		public Header(ResultSet rs) throws SQLException{
			try {
				this.rowName = rs.getString("row");
				this.rTotal = rs.getDouble("ars");
				this.colName = rs.getString("col");
				this.cTotal = rs.getDouble("brs");
				//System.out.println(name);
			} catch (SQLException e) {
				e.printStackTrace();
				throw e;
			}			
		}
		
		public String getRow() { return rowName; }
		public String getCol() { return colName; }
		public Double getr() { return rTotal; }
		public Double getc() { return cTotal; }
	}
	
	private ArrayList<Header> header = new ArrayList<Header>();*/
	private ArrayList<Integer> header = new ArrayList<Integer>();
	
	private static final String TOPK_H = "SELECT p.product_name, p.id, SUM(pr.price * pr.quantity) as rs"
		    + " FROM product as p, products_in_cart as pr,"
		    + " shopping_cart as sh WHERE p.id = pr.product_id AND pr.cart_id = sh.id"
		    + " AND sh.is_purchased = true GROUP BY p.product_name, p.id"
		    + " ORDER BY rs DESC";
	
	private static final String ALPH_H = "SELECT p.product_name, p.id, SUM(pr.price * pr.quantity) as rs"
		    + " FROM product as p, products_in_cart as pr,"
		    + " shopping_cart as sh WHERE p.id = pr.product_id AND pr.cart_id = sh.id"
		    + " AND sh.is_purchased = true GROUP BY p.product_name, p.id"
		    + " ORDER BY p.product_name";
	
	private static final String TOPK_CATE_H = "SELECT p.product_name, p.id, SUM(pr.price * pr.quantity) as rs"
		    + " FROM product as p, products_in_cart as pr,"
		    + " shopping_cart as sh WHERE p.id = pr.product_id AND pr.cart_id = sh.id"
		    + " AND sh.is_purchased = true AND p.category_id = ? GROUP BY p.product_name, p.id"
		    + " ORDER BY rs DESC";
	
	private static final String ALPH_CATE_H = "SELECT p.product_name, p.id, SUM(pr.price * pr.quantity) as rs"
		    + " FROM product as p, products_in_cart as pr,"
		    + " shopping_cart as sh WHERE p.id = pr.product_id AND pr.cart_id = sh.id"
		    + " AND sh.is_purchased = true AND p.category_id = ? GROUP BY p.product_name, p.id"
		    + " ORDER BY p.product_name";
	
	public SalesAnalyticsDAO(Connection con) {
		this.con = con;
	}
	
	public ArrayList<SalesAnalyticsModel> filterB(String header, String order) throws SQLException{
		ArrayList<SalesAnalyticsModel> list = new ArrayList<SalesAnalyticsModel>();
				
		return list;
	}
	
	public ArrayList<SalesAnalyticsModel> filterBC(String header, String order, int cate_id) throws SQLException{
		ArrayList<SalesAnalyticsModel> list = new ArrayList<SalesAnalyticsModel>();
				
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

}
