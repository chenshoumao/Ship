<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%
    String path = request.getContextPath(); 
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
<link href="<%=basePath %>css/bootstrap.css" rel="stylesheet">
<link href="<%=basePath %>css/bootstrap.min.css" rel="stylesheet">
<script type="text/javascript"
	src="<%=basePath %>js/jquery-1.9.1.min.js"></script>
<script type="text/javascript" src="<%=basePath %>js/bootstrap.js"></script>
<script type="text/javascript" src="<%=basePath %>js/bootstrap.min.js"></script>
<script type="text/javascript">
	$(function(){
		$("#submit").click(function(){ 
			var temp = "";
			$("[name='checkbox']").prop("checked", function(i,val){   
				 if(val){
					 temp += $(this).val() + ",";
				 }
			})
			alert("已经向岸端程序发出更新请求，请等待。。。");
			$.ajax({
				url:"http://localhost:8081/Ship/ShipServlet",
				type:'post', 
				dataType:'json',
				data:{'data':temp},				
				success:function(map){ 
					var state = map['state']; 
					if(state){
					var consoleInfo = "";
					var list = map['list'];
					for(var i = 0; i < list.length;i++){
						$.each(list[i],function(key,values){ 
							if(values == true){
								consoleInfo += key + " 已获取到更新包，即将更新并重启。。。";
							}
							else{
								state = false;
								consoleInfo += key + " " + values;
							}
							
						})
					}
					alert(consoleInfo);
					
					if(!state){
						//由于后台验证代码的存在，一发现有错误情况是，根本不会有values == true 的存在，所以此时 consoleInfo 都为错误信息 
						$.ajax({
							url:"http://localhost:8080/Ship/UpdateShipLogs",
							type:'post', 
							data:{'info':consoleInfo}
						})
					}}
					else if(!state){
						alert(map['reason']);
					}
					
				}
			})
		})
	})
</script>
</head>
<body>
	<div class="container" style="margin-left: 36%;">
		<div class="row clearfix">
			<div class="col-md-4 column">
				<table class="table">
					<thead>
						<tr>
							<th>编号1</th>
							<th>组件</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td>
								<div class="checkbox">
									<label>1 <input type="checkbox" name="checkbox" value="app"/></label>
								</div>
							</td>
							<td><span>程序应用</span></td>

						</tr>

						<tr>
							<td>
								<div class="checkbox">
									<label>2 <input type="checkbox" name="checkbox" value="haitu"/></label>
								</div>
							</td>
							<td><span>海图</span></td>

						</tr>

						<tr>
							<td>
								<div class="checkbox">
									<label>3 
										<input type="checkbox" name="checkbox"  value="ditu"/>
									</label>
								</div>
							</td>
							<td><span>底图</span></td>

						</tr>

					</tbody>
				</table>
			</div>
		</div>
		<div class="col-md-4 column">
			<button class="btn-primary" id="submit">确定</button>
		</div>
	</div>
</body>
</html>