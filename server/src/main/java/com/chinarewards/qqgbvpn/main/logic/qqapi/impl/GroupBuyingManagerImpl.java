package com.chinarewards.qqgbvpn.main.logic.qqapi.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chinarewards.qqgbvpn.common.DateTimeProvider;
import com.chinarewards.qqgbvpn.domain.Agent;
import com.chinarewards.qqgbvpn.domain.GrouponCache;
import com.chinarewards.qqgbvpn.domain.PageInfo;
import com.chinarewards.qqgbvpn.domain.Pos;
import com.chinarewards.qqgbvpn.domain.Validation;
import com.chinarewards.qqgbvpn.domain.event.DomainEntity;
import com.chinarewards.qqgbvpn.domain.event.DomainEvent;
import com.chinarewards.qqgbvpn.domain.status.CommunicationStatus;
import com.chinarewards.qqgbvpn.domain.status.ValidationStatus;
import com.chinarewards.qqgbvpn.logic.journal.JournalLogic;
import com.chinarewards.qqgbvpn.main.dao.qqapi.GroupBuyingDao;
import com.chinarewards.qqgbvpn.main.exception.CopyPropertiesException;
import com.chinarewards.qqgbvpn.main.exception.SaveDBException;
import com.chinarewards.qqgbvpn.main.logic.qqapi.GroupBuyingManager;
import com.chinarewards.qqgbvpn.main.vo.ValidateResponseMessageVO;
import com.chinarewards.qqgbvpn.main.vo.ValidationVO;
import com.chinarewards.qqgbvpn.qqapi.exception.MD5Exception;
import com.chinarewards.qqgbvpn.qqapi.exception.ParseXMLException;
import com.chinarewards.qqgbvpn.qqapi.exception.SendPostTimeOutException;
import com.chinarewards.qqgbvpn.qqapi.service.GroupBuyingService;
import com.chinarewards.qqgbvpn.qqapi.vo.GroupBuyingSearchListVO;
import com.chinarewards.qqgbvpn.qqapi.vo.GroupBuyingValidateResultVO;
import com.chinarewards.utils.StringUtil;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;

public class GroupBuyingManagerImpl implements GroupBuyingManager {
	
	Logger log = LoggerFactory.getLogger(getClass());
	
	@Inject   
	private Provider<GroupBuyingService> service;
	
	@Inject
	private Provider<GroupBuyingDao> dao;
	
	@Inject
	private JournalLogic journalLogic;
	
	@Inject
	private DateTimeProvider dtProvider;
	
	private final static String SUCCESS_CODE = "0";
	
	private final int ERROR_CODE_THER = 999; 
	
	private final int RESULT_SUCCESS_CODE = 0; 
	
