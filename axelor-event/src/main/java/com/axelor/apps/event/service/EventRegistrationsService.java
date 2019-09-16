package com.axelor.apps.event.service;

import com.axelor.apps.event.db.EventRegistrations;
import com.axelor.apps.event.db.Events;
import java.math.BigDecimal;

public interface EventRegistrationsService {

  BigDecimal computeRegisterationAmount(EventRegistrations eventRegistrations);

  Events totalEntry(Events events);
}
