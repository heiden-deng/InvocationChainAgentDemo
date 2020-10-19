package com.heiden.service;

import com.heiden.config.Config;
import org.apache.kafka.clients.producer.*;

import java.util.Properties;

public class KafkaService {
    private static String brokerAddress = null;

    private static volatile Producer<String, String> producer = null;

    public static void setBorkerAddress(String address){
        brokerAddress = address;
    }

    public static void initProducer(String ipAddress){
        Properties props = new Properties();
        props.put("bootstrap.servers", ipAddress);
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        //配置partitionner选择策略，可选配置
        //props.put("partitioner.class", "cn.ljh.kafka.kafka_helloworld.SimplePartitioner2");
        producer = new KafkaProducer<>(props);
    }

    public static void sendMessage(String msg){
        if (producer == null){
            synchronized (KafkaService.class){
                if (producer == null){
                    initProducer(brokerAddress);
                }
            }
        }
        ProducerRecord<String,String> data = new ProducerRecord<String,String>(Config.KafkaTopicName,Config.KafkaTopicKeyName,msg);
        producer.send(data);
        producer.flush();
    }
}
