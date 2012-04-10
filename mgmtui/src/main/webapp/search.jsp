<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.*, com.chinarewards.qqgbvpn.mgmtui.search.*" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>权益查询</title>
</head>
<body>
	<center>
		<s:form action="search" method="post" theme="simple">
			<font style="color:red;"><s:fielderror fieldName="inputError" /></font>
			请输入cdkey：<s:textfield name="cdkey" tabindex="1"/><s:submit value="确认"></s:submit>
		</s:form>
		<s:iterator value="history" status="historyStatus">
				<s:if test="#historyStatus.count < 1">
					<p>没有数据可显示</p>
				</s:if>
				<s:else>
					<table border="1">
						<tr>
							<th>时间</th>
							<th>优惠情况</th>
							<th>POS机编号</th>
						</tr>
						<tr>
							<td> <s:property value="lastModifiedAt" /> </td>
							<td>
								<s:if test="history.atype == 'gfit'">
									可以领取奖品
								</s:if>
								<s:else>
									消费<s:property value="consumeAmt" />元，反馈<s:property value="rebateAmt" />元
								</s:else>
							</td>
							<td><s:property value="posId" /></td>
						</tr>
					</table>
				</s:else>
		</s:iterator>
	</center>
</body>
</html>