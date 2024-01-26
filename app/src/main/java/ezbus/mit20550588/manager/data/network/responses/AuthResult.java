package ezbus.mit20550588.manager.data.network.responses;

import ezbus.mit20550588.manager.data.model.UserModel;

public class AuthResult {
    public enum Status {SUCCESS, ERROR}
    private final Status status;
    private final UserModel user;
    private final String errorMessage;

    public AuthResult(Status status, UserModel user, String errorMessage) {
        this.status = status;
        this.user = user;
        this.errorMessage = errorMessage;
    }

    public Status getStatus() {
        return status;
    }

    public UserModel getUser() {
        return user;
    }

    public String getErrorMessage() {
        return errorMessage;
    }


}

