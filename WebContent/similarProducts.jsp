<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="ucsd.shoppingApp.models.* , java.util.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Similar Products</title>
</head>
<body>
	<% if(session.getAttribute("roleName" ) != null) { 
	%>
		<table cellspacing="5">
		<tr>
		<td valign="top"> <jsp:include page="./menu.jsp"></jsp:include></td>
		<td></td>
		<td>
			<h3>Hello <%= session.getAttribute("personName") %></h3>
		<h3>Similar Products</h3>
		
		
    <%
    	ArrayList<ProductPairModel> pair = (ArrayList<ProductPairModel>) request.getAttribute("productPair");
    	if(pair.size() > 0) {
    		%> <table border='1'><%
    	
    	for(ProductPairModel entity : pair) {
    		%><tr>
    		<td><center><%=entity.getProduct1() %></center></td>
    		<td><center><%=entity.getProduct2() %></center></td>
    		<td><center><%=entity.getCosine() %></center></td>
    		</tr><%
    	}

    		%> </table> <%
    	}
    	
	} else {%>
		<h3>Please <a href = "./login.jsp">login</a> before viewing the page</h3>
	<% }
	%>
	</td>
	</tr>
	</table>
</body>
</html>