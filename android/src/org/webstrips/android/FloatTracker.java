package org.webstrips.android;

public class FloatTracker {

	private static float DEFAULT_THRESHOLD = 0.001f;
	
	private static float DEFAULT_SPEED = 0.3f;
	
	private float value, goal, speed, threshold;
	
	public FloatTracker(float value, float goal, float speed, float threshold) {
		
		this.value = value;
		this.goal = goal;
		this.speed = Math.min(1, Math.max(0, speed));
		this.threshold = threshold;
		
	}
	
	public FloatTracker(float value, float goal, float speed) {
		this(value, goal, speed, DEFAULT_THRESHOLD);
	}
	
	public FloatTracker(float value, float goal) {
		this(value, goal, DEFAULT_SPEED, DEFAULT_THRESHOLD);
	}
	
	public boolean onGoal() {
		return Math.abs(value - goal) < threshold;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public float getGoal() {
		return goal;
	}

	public void setGoal(float goal) {
		this.goal = goal;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}
	
	public float track() {
		if (onGoal()) {
			value = goal;
			return value;
		}
		float old = value;
		value = value + (goal - value) * speed;
		return old;
	}
}
