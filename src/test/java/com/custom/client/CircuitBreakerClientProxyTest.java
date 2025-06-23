package com.custom.client;

import com.custom.client.circuitbreaker.CircuitBreaker;
import com.custom.client.circuitbreaker.CircuitBreakerProvider;
import com.custom.client.fallback.DefaultFallbackStrategy;
import com.custom.client.fallback.FallbackStrategy;
import com.custom.client.service.ServiceCenter;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CircuitBreakerClientProxy测试类")
public class CircuitBreakerClientProxyTest {

    private CircuitBreakerClientProxy proxy;
    
    @Mock
    private RPCClient mockRpcClient;
    
    @Mock
    private ServiceCenter mockServiceCenter;
    
    @Mock
    private CircuitBreakerProvider mockCircuitBreakerProvider;
    
    @Mock
    private CircuitBreaker mockCircuitBreaker;
    
    @Mock
    private FallbackStrategy<String> mockFallbackStrategy;

    @BeforeEach
    void setUp() {
        proxy = new CircuitBreakerClientProxy();
        // 使用反射设置mock对象
        try {
            java.lang.reflect.Field rpcClientField = CircuitBreakerClientProxy.class.getDeclaredField("rpcClient");
            rpcClientField.setAccessible(true);
            rpcClientField.set(proxy, mockRpcClient);

            java.lang.reflect.Field serviceCenterField = CircuitBreakerClientProxy.class.getDeclaredField("serviceCenter");
            serviceCenterField.setAccessible(true);
            serviceCenterField.set(proxy, mockServiceCenter);

            java.lang.reflect.Field circuitBreakerProviderField = CircuitBreakerClientProxy.class.getDeclaredField("circuitBreakerProvider");
            circuitBreakerProviderField.setAccessible(true);
            circuitBreakerProviderField.set(proxy, mockCircuitBreakerProvider);

            java.lang.reflect.Field fallbackStrategyField = CircuitBreakerClientProxy.class.getDeclaredField("fallbackStrategy");
            fallbackStrategyField.setAccessible(true);
            fallbackStrategyField.set(proxy, mockFallbackStrategy);
        } catch (Exception e) {
            fail("设置mock对象失败", e);
        }
    }

    @Test
    @DisplayName("测试正常请求流程")
    void testNormalRequest() throws Throwable {
        // Arrange
        Method method = TestInterface.class.getMethod("testMethod");
        Object[] args = new Object[]{};
        RPCRequest expectedRequest = RPCRequest.builder()
                .interfaceName(TestInterface.class.getName())
                .methodName("testMethod")
                .params(args)
                .paramsType(method.getParameterTypes())
                .build();
        RPCResponse expectedResponse = RPCResponse.success("success");

        when(mockCircuitBreakerProvider.getCircuitBreaker("testMethod")).thenReturn(mockCircuitBreaker);
        when(mockCircuitBreaker.allowRequest()).thenReturn(true);
        when(mockServiceCenter.checkRetry(TestInterface.class.getName())).thenReturn(false);
        when(mockRpcClient.sendRequest(expectedRequest)).thenReturn(expectedResponse);

        // Act
        Object result = proxy.invoke(null, method, args);

        // Assert
        assertEquals("success", result);
        verify(mockCircuitBreaker).recordSuccess();
    }

    @Test
    @DisplayName("测试熔断器打开时的降级处理")
    void testCircuitBreakerOpen() throws Throwable {
        // Arrange
        Method method = TestInterface.class.getMethod("testMethod");
        Object[] args = new Object[]{};
        String fallbackResult = "fallback result";

        when(mockCircuitBreakerProvider.getCircuitBreaker("testMethod")).thenReturn(mockCircuitBreaker);
        when(mockCircuitBreaker.allowRequest()).thenReturn(false);
        when(mockFallbackStrategy.fallback(any(Exception.class))).thenReturn(fallbackResult);

        // Act
        Object result = proxy.invoke(null, method, args);

        // Assert
        assertEquals(fallbackResult, result);
        verify(mockRpcClient, never()).sendRequest(any());
    }

    @Test
    @DisplayName("测试请求失败时的熔断器记录")
    void testRequestFailure() throws Throwable {
        // Arrange
        Method method = TestInterface.class.getMethod("testMethod");
        Object[] args = new Object[]{};
        String fallbackResult = "fallback result";

        when(mockCircuitBreakerProvider.getCircuitBreaker("testMethod")).thenReturn(mockCircuitBreaker);
        when(mockCircuitBreaker.allowRequest()).thenReturn(true);
        when(mockServiceCenter.checkRetry(TestInterface.class.getName())).thenReturn(false);
        when(mockRpcClient.sendRequest(any())).thenThrow(new RuntimeException("Test exception"));
        when(mockFallbackStrategy.fallback(any(Exception.class))).thenReturn(fallbackResult);

        // Act
        Object result = proxy.invoke(null, method, args);

        // Assert
        assertEquals(fallbackResult, result);
        verify(mockCircuitBreaker).recordFailure();
    }

    @Test
    @DisplayName("测试代理对象创建")
    void testGetProxy() {
        // Act
        TestInterface proxy = this.proxy.getProxy(TestInterface.class);

        // Assert
        assertNotNull(proxy);
        assertTrue(proxy instanceof TestInterface);
    }

    // 测试接口
    private interface TestInterface {
        String testMethod();
    }
} 