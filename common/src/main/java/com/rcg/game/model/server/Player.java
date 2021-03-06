package com.rcg.game.model.server;

import java.util.List;

import com.rcg.server.api.ClientHandle;

public interface Player {

	public void setClientHandle(ClientHandle handle);

	public String getName();

	public long getId();

	public ClientHandle getClientHandle();

	public List<Long> getAllCardIds();

	public List<Long> getAllDeckIds();

}
