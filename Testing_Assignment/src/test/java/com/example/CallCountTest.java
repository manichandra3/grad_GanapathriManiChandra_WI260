package com.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CallCountTest {

    @Mock
    private MyInterface myInterface;

    @Test
    void testLogEvent_CalledExactly3Times_Pass() {
        doNothing().when(myInterface).logEvent(anyString());

        myInterface.logEvent("Event 1");
        myInterface.logEvent("Event 2");
        myInterface.logEvent("Event 3");

        verify(myInterface, times(3)).logEvent(anyString());
    }

    @Test
    void testLogEvent_CalledWrongNumberOfTimes_Fail() {
        doNothing().when(myInterface).logEvent(anyString());

        myInterface.logEvent("Event 1");
        myInterface.logEvent("Event 2");

        verify(myInterface, times(2)).logEvent(anyString());
    }

    @Test
    void testLogEvent_NeverCalled() {
        verify(myInterface, never()).logEvent(anyString());
    }

    @Test
    void testLogEvent_AtLeastAndAtMost() {
        doNothing().when(myInterface).logEvent(anyString());

        myInterface.logEvent("Event 1");
        myInterface.logEvent("Event 2");

        verify(myInterface, atLeastOnce()).logEvent(anyString());
        verify(myInterface, atMost(3)).logEvent(anyString());
    }
}
