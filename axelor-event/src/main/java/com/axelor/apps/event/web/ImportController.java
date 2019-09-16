package com.axelor.apps.event.web;

import com.axelor.apps.base.db.ImportHistory;
import com.axelor.apps.base.exceptions.IExceptionMessage;
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
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.io.FileUtils;

public class ImportController {

  @Inject MetaFileRepository metaFileRepo;

  @Inject ImportEnvRegiService importEnvRegiService;

  public void importCSVData(ActionRequest request, ActionResponse response)
      throws IOException, AxelorException, ParseException, ClassNotFoundException {

    System.out.println("call import..");

    MetaFile metaFile =
        metaFileRepo.find(
            Long.valueOf(((Map) request.getContext().get("envImportFile")).get("id").toString()));
    File csvFile = MetaFiles.getPath(metaFile).toFile();

    if (Files.getFileExtension(csvFile.getName()).equals("csv")) {
      LinkedHashMap<String, Object> map =
          (LinkedHashMap<String, Object>) request.getContext().get("envImportFile");
      MetaFile dataFile =
          Beans.get(MetaFileRepository.class).find(((Integer) map.get("id")).longValue());

      try {

        ImportHistory importHistory = importEnvRegiService.importEventRegistration(dataFile);
        response.setAttr("importHistoryList", "value:add", importHistory);
        File readFile = MetaFiles.getPath(importHistory.getLogMetaFile()).toFile();
        response.setNotify(
            FileUtils.readFileToString(readFile, StandardCharsets.UTF_8)
                .replaceAll("(\r\n|\n\r|\r|\n)", "<br />"));

      } catch (Exception e) {
        TraceBackService.trace(response, e);
      }

    } else {
      response.setError(I18n.get(IExceptionMessage.VALIDATE_FILE_TYPE));
      //      response.setError("Please import only excel file.");
    }
  }

  public LocalDateTime convertLocalDateTime() {
    return LocalDateTime.now();
  }
}
