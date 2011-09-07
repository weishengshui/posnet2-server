/**
 * 
 */
package com.chinarewards.qqgbvpn.mgmtui.logic.pos.impl;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chinarewards.qqgbvpn.common.DateTimeProvider;
import com.chinarewards.qqgbvpn.domain.PageInfo;
import com.chinarewards.qqgbvpn.domain.event.DomainEntity;
import com.chinarewards.qqgbvpn.domain.event.DomainEvent;
import com.chinarewards.qqgbvpn.domain.status.DeliveryNoteStatus;
import com.chinarewards.qqgbvpn.domain.status.PosDeliveryStatus;
import com.chinarewards.qqgbvpn.domain.status.PosInitializationStatus;
import com.chinarewards.qqgbvpn.domain.status.PosOperationStatus;
import com.chinarewards.qqgbvpn.logic.journal.JournalLogic;
import com.chinarewards.qqgbvpn.mgmtui.dao.DeliveryDao;
import com.chinarewards.qqgbvpn.mgmtui.dao.DeliveryDetailDao;
import com.chinarewards.qqgbvpn.mgmtui.dao.agent.AgentDao;
import com.chinarewards.qqgbvpn.mgmtui.dao.pos.PosDao;
import com.chinarewards.qqgbvpn.mgmtui.exception.DeliveryNoteWithNoDetailException;
import com.chinarewards.qqgbvpn.mgmtui.exception.PosNotExistException;
import com.chinarewards.qqgbvpn.mgmtui.exception.PosWithWrongStatusException;
import com.chinarewards.qqgbvpn.mgmtui.exception.ServiceException;
import com.chinarewards.qqgbvpn.mgmtui.logic.pos.DeliveryLogic;
import com.chinarewards.qqgbvpn.mgmtui.model.agent.AgentVO;
import com.chinarewards.qqgbvpn.mgmtui.model.delivery.DeliveryNoteDetailVO;
import com.chinarewards.qqgbvpn.mgmtui.model.delivery.DeliveryNoteVO;
import com.chinarewards.qqgbvpn.mgmtui.model.pos.PosVO;
import com.chinarewards.qqgbvpn.mgmtui.model.util.PaginationTools;
import com.chinarewards.qqgbvpn.mgmtui.util.Tools;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;

/**
 * @author cream
 * 
 */
public class DeliveryLogicImpl implements DeliveryLogic {

	Logger log = LoggerFactory.getLogger(DeliveryLogicImpl.class);

	@Inject
	Provider<DeliveryDao> deliveryDao;

	@Inject
	Provider<DeliveryDetailDao> deliveryDetailDao;

	@Inject
	Provider<AgentDao> agentDao;

	@Inject
	Provider<PosDao> posDao;

	@Inject
	DateTimeProvider dtProvider;

	@Inject
	Provider<JournalLogic> journalLogic;

	protected DeliveryDao getDeliveryDao() {
		return deliveryDao.get();
	}

	protected DeliveryDetailDao getDetailDao() {
		return deliveryDetailDao.get();
	}

	@Override
	@Transactional
	public List<DeliveryNoteVO> fetchAllDelivery() {
		return getDeliveryDao().fetchAllDelivery();
	}

	@Override
	public DeliveryNoteVO fetchById(String noteId) {
		if (Tools.isEmptyString(noteId)) {
			throw new IllegalArgumentException("delivery id is missing");
		}
		return getDeliveryDao().fetchDeliveryById(noteId);
	}

	@Override
	@Transactional
	public PageInfo<DeliveryNoteVO> fetchDeliveryList(PaginationTools pagination) {
		log.debug(
				" Process in method fetchDeliveryList, start-{}, limit-{}",
				new Object[] { pagination.getStartIndex(),
						pagination.getCountOnEachPage() });

		long count = getDeliveryDao().countDelivertList();

		log.debug("result.count:{}", count);

		List<DeliveryNoteVO> list = getDeliveryDao().fetchDeliveryList(
				pagination.getStartIndex(), pagination.getCountOnEachPage());

		log.debug("result.list.size:{}", list == null ? "null" : list.size());

		PageInfo<DeliveryNoteVO> result = new PageInfo<DeliveryNoteVO>();
		result.setItems(list);
		if (count > Integer.MAX_VALUE) {
			result.setRecordCount(Integer.MAX_VALUE);
		}
		result.setRecordCount((int) count);

		return result;
	}

