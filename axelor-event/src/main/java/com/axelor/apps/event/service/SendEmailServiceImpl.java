package com.axelor.apps.event.service;

import com.axelor.apps.base.service.user.UserService;
import com.axelor.apps.crm.message.MessageServiceCrmImpl;
import com.axelor.apps.message.db.repo.MessageRepository;
import com.axelor.meta.db.repo.MetaAttachmentRepository;
import com.google.inject.Inject;

public class SendEmailServiceImpl extends MessageServiceCrmImpl {

  @Inject
  public SendEmailServiceImpl(
      MetaAttachmentRepository metaAttachmentRepository,
      MessageRepository messageRepository,
      UserService userService) {
    super(metaAttachmentRepository, messageRepository, userService);
  }
}
