package com.brett.renderer.lighting;

public class DayCycle implements IDayCycle{

	private boolean timeEnabled = true;
	public long timeInTicks = 0;
	
	@Override
	public void update() {
		if (timeEnabled) {
			
		}
	}

	public boolean isTimeEnabled() {
		return timeEnabled;
	}

	public void setTimeEnabled(boolean timeEnabled) {
		this.timeEnabled = timeEnabled;
	}

	public long getTimeInTicks() {
		return timeInTicks;
	}
	
}
