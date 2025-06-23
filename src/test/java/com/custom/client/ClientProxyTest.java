package com.custom.client;

import com.custom.common.message.RPCRequest;
import com.custom.common.message.RPCResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClientProxy测试类")
public class ClientProxyTest {

    private static final String TEST_HOST = "localhost";
    private static final int TEST_PORT = 8080;
    private ClientProxy clientProxy;

    @BeforeEach
    void setUp() {
        clientProxy = new ClientProxy(TEST_HOST, TEST_PORT);
    }

    @Test
    @DisplayName("测试正常请求流程")
    void testNormalRequest() throws Throwable {
        // Arrange
        Method method = TestInterface.class.getMethod("testMethod", String.class);
        Object[] args = new Object[]{"test"};
        String expectedResponse = "success";

        try (MockedStatic<IOClient> ioClientMockedStatic = mockStatic(IOClient.class)) {
            RPCResponse mockResponse = RPCResponse.success(expectedResponse);
            ioClientMockedStatic.when(() -> IOClient.sendRequest(eq(TEST_HOST), eq(TEST_PORT), any(RPCRequest.class)))
                    .thenReturn(mockResponse);

            // Act
            Object result = clientProxy.invoke(null, method, args);

            // Assert
            assertEquals(expectedResponse, result);
            ioClientMockedStatic.verify(() -> IOClient.sendRequest(eq(TEST_HOST), eq(TEST_PORT), any(RPCRequest.class)));
        }
    }

    @Test
    @DisplayName("测试请求异常处理")
    void testRequestException() throws Throwable {
        // Arrange
        Method method = TestInterface.class.getMethod("testMethod", String.class);
        Object[] args = new Object[]{"test"};

        try (MockedStatic<IOClient> ioClientMockedStatic = mockStatic(IOClient.class)) {
            ioClientMockedStatic.when(() -> IOClient.sendRequest(eq(TEST_HOST), eq(TEST_PORT), any(RPCRequest.class)))
                    .thenThrow(new RuntimeException("Test exception"));

            // Act & Assert
            assertThrows(RuntimeException.class, () -> clientProxy.invoke(null, method, args));
        }
    }

    @Test
    @DisplayName("测试空参数请求")
    void testNullArgsRequest() throws Throwable {
        // Arrange
        Method method = TestInterface.class.getMethod("testMethod", String.class);
        Object[] args = null;
        String expectedResponse = "success";

        try (MockedStatic<IOClient> ioClientMockedStatic = mockStatic(IOClient.class)) {
            RPCResponse mockResponse = RPCResponse.success(expectedResponse);
            ioClientMockedStatic.when(() -> IOClient.sendRequest(eq(TEST_HOST), eq(TEST_PORT), any(RPCRequest.class)))
                    .thenReturn(mockResponse);

            // Act
            Object result = clientProxy.invoke(null, method, args);

            // Assert
            assertEquals(expectedResponse, result);
            ioClientMockedStatic.verify(() -> IOClient.sendRequest(eq(TEST_HOST), eq(TEST_PORT), any(RPCRequest.class)));
        }
    }

    @Test
    @DisplayName("测试代理对象创建")
    void testGetProxy() {
        // Act
        TestInterface proxy = clientProxy.getProxy(TestInterface.class);

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

        try (MockedStatic<IOClient> ioClientMockedStatic = mockStatic(IOClient.class)) {
            RPCResponse mockResponse = RPCResponse.success("success");
            ioClientMockedStatic.when(() -> IOClient.sendRequest(eq(TEST_HOST), eq(TEST_PORT), any(RPCRequest.class)))
                    .thenAnswer(invocation -> {
                        RPCRequest request = invocation.getArgument(2);
                        assertEquals(TestInterface.class.getName(), request.getInterfaceName());
                        assertEquals("testMethod", request.getMethodName());
                        assertArrayEquals(args, request.getParams());
                        assertArrayEquals(method.getParameterTypes(), request.getParamsType());
                        return mockResponse;
                    });

            // Act
            clientProxy.invoke(null, method, args);

            // Assert
            ioClientMockedStatic.verify(() -> IOClient.sendRequest(eq(TEST_HOST), eq(TEST_PORT), any(RPCRequest.class)));
        }
    }

    // 测试接口
    private interface TestInterface {
        String testMethod(String param);
    }
} 