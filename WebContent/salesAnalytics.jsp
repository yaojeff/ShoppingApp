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
<script type="text/javascript">
function refresh(cat,name) {
	var xhttp;
	xhttp = new XMLHttpRequest();
	var url = "refresh.jsp";
	url = url + "?cate=" + cat+"?name=" + name;
	if(document.getElementById("GuamPROD_258").style.color == 0xff0000)
		document.getElementById("dataTable").style.color = 0x0000ff;
	else document.getElementById("GuamPROD_258").style.color = 0xff0000
	xhttp.onreadystatechange = function() {
		if(this.readyState == 4) {
			
			document.getElementById("GuamPROD_258").innerHTML = parseInt(document.getElementById("GuamPROD_258").innerHTML,10) * 2;
		}
	};
	xhttp.open("GET", url,true);
	xhttp.send(null);
}
</script>
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

			<%

			int category_id = -1;
			String name = session.getAttribute("personName").toString();
			if(request.getSession().getAttribute("sess_cate_id") != null) {
				category_id = (int)request.getSession().getAttribute("sess_cate_id");
			}
			
			%>
			<form method="GET" action="AnalyticsController">
			<table>
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
			<div id="result">
			<c:if test="${zeroresults==1}">
				<h4> No results found </h4>
			</c:if>
			<c:if test="${pres==1}">

			<input type="submit" onclick= "refresh(<%=category_id %>,<%=name %>)" value="Refresh" name="action">

			<table border='10' id='dataTable'>
			<%
			ArrayList<SalesAnalyticsModel> list = new ArrayList<SalesAnalyticsModel>();
			ArrayList<SalesAnalyticsModel> header = new ArrayList<SalesAnalyticsModel>();
			if(request.getAttribute("body") != null) 
				list = (ArrayList<SalesAnalyticsModel>) request.getAttribute("body");
			//System.out.println(list.size()); TODO remove debuging 
			%>
			<tr><td id="test"></td>
			<%
			for(int i = 0; i < 50; i++) {
				SalesAnalyticsModel entity = list.get(i);
				%>
				<td><center><b><%=entity.getProductName() %></b><br/>($<Label id=<%=entity.getProductName()%>><%=entity.getProductSum() %></Label>)</center></td>		
				<%
			}
			%></tr>
			<%

			//System.out.println("body size: " + list.size());
			/*for(SalesAnalyticsModel entity: list) {
				System.out.println(entity.getName() + entity.getSum());
			}*/
			int counter = 0;
			for(SalesAnalyticsModel entity : list) {
				if(counter == 0) {
					%>
					<tr>
					<td ><center><b><Label id=<%=entity.getStateName() %>><%=entity.getStateName()%></Label></b><br/>
					($<%=entity.getStateSum()%>)</center></td>
					<td ><center>$<Label id=<%=entity.getStateName()+entity.getProductName() %>><%=entity.getCellSum()%></Label></center></td>	
					
					<% 					
				}else {
					%>
					<td ><center>$<Label id=<%=entity.getStateName()+entity.getProductName() %>><%=entity.getCellSum()%></Label></center></td>		
					<%
					if(counter == 49) {
						counter = 0;
						%>
						</tr>
						<%
						continue;
					}
				}
				counter = (counter+1);

						
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