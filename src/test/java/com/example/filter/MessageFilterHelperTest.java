package com.example.filter;

import com.example.filter.utils.MessageFilterHelper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MessageFilterHelperTest {

    final String filterString = "{\"transportOrder.receiver.code\":[\"MU807747\",\"MU807732\"], \"billingGroup\":[\"BAA\",\"BAM\",\"GVA\"]}";

    @Test
    void testFilterWithMockData(){

        String serializedValidMessage = MockPojoHelper.serializeToJsonString(MockPojoHelper.makeValidMessage());
        String serializedInvalidMessage = MockPojoHelper.serializeToJsonString(MockPojoHelper.makeInvalidMessage());

        assertTrue(MessageFilterHelper.isMessageValid(serializedValidMessage, filterString));
        assertFalse(MessageFilterHelper.isMessageValid(serializedInvalidMessage, filterString));
    }

}
