package com.custom.register;

import com.custom.client.cache.ServiceCache;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;

/**
 * @author Gemaxis
 * @date 2024/07/12 15:59
 **/
public class WatchZK {
    private CuratorFramework client;
    ServiceCache serviceCache;

    public WatchZK(CuratorFramework client, ServiceCache serviceCache) {
        this.client = client;
        this.serviceCache = serviceCache;
    }

    public void watchToUpdate(String path) {
        CuratorCache curatorCache = CuratorCache.build(client, "/");
        curatorCache.listenable().addListener(new CuratorCacheListener() {
            // 第一个参数：事件类型（枚举）
            // 第二个参数：节点更新前的状态、数据
            // 第三个参数：节点更新后的状态、数据
            @Override
            public void event(Type type, ChildData childData, ChildData childData1) {
                // 创建节点时：节点刚被创建，不存在 更新前节点 ，所以第二个参数为 null
                // 删除节点时：节点被删除，不存在 更新后节点 ，所以第三个参数为 null
                // 节点创建时没有赋予值 create /curator/app1 只创建节点，在这种情况下，更新前节点的 data 为 null，获取不到更新前节点的数据
                switch (type.name()) {
                    case "NODE_CREATED": // 监听器第一次执行时节点存在也会触发次事件
                        //获取更新的节点的路径
                        String path = new String(childData1.getPath());
                        String[] pathList = path.split("/");
                        if (pathList.length <= 2) {
                            break;
                        } else {
                            String serviceName = pathList[1];
                            String address = pathList[2];
                            //将新注册的服务加入到本地缓存中
                            serviceCache.addServiceToCache(serviceName, address);
                        }
                        break;
                    case "NODE_CHANGED": // 节点更新
                        if (childData.getData() != null) {
                            System.out.println("修改前的数据: " + new String(childData.getData()));
                        } else {
                            System.out.println("节点第一次赋值!");
                        }
                        System.out.println("修改后的数据: " + new String(childData1.getData()));
                        break;
                    case "NODE_DELETED": // 节点删除
                        String pathDelete = new String(childData.getPath());
                        String[] pathListDelete = pathDelete.split("/");
                        if (pathListDelete.length <= 2) {
                            break;
                        } else {
                            String serviceName = pathListDelete[1];
                            String address = pathListDelete[2];
                            serviceCache.deleteServiceFromCache(serviceName, address);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        //开启监听
        curatorCache.start();

    }
}
