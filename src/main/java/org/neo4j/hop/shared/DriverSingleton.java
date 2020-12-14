package org.neo4j.hop.shared;

import org.apache.hop.core.logging.ILogChannel;
import org.apache.hop.core.variables.IVariables;
import org.neo4j.driver.Driver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DriverSingleton {

  private static DriverSingleton singleton;

  private Map<String, Driver> driverMap;

  private DriverSingleton() {
    driverMap = new HashMap<>();
  }

  public static DriverSingleton getInstance() {
    if ( singleton == null ) {
      singleton = new DriverSingleton();
    }
    return singleton;
  }

  public static Driver getDriver( ILogChannel log, IVariables variables, NeoConnection connection ) {
    DriverSingleton ds = getInstance();

    String key = getDriverKey( connection, variables);

    Driver driver = ds.driverMap.get( key );
    if ( driver == null ) {
      driver = connection.getDriver( log, variables );
      ds.driverMap.put( key, driver );
    }

    return driver;
  }

  public static void closeAll() {
    DriverSingleton ds = getInstance();

    List<String> keys = new ArrayList<>( ds.getDriverMap().keySet() );
    for ( String key : keys ) {
      synchronized ( ds.getDriverMap() ) {
        Driver driver = ds.driverMap.get( key );
        driver.close();
        ds.driverMap.remove( key );
      }
    }
  }

  private static String getDriverKey( NeoConnection connection, IVariables variables ) {
    String hostname = variables.resolve( connection.getServer() );
    String boltPort = variables.resolve( connection.getBoltPort() );
    String username = variables.resolve( connection.getUsername() );

    return hostname + ":" + boltPort + "@" + username;
  }

  /**
   * Gets driverMap
   *
   * @return value of driverMap
   */
  public Map<String, Driver> getDriverMap() {
    return driverMap;
  }


}
