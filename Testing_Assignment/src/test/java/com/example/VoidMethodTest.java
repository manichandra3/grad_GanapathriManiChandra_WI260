package com.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoidMethodTest {

    @Mock
    private MyInterface myInterface;

    @Test
    void testProcessData_VoidMethod_Success() {
        doNothing().when(myInterface).processData(anyString());

        myInterface.processData("test data");

        verify(myInterface).processData("test data");
    }

    @Test
    void testProcessData_VoidMethod_ThrowsException() {
        doThrow(new IllegalArgumentException("Invalid data"))
            .when(myInterface).processData("");

        try {
            myInterface.processData("");
            fail("Should have thrown exception");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid data", e.getMessage());
        }

        verify(myInterface).processData("");
    }

    @Test
    void testProcessData_VerifyNoInteraction() {
        verifyNoInteractions(myInterface);
    }
}
