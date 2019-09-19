package com.axelor.apps.event.service;

import com.axelor.apps.event.db.Events;
import com.axelor.exception.AxelorException;

public interface EventsService {

  void sendEmails(Events events) throws AxelorException;

  Events changeEventDetail(Events events);
}
