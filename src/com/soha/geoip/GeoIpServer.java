package com.soha.geoip;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLClassLoader;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.Permission;
import java.util.LinkedHashMap;
import java.util.Map;

import com.maxmind.geoip2.*;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.*;
import com.maxmind.geoip2.record.*;

public class GeoIpServer extends UnicastRemoteObject implements GeoIpService, Serializable {

	private static final long serialVersionUID = 213218932983171382L;

	// A File object pointing to your GeoIP2 or GeoLite2 database
	static File database = null;
	static DatabaseReader reader;

	public GeoIpServer() throws RemoteException {
		//super();
		init();
	}

	public void init (){
		synchronized(this){
			if (database == null){
				try {
					database = new File("//home/sbapp/software/geoip/GeoIP2-City_20150414/GeoIP2-City.mmdb");
					// This creates the DatabaseReader object, which should be reused across
					// lookups.
					reader = new DatabaseReader.Builder(database).build();
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(0);
				}
			}
		}
	}

	@Override
	public Map getIpData(String ip) throws RemoteException{
		Map ret = new LinkedHashMap();
		try {
			InetAddress ipAddress = InetAddress.getByName(ip);
			// Replace "city" with the appropriate method for your database, e.g.,
			// "country".
			CityResponse response = reader.city(ipAddress);

			Country country = response.getCountry();
			//			System.out.println(country.getIsoCode());            // 'US'
			//			System.out.println(country.getName());               // 'United States'
			//			System.out.println(country.getNames().get("zh-CN")); // '美国'
			ret.put("Country", country.getName());
			ret.put("CC", country.getIsoCode());

			Subdivision subdivision = response.getMostSpecificSubdivision();
			//			System.out.println(subdivision.getName());    // 'Minnesota'
			//			System.out.println(subdivision.getIsoCode()); // 'MN'
			ret.put("State", subdivision.getName());
			ret.put("SC", subdivision.getIsoCode());

			City city = response.getCity();
			//			System.out.println(city.getName()); // 'Minneapolis'
			ret.put("City", city.getName());

			Postal postal = response.getPostal();
			//			System.out.println(postal.getCode()); // '55455'

			Location location = response.getLocation();
			//			System.out.println(location.getLatitude());  // 44.9733
			//			System.out.println(location.getLongitude()); // -93.2323
			ret.put("Lat", location.getLatitude());
			ret.put("Long", location.getLongitude());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GeoIp2Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ret;
	}

	static class CustomSecurityManager extends SecurityManager {

		SecurityManager original;

		CustomSecurityManager(SecurityManager original) {
			this.original = original;
		}

		/**
		 * Deny permission to exit the VM.
		 */
		public void checkExit(int status) {
			//throw(new SecurityException("Not allowed"));
		}

		/**
		 * Allow this security manager to be replaced, if fact, allow pretty
		 * much everything.
		 */
		public void checkPermission(Permission perm) {
		}

		public SecurityManager getOriginalSecurityManager() {
			return original;
		}
	}

	public static void main ( String args[] ) throws Exception
	{
		System.out.println("Starting GeoIpServer...");
		System.out.println("GeoIpServer Class.forName: "+Class.forName ("com.soha.geoip.GeoIpService").getName());
		ClassLoader cl = ClassLoader.getSystemClassLoader();

		URL[] urls = ((URLClassLoader)cl).getURLs();

		for(URL url: urls){
			System.out.println(url.getFile());
		}

		try {

			Class loadedClass = Class.forName("com.soha.geoip.GeoIpService");
			System.out.println("Class " + loadedClass + " found successfully!");
		}
		catch (ClassNotFoundException ex) {
			System.err.println("A ClassNotFoundException was caught: " + ex.getMessage());
			ex.printStackTrace();
		}

		// Assign a security manager, in the event that dynamic
		// classes are loaded
		//if (System.getSecurityManager() == null)
		System.setSecurityManager ( new CustomSecurityManager(System.getSecurityManager()) );

		// Create an instance of server ...
		GeoIpServer svr = new GeoIpServer();
		//Naming.bind ("GeoIpServer", svr);

		//GeoIpService stub = (GeoIpService) UnicastRemoteObject.exportObject(svr, 0);
		//Registry registry = LocateRegistry.getRegistry();
		Registry registry = LocateRegistry.createRegistry(1099);
		// ... and bind it with the RMI Registry
		registry.bind("GeoIpServer", svr);


		System.out.println ("GeoIpServer bound....");
	}


}
