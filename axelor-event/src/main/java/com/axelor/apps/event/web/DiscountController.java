package com.axelor.apps.event.web;

import com.axelor.apps.event.db.Discount;
import com.axelor.apps.event.db.repo.EventRegistrationsRepository;
import com.axelor.apps.event.exceptions.IExceptionMessage;
import com.axelor.i18n.I18n;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class DiscountController {

  @Inject EventRegistrationsRepository eventRegistrationsRepository;

  public void checkDays(ActionRequest request, ActionResponse response) {

    Discount discount = request.getContext().asType(Discount.class);

    LocalDateTime fromDateTime = discount.getEvent().getStartDate();
    LocalDateTime toDateTime = discount.getEvent().getEndDate();

    LocalDateTime tempDateTime = LocalDateTime.from(fromDateTime);
    long days = tempDateTime.until(toDateTime, ChronoUnit.DAYS);

    if (discount.getBeforeDays() > days) {
      //      response.setError(
      //          "Don’t allows to put days which exceed duration between open and close
      // registration dates.");
      response.setError(I18n.get(IExceptionMessage.VALIDATE_DISCOUNT_BEFORE_DAYS));
    }
  }

  public void computeAmount(ActionRequest request, ActionResponse response) {
    BigDecimal amount = BigDecimal.ZERO;
    Discount disc = request.getContext().asType(Discount.class);
    //    Discount amount: Discount percent * Event fees /100.
    amount =
        (disc.getDiscountPercent().multiply(disc.getEvent().getEventFees()))
            .divide(new BigDecimal(100));

    response.setValue("discountAmount", amount);
  }
}
