/**
 * Redes Integradas de Telecomunicacoes
 * MEEC/MERSIM 2026/2027
 *
 * Routing.java
 *
 * Objects that store routing table information and provide supporting functions
 *
 * @author Luis Bernardo
 */
package router;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Iterator;


/**
 * Store routing table information and provide supporting functions
 */
public final class RoutingTable {
    /** Routing table object */
    private final ConcurrentHashMap<Character,RouteEntry> rtab;
    
    /**
     * Constructor
     */
    public RoutingTable() {
        rtab= new ConcurrentHashMap<>();
    }

    /**
     * Constructor that clones table received
     * @param src  Initial table 
     */
    public RoutingTable(RoutingTable src) {
        rtab= new ConcurrentHashMap<>();
        merge_table(src);
    }
    
    /**
     * Check if the routing table is defined and initialized 
     * @return true if it is defined
     */
    public boolean is_valid() {
        return (rtab!=null) && !rtab.isEmpty();
    }
    
    /**
     * Size of the routing table
     * @return the number of entries in the routing table
     */
    public int size() {    
        if (!is_valid())
            return 0;
        return rtab.size();
    }
    
    /**
     * Clears the routing table and stops all hold down timers
     */
    public void clear() {
        if (rtab != null) {
            for (RouteEntry re : rtab.values()) {
                if (re.is_holddown())
                    re.stop_holddown(false);
            }
            rtab.clear();
        }
    }
    
    /**
     * Add or replace a route entry to the routing table
     * @param re RouteEntry object
     */
    public void add_route(RouteEntry re) {
        if ((rtab != null) && (re!=null))
            rtab.put(re.dest, re);
    }
    
    /**
     * Merge routing table - select the shortest distance and exclude routes through exclude_area
     * @param rt  Routing table to merge
     */
    public void merge_table(RoutingTable rt) {
        if ((rtab == null) || (rt == null) || !rt.is_valid())
            return;
        for (RouteEntry re: rt.rtab.values()) {
            RouteEntry aux= rtab.get(re.dest);
            if ((aux==null) || (re.dist < aux.dist))
                rtab.put(re.dest, new RouteEntry(re));
        }
    }
    
    /**
     * Returns the RouteEntry associated to a destination
     * @param dest destination
     * @return RouteEntry object
     */
    public RouteEntry get_RouteEntry(char dest) {
        if (!is_valid())
            return null;
        return rtab.get(dest);
    }
    
    /**
     * Return the route's set
     * @return set of all RouteEntry 
     */
    public Collection<RouteEntry> get_routeset() {
        if (!is_valid())
            return null;
        return rtab.values();
    }
    
    /**
     * Delete route from routing table
     * @param re    the RouteEntry object to remove
     * @return  true if the object was removed, false otherwise
     */
    public boolean delete_routeEntry(RouteEntry re) {
        if (rtab == null)
            return false;
        return rtab.remove(re.dest, re);
    }
    
    /**
     * Return the routing table as an array of Entry
     * @return Entry vector with table contents 
     */
    public Entry[] get_Entry_vector() {
        if (!is_valid())
            return null;
        Entry[] vec= new Entry[rtab.size()];
        rtab.values().toArray(vec);
        return vec;
    }   
    
    /**
     * Returns the next hop address in the path to dest
     * @param dest destination
     * @return the next hop address
     */
    public char nextHop(char dest) {
        RouteEntry re= get_RouteEntry(dest);
        if (re == null)
            return ' ';
        return re.next_hop;
    }
    
    /**
     * Builds an iterator to the RouteEntry values
     * @return the iterator
     */
    public Iterator<RouteEntry> iterator() {
        if (!is_valid())
            return null;
        return rtab.values().iterator();
    }
    
    /**
     * Compare the local routing tables with rt
     * @param rt - routing table
     * @return true if rt is equal to rtab and not null, false otherwise
     */
    public boolean equal_RoutingTable(RoutingTable rt) {
        if ((rt == null) || !rt.is_valid() || !is_valid() )
            return false;
        ConcurrentHashMap<Character, RouteEntry> map= rt.rtab;
        if (rtab.size() != map.size()) {
            return false;
        }
        Iterator<RouteEntry> it= rt.iterator();
        while (it.hasNext()) {
            RouteEntry re= it.next();
            if (!re.equals_to(rtab.get(re.dest))) {
                return false;
            }                
        }      
        return true;
    } 
    
    /**
     * Log the content of a routing table object
     * @param log Logging object
     */
    public void Log_routing_table(Log log) {
        if (rtab==null) {
            return;
        }
        for (RouteEntry re: rtab.values()) {
            log.Log(re.toString()+"\n");
        }
    }
    
}
