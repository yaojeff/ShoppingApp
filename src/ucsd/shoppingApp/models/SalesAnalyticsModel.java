package ucsd.shoppingApp.models;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SalesAnalyticsModel {
	private String name;
	private Double sum;
	
	public String getHName() { 
		if(name.length() > 10) return name.substring(0, 10); 
		else return name;
	}
	public String getName() { return name; }
	public Double getSum() { return sum; }
	public void setName(String n) { name = n;}
	public void setSum(Double s) { sum = s;}
	
	public SalesAnalyticsModel(ResultSet rs) throws SQLException {
		try {
			this.name = rs.getString("name");
			this.sum = rs.getDouble("rs");
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public SalesAnalyticsModel(String n, Double s) {
		name = n;
		sum = s;
	}

}
