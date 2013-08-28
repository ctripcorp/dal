package com.ctrip.sysdev.das.commons;

public interface ByteBufferWrapper {
	public ByteBufferWrapper get(int capacity);
	
	

	public void writeByte(int index, byte data);

	public void writeByte(byte data);

	public byte readByte();

	public void writeInt(int data);

	public void writeBytes(byte[] data);

	public int readableBytes();

	public int readInt();

	public void readBytes(byte[] dst);

	public int readerIndex();

	public void setReaderIndex(int readerIndex);
}