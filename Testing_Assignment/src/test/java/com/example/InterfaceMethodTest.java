package com.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InterfaceMethodTest {

    @Mock
    private MyInterface myInterface;

    @Test
    void testFetchData_WithoutImplementation() {
        when(myInterface.fetchData("user123")).thenReturn("John Doe");
        when(myInterface.fetchData("invalid")).thenReturn(null);

        String result1 = myInterface.fetchData("user123");
        String result2 = myInterface.fetchData("invalid");

        assertEquals("John Doe", result1);
        assertNull(result2);
        
        verify(myInterface).fetchData("user123");
        verify(myInterface).fetchData("invalid");
    }

    @Test
    void testFetchData_WithException() {
        when(myInterface.fetchData("error"))
            .thenThrow(new RuntimeException("Connection failed"));

        assertThrows(RuntimeException.class, 
            () -> myInterface.fetchData("error"));
    }
}
