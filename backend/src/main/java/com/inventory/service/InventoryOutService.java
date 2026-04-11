package com.inventory.service;

import com.inventory.dto.InventoryOutDTO;
import java.util.List;

public interface InventoryOutService {

    void createOut(InventoryOutDTO dto);

    List<InventoryOutDTO> listByDateRange(String startDate, String endDate);
}
