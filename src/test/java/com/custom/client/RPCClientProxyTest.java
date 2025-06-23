package com.custom.client;

import com.custom.common.message.RPCRequest;
import com.custom.common.message.RPCResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RPCClientProxy测试类")
public class RPCClientProxyTest {

    @Mock
    private RPCClient mockRpcClient;

    private RPCClientProxy rpcClientProxy;

    @BeforeEach
    void setUp() {
        rpcClientProxy = new RPCClientProxy(mockRpcClient);
    }

    @Test
    @DisplayName("测试正常请求流程")
    void testNormalRequest() throws Throwable {
        // Arrange
        Method method = TestInterface.class.getMethod("testMethod", String.class);
        Object[] args = new Object[]{"test"};
        String expectedResponse = "success";

        RPCResponse mockResponse = RPCResponse.success(expectedResponse);
        when(mockRpcClient.sendRequest(any(RPCRequest.class))).thenReturn(mockResponse);

        // Act
        Object result = rpcClientProxy.invoke(null, method, args);

        // Assert
        assertEquals(expectedResponse, result);
        verify(mockRpcClient).sendRequest(any(RPCRequest.class));
    }

    @Test
    @DisplayName("测试空参数请求")
    void testNullArgsRequest() throws Throwable {
        // Arrange
        Method method = TestInterface.class.getMethod("testMethod", String.class);
        Object[] args = null;
        String expectedResponse = "success";

        RPCResponse mockResponse = RPCResponse.success(expectedResponse);
        when(mockRpcClient.sendRequest(any(RPCRequest.class))).thenReturn(mockResponse);

        // Act
        Object result = rpcClientProxy.invoke(null, method, args);

        // Assert
        assertEquals(expectedResponse, result);
        verify(mockRpcClient).sendRequest(any(RPCRequest.class));
    }

    @Test
    @DisplayName("测试请求失败")
    void testRequestFailure() throws Throwable {
        // Arrange
        Method method = TestInterface.class.getMethod("testMethod", String.class);
        Object[] args = new Object[]{"test"};

        when(mockRpcClient.sendRequest(any(RPCRequest.class))).thenReturn(RPCResponse.fail());

        // Act
        Object result = rpcClientProxy.invoke(null, method, args);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("测试代理对象创建")
    void testGetProxy() {
        // Act
        TestInterface proxy = rpcClientProxy.getProxy(TestInterface.class);

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
        Object result = rpcClientProxy.invoke(null, method, args);

        // Assert
        assertEquals(expectedResponse, result);
        verify(mockRpcClient).sendRequest(any(RPCRequest.class));
    }

    // 测试接口
    private interface TestInterface {
        String testMethod(String param);
    }
} 