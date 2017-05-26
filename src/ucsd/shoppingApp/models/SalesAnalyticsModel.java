package ucsd.shoppingApp.models;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SalesAnalyticsModel {
	private String product;
	private Double sum;
	
	public String getProduct() { return product; }
	public Double getSum() { return sum; }
	public String getName() { return ""; }
	public void setName(String n) {}
	public void setProduct(String p) { product = p; }
	public void setSum(Double s) { sum = s;}
	
	public SalesAnalyticsModel(ResultSet rs) throws SQLException {
		try {
			this.product = rs.getString("product_name");
			this.sum = rs.getDouble("rs");
			//System.out.println(name);
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}	
}
