using System.Net.Sockets;
using System.Text;
using System;
using System.IO;
using com.ctrip.platform.dao.type;
using com.ctrip.platform.dao.param;

namespace com.ctrip.platform
{
    public class DALClient
    {

        private Socket sock;
        private NetworkStream networkStream;

        /// <summary>
        /// Initialize the connection to the server
        /// </summary>
        /// <returns>Initialize success or not</returns>
        public bool Initialize()
        {
            sock = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            try
            {
                sock.Connect(Consts.serverAddr, Consts.serverPort);
                networkStream = new NetworkStream(sock);
            }
            catch
            {
                return false;
            }

            return true;
        }

        public void WriteParameter(IParameter param)
        {
            if (null == networkStream)
            {
                throw new Exception("Socket Connect Error, Please Try Again!");
            }

            //var serializer = new ParameterSerializer();

            //var payload = serializer.PackSingleObject(param);

            //networkStream.Write(payload, 0, payload.Length);

            networkStream.Close(1);
        }

        /// <summary>
        /// Write Request to DAL Service and return the taskid
        /// </summary>
        /// <param name="payload">the payload to write</param>
        /// <returns>current task id</returns>
        public string WriteRequest(byte[] payload)
        {
            if (null == networkStream)
            {
                throw new Exception("Socket Connect Error, Please Try Again!");
            }

            int wholeLength = 4 + 16 + 2 + 2 + 2 + 4 + Consts.credential.Length + payload.Length;

            MemoryStream ms = new MemoryStream();

            WriteWholeLen(ms, wholeLength);

            var taskid = WriteTaskid(ms);

            WriteDBId(ms, Consts.databaseId);

            WriteCredential(ms, Consts.credential);

            WriteProtocolVersion(ms, Consts.protocolVersion);

            WritePayload(ms, payload);

            //networkStream.Write(ms.GetBuffer(), 0, (int)ms.Length);

            networkStream.Close(1);

            return taskid;
        }

        /// <summary>
        /// Step 1: Write the whole packet length to the socket
        /// </summary>
        /// <param name="wholeLength"></param>
        private void WriteWholeLen(MemoryStream stream, int wholeLength)
        {
            //stream.WriteByte((byte)(wholeLength >> 24));
            //stream.WriteByte((byte)(wholeLength >> 16));
            //stream.WriteByte((byte)(wholeLength >> 8));
            //stream.WriteByte((byte)(wholeLength >> 0));

            networkStream.WriteByte((byte)(wholeLength >> 24));
            networkStream.WriteByte((byte)(wholeLength >> 16));
            networkStream.WriteByte((byte)(wholeLength >> 8));
            networkStream.WriteByte((byte)(wholeLength >> 0));
        }

        /// <summary>
        /// Step 2: Write an uuid the the socket and return it
        /// </summary>
        /// <returns>the task id in form of uuid</returns>
        private string WriteTaskid(MemoryStream stream)
        {
            var taskid = System.Guid.NewGuid();
            var uuidBytes = taskid.ToByteArray();

            //var taskid = System.Guid.NewGuid().ToString();
            //var uuidBytes = Encoding.Default.GetBytes(taskid);

            //stream.Write(uuidBytes, 0, uuidBytes.Length);

            networkStream.Write(uuidBytes, 0, uuidBytes.Length);

            return taskid.ToString();
        }

        /// <summary>
        /// Step 3: Write the db id to the socket, represent the database to connect to
        /// </summary>
        /// <param name="dbId">the identity of the database</param>
        private void WriteDBId(MemoryStream stream, int dbId)
        {
            //stream.WriteByte((byte)(dbId >> 8));
            //stream.WriteByte((byte)(dbId >> 0));

            networkStream.WriteByte((byte)(dbId >> 8));
            networkStream.WriteByte((byte)(dbId >> 0));
        }

        /// <summary>
        /// Step 4: Write the credential information to the socket
        /// </summary>
        /// <param name="credential">credential information, user=???;password=???</param>
        private void WriteCredential(MemoryStream stream, string credential)
        {
            int credentialLength = credential.Length;

            //stream.WriteByte((byte)(credentialLength >> 8));
            //stream.WriteByte((byte)(credentialLength >> 0));

            networkStream.WriteByte((byte)(credentialLength >> 8));
            networkStream.WriteByte((byte)(credentialLength >> 0));

            var credentialBytes = Encoding.Default.GetBytes(credential);

            //stream.Write(credentialBytes, 0, credentialBytes.Length);

            networkStream.Write(credentialBytes, 0, credentialBytes.Length);
        }

        /// <summary>
        /// Step 5: Write the version of the protocol to the socket, server will handle it accordinglly
        /// </summary>
        /// <param name="version">the protocol version</param>
        private void WriteProtocolVersion(MemoryStream stream, int version)
        {
            //stream.WriteByte((byte)(version >> 8));
            //stream.WriteByte((byte)(version >> 0));

            networkStream.WriteByte((byte)(version >> 8));
            networkStream.WriteByte((byte)(version >> 0));
        }

        /// <summary>
        /// Step 6: Write the payload to the socket, different version of protocol will be with different
        /// type of format, the server will handle it accordinglly
        /// </summary>
        /// <param name="payload">the payload in byte array</param>
        private void WritePayload(MemoryStream stream, byte[] payload)
        {
            var payloadLength = payload.Length;

            //stream.WriteByte((byte)(payloadLength >> 24));
            //stream.WriteByte((byte)(payloadLength >> 16));
            //stream.WriteByte((byte)(payloadLength >> 8));
            //stream.WriteByte((byte)(payloadLength >> 0));

            //stream.Write(payload, 0, payloadLength);

            networkStream.WriteByte((byte)(payloadLength >> 24));
            networkStream.WriteByte((byte)(payloadLength >> 16));
            networkStream.WriteByte((byte)(payloadLength >> 8));
            networkStream.WriteByte((byte)(payloadLength >> 0));

            networkStream.Write(payload, 0, payloadLength);
        }

        /// <summary>
        /// Free the used resource
        /// </summary>
        /// <returns></returns>
        public bool Close()
        {
            if (null != sock)
            {
                sock.Close();
            }
            if (null != networkStream)
            {
                networkStream.Close();
            }
            return true;
        }
    }
}
