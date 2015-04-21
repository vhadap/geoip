package com.soha.geoip;

import java.net.InetAddress;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface GeoIpService extends Remote {

	public Map getIpData(String ip) throws RemoteException;

}
