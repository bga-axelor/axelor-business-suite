package com.axelor.apps.event.service;

import com.axelor.app.AppSettings;
import com.axelor.apps.base.exceptions.IExceptionMessage;
import com.axelor.data.csv.CSVImporter;
import com.axelor.exception.AxelorException;
import com.axelor.exception.db.repo.TraceBackRepository;
import com.axelor.i18n.I18n;
import com.axelor.meta.MetaFiles;
import com.axelor.meta.db.MetaFile;
import com.google.common.io.Files;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class ImportEnvRegiServiceImpl implements ImportEnvRegiService {

  @Override
  public void importEventRegistration(MetaFile dataFile, String eventId) {
    try {

      File configXmlFile = this.getConfigXmlFile();
      File dataCsvFile = this.getDataCsvFile(dataFile, eventId);

      Map<String, Object> map = new HashMap<String, Object>();
      map.put("eventId", eventId);

      getEventRegistrationFile(configXmlFile, dataCsvFile, map);
      this.deleteTempFiles(configXmlFile, dataCsvFile);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private File getConfigXmlFile() {

    File configFile = null;
    try {
      configFile = File.createTempFile("input-config", ".xml");

      InputStream bindFileInputStream =
          this.getClass().getResourceAsStream("/import-configs/event-registration.xml");

      if (bindFileInputStream == null) {
        throw new AxelorException(
            TraceBackRepository.CATEGORY_CONFIGURATION_ERROR,
            I18n.get(IExceptionMessage.IMPORTER_3));
      }

      FileOutputStream outputStream = new FileOutputStream(configFile);

      IOUtils.copy(bindFileInputStream, outputStream);

    } catch (Exception e) {
      e.printStackTrace();
    }
    return configFile;
  }

  private void getEventRegistrationFile(
      File configXmlFile, File dataCsvFile, Map<String, Object> map) {
    try {

      CSVImporter csvImporter =
          new CSVImporter(
              configXmlFile.getAbsolutePath(), AppSettings.get().getPath("file.upload.dir", ""));

      csvImporter.setContext(map);
      csvImporter.run();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private File getDataCsvFile(MetaFile dataFile, String eventId) throws IOException {

    File csvFile = null;
    try {
      File tempDir = Files.createTempDir();
      csvFile = new File(tempDir, "event_registration.csv");
      Files.copy(MetaFiles.getPath(dataFile).toFile(), csvFile);

    } catch (Exception e) {
      e.printStackTrace();
    }
    return csvFile;
  }

  private void deleteTempFiles(File configXmlFile, File dataCsvFile) {

    try {
      if (configXmlFile.isDirectory() && dataCsvFile.isDirectory()) {
        FileUtils.deleteDirectory(configXmlFile);
        FileUtils.deleteDirectory(dataCsvFile);
      } else {
        configXmlFile.delete();
        dataCsvFile.delete();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
