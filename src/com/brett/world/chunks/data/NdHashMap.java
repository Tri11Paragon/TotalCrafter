package com.brett.world.chunks.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
* @author Brett
* @date Jun. 28, 2020
*/

public class NdHashMap<K, V> implements Cloneable {
	
	public HashMap<K, HashMap<K, HashMap<K, V>>> hm = new HashMap<K, HashMap<K, HashMap<K, V>>>();
	
	public V set(K k1, K k2, K k3, V v) {
		HashMap<K, HashMap<K, V>> bl = hm.get(k1);
		if (bl == null) {
			bl = new HashMap<K, HashMap<K, V>>();
			hm.put(k1, bl);
		}
		HashMap<K, V> hm = bl.get(k2);
		if (hm == null) {
			hm = new HashMap<K, V>();
			bl.put(k2, hm);
		}
		V in = hm.get(k3);
		if (in != null)
			hm.remove(k3);
		hm.put(k3, v);
		return in;
	}
	
	public NdHashMap<K, V> clone(){
		NdHashMap<K, V> ndhm = new NdHashMap<K, V>();
		iterate((HashMap<K, HashMap<K, HashMap<K, V>>> map, NdHashMap<K, V> dt, K k1, K k2, K k3, V v1) -> {
			ndhm.set(k1, k2, k3, v1);
		});
		return ndhm;
	}
	
	public void clone(NdHashMap<K, V> hm) {
		iterate((HashMap<K, HashMap<K, HashMap<K, V>>> map, NdHashMap<K, V> dt, K k1, K k2, K k3, V v1) -> {
			hm.set(k1, k2, k3, v1);
		});
	}
	
	public boolean containsKey(K k1, K k2, K k3) {
		return hm.containsKey(k1) ? hm.get(k1).containsKey(k2) ? hm.get(k2).containsKey(k3) ? true : false : false : false;
	}
	
	/**
	 * iterate through all elements in the hashmap
	 * @param func function to callback for each entry 
	 */
	public void iterate(NdLoopAll<K, V> func) {
		// not sure if this is the best way but this function isn't ment to be called all the time.
		// creates too many objects.
		Iterator<Entry<K, HashMap<K, HashMap<K, V>>>> l1 = hm.entrySet().iterator();
		while (l1.hasNext()) {
			Entry<K, HashMap<K, HashMap<K, V>>> lk1 = l1.next();
			K x = lk1.getKey();
			Iterator<Entry<K, HashMap<K, V>>> l2 = lk1.getValue().entrySet().iterator();
			while (l2.hasNext()) {
				Entry<K, HashMap<K, V>> lk2 = l2.next();
				K y = lk2.getKey();
				Iterator<Entry<K, V>> l3 = lk2.getValue().entrySet().iterator();
				while (l3.hasNext()) {
					Entry<K, V> lk3 = l3.next();
					K z = lk3.getKey();
					func.loopd(hm, this, x, y, z, lk3.getValue());
				}
			}
		}
	}
	
	public V setN(K k1, K k2, K k3, V v) {
		HashMap<K, HashMap<K, V>> bl = hm.get(k1);
		if (bl == null) {
			bl = new HashMap<K, HashMap<K, V>>();
			hm.put(k1, bl);
		}
		HashMap<K, V> hm = bl.get(k2);
		if (hm == null) {
			hm = new HashMap<K, V>();
			bl.put(k2, hm);
		}
		V in = hm.get(k3);
		if (in != null)
			return in;
		hm.put(k3, v);
		return null;
	}
	
	public void setNC(K k1, K k2, K k3, V v) {
		HashMap<K, HashMap<K, V>> bl = hm.get(k1);
		if (bl == null) {
			bl = new HashMap<K, HashMap<K, V>>();
			hm.put(k1, bl);
		}
		HashMap<K, V> hm = bl.get(k2);
		if (hm == null) {
			hm = new HashMap<K, V>();
			bl.put(k2, hm);
		}
		hm.put(k3, v);
	}
	
	/**
	 * gets at an index but creates maps when null.
	 */
	public V getC(K k1, K k2, K k3) {
		HashMap<K, HashMap<K, V>> bl = hm.get(k1);
		if (bl == null) {
			bl = new HashMap<K, HashMap<K, V>>();
			hm.put(k1, bl);
		}
		HashMap<K, V> hm = bl.get(k2);
		if (hm == null) {
			hm = new HashMap<K, V>();
			bl.put(k2, hm);
		}
		V in = hm.get(k3);
		return in;
	}
	
	public V get(K k1, K k2, K k3) {
		HashMap<K, HashMap<K, V>> bl = hm.get(k1);
		if (bl == null)
			return null;
		HashMap<K, V> hm = bl.get(k2);
		if (hm == null) 
			return null;
		V in = hm.get(k3);
		return in;
	}
	
}