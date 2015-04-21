package com.soha.geoip;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedHashMap;
import java.util.Map;

import com.maxmind.geoip2.*;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.*;
import com.maxmind.geoip2.record.*;

public class GeoIpServer extends UnicastRemoteObject implements GeoIpService {

	private static final long serialVersionUID = 213218932983171382L;

	// A File object pointing to your GeoIP2 or GeoLite2 database
	static File database = null;
	static DatabaseReader reader;

	public GeoIpServer() throws RemoteException {
		super();
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
				}
			}
		}
	}

	@Override
	public Map getIpData(InetAddress ipAddress) {
		Map ret = new LinkedHashMap();
		try {
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


	public static void main ( String args[] ) throws Exception
	{
		// Assign a security manager, in the event that dynamic
		// classes are loaded
		if (System.getSecurityManager() == null)
			System.setSecurityManager ( new RMISecurityManager() );

		// Create an instance of server ...
		GeoIpServer svr = new GeoIpServer();

		// ... and bind it with the RMI Registry
		Naming.bind ("GeoIpServer", svr);

		System.out.println ("Service bound....");
	}


}