package com.chinarewards.qqgbvpn.main.protocol.filter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chinarewards.qqgbvpn.domain.Pos;
import com.chinarewards.qqgbvpn.main.management.DatabaseMXBean;
import com.google.inject.Inject;

public class DatabaseManage implements DatabaseMXBean {

	EntityManager em;
	Configuration conf;

	Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	public DatabaseManage(EntityManager em, Configuration conf) {
		this.em = em;
		this.conf = conf;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean isAliveJpa() {
		boolean flag = false;
		try {
			List<Pos> list = em.createQuery("FROM PingTest").setFirstResult(0)
					.setMaxResults(1).getResultList();
			if (list.size() > 0)
				flag = true;
		} catch (Exception e) {
			log.error("Appear exception!", e);
		}

		return flag;
	}

	@Override
	public boolean isAliveJdbc() {
		boolean flag = false;
		Connection conn = getConnectionFromConfiguration(conf);
		if (conn == null)
			return flag;

		Statement stmt;
		try {
			stmt = conn.createStatement();
			String sql = "select count(*) from PingTest";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				long count = rs.getLong(1);
				if (count > 0) {
					log.debug("isAliveJdbc PingTest records count={}", count);
					flag = true;
				}
				break;
			}
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			log.error("query error", e);
		}

		return flag;
	}

	private Connection getConnectionFromConfiguration(Configuration conf) {
		Configuration dbConfig = conf.subset("db");
		String driverName = dbConfig.getString("driver");
		String url = dbConfig.getString("url");
		String userName = dbConfig.getString("user");
		String password = dbConfig.getString("password");

		try {
			Class.forName(driverName);
			Connection conn = DriverManager.getConnection(url, userName,
					password);
			return conn;
		} catch (ClassNotFoundException e) {
			log.error("Class not found!", e);
		} catch (SQLException e) {
			log.error("Connection error!", e);
		}

		return null;

	}

}
