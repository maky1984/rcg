package com.rcg.server.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.rcg.server.api.ClientHandle;
import com.rcg.server.api.Message;
import com.rcg.server.api.MessageHandler;
import com.rcg.server.api.MessageService;
import com.rcg.server.api.RCGServerException;

public class MessageServiceImpl implements MessageService, Runnable {

	private static final long WAIT_READ_TIME = 500;
	
	private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

	private List<Client> clients = Collections.synchronizedList(new ArrayList<Client>());

	private MessageHandler defaultMessageHandler;
	
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
		ClientHandle handle;
		Socket socket;
		JsonParser parser;
		List<Message> messages;
		@Override
		public String toString() {
			return "Uid:" + (handle == null ? "NULL" : handle.getUid()) + " socket:" + (socket == null ? "NULL" : socket.toString());
		}
	}

	private static Client createClient(ClientHandle handle, Socket socket) {
		logger.info("Creating new client handle=" + handle);
		Client client = new Client();
		client.handle = handle;
		client.socket = socket;
		client.messages = new ArrayList<Message>();
		return client;
	}
	
	private ClientHandle getNewClientHandle(long uid) {
		ClientHandle clientHandle = new ClientHandleImpl(uid);
		clientHandle.addMessageHandler(defaultMessageHandler);
		return clientHandle;
	}
	
	public void setDefaultMessageHandler(MessageHandler defaultMessageHandler) {
		this.defaultMessageHandler = defaultMessageHandler;
	}

	@Override
	public synchronized void open(int port) {
		this.port = port;
		logger.info("Openning message server with PORT=" + port);
		try {
			if (port != PORT_UNDEFINED) {
				serverSocket = new ServerSocket(port);
			}
			JsonFactory jsonFactory = new JsonFactory();
			jsonFactory.disable(Feature.AUTO_CLOSE_SOURCE);
			mapper = new ObjectMapper(jsonFactory);
			mapper.enableDefaultTyping();
			mapper.disable(SerializationFeature.FLUSH_AFTER_WRITE_VALUE);
			mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
			mapper.configure(JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM, false);
		} catch (IOException e) {
			logger.error("Cant create socket");
		}
		if (port != PORT_UNDEFINED) {
			new Thread(this, "MessageService-server").start();
		}
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while (!isStopped) {
					try {
						readMessage();
						Thread.sleep(WAIT_READ_TIME);
					} catch (InterruptedException e) {
						logger.error("ERRROR:", e);
					}
				}
			}
		}, "MessageService-read-write").start();
	}

	private boolean sequrityCheck(Header header, Client client) {
		// TODO:
		return true;
	}

	private boolean innerCheckClient(Socket socket, Header header) {
		synchronized (clients) {
			boolean found = false;
			for (Client client : clients) {
				if (client.handle != null && client.handle.getUid() == header.getUid()) {
					if (found) {
						logger.error("ERROR! check error: there are several clients with the same uid, client:" + client);
						if (sequrityCheck(header, client)) {
							closeClient(client);
							client.socket = socket;
						} else {
							logger.error("ERROR! Sequrity check fail for client = " + client + " and header = " + header);
							closeClient(client);
							clients.remove(client);
							return false;
						}
					} else {
						if (sequrityCheck(header, client)) {
							found = true;
						} else {
							logger.error("ERROR! Sequrity check fail for new client and header = " + header);
							closeClient(client);
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	private boolean checkMessage(Header header, Message message) {
		return header.getMessageSize() == message.getSizeInBytes();
	}

	private Client getClient(ClientHandle handle) {
		Client resClient = null;
		synchronized (clients) {
			for (Client client : clients) {
				if (client.handle == null) {
					logger.info("INFO. there is client with null handle client = " + client);
				} else if (client.handle.equals(handle)) {
					if (resClient != null) {
						logger.error("ERROR! check error. There are two clients with the same handle");
					}
					resClient = client;
				}
			}
		}
		return resClient;
	}

	private Client getClient(long uid) {
		Client resClient = null;
		synchronized (clients) {
			for (Client client : clients) {
				if (client.handle.getUid() == uid) {
					if (resClient != null) {
						logger.error("ERROR! check error. There are two clients with the same handle");
					}
					resClient = client;
				}
			}
		}
		return resClient;
	}

	@Override
	public synchronized ClientHandle[] getClients() {
		Client[] currentClients = clients.toArray(new Client[0]);
		ClientHandle[] result = new ClientHandle[currentClients.length];
		for (int i = 0; i < currentClients.length; i++) {
			result[i] = currentClients[i].handle;
		}
		return result;
	}

	public synchronized void send(ClientHandle handle, Message message) {
		getClient(handle).messages.add(message);
	}

	private synchronized boolean send(Client client, Message message) {
		logger.info("Send message to client:" + client.handle + " message:" + message);
		try {
			OutputStream out = client.socket.getOutputStream();
			Header header = createHeader(client.handle.getUid(), message.getSizeInBytes());
			mapper.writeValue(out, header);
			System.out.println(mapper.writeValueAsString(header));
			mapper.writeValue(out, message);
			out.flush();
			System.out.println(mapper.writeValueAsString(message));
			return true;
		} catch (IOException e) {
			logger.error("Sending message error: ", e);
		} catch (Throwable e) {
			logger.error("FATAL ERROR during send Unknown ex: ", e);
		}
		return false;
	}

	private void closeClient(Client client) {
		try {
			if (client.parser != null) {
				client.parser.close();
				client.parser = null;
			}
			closeSocket(client.socket);
			client.socket = null;
		} catch (IOException e) {
			logger.error("Closing parser error: ", e);
		}
	}

	private void closeSocket(Socket socket) {
		try {
			socket.close();
		} catch (IOException e) {
			logger.error("Closing socket error: ", e);
		}
	}

	private synchronized void readMessage(Client client, Socket socket) {
		try {
			if (client.parser == null) {
				InputStream in = client.socket.getInputStream();
				client.parser = mapper.getFactory().createParser(in);
			}
			Header header = client.parser.readValueAs(Header.class);
			if (client.handle == null) {
				logger.info("Adding new client handle for client: " + client);
				client.handle = getNewClientHandle(header.getUid());
			}
			System.out.println("ReadMessage:" + mapper.writeValueAsString(header));
			// register client connection
			if (innerCheckClient(client.socket, header)) {
				Message message = client.parser.readValueAs(Message.class);
				System.out.println("ReadMessage:" + mapper.writeValueAsString(message));
				boolean checkMessage = checkMessage(header, message);
				if (checkMessage) {
					client.handle.process(message);
				} else {
					logger.info("Message rejected because of sum check with header:" + header);
				}
			} else {
				logger.error("Unknown client or header: " + header + " socket:" + client.socket.getInetAddress().toString() + ":" + client.socket.getPort());
				closeClient(client);
			}
		} catch (IOException e) {
			logger.error("Error during socket read/write", e);
		} catch (Throwable e) {
			logger.error("FATAL ERROR during read Unknown ex: ", e);
		}
	}

	private synchronized void readMessage() {
		try {
			for(Client client : clients) {
				if (client.socket != null) {
					InputStream in = client.socket.getInputStream();
					while (in.available() > 0) {
						readMessage(client, null);
					}
				}
				for (Message message : client.messages) {
					send(client, message);
				}
				client.messages.clear();
			}
		} catch (IOException e) {
			logger.error("Error during readMessage", e);
		}
	}
	
	@Override
	public void run() {
		logger.info("Message server opened");
		while (!isStopped) {
			try {
				final Socket socket = serverSocket.accept();
				addClientHandle(null, socket);
			} catch (SocketException e) {
				logger.info("Socket exception:", e);
			} catch (IOException e) {
				logger.error("Error during socket binding or closed", e);
			}
		}
		logger.info("Message server stopped");
	}

	private synchronized void addClientHandle(ClientHandle clientHandle, Socket socket) {
		logger.info("Clients dump (" + clients.size() + "):");
		for (Client client : clients) {
			logger.info("Client: " + client);
		}
		clients.add(createClient(clientHandle, socket));
	}

	public synchronized void addClientHandle(ClientHandle clientHandle) throws RCGServerException {
		try {
			addClientHandle(clientHandle, new Socket(clientHandle.getHost(), clientHandle.getPort()));
		} catch (UnknownHostException e) {
			logger.error("addClientHandle error ", e);
			throw new RCGServerException(RCGServerException.CONNECTION_ERROR);
		} catch (IOException e) {
			logger.error("addClientHandle error ", e);
			throw new RCGServerException(RCGServerException.CONNECTION_ERROR);
		}
	}
 
	@Override
	public synchronized void stop() {
		isStopped = true;
		for (Client client : clients) {
			closeClient(client);
		}
		if (port != PORT_UNDEFINED) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				logger.error("Closing server socket error ", e);
			}
		}
	}
}