	private final int SIGNEDINTMAXVALUE  = 32767; 
	
	
	/**
	 * 初始化团购商品缓存
	 * 
	 * @author iori
	 * @param params
	 * @return
	 * @throws CopyPropertiesException 
	 */
	@Transactional
	public String initGrouponCache(HashMap<String, String> params)
			throws MD5Exception, ParseXMLException, SendPostTimeOutException,
			JsonGenerationException, SaveDBException, CopyPropertiesException {
		//调用腾讯的接口查询商品列表
		HashMap<String, Object> map = service.get().groupBuyingSearch(params);
		map.putAll(params);
		//处理缓存
		String posId = (String)map.get("posId");
		Date date = dtProvider.getTime();
		ObjectMapper mapper = new ObjectMapper();
		String resultCode = (String) map.get("resultCode");
		//商品缓存列表
		List<GrouponCache> oldCache = new ArrayList<GrouponCache>();
		List<GroupBuyingSearchListVO> items = (List<GroupBuyingSearchListVO>) map.get("items");
		//新商品列表
		List<GrouponCache> grouponCacheList = new ArrayList<GrouponCache>();
		//查询成功，才处理商品列表
		if (SUCCESS_CODE.equals(resultCode)) {
			if (items != null && items.size() > 0) {
				for (GroupBuyingSearchListVO vo : items) {
					GrouponCache grouponCache = new GrouponCache();
					grouponCache.setPosId(posId);
					grouponCache.setCreateDate(date);
					grouponCache.setResultCode(resultCode);
					try {
						BeanUtils.copyProperties(grouponCache, vo);
					} catch (Exception e) {
						throw new CopyPropertiesException(e);
					}
					grouponCacheList.add(grouponCache);
				}
			}
		}
		
		String initEventDetail = "";
		try {
			if (grouponCacheList != null && grouponCacheList.size() > 0) {
				initEventDetail = mapper.writeValueAsString(grouponCacheList);
			}
		} catch (Exception e) {
			throw new JsonGenerationException(e);
		}
		
		String delEventDetail = "";
		try {
			//删除缓存
			oldCache = dao.get().deleteGrouponCache(posId);
			try {
				if (oldCache != null && oldCache.size() > 0) {
					delEventDetail = mapper.writeValueAsString(oldCache);
				}
			} catch (Exception e) {
				throw new JsonGenerationException(e);
			}
			//保存删除缓存日志
			journalLogic.logEvent(DomainEvent.GROUPON_CACHE_DELETE.toString(),
					DomainEntity.GROUPON_CACHE.toString(), posId, delEventDetail);
			if (SUCCESS_CODE.equals(resultCode)) {
				//保存商品缓存
				if (grouponCacheList != null && grouponCacheList.size() > 0) {
					for (GrouponCache vo : grouponCacheList) {
						dao.get().saveGrouponCache(vo);
					}
				} else {
					//当result code是0，但商品列表为空，则只保存一个记录result code的数据
					GrouponCache emptyCache = new GrouponCache();
					emptyCache.setPosId(posId);
					emptyCache.setCreateDate(date);
					emptyCache.setResultCode(resultCode);
					dao.get().saveGrouponCache(emptyCache);
					//这种情况应该是少有的，日志的detail中记录的是result code
					//说明查询成功，但商品为空
					initEventDetail = resultCode;
				}
			} else {
				//如果result code不为0，保存一个只有posId和result code的缓存记录
				//用于查询时获取result code
				GrouponCache errorCache = new GrouponCache();
				errorCache.setPosId(posId);
				errorCache.setCreateDate(date);
				errorCache.setResultCode(resultCode);
				dao.get().saveGrouponCache(errorCache);
				
				//当result code不为0时，日志的detail中记录的是result code
				initEventDetail = resultCode;
			}
			//保存商品日志
			journalLogic.logEvent(DomainEvent.GROUPON_CACHE_INIT.toString(),
					DomainEntity.GROUPON_CACHE.toString(), posId, initEventDetail);
		} catch (Exception e) {
			//记日志
			log.error("groupon cache init error");
			log.error("ts: " + date);
			log.error("old groupon cache information:");
			log.error("entity: " + DomainEntity.GROUPON_CACHE.toString());
			log.error("entityId: " + posId);
			log.error("event: " + DomainEvent.GROUPON_CACHE_DELETE.toString());
			log.error("eventDetail: " + delEventDetail);
			log.error("new groupon information:");
			log.error("entity: " + DomainEntity.GROUPON_CACHE.toString());
			log.error("entityId: " + posId);
			log.error("event: " + DomainEvent.GROUPON_CACHE_INIT.toString());
			log.error("eventDetail: " + initEventDetail);
			throw new SaveDBException(e);
		}
		
		return resultCode;
	}

	  
    /**
	 * 团购查询
	 * 
	 * @author iori
	 * @param params
	 * @return
     * @throws SendPostTimeOutException 
     * @throws ParseXMLException 
     * @throws MD5Exception 
     * @throws SaveDBException 
     * @throws JsonGenerationException 
	 */
	@Transactional
	public HashMap<String, Object> groupBuyingSearch(
			HashMap<String, String> params) throws MD5Exception, ParseXMLException, SendPostTimeOutException, JsonGenerationException, SaveDBException {
		Date date = dtProvider.getTime();
		HashMap<String, Object> relustMap = new HashMap<String, Object>();
		
		String posId = params.get("posId");
		
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageId(Integer.valueOf(params.get("curpage")));
		pageInfo.setPageSize(Integer.valueOf(params.get("pageSize")));
		
		//分页查询商品
		pageInfo = dao.get().getGrouponCachePagination(pageInfo,posId);
		
		//缓存商品列表
		List<GrouponCache> cacheList = (List<GrouponCache>) pageInfo.getItems();
		
		//获取result code
		String resultCode = getResultCode(cacheList);
		
		ObjectMapper mapper = new ObjectMapper();
		String eventDetail = "";
		if (cacheList != null && cacheList.size() > 0) {
			try {
				eventDetail = mapper.writeValueAsString(cacheList);
			} catch (Exception e) {
				throw new JsonGenerationException(e);
			}
		}
		
		try {
			//记查询日志
			journalLogic.logEvent(DomainEvent.POS_PRODUCT_SEARCH.toString(),
					DomainEntity.GROUPON_CACHE.toString(), posId, eventDetail);
		} catch (Exception e) {
			log.error("group buying search save journal error");
			log.error("ts: " + date);
			log.error("entity: " + DomainEntity.GROUPON_CACHE.toString());
			log.error("entityId: " + posId);
			log.error("event: " + DomainEvent.POS_PRODUCT_SEARCH.toString());
			log.error("eventDetail: " + eventDetail);
			throw new SaveDBException(e);
		}
		
		//这个处理，是为了将记录result code，但商品为空的数据删除掉
		if (cacheList != null && cacheList.size() > 0) {
			GrouponCache cache = cacheList.get(0);
			if (StringUtil.isEmptyString(cache.getGrouponId())) {
				pageInfo.setPageId(1);
				pageInfo.setRecordCount(0);
				pageInfo.setPageCount(0);
				pageInfo.setItems(new ArrayList<GrouponCache>());
			}
		}
		
		relustMap.put("resultCode", resultCode);
		relustMap.put("pageInfo", pageInfo);
		return relustMap;
	}
	
