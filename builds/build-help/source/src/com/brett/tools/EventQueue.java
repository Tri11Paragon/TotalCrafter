package com.brett.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brett.voxel.tools.IEventListener;

/**
*
* @author brett
* @date Mar. 15, 2020
* Yes I made an event handler for this
* No I don't think I use it. -May 26
* ignore this class
*/

public class EventQueue {
	
	private static Map<Integer, List<IEventListener>> events = new HashMap<Integer, List<IEventListener>>();
	
	public static void regiserEvent(int event, IEventListener e) {
		if (events.containsKey(event)) {
			List<IEventListener> ls = events.get(event);
			ls.add(e);
			// might not need this but hey just making sure.
			events.remove(event);
			events.put(event, ls);
		} else {
			List<IEventListener> ls = new ArrayList<IEventListener>();
			ls.add(e);
			events.put(event, ls);
		}
	}
	
	public static void doEvent(int event) {
		if (events.containsKey(event)) {
			List<IEventListener> el = events.get(event);
			for (IEventListener e : el) {
				e.event();
			}
		}
	}
	
}