	@Override
	@Transactional
	public List<DeliveryNoteDetailVO> fetchDetailListByNoteId(String deliveryId) {

		log.debug(" Process in method fetchDeliveryNoteDetailList, note.id:{}",
				deliveryId);
		if (Tools.isEmptyString(deliveryId)) {
			throw new IllegalArgumentException("deliveryNote ID is missing!");
		}

		List<DeliveryNoteDetailVO> resultList = getDetailDao()
				.fetchDetailListByNoteId(deliveryId);

		log.debug("result.size:{}",
				resultList == null ? "null" : resultList.size());
		return resultList;
	}

	@Override
	public DeliveryNoteVO createDeliveryNote() {
		Date now = dtProvider.getTime();
		// create new delivery note with status DeliveryNoteStatus#DRAFT
		DeliveryNoteVO dn = new DeliveryNoteVO();
		dn.setCreateDate(now);
		dn.setDnNumber(Tools.getOnlyNumber("POSDN-DRAFT"));
		dn.setStatus(DeliveryNoteStatus.DRAFT.toString());

		return getDeliveryDao().create(dn);
	}

	@Override
	public void deleteDeliveryNote(String noteId) {
		if (Tools.isEmptyString(noteId)) {
			throw new IllegalArgumentException("delivery note ID is missing.");
		}
		DeliveryNoteVO dn = getDeliveryDao().fetchDeliveryById(noteId);
		if (!DeliveryNoteStatus.DRAFT.toString().equals(dn.getStatus())) {
			throw new IllegalArgumentException(
					"delivery should be DRAFT, but now is " + dn.getStatus());
		}
		getDeliveryDao().deleteById(noteId);
	}

	@Override
	public DeliveryNoteVO associateAgent(String deliveryNoteId, String agentId) {
		if (Tools.isEmptyString(deliveryNoteId)) {
			throw new IllegalArgumentException("Delivery note ID is missing.");
		}

		DeliveryNoteVO dn = null;
		try {
			DeliveryNoteVO note = getDeliveryDao().fetchDeliveryById(
					deliveryNoteId);
			AgentVO agent = agentDao.get().findById(agentId);
			note.setAgent(agent);
			note.setAgentName(agent == null ? null : agent.getName());
			dn = getDeliveryDao().merge(note);
		} catch (ServiceException e) {
			log.error("unknow exception catched!", e);
		}
		return dn;
	}

	@Override
	public DeliveryNoteDetailVO appendPosToNote(String deliveryNoteId,
			String posId) throws PosNotExistException,
			PosWithWrongStatusException {
		if (Tools.isEmptyString(deliveryNoteId)) {
			throw new IllegalArgumentException("Delivery note ID is missing.");
		}
		if (Tools.isEmptyString(posId)) {
			throw new IllegalArgumentException("POS ID is missing.");
		}

		// check the POS first.
		DeliveryNoteDetailVO detail = getDetailDao().fetchByPosId(posId);

		// if existed, do nothing
		// else create new deliveryNoteDetail
		if (detail == null) {
			detail = getDetailDao().create(deliveryNoteId, posId);
		}

		return detail;
	}

	@Override
	public void deletePosFromNote(String deliveryNoteId, String detailId) {
		if (Tools.isEmptyString(deliveryNoteId)) {
			throw new IllegalArgumentException("Delivery note ID is missing.");
		}
		if (Tools.isEmptyString(detailId)) {
			throw new IllegalArgumentException(
					"Delivery note detail ID is missing.");
		}

		// fetch detail and remove it.
		getDetailDao().delete(detailId);
	}

