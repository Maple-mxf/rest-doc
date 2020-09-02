package restdoc.remoting;

import restdoc.remoting.exception.RemotingCommandException;

public interface CommandCustomHeader {

    void checkFields() throws RemotingCommandException;
}
