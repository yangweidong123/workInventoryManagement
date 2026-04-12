package com.inventory.controller;

import com.inventory.adapter.PlatformAdapter;
import com.inventory.dto.Result;
import com.inventory.dto.StandardOrder;
import com.inventory.service.OrderProcessService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/webhook")
public class WebhookController {

    @Autowired
    private Map<String, PlatformAdapter> adapterMap;

    @Autowired
    private OrderProcessService orderProcessService;

    @PostMapping("/{platform}")
    public Result<Void> receiveWebhook(@PathVariable String platform, HttpServletRequest request) {
        PlatformAdapter adapter = adapterMap.get(platform + "Adapter");
        if (adapter == null) {
            return Result.error(400, "不支持的平台: " + platform);
        }

        try {
            StandardOrder order = adapter.parseWebhookRequest(request);
            if (order != null) {
                orderProcessService.processOrder(order);
            }
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(500, "处理失败: " + e.getMessage());
        }
    }

    @GetMapping("/{platform}/test")
    public Result<String> testWebhook(@PathVariable String platform) {
        return Result.success("Webhook URL: /api/webhook/" + platform);
    }
}
