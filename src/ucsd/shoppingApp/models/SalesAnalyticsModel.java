package ucsd.shoppingApp.models;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SalesAnalyticsModel {
	private String state_name;
	private String product_name;
	private Double state_sum;
	private Double product_sum;
	private Double cell_sum;
	
	public String getProductName() { 
		if(product_name.length() > 10) return product_name.substring(0, 10); 
		else return product_name;
	}
	public String getStateName() { return state_name; }
	public Double getStateSum() { return state_sum; }
	public Double getProductSum() { return product_sum; }
	public Double getCellSum() { return cell_sum; }
	public void setStateName(String n) { state_name = n;}
	public void setProductName(String n) { product_name = n;}
	
	public SalesAnalyticsModel(ResultSet rs) throws SQLException {
		try {
			this.state_name = rs.getString("state_name");
			this.product_name = rs.getString("product_name");
			this.state_sum = rs.getDouble("state_sum");
			this.cell_sum = rs.getDouble("cell_sum");
			this.product_sum = rs.getDouble("product_sum");
			
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}
	

}
