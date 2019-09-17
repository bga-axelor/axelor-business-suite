package com.axelor.apps.event.module;

import com.axelor.app.AxelorModule;
import com.axelor.apps.crm.message.MessageServiceCrmImpl;
import com.axelor.apps.event.service.EventRegistrationsService;
import com.axelor.apps.event.service.EventRegistrationsServiceImpl;
import com.axelor.apps.event.service.EventsService;
import com.axelor.apps.event.service.EventsServiceImpl;
import com.axelor.apps.event.service.ImportEnvRegiService;
import com.axelor.apps.event.service.ImportEnvRegiServiceImpl;
import com.axelor.apps.event.service.SendEmailServiceImpl;

public class EventModule extends AxelorModule {

  @Override
  protected void configure() {

    bind(ImportEnvRegiService.class).to(ImportEnvRegiServiceImpl.class);
    bind(EventRegistrationsService.class).to(EventRegistrationsServiceImpl.class);
    bind(EventsService.class).to(EventsServiceImpl.class);
    bind(MessageServiceCrmImpl.class).to(SendEmailServiceImpl.class);
  }
}
