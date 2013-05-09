<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>

<style>
span.highlight {
	background-color: #9999ff;
}
</style>

</head>
<body>
	<p>hello</p>

	<form method="post" action=".">
		<textarea cols="80" rows="10" name="text"><%=(request.getAttribute("TEXTAREA_TEXT"))%></textarea>
		<input type="submit" value="Submit">
	</form>

	<p>
		<%=(request.getAttribute("HIGHLIGHTED_HTML"))%>
	</p>

</body>
</html>