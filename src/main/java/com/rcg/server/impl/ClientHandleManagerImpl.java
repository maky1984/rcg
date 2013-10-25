package com.rcg.server.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.rcg.server.api.ClientHandle;
import com.rcg.server.api.ClientHandleManager;

public class ClientHandleManagerImpl implements ClientHandleManager {

	public static final long UNKNOWN_UID = -1;

	private Map<Long, ClientHandle> handles = Collections.synchronizedMap(new HashMap<Long, ClientHandle>());

	private ClientHandle anonym = new ClientHandleImpl(UNKNOWN_UID);

	public ClientHandleManagerImpl() {
	}

	@Override
	public ClientHandle getClientHandle(long uid) {
		ClientHandle handle = handles.get(uid);
		return handle == null ? anonym : handle;
	}

}
