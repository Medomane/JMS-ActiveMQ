package org.sid.kafkaspring;

import com.fasterxml.jackson.databind.json.JsonMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class MainService {
    @KafkaListener(topics = MyGlobal.Topic,groupId = MyGlobal.GroupId)
    public void onMessage(ConsumerRecord<String,String> message) throws Exception {
        System.out.println("**********************");
        if(message.key().equals(PageEvent.class.getName())){
            PageEvent pg = new JsonMapper().readValue(message.value(), PageEvent.class);
            System.out.println(message.key()+" ------ "+pg.toString());
        }
    }
}
