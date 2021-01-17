package restdoc.remoting.common;

/**
 * The RequestCode is constant
 */
public interface RequestCode {

    /**
     * acknowledge version
     */
    int AcknowledgeVersion = 1;

    /**
     *
     */
    int CollectClientInfo = 2;

    /**
     *
     */
    int CollectApi = 4;

    /**
     *
     */
    int InvokeApi = 5;
}
