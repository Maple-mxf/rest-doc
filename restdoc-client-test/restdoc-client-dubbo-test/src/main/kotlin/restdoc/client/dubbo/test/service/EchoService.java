package restdoc.client.dubbo.test.service;

public interface EchoService {

    void echo(String a);

    default void echo1(String a){

        System.err.println(String.format("restdoc.client.dubbo.test.service.EchoService.echo1 %s",a));
    }
    default   void echo2(String a){
        System.err.println(String.format("restdoc.client.dubbo.test.service.EchoService.echo2 %s","Hello Kotlin"));
    }
    default    void echo3(String a){
        System.err.println(String.format("restdoc.client.dubbo.test.service.EchoService.echo3 %s","Hello Java"));
    }
}
