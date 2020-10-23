package restdoc.client.dubbo.test.service;

import org.apache.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;
import restdoc.client.dubbo.test.model.DuplicatePojo;

@Service
@Component
public class DuplicateServiceImpl implements DuplicateService {

    @Override
    public DuplicatePojo transfter(String type) {

        DuplicatePojo duplicatePojo = new DuplicatePojo();
        duplicatePojo.setAttribute("Maple");

        return duplicatePojo;
    }
}
