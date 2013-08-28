package com.ctrip.sysdev.das.commons;

import io.netty.buffer.ByteBuf;


public class Netty4ByteBufferWrapper implements ByteBufferWrapper {
	private ByteBuf buffer;

	public Netty4ByteBufferWrapper(ByteBuf in) {
		buffer = in;
	}

	public ByteBufferWrapper get(int capacity) {
		return this;
	}

	public byte readByte() {
		return buffer.readByte();
	}

	public void readBytes(byte[] dst) {
		buffer.readBytes(dst);
	}

	public int readInt() {
		return buffer.readInt();
	}

	public int readableBytes() {
		return buffer.readableBytes();
	}

	public int readerIndex() {
		return buffer.readerIndex();
	}

	public void setReaderIndex(int index) {
		buffer.setIndex(index, buffer.writerIndex());
	}

	public void writeByte(byte data) {
		buffer.writeByte(data);
	}

	public void writeBytes(byte[] data) {
		buffer.writeBytes(data);
	}

	public void writeInt(int data) {
		buffer.writeInt(data);
	}

	public ByteBuf getBuffer() {
		return buffer;
	}

	public void writeByte(int index, byte data) {
		buffer.writeByte(data);
	}
}