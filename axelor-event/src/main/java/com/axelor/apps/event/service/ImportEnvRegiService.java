package com.axelor.apps.event.service;

import com.axelor.meta.db.MetaFile;

public interface ImportEnvRegiService {

  void importEventRegistration(MetaFile dataFile, String eventId);
}
