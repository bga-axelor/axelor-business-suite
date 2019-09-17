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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EventRegistrationsServiceImpl implements EventRegistrationsService {

  @Inject EventRegistrationsRepository eventRegistrationsRepository;

  @Inject EventsRepository eventsRepository;

  @Override
  public BigDecimal computeRegisterationAmount(EventRegistrations eventRegistrations) {
    BigDecimal maxDisc = BigDecimal.ZERO;

    //    LocalDate fromDate = eventRegistrations.getEvent().getRegistrationOpen();
    //    LocalDate toDate = eventRegistrations.getRegistrationDate().toLocalDate();
    LocalDate fromDate = eventRegistrations.getRegistrationDate().toLocalDate();
    LocalDate toDate = eventRegistrations.getEvent().getRegistrationClose();

    LocalDate tempDateTime = LocalDate.from(fromDate);
    long days = tempDateTime.until(toDate, ChronoUnit.DAYS);

    List<BigDecimal> discAmountList = new ArrayList<BigDecimal>();

    if (!eventRegistrations.getEvent().getDiscounts().isEmpty()
        && eventRegistrations.getEvent().getDiscounts().size() != 0) {

      for (Discount discount : eventRegistrations.getEvent().getDiscounts()) {
        if (days <= discount.getBeforeDays()) {
          discAmountList.add(discount.getDiscountAmount());
        }
      }

      if (!discAmountList.isEmpty()) {
        maxDisc = Collections.max(discAmountList);
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
      totalDiscount = totalDiscount.add(events.getEventFees().subtract(er.getAmount()));
    }

    events.setTotalEntry(totalEntry);
    events.setAmountCollected(amountCollected);
    events.setTotalDiscount(totalDiscount);

    return eventsRepository.save(events);
  }
}
