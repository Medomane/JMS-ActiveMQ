package org.sid.kafkaspring;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Random;

@RestController
public class MainController {

    final KafkaTemplate<String,PageEvent> kafkaTemplate ;

    public MainController(KafkaTemplate<String, PageEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @GetMapping("/send/{page}")
    public String send(@PathVariable String page){
        kafkaTemplate.send(MyGlobal.Topic,PageEvent.class.getName(),new PageEvent(page,new Date(),new Random().nextInt(1000)));
        return "Message sent ... ";
    }
}
