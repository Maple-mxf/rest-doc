package smartdoc.dashboard.cli;

import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class HostTest {

    @Test
    public void testGetHostName() throws UnknownHostException {
        InetAddress inetAddress = InetAddress.getLocalHost();
        System.err.println(inetAddress.getHostAddress());
        System.out.println("Host Name:- " + inetAddress.getHostName());
    }
}
