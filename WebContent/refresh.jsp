<%@ page import="java.sql.*"%>
<% response.setContentType("application/json") ; %>
<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="org.json.*, java.lang.*"%>
<%@ page import="java.util.*" %>
<%
class SaleData{
	private String id;
	private String state_name;
	private String product_name;
	private Double cell_sum;
	private boolean is_new;
	
	public String getID() { return id; }
	public String getStateName() { return state_name; }
	public String getProductName() { return product_name; }
	public Double getCellSum() { return cell_sum; }
	public boolean getIsNew() { return is_new; }
	
	SaleData(ResultSet rs) throws SQLException{
		try {
			state_name = rs.getString("state_name");
			product_name = rs.getString("product_name");
			cell_sum = rs.getDouble("cell_sum");
			is_new = rs.getBoolean("is_new");
			id = state_name + product_name;
		}catch(Exception e) {
			throw e;
		}
	}
	
};

ArrayList<SaleData> sales = new ArrayList<SaleData>();

try {
	Class.forName("org.postgresql.Driver");  
	Connection con=DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/postgres","postgres","1234");
    con.setAutoCommit(false);
    
    int category_id = Integer.parseInt(request.getParameter("cate").toString());
    String user_name = request.getParameter("name").toString();
    String get_log = " SELECT l.state_name, l.product_name, l.cell_sum, lt.is_new " +
    		" FROM observed_user o, log_tracing lt, log_table l " +
    		" WHERE o.user_name = '" + user_name + "' AND o.id = lt.observed_user_id AND lt.log_id = l.id ";
    
    Statement stmt = con.createStatement();
    ResultSet rs = stmt.executeQuery(get_log);
    while(rs.next()) {
    	sales.add(new SaleData(rs));
    }
    con.close();
}catch(Exception e) {
	e.printStackTrace();
}

JSONObject jObject = new JSONObject();
try {
	JSONArray jArray = new JSONArray();
	for(SaleData s : sales) {
		JSONObject cJSON = new JSONObject();
		cJSON.put("id",s.getID());
		cJSON.put("state_name",s.getStateName());
		cJSON.put("product_name",s.getProductName());
		cJSON.put("cell_sum",s.getCellSum());
		cJSON.put("is_new",s.getIsNew());
	}
} catch(Exception jse) {
	jse.printStackTrace();
}

response.getWriter().print(jObject);

%>