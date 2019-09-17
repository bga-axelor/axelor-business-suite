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
    //    Message message = new Message();
    //    EmailAddress emailAddress = new EmailAddress();
    //    Set<EmailAddress> toEmailAddressSet = new HashSet<EmailAddress>();
    //    EmailAccount emailAccount;
    //    // find default validate congure email account
    //    emailAccount =
    //        emailAccountRepository
    //            .all()
    //            .filter("self.isValid = :isValid and self.isDefault = :isDefault")
    //            .bind("isValid", true)
    //            .bind("isDefault", true)
    //            .fetchOne();
    //
    //    emailAddress.setAddress("bga.axelor@gmail.com");
    //    toEmailAddressSet.add(emailAddress);
    //    message.setMediaTypeSelect(2);
    //    message.setSubject("send static message...");
    //    message.setToEmailAddressSet(toEmailAddressSet);
    //    message.setContent("static email for send from axelor-event for invitation.");
    //    message.setMailAccount(emailAccount);
    //    message = messageRepo.save(message);
    //
    //    System.out.println(message.getId());
    //
    //    try {
    //      sendEmailServiceImpl.sendMessage(messageRepo.find(message.getId()));
    //      response.setReload(true);
    //      response.setFlash(I18n.get(IExceptionMessage.MESSAGE_4));
    //    } catch (AxelorException e) {
    //      TraceBackService.trace(response, e);
    //    }
  }
}
