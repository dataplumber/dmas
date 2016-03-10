//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
package gov.nasa.podaac.inventory.core;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

import oracle.jdbc.driver.OracleConnection;

import org.hibernatespatial.helper.FinderException;
import org.hibernatespatial.oracle.ConnectionFinder;

import com.mchange.v2.c3p0.C3P0ProxyConnection;

/**
 * C3P0 <code>ConnectionFinder</code> implementation.
 * <p>
 * This implementation attempts to retrieve the <code>OracleConnection</code>
 * using the c3p0 rawConnection. The purpose is to make c3p0 connection pool
 * work with hibernate spatial oracle.
 * </p>
 * @author clwong
 *
 * $Id: C3P0OracleConnectionFinder.java 13176 2014-04-03 18:56:47Z gangl $
 */
public class C3P0OracleConnectionFinder implements ConnectionFinder {
	
    public static Connection getRawConnection(Connection con) {
        return con;
    }

	public OracleConnection find(Connection con) throws FinderException {
		if (con == null) {
			return null;
		}
		if (con instanceof OracleConnection) {
			return (OracleConnection) con;
		}
		else if (con instanceof C3P0ProxyConnection) {
			C3P0ProxyConnection cpCon = (C3P0ProxyConnection) con;
			try {
				Method method = getClass().getMethod("getRawConnection", new Class[] {Connection.class});
				return (OracleConnection) cpCon.rawConnectionOperation(
						method, null, new Object[] {C3P0ProxyConnection.RAW_CONNECTION});
			}
			catch (SQLException ex) {
				throw new FinderException(ex.getMessage());
			}
			catch (Exception ex) {
				throw new FinderException("Error in reflection:"+ex.getMessage());
			}
        }
        else {
            Connection conTmp = null;
			try {
				conTmp = con.getMetaData().getConnection();
			} catch (SQLException e) {}
            if (conTmp instanceof OracleConnection) return (OracleConnection) conTmp;
        }
		throw new FinderException("Could not find Native Connection of type OracleConnection");
	}
           
}
