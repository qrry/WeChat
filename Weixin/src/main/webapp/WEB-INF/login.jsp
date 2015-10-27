<%@ page pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>java wireless</title>
 <link rel="shortcut icon" href="http://wiki.tuan800-inc.com/s/zh_CN/5635/60fd2eb45debbf4ede2b669f4c9b96b4ce40a937.14/_/favicon.ico" />
<link rel="stylesheet" href='<c:url value="/css/bootstrap.css"></c:url>' />
<link rel="stylesheet" href='<c:url value="/css/app.css"></c:url>' />
<script src="<c:url value='/js/jquery.js'/>" type="text/javascript"></script>
<script type="text/javascript" src="<c:url value="/js/bootstrap.js"></c:url>"></script>
</head>
<body>
	<div class="container w-xxl w-auto-xs">
		<br /> <span class="navbar-brand block m-t">JAVA WIRELESS</span>
		<div class="m-b-lg">
			<div class="wrapper text-center">
				<strong>用户登录</strong>
			</div>
			<form name="form"  class="form-validation" method="POST" action='<c:url value="/api/authen/login"></c:url>' >
				<div class="text-danger wrapper text-center" >
					</div>
				<div class="list-group list-group-sm">
					<div class="list-group-item">
						<input type="text" placeholder="登录名"
							class="form-control no-border" name="username" required>
					</div>
					<div class="list-group-item">
						<input type="password" placeholder="密码"
							class="form-control no-border" name="password" required>
					</div>
				</div>
				<button type="submit" class="btn btn-lg btn-danger btn-block"
					 >登录</button>
				<div class="text-center m-t m-b">
					<a href="http://oa.tuan800-inc.com/forgetPWD.html" target="_blank"
						class="forgetPswTip">忘记密码？</a>
				</div>
				<div class="line line-dashed"></div>
			</form>
		</div>
	</div>
</body>
</html>