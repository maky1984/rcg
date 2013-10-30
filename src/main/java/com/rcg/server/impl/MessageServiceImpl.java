package com.rcg.server.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.soap.MessageFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.rcg.server.api.ClientHandle;
import com.rcg.server.api.ClientHandle.AckStatus;
import com.rcg.server.api.ClientHandleManager;
import com.rcg.server.api.Message;
import com.rcg.server.api.MessageService;

public class MessageServiceImpl implements MessageService, Runnable {

	private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

	private List<Client> clients = Collections.synchronizedList(new ArrayList<Client>());

	private ClientHandleManager clientHandleManager;

	public int port;
	private ServerSocket serverSocket;
	private ObjectMapper mapper;
	private volatile boolean isStopped;

	private static Header createHeader(long uid, long size) {
		Header header = new Header();
		header.setUid(uid);
		header.setMessageSize(size);
		return header;
	}

	private static class Client {
		long uid;
		ClientHandle handle;
		Socket socket;
	}

	private static Client createClient(ClientHandle handle, Header header, Socket socket) {
		Client client = new Client();
		client.uid = header == null ? handle.getUid() : header.getUid();
		client.handle = handle;
		client.socket = socket;
		return client;
	}

	@Override
	public void init(ClientHandleManager clientHandleManager) {
		this.clientHandleManager = clientHandleManager;
	}

	@Override
	public void open(int port) {
		if (clientHandleManager == null) {
			logger.error("ERROR! Client handle manager not provided");
		} else {
			logger.info("Openning message server with PORT=" + port);
			try {
				serverSocket = new ServerSocket(port);
				JsonFactory jsonFactory = new JsonFactory();
				jsonFactory.disable(Feature.AUTO_CLOSE_SOURCE);
				mapper = new ObjectMapper(jsonFactory);
				mapper.enableDefaultTyping();
				mapper.enable(SerializationFeature.FLUSH_AFTER_WRITE_VALUE);
				mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
			} catch (IOException e) {
				logger.error("Cant create socket");
			}
			new Thread(this, "MessageService-server").start();
		}
	}

	private boolean sequrityCheck(Header header, Client client) {
		// TODO:
		return true;
	}

	private boolean innerCheckClient(Socket socket, Header header) {
		boolean found = false;
		synchronized (clients) {
			for (Client client : clients) {
				if (client.uid == header.getUid()) {
					if (found) {
						logger.error("ERROR! check error: there are several clients with the same uid, client:" + client);
						if (sequrityCheck(header, client)) {
							closeSocket(client.socket);
							client.socket = socket;
						} else {
							logger.error("ERROR! Sequrity check fail for client = " + client + " and header = " + header);
							closeSocket(client.socket);
							clients.remove(client);
						}
					} else {
						if (sequrityCheck(header, client)) {
							found = true;
						} else {
							logger.error("ERROR! Sequrity check fail for new client and header = " + header);
							closeSocket(socket);
						}
					}
				}
			}
			if (!found) {
				ClientHandle handle = clientHandleManager.getClientHandle(header.getUid());
				if (handle == null) {
					logger.error("ERROR! Client not registered");
				} else {
					clients.add(createClient(handle, header, socket));
					found = true;
				}
			}
		}
		return found;
	}

	private boolean checkMessage(Header header, Message message) {
		return header.getMessageSize() == message.getSizeInBytes();
	}

	private ClientHandle getClient(Header header, Socket socket) {
		ClientHandle result = null;
		synchronized (clients) {
			for (Client client : clients) {
				if (header.getUid() == client.uid) {
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
	public boolean send(ClientHandle handle, Message message) {
		logger.info("Send message to client:" + handle + " message:" + message);
		try {
			Socket socket = getSocket(handle);
			if (socket == null) {
				// Create socket
				socket = new Socket(handle.getHost(), handle.getPort());
				logger.info("Connected to the client:" + handle);
				clients.add(createClient(handle, null, socket));
			}
			OutputStream out = socket.getOutputStream();
			Header header = createHeader(handle.getUid(), message.getSizeInBytes());
			mapper.writeValue(out, header);
			System.out.println(mapper.writeValueAsString(header));
			mapper.writeValue(out, message);
			System.out.println(mapper.writeValueAsString(message));
			AckStatus status = mapper.readValue(socket.getInputStream(), AckStatus.class);
			if (status != AckStatus.OK) {
				logger.error("Some error hapends during sending to client:" + handle);
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
				if (innerCheckClient(socket, header)) {
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
			} catch (SocketException e) {
				logger.info("Socket exception:", e);
			} catch (IOException e) {
				logger.error("Error during socket binding or closed", e);
			}
		}
		logger.info("Message server stopped");
	}

	@Override
	public void stop() {
		isStopped = true;
		for (Client client : clients) {
			closeSocket(client.socket);
		}
		try {
			serverSocket.close();
		} catch (IOException e) {
			logger.error("Closing server socket error ", e);
		}
	}
}
