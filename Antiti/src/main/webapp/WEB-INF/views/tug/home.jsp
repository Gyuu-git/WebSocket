<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
	<title>Home</title>
	<link rel="stylesheet" href="https://uicdn.toast.com/grid/latest/tui-grid.css" />
	<script src="https://uicdn.toast.com/grid/latest/tui-grid.js"></script>
	<script type="text/javascript" src="/resources/js/jquery-3.6.1.min.js"></script>
</head>
<body>
<script type="text/javascript">
window.onload = function(){
	var gridData;
	$.ajax({
		url : "/preBoard/getPreBoardList",
		method :"GET",
		dataType : "JSON",
		success : function(result){
			gridData = result; 
		} 
	});
	
	var grid = new tui.Grid({
		el: document.getElementById('grid'),
		scrollX: false,
		scrollY: false,
		columns: [
			{
				header: 'boardNum',
				name: 'boardNum',
			},
			{
				header: 'spaceName',
				name: 'spaceName',
			},
			{
				header: 'Category',
				name: 'Category'
			},
			{
				header: 'price',
				name: 'price'
			},
			{
				header: 'regDate',
				name: 'regDate',
			}
		]
	});
	
	const grid = new tui.Grid({
	      el: document.getElementById('grid'),
	      data: gridData,
	      scrollX: false,
	      scrollY: false,
	      columns: [
	        {
	          header: 'Name',
	          name: 'name'
	        },
	        {
	          header: 'Artist',
	          name: 'artist'
	        },
	        {
	          header: 'Type',
	          name: 'type'
	        },
	        {
	          header: 'Release',
	          name: 'release'
	        },
	        {
	          header: 'Genre',
	          name: 'genre'
	        }
	      ]
	    });
};
</script>
<div id="grid"></div>

</body>
</html>
