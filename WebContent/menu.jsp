<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
</head>
<body>
<h3>Menu</h3>
<ul>
	<% if(session.getAttribute("roleName") != null) {
	%>	
		<% if(session.getAttribute("roleName").equals("Owner")) {
			out.write("<li><a href='./CategoryController?action=listCategories'/>Categories</a></li>");
			out.write("<li><a href='./product.jsp'/>Products</a></li>");
			out.write("<li><a href='./salesAnalytics.jsp'>Sales Analytics</a></li>");
		}	
		%>
		<li><a href='productsearch.jsp'>Products Browsing</a></li>
		<li><a href='./BuyController'>Buy Shopping Cart</a></li>
		<li><a href='buyOrders.jsp'>Buy N Products</a></li>
		<li><a href='./SimilarProductsController?action=pairs'>Similar Products</a></li>
	<% } %>
</ul>
</body>
</html>