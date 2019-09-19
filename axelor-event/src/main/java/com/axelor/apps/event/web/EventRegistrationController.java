package com.axelor.apps.event.web;

import com.axelor.apps.event.db.EventRegistrations;
import com.axelor.apps.event.db.Events;
import com.axelor.apps.event.db.repo.EventRegistrationsRepository;
import com.axelor.apps.event.service.EventRegistrationsService;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;

public class EventRegistrationController {

  @Inject EventRegistrationsRepository eventRegistrationsRepository;

  @Inject EventRegistrationsService eventRegistrationsService;

  public void computeRegisterationAmount(ActionRequest request, ActionResponse response) {

    EventRegistrations eventRegistrations = request.getContext().asType(EventRegistrations.class);

    if (eventRegistrations.getEvent() != null) {
      response.setValue(
          "amount", eventRegistrationsService.computeRegisterationAmount(eventRegistrations));
    }
  }

  public void changeEventDetail(ActionRequest request, ActionResponse response) {

    EventRegistrations eventRegistrations = request.getContext().asType(EventRegistrations.class);
    eventRegistrationsService.changeEventDetail(eventRegistrations.getEvent());
  }

  public void setEvent(ActionRequest request, ActionResponse response) {
    EventRegistrations eventRegistrations = request.getContext().asType(EventRegistrations.class);
    if (eventRegistrations.getEvent() == null
        && request.getContext().get("_isOpenFromMenu") == null) {
      Events events = request.getContext().getParent().asType(Events.class);
      eventRegistrations.setEvent(events);
    }
    response.setValues(eventRegistrations);
  }
}
