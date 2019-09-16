package com.axelor.apps.event.web;

import com.axelor.apps.event.service.SendEmailServiceImpl;
import com.axelor.apps.message.db.Message;
import com.axelor.apps.message.exception.IExceptionMessage;
import com.axelor.apps.tool.ModelTool;
import com.axelor.exception.AxelorException;
import com.axelor.exception.db.repo.TraceBackRepository;
import com.axelor.exception.service.TraceBackService;
import com.axelor.i18n.I18n;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class EventsController {

  @Inject private SendEmailServiceImpl sendEmailServiceImpl;

  @SuppressWarnings({"unchecked", "unused"})
  public void sendEmails(ActionRequest request, ActionResponse response) {
    //    List<Integer> idList = (List<Integer>) request.getContext().get("_ids");
    List<Integer> idList = new ArrayList<Integer>();
    idList.add(1);
    idList.add(2);
    try {
      if (idList == null) {
        throw new AxelorException(
            TraceBackRepository.CATEGORY_MISSING_FIELD,
            I18n.get(IExceptionMessage.MESSAGE_MISSING_SELECTED_MESSAGES));
      }
      ModelTool.apply(
          Message.class, idList, model -> sendEmailServiceImpl.sendMessage((Message) model));
      response.setFlash(
          String.format(I18n.get(IExceptionMessage.MESSAGES_SEND_IN_PROGRESS), idList.size()));
      response.setReload(true);
    } catch (AxelorException e) {
      TraceBackService.trace(response, e);
    }
  }
}
