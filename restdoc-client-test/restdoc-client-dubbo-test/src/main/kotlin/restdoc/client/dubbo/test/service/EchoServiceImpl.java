package restdoc.client.dubbo.test.service;

/*@Service
@Component*/
public class EchoServiceImpl implements EchoService {

    @Override
    public void echo(String a) {
        System.err.println("restdoc.client.dubbo.test.service.EchoService.echo hello world");
    }
}
