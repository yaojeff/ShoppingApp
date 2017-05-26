package ucsd.shoppingApp.models;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerAnalyticsModel extends SalesAnalyticsModel{
	String name;
	
	@Override
	public String getName() { return name; }
	@Override
	public void setName(String n) { name = n; }
	
	public CustomerAnalyticsModel(ResultSet rs) throws SQLException{
		super(rs);
		try {
			this.name = rs.getString("person_name");
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

}
