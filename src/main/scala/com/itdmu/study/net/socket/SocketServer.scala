package com.itdmu.study.net.socket

import java.io.{BufferedInputStream, BufferedOutputStream, DataInputStream, DataOutputStream}
import java.net.{ServerSocket, Socket}

/**
 * 网络编程 Socket端口服务器
 */
object SocketServer {
  def main(args: Array[String]): Unit = {

    //端口号
    val port = 7777
    //在端口上创建一个服务器嵌套字
    val serverSocket = new ServerSocket(port)
    //监听来自客户端的连接
    val socket: Socket = serverSocket.accept()

    val dataInputStream: DataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream))
    val dataOutputStream: DataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream))

    do {
      val length: Double = dataInputStream.readDouble()
      println("服务器端收到的边长数据为：" + length)
      val result: Double = length * length
      dataOutputStream.writeDouble(result)
      dataOutputStream.flush()
    } while (dataInputStream.readInt() != 0)

    socket.close()
    serverSocket.close()
  }

}
