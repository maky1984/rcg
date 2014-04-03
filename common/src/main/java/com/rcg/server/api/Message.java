package com.rcg.server.api;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Message {

	private static final Logger logger = LoggerFactory.getLogger(Message.class);

	private byte[] data;

	private String className;

	public Message() {
	}
	
	public Message(Object obj) {
		fillMessage(obj);
	}
	
	@JsonIgnore
	public void fillMessage(Object obj) {
		try {
			className = obj.getClass().getName();
			ObjectMapper mapper = new ObjectMapper();
			data = mapper.writeValueAsBytes(obj);
			logger.info("fillMessage:" + mapper.writeValueAsString(obj));
		} catch (JsonProcessingException e) {
			logger.error("ERROR. Cant fill message", e);
		}
	}

	@JsonIgnore
	public boolean containsClass(Class<?> cls) {
		return className.equals(cls.getName());
	}
	
	@JsonIgnore
	public <T> T unpackMessage() {
		try {
			@SuppressWarnings("unchecked")
			Class<T> cls = (Class<T>) Class.forName(getClassName());
			ObjectMapper mapper = new ObjectMapper();
			T t = mapper.readValue(data, cls);
			logger.info("unpackMessage:" + t);
			return t;
		} catch (IOException e) {
			logger.error("ERROR. Cant read message", e);
		} catch (ClassNotFoundException e) {
			logger.error("ERROR. Cant find class", e);
		}
		return null;
	}

	@JsonIgnore
	public long getSizeInBytes() {
		return data.length;
	}

	public byte[] getData() {
		return data;
	}

	public String getClassName() {
		return className;
	}
	
	@Override
	public String toString() {
		return "Message with class:" + className;
	}

}
