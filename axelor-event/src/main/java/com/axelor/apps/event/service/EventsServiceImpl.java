package com.axelor.apps.event.service;

import com.axelor.apps.event.db.EventRegistrations;
import com.axelor.apps.event.db.Events;
import com.axelor.apps.event.db.repo.EventRegistrationsRepository;
import com.axelor.apps.message.db.EmailAccount;
import com.axelor.apps.message.db.EmailAddress;
import com.axelor.apps.message.db.Message;
import com.axelor.apps.message.db.repo.EmailAccountRepository;
import com.axelor.apps.message.db.repo.MessageRepository;
import com.axelor.apps.message.service.MessageService;
import com.axelor.exception.AxelorException;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import java.util.HashSet;
import java.util.Set;

public class EventsServiceImpl implements EventsService {

  @Inject private MessageRepository messageRepo;
  @Inject private MessageService messageService;
  @Inject private EmailAccountRepository emailAccountRepository;
  @Inject private EventRegistrationsRepository eventRegistrationsRepository;

  @Override
  public void sendEmails(Events events) throws AxelorException {
    Message message;
    for (EventRegistrations eventRegistrations : events.getEventRegistrations()) {
      System.out.println(eventRegistrations.getEmail());
      EmailAddress emailAddress = new EmailAddress();
      Set<EmailAddress> toEmailAddressSet = new HashSet<EmailAddress>();
      emailAddress.setAddress(eventRegistrations.getEmail());
      toEmailAddressSet.add(emailAddress);
      message = saveMassage(emailAddress, toEmailAddressSet, events.getEvnRef());
      messageService.sendMessage(messageRepo.find(message.getId()));
      setSendMailTrue(eventRegistrations);
    }
  }

  // use for get default email account
  private EmailAccount getEmailAccount() {
    return emailAccountRepository
        .all()
        .filter("self.isValid = :isValid and self.isDefault = :isDefault")
        .bind("isValid", true)
        .bind("isDefault", true)
        .fetchOne();
  }

  @Transactional
  private Message saveMassage(
      EmailAddress emailAddress, Set<EmailAddress> toEmailAddressSet, String evnRef) {
    Message message = new Message();
    message.setMediaTypeSelect(2);
    message.setSubject("send static message...");
    message.setToEmailAddressSet(toEmailAddressSet);
    message.setContent("Email for send from axelor-event " + evnRef + " for invitation.");
    message.setMailAccount(getEmailAccount());
    return messageRepo.save(message);
  }

  @Transactional
  private void setSendMailTrue(EventRegistrations eventRegistrations) {
    eventRegistrations.setIsSendEmail(true);
    eventRegistrationsRepository.save(eventRegistrations);
  }
}
