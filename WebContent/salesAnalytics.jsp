<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
 <%@page import="java.sql.Connection, ucsd.shoppingApp.ConnectionManager, ucsd.shoppingApp.*"%>
 <%@ page import="ucsd.shoppingApp.models.* , java.util.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Sales Analytics</title>
</head>
<body>
	<% if(session.getAttribute("roleName" ) != null) { 
		String role = session.getAttribute("roleName").toString();
		if("owner".equalsIgnoreCase(role) == true) {
			Connection con = ConnectionManager.getConnection();
			CategoryDAO categoryDao = new CategoryDAO(con);
			List<CategoryModel> category_list = categoryDao.getCategories();
			con.close();

	%>
		<table cellspacing="5">
			<tr>
			<td valign="top"> <jsp:include page="./menu.jsp"></jsp:include></td>
			<td></td>
			<td>
				<h3>Hello <%= session.getAttribute("personName") %></h3>
			<h3>Sales Analytics</h3>
			<form method="GET" action="AnalyticsController">
			<%
			if(request.getSession().getAttribute("sess_first_page") == null ) {
				int category_id = -1;
				if(request.getSession().getAttribute("sess_cate_id") != null) {
					category_id = (int)request.getSession().getAttribute("sess_cate_id");
				}
				System.out.println(category_id);
			
			%>

				<table>
				<tr><td>
				Row header : 
				</td><td> 
				<select required name="row_header">
					<option value = "Customer">Customer</option>
					<option value = "State">State</option>
				</select>
				</td></tr>
				<tr><td>
				Order : 
				</td><td> 
				<select required name="order">
					<option value = "alph">Alphabetical</option>
					<option value = "topK">Top-K</option>
				</select>
				</td></tr>
				<tr><td>
				Category : 
				</td><td> 
				<select name="cate">
					<option value="-1">All</option>
					<%
						for (CategoryModel cat : category_list) {
					%>
					<option value="<%=cat.getId()%>" <%if (cat.getId() == category_id) { %> selected="selected" <%} %>> 
						<%=cat.getCategoryName()%>
					</option>
					<%
						}
					%>
				</select>
				</td></tr>
				<tr>
					<td>
						<input type="submit" value="Run Query" name="action">
					</td>
				</tr>
				</table>
			</form>
			<%
			}
			%>
			<div id="result">
			<c:if test="${zeroresults==1}">
				<h4> No results found </h4>
			</c:if>
			<c:if test="${pres==1}">
				<table border='10'>
				
			<%
			ArrayList<SalesAnalyticsModel> list = new ArrayList<SalesAnalyticsModel>();
			ArrayList<SalesAnalyticsModel> header = new ArrayList<SalesAnalyticsModel>();
			if(request.getAttribute("header") != null) 
				header = (ArrayList<SalesAnalyticsModel>) request.getAttribute("header");
			if(request.getAttribute("body") != null) 
				list = (ArrayList<SalesAnalyticsModel>) request.getAttribute("body");
			//System.out.println(list.size()); TODO remove debuging 
			%>
			<tr><td></td>
			<%
			for(SalesAnalyticsModel entity : header) {
				%>
				<td><b><%=entity.getName() %></b><br/>{$<%=entity.getSum() %>}</td>		
				<%
			}
			%></tr>
			<%

			//System.out.println("body size: " + list.size());
			/*for(SalesAnalyticsModel entity: list) {
				System.out.println(entity.getName() + entity.getSum());
			}*/
			String nameCheck = "";
			boolean endOfRow = true;
			for(SalesAnalyticsModel entity : list) {
				if(!nameCheck.equals(entity.getName()) && endOfRow) {
					nameCheck = entity.getName();
					%>
					<tr>
					<td><b><%=entity.getName()%></b><br/>
					{$<%=entity.getSum()%>}</td>
					
					<% 
					endOfRow = false;
					continue;
				}
				if(nameCheck.equals(entity.getName()) && !endOfRow) {
					endOfRow = true;
					%>
					</tr>
					<% 
					continue;
				}				
				
				%>
				<td>
				<%=entity.getSum()%></td>
				
				<% 		
			}
			%>
			</table></c:if></div>
		</td>
		</table>
	<%   
		} else { %>
			<h3>This page is available to owners only</h3>	
			<%		
		}
	} else {%>
		<h3>Please <a href = "./login.jsp">login</a> before viewing the page</h3>
	<% }
	%>
</body>
</html>