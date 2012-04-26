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

import exception.DataParseException;

public class QQActivityMerberManagerImpl implements QQActivityMerberManager {
	
	protected Logger logger = LoggerFactory.getLogger(QQActivityMerberManagerImpl.class);
	
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
	
	/**
	 * description：parse request parameter timestamp
	 * @param timestamp request parameter timestamp from originStr
	 * @return
	 * @throws DataParseException
	 * @time 2012-4-26   下午01:58:33
	 * @author Seek
	 */
	private Date parseTimestamp(String timestamp)
			throws DataParseException {
		Date timestampDate = null;
		try {
			//parse date
			timestampDate = new SimpleDateFormat("yyyyMMdd'T'HHmmssZ")
					.parse(timestamp);
		} catch (Exception e) {
			throw new DataParseException(e);
		}
		return timestampDate;
	}
	
	/**
	 * description：prase originStr
	 * @param originStr		-	request origin string
	 * @param secretKey		-	secretkey of DES
	 * @return SynMemberReq -	request data vo
	 * @throws DataParseException
	 * @time 2012-4-26   下午01:56:07
	 * @author Seek
	 */
	private SynMemberReq parseData(String originStr, final String secretKey)
			throws DataParseException {
		SynMemberReq synMemberReq = null;

		// decrypt des
		String jsonString = null;
		try {
			jsonString = new String(DESECBUtil.decrypt(
					Base64.decodeBase64(originStr), secretKey));
			logger.debug("decrypt json string:" + jsonString);

			// parse json string
			synMemberReq = JsonUtil.parseObject(jsonString,
					new JacksonTypeReference<SynMemberReq>() {
					});
		} catch (Exception e) {
			throw new DataParseException(e);
		}
		return synMemberReq;
	}
	
	/**
	 * description：save to journal
	 * @param originStr		-	request origin string
	 * @param returncode	-	return code
	 * @param qqActivityMemberId	-	entity id
	 * @param memberKey -	request a field memberkey of originStr
	 * @param timestamp	-	request a field timestamp of originStr
	 * @param exception handle throw exception
	 * @return SynMemberResp	-	response vo
	 * @time 2012-4-26   上午11:54:09
	 * @author Seek
	 */
	private SynMemberResp insertJournal(String originStr, int returncode,
			String qqActivityMemberId, String memberKey, String timestamp,
			Exception exception) {
		SynMemberResp synMemberResp;
		//insert Journal
		SyncQQMemberKeyJournalVo journalVo = new SyncQQMemberKeyJournalVo();
		
		//set errorStackTrace
		if(returncode != SynMemberResp.SUCCESS && returncode != SynMemberResp.PARAM_LACK){
			if(exception instanceof DataParseException){
				journalVo.setErrorStackTrace(DataParseException.class.getName());
			}else {
				journalVo.setErrorStackTrace(getExceptionStackTrace(exception));
			}
		}
		
		//set rawResponseCtn
		synMemberResp = new SynMemberResp(returncode);
		String rawResponseCtn = "";
		try {
			rawResponseCtn = JsonUtil.formatObject(synMemberResp);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
		}
		journalVo.setRawResponseCtn(rawResponseCtn);
		
		journalVo.setRawRequestCtn(originStr);
		journalVo.setMemberKey(memberKey);
		journalVo.setTimestamp(timestamp);
		
		
		DomainEvent domainEvent = DomainEvent.SYNC_QQ_MEMBER_KEY_OK;
		if(returncode != SynMemberResp.SUCCESS){
			domainEvent = DomainEvent.SYNC_QQ_MEMBER_KEY_FAILED;
		}
		
		try {
			String eventDetail = JsonUtil.formatObject(journalVo);
			journalLogic.logEvent(domainEvent, DomainEntity.QQ_ACTIVITY_MEMBER,
					qqActivityMemberId, eventDetail);
		} catch (Exception e) {
			throw new RuntimeException("Exception appear when save journal as obtain qq-adidas privilege", e);
		}
		return synMemberResp;
	}
	
	@Override
	public SynMemberResp generateQQActivityMember(String originStr, final String secretKey) {
		SynMemberResp synMemberResp = null;
		int returncode = -1;
		
		String qqActivityMemberId = "";
		String memberKey = "";
		String timestamp = "";
		Exception exception = null;
		try{
			//parse originStr, decrypt des, base64 decode, json string to object.
			SynMemberReq synMemberReq = parseData(originStr, secretKey);
			//parse timestamp to Date
			Date timestampDate = parseTimestamp(synMemberReq.getTimestamp());
		
			if (synMemberReq.getMemberKey() == null || "".equals(synMemberReq.getMemberKey())) {
				returncode = SynMemberResp.PARAM_LACK;
			} else {
				qqActivityMemberId = memberService
						.generateQQActivityMember(
								synMemberReq.getMemberKey(), timestampDate);
				returncode = SynMemberResp.SUCCESS;
			}
		}catch(MemberKeyExistedException e){
			returncode = SynMemberResp.MEMBER_KEY_REPEAT;
			logger.error(e.getMessage(), e);
			exception = new Exception(e);
		}catch(DataParseException e){
			returncode = SynMemberResp.PARSE_ERR;
			logger.error(e.getMessage(), e);
			exception = new Exception(e);
		}catch(Throwable e) {
			returncode = SynMemberResp.SYS_ERR;
			logger.error(e.getMessage(), e);
			exception = new Exception(e);
		}finally {
			try{
				synMemberResp = insertJournal(originStr, returncode,
						qqActivityMemberId, memberKey, timestamp, exception);
			}catch(Throwable e){
				logger.error("insertJournal method throw Exception : {}", e);
			}
			
		}
		return synMemberResp;
	}

}
