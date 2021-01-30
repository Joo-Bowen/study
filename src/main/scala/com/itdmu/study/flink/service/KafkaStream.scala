package com.itdmu.study.flink.service

import java.util.Properties

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer


object KafkaStream {
  def main(args: Array[String]): Unit = {


    //val params: ParameterTool = ParameterTool.fromArgs(args)
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    //env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)

    val properties = new Properties()
    properties.setProperty("bootstrap.servers", "node-01:9092,node-02:9092,node-03:9092")
    properties.setProperty("group.id", "group8888")
    properties.setProperty("key.deserializer", classOf[StringDeserializer].getName)
    properties.setProperty("value.deserializer", classOf[StringDeserializer].getName)
    properties.setProperty("auto.offset.reset", "latest")

    import org.apache.flink.api.scala._

    //Kafka中的普通数据
    val input: DataStream[String] = env.addSource(new FlinkKafkaConsumer010[String]("flink", new SimpleStringSchema(), properties))




    //自定义反序列化schema
    //val input: DataStream[ConsumerRecord[String, String]] = env.addSource(new FlinkKafkaConsumer010[ConsumerRecord[String, String]]("flink", new MyCustomerDeserializationSchema, properties))


/*
    val value: DataStream[(String, Int)] = input.map(_.split(",", -1)(0))
      .map(x => {
        if (x.contains('1')) {
          ("big", 1)
        } else {
          ("small", 1)
        }
      }).keyBy(0)
      .timeWindow(Time.seconds(60),Time.seconds(30))
      //.window(TumblingEventTimeWindows.of(Time.seconds(10)))
      .sum(1)

    import scala.collection.JavaConversions._

    /*val set: util.HashSet[InetSocketAddress] = new util.HashSet[InetSocketAddress]()
    
    set.add(new InetSocketAddress("node-01",6379))
    set.add(new InetSocketAddress("node-02",6379))
    set.add(new InetSocketAddress("node-03",6379))

    val config: FlinkJedisClusterConfig = new FlinkJedisClusterConfig.Builder().setNodes(set).build()
    */
    value.addSink(new RedisSink[(String, Int)](

      new FlinkJedisPoolConfig.Builder().setDatabase(0).setHost("node-01").setPort(6379).build(),

      
      //new FlinkJedisClusterConfig()

      new RedisMapper[(String, Int)] {
        override def getCommandDescription: RedisCommandDescription = {
          new RedisCommandDescription(RedisCommand.HSET,"window")
        }

        override def getKeyFromData(data: (String, Int)): String = data._1

        override def getValueFromData(data: (String, Int)): String = data._2.toString
      }
    ))*/

    //sink
    input.print()

    /*//创建一个文件滚动规则
    val rolling: DefaultRollingPolicy[ConsumerRecord, String] = DefaultRollingPolicy.builder().withInactivityInterval(200000L)
      .withRolloverInterval(6000).build()

    StreamingFileSink
      .forRowFormat(new Path("hdfs://node-01:9000/flink_sink/"), new SimpleStringEncoder[ConsumerRecord]("UTF-8"))
      .withBucketCheckInterval(1000L)
      .withRollingPolicy(rolling)*/


    //数据Sink到mysql
    //input.addSink(new MysqlSink)

    //redis Sink
    /*
        input.addSink(new RedisSink[Student](
          new FlinkJedisPoolConfig.Builder().setDatabase(1).setHost("192.168.156.111").setPort(6379).build(),
          new RedisMapper[Student] {
            override def getCommandDescription: RedisCommandDescription = {
              new RedisCommandDescription(RedisCommand.HSET,"H_CODE")
            }

            override def getKeyFromData(data: Student): String = {
              data.code
            }

            override def getValueFromData(data: Student): String = {
              data.name
            }
          }
        ))
    */

    env.execute()
  }
}


