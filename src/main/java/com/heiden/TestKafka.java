package com.heiden;

import com.heiden.service.KafkaService;

public class TestKafka {
    public static void main(String[] args){
        KafkaService.setBorkerAddress("127.0.0.1:9092");
        KafkaService.sendMessage("test kafka msg");
        System.out.println("test hello");
    }
}