	/**
	 * 根据缓存商品列表取result code,默认为0
	 * @author iori
	 * @param posId
	 * @return
	 */
	private String getResultCode(List<GrouponCache> cacheList) {
		String resultCode = SUCCESS_CODE;
		if (cacheList != null && cacheList.size() > 0) {
			GrouponCache cache = cacheList.get(0);
			resultCode = cache.getResultCode();
		}
		return resultCode;
	}

	/**
	 * 团购验证
	 * 
	 * @author iori
	 * @param params
	 * @return
	 * @throws SendPostTimeOutException 
	 * @throws ParseXMLException 
	 * @throws MD5Exception 
	 * @throws SaveDBException 
	 */
	@Transactional
	public HashMap<String, Object> groupBuyingValidate(
			HashMap<String, String> params) throws MD5Exception, ParseXMLException, SendPostTimeOutException, SaveDBException {
		//调用腾讯接口验证团购
		HashMap<String, Object> map = service.get().groupBuyingValidate(params);
		map.putAll(params);
		//处理后台验证结果
		String posId = (String) map.get("posId");
		String resultCode = (String) map.get("resultCode");
		String token = (String) map.get("token");
		String grouponId = (String) map.get("grouponId");
		//查询POS机
		Pos pos = dao.get().getPosByPosId(posId);
		List<GroupBuyingValidateResultVO> items = (List<GroupBuyingValidateResultVO>) map.get("items");
		String resultStatus = "";
		String resultName = "";
		String resultExplain = "";
		String currentTime = "";
		String useTime = "";
		String validTime = "";
		String refundTime = "";
		if (items != null && items.size() > 0) {
			GroupBuyingValidateResultVO item = items.get(0);
			resultStatus = item.getResultStatus();
			resultName = item.getResultName();
			resultExplain = item.getResultExplain();
			currentTime = item.getCurrentTime();
			useTime = item.getUseTime();
			validTime = item.getValidTime();
			refundTime = item.getRefundTime();
		}
		//查询第三方
		Agent agent = dao.get().getAgentByPosId(posId);
		//保证验证的POS存在，并且绑定了第三方
		if (pos != null && agent != null) {
			Validation validation = new Validation();
			Date date = dtProvider.getTime();
			String event = SUCCESS_CODE.equals(resultCode) ? DomainEvent.POS_ORDER_VALIDATED_OK.toString() : DomainEvent.POS_ORDER_VALIDATED_FAILED.toString();
			String eventDetail = "";
			try {
				validation.setTs(date);
				validation.setVcode(token);
				validation.setPcode(grouponId);
				validation.setPosId(pos.getPosId());
				validation.setPosModel(pos.getModel());
				validation.setPosSimPhoneNo(pos.getSimPhoneNo());
				validation.setStatus(SUCCESS_CODE.equals(resultStatus) ? ValidationStatus.SUCCESS : ValidationStatus.FAILED);
				validation.setCstatus(SUCCESS_CODE.equals(resultCode) ? CommunicationStatus.SUCCESS : CommunicationStatus.FAILED);
				validation.setResultStatus(resultStatus);
				validation.setResultName(resultName);
				validation.setResultExplain(resultExplain);
				validation.setCurrentTime(currentTime);
				validation.setUseTime(useTime);
				validation.setValidTime(validTime);
				validation.setRefundTime(refundTime);
				validation.setAgentId(agent.getId());
				validation.setAgentName(agent.getName());
				//保存验证信息
				dao.get().saveValidation(validation);
				
				ObjectMapper mapper = new ObjectMapper();
				if (SUCCESS_CODE.equals(resultCode) && map.get("items") != null) {
					eventDetail = mapper.writeValueAsString(validation);
				} else {
					eventDetail = resultCode;
				}
				//记录保存日志
				journalLogic.logEvent(event, DomainEntity.VALIDATION.toString(), posId, eventDetail);
			} catch (Exception e) {
				log.error("group buying validate save error");
				log.error("posId: " + posId);
				log.error("ts: " + date);
				log.error("token: " + token);
				log.error("grouponId" + grouponId);
				log.error("entity: " + DomainEntity.VALIDATION.toString());
				log.error("entityId: " + validation.getId());
				log.error("event: " + event);
				log.error("eventDetail: " + eventDetail);
				throw new SaveDBException(e);
			}
		} else {
			log.error("group buying validate get pos or agent error");
			log.error("pos or agent not found by posId : " + posId);
			throw new SaveDBException("group buying validate error,pos or agent not found by posId : " + posId);
		}
		
		return map;
	}


	@Override
	public GroupBuyingValidateResultVO groupBuyingValidateLocal(String grouponId,
			String grouponVCode)throws SaveDBException,JsonGenerationException{
		return dao.get().groupBuyingValidateLocal(grouponId, grouponVCode);
	}


	@Override
	public void createValidateResultLocal(String grouponId,String grouponVCode,
			GroupBuyingValidateResultVO groupBuyingValidateResultVO)
			throws SaveDBException,JsonGenerationException{
		dao.get().createValidateResultLocal(grouponId, grouponVCode, groupBuyingValidateResultVO);
	}


	@Override
	public void groupBuyValidateCallBack(String grouponId, String grouponVCode)
			throws SaveDBException {
		dao.get().groupBuyValidateCallBack(grouponId, grouponVCode);
	}


	@Override
	public ValidationVO getValidationByPcodeVcodeLastTs(String pcode, String vcode)
			throws SaveDBException, JsonGenerationException {
		
		return dao.get().getValidationByPcodeVcodeLastTs(pcode, vcode);
	}
	
	@Override
	public ValidationVO getValidationByPcodeVcodeFirstTs(String pcode, String vcode)
			throws SaveDBException, JsonGenerationException {
		
		return dao.get().getValidationByPcodeVcodeFirstTs(pcode, vcode);
	}

