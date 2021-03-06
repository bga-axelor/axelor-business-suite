package com.axelor.apps.gst.service;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.Tax;
import com.axelor.apps.account.db.TaxLine;
import com.axelor.apps.account.db.repo.TaxLineRepository;
import com.axelor.apps.account.db.repo.TaxRepository;
import com.axelor.apps.account.service.AccountManagementAccountService;
import com.axelor.apps.account.service.AnalyticMoveLineService;
import com.axelor.apps.account.service.app.AppAccountService;
import com.axelor.apps.base.db.Address;
import com.axelor.apps.base.service.CurrencyService;
import com.axelor.apps.base.service.PriceListService;
import com.axelor.apps.purchase.service.PurchaseProductService;
import com.axelor.apps.supplychain.service.InvoiceLineSupplychainService;
import com.axelor.exception.AxelorException;
import com.google.inject.Inject;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class InvoiceLineServiceGSTImpl extends InvoiceLineSupplychainService
    implements InvoiceLineServiceGST {

  @Inject private AddressService addressService;
  @Inject private TaxLineRepository taxLineRepository;
  @Inject private TaxRepository taxRepository;

  @Inject
  public InvoiceLineServiceGSTImpl(
      CurrencyService currencyService,
      PriceListService priceListService,
      AppAccountService appAccountService,
      AnalyticMoveLineService analyticMoveLineService,
      AccountManagementAccountService accountManagementAccountService,
      PurchaseProductService purchaseProductService) {
    super(
        currencyService,
        priceListService,
        appAccountService,
        analyticMoveLineService,
        accountManagementAccountService,
        purchaseProductService);
  }

  @Override
  public Map<String, Object> fillProductInformation(Invoice invoice, InvoiceLine invoiceLine)
      throws AxelorException {

    Map<String, Object> productInformation = new HashMap<>();
    productInformation = super.fillProductInformation(invoice, invoiceLine);

    invoiceLine.setPrice(new BigDecimal(productInformation.get("price").toString()));
    invoiceLine.setGstRate(invoiceLine.getProduct().getGstRate());
    //    set tax Line for product base on GSTRate  if product have no taxLine
    if (productInformation.get("taxLine") == null) {
      long id =
          (long)
              (taxRepository.findByCode("GST") != null
                  ? taxRepository.findByCode("GST").getId()
                  : 0);

      TaxLine taxLine =
          taxLineRepository
              .all()
              .filter("self.value = :value and self.tax = :taxId")
              .bind("value", invoiceLine.getGstRate().divide(new BigDecimal(100)))
              .bind("taxId", id)
              .fetchOne();

      //      TaxLine taxLine =
      //          taxLineRepository
      //              .all()
      //              .filter("self.value = :value and self.taxt = :taxId")
      //              .bind("value", invoiceLine.getGstRate().divide(new BigDecimal(100)))
      //              .fetchOne();
      System.out.println(taxLine.getValue());

      if (taxLine != null) {
        productInformation.remove("error");
        invoiceLine.setTaxLine(taxLine);
        productInformation.put("taxLine", taxLine);
      }
    }

    boolean isNullAddress = false;
    boolean isSameState = false;

    if (invoice.getCompany() == null
        || invoice.getCompany() == null
        || invoice.getCompany().getAddress() == null
        || invoice.getCompany().getAddress().getState() == null
        || invoice.getAddress() == null
        || invoice.getAddress().getState() == null) {
      System.out.println("address..is null extends..method..");
      isNullAddress = true;
    } else {
      Address companyAddress = invoice.getCompany().getAddress();
      Address invoiceAddress = invoice.getAddress();
      //              Address shippingAddress = invoice.getShipingAddress();
      Address shippingAddress = invoice.getCompany().getAddress();

      isSameState =
          addressService.checkAddressStateForInvoice(
              companyAddress,
              invoiceAddress,
              shippingAddress,
              invoice.getIsUseInvoiceAddressAsShiping());
    }

    invoiceLine = calculateInvoiceLine(invoiceLine, isSameState, isNullAddress);

    productInformation.put("igst", invoiceLine.getIgst());
    productInformation.put("sgst", invoiceLine.getSgst());
    productInformation.put("cgst", invoiceLine.getCgst());
    productInformation.put("grossAmount", invoiceLine.getGrossAmount());

    return productInformation;
  }

  @Override
  public InvoiceLine calculateInvoiceLine(
      InvoiceLine invoiceLine, Boolean isInvoiceIsShipping, Boolean isNullAddress) {

    BigDecimal netAmt = BigDecimal.ZERO;
    BigDecimal igst = BigDecimal.ZERO;
    BigDecimal sgst = BigDecimal.ZERO;
    BigDecimal cgst = BigDecimal.ZERO;
    BigDecimal finalGST = BigDecimal.ZERO;
    BigDecimal grossAmt = BigDecimal.ZERO;

    if (!isNullAddress) {
      if (invoiceLine.getQty() != null
          && invoiceLine.getPrice() != null
          && invoiceLine.getQty() != BigDecimal.ZERO) {
        BigDecimal qty = invoiceLine.getQty();
        BigDecimal price = invoiceLine.getPrice();
        netAmt = qty.multiply(price);
      }

      //      GST

      BigDecimal gstAmount = netAmt.multiply(invoiceLine.getGstRate().divide(new BigDecimal(100)));
      if (isInvoiceIsShipping) {
        // Net amount*GST rate/2:  if state is same in invoice address and company address on
        // invoice
        sgst = gstAmount.divide(new BigDecimal(2));
        cgst = gstAmount.divide(new BigDecimal(2));
      } else {
        igst = gstAmount;
      }
    }

    invoiceLine.setSgst(sgst);
    invoiceLine.setCgst(cgst);
    invoiceLine.setIgst(igst);

    // Net amount + (IGST or SGST + CGST)
    finalGST =
        finalGST.add(invoiceLine.getIgst()).add(invoiceLine.getSgst()).add(invoiceLine.getCgst());
    grossAmt = netAmt.add(finalGST);
    invoiceLine.setGrossAmount(grossAmt);
    return invoiceLine;
  }
}
