package com.library.app.common.model;

import com.library.app.common.exception.FieldNotValidException;

public class StandardsOperationResults {

    public StandardsOperationResults() {
    }

    public static OperationResult getOperationResultDuplicated(final ResourceMessage resourceMessage, final String fieldsNames) {
        return OperationResult.error(resourceMessage.getKeyOfResourceExistent(),
                resourceMessage.getMessageOfResourceExistent(fieldsNames));
    }

    public static OperationResult getOperationResultInvalidField(final ResourceMessage resourceMessage, final FieldNotValidException fieldNotValid) {
        return OperationResult.error(resourceMessage.getKeyOfInvalidField(fieldNotValid.field()),
                fieldNotValid.getMessage());
    }

    public static OperationResult getOperationResultResultNotFound(final ResourceMessage resourceMessage) {
        return OperationResult.error(resourceMessage.getKeyOfResourceNotFound(),
                resourceMessage.getMessageOfResourceNotFound());
    }
}
