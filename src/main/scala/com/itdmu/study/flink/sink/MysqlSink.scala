package com.itdmu.study.flink.sink

import java.sql.{Connection, DriverManager, PreparedStatement}

import com.itdmu.study.pojo.Student
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.sink.{RichSinkFunction, SinkFunction}

class MysqlSink extends RichSinkFunction[Student]{

  var conn:Connection = _
  var pst:PreparedStatement = _

  //生命周期管理，在Sink初始化的时候调用
  override def open(parameters: Configuration): Unit = {
    conn = DriverManager.getConnection("jdbc:mysql://192.168.156.111/test?characterEncoding=utf-8","root","root")
    pst = conn.prepareStatement("insert into t_student (code,name,gender,age,phone) values (?,?,?,?,?)")
  }

  //把Student写入表
  override def invoke(value: Student, context: SinkFunction.Context[_]): Unit = {
    pst.setString(1,value.code)
    pst.setString(2,value.name)
    pst.setString(3,value.gender)
    pst.setInt(4,value.age)
    pst.setString(5,value.phone)
    pst.executeUpdate()
  }

  override def close(): Unit = {
    pst.close()
    conn.close()
  }


}
