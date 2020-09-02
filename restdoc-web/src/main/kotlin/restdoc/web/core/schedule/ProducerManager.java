package restdoc.web.core.schedule;


import org.springframework.stereotype.Component;

/**
 * The ProducerManager provided write message to channel
 */
@Component
public class ProducerManager {

    private final ScheduleInstanceServerHandler scheduleInstanceServerHandler;

    public ProducerManager(ScheduleInstanceServerHandler scheduleInstanceServerHandler) {
        this.scheduleInstanceServerHandler = scheduleInstanceServerHandler;
    }

    
}
