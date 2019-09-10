package com.axelor.apps.gst.module;

import com.axelor.app.AxelorModule;
import com.axelor.apps.gst.service.AddressService;
import com.axelor.apps.gst.service.AddressServiceImpl;
import com.axelor.apps.gst.service.InvoiceLineServiceGST;
import com.axelor.apps.gst.service.InvoiceLineServiceGSTImpl;
import com.axelor.apps.gst.service.InvoiceServiceGST;
import com.axelor.apps.gst.service.InvoiceServiceGSTImpl;

public class GSTModule extends AxelorModule {

  @Override
  protected void configure() {

    bind(InvoiceLineServiceGST.class).to(InvoiceLineServiceGSTImpl.class);
    bind(AddressService.class).to(AddressServiceImpl.class);
    //    bind(InvoiceLineSupplychainService.class).to(InvoiceLineServiceGSTImpl.class);
    //    bind(InvoiceServiceProjectImpl.class).to(InvoiceServiceGSTImpl.class);
    //    bind(SaleOrderLineServiceSupplyChainImpl.class).to(SaleOrderLineServiceGSTImpl.class);
    //    bind(InvoicePrintServiceImpl.class).to(InvoicePrintGSTServiceImpl.class);
    bind(InvoiceServiceGST.class).to(InvoiceServiceGSTImpl.class);
  }
}
