package ucsd.shoppingApp.models;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StateAnalyticsModel extends SalesAnalyticsModel{
	String name;
	
	@Override
	public String getName() { return name; }
	@Override
	public void setName(String n) { name = n; }
	
	public StateAnalyticsModel(ResultSet rs) throws SQLException{
		super(rs);
		try {
			this.name = rs.getString("state_name");
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

}