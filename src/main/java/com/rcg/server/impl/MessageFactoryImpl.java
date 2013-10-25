package com.rcg.server.impl;

import java.util.HashMap;
import java.util.Map;

import com.rcg.server.api.MessageFactory;

public class MessageFactoryImpl implements MessageFactory {

	private volatile int current = 0;
	private Map<Integer, Class<?>> registeredMessageTypes = new HashMap<Integer, Class<?>>();

	@Override
	public int registerMessageType(Class<?> classObject) {
		int resultType = -1;
		if (!registeredMessageTypes.containsValue(classObject)) {
			resultType = current++;
		} else {
			for (Integer type : registeredMessageTypes.keySet()) {
				if (classObject.equals(registeredMessageTypes.get(type))) {
					resultType = type;
				}
			}
		}
		return resultType;
	}

	@Override
	public Class<?> getMessageClassByType(int messageType) {
		return registeredMessageTypes.get(messageType);
	}

}