	@Override
	public void createValidation(String posId, ValidationVO validationVo)
			throws SaveDBException, JsonGenerationException {
		
		Pos pos = dao.get().getPosByPosId(posId);
		Agent agent = dao.get().getAgentByPosId(posId);
		
		if(pos != null && agent != null && validationVo != null){
			Validation validation = new Validation();
			Date date = dtProvider.getTime();
			String eventDetail = "";
			String event = validationVo.getStatus().equals(CommunicationStatus.SUCCESS) ? DomainEvent.POS_ORDER_VALIDATED_OK.toString() : DomainEvent.POS_ORDER_VALIDATED_FAILED.toString();
			
			validation.setTs(date);
			validation.setVcode(validationVo.getVcode());
			validation.setPcode(validationVo.getPcode());
			validation.setPosId(pos.getPosId());
			validation.setPosModel(pos.getModel());
			validation.setPosSimPhoneNo(pos.getSimPhoneNo());
			validation.setStatus(validationVo.getStatus());
			validation.setCstatus(validationVo.getCstatus());
			validation.setResultStatus(validationVo.getResultStatus());
			validation.setResultName(validationVo.getResultName());
			validation.setResultExplain(validationVo.getResultExplain());
			validation.setCurrentTime(validationVo.getCurrentTime());
			validation.setUseTime(validationVo.getUseTime());
			validation.setValidTime(validationVo.getValidTime());
			validation.setRefundTime(validationVo.getRefundTime());
			validation.setAgentId(agent.getId());
			validation.setAgentName(agent.getName());
			// 保存验证信息
			dao.get().saveValidation(validation);

			ObjectMapper mapper = new ObjectMapper();
			try {
				eventDetail = mapper.writeValueAsString(validation);

			} catch (Exception e) {
				log.error("journalLogic save error");
				log.error("posId: " + posId);
				log.error("ts: " + date);
				log.error("token: " + validationVo.getVcode());
				log.error("grouponId" + validationVo.getPcode());
				log.error("entity: " + DomainEntity.VALIDATION.toString());
				log.error("entityId: " + validation.getId());
				log.error("event: " + event);
				log.error("eventDetail: " + eventDetail);
				throw new SaveDBException(e);
			}
	
			//记录保存日志
			journalLogic.logEvent(event, DomainEntity.VALIDATION.toString(), posId, eventDetail);
		}else{
			throw new SaveDBException("create validate error,validationVo is null or pos or agent not found by posId : " + posId);
		}
	}


	@Override
	public int getValidationCountByPcodeVcode(String pcode, String vcode)
			throws SaveDBException, JsonGenerationException {
		return dao.get().getValidationCountByPcodeVcode(pcode, vcode);
	}


