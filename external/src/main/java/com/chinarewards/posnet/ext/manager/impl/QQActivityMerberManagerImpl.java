package com.chinarewards.posnet.ext.manager.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chinarewards.ext.api.qq.adidas.exception.MemberKeyExistedException;
import com.chinarewards.ext.api.qq.adidas.service.QQActivityMemberService;
import com.chinarewards.posnet.ext.manager.QQActivityMerberManager;
import com.chinarewards.posnet.ext.util.DESECBUtil;
import com.chinarewards.posnet.ext.util.json.JacksonTypeReference;
import com.chinarewards.posnet.ext.util.json.JsonUtil;
import com.chinarewards.posnet.ext.vo.SynMemberReq;
import com.chinarewards.posnet.ext.vo.SynMemberResp;
import com.chinarewards.posnet.ext.vo.SyncQQMemberKeyJournalVo;
import com.chinarewards.qqgbvpn.domain.event.DomainEntity;
import com.chinarewards.qqgbvpn.domain.event.DomainEvent;
import com.chinarewards.qqgbvpn.logic.journal.JournalLogic;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

public class QQActivityMerberManagerImpl implements QQActivityMerberManager {
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Inject
	QQActivityMemberService memberService;
	
	@Inject
	JournalLogic journalLogic;
	
	/**
	 * 得到异常的堆栈信息
	 * @param e
	 * @return
	 */
	private String getExceptionStackTrace(Exception e){		
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		e.printStackTrace(new PrintWriter(buf, true));
		
		String stackTrace = buf.toString();
		try {
			buf.close();
		} catch (IOException ex) {
			logger.error(ex.getMessage(), ex);
		}
		return stackTrace;
	}
	
	@Transactional
	@Override
	public SynMemberResp generateQQActivityMember(String originStr, final String secretKey) {
		SynMemberResp synMemberResp = null;
		int returncode = -1;
		
		String qqActivityMemberId = "";
		String memberKey = "";
		String timestamp = "";
		Exception exception = null;
		try{
			//decrypt des
			String jsonString = null;
			try {
				jsonString = new String(DESECBUtil.decrypt(
						Base64.decodeBase64(originStr), secretKey));
				logger.debug("decrypt json string:"+ jsonString);
			} catch (Exception e) {
				returncode = 1;
				exception = new Exception(e);
				throw exception;
			}
			
			SynMemberReq synMemberReq = null;
			Date timestampDate = null;
			try {
				//parse json string
				synMemberReq = JsonUtil.parseObject(jsonString,
						new JacksonTypeReference<SynMemberReq>() {
						});
				
				memberKey = synMemberReq.getMemberKey();
				timestamp = synMemberReq.getTimestamp();
				
				//parse date
				timestampDate = new SimpleDateFormat("yyyyMMdd'T'HHmmssZ")
						.parse(synMemberReq.getTimestamp());
			} catch (Throwable e) {
				returncode = 1;
				exception = new Exception(e);
				throw exception;
			}
		
			if (synMemberReq.getMemberKey() == null || "".equals(synMemberReq.getMemberKey())) {
				returncode = 2;
			} else {
				try {
					qqActivityMemberId = memberService
							.generateQQActivityMember(
									synMemberReq.getMemberKey(), timestampDate);
					returncode = 0;
				} catch (MemberKeyExistedException e) {
					returncode = 3;
					exception = new Exception(e);
					throw exception;
				} catch (Exception e) {
					returncode = 4;
					exception = new Exception(e);
					throw exception;
				}
			}
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
			exception = new Exception(e);
		}finally {
			//insert Journal
			SyncQQMemberKeyJournalVo journalVo = new SyncQQMemberKeyJournalVo();
			
			//set errorStackTrace
			if(returncode != 0 && returncode != 2){
				journalVo.setErrorStackTrace(getExceptionStackTrace(exception));
			}
			
			//set rawResponseCtn
			synMemberResp = new SynMemberResp(returncode);
			String rawResponseCtn = "";
			try {
				rawResponseCtn = JsonUtil.formatObject(rawResponseCtn);
			} catch (Throwable e) {
				logger.error(e.getMessage(), e);
			}
			journalVo.setRawResponseCtn(rawResponseCtn);
			
			journalVo.setRawRequestCtn(originStr);
			journalVo.setMemberKey(memberKey);
			journalVo.setTimestamp(timestamp);
			
			
			DomainEvent domainEvent = DomainEvent.SYNC_QQ_MEMBER_KEY_OK;
			if(returncode != 0){
				domainEvent = DomainEvent.SYNC_QQ_MEMBER_KEY_FAILED;
			}
			
			try {
				String eventDetail = JsonUtil.formatObject(journalVo);
				journalLogic.logEvent(domainEvent, DomainEntity.QQ_ACTIVITY_MEMBER,
						qqActivityMemberId, eventDetail);
			} catch (Throwable e) {
				throw new RuntimeException("Exception appear when save journal as obtain qq-adidas privilege", e);
			}
		}
		
		return synMemberResp;
	}

}
