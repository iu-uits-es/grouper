/**
 * @author mchyzer $Id$
 */
package edu.internet2.middleware.grouper.j2ee;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import org.apache.commons.logging.Log;
import org.hibernate.SessionFactory;

import edu.internet2.middleware.grouper.cache.EhcacheController;
import edu.internet2.middleware.grouper.cfg.GrouperConfig;
import edu.internet2.middleware.grouper.internal.dao.hib3.Hib3DAO;
import edu.internet2.middleware.grouper.util.GrouperUtil;

/**
 *
 */
public class ServletContextUtils {

  /** logger */
  private static final Log LOG = GrouperUtil.getLog(ServletContextUtils.class);

  /**
   * 
   */
  public ServletContextUtils() {
  }

  /**
   * 
   */
  public static void contextDestroyed() {
    
    if (GrouperConfig.retrieveConfig().propertyValueBoolean("grouper.exit.close.hibernate", true)) {
      try {
        SessionFactory sessionFactory = Hib3DAO.getSessionFactory();
        sessionFactory.close();
      } catch (Exception e) {
        LOG.error("Problem closing hibernate session factory", e);
      }
    }
    
    if (GrouperConfig.retrieveConfig().propertyValueBoolean("grouper.exit.stop.ehcache", true)) {
      try {
        EhcacheController.ehcacheController().stop();
      } catch (Exception e) {
        LOG.error("Problem stopping ehcache", e);
      }
    }

    if (GrouperConfig.retrieveConfig().propertyValueBoolean("grouper.exit.deregister.database.drivers", true)) {

      Enumeration<Driver> drivers = DriverManager.getDrivers();
      while (drivers.hasMoreElements()) {
        Driver driver = drivers.nextElement();
        try {
          DriverManager.deregisterDriver(driver);
          LOG.debug("deregistering jdbc driver: " + driver);
        } catch (SQLException e) {
          LOG.error("Error deregistering driver " + driver, e);
        }
      }
    }
    
  }

}
