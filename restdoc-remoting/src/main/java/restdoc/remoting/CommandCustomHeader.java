package restdoc.remoting;


import restdoc.remoting.exception.RemotingCommandException;

/**
 * CommandCustomHeader provided setup custom header
 */
public interface CommandCustomHeader {

    void checkFields() throws RemotingCommandException;
}
