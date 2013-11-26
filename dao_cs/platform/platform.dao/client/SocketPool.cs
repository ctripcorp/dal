using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Net;
using System.Threading;
using platform.dao.log;

namespace platform.dao.client
{
    /// <summary>
    /// 封装PooledSocket的连接池，可以获取或回收PooledSocket对象
    /// </summary>
    public class SocketPool
    {
        private static ILogAdapter logger = LogFactory.GetLogger(typeof(SocketPool).Name);

        internal delegate T UseSocket<T>(PooledSocket socket);
        internal delegate void UseSocket(PooledSocket socket);

        private int sendReceiveTimeout = 2000;
        private int connectTimeout = 2000;
        private uint maxPoolSize = 10;
        private uint minPoolSize = 5; 
        private TimeSpan socketRecycleAge = TimeSpan.FromMinutes(30);
        /// <summary>
        /// 如果主机不响应，将其标志为暂时宕机，并隔一段时间进行重试，每失败一次
        /// 时间加倍，如果主机开始响应，将时间重置为1
        /// </summary>
        private int deadEndPointSecondsUntilRetry = 1;
        /// <summary>
        /// 最大重试时间1分钟
        /// </summary>
        private const int maxDeadEndPointSecondsUntilRetry = 60 * 1; 

        private IPEndPoint endPoint;
        private Queue<PooledSocket> queue;

        private bool isEndPointDead = false;
        public bool IsEndPointDead { get { return isEndPointDead; } }


        private DateTime deadEndPointRetryTime;
        /// <summary>
        /// 主机宕机后，开始重试的时间点
        /// </summary>
        public DateTime DeadEndPointRetryTime { get { return deadEndPointRetryTime; } }

        internal int SendReceiveTimeout { get { return sendReceiveTimeout; } set { sendReceiveTimeout = value; } }
        internal int ConnectTimeout { get { return connectTimeout; } set { connectTimeout = value; } }
        internal uint MaxPoolSize { get { return maxPoolSize; } set { maxPoolSize = value; } }
        internal uint MinPoolSize { get { return minPoolSize; } set { minPoolSize = value; } }
        internal TimeSpan SocketRecycleAge { get { return socketRecycleAge; } set { socketRecycleAge = value; } }

        /// <summary>
        /// 以IP和端口号初始化一个SocketPool
        /// </summary>
        /// <param name="host"></param>
        /// <param name="port"></param>
        internal SocketPool(string host, int port)
        {
            IPAddress address;
            //See if it is valid ip address
            if (!IPAddress.TryParse(host, out address))
            {
                //See if we can resolve it as a hostname
                try
                {
                    address = Dns.GetHostEntry(host).AddressList[0];
                }
                catch (Exception e)
                {
                    throw new ArgumentException("无法解析主机: " + host, e);
                }
            }
            endPoint = new IPEndPoint(address, port);
            queue = new Queue<PooledSocket>();
        }



        /// <summary>
		/// Gets a socket from the pool.
		/// If there are no free sockets, a new one will be created. If something goes
		/// wrong while creating the new socket, this pool's endpoint will be marked as dead
		/// and all subsequent calls to this method will return null until the retry interval
		/// has passed.
		/// </summary>
		internal PooledSocket Acquire() {
			//Do we have free sockets in the pool?
			//if so - return the first working one.
			//if not - create a new one.
			lock(queue) {
				while(queue.Count > 0) {
					PooledSocket socket = queue.Dequeue();
					if(socket != null && socket.IsAlive) {
						return socket;
					}
				}
			}

			//If we know the endpoint is dead, check if it is time for a retry, otherwise return null.
			if (isEndPointDead) {
				if (DateTime.Now > deadEndPointRetryTime) {
					//Retry
					isEndPointDead = false;
				} else {
					//Still dead
					return null;
				}
			} 

			//Try to create a new socket. On failure, mark endpoint as dead and return null.
			try {
				PooledSocket socket = new PooledSocket(this, endPoint, SendReceiveTimeout, ConnectTimeout);
				//Reset retry timer on success.
				deadEndPointSecondsUntilRetry = 1;
				return socket;
			}
			catch (Exception e) {
				logger.Error(string.Format("Error connecting to: {0}", e));
				//Mark endpoint as dead
				isEndPointDead = true;
				//Retry in 2 minutes
				deadEndPointRetryTime = DateTime.Now.AddSeconds(deadEndPointSecondsUntilRetry);
				if (deadEndPointSecondsUntilRetry < maxDeadEndPointSecondsUntilRetry) {
					deadEndPointSecondsUntilRetry = deadEndPointSecondsUntilRetry * 2; //Double retry interval until next time
				}
				return null;
			}
		}

		/// <summary>
		/// Returns a socket to the pool.
		/// If the socket is dead, it will be destroyed.
		/// If there are more than MaxPoolSize sockets in the pool, it will be destroyed.
		/// If there are less than MinPoolSize sockets in the pool, it will always be put back.
		/// If there are something inbetween those values, the age of the socket is checked. 
		/// If it is older than the SocketRecycleAge, it is destroyed, otherwise it will be 
		/// put back in the pool.
		/// </summary>
		internal void Return(PooledSocket socket) {
			//If the socket is dead, destroy it.
			if (!socket.IsAlive) {
				socket.Close();
			} else {
				//Clean up socket
				if (socket.Reset()) {
				}

				//Check pool size.
				if (queue.Count >= MaxPoolSize) {
					//If the pool is full, destroy the socket.
					socket.Close();
				} else if (queue.Count > MinPoolSize && DateTime.Now - socket.Created > SocketRecycleAge) {
					//If we have more than the minimum amount of sockets, but less than the max, and the socket is older than the recycle age, we destroy it.
					socket.Close();
				} else {
					//Put the socket back in the pool.
					lock (queue) {
						queue.Enqueue(socket);
					}
				}
			}
		}

        /// <summary>
        /// 传入函数供执行
        /// </summary>
        /// <param name="defaultValue"></param>
        /// <param name="use"></param>
        /// <returns></returns>
        internal T Execute<T>(UseSocket<T> use, PooledSocket sock = null)
        {
            PooledSocket socket = sock;
            try
            {
                //Acquire a socket
                if(null == socket)
                    socket = Acquire();

                //Use the socket as a parameter to the delegate and return its result.
                if (socket != null)
                {
                    return use(socket);
                }
            }
            catch (Exception e)
            {
                logger.Error(string.Format("Error in Execute<T>: {0}", e.StackTrace));

                //Socket is probably broken
                if (socket != null)
                {
                    socket.Close();
                }
            }
            return default(T);
        }

        /// <summary>
        /// 传入函数供执行
        /// </summary>
        /// <param name="defaultValue"></param>
        /// <param name="use"></param>
        /// <returns></returns>
        internal void Execute(UseSocket use, PooledSocket sock = null)
        {
            PooledSocket socket = sock;
            try
            {
                //Acquire a socket
                if (null == socket)
                    socket = Acquire();

                //Use the socket as a parameter to the delegate and return its result.
                if (socket != null)
                {
                    use(socket);
                }
            }
            catch (Exception e)
            {
                logger.Error(string.Format("Error in Execute<T>: {0}", e.StackTrace));

                //Socket is probably broken
                if (socket != null)
                {
                    socket.Close();
                }
            }
        }

    }
}
