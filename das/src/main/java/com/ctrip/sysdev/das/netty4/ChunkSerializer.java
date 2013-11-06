package com.ctrip.sysdev.das.netty4;

/**
 * @deprecated
 * @author jhhe
 *
 */
public class ChunkSerializer {
//	public void write(ChannelHandlerContext ctx, List<List<StatementParameter>> obj, boolean isLast)throws SerDeException {
//		byte[] bytes = serialize(obj);
//		ByteBuf bf = ctx.alloc().buffer();
//		
//		bf.writeInt(bytes.length + 1);
//		bf.writeBoolean(isLast);
//		bf.writeBytes(bytes);
//		ctx.write(bytes);
//	}
//
//	private byte[] serialize(List<List<StatementParameter>> obj)
//			throws SerDeException {
//		ByteArrayOutputStream out = new ByteArrayOutputStream();
//		MessagePack msgpack = new MessagePack();
//		Packer packer = msgpack.createPacker(out);
//		try {
//			packer.writeArrayBegin(obj.size());
//			for (List<StatementParameter> row : obj) {
//				packer.writeArrayBegin(row.size());
//				for (StatementParameter col : row) {
//					col.pack(packer);
//				}
//				packer.writeArrayEnd();
//			}
//			packer.writeArrayEnd();
//		} catch (IOException e) {
//			e.printStackTrace();
//			throw new SerDeException("ResponseSerDe doSerialize exception ", e);
//		}
//		return out.toByteArray();
//	}
}
