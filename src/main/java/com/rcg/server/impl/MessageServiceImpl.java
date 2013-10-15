package com.rcg.server.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.rcg.server.api.ClientHandle;
import com.rcg.server.api.ClientHandle.AckStatus;
import com.rcg.server.api.ClientHandleFactory;
import com.rcg.server.api.Message;
import com.rcg.server.api.MessageService;

public class MessageServiceImpl implements MessageService, Runnable {

	public static final int PORT = 47777;

	private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

	private List<Client> clients = Collections.synchronizedList(new ArrayList<Client>());

	private ClientHandleFactory clientHandleFactory = new ClientHandleFactoryImpl();
	
	private ServerSocket serverSocket;
	private ObjectMapper mapper;
	private volatile boolean isStopped;

	private static class Header {
		long uid;
		long code;
		long messageSize;
		long reserved1;
		long reserved2;
		long reserved3;
		long reserved4;
	}

	private static Header createHeader(long uid, long size) {
		Header header = new Header();
		header.uid = uid;
		header.messageSize = size;
		return header;
	}

	private static class Client {
		long uid;
		ClientHandle handle;
		Socket socket;
	}
	
	private static Client createClient(long uid, ClientHandle handle, Socket socket) {
		Client client = new Client();
		client.uid = uid;
		client.handle = handle;
		client.socket = socket;
		return client;
	}

	@Override
	public void open() {
		logger.info("Openning message server with PORT=" + PORT);
		try {
			serverSocket = new ServerSocket(PORT);
			mapper = new ObjectMapper();
			mapper.enableDefaultTyping();
			mapper.enable(SerializationFeature.FLUSH_AFTER_WRITE_VALUE);
		} catch (IOException e) {
			logger.error("Cant create socket");
		}
	}

	private boolean registerOrUpdateClient(Socket socket, Header header) {
		boolean found = false;
		synchronized(clients) {
			for(Client client : clients) {
				if (client.uid == header.uid) {
					if (found) {
						logger.error("ERROR! check error: there several clients with the same uid, client:" + client);
					}
					closeSocket(client.socket);
					client.socket = socket;
				}
			}
			if (!found) {
				// register new client
				ClientHandle client = clientHandleFactory.createClientHandle();
				//clients.add(createClient(cl, handle, socket))
				// TODO
			}
		}
		//TODO:
		return false;
	}

	private boolean checkMessage(Header header, Message message) {
		return header.messageSize == message.getSizeInBytes();
	}

	private ClientHandle getClient(Header header, Socket socket) {
		ClientHandle result = null;
		synchronized (clients) {
			for (Client client : clients) {
				if (header.uid == client.uid) {
					if (result != null) {
						logger.error("ERROR! check error. There are two clients with the same uid");
					}
					result = client.handle;
				}
			}
		}
		return result;
	}

	private Socket getSocket(ClientHandle handle) {
		Socket socket = null;
		synchronized (clients) {
			for (Client client : clients) {
				if (client.handle.equals(handle)) {
					if (socket != null) {
						logger.error("ERROR! check error. There are two clients with the same handle");
					}
					socket = client.socket;
				}
			}
		}
		return socket;
	}

	@Override
	public ClientHandle[] getClients() {
		Client[] currentClients = clients.toArray(new Client[0]);
		ClientHandle[] result = new ClientHandle[currentClients.length];
		for (int i = 0; i < currentClients.length; i++) {
			result[i] = currentClients[i].handle;
		}
		return result;
	}

	@Override
	public boolean send(ClientHandle client, Message message) {
		logger.info("Send message to client:" + client + " message:" + message);
		try {
			Socket socket = getSocket(client);
			OutputStream out = socket.getOutputStream();
			Header header = createHeader(client.getUid(), message.getSizeInBytes());
			mapper.writeValue(out, header);
			mapper.writeValue(out, message);
			AckStatus status = mapper.readValue(socket.getInputStream(), AckStatus.class);
			if (status != AckStatus.OK) {
				logger.error("Some error hapends during sending to client:" + client);
			}
			return status == AckStatus.OK;
		} catch (IOException e) {
			logger.error("Sending message error: ", e);
		}
		return false;
	}

	private void closeSocket(Socket socket) {
		try {
			socket.close();
		} catch (IOException e) {
			logger.error("Closing socket error: ", e);
		}
	}

	@Override
	public void run() {
		logger.info("Message server opened");
		while (!isStopped) {
			try {
				Socket socket = serverSocket.accept();
				InputStream in = socket.getInputStream();
				Header header = mapper.readValue(in, Header.class);
				// register client connection
				if (registerOrUpdateClient(socket, header)) {
					Message message = mapper.readValue(in, Message.class);
					boolean checkMessage = checkMessage(header, message);
					AckStatus status;
					if (checkMessage) {
						status = getClient(header, socket).process(message);
					} else {
						logger.info("Message rejected because of sum check with header:" + header);
						status = AckStatus.DENIED_SUM_CHECK;
					}
					mapper.writeValue(socket.getOutputStream(), status);
				} else {
					logger.error("Unknown client or header: " + header + " socket:" + socket.getInetAddress().toString() + ":" + socket.getPort());
					closeSocket(socket);
				}
			} catch (IOException e) {
				logger.error("Error during socket binding", e);
			}
		}
		logger.info("Message server stopped");
	}

	@Override
	public void stop() {
		isStopped = true;
	}
}
