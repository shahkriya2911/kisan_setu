package com.project.kisan_setu.service;

import com.project.kisan_setu.entity.Order;

public interface InvoiceService {
    byte[] generateInvoice(Order order);
}
