package restdoc.client.dubbo.test;

import org.apache.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;

@Service
@Component
public class EchoServiceImpl implements EchoService {

    @Override
    public void echo() {
        System.err.println("hello world");
    }
}
