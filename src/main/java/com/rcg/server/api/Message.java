package com.rcg.server.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public interface Message {

	public boolean hasTask();
	
	public Task getTask();
	
	public long getSizeInBytes();
	
}
