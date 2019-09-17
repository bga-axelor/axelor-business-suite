package com.axelor.apps.event.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import com.axelor.apps.base.db.ImportConfiguration;
import com.axelor.apps.base.db.ImportHistory;
import com.axelor.apps.base.exceptions.IExceptionMessage;
import com.axelor.apps.base.service.imports.importer.FactoryImporter;
import com.axelor.exception.AxelorException;
import com.axelor.exception.db.repo.TraceBackRepository;
import com.axelor.i18n.I18n;
import com.axelor.meta.MetaFiles;
import com.axelor.meta.db.MetaFile;
import com.google.common.io.Files;
import com.google.inject.Inject;

public class ImportEnvRegiServiceImpl implements ImportEnvRegiService {

  @Inject private FactoryImporter factoryImporter;

  @Inject private MetaFiles metaFiles;

  @Override
  public ImportHistory importEventRegistration(MetaFile dataFile, String eventId) {
    ImportHistory importHistory = null;
    try {

      File configXmlFile = this.getConfigXmlFile();
      File dataCsvFile = this.getDataCsvFile(dataFile, eventId);

      importHistory = getEventRegistrationFile(configXmlFile, dataCsvFile);
      this.deleteTempFiles(configXmlFile, dataCsvFile);

    } catch (Exception e) {
      e.printStackTrace();
    }
    return importHistory;
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

  private ImportHistory getEventRegistrationFile(File configXmlFile, File dataCsvFile) {

    ImportHistory importHistory = null;
    try {
      ImportConfiguration importConfiguration = new ImportConfiguration();
      importConfiguration.setBindMetaFile(metaFiles.upload(configXmlFile));
      importConfiguration.setDataMetaFile(metaFiles.upload(dataCsvFile));

      importHistory = factoryImporter.createImporter(importConfiguration).run();

    } catch (Exception e) {
      e.printStackTrace();
    }
    return importHistory;
  }

  private File getDataCsvFile(MetaFile dataFile, String eventId) throws IOException {
    BufferedReader br = null;
    BufferedWriter bw = null;
    final String lineSep = ",";

    File csvFile = null;
    File csvFile1 = null;
    String addedColumn = null;
    try {
      File tempDir = Files.createTempDir();
      csvFile = new File(tempDir, "event_registration.csv");
      csvFile1 = new File(tempDir, "event_registration1.csv");
      Files.copy(MetaFiles.getPath(dataFile).toFile(), csvFile);
      // append eventId column

      br = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile)));
      bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile1)));

      String line = null;
      int i = 0;
      for (line = br.readLine(); line != null; line = br.readLine(), i++) {
        if (i == 0) {
          addedColumn = String.valueOf("eventId");
        } else {
          addedColumn = String.valueOf(eventId);
        }
        bw.write(line + lineSep + addedColumn);
        bw.newLine();
      }

      Files.copy(MetaFiles.getPath(dataFile).toFile(), csvFile1);

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (br != null) br.close();
      if (bw != null) bw.close();
    }
    return csvFile1;
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
