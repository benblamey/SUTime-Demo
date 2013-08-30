<%@ page import="benblamey.sutime.*" %>
<%@ page import="java.util.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Parsing Temporal Expressions with Distributed Semantics</title>
	<link rel="stylesheet" type="text/css" href="styles.css">

<style>
span.highlight {
	background-color: #9999ff;
}
</style>

</head>
<body>
	<h1>The First Day of Summer: Parsing Temporal Expressions with Distributed Semantics</h1>
	<p>Research Paper under review.</p>

	<form method="post" action="Index">
	
	<div>
		<textarea cols="80" rows="10" name="text"><%=(request.getAttribute("TEXTAREA_TEXT"))%></textarea>
		</div>
		<div>
		
		<input type="submit" value="Submit">
		</div>
	</form>

	<p>
		<%=(request.getAttribute("HIGHLIGHTED_HTML"))%>
	</p>
	
	<hr/>
	
	<% for (@SuppressWarnings("unchecked") AnnotationViewModel vm : (List<AnnotationViewModel>)request.getAttribute("VIEW_MODELS")) { %>
	
<pre><code><%=vm.getHTMLTimexXML()%></code></pre>

<img src="<%=vm.getGnuplotImage(request) %>" />
	
	
	<% } %>

</body>
</html>