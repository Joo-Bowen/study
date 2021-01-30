package com.itdmu.study.net.socket

import java.io.{BufferedInputStream, BufferedOutputStream, DataInputStream, DataOutputStream}
import java.net.Socket
import java.util.Scanner

import scala.util.control.Breaks._

/**
 * 网络编程 Socket端口客户端
 */
object SocketClient {
  def main(args: Array[String]): Unit = {
    val port = 7777
    val host = "127.0.0.1"

    val socket = new Socket(host, port)

    val dataInputStream: DataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream))
    val dataOutputStream: DataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream))

    val scanner = new Scanner(System.in)
    var flag = false

    while (!flag) {
      println("请输入正方形的边长：")
      val length: Double = scanner.nextDouble()

      dataOutputStream.writeDouble(length)
      dataOutputStream.flush()

      val area: Double = dataInputStream.readDouble()

      println("服务器返回的计算面积为:" + area)
      breakable({
        while (true) {
          println("继续计算？(Y/N)")
          val str: String = scanner.next()

          if (str.equalsIgnoreCase("N")) {
            dataOutputStream.writeInt(0)
            dataOutputStream.flush()
            flag = true
            break()
          } else if (str.equalsIgnoreCase("Y")) {
            dataOutputStream.writeInt(1)
            dataOutputStream.flush()
            break()
          }
        }
      })
    }
    socket.close()

  }
}
