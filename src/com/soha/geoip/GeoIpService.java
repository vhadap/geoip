package com.soha.geoip;

import java.net.InetAddress;
import java.rmi.Remote;
import java.util.Map;

public interface GeoIpService extends Remote {

	public Map getIpData(InetAddress ipAddress);

}
