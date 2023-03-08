package com.worldgn.connector;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

abstract class PreferenceBase {
	public abstract String prefName();

	public abstract Context getContext();

	public SharedPreferences getPref() {
		return getContext().getSharedPreferences(prefName(),
				Context.MODE_MULTI_PROCESS | Context.MODE_MULTI_PROCESS);
	}

	public int getInt(String arg, int defaultval) {
		SharedPreferences pref = getContext().getSharedPreferences(prefName(),
				Context.MODE_MULTI_PROCESS | Context.MODE_MULTI_PROCESS);
		return pref.getInt(arg, defaultval);
	}

	public void setInt(String arg, int value) {
		SharedPreferences pref = getContext().getSharedPreferences(prefName(),
				Context.MODE_MULTI_PROCESS | Context.MODE_MULTI_PROCESS);
		Editor editor = pref.edit();
		editor.putInt(arg, value);
		editor.commit();
	}

	public float getFloat(String arg, float defaultval) {
		SharedPreferences pref = getContext().getSharedPreferences(prefName(),
				Context.MODE_MULTI_PROCESS | Context.MODE_MULTI_PROCESS);
		return pref.getFloat(arg, defaultval);
	}

	public void setFloat(String arg, float value) {
		SharedPreferences pref = getContext().getSharedPreferences(prefName(),
				Context.MODE_MULTI_PROCESS | Context.MODE_MULTI_PROCESS);
		Editor editor = pref.edit();
		editor.putFloat(arg, value);
		editor.commit();
	}

	public long getLong(String arg, long defaultval) {
		SharedPreferences pref = getContext().getSharedPreferences(prefName(),
				Context.MODE_MULTI_PROCESS | Context.MODE_MULTI_PROCESS);
		return pref.getLong(arg, defaultval);
	}

	public void setLong(String arg, long value) {
		SharedPreferences pref = getContext().getSharedPreferences(prefName(),
				Context.MODE_MULTI_PROCESS | Context.MODE_MULTI_PROCESS);
		Editor editor = pref.edit();
		editor.putLong(arg, value);
		editor.commit();
	}

	public String getString(String arg, String defaultval) {
		SharedPreferences pref = getContext().getSharedPreferences(prefName(),
				Context.MODE_MULTI_PROCESS | Context.MODE_MULTI_PROCESS);
		return pref.getString(arg, defaultval);
	}

	public void setString(String arg, String value) {
		SharedPreferences pref = getContext().getSharedPreferences(prefName(),
				Context.MODE_MULTI_PROCESS | Context.MODE_MULTI_PROCESS);
		Editor editor = pref.edit();
		editor.putString(arg, value);
		editor.commit();
	}

	public boolean getBoolean(String arg, boolean defaultval) {
		SharedPreferences pref = getContext().getSharedPreferences(prefName(),
				Context.MODE_MULTI_PROCESS | Context.MODE_MULTI_PROCESS);
		return pref.getBoolean(arg, defaultval);
	}

	public void setBoolean(String arg, boolean value) {
		SharedPreferences pref = getContext().getSharedPreferences(prefName(),
				Context.MODE_MULTI_PROCESS | Context.MODE_MULTI_PROCESS);
		Editor editor = pref.edit();
		editor.putBoolean(arg, value);
		editor.commit();
	}

	public void removePref(String arg) {
		SharedPreferences pref = getContext().getSharedPreferences(prefName(),
				Context.MODE_MULTI_PROCESS | Context.MODE_MULTI_PROCESS);
		Editor editor = pref.edit();
		editor.remove(arg);
		editor.commit();
	}

	public boolean isKeySet(String arg) {
		SharedPreferences pref = getContext().getSharedPreferences(prefName(),
				Context.MODE_MULTI_PROCESS | Context.MODE_MULTI_PROCESS);
		return pref.contains(arg);
	}

	public void clear(){
		SharedPreferences pref = getContext().getSharedPreferences(prefName(),
				Context.MODE_MULTI_PROCESS | Context.MODE_MULTI_PROCESS);
		Editor editor = pref.edit();
		editor.clear();
		editor.commit();
	}
}
