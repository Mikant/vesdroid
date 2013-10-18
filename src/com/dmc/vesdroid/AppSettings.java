package com.dmc.vesdroid;

import android.content.SharedPreferences;

public class AppSettings {
	private final SharedPreferences _baseProps;
	
	private int _harmonicsCount;
	private int _phaseCount;
	
	// proj
	private Boolean _reverseOrder; // ??? - btn m'ybe
	
	public AppSettings(SharedPreferences baseProps) {
		_baseProps = baseProps;
	}
}
