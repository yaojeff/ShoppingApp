package ucsd.shoppingApp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import ucsd.shoppingApp.models.SalesAnalyticsModel;

public class SalesAnalyticsDAO {
	private Connection con;

	private ArrayList<Integer> header;
	private ArrayList<SalesAnalyticsModel> rowH;
	
	
	private static final String TOPK = "with overall_table as"
			+ " (select pc.product_id,c.state_id,sum(pc.price*pc.quantity) as amount"
			+ " from products_in_cart pc"
			+ " inner join shopping_cart sc on (sc.id = pc.cart_id and sc.is_purchased = true)"
			+ " inner join product p on (pc.product_id = p.id)"
			+ " inner join person c on (sc.person_id = c.id)"
			+ " group by pc.product_id,c.state_id),"
			+ " top_state as (select state_id, sum(amount) as dollar from ("
			+ " select state_id, amount from overall_table"
			+ " UNION ALL"
			+ " select id as state_id, 0.0 as amount from state"
			+ " ) as state_union"
			+ " group by state_id order by dollar desc"
			+ " ),top_n_state as (select row_number() over(order by dollar desc) as state_order, state_id,"
			+ " dollar from top_state),top_prod as (select product_id, sum(amount) as dollar from ("
			+ " select product_id, amount from overall_table"
			+ " UNION ALL select id as product_id, 0.0 as amount from product"
			+ " ) as product_union"
			+ " group by product_id order by dollar desc limit 50"
			+ " ),top_n_prod as (select row_number() over(order by dollar desc) as product_order, product_id,"
			+ " dollar from top_prod) select s.state_name, pr.product_name, "
			+ " COALESCE(ot.amount, 0.0) as cell_sum, ts.dollar as state_sum, tp.dollar as product_sum"
			+ " from top_n_prod tp CROSS JOIN top_n_state ts LEFT OUTER JOIN overall_table ot"
			+ " ON ( tp.product_id = ot.product_id and ts.state_id = ot.state_id)"
			+ " inner join state s ON ts.state_id = s.id"
			+ " inner join product pr ON tp.product_id = pr.id"
			+ " order by ts.state_order, tp.product_order";
	
	private static final String TOPK_CATE = "with overall_table as"
			+ " (select pc.product_id,c.state_id,sum(pc.price*pc.quantity) as amount"
			+ " from products_in_cart pc"
			+ " inner join shopping_cart sc on (sc.id = pc.cart_id and sc.is_purchased = true)"
			+ " inner join product p on (pc.product_id = p.id) AND (pc.category_id = ?)"
			+ " inner join person c on (sc.person_id = c.id)"
			+ " group by pc.product_id,c.state_id),"
			+ " top_state as (select state_id, sum(amount) as dollar from ("
			+ " select state_id, amount from overall_table"
			+ " UNION ALL"
			+ " select id as state_id, 0.0 as amount from state"
			+ " ) as state_union"
			+ " group by state_id order by dollar desc"
			+ " ),top_n_state as (select row_number() over(order by dollar desc) as state_order, state_id,"
			+ " dollar from top_state),top_prod as (select product_id, sum(amount) as dollar from ("
			+ " select product_id, amount from overall_table"
			+ " UNION ALL select id as product_id, 0.0 as amount from product"
			+ " ) as product_union"
			+ " group by product_id order by dollar desc limit 50"
			+ " ),top_n_prod as (select row_number() over(order by dollar desc) as product_order, product_id,"
			+ " dollar from top_prod) select s.state_name, pr.product_name, "
			+ " COALESCE(ot.amount, 0.0) as cell_sum, ts.dollar as state_sum, tp.dollar as product_sum"
			+ " from top_n_prod tp CROSS JOIN top_n_state ts LEFT OUTER JOIN overall_table ot"
			+ " ON ( tp.product_id = ot.product_id and ts.state_id = ot.state_id)"
			+ " inner join state s ON ts.state_id = s.id"
			+ " inner join product pr ON tp.product_id = pr.id"
			+ " order by ts.state_order, tp.product_order";
	
	public SalesAnalyticsDAO(Connection con) {
		this.rowH = new ArrayList<SalesAnalyticsModel>();
		this.header = new ArrayList<Integer>();
		this.con = con;
	}		
	
	public boolean endCol() {
		return header.size() < 50;
	}
	
	public ArrayList<SalesAnalyticsModel> filter() throws SQLException{
		ArrayList<SalesAnalyticsModel> list = new ArrayList<SalesAnalyticsModel>();
		ResultSet rs = null;
		Statement stmt = null;
		StringBuilder sb = null;
		sb = new StringBuilder(TOPK);
		System.out.println(sb.toString());
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sb.toString());
		while(rs.next())
			list.add(new SalesAnalyticsModel(rs));
					
		} catch(SQLException e) {
			e.printStackTrace();
			throw e;
		}

		if(rs != null) rs.close();
		if(stmt != null) {
			try {
				stmt.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	
	
	
	public ArrayList<SalesAnalyticsModel> filterCate(int cate_id) throws SQLException{
		ArrayList<SalesAnalyticsModel> list = new ArrayList<SalesAnalyticsModel>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		StringBuilder sb = null;
		sb = new StringBuilder(TOPK);
		System.out.println(sb.toString());
		try {
			pstmt = con.prepareStatement(sb.toString());
			pstmt.setInt(1, cate_id);
			rs = pstmt.executeQuery();
		while(rs.next())
			list.add(new SalesAnalyticsModel(rs));
					
		} catch(SQLException e) {
			e.printStackTrace();
			throw e;
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
	
	

	
	public void reset() {
		rowH.clear();
		header.clear();
	}

}
