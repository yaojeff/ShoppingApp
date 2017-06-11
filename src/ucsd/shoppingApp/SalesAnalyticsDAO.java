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
	
	private static final String TOPK = "with"
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
			+ " group by product_id order by dollar desc"
			+ " ),top_n_prod as (select row_number() over(order by dollar desc) as product_order, product_id,"
			+ " dollar from top_prod), rs as ( select s.state_name, pr.product_name, "
			+ " COALESCE(ot.amount, 0.0) as cell_sum, ts.dollar as state_sum, tp.dollar as product_sum"
			+ " from top_n_prod tp CROSS JOIN top_n_state ts LEFT OUTER JOIN overall_table ot"
			+ " ON ( tp.product_id = ot.product_id and ts.state_id = ot.state_id)"
			+ " inner join state s ON ts.state_id = s.id"
			+ " inner join product pr ON tp.product_id = pr.id"
			+ " order by ts.state_order, tp.product_order )"
			+ " INSERT INTO pre_comp (state_name, product_name, cell_sum, state_sum, product_sum)"
			+ " SELECT state_name, product_name, cell_sum, state_sum, product_sum from rs";;
	
	private static final String INIT_PRE_TABLE =
			" do $$ "
			+ " begin "
			+ " IF NOT EXISTS ( "
			+ "  SELECT 1 "
			+ "   FROM   information_schema.tables "
			+ "  WHERE  table_schema = 'public' "
			+ "  AND    table_name = 'pre_comp' "
			+ "   ) "
			+ " THEN "
			+ "	CREATE TABLE pre_comp (state_name TEXT, product_name TEXT, cell_sum DOUBLE PRECISION, state_sum DOUBLE PRECISION, product_sum DOUBLE PRECISION); "
			+ " ELSE "
			+ "	DROP TABLE pre_comp; "
			+ "	CREATE TABLE pre_comp (state_name TEXT, product_name TEXT, cell_sum DOUBLE PRECISION, state_sum DOUBLE PRECISION, product_sum DOUBLE PRECISION);"
			+ " END IF; "
			+ " end "
			+ " $$ ";;
	
	private static final String TABLE = "select state_name, product_name, cell_sum, state_sum, product_sum from"
			+ " (select p.*, row_number() over (partition by p.state_name order by p.product_sum desc) as rownum"
			+ " from pre_comp p) x where x.rownum <= 50 order by x.state_sum desc, x.product_sum desc;";
	
	
	private static final String OVERALL_NONFILTER = "TRUNCATE overall_table; INSERT INTO overall_table (select * FROM overall_tableN)";
	
	private static final String OVERALL_FILTER = "TRUNCATE overall_table; INSERT INTO overall_table(state_id,product_id,amount) (select state_id, product_id, amount FROM overall_tableF WHERE overall_tableF.category_id = ?)";
	
	public SalesAnalyticsDAO(Connection con) {
		this.con = con;
	}		
	
	public void initialPrecompute() throws SQLException {
		Statement stmt = null;
		StringBuilder sb = null;
		sb = new StringBuilder(INIT_PRE_TABLE);
		System.out.println(sb.toString());
		try {
			stmt = con.createStatement();
			stmt.executeUpdate(sb.toString());
		} catch(SQLException e) {
			e.printStackTrace();
			throw e;
		}
		
		if(stmt != null) {
			try {
				stmt.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}		
	}
	
	public void initialOverall(int cate_id) throws SQLException {
		PreparedStatement stmt = null;
		StringBuilder sb = null;
		if(cate_id < 0)
			sb = new StringBuilder(OVERALL_NONFILTER);
		else
			sb = new StringBuilder(OVERALL_FILTER);
		try {

			stmt = con.prepareStatement(sb.toString());
			if(cate_id > 0)
				stmt.setInt(1, cate_id);
			System.out.println(sb.toString());
			stmt.executeUpdate();
		} catch(SQLException e) {
			e.printStackTrace();
			throw e;
		}
		
		if(stmt != null) {
			try {
				stmt.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void precomputTable(int cate_id) throws SQLException{
		PreparedStatement stmt = null;
		StringBuilder sb = null;
		initialOverall(cate_id);

		sb = new StringBuilder(TOPK);
		try {
			initialPrecompute();
			System.out.println(sb.toString());
			stmt = con.prepareStatement(sb.toString());
			stmt.executeUpdate();
		} catch(SQLException e) {
			e.printStackTrace();
			throw e;
		}
		
		if(stmt != null) {
			try {
				stmt.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public ArrayList<SalesAnalyticsModel> filter(int cate_id) throws SQLException{
		ArrayList<SalesAnalyticsModel> list = new ArrayList<SalesAnalyticsModel>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		StringBuilder sb = null;
		sb = new StringBuilder(TABLE);
		try {
			System.out.println("In DAO");
			precomputTable(cate_id);
			System.out.println(sb.toString());
			pstmt = con.prepareStatement(sb.toString());
			rs = pstmt.executeQuery();
			while(rs.next())
			list.add(new SalesAnalyticsModel(rs));
			con.commit();
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
	

}
