package com.worldgn.connector;

interface HttpPostListener {
	public void onSuccess(String response);
	public void onError();
	
}