	@Override
	@Transactional
	public List<DeliveryNoteDetailVO> delivery(String deliveryNoteId)
			throws DeliveryNoteWithNoDetailException {
		// Check arguments.
		if (Tools.isEmptyString(deliveryNoteId)) {
			throw new IllegalArgumentException("Delivery note ID is missing.");
		}

		List<DeliveryNoteDetailVO> uninitList = new LinkedList<DeliveryNoteDetailVO>();

		// fetch DeliveryNoteDetail list.
		List<DeliveryNoteDetailVO> detailList = getDetailDao()
				.fetchDetailListByNoteId(deliveryNoteId);

		if (detailList == null || detailList.isEmpty()) {
			throw new DeliveryNoteWithNoDetailException();
		}

		// check POS initialized status.
		for (DeliveryNoteDetailVO detail : detailList) {
			if (!detail.getIstatus().equals(
					PosInitializationStatus.INITED.toString())) {
				uninitList.add(detail);
			}
		}

		return uninitList;
	}

	@Override
	@Transactional
	public void confirmDelivery(String deliveryNoteId) {
		// check delivery note status - DeliveryNoteStatus#DRAFT
		DeliveryNoteVO dn = getDeliveryDao().fetchDeliveryById(deliveryNoteId);
		if (!DeliveryNoteStatus.DRAFT.toString().equals(dn.getStatus())) {
			throw new IllegalArgumentException(
					"Delivery note status should be DRAFT, not "
							+ dn.getStatus());
		}

		Date now = dtProvider.getTime();

		// modify delivery note status - DeliveryNoteStatus#CONFIRMED
		dn.setStatus(DeliveryNoteStatus.CONFIRMED.toString());
		dn.setConfirmDate(now);
		dn.setDnNumber(Tools.getOnlyNumber("POSDN"));
		getDeliveryDao().merge(dn);

		List<PosVO> posList = getDetailDao().fetchPosByNoteId(dn.getId());
		try {
			for (PosVO pos : posList) {
				// check POS status.
				if (!PosDeliveryStatus.DELIVERED.toString().equals(
						pos.getDstatus())) {
					throw new IllegalArgumentException(
							"Pos dstatus should be PosDeliveryStatus.DELIVERED, but now is:"
									+ pos.getDstatus());
				}
				pos.setDstatus(PosDeliveryStatus.DELIVERED.toString());
				pos.setOstatus(PosOperationStatus.ALLOWED.toString());
				posDao.get().updatePos(pos);
			}
		} catch (Exception e) {
			log.error("Unknow Exception catched!", e);
		}

		// add journalLogic
		try {
			ObjectMapper map = new ObjectMapper();
			String eventDetail;
			eventDetail = map.writeValueAsString(dn);
			journalLogic.get().logEvent(
					DomainEvent.USER_CONFIRMED_DNOTE.toString(),
					DomainEntity.DELIVERY_NOTE.toString(), deliveryNoteId,
					eventDetail);
		} catch (Exception e) {
			log.error("Error in parse to JSON", e);
		}
	}

	@Override
	@Transactional
	public void printDelivery(String deliveryNoteId) {
		// check delivery note status - not be DeliveryNoteStatus#DRAFT
		DeliveryNoteVO dn = getDeliveryDao().fetchDeliveryById(deliveryNoteId);
		if (DeliveryNoteStatus.DRAFT.toString().equals(dn.getStatus())) {
			throw new IllegalArgumentException(
					"Delivery note status should not be DRAFT, it is "
							+ dn.getStatus() + " now");
		}

		Date now = dtProvider.getTime();

		// modify delivery note status - DeliveryNoteStatus#PRINTED
		dn.setStatus(DeliveryNoteStatus.PRINTED.toString());
		dn.setPrintDate(now);
		getDeliveryDao().merge(dn);

		// add journalLogic
		try {
			ObjectMapper map = new ObjectMapper();
			String eventDetail;
			eventDetail = map.writeValueAsString(dn);
			journalLogic.get().logEvent(
					DomainEvent.USER_PRINTED_DNOTE.toString(),
					DomainEntity.DELIVERY_NOTE.toString(), deliveryNoteId,
					eventDetail);
		} catch (Exception e) {
			log.error("Error in parse to JSON", e);
		}
	}

}
