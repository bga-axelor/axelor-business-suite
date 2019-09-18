package com.axelor.apps.event.service;

import com.axelor.apps.event.db.Discount;
import com.axelor.apps.event.db.EventRegistrations;
import com.axelor.apps.event.db.Events;
import com.axelor.apps.event.db.repo.EventRegistrationsRepository;
import com.axelor.apps.event.db.repo.EventsRepository;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class EventRegistrationsServiceImpl implements EventRegistrationsService {

  @Inject EventRegistrationsRepository eventRegistrationsRepository;

  @Inject EventsRepository eventsRepository;

  @Override
  public BigDecimal computeRegisterationAmount(EventRegistrations eventRegistrations) {
    BigDecimal maxDisc = BigDecimal.ZERO;

    LocalDate fromDate = eventRegistrations.getRegistrationDate().toLocalDate();
    LocalDate toDate = eventRegistrations.getEvent().getRegistrationClose();

    LocalDate tempDateTime = LocalDate.from(fromDate);
    long days = tempDateTime.until(toDate, ChronoUnit.DAYS);

    if (!eventRegistrations.getEvent().getDiscounts().isEmpty()
        && eventRegistrations.getEvent().getDiscounts().size() != 0) {

      for (Discount discount : eventRegistrations.getEvent().getDiscounts()) {
        if (days <= discount.getBeforeDays()) {
          if (maxDisc.compareTo(discount.getDiscountAmount()) != 1) {
            maxDisc = discount.getDiscountAmount();
          }
        }
      }
    }
    return eventRegistrations.getEvent().getEventFees().subtract(maxDisc);
  }

  @Override
  @Transactional
  public Events changeEventDetail(Events events) {
    BigDecimal amountCollected = BigDecimal.ZERO;
    BigDecimal totalDiscount = BigDecimal.ZERO;
    int totalEntry =
        (int)
            eventRegistrationsRepository
                .all()
                .filter("self.event= :event")
                .bind("event", events)
                .count();

    for (EventRegistrations er : events.getEventRegistrations()) {
      amountCollected = amountCollected.add(er.getAmount());
    }

    totalDiscount =
        (events.getEventFees().multiply(new BigDecimal(totalEntry))).subtract(amountCollected);
    events.setTotalEntry(totalEntry);
    events.setAmountCollected(amountCollected);
    events.setTotalDiscount(totalDiscount);

    return eventsRepository.save(events);
  }
}
