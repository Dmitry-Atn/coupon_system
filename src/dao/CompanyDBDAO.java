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
import exceptions.CouponSystemException;
import exceptions.EntityAlreadyExistsException;
import exceptions.EntityInactiveException;
import exceptions.EntityNotExistException;
import exceptions.NonUniqueDataException;

public class CompanyDBDAO implements CompanyDAO {
	
	private ConnectionPool cp;
	
	public CompanyDBDAO() {
		cp = ConnectionPool.getInstance();
	}
	
	@Override
	public long createCompany(Company company) throws CouponSystemException, EntityAlreadyExistsException, NonUniqueDataException, CouponSystemException {
		Connection connection = cp.getConnection();
		long companyId;
		try {
			//Check is company already exist
			String query = "SELECT * FROM company WHERE company_name = ? AND status <> 3;";
			PreparedStatement st = connection.prepareStatement(query);
			st.setString(1, company.getCompanyName());
			ResultSet rs = st.executeQuery();
			if  ( rs.next() ) {
				throw new EntityAlreadyExistsException("Company " + company.getCompanyName() + " already exist");
			}
			//Check is company email already in use
			query = "SELECT * FROM company WHERE email = ? AND status <> 3;";
			st = connection.prepareStatement(query);
			st.setString(1, company.getEmail());
			rs = st.executeQuery();
			if (rs.next()) {
				throw new NonUniqueDataException("That email already registered: " + company.getEmail());
			}
			//Insert company to DB and set status 1
			query = "INSERT INTO company (company_name, password, email, status) VALUES (?, ?, ?, 0);";
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, company.getCompanyName());
			statement.setString(2, company.getPassword());
			statement.setString(3, company.getEmail());
			statement.execute();	
			connection.commit();
			statement.close();
			
			//get company id and return it
			companyId = getCompanyIdByName(company.getCompanyName(), connection);
		}
		catch (SQLException e){
			throw new CouponSystemException(e);
		}finally {
			cp.returnConnection(connection);
		}	
		return companyId;
		
	}
	
	@Override
	public void removeCompany(long companyId) throws CouponSystemException, EntityNotExistException, EntityInactiveException {
		
		Connection connection = cp.getConnection();
		try {
			ResultSet rs = selectCompanyById(companyId, connection);
			//if there are no any company with such ID in db
			if  ( !rs.next() ) {
				throw new EntityNotExistException("Company id :" + companyId + " not exist");
			} else if (rs.getInt("status") == 3){
				//if the company has status 3 - deleted 
				throw new EntityInactiveException("Company id :" + companyId + " has been deleted already");
			} else {
				//else: company is enabled and exist in DB
				String query = "UPDATE company SET status = 3 WHERE company_id = ? ;";
				PreparedStatement st = connection.prepareStatement(query);
				st.setLong(1, companyId);
				st.execute();
				st.close();
				//TODO: freeze companny coupons
				//deactivate all company's coupons
				query = "UPDATE coupon SET status = 3 WHERE company_id = ? ;";
				st = connection.prepareStatement(query);
				st.setLong(1, companyId);
				st.execute();
				st.close();
				//deactivate all company coupons that has been purchased
				query = "UPDATE customer_inventory SET status = 3 WHERE company_id = ?;";
				st = connection.prepareStatement(query);
				st.setLong(1, companyId);
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
	public void updateCompany(Company c) throws CouponSystemException, EntityNotExistException, NonUniqueDataException, EntityInactiveException{
		Connection connection = cp.getConnection();
		long companyId = c.getCompanyID();
		try {
			ResultSet rs = selectCompanyById(companyId, connection);
			//if there are no any company with such ID in DB
			if  ( !rs.next() ) {
				throw new EntityNotExistException("Company id :" + companyId + " not exist in DB");
			} else if (rs.getInt("status") == 3){
				//if the company has status 3 - disabled 
				throw new EntityInactiveException("Company id :" + companyId + " has been deleted already");
			} else {
				String query = "SELECT * FROM company WHERE email = ? AND company_id <> ? AND status <> 3;";
				PreparedStatement st = connection.prepareStatement(query);
				st.setString(1, c.getEmail());
				st.setLong(2, c.getCompanyID());
				rs = st.executeQuery();
				if (rs.next()) {
					throw new NonUniqueDataException("That email already registered: " + c.getEmail());
				}
				query = "SELECT * FROM company WHERE company_name = ? AND company_id <> ? AND status <> 3;";
				st = connection.prepareStatement(query);
				st.setString(1, c.getCompanyName());
				st.setLong(2, c.getCompanyID());
				rs = st.executeQuery();
				if (rs.next()) {
					throw new EntityAlreadyExistsException("That company already registered: " + c.getCompanyName());
				}
				//else: company is enabled and exist in DB
				query = "UPDATE company SET company_name = ?, password = ?, email = ? WHERE company_id = ?;";
				st = connection.prepareStatement(query);
				st.setString(1, c.getCompanyName());
				st.setString(2, c.getPassword());
				st.setString(3, c.getEmail());
				st.setLong(4, companyId);
				st.execute();
				
				connection.commit();
				st.close();
			}
		}
		catch (SQLException e){
			throw new CouponSystemException(e);
		}
		cp.returnConnection(connection);
	}

	@Override
	public Company getCompany(long id) throws CouponSystemException, EntityNotExistException {
		Connection connection = cp.getConnection();
		Company company = null;
		try {
			//Check is company already exist
			ResultSet rs = selectCompanyById(id, connection);
			if  ( !rs.next() ) {
				throw new EntityNotExistException("Company id : " + id + " not exist in DB");
			}
			else if (rs.getInt("status") == 3){
				throw new EntityInactiveException("Company id : " + id + " has been deleted");
			} else {
			
				company = new Company(rs.getLong("comapny_id"),
						rs.getString("company_name"),
						rs.getString("password"),
						rs.getString("email"),
						getCouponsByCompanyId(rs.getLong("comapny_id"), connection));			
			}
			connection.commit();
		} catch (SQLException e) {
			throw new CouponSystemException(e.getMessage() + "\n" + e.getCause());
		}finally {
			cp.returnConnection(connection);
		}
		return company;
	}

	@Override
	public Collection<Company> getAllCompanies() throws CouponSystemException{
		Connection connection = cp.getConnection();
		Collection<Company> res = new ArrayList<Company>();
		PreparedStatement st = null;
		try {
			String query = "SELECT * FROM company WHERE status <> 3;";
			st = connection.prepareStatement(query);
			ResultSet rs = st.executeQuery();
			//TODO: Optimization: Pull companies, pull coupons and then merge them to companies collection
			if (!rs.next()) {
				System.out.println("There are no companies registered");
			} else {
				do {
					res.add(
							new Company(
							rs.getLong("company_id"),
							rs.getString("company_name"),
							rs.getString("password"),
							rs.getString("email"),
							getCouponsByCompanyId(rs.getLong("company_id"),connection)
								)
							);					
				} while (rs.next());
				connection.commit();
				st.close();
				return res;
			}
		} catch (SQLException e) {
			throw new CouponSystemException(e);
		}finally {
			try {
				connection.commit();
				if (st!=null) {st.close();};
			} catch (SQLException e1){
				throw new CouponSystemException(e1);
			}
			cp.returnConnection(connection);	
		}
		return res;
	}

	@Override
	public Collection<Coupon> getCoupons(long companyId) throws EntityNotExistException, CouponSystemException{
		Connection connection = cp.getConnection();
		Collection<Coupon> coupons = null;
		try {
			coupons = getCouponsByCompanyId(companyId, connection);
		}catch (SQLException e) {
			throw new CouponSystemException(e.getMessage());
		}finally {
			cp.returnConnection(connection);		
		}
		return coupons;
	}

	@Override
	public boolean login(String companyEmail, String password) throws CouponSystemException {
		Connection connection = cp.getConnection();
		try {
			//Check is company already exist
			String query = "SELECT * FROM company WHERE email = ? AND password = ? AND status <> 3;";
			PreparedStatement st = connection.prepareStatement(query);
			st.setString(1, companyEmail);
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
	
	private ResultSet selectCompanyById(long companyId, Connection connection) throws SQLException{
		String query = "SELECT * FROM company WHERE company_id = ? ;";
		PreparedStatement st = connection.prepareStatement(query);
		st.setLong(1, companyId);
		ResultSet rs = st.executeQuery();
		st.close();
		return rs;
	}
	
	public long getCompanyIdByName(String companyName, Connection connection) throws EntityNotExistException, CouponSystemException{
		try {
			String query = "SELECT * FROM company WHERE company_name = ? AND status <> 3;";
			PreparedStatement st = connection.prepareStatement(query);
			st.setString(1, companyName);
			ResultSet rs = st.executeQuery();
			if (!rs.next()) {
				st.close();
				throw new EntityNotExistException("Company name: " + companyName + " not exist in DB");
			} else {
				return rs.getLong("company_id");
			}
		} catch (SQLException e) {
			throw new CouponSystemException(e);
		} 
	}
	
	public void clearDB() throws CouponSystemException{
		Connection connection = cp.getConnection();
		try {
			String query = "DELETE FROM company WHERE 1=1;";
			Statement st = connection.createStatement();
			st.execute(query);
			st.close();
			connection.commit();
		} catch (SQLException e) {
			throw new CouponSystemException("Somthing went wrong during clean-up of 'company' table");
		}
		cp.returnConnection(connection);
	}
	
	private Collection<Coupon> getCouponsByCompanyId(long companyId, Connection connection) throws SQLException, EntityNotExistException {
		Collection<Coupon> coupons = new ArrayList<Coupon>();
		ResultSet rs = selectCompanyById(companyId, connection);
		//if there are no any company with such ID in db
		if  ( !rs.next() ) {
			throw new EntityNotExistException("Company id :" + companyId + " not exist in DB");
		} else {	
			String query = "SELECT * FROM coupon WHERE company_id = ? AND status <> 3;";
			PreparedStatement st = connection.prepareStatement(query);
			st.setLong(1, companyId);
			rs = st.executeQuery();
			if (!rs.next()) {
				return coupons;
			} else {
				do {
					coupons.add(new Coupon(rs.getLong("coupon_id"),
							rs.getString("title"),
							rs.getDate("start_date"),
							//TODO: convert java.sql.date to java.util.date
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
	}
}
