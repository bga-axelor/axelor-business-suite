package com.axelor.apps.event.web;

import com.axelor.apps.event.db.Discount;
import com.axelor.apps.event.db.Events;
import com.axelor.apps.event.db.repo.EventRegistrationsRepository;
import com.axelor.apps.event.exceptions.IExceptionMessage;
import com.axelor.i18n.I18n;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DiscountController {

  @Inject EventRegistrationsRepository eventRegistrationsRepository;

  public void checkDays(ActionRequest request, ActionResponse response) {

    Discount discount = request.getContext().asType(Discount.class);
    if (discount.getEvent() != null) {

      LocalDate fromDateTime = discount.getEvent().getRegistrationOpen();
      LocalDate toDateTime = discount.getEvent().getRegistrationClose();

      LocalDate tempDateTime = LocalDate.from(fromDateTime);
      long days = tempDateTime.until(toDateTime, ChronoUnit.DAYS);

      if (discount.getBeforeDays() > days) {
        response.setError(I18n.get(IExceptionMessage.VALIDATE_DISCOUNT_BEFORE_DAYS));
      }
    } else {
      response.setError(I18n.get(IExceptionMessage.VALIDATE_EVENT));
    }
  }

  public void computeAmount(ActionRequest request, ActionResponse response) {
    BigDecimal amount = BigDecimal.ZERO;
    Discount disc = request.getContext().asType(Discount.class);
    if (disc.getEvent() != null && request.getContext().get("_isOpenFromMenu") == null) {
      Events events = request.getContext().getParent().asType(Events.class);
      disc.setEvent(events);
    }
    if (disc.getEvent() != null && disc.getDiscountPercent() != BigDecimal.ZERO) {
      amount =
          (disc.getDiscountPercent().multiply(disc.getEvent().getEventFees()))
              .divide(new BigDecimal(100));
    }
    response.setValue("discountAmount", amount);
  }
}
