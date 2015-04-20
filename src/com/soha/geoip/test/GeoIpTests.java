package com.soha.geoip.test;

import java.net.InetAddress;
import java.util.Map;

import com.soha.geoip.GeoIpAccess;

public class GeoIpTests {

	public static void main(String[] args) throws Exception {
		GeoIpAccess.init();

		InetAddress ipAddress = InetAddress.getByName("128.101.101.101");
		long cts = System.currentTimeMillis();
		Map data = GeoIpAccess.getIpData(ipAddress);
		long tts = (System.currentTimeMillis() - cts);
		System.out.println("getIpData took "+tts + " ms\n"+data);
	}

}
