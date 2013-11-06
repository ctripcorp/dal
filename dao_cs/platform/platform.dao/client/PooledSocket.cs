using System;
using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Text;
using platform.dao.exception;
using platform.dao.log;

namespace platform.dao.client
{
    /// <summary>
    /// 封装一个连接到指定DAS Worker的socket ，以DAS的私有协议方式
    /// 进行通信，并作容错处理
    /// </summary>
    public class PooledSocket : IDisposable
    {
        private static ILogAdapter logger = LogFactory.GetLogger(typeof(PooledSocket).Name);

        private static readonly int READ_BUFFER = 8192;

        private SocketPool socketPool;
        private Socket socket;
        private NetworkStream stream;
        public readonly DateTime Created;

        public PooledSocket(SocketPool socketPool, IPEndPoint endPoint, int sendReceiveTimeout, int connectTimeout)
        {
            this.socketPool = socketPool;
            Created = DateTime.Now;

            //Set up the socket.
            socket = new Socket(endPoint.AddressFamily, SocketType.Stream, ProtocolType.Tcp);
            socket.SetSocketOption(SocketOptionLevel.Socket, SocketOptionName.SendTimeout, sendReceiveTimeout);
            socket.SetSocketOption(SocketOptionLevel.Socket, SocketOptionName.ReceiveTimeout, sendReceiveTimeout);
            socket.ReceiveTimeout = sendReceiveTimeout;
            socket.SendTimeout = sendReceiveTimeout;

            //Do not use Nagle's Algorithm
            socket.NoDelay = true;

            //Establish connection asynchronously to enable connect timeout.
            IAsyncResult result = socket.BeginConnect(endPoint, null, null);
            bool success = result.AsyncWaitHandle.WaitOne(connectTimeout, false);
            if (!success)
            {
                try { socket.Close(); }
                catch { }
                throw new SocketException();
            }
            socket.EndConnect(result);

            //Wraps two layers of streams around the socket for communication.
            stream = new NetworkStream(socket, false);
        }

        public void Dispose()
        {
            socketPool.Return(this);
        }

        /// <summary>
        /// This method closes the underlying stream and socket.
        /// </summary>
        public void Close()
        {
            if (stream != null)
            {
                try { stream.Close(); }
                catch (Exception e) { logger.Error(string.Format("Error closing stream: {0}", e.StackTrace)); }
                stream = null;
            }
            if (socket != null)
            {
                try { socket.Shutdown(SocketShutdown.Both); }
                catch (Exception e) { logger.Error(string.Format("Error shutting down socket: {0}", e.StackTrace)); }
                try { socket.Close(); }
                catch (Exception e) { logger.Error(string.Format("Error closing socket: {0}", e.StackTrace)); }
                socket = null;
            }
        }

        /// <summary>
        /// Checks if the underlying socket and stream is connected and available.
        /// </summary>
        public bool IsAlive
        {
            get { return socket != null && socket.Connected && stream.CanRead; }
        }

        /// <summary>
        /// Resets this PooledSocket by making sure the incoming buffer of the socket is empty.
        /// If there was any leftover data, this method return true.
        /// </summary>
        public bool Reset()
        {
            if (socket.Available > 0)
            {
                byte[] b = new byte[socket.Available];
                stream.Read(b, 0, b.Length);
                return true;
            }
            return false;
        }

        /// <summary>
        /// 向网络写入4个字节的数据
        /// </summary>
        /// <param name="value"></param>
        public void WriteInt(int value)
        {
            stream.WriteByte((byte)(value >> 24));
            stream.WriteByte((byte)(value >> 16));
            stream.WriteByte((byte)(value >> 8));
            stream.WriteByte((byte)(value >> 0));
        }

        /// <summary>
        /// 向网络写入2个字节的数据
        /// </summary>
        /// <param name="value"></param>
        public void WriteShort(short value)
        {
            stream.WriteByte((byte)(value >> 8));
            stream.WriteByte((byte)(value >> 0));
        }

        /// <summary>
        /// 向网络写入二进制数据
        /// </summary>
        /// <param name="value"></param>
        public void WriteBytes(byte[] value)
        {
            stream.Write(value, 0, value.Length);
        }

        /// <summary>
        /// 从网络中读取4个字节
        /// </summary>
        /// <returns></returns>
        public int ReadInt()
        {
            return (stream.ReadByte() << 24) |
                        (stream.ReadByte() << 16) |
                        (stream.ReadByte() << 8) |
                        (stream.ReadByte() << 0);
        }

        /// <summary>
        /// 从网络中读取2个字节
        /// </summary>
        /// <returns></returns>
        public short ReadShort()
        {
            return (short)((stream.ReadByte() << 8) |
                        (stream.ReadByte() << 0));
        }

        /// <summary>
        /// 从网络中读取固定数量的数据，填充为byte array
        /// </summary>
        /// <param name="length"></param>
        /// <returns></returns>
        public byte[] ReadBytes(int length)
        {
            byte[] header = new byte[length];

            int taskidLen = stream.Read(header, 0, header.Length);

            return header;

        }

    }
}
