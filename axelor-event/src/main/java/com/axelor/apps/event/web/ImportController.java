package com.axelor.apps.event.web;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import com.axelor.apps.event.db.repo.EventsRepository;
import com.axelor.apps.event.exceptions.IExceptionMessage;
import com.axelor.apps.event.service.EventRegistrationsService;
import com.axelor.apps.event.service.ImportEnvRegiService;
import com.axelor.exception.AxelorException;
import com.axelor.exception.service.TraceBackService;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.meta.MetaFiles;
import com.axelor.meta.db.MetaFile;
import com.axelor.meta.db.repo.MetaFileRepository;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.common.io.Files;
import com.google.inject.Inject;

public class ImportController {

  @Inject EventRegistrationsService eventRegistrationsService;

  @Inject ImportEnvRegiService importEnvRegiService;

  @SuppressWarnings("unchecked")
  public void importCSVData(ActionRequest request, ActionResponse response)
      throws IOException, AxelorException, ParseException, ClassNotFoundException {

    String eventId = request.getContext().get("_eventId").toString();

    LinkedHashMap<String, Object> map =
        (LinkedHashMap<String, Object>) request.getContext().get("envImportFile");

    MetaFile dataFile =
        Beans.get(MetaFileRepository.class).find(((Integer) map.get("id")).longValue());
    File csvFile = MetaFiles.getPath(dataFile).toFile();

    if (Files.getFileExtension(csvFile.getName()).equals("csv")) {

      try {

        importEnvRegiService.importEventRegistration(dataFile, eventId);

        response.setNotify("Event registration import success.");

      } catch (Exception e) {
        TraceBackService.trace(response, e);
      }

    } else {
      response.setError(I18n.get(IExceptionMessage.VALIDATE_FILE_TYPE));
    }
    eventRegistrationsService.changeEventDetail(
        Beans.get(EventsRepository.class).all().filter("self.id = ?", eventId).fetchOne());
  }

  public LocalDateTime convertLocalDateTime(String strDate) {
    return LocalDateTime.parse(strDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
  }
}
