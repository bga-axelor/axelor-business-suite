/**
 * Axelor Business Solutions
 *
 * Copyright (C) 2014 Axelor (<http://axelor.com>).
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.axelor.apps.account.service.batch;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.axelor.apps.account.db.AccountingBatch;
import com.axelor.apps.account.db.AccountingSituation;
import com.axelor.apps.account.db.repo.AccountingSituationRepository;
import com.axelor.apps.account.exception.IExceptionMessage;
import com.axelor.apps.account.service.AccountCustomerService;
import com.axelor.apps.base.db.Company;
import com.axelor.db.JPA;
import com.axelor.exception.AxelorException;
import com.axelor.exception.db.IException;
import com.axelor.exception.service.TraceBackService;
import com.axelor.i18n.I18n;

public class BatchAccountCustomer extends BatchStrategy {

	private static final Logger LOG = LoggerFactory.getLogger(BatchAccountCustomer.class);
	
	@Inject
	private AccountingSituationRepository accountingSituationRepo;;

	@Inject
	public BatchAccountCustomer(AccountCustomerService accountCustomerService) {
		
		super(accountCustomerService);
	}
	
	
	@Override
	protected void start() throws IllegalArgumentException, IllegalAccessException, AxelorException {
		
		super.start();
		
		checkPoint();

	}

	
	@Override
	public void process()  {
		
		AccountingBatch accountingBatch = batch.getAccountingBatch();
		Company company = accountingBatch.getCompany();
		
		boolean updateCustAccountOk = accountingBatch.getUpdateCustAccountOk();
		boolean updateDueCustAccountOk = accountingBatch.getUpdateDueCustAccountOk();
		boolean updateDueReminderCustAccountOk = accountingBatch.getUpdateDueReminderCustAccountOk();
		
		List<AccountingSituation> accountingSituationList = (List<AccountingSituation>) accountingSituationRepo.all().filter("self.company = ?1", company).fetch();
		int i = 0;
		JPA.clear();
		for(AccountingSituation accountingSituation : accountingSituationList)  {
			try {
				
				accountingSituation = accountCustomerService.updateAccountingSituationCustomerAccount(
						accountingSituationRepo.find(accountingSituation.getId()),
						updateCustAccountOk,
						updateDueCustAccountOk,
						updateDueReminderCustAccountOk);
				
				if(accountingSituation != null)  {
					this.updateAccountingSituation(accountingSituation);
					i++;
				}
				
			} catch (Exception e) {
				
				TraceBackService.trace(new Exception(String.format(I18n.get(IExceptionMessage.BATCH_ACCOUNT_1), 
						accountingSituationRepo.find(accountingSituation.getId()).getName()), e), IException.ACCOUNT_CUSTOMER, batch.getId());
				
				incrementAnomaly();
				
				LOG.error("Bug(Anomalie) généré(e) pour la situation compable {}", accountingSituationRepo.find(accountingSituation.getId()).getName());
				
			} finally {
				
				if (i % 1 == 0) { JPA.clear(); }
	
			}	
		}
	}
	
	
	

	/**
	 * As {@code batch} entity can be detached from the session, call {@code Batch.find()} get the entity in the persistant context.
	 * Warning : {@code batch} entity have to be saved before.
	 */
	@Override
	protected void stop() {
		String comment = "";
		comment = I18n.get(IExceptionMessage.BATCH_ACCOUNT_2);
		comment += String.format(I18n.get(IExceptionMessage.BATCH_ACCOUNT_3), batch.getDone());
		comment += String.format(I18n.get(com.axelor.apps.base.exceptions.IExceptionMessage.ALARM_ENGINE_BATCH_4), batch.getAnomaly());

		super.stop();
		addComment(comment);
		
	}
	
	
	
	public String updateAccountingSituationMarked(Company company)  {
		
		int anomaly = 0;
		
		List<AccountingSituation> accountingSituationList = null;
		
		if(company != null)  {
			accountingSituationList = (List<AccountingSituation>) accountingSituationRepo.all().filter("self.company = ?1 and self.custAccountMustBeUpdateOk = 'true'", company).fetch();
		}
		else  {
			accountingSituationList = (List<AccountingSituation>) accountingSituationRepo.all().filter("self.custAccountMustBeUpdateOk = 'true'").fetch();
		}
		
		int i = 0;
		JPA.clear();
		for(AccountingSituation accountingSituation : accountingSituationList)  {
			try {
				
				accountingSituation = accountCustomerService.updateAccountingSituationCustomerAccount(
						accountingSituationRepo.find(accountingSituation.getId()),
						true, true, false);
				
				if(accountingSituation != null)  {
					i++;
				}
				
			} catch (Exception e) {
				
				TraceBackService.trace(new Exception(String.format(I18n.get(IExceptionMessage.BATCH_ACCOUNT_1),  accountingSituationRepo.find(accountingSituation.getId()).getName()), e), IException.ACCOUNT_CUSTOMER, batch.getId());
				
				anomaly++;
				
				LOG.error("Bug(Anomalie) généré(e) pour le compte client {}",  accountingSituationRepo.find(accountingSituation.getId()));
				
			} finally {
				
				if (i % 5 == 0) { JPA.clear(); }
	
			}	
		}
		
		if(anomaly!=0)  {		
			return String.format(I18n.get(IExceptionMessage.BATCH_ACCOUNT_4),anomaly);
		}
		else  {
			return String.format(I18n.get(IExceptionMessage.BATCH_ACCOUNT_5),i);
		}
	}
	
}
