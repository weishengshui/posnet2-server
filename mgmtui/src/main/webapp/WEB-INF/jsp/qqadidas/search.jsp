<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page
	import="java.util.*,com.chinarewards.qqgbvpn.mgmtui.struts.qq.adidas.*"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>权益查询</title>
</head>
<body>
	<center>
		<h2>QQ权益查询</h2>
		<font style="color: red;"><s:fielderror fieldName="inputError" /></font>
		<s:form action="pquery" method="get" theme="simple" namespace="/qq-adidas/backend">
			请输入验证码：<s:textfield name="memberKey" tabindex="1" />
			<s:submit value="确认"></s:submit>
		</s:form>
		<table border="1" align="center">

			<s:iterator value="historys" status="historyStatus" id="history">
				<s:if test="#historyStatus.first">
					<tr>
						<th>验证码</th>
						<th>时间</th>
						<th>权益</th>
						<th>POS机编号</th>
					</tr>
					<tr align="center">
						<td><s:property value="memberKey" /></td>
						<td><s:property value="lastModifiedAt.toString().substring(0,19)" /></td>
						<td><s:if test='#history.aType.toString().equals("GIFT")'>
								领取免费礼品
								</s:if> <s:else>
									消费<s:property value="consumeAmt" />元，获得折扣<s:property
									value="rebateAmt" />元权益
								</s:else></td>
						<td><s:property value="posId" /></td>
					</tr>
				</s:if>
				<s:else>
					<tr align="center">
						<td><s:property value="memberKey" /></td>
						<td><s:property value="lastModifiedAt.toString().substring(0,19)" /></td>
						<td><s:if test='#history.aType.toString().equals("GIFT")'>
								领取免费礼品
								</s:if> <s:else>
									消费<s:property value="consumeAmt" />元，获得折扣<s:property
									value="rebateAmt" />元权益
								</s:else></td>
						<td><s:property value="posId" /></td>
					</tr>
				</s:else>
			</s:iterator>
		</table>
	</center>
</body>
</html>
