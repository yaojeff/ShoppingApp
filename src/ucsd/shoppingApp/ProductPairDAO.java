package ucsd.shoppingApp;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import ucsd.shoppingApp.models.ProductPairModel;

public class ProductPairDAO {
	private static final String SIMILAR_PRO = "SELECT "
			+ "a.product_name as id1, b.product_name as id2, "
			+ "SUM(a.rs * b.rs) / (c.rs * d.rs) as rs FROM person p INNER JOIN "
			+ "(SELECT p.id, pro.product_name, pro.id as pid, SUM(pr.price*"
			+ "pr.quantity) as rs FROM person p, product pro, "
			+ "products_in_cart pr, shopping_cart sh WHERE sh.person_id = "
			+ "p.id AND pro.id = pr.product_id AND pr.cart_id = sh.id GROUP "
			+ "BY p.id, pro.id) a on a.id = p.id INNER JOIN (SELECT p.id, "
			+ "pro.product_name, pro.id as pid, SUM(pr.price*pr.quantity) "
			+ "as rs FROM person p, product pro, products_in_cart pr, "
			+ "shopping_cart sh WHERE sh.person_id = p.id AND pro.id = "
			+ "pr.product_id AND pr.cart_id = sh.id GROUP BY p.id, pro.id) "
			+ "b on b.id = p.id AND a.pid < b.pid) INNER JOIN (SELECT "
			+ "p.id, SUM(pr.price*pr.quantity) as rs FROM product p, "
			+ "products_in_cart pr, shopping_cart sh WHERE p.id = pr.product_id "
			+ "AND pr.cart_id = sh.id AND sh.is_purchased = true GROUP BY "
			+ "p.id) c on c.id = a.pid INNER JOIN (SELECT p.id, SUM(pr.price*"
			+ "pr.quantity) as rs FROM product p, products_in_cart pr, "
			+ "shopping_cart sh WHERE p.id = pr.product_id AND pr.cart_id "
			+ "= sh.id AND sh.is_purchased = true GROUP BY p.id) d on d.id "
			+ "= b.pid GROUP BY a.pid, b.pid,a.product_name,b.product_name,"
			+ "c.rs,d.rs ORDER BY rs DESC LIMIT 100";
	
	private Connection con;
	
	public ProductPairDAO(Connection con) {
		this.con = con;
	}
	
	public ArrayList<ProductPairModel> getProductPair() throws SQLException {
		Statement stmt = null;
		ResultSet rs = null;
		ArrayList<ProductPairModel> result = new ArrayList<ProductPairModel>();
		System.out.println(SIMILAR_PRO);
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(SIMILAR_PRO);
			while(rs.next()){
				result.add(new ProductPairModel(rs));
			}
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if(rs != null) rs.close();
			if(stmt != null) {
				try {
					stmt.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
			
}
