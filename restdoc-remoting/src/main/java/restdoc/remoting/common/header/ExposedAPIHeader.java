package restdoc.remoting.common.header;

import restdoc.remoting.CommandCustomHeader;
import restdoc.remoting.common.ApplicationType;
import restdoc.remoting.exception.RemotingCommandException;

public class ExposedAPIHeader implements CommandCustomHeader {

    private ApplicationType applicationType;

    public ApplicationType getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(ApplicationType applicationType) {
        this.applicationType = applicationType;
    }

    @Override
    public void checkFields() throws RemotingCommandException {
    }
}