	@SuppressWarnings("unchecked")
	@Override
	public ValidateResponseMessageVO qqgbvValidationCommand(
			HashMap<String, String> postParams) throws SaveDBException,JsonGenerationException{
		
		ValidateResponseMessageVO validateResponseMessage = new ValidateResponseMessageVO();
		try {
			// 验证首先检查是否通过本地验证（就是验证结果表GroupBuyingValidateResult里面是否还存在同一个团购同一个验证码的数据）
			GroupBuyingValidateResultVO groupBuyingValidateResultVO = dao.get().groupBuyingValidateLocal(
					postParams.get("grouponId"), postParams.get("token"));
			// 如果存在一条需要验证的数据说明，上一次验证失败（失败的原因有：可能是验证请求回复POS机失败，或者验证后的ACK请求失败）
			if (groupBuyingValidateResultVO != null) {
				
				log.debug("validate to dup.....");
				
				// 从validate表里面获取上一次验证和第一次验证这个团购的数据然后回复给POS机
				ValidationVO lastValidationVo = dao.get().getValidationByPcodeVcodeLastTs(
						postParams.get("grouponId"), postParams.get("token"));
				ValidationVO firstValidationVo = dao.get().getValidationByPcodeVcodeFirstTs(
						postParams.get("grouponId"), postParams.get("token"));
				
				// 查询在该次验证前验证了多少次
				int validateCount = dao.get().getValidationCountByPcodeVcode(
						postParams.get("grouponId"), postParams.get("token"));
				
				// 因为要得到当前的验证次数，所以要加上1
				int currCount = validateCount + 1;
				log.debug("validate count:{}", currCount);
				if (currCount > SIGNEDINTMAXVALUE) {
					currCount = SIGNEDINTMAXVALUE;
				}
				
				validateResponseMessage.setValidate_count(currCount);
				validateResponseMessage.setPrev_posId(lastValidationVo.getPosId());
				validateResponseMessage.setFirst_posId(firstValidationVo.getPosId());
				validateResponseMessage.setQqws_resultcode(0);
				validateResponseMessage.setQqvalidate_resultstatus(Long.parseLong(firstValidationVo.getResultStatus()));
				validateResponseMessage.setResultName(groupBuyingValidateResultVO.getResultName());
				validateResponseMessage.setCurrentTime(groupBuyingValidateResultVO.getCurrentTime());
				validateResponseMessage.setResultExplain(groupBuyingValidateResultVO.getResultExplain());
				validateResponseMessage.setUseTime(groupBuyingValidateResultVO.getUseTime());
				validateResponseMessage.setValidTime(groupBuyingValidateResultVO.getValidTime());
				validateResponseMessage.setPrev_validate_time(lastValidationVo.getTs());
				validateResponseMessage.setFirst_validate_time(firstValidationVo.getTs());

				// 每一次验证都要插入一条验证的数据，作为历史记录
				// 为了区分相对腾讯来说的验证成功，和针对我们自己服务器的成功这里添加了一个成功的状态
				// 腾讯成功是SUCCESS 我们自己的服务器成功是SUCCESS_DUP
				lastValidationVo.setStatus(ValidationStatus.SUCCESS_DUP);
				this.createValidation(postParams.get("posId"), lastValidationVo);
			}
			// 这个团购这个验证码第一次验证，请求腾讯服务器验证。
			else {
				log.debug("ValidateCommand to tx...");
				HashMap<String, Object> result = this.groupBuyingValidate(postParams);
				int resultCode = Integer.valueOf((String) result.get("resultCode"));
				log.debug("resultCode: {}", resultCode);
				if (resultCode == RESULT_SUCCESS_CODE) {
					List<GroupBuyingValidateResultVO> items = (List<GroupBuyingValidateResultVO>) result.get("items");

					validateResponseMessage.setQqws_resultcode(0);
					validateResponseMessage.setQqvalidate_resultstatus(0);

					for (GroupBuyingValidateResultVO item : items) {
						log.debug("item.getResultStatus(): [{}]",
								item.getResultStatus());
						// 腾讯那边说resultStatus非0是验证通过
						if (!SUCCESS_CODE.equals(item.getResultStatus())) {
							validateResponseMessage
									.setQqvalidate_resultstatus(Long
											.parseLong(item.getResultStatus()));
						} else {
							// 创建本地验证记录用来备份，如果这次验证失败可以第二次验证时不用访问腾讯服务器，这条记录会在某一次验证成功后的ACK请求中删除
							log.debug("ValidateCommand create groupBuyingValidateResult");
							dao.get().createValidateResultLocal(postParams.get("grouponId"), postParams.get("token"), item);
						}

						ValidationVO firstValidationVo = dao.get().getValidationByPcodeVcodeFirstTs(
										postParams.get("grouponId"),postParams.get("token"));

						validateResponseMessage.setValidate_count(1);
						validateResponseMessage.setResultName(item.getResultName());
						validateResponseMessage.setCurrentTime(item.getCurrentTime());
						validateResponseMessage.setResultExplain(item.getResultExplain());
						validateResponseMessage.setUseTime(item.getUseTime());
						validateResponseMessage.setValidTime(item.getValidTime());
						validateResponseMessage.setPrev_posId(null);
						validateResponseMessage.setFirst_posId(postParams.get("posId"));
						validateResponseMessage.setPrev_validate_time(null);
						validateResponseMessage.setFirst_validate_time(firstValidationVo.getTs());
					}
				} else {
					switch (resultCode) {
					case -1:
						log.debug("error==:服务器繁忙");
						validateResponseMessage.setQqws_resultcode(resultCode);
						break;
					case -2:
						log.debug("error==:md5校验失败");
						validateResponseMessage.setQqws_resultcode(resultCode);
						break;
					case -3:
						log.debug("error==:没有权限");
						validateResponseMessage.setQqws_resultcode(resultCode);
						break;
					default:
						log.debug("error==:未知错误");
						validateResponseMessage.setQqws_resultcode(resultCode);
						break;
					}
				}
			}

		} catch (JsonGenerationException e) {
			log.error("error==:生成JSON对象出错", e);
			validateResponseMessage.setQqws_resultcode(ERROR_CODE_THER);
		} catch (MD5Exception e) {
			log.error("error==:生成MD5校验位出错", e);
			validateResponseMessage.setQqws_resultcode(ERROR_CODE_THER);
		} catch (ParseXMLException e) {
			log.error("error==:解析XML出错", e);
			validateResponseMessage.setQqws_resultcode(ERROR_CODE_THER);
		} catch (SendPostTimeOutException e) {
			log.error("error==:POST连接出错", e);
			validateResponseMessage.setQqws_resultcode(ERROR_CODE_THER);
		} catch (SaveDBException e) {
			log.error("error==:后台保存数据库出错 ", e);
			validateResponseMessage.setQqws_resultcode(ERROR_CODE_THER);
		} catch (Throwable e) {
			log.error("error==:", e);
			validateResponseMessage.setQqws_resultcode(ERROR_CODE_THER);
		}

		return validateResponseMessage;
	}

}
