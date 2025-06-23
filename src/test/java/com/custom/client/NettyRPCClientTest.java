package com.custom.client;

import com.custom.client.service.ServiceCenter;
import com.custom.common.message.RPCRequest;
import com.custom.common.message.RPCResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.AttributeKey;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NettyRPCClient测试类")
public class NettyRPCClientTest {

    private static final String TEST_HOST = "localhost";
    private static final int TEST_PORT = 8080;
    private static final String TEST_INTERFACE = "com.custom.TestInterface";

    @Mock
    private ServiceCenter mockServiceCenter;
    
    @Mock
    private Bootstrap mockBootstrap;
    
    @Mock
    private ChannelFuture mockChannelFuture;
    
    @Mock
    private Channel mockChannel;
    
    @Mock
    private EventLoopGroup mockEventLoopGroup;

    private NettyRPCClient nettyRPCClient;

    @BeforeEach
    void setUp() {
        nettyRPCClient = new NettyRPCClient(mockServiceCenter);
    }

    @AfterEach
    void tearDown() {
        // 清理资源
        if (mockEventLoopGroup != null) {
            mockEventLoopGroup.shutdownGracefully();
        }
    }

    @Test
    @DisplayName("测试正常请求流程")
    void testNormalRequest() throws Exception {
        // Arrange
        RPCRequest request = RPCRequest.builder()
                .interfaceName(TEST_INTERFACE)
                .methodName("testMethod")
                .build();
        RPCResponse expectedResponse = RPCResponse.success("success");

        InetSocketAddress address = new InetSocketAddress(TEST_HOST, TEST_PORT);
        when(mockServiceCenter.serverDiscovery(TEST_INTERFACE)).thenReturn(address);
        when(mockBootstrap.connect(TEST_HOST, TEST_PORT)).thenReturn(mockChannelFuture);
        when(mockChannelFuture.channel()).thenReturn(mockChannel);
        when(mockChannelFuture.sync()).thenReturn(mockChannelFuture);
        when(mockChannel.attr(AttributeKey.valueOf("RPCResponse"))).thenReturn(mockChannel.attr(AttributeKey.valueOf("RPCResponse")));
        when(mockChannel.attr(AttributeKey.valueOf("RPCResponse")).get()).thenReturn(expectedResponse);

        // 使用反射设置bootstrap
        try {
            java.lang.reflect.Field bootstrapField = NettyRPCClient.class.getDeclaredField("bootstrap");
            bootstrapField.setAccessible(true);
            bootstrapField.set(null, mockBootstrap);
        } catch (Exception e) {
            fail("设置bootstrap失败", e);
        }

        // Act
        RPCResponse response = nettyRPCClient.sendRequest(request);

        // Assert
        assertNotNull(response);
        assertEquals(expectedResponse.getData(), response.getData());
        verify(mockChannel).writeAndFlush(request);
        verify(mockChannelFuture).sync();
    }

    @Test
    @DisplayName("测试服务发现失败")
    void testServiceDiscoveryFailure() {
        // Arrange
        RPCRequest request = RPCRequest.builder()
                .interfaceName(TEST_INTERFACE)
                .methodName("testMethod")
                .build();

        when(mockServiceCenter.serverDiscovery(TEST_INTERFACE)).thenReturn(null);

        // Act
        RPCResponse response = nettyRPCClient.sendRequest(request);

        // Assert
        assertNull(response);
    }

    @Test
    @DisplayName("测试连接失败")
    void testConnectionFailure() throws Exception {
        // Arrange
        RPCRequest request = RPCRequest.builder()
                .interfaceName(TEST_INTERFACE)
                .methodName("testMethod")
                .build();

        InetSocketAddress address = new InetSocketAddress(TEST_HOST, TEST_PORT);
        when(mockServiceCenter.serverDiscovery(TEST_INTERFACE)).thenReturn(address);
        when(mockBootstrap.connect(TEST_HOST, TEST_PORT)).thenReturn(mockChannelFuture);
        when(mockChannelFuture.sync()).thenThrow(new InterruptedException("Connection failed"));

        // 使用反射设置bootstrap
        try {
            java.lang.reflect.Field bootstrapField = NettyRPCClient.class.getDeclaredField("bootstrap");
            bootstrapField.setAccessible(true);
            bootstrapField.set(null, mockBootstrap);
        } catch (Exception e) {
            fail("设置bootstrap失败", e);
        }

        // Act
        RPCResponse response = nettyRPCClient.sendRequest(request);

        // Assert
        assertNull(response);
    }

    @Test
    @DisplayName("测试空响应")
    void testNullResponse() throws Exception {
        // Arrange
        RPCRequest request = RPCRequest.builder()
                .interfaceName(TEST_INTERFACE)
                .methodName("testMethod")
                .build();

        InetSocketAddress address = new InetSocketAddress(TEST_HOST, TEST_PORT);
        when(mockServiceCenter.serverDiscovery(TEST_INTERFACE)).thenReturn(address);
        when(mockBootstrap.connect(TEST_HOST, TEST_PORT)).thenReturn(mockChannelFuture);
        when(mockChannelFuture.channel()).thenReturn(mockChannel);
        when(mockChannelFuture.sync()).thenReturn(mockChannelFuture);
        when(mockChannel.attr(AttributeKey.valueOf("RPCResponse"))).thenReturn(mockChannel.attr(AttributeKey.valueOf("RPCResponse")));
        when(mockChannel.attr(AttributeKey.valueOf("RPCResponse")).get()).thenReturn(null);

        // 使用反射设置bootstrap
        try {
            java.lang.reflect.Field bootstrapField = NettyRPCClient.class.getDeclaredField("bootstrap");
            bootstrapField.setAccessible(true);
            bootstrapField.set(null, mockBootstrap);
        } catch (Exception e) {
            fail("设置bootstrap失败", e);
        }

        // Act
        RPCResponse response = nettyRPCClient.sendRequest(request);

        // Assert
        assertNull(response);
    }

    @Test
    @DisplayName("测试构造函数")
    void testConstructors() {
        // 测试无参构造函数
        NettyRPCClient client1 = new NettyRPCClient();
        assertNotNull(client1);

        // 测试带host和port的构造函数
        NettyRPCClient client2 = new NettyRPCClient(TEST_HOST, TEST_PORT);
        assertNotNull(client2);

        // 测试带ServiceCenter的构造函数
        NettyRPCClient client3 = new NettyRPCClient(mockServiceCenter);
        assertNotNull(client3);
    }
} 