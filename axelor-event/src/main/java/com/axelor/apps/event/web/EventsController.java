package com.axelor.apps.event.web;

import com.axelor.apps.event.db.Events;
import com.axelor.apps.event.service.EventsService;
import com.axelor.apps.message.exception.IExceptionMessage;
import com.axelor.exception.AxelorException;
import com.axelor.exception.service.TraceBackService;
import com.axelor.i18n.I18n;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

public class EventsController {

  @Inject EventsService eventService;

  @Transactional
  public void sendEmails(ActionRequest request, ActionResponse response) {
    Events events = request.getContext().asType(Events.class);
    try {
      eventService.sendEmails(events);
      response.setReload(true);
      response.setFlash(I18n.get(IExceptionMessage.MESSAGE_4));
    } catch (AxelorException e) {
      TraceBackService.trace(response, e);
    }
  }

  public void changeEventDetail(ActionRequest request, ActionResponse response) {
    Events events = request.getContext().asType(Events.class);
    response.setValues(eventService.changeEventDetail(events));
  }
}
