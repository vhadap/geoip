package com.soha.geoip.test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Map;

import com.soha.geoip.GeoIpServer;
import com.soha.geoip.GeoIpService;

public class GeoIpTests {
	static String rmi_ip = "10.5.0.78";
	static GeoIpService geoIpServer;
	
	static void getGeoIpData(String ip){
		System.out.println("getGeoIpData");
//		InetAddress ipAddress = null;
//		try {
//			ipAddress = InetAddress.getByName(ip);
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		long cts = System.currentTimeMillis();
		Map data;
		try {
			data = geoIpServer.getIpData(ip);
			long tts = (System.currentTimeMillis() - cts);
			System.out.println(ip+" took "+tts + " ms: "+data);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static void main(String[] args) throws Exception {
		// Call registry for GeoIpServer
		geoIpServer = (GeoIpService) Naming.lookup("rmi://" + rmi_ip + "/GeoIpServer");
		
		getGeoIpData("128.101.101.101");
		getGeoIpData("202.83.18.161");
		getGeoIpData("54.173.0.158");
		getGeoIpData("54.68.209.217");
		getGeoIpData("115.99.182.229");
		getGeoIpData("54.209.12.45");
		getGeoIpData("101.183.110.185");
		getGeoIpData("122.172.134.48");
		getGeoIpData("101.183.62.248");
		getGeoIpData("66.249.64.12");
		getGeoIpData("122.171.91.116");
		getGeoIpData("66.249.89.17");
		getGeoIpData("122.167.137.143");
	}

}
