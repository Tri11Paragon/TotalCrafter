package com.brett.world.chunks.data;

import java.util.HashMap;

/**
* @author Brett
* @date Jun. 28, 2020
*/

public class NdHashMap<K, V> {
	
	private HashMap<K, HashMap<K, HashMap<K, V>>> hm = new HashMap<K, HashMap<K, HashMap<K, V>>>();
	
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
