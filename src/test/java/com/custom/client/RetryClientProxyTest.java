package com.custom.client;

import com.custom.client.retry.GuavaRetry;
import com.custom.client.service.ServiceCenter;
import com.custom.common.message.RPCRequest;
import com.custom.common.message.RPCResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RetryClientProxy测试类")
public class RetryClientProxyTest {

    @Mock
    private RPCClient mockRpcClient;

    @Mock
    private ServiceCenter mockServiceCenter;

    @Mock
    private GuavaRetry mockGuavaRetry;

    private RetryClientProxy retryClientProxy;

    @BeforeEach
    void setUp() {
        retryClientProxy = new RetryClientProxy();
        // 使用反射设置mock对象
        try {
            java.lang.reflect.Field rpcClientField = RetryClientProxy.class.getDeclaredField("rpcClient");
            rpcClientField.setAccessible(true);
            rpcClientField.set(retryClientProxy, mockRpcClient);

            java.lang.reflect.Field serviceCenterField = RetryClientProxy.class.getDeclaredField("serviceCenter");
            serviceCenterField.setAccessible(true);
            serviceCenterField.set(retryClientProxy, mockServiceCenter);
        } catch (Exception e) {
            fail("设置mock对象失败", e);
        }
    }

    @Test
    @DisplayName("测试正常请求流程（无重试）")
    void testNormalRequestWithoutRetry() throws Throwable {
        // Arrange
        Method method = TestInterface.class.getMethod("testMethod", String.class);
        Object[] args = new Object[]{"test"};
        String expectedResponse = "success";

        when(mockServiceCenter.checkRetry(TestInterface.class.getName())).thenReturn(false);
        RPCResponse mockResponse = RPCResponse.success(expectedResponse);
        when(mockRpcClient.sendRequest(any(RPCRequest.class))).thenReturn(mockResponse);

        // Act
        Object result = retryClientProxy.invoke(null, method, args);

        // Assert
        assertEquals(expectedResponse, result);
        verify(mockRpcClient).sendRequest(any(RPCRequest.class));
        verify(mockServiceCenter).checkRetry(TestInterface.class.getName());
    }

    @Test
    @DisplayName("测试正常请求流程（有重试）")
    void testNormalRequestWithRetry() throws Throwable {
        // Arrange
        Method method = TestInterface.class.getMethod("testMethod", String.class);
        Object[] args = new Object[]{"test"};
        String expectedResponse = "success";

        when(mockServiceCenter.checkRetry(TestInterface.class.getName())).thenReturn(true);
        RPCResponse mockResponse = RPCResponse.success(expectedResponse);

        try (MockedStatic<GuavaRetry> guavaRetryMockedStatic = mockStatic(GuavaRetry.class)) {
            guavaRetryMockedStatic.when(() -> new GuavaRetry()).thenReturn(mockGuavaRetry);
            when(mockGuavaRetry.sendServiceWithRetry(any(RPCRequest.class), any(RPCClient.class)))
                    .thenReturn(mockResponse);

            // Act
            Object result = retryClientProxy.invoke(null, method, args);

            // Assert
            assertEquals(expectedResponse, result);
            verify(mockServiceCenter).checkRetry(TestInterface.class.getName());
            verify(mockGuavaRetry).sendServiceWithRetry(any(RPCRequest.class), any(RPCClient.class));
        }
    }

    @Test
    @DisplayName("测试请求失败（无重试）")
    void testRequestFailureWithoutRetry() throws Throwable {
        // Arrange
        Method method = TestInterface.class.getMethod("testMethod", String.class);
        Object[] args = new Object[]{"test"};

        when(mockServiceCenter.checkRetry(TestInterface.class.getName())).thenReturn(false);
        when(mockRpcClient.sendRequest(any(RPCRequest.class))).thenReturn(RPCResponse.fail());

        // Act
        Object result = retryClientProxy.invoke(null, method, args);

        // Assert
        assertNull(result);
        verify(mockRpcClient).sendRequest(any(RPCRequest.class));
        verify(mockServiceCenter).checkRetry(TestInterface.class.getName());
    }

    @Test
    @DisplayName("测试请求失败（有重试）")
    void testRequestFailureWithRetry() throws Throwable {
        // Arrange
        Method method = TestInterface.class.getMethod("testMethod", String.class);
        Object[] args = new Object[]{"test"};

        when(mockServiceCenter.checkRetry(TestInterface.class.getName())).thenReturn(true);

        try (MockedStatic<GuavaRetry> guavaRetryMockedStatic = mockStatic(GuavaRetry.class)) {
            guavaRetryMockedStatic.when(() -> new GuavaRetry()).thenReturn(mockGuavaRetry);
            when(mockGuavaRetry.sendServiceWithRetry(any(RPCRequest.class), any(RPCClient.class)))
                    .thenReturn(RPCResponse.fail());

            // Act
            Object result = retryClientProxy.invoke(null, method, args);

            // Assert
            assertNull(result);
            verify(mockServiceCenter).checkRetry(TestInterface.class.getName());
            verify(mockGuavaRetry).sendServiceWithRetry(any(RPCRequest.class), any(RPCClient.class));
        }
    }

    @Test
    @DisplayName("测试代理对象创建")
    void testGetProxy() {
        // Act
        TestInterface proxy = retryClientProxy.getProxy(TestInterface.class);

        // Assert
        assertNotNull(proxy);
        assertTrue(proxy instanceof TestInterface);
    }

    @Test
    @DisplayName("测试请求参数构建")
    void testRequestBuilding() throws Throwable {
        // Arrange
        Method method = TestInterface.class.getMethod("testMethod", String.class);
        Object[] args = new Object[]{"test"};
        String expectedResponse = "success";

        when(mockServiceCenter.checkRetry(TestInterface.class.getName())).thenReturn(false);
        RPCResponse mockResponse = RPCResponse.success(expectedResponse);
        when(mockRpcClient.sendRequest(any(RPCRequest.class))).thenAnswer(invocation -> {
            RPCRequest request = invocation.getArgument(0);
            assertEquals(TestInterface.class.getName(), request.getInterfaceName());
            assertEquals("testMethod", request.getMethodName());
            assertArrayEquals(args, request.getParams());
            assertArrayEquals(method.getParameterTypes(), request.getParamsType());
            return mockResponse;
        });

        // Act
        Object result = retryClientProxy.invoke(null, method, args);

        // Assert
        assertEquals(expectedResponse, result);
        verify(mockRpcClient).sendRequest(any(RPCRequest.class));
    }

    // 测试接口
    private interface TestInterface {
        String testMethod(String param);
    }
} 