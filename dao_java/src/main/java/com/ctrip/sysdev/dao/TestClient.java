package com.ctrip.sysdev.dao;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.UUID;

import com.ctrip.sysdev.dao.DasProto.CRUD;
import com.ctrip.sysdev.dao.DasProto.InnerResultSet;
import com.ctrip.sysdev.dao.DasProto.Request;
import com.ctrip.sysdev.dao.DasProto.RequestMessage;
import com.ctrip.sysdev.dao.DasProto.Row;
import com.ctrip.sysdev.dao.DasProto.StatementType;

public class TestClient {

	public static void main(String[] args) throws Exception {

		Socket requestSocket;
		DataOutputStream out;
		DataInputStream in;

		requestSocket = new Socket("172.16.155.151", 9000);

		// 2. get Input and Output streams
		out = new DataOutputStream(requestSocket.getOutputStream());

		in = new DataInputStream(requestSocket.getInputStream());
		// 3: Communicating with the server

		RequestMessage.Builder requestMessageBuilder = RequestMessage
				.newBuilder();

		requestMessageBuilder.setCrud(CRUD.GET).setFlags(1).setMaster(true)
				.setName("select r.HotelID, BasicRoomTypeID, PictureTypeID, r.PictureTitle,ri.pictitle as PictureETitle, PictureName, Rank, Source from HotelBasicRoomPicture r with(nolock) left join HotelRoomPicture_Intl as ri with(nolock) on r.pictureid = ri.picid  join hotel as h with(nolock) on r.HotelID=h.Hotel and r.DisplayStatus='T'  Join resource re with(nolock) on re.resource = h.hotel join city c (nolock) on c.city = re.city and c.city in (select city from city (nolock) where Country = 1)  order by rank").setStateType(StatementType.SQL);

		Request.Builder requestBuilder = Request.newBuilder();

		requestBuilder.setCred("30303").setDb("HtlProductdb")
				.setId(UUID.randomUUID().toString())
				.setMsg(requestMessageBuilder.build());

		byte[] payload = requestBuilder.build().toByteArray();

		out.writeInt(payload.length + 2);

		out.writeShort(1);

		out.write(payload, 0, payload.length);

		out.flush();
		
		int headerLength = in.readInt();
		
		int proversion = in.readShort();
		
		byte[] leftData = new byte[headerLength - 2];
		
		in.read(leftData, 0, headerLength - 2);
		
		boolean readFinish = false;
		
		int count = 0;
		
		while(!readFinish){
			
			int bodyLength = in.readInt();
			
			byte[] bodyData = new byte[bodyLength];
			
			in.read(bodyData, 0, bodyLength);
			
			InnerResultSet result = InnerResultSet.parseFrom(bodyData);
			readFinish = result.getLast();
			for(Row row : result.getRowsList()){
				count++;
			}
		}
		
		requestSocket.close();
		
		System.out.println(count);

	}
}
