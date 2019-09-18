package com.axelor.apps.event.csv.script;

import com.axelor.apps.event.db.EventRegistrations;
import com.axelor.apps.event.service.EventRegistrationsService;
import com.google.inject.Inject;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImportEventRegistration {

  @Inject EventRegistrationsService eventRegistrationsService;

  private final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public Object importEventRegistration(Object bean, Map<String, Object> values) {
    BigDecimal amount = BigDecimal.ZERO;

    assert bean instanceof EventRegistrations;
    EventRegistrations eventRegistrations = (EventRegistrations) bean;
    try {
      System.out.println(eventRegistrations.getEvent());
      amount = eventRegistrationsService.computeRegisterationAmount(eventRegistrations);
      eventRegistrations.setAmount(amount);
    } catch (Exception e) {
      LOG.error("Error when importing Event registration : {}", e.getMessage());
    }

    return eventRegistrations;
  }
}
