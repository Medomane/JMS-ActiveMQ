package org.sid.kafkaspring;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageEvent {
    private String page;
    private Date date;
    private int duration;
    @Override
    public String toString(){
        return "Page : "+page+", Date : "+date+", Duration : "+duration;
    }
}
