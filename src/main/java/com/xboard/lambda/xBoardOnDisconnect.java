package com.xboard.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2WebSocketEvent;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.util.HashMap;

public class xBoardOnDisconnect implements RequestHandler<APIGatewayV2WebSocketEvent, APIGatewayProxyResponseEvent> {

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayV2WebSocketEvent input, Context context) {
        DynamoDbClient client = DynamoDbClient.create();
        String connectionId = input.getRequestContext().getConnectionId();

        HashMap<String, AttributeValue> itemValues = new HashMap<>();
        itemValues.put("connectionId", AttributeValue.builder().s(connectionId).build());

        DeleteItemRequest deleteReq = DeleteItemRequest.builder()
                .tableName(System.getenv("TABLE_NAME"))
                .key(itemValues)
                .build();

        try {
            client.deleteItem(deleteReq);
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setIsBase64Encoded(false);
        response.setStatusCode(200);
        response.setBody( "Disconnected.");
        return response;
    }
}
