package com.axelor.apps.event.service;

import com.axelor.apps.base.db.ImportHistory;
import com.axelor.meta.db.MetaFile;

public interface ImportEnvRegiService {

  ImportHistory importEventRegistration(MetaFile dataFile, String eventId);
}
