package dao;

import exceptions.*;
import entities.*;

import java.sql.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class CouponDBDAO implements CouponDAO {

	private ConnectionPool cp;

	public CouponDBDAO() {
		cp = ConnectionPool.getInstance();
	}

	// check if it is a new coupon, if yes - insert into DB, if no - throw an
	// exception
	@Override
	public long createCoupon(Coupon coupon) throws CouponSystemException, EntityAlreadyExistsException {

		Connection connection = cp.getConnection();

		try {
			
			// check if coupon with same title already and on same period already exists
			String query = "SELECT * FROM coupon WHERE title = ? AND status <> 3 AND ((? > start_date AND ? <= end_date) OR (? >= start_date AND ? < end_date) OR (? <= start_date AND ? >= end_date));";
			PreparedStatement st = connection.prepareStatement(query);
			st.setString(1, coupon.getTitle());
			st.setDate(2, coupon.getCouponEndtDate());
			st.setDate(3, coupon.getCouponEndtDate());
			st.setDate(4, coupon.getCouponStartDate());
			st.setDate(5, coupon.getCouponStartDate());
			st.setDate(6, coupon.getCouponStartDate());
			st.setDate(7, coupon.getCouponEndtDate());
			ResultSet result = st.executeQuery();
			if (result.next()) {
				throw new EntityAlreadyExistsException("Coupon with this title already exist on this period");
			}
			// insert coupon data into DB and set status 1
			query = "INSERT INTO coupon (title, description, start_date, end_date, amount, type, price, image, company_id, status) VALUES (?,?,?,?,?,?,?,?,?,0);";
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, coupon.getTitle());
			statement.setString(2, coupon.getDescription());
			statement.setDate(3, coupon.getCouponStartDate());
			statement.setDate(4, coupon.getCouponEndtDate());
			statement.setInt(5, coupon.getAmount());
			statement.setString(6, coupon.getCouponType().toString());
			statement.setDouble(7, coupon.getCouponPrice());
			statement.setString(8, coupon.getImage());
			statement.setLong(9, coupon.getCompanyId());
			statement.setInt(10, 0);
			statement.execute();
			connection.commit();
			statement.close();
			long couponId = getCouponId(coupon.getTitle(), coupon.getCouponStartDate(), connection);
			return couponId;
		} catch (SQLException e) {
			throw new CouponSystemException(e);
		} finally {
			cp.returnConnection(connection);
		}

	}

	// method for removing coupon
	@Override
	public void removeCoupon(long couponId) throws CouponSystemException {
		Connection connection = cp.getConnection();
		try {
			ResultSet rs = selectCouponById(couponId, connection);
			//if there are no any coupon with such ID in db
			if  ( !rs.next() ) {
				throw new EntityNotExistException("Coupon id :" + couponId + " not exist");
			} else if (rs.getInt("status") == 3){
				//if the coupon has status 3 - deleted 
				throw new EntityInactiveException("Coupon id :" + couponId + " has been deleted already");
			} else {
				//else: coupon is enabled and exist in DB
				String query = "UPDATE coupon SET status = 3 WHERE coupon_id = ? ;";
				PreparedStatement st = connection.prepareStatement(query);
				st.setLong(1, couponId);
				st.execute();
				st.close();
				//deactivate all coupon coupons that has been purchased
				query = "UPDATE customer_inventory SET status = 3 WHERE coupon_id = ?;";
				st = connection.prepareStatement(query);
				st.setLong(1, couponId);
				st.execute();
				st.close();
			}
			connection.commit();
		}
		catch (SQLException e){
			throw new CouponSystemException(e);
		} finally {
			cp.returnConnection(connection);
		}

	}
	
	@Override
	public void assignCoupon(long couponId, long customerId) throws CouponSystemException {
		
		Connection connection = cp.getConnection();
		try {
			ResultSet rs = selectCouponById(couponId, connection);
			//if there are no any coupon with such ID in db
			if  ( !rs.next() ) {
				throw new EntityNotExistException("Coupon id :" + couponId + " not exist");
			} else if (rs.getInt("status") == 3){
				//if the coupon has status 3 - deleted 
				throw new EntityInactiveException("Coupon id :" + couponId + " is unable to be purchased or it has been deleted!");
			} else if (rs.getInt("amount") == 0){
				//ifrequested coupons sold out
				throw new CouponSoldOutException("Coupon id :" + couponId + " is sold out.");
			} else {
				int coupon_amount = rs.getInt("amount");
				//check if coupon already purchased
				String query = "SELECT * FROM customer_inventory WHERE coupon_id = ? AND customer_id = ?;";
				PreparedStatement st = connection.prepareStatement(query);
				st.setLong(1, couponId);
				st.setLong(2, customerId);
				rs = st.executeQuery();
				if (rs.next()) {
					throw new DuplicatedPurchaseException("Coupon id :" + couponId + " already purchased by customer " + customerId);
				} else {
					//add coupon to customer
					try {
						query = "INSERT INTO customer_inventory (customer_id, coupon_id, status) VALUES (?,?,0);";
						st = connection.prepareStatement(query);
						st.setLong(1, customerId);
						st.setLong(2, couponId);					
						st.execute();
					} catch (SQLException sqlexception) {
						throw new CustomerInventoryException("Exception occured during adding coupon " + couponId + " to customer " + customerId);
					}								
					//decrease number of coupons
					try {
						query = "UPDATE coupon SET amount = ?;";
						st = connection.prepareStatement(query);
						st.setLong(1, (coupon_amount - 1));				
						st.execute();
					} catch (SQLException sqlexception) {
						throw new CustomerInventoryException("Exception occured during updating coupon " + couponId + " amount to " + (coupon_amount - 1));
					}
					
				}
				st.close();
			}
			connection.commit();
		}
		catch (SQLException e){
			throw new CouponSystemException(e);
		} finally {
			cp.returnConnection(connection);
		}

	}
	
	
	
	
	
	// method for updating coupon
	@Override
	public void updateCoupon(Coupon coupon) throws CouponSystemException {
		Connection connection = cp.getConnection();
		long couponId = coupon.getCouponID();
		try {
			//coupon not exist
			ResultSet rs = selectCouponById(couponId, connection);
			//if there are no any coupon with such ID in DB
			if  ( !rs.next() ) {
				throw new EntityNotExistException("Coupon id :" + couponId + " not exist in DB");
			} else if (rs.getInt("status") == 3){
				throw new EntityInactiveException("Coupon id :" + couponId + " has been deleted already");
			} else {
				//((? > start_date AND ? <= end_date) OR (? >= start_date AND ? < end_date) OR (? <= start_date AND ? >= end_date)
				String query = "SELECT * FROM coupon WHERE title = ? AND status <> 3 AND ((? > start_date AND ? <= end_date) OR (? >= start_date AND ? < end_date) OR (? <= start_date AND ? >= end_date));";
				PreparedStatement st = connection.prepareStatement(query);
				st.setString(1, coupon.getTitle());
				st.setDate(2, coupon.getCouponEndtDate());
				st.setDate(3, coupon.getCouponEndtDate());
				st.setDate(4, coupon.getCouponStartDate());
				st.setDate(5, coupon.getCouponStartDate());
				st.setDate(6, coupon.getCouponStartDate());
				st.setDate(7, coupon.getCouponEndtDate());
				ResultSet result = st.executeQuery();
				if (result.next()) {
					throw new EntityAlreadyExistsException("Coupon with this title already exist on this period");
				} else {
					//update coupon
					String query1 = "UPDATE coupon SET title = ?, description = ?, start_date = ?, end_date = ?, amount = ?, type = ?, price = ?, image = ?, company_id = ?;";
					PreparedStatement statement = connection.prepareStatement(query1);
					statement.setString(1, coupon.getTitle());
					statement.setString(2, coupon.getDescription());
					statement.setDate(3, coupon.getCouponStartDate());
					statement.setDate(4, coupon.getCouponEndtDate());
					statement.setInt(5, coupon.getAmount());
					statement.setString(6, coupon.getCouponType().toString());
					statement.setDouble(7, coupon.getCouponPrice());
					statement.setString(8, coupon.getImage());
					statement.setLong(9, coupon.getCompanyId());
					statement.executeUpdate();
					connection.commit();
					statement.close();
				}
			}
		} catch (SQLException e) {
			throw new CouponSystemException(e);
		} finally {
			cp.returnConnection(connection);
		}

	}

	// method to get a specific coupon from DB
	@Override
	public Coupon getCoupon(long id) throws CouponSystemException {
		// constructing a Coupon to return
		Connection connection = cp.getConnection();
		
		Coupon coupon = null;
		try {
			//Check is coupon already exist
			ResultSet rs = selectCouponById(id, connection);
			if  ( !rs.next() ) {
				throw new EntityNotExistException("Coupon id : " + id + " not exist in DB");
			}
			else if (rs.getInt("status") == 3){
				throw new EntityInactiveException("Coupon id : " + id + " has been deleted");
			} else {
			
				coupon = new Coupon();
				coupon.setCouponID(rs.getLong("couponID"));
				coupon.setTitle(rs.getString("title"));
				coupon.setCouponStartDate(rs.getDate("start_date"));
				coupon.setCouponEndtDate(rs.getDate("end_date"));
				coupon.setAmount(rs.getInt("amount"));
				coupon.setCouponType(CouponType.valueOf(rs.getString("type")));
				coupon.setDescription(rs.getString("description"));
				coupon.setCouponPrice(rs.getDouble("price"));
				coupon.setImage(rs.getString("image"));
				coupon.setCompanyId(rs.getLong("company_id"));
			}
			connection.commit();
		} catch (SQLException e) {
			throw new CouponSystemException(e.getMessage() + "\n" + e.getCause());
		}finally {
			cp.returnConnection(connection);
		}
		return coupon;
	}

	
	// method to get all the coupons from the DB
	@Override
	public Collection<Coupon> getAllCoupons() throws CouponSystemException {
		Collection<Coupon> allCoupons = new ArrayList<Coupon>();
		Connection connection = cp.getConnection();
		try {
			String query = "SELECT * FROM coupon WHERE status <> 3;";
			PreparedStatement statement = connection.prepareStatement(query);
			ResultSet result = statement.executeQuery();
			Coupon coupon = null;
			while (result.next()) {
				coupon = new Coupon();
				coupon.setCouponID(result.getLong("couponID"));
				coupon.setTitle(result.getString("title"));
				coupon.setCouponStartDate(result.getDate("start_date"));
				coupon.setCouponEndtDate(result.getDate("end_date"));
				coupon.setAmount(result.getInt("amount"));
				coupon.setCouponType(CouponType.valueOf(result.getString("type")));
				coupon.setDescription(result.getString("description"));
				coupon.setCouponPrice(result.getDouble("price"));
				coupon.setImage(result.getString("image"));
				coupon.setCompanyId(result.getLong("company_id"));
				// add to list
				allCoupons.add(coupon);
			}
			statement.close();

		} catch (SQLException e) {
			throw new CouponSystemException(e);
		} finally {
			cp.returnConnection(connection);
		}
		return allCoupons;
	}
	
	//get all Coupons of company
	@Override
	public Collection<Coupon> getCouponsByCompany(long companyId) throws CouponSystemException {
		Collection<Coupon> allCoupons = new ArrayList<Coupon>();
		Connection connection = cp.getConnection();
		try {
			String query = "SELECT * FROM coupon WHERE status <> 3 AND company_id=?;";
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, Long.toString(companyId));
			ResultSet result = statement.executeQuery();
			Coupon coupon = null;
			while (result.next()) {
				coupon = new Coupon();
				coupon.setCouponID(result.getLong("couponID"));
				coupon.setTitle(result.getString("title"));
				coupon.setCouponStartDate(result.getDate("start_date"));
				coupon.setCouponEndtDate(result.getDate("end_date"));
				coupon.setAmount(result.getInt("amount"));
				coupon.setCouponType(CouponType.valueOf(result.getString("type")));
				coupon.setDescription(result.getString("description"));
				coupon.setCouponPrice(result.getDouble("price"));
				coupon.setImage(result.getString("image"));
				coupon.setCompanyId(result.getLong("company_id"));
				// add to list
				allCoupons.add(coupon);
			}
			statement.close();

		} catch (SQLException e) {
			throw new CouponSystemException(e);
		} finally {
			cp.returnConnection(connection);
		}
		return allCoupons;
	}
	
	
	//get all Coupons of company by type
	@Override
	public Collection<Coupon> getCouponsByCompanyAndType(long companyId, CouponType couponType) throws CouponSystemException {
		Collection<Coupon> allCoupons = new ArrayList<Coupon>();
		Connection connection = cp.getConnection();
		try {
			String query = "SELECT * FROM coupon WHERE status <> 3 AND company_id=? AND type=?;";
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, Long.toString(companyId));
			statement.setString(2, couponType.toString());
			ResultSet result = statement.executeQuery();
			Coupon coupon = null;
			while (result.next()) {
				coupon = new Coupon();
				coupon.setCouponID(result.getLong("couponID"));
				coupon.setTitle(result.getString("title"));
				coupon.setCouponStartDate(result.getDate("start_date"));
				coupon.setCouponEndtDate(result.getDate("end_date"));
				coupon.setAmount(result.getInt("amount"));
				coupon.setCouponType(CouponType.valueOf(result.getString("type")));
				coupon.setDescription(result.getString("description"));
				coupon.setCouponPrice(result.getDouble("price"));
				coupon.setImage(result.getString("image"));
				coupon.setCompanyId(result.getLong("company_id"));
				// add to list
				allCoupons.add(coupon);
			}
			statement.close();

		} catch (SQLException e) {
			throw new CouponSystemException(e);
		} finally {
			cp.returnConnection(connection);
		}
		return allCoupons;
	}
	
	
	//get all Coupons of company up to specified price
		@Override
		public Collection<Coupon> getCouponsBeforeDate(long companyId, Date date) throws CouponSystemException {
			Collection<Coupon> allCoupons = new ArrayList<Coupon>();
			Connection connection = cp.getConnection();
			try {
				String query = "SELECT * FROM coupon WHERE status <> 3 AND company_id=? AND end_date <= ?;";
				PreparedStatement statement = connection.prepareStatement(query);
				statement.setString(1, Long.toString(companyId));
				statement.setString(2, date.toString());
				ResultSet result = statement.executeQuery();
				Coupon coupon = null;
				while (result.next()) {
					coupon = new Coupon();
					coupon.setCouponID(result.getLong("couponID"));
					coupon.setTitle(result.getString("title"));
					coupon.setCouponStartDate(result.getDate("start_date"));
					coupon.setCouponEndtDate(result.getDate("end_date"));
					coupon.setAmount(result.getInt("amount"));
					coupon.setCouponType(CouponType.valueOf(result.getString("type")));
					coupon.setDescription(result.getString("description"));
					coupon.setCouponPrice(result.getDouble("price"));
					coupon.setImage(result.getString("image"));
					coupon.setCompanyId(result.getLong("company_id"));
					// add to list
					allCoupons.add(coupon);
				}
				statement.close();

			} catch (SQLException e) {
				throw new CouponSystemException(e);
			} finally {
				cp.returnConnection(connection);
			}
			return allCoupons;
		}
		

	// method to get all coupons of the same type from DB
	// return a collection of coupons of the requested type
	@Override
	public Collection<Coupon> getCouponsByType(CouponType couponType) throws CouponSystemException {
		// constructing a List<Coupon> to return
		Collection<Coupon> allCouponsByType = new ArrayList<Coupon>();
		Connection connection = cp.getConnection();
		try {
			String query = "SELECT * FROM coupon WHERE type=?";
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, couponType.toString());
			ResultSet result = statement.executeQuery();
			Coupon coupon = null;
			while (result.next()) {
				coupon = new Coupon();
				coupon.setCouponID(result.getLong("couponID"));
				coupon.setTitle(result.getString("title"));
				coupon.setCouponStartDate(result.getDate("start_date"));
				coupon.setCouponEndtDate(result.getDate("end_date"));
				coupon.setAmount(result.getInt("amount"));
				coupon.setCouponType(CouponType.valueOf(result.getString("type")));
				coupon.setDescription(result.getString("description"));
				coupon.setCouponPrice(result.getDouble("price"));
				coupon.setImage(result.getString("image"));
				coupon.setCompanyId(result.getLong("company_id"));
				// add to list
				allCouponsByType.add(coupon);
			}
			statement.close();

		} catch (SQLException e) {
			throw new CouponSystemException(e);
		} finally {
			cp.returnConnection(connection);
		}
		return allCouponsByType;
	}

	// method that gets id's of coupons which have expired
	// and returns a list of those coupons
	@Override
	public Collection<Coupon> getOldCoupons() throws CouponSystemException {
		Collection<Coupon> oldCoupons = new ArrayList<Coupon>();
		Connection connection = cp.getConnection();
		try {
			String query = "SELECT coupon_id FROM coupon WHERE DATE(NOW()) > end_date;";
			PreparedStatement statement = connection.prepareStatement(query);
			ResultSet result = statement.executeQuery();
			Coupon coupon = null;
			while (result.next()) {
				coupon = new Coupon();
				coupon.setCouponID(result.getLong("couponID"));
				oldCoupons.add(coupon);
			}
			statement.close();

		} catch (SQLException e) {
			throw new CouponSystemException(e);
		}
		finally {
			cp.returnConnection(connection);
		}
		return oldCoupons;
	}
	
	private long getCouponId(String couponName, Date startDate, Connection connection) throws EntityNotExistException, CouponSystemException{
		try {
			String query = "SELECT * FROM coupon WHERE coupon_name = ? AND start_date = ? AND status <> 3;";
			PreparedStatement st = connection.prepareStatement(query);
			st.setString(1, couponName);
			st.setDate(2, startDate);
			ResultSet rs = st.executeQuery();
			st.close();
			if (!rs.next()) {
				throw new EntityNotExistException("Coupon name: " + couponName + " with start date " + startDate + " not exist in DB");
			} else {
				return rs.getLong("coupon_id");
			}
		} catch (SQLException e) {
			throw new CouponSystemException(e);
		} 
	}
	
	
	private ResultSet selectCouponById(long couponId, Connection connection) throws SQLException{
		String query = "SELECT * FROM coupon WHERE coupon_id = ? ;";
		PreparedStatement st = connection.prepareStatement(query);
		st.setLong(1, couponId);
		ResultSet rs = st.executeQuery();
		st.close();
		return rs;
	}

	@Override
	public Collection<Coupon> getCouponsUpToPrice(long companyId, int price) throws CouponSystemException {
		Collection<Coupon> allCoupons = new ArrayList<Coupon>();
		Connection connection = cp.getConnection();
		try {
			String query = "SELECT * FROM coupon WHERE status <> 3 AND company_id=? AND price <= ?;";
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, Long.toString(companyId));
			statement.setString(2, Integer.toString(price));
			ResultSet result = statement.executeQuery();
			Coupon coupon = null;
			while (result.next()) {
				coupon = new Coupon();
				coupon.setCouponID(result.getLong("couponID"));
				coupon.setTitle(result.getString("title"));
				coupon.setCouponStartDate(result.getDate("start_date"));
				coupon.setCouponEndtDate(result.getDate("end_date"));
				coupon.setAmount(result.getInt("amount"));
				coupon.setCouponType(CouponType.valueOf(result.getString("type")));
				coupon.setDescription(result.getString("description"));
				coupon.setCouponPrice(result.getDouble("price"));
				coupon.setImage(result.getString("image"));
				coupon.setCompanyId(result.getLong("company_id"));
				// add to list
				allCoupons.add(coupon);
			}
			statement.close();

		} catch (SQLException e) {
			throw new CouponSystemException(e);
		} finally {
			cp.returnConnection(connection);
		}
		return allCoupons;
	}

}