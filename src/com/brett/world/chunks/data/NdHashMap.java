package com.brett.world.chunks.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
* @author Brett
* @date Jun. 28, 2020
*/

public class NdHashMap<K, V> implements Cloneable {
	
	public volatile ConcurrentHashMap<K, ConcurrentHashMap<K, ConcurrentHashMap<K, V>>> hm = new ConcurrentHashMap<K, ConcurrentHashMap<K, ConcurrentHashMap<K, V>>>();
	
	public V set(K k1, K k2, K k3, V v) {
		ConcurrentHashMap<K, ConcurrentHashMap<K, V>> bl = hm.get(k1);
		if (bl == null) {
			bl = new ConcurrentHashMap<K, ConcurrentHashMap<K, V>>();
			hm.put(k1, bl);
		}
		ConcurrentHashMap<K, V> hm = bl.get(k2);
		if (hm == null) {
			hm = new ConcurrentHashMap<K, V>();
			bl.put(k2, hm);
		}
		V in = hm.get(k3);
		if (in != null)
			hm.remove(k3);
		hm.put(k3, v);
		return in;
	}
	
	public void put(NdHashMap<K, V> hm) {
		hm.iterate((NdHashMap<K, V> dt, K k1, K k2, K k3, V v1)->{
			this.set(k1, k2, k3, v1);
		});
	}
	
	/**
	 * fast and rough size of the map.
	 */
	public int sizeF() {
		return hm.size()*3;
	}
	
	public int sizeS() {
		Counter c = new Counter();
		iterate((NdHashMap<K, V> dt, K k1, K k2, K k3, V v1) -> {
			c.increment();
		});
		return c.count();
	}
	
	public void clear() {
		hm = new ConcurrentHashMap<K, ConcurrentHashMap<K,ConcurrentHashMap<K,V>>>();
	}
	
	public NdHashMap<K, V> clone(){
		NdHashMap<K, V> ndhm = new NdHashMap<K, V>();
		iterate((NdHashMap<K, V> dt, K k1, K k2, K k3, V v1) -> {
			ndhm.set(k1, k2, k3, v1);
		});
		return ndhm;
	}
	
	public List<NdHashMap<K, V>> cloneQuarter(){
		List<NdHashMap<K, V>> lst = new ArrayList<NdHashMap<K,V>>();
		lst.add(new NdHashMap<K, V>());
		lst.add(new NdHashMap<K, V>());
		lst.add(new NdHashMap<K, V>());
		lst.add(new NdHashMap<K, V>());
		Counter c = new Counter();
		iterate((NdHashMap<K, V> dt, K k1, K k2, K k3, V v1) -> {
			lst.get(c.count()).set(k1, k2, k3, v1);
			c.increment();
			c.modulo(4);
		});
		return lst;
	}
	
	public List<NdHashMap<K, V>> split(int amount){
		List<NdHashMap<K, V>> lst = new ArrayList<NdHashMap<K,V>>();
		for (int i = 0; i < amount; i++)
			lst.add(new NdHashMap<K, V>());
		Counter c = new Counter();
		iterate((NdHashMap<K, V> dt, K k1, K k2, K k3, V v1) -> {
			lst.get(c.count()).set(k1, k2, k3, v1);
			c.increment();
			c.modulo(amount);
		});
		return lst;
	}
	
	public void clone(NdHashMap<K, V> hm) {
		iterate((NdHashMap<K, V> dt, K k1, K k2, K k3, V v1) -> {
			hm.set(k1, k2, k3, v1);
		});
	}
	
	/**
	 * clears all values from this hashmap using the provided hashmap (if they exist)
	 */
	public void clear(NdHashMap<K, V> hm) {
		hm.iterate((NdHashMap<K, V> dt, K k1, K k2, K k3, V v1) -> {
			if (this.containsKey(k1, k2, k3))
				this.remove(k1, k2, k3);
		});
	}
	
	public boolean containsKey(K k1, K k2, K k3) {
		ConcurrentHashMap<K, ConcurrentHashMap<K, V>> h1 = hm.get(k1);
		if (h1 == null)
			return false;
		ConcurrentHashMap<K, V> h2 = h1.get(k2);
		if (h2 == null)
			return false;
		if (h2.get(k3) == null)
			return false;
		return true;
	}
	
	/**
	 * iterate through all elements in the hashmap
	 * @param func function to callback for each entry 
	 */
	public void iterate(NdLoopAll<K, V> func) {
		// not sure if this is the best way but this function isn't ment to be called all the time.
		// creates too many objects.
		Iterator<Entry<K, ConcurrentHashMap<K, ConcurrentHashMap<K, V>>>> l1 = hm.entrySet().iterator();
		while (l1.hasNext()) {
			Entry<K, ConcurrentHashMap<K, ConcurrentHashMap<K, V>>> lk1 = l1.next();
			K x = lk1.getKey();
			Iterator<Entry<K, ConcurrentHashMap<K, V>>> l2 = lk1.getValue().entrySet().iterator();
			while (l2.hasNext()) {
				Entry<K, ConcurrentHashMap<K, V>> lk2 = l2.next();
				K y = lk2.getKey();
				Iterator<Entry<K, V>> l3 = lk2.getValue().entrySet().iterator();
				while (l3.hasNext()) {
					Entry<K, V> lk3 = l3.next();
					K z = lk3.getKey();
					func.loopd(this, x, y, z, lk3.getValue());
				}
			}
		}
	}
	
	public V setN(K k1, K k2, K k3, V v) {
		ConcurrentHashMap<K, ConcurrentHashMap<K, V>> bl = hm.get(k1);
		if (bl == null) {
			bl = new ConcurrentHashMap<K, ConcurrentHashMap<K, V>>();
			hm.put(k1, bl);
		}
		ConcurrentHashMap<K, V> hm = bl.get(k2);
		if (hm == null) {
			hm = new ConcurrentHashMap<K, V>();
			bl.put(k2, hm);
		}
		V in = hm.get(k3);
		if (in != null)
			return in;
		hm.put(k3, v);
		return null;
	}
	
	public void setNC(K k1, K k2, K k3, V v) {
		ConcurrentHashMap<K, ConcurrentHashMap<K, V>> bl = hm.get(k1);
		if (bl == null) {
			bl = new ConcurrentHashMap<K, ConcurrentHashMap<K, V>>();
			hm.put(k1, bl);
		}
		ConcurrentHashMap<K, V> hm = bl.get(k2);
		if (hm == null) {
			hm = new ConcurrentHashMap<K, V>();
			bl.put(k2, hm);
		}
		hm.put(k3, v);
	}
	
	/**
	 * gets at an index but creates maps when null.
	 */
	public V getC(K k1, K k2, K k3) {
		ConcurrentHashMap<K, ConcurrentHashMap<K, V>> bl = hm.get(k1);
		if (bl == null) {
			bl = new ConcurrentHashMap<K, ConcurrentHashMap<K, V>>();
			hm.put(k1, bl);
		}
		ConcurrentHashMap<K, V> hm = bl.get(k2);
		if (hm == null) {
			hm = new ConcurrentHashMap<K, V>();
			bl.put(k2, hm);
		}
		V in = hm.get(k3);
		return in;
	}
	
	public V get(K k1, K k2, K k3) {
		ConcurrentHashMap<K, ConcurrentHashMap<K, V>> bl = hm.get(k1);
		if (bl == null)
			return null;
		ConcurrentHashMap<K, V> hm = bl.get(k2);
		if (hm == null) 
			return null;
		V in = hm.get(k3);
		return in;
	}
	
	public V remove(K k1, K k2, K k3) {
		ConcurrentHashMap<K, ConcurrentHashMap<K, V>> bl = hm.get(k1);
		if (bl == null)
			return null;
		ConcurrentHashMap<K, V> hm = bl.get(k2);
		if (hm == null) 
			return null;
		V in = hm.get(k3);
		if (in != null)
			hm.remove(k3);
		return in;
	}
	
}
