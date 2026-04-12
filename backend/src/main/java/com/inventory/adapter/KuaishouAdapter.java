package com.inventory.adapter;

import com.inventory.dto.PlatformCredential;
import com.inventory.dto.StandardOrder;
import com.inventory.dto.SyncResult;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class KuaishouAdapter extends AbstractPlatformAdapter {

    @Override
    public String getPlatformType() {
        return "KUAISHOU";
    }

    @Override
    public boolean verifyCredentials(PlatformCredential credential) {
        return false;
    }

    @Override
    public String refreshToken(String refreshToken) {
        return null;
    }

    @Override
    public StandardOrder parseWebhookRequest(HttpServletRequest request) {
        return null;
    }

    @Override
    public List<StandardOrder> fetchOrders(Long since) {
        return new ArrayList<>();
    }

    @Override
    public SyncResult syncInventory(String platformSku, Integer quantity) {
        return SyncResult.success("KS_" + System.currentTimeMillis());
    }

    @Override
    public String generateSignature(String timestamp, String body, String secret) {
        return "";
    }
}
