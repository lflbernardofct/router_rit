/**
 * Redes Integradas de Telecomunicacoes
 * MEEC/MERSIM 2026/2027
 *
 * LocalIPaddresses.java
 *
 * Class that manages local IP addresses and converts all local address to the default one
 *
 * @author  Luis Bernardo
 */
package router;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that manages local IP addresses and converts all local address to the default one
 */
public class LocalIPaddresses {

    /**
     * Constructor
     *
     * @param print_addresses print the list with all local addresses
     */
    public LocalIPaddresses(boolean print_addresses) {
        IPaddrSet = refresh_all_local_IP_addresses(print_addresses);
        update_default_local_IP_address();
    }

    /**
     * Update default local IP address
     */
    public final void update_default_local_IP_address() {
        try {
            // In case you want to test this program without an active network
            //  uncomment this line to always use the loopback address!
            local_IP_address = InetAddress.getLocalHost();
        } catch (Exception e) {
            // In case of error use loopback address
            local_IP_address = InetAddress.getLoopbackAddress();
        }
    }

    /**
     * Get the default local IP address
     *
     * @return default local IP address
     */
    public InetAddress get_default_local_IP_address() {
        return local_IP_address;
    }

    /**
     * Returns an hashset with all machine's IP addresses
     *
     * @param print_addresses print addresses to stdout
     * @return the hashset
     */
    public final HashSet<String> get_all_local_IP_addresses(boolean print_addresses) {
        HashSet<String> addrSet = new HashSet<>();
        try {
            for (Enumeration<NetworkInterface> eni = NetworkInterface.getNetworkInterfaces(); eni.hasMoreElements();) {
                final NetworkInterface ifc = eni.nextElement();
                try {
                    if (ifc.isUp()) {
                        for (Enumeration<InetAddress> ena = ifc.getInetAddresses(); ena.hasMoreElements();) {
                            String ip = ena.nextElement().getHostAddress();
                            if (ip.contains("%")) {
                                // It is an IPv6 address - remove comment with device name
                                ip = ip.substring(0, ip.indexOf('%'));
                            }
                            addrSet.add(ip);
                            if (print_addresses) {
                                System.out.println("Local IP address: " + ip);
                            }
                        }
                    }
                } catch (SocketException ex) {
                    Logger.getLogger(Router.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return addrSet;
        } catch (SocketException ex) {
            Logger.getLogger(Router.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("IP addresses: " + addrSet);
        return addrSet;
    }

    /**
     * Refreshes the set of all local addresses
     *
     * @param print_addresses
     * @return
     */
    public final HashSet<String> refresh_all_local_IP_addresses(boolean print_addresses) {
        update_default_local_IP_address();
        return (IPaddrSet = get_all_local_IP_addresses(print_addresses));
    }

    /**
     * Test if ip is a local address
     *
     * @param ip print all local IP addresses
     * @return the hashset with all addresses
     */
    public boolean is_local_IP_address(InetAddress ip) {
        if (IPaddrSet == null) {
            refresh_all_local_IP_addresses(false);
        }
        return ip.isAnyLocalAddress() || ip.isLinkLocalAddress()
                || ip.isLoopbackAddress() || ip.isMCLinkLocal()
                || ip.isMCNodeLocal() || ip.isMCSiteLocal()
                || ip.isSiteLocalAddress() || IPaddrSet.contains(ip.getHostAddress());
    }

    /**
     * Validates an IP string and returns the corresponding InetAddress.
     * Converts the address to the default local one, if it is local.
     *
     * @param ipstr input string
     * @return the address or null, if not valid
     */
    public InetAddress parse_and_convert_ip_string(String ipstr) {
        InetAddress netip;
        try {
            netip = InetAddress.getByName(ipstr);
        } catch (UnknownHostException e) {
            // Invalid address string
            return null;
        }

        if (is_local_IP_address(netip)) // Return the default IP address
        {
            return get_default_local_IP_address();
        } else {
            return netip;
        }
    }

    /**
     * List of all local IP addresses
     */
    private HashSet<String> IPaddrSet;
    /**
     * cache of default local IP address
     */
    private InetAddress local_IP_address;
}
