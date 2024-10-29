package com.ty;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;

public class Test {

	public static void main(String[] args) {
		Connection connection = ConnectionPool.giveConnection();

		try {
			connection.setAutoCommit(false);

			Statement stm1 = connection.createStatement();
			String sql1 = "INSERT INTO product VALUES(103,'Pizza',250,'cheeze burst')";
			stm1.execute(sql1);

			Statement stm2 = connection.createStatement();
			String sql2 = "INSERT INTO product VALUES(104,'Burger',300,'extra slice cheeze')";
			stm2.execute(sql2);

			// savepoint
			Savepoint savepoint = connection.setSavepoint();

			Statement stm3 = connection.createStatement();
			String sql3 = "INSERT INTO payment VALUES(222,550,25,25)";
			stm3.execute(sql3);

			if (PaymentGateWay.pay()) {
				connection.commit();
				System.out.println("payment is successful and order is placed");
			} else {
				connection.rollback(savepoint);
				connection.commit();
				System.out.println("payment is failed and order is canceled");
			}

			ConnectionPool.submitConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
