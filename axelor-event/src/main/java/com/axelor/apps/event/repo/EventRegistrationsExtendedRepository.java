package com.axelor.apps.event.repo;

import com.axelor.apps.event.db.EventRegistrations;
import com.axelor.apps.event.db.Events;
import com.axelor.apps.event.db.repo.EventRegistrationsRepository;
import com.axelor.apps.event.service.EventRegistrationsService;
import com.google.inject.Inject;

public class EventRegistrationsExtendedRepository extends EventRegistrationsRepository {

  @Inject EventRegistrationsService eventRegistrationsService;


  @Override
  public void remove(EventRegistrations eventRegistrations) {
    Events events = eventRegistrations.getEvent();
    super.remove(eventRegistrations);
    eventRegistrationsService.changeEventDetail(events);
  }
}
