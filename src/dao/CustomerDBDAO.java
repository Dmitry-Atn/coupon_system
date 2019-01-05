package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

import entities.Company;
import entities.Coupon;
import entities.CouponType;
import entities.Customer;
import exceptions.CouponSystemException;
import exceptions.EntityAlreadyExistsException;
import exceptions.EntityInactiveException;
import exceptions.EntityNotExistException;
import exceptions.NonUniqueDataException;

public class CustomerDBDAO implements CustomerDAO {
	
	private ConnectionPool cp;
	
	public CustomerDBDAO() {
		cp = ConnectionPool.getInstance();
	}
	
	@Override
	public void createCustomer(Customer customer) throws NonUniqueDataException, CouponSystemException{
		Connection connection = cp.getConnection();
		try {
			//Check is customer email already in use
			String query = "SELECT * FROM customer WHERE email = ?;";
			PreparedStatement st = connection.prepareStatement(query);
			st.setString(1, customer.getEmail());
			ResultSet rs = st.executeQuery();
			if (rs.next()) {
				throw new NonUniqueDataException("That email already registered: " + customer.getEmail());
			}
			//Insert customer to DB and set status 0
			query = "INSERT INTO customer (name, email, password, status) VALUES (?, ?, ?, 0);";
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, customer.getName());
			statement.setString(2, customer.getEmail());
			statement.setString(3, customer.getPassword());
			statement.execute();	
			connection.commit();
			statement.close();
		}
		catch (SQLException e){
			throw new CouponSystemException(e);
		}finally {
			cp.returnConnection(connection);
		}	
	}

	@Override
	public void removeCustomer(long customerId) throws EntityNotExistException, EntityInactiveException, CouponSystemException{
		Connection connection = cp.getConnection();
		try {
			ResultSet rs = selectCustomerById(customerId, connection);
			//if there are no any customer with such ID in db
			if  ( !rs.next() ) {
				throw new EntityNotExistException("Customer id: " + customerId + " not exist in DB");
			} else if (rs.getInt("status") == 3){
				//if the customer has status 3 - deleted
				throw new EntityInactiveException("Customer id: " + customerId + " has been deleted aready");
			} else {
				//else: customer is enabled and exist in DB
				String query = "UPDATE customer SET status = 3 WHERE customer_id = ?;";
				PreparedStatement st = connection.prepareStatement(query);
				st.setLong(1, customerId);
				st.executeUpdate();
				connection.commit();
				st.close();
			}
		}
		catch (SQLException e){
			throw new CouponSystemException(e);
		} finally {
			cp.returnConnection(connection);
		}
	}

	@Override
	public void updateCustomer(Customer customer) throws CouponSystemException, EntityAlreadyExistsException, EntityInactiveException, EntityNotExistException{
		Connection connection = cp.getConnection();
		long customerId = customer.getCustomerID();
		try {
			ResultSet rs = selectCustomerById(customerId, connection);
			//if there are no any customer with such ID in DB
			if  ( !rs.next() ) {
				throw new EntityNotExistException("Customer id: " + customerId + " not exist in DB");
			} else if (rs.getInt("status") == 3){
				//if the customer has status 3 - deleted
				throw new EntityInactiveException("Customer id: " + customerId + " has been deleted already");
			} else {
				String query = "SELECT * FROM customer WHERE email = ? AND customer_id <> ? AND status <> 3;";
				PreparedStatement st = connection.prepareStatement(query);
				st.setString(1, customer.getEmail());
				st.setLong(2, customer.getCustomerID());
				rs = st.executeQuery();
				if (rs.next()) {
					throw new NonUniqueDataException("That email already registered: " + customer.getEmail());
				}
				//else: customer is enabled and exist in DB
				query = "UPDATE customer SET name = ?, password = ?, email = ? WHERE customer_id = ?;";
				st = connection.prepareStatement(query);
				st.setString(1, customer.getName());
				st.setString(2, customer.getPassword());
				st.setString(3, customer.getEmail());
				st.setLong(4, customerId);
				st.executeUpdate();
				
				connection.commit();
				st.close();
			}
		}
		catch (SQLException e){
			throw new CouponSystemException(e);
		} finally {
			cp.returnConnection(connection);
		}
	}

	@Override
	public Customer getCustomer(long customerId) throws EntityNotExistException, CouponSystemException{
		Connection connection = cp.getConnection();
		Customer customer = null;
		try {
			//Check is customery exist
			ResultSet rs = selectCustomerById(customerId, connection);
			if  ( !rs.next() ) {
				throw new EntityNotExistException("Customer id :" +customerId + " not exist in DB");
			}
			else {
				customer = new Customer(rs.getLong("customer_id"), rs.getString("name"),  rs.getString("password"), rs.getString("email"));	
			}
			connection.commit();
		} catch (SQLException e) {
			throw new CouponSystemException(e.getMessage() + "\n" + e.getCause());
		}finally {
			cp.returnConnection(connection);
		}
		return customer;
	}
	
	@Override
	public Collection<Customer> getAllCustomers() throws CouponSystemException{
		Connection connection = cp.getConnection();
		Collection<Customer> res = new ArrayList<Customer>();
		try {
			String query = "SELECT * FROM customer WHERE status <> 3;";
			Statement st = connection.createStatement();
			st.execute(query);
			ResultSet rs = st.getResultSet();
			if (!rs.next()) {
				System.out.println("There are no customers registered");
			} else {
				do {
					res.add(new Customer(rs.getLong("customer_id"), rs.getString("name"),  rs.getString("password"), rs.getString("email")));					
				} while (rs.next());
				return res;
			}
		} catch (SQLException e) {
			throw new CouponSystemException(e.getMessage() + "/n" + e.getCause());
		}finally {
			cp.returnConnection(connection);	
		}
		return res;
	}

	@Override
	public Collection<Coupon> getCoupons(long customerId) throws EntityNotExistException, CouponSystemException{
		Connection connection = cp.getConnection();
		Collection<Coupon> coupons = new ArrayList<Coupon>();
		try {
			ResultSet rs = selectCustomerById(customerId, connection);
			//if there are no any customer with such ID in db
			if  ( !rs.next() ) {
				throw new EntityNotExistException("Customer id :" + customerId + " not exist in DB");
			} else {
				String query = "SELECT * FROM coupon WHERE company_id = ? AND status = 1;";
				PreparedStatement st = connection.prepareStatement(query);
				st.setLong(1, customerId);
				st.execute(query);
				rs = st.getResultSet();
				if (!rs.next()) {
					System.out.println("There are no coupons");
				} else {
					do {
						coupons.add(new Coupon(rs.getLong("coupon_id"),
								rs.getString("title"),
								rs.getDate("start_date"),
								rs.getDate("end_date"),
								rs.getInt("amount"),
								CouponType.values()[rs.getInt("type")],
								rs.getString("message"),
								rs.getInt("price"),
								rs.getString("image"),
								rs.getLong("company_id")));					
					} while (rs.next());
					return coupons;
				}
			}
		}catch (SQLException e) {
			throw new CouponSystemException(e.getMessage());
		}finally {
			cp.returnConnection(connection);		
		}
		return coupons;
	}
	
	//TODO: Implement add coupon to customer inventory

	@Override
	public boolean login(String email, String password) throws CouponSystemException{
		Connection connection = cp.getConnection();
		try {
			String query = "SELECT * FROM customer WHERE email = ? AND password = ?;";
			PreparedStatement st = connection.prepareStatement(query);
			st.setString(1, email);
			st.setString(2, password);
			ResultSet rs = st.executeQuery();
			if  ( !rs.next() ) {
				return false;
			} else {
				return true;
			}
		} catch (SQLException e) {
			throw new CouponSystemException(e.getMessage() + "\n" + e.getCause());
		}finally {
			cp.returnConnection(connection);
		}
	}
		
	private ResultSet selectCustomerById(long customerId, Connection connection) throws SQLException{
		String query = "SELECT * FROM customer WHERE customer_id = ? ;";
		PreparedStatement st = connection.prepareStatement(query);
		st.setLong(1, customerId);
		ResultSet rs = st.executeQuery();
		st.close();
		return rs;
	} 
	
	public long getCustomerIdByName(String customerName) throws EntityNotExistException, CouponSystemException{
		Connection connection = cp.getConnection();
		try {
			String query = "SELECT * FROM customer WHERE name = ? AND status <> 3;";
			PreparedStatement st = connection.prepareStatement(query);
			st.setString(1, customerName);
			ResultSet rs = st.executeQuery();
			connection.commit();
			st.close();
			if (!rs.next()) {
				throw new EntityNotExistException("Customer name: " + customerName + " not exist in DB or deleted");
			} else {
				st.close();
				return rs.getLong("customer_id");
			}
			
		} catch (SQLException e) {
			throw new CouponSystemException(e);
		} finally {
			cp.returnConnection(connection);
		}
	}
	
	
	public void clearDB() throws CouponSystemException{
		Connection connection = cp.getConnection();
		try {
			String query = "DELETE FROM customer WHERE 1=1;";
			Statement st = connection.createStatement();
			st.execute(query);
			st.close();
			connection.commit();
		} catch (SQLException e) { 
			throw new CouponSystemException("Somthing went wrong during clean-up of 'customer' table" + e);
		} finally {
			if (connection != null) {
				cp.returnConnection(connection);
			}
			
		}
		
	}

	@Override
	public Collection<Coupon> getPurchaseHistory(long customerId) {
		// TODO: implement getPurchaseHistory
		return null;
	}

}
