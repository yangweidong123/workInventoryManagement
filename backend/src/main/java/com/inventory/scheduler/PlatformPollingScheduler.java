package com.inventory.scheduler;

import com.inventory.adapter.PlatformAdapter;
import com.inventory.entity.PlatformConfig;
import com.inventory.service.PlatformConfigService;
import com.inventory.service.OrderProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class PlatformPollingScheduler {

    @Autowired
    private PlatformConfigService platformConfigService;

    @Autowired
    private Map<String, PlatformAdapter> adapterMap;

    @Autowired
    private OrderProcessService orderProcessService;

    @Scheduled(fixedDelayString = "${platform.polling.interval:300000}")
    public void pollOrders() {
        List<PlatformConfig> configs = platformConfigService.list();
        for (PlatformConfig config : configs) {
            if (config.getEnabled() != 1) {
                continue;
            }

            PlatformAdapter adapter = adapterMap.get(config.getPlatform() + "Adapter");
            if (adapter == null) {
                continue;
            }

            try {
                Long lastSyncTime = config.getLastSyncTime() != null 
                    ? config.getLastSyncTime().toLocalDate().toEpochDay() 
                    : null;

                var orders = adapter.fetchOrders(lastSyncTime);
                for (var order : orders) {
                    try {
                        orderProcessService.processOrder(order);
                    } catch (Exception e) {
                        // 单个订单处理失败不影响其他订单
                    }
                }

                platformConfigService.updateHeartbeat(config.getPlatform());
            } catch (Exception e) {
                // 轮询失败处理
            }
        }
    }
}
