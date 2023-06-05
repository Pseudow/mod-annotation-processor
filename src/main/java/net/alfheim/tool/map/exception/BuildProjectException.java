package net.alfheim.tool.map.exception;

public class BuildProjectException extends Exception {
    public BuildProjectException(String errorMessage) {
        super(errorMessage);
    }

    public BuildProjectException(String errorMessage, Exception exception) {
        super(errorMessage, exception);
    }
}
