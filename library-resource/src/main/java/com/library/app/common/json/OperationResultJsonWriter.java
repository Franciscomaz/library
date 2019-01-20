package com.library.app.common.json;

import com.google.gson.JsonObject;
import com.library.app.common.model.OperationResult;

public final class OperationResultJsonWriter {

    public OperationResultJsonWriter() {
    }

    public static String toJson(OperationResult result) {
        return JsonWriter.writeToString(getJsonObject(result));
    }

    private static Object getJsonObject(OperationResult operationResult) {
        if(operationResult.isSuccess()) {
            return getJsonSuccess(operationResult);
        }
        return getJsonError(operationResult);
    }

    private static Object getJsonSuccess(OperationResult operationResult) {
        return operationResult.getEntity();
    }

    private static Object getJsonError(OperationResult operationResult) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("errorIdentification",  operationResult.getErrorIdentification());
        jsonObject.addProperty("errorDescription",  operationResult.getErrorDescription());

        return jsonObject;
    }
}
