package com.inventory.adapter;

import com.inventory.dto.PlatformCredential;
import com.inventory.dto.StandardOrder;
import com.inventory.dto.SyncResult;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface PlatformAdapter {

    String getPlatformType();

    boolean verifyCredentials(PlatformCredential credential);

    String refreshToken(String refreshToken);

    StandardOrder parseWebhookRequest(HttpServletRequest request);

    List<StandardOrder> fetchOrders(Long since);

    SyncResult syncInventory(String platformSku, Integer quantity);

    String generateSignature(String timestamp, String body, String secret);
}
