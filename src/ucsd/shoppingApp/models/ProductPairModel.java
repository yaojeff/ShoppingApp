package ucsd.shoppingApp.models;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductPairModel {
	private String product1;
	private String product2;
	private Double cosine;
	
	public boolean contain(String n) {
		if(product1.equals(n)) return true;
		if(product2.equals(n)) return true;
		return false;
	}
	
	public String getProduct1() { return product1; }
	public String getProduct2() { return product2; }
	public Double getCosine() { return cosine; }
	
	public ProductPairModel(ResultSet rs) throws SQLException {
		try {
			this.product1 = rs.getString("id1");
			this.product2 = rs.getString("id2");
			this.cosine = rs.getDouble("rs");
		} catch(SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}
}
