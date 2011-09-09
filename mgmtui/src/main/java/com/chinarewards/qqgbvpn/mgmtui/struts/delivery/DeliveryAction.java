package com.chinarewards.qqgbvpn.mgmtui.struts.delivery;

import java.util.List;

import org.apache.struts2.ServletActionContext;

import com.chinarewards.qqgbvpn.domain.PageInfo;
import com.chinarewards.qqgbvpn.mgmtui.exception.DeliveryWithWrongStatusException;
import com.chinarewards.qqgbvpn.mgmtui.exception.PosNotExistException;
import com.chinarewards.qqgbvpn.mgmtui.exception.PosWithWrongStatusException;
import com.chinarewards.qqgbvpn.mgmtui.logic.agent.AgentLogic;
import com.chinarewards.qqgbvpn.mgmtui.logic.pos.DeliveryLogic;
import com.chinarewards.qqgbvpn.mgmtui.model.agent.AgentStore;
import com.chinarewards.qqgbvpn.mgmtui.model.agent.AgentVO;
import com.chinarewards.qqgbvpn.mgmtui.model.delivery.DeliveryNoteDetailVO;
import com.chinarewards.qqgbvpn.mgmtui.model.delivery.DeliveryNoteVO;
import com.chinarewards.qqgbvpn.mgmtui.model.util.PaginationTools;
import com.chinarewards.qqgbvpn.mgmtui.struts.BasePagingToolBarAction;
import com.google.inject.Injector;

/**
 * delivery action
 * 
 * @author Seek	
 *
 */
public class DeliveryAction extends BasePagingToolBarAction {
	
	private static final long serialVersionUID = 6936323925311944355L;
	
	private DeliveryLogic deliveryLogic;
	
	private AgentLogic agentLogic;
	
	private List<DeliveryNoteVO> deliveryNoteVOList;
	
	private DeliveryNoteVO deliveryNoteVO;
	
	private List<DeliveryNoteDetailVO> deliveryNoteDetailVOList;
	
	private List<AgentVO> agentVOList;
	
	private String deliveryId;
	
	//POS NUM
	private String posNum;
	
	private DeliveryNoteDetailVO deliveryNoteDetailVO;
	
	private String agentId;
	
	private Boolean processSuccess;
	
	private String deliveryNoteDetailId;
	
	private String id;
	
	private Integer addPosStatus;
	
	@Override
	public String execute(){
		if(super.getCurrentPage()==0){
			super.setCurrentPage(1);
		}
		
		try{
			//查询所有的第三方合作伙伴
			AgentStore agentStore = getAgentLogic().queryAgent(null);
			agentVOList = agentStore.getAgentVOList();
		}catch(Throwable e){
			log.error(e.getMessage(), e);
			return ERROR;
		}
		return SUCCESS;
	}
	
	/**
	 * description：交付单列表
	 * @time 2011-9-7   上午10:40:00
	 * @author Seek
	 */
	public String deliveryList(){
		PaginationTools paginationTools = new PaginationTools();
		paginationTools.setCountOnEachPage(super.getPageSize());
		paginationTools.setStartIndex( (super.getCurrentPage()-1) * super.getPageSize() );
		
		try{
			PageInfo<DeliveryNoteVO> pageInfo = getDeliveryLogic().fetchDeliveryList(paginationTools);
			
			deliveryNoteVOList = pageInfo.getItems();
			super.setCountTotal(pageInfo.getRecordCount());
			
			//template
			final String urlMark = "{*}";
			String urlTemplate = super.buildURLTemplate(super.getCurrentPath(), "&", "currentPage=", urlMark);
			
			setUrlTemplate(urlTemplate);
			setUrlMark(urlMark);
		}catch(Throwable e){
			log.error(e.getMessage(), e);
			return ERROR;
		}
		return SUCCESS;
	}
	
	/**
	 * description：创建一个交付单
	 * @return
	 * @time 2011-9-7   下午05:26:37
	 * @author Seek
	 */
	public String createDeliveryNote(){
		try{
			DeliveryNoteVO aDeliveryNoteVO = getDeliveryLogic().createDeliveryNote();
			deliveryId = aDeliveryNoteVO.getId();
		}catch(Throwable e){
			log.error(e.getMessage(), e);
			return ERROR;
		}
		return SUCCESS;
	}
	
	/**
	 * description：显示添加pos机
	 * @return
	 * @time 2011-9-7   下午03:09:10
	 * @author Seek
	 */
	public String showAddPosForDelivery(){
		try{
			//查询所有的第三方合作伙伴
			AgentStore agentStore = getAgentLogic().queryAgent(null);
			agentVOList = agentStore.getAgentVOList();
			
			if(deliveryId != null){
				deliveryNoteDetailVOList = getDeliveryLogic().fetchDetailListByNoteId(deliveryId);
				deliveryNoteVO = getDeliveryLogic().fetchById(deliveryId);
				
				if(!"DRAFT".equals(deliveryNoteVO.getStatus())){
					return INPUT;
				}
				log.debug("deliveryNoteVO.number:"+deliveryNoteVO.getDnNumber());
			}
		}catch(Throwable e){
			log.error(e.getMessage(), e);
			return ERROR;
		}
		return SUCCESS;
	}
	
	/**
	 * description：添加第三方到交付单
	 * @return
	 * @time 2011-9-7   下午04:11:30
	 * @author Seek
	 */
	public String addAgentForDelivery(){
		try{
			if(agentId != null && agentId.length() == 0){
				agentId = null;
			}
			
			getDeliveryLogic().associateAgent(deliveryId, agentId);
			processSuccess = true;
		}catch(DeliveryWithWrongStatusException e){
			log.error(e.getMessage(), e);
			processSuccess = false;
			errorMsg = "非草稿状态的交付单不允许进行该操作";
			return ERROR;
		}catch(Throwable e){
			log.error(e.getMessage(), e);
			processSuccess = false;
		}
		return SUCCESS;
	}
	
	/**
	 * description：添加Pos到交付单
	 * @time 2011-9-7   下午03:58:09
	 * @author Seek
	 */
	public String addPosForDelivery(){
		log.debug("addPosForDelivery() run...");
		log.debug("deliveryId:"+deliveryId);
		log.debug("posNum:"+posNum);
		try{
			deliveryNoteDetailVO = getDeliveryLogic().appendPosToNote(deliveryId, posNum);
			addPosStatus = 0;
		}catch(DeliveryWithWrongStatusException e){
			log.error(e.getMessage(), e);
			errorMsg = "非草稿状态的交付单不允许进行该操作";
			addPosStatus = 3;
			return ERROR;
		}catch(PosNotExistException e){
			addPosStatus = 1;
			log.error(e.getMessage(), e);
		}catch(PosWithWrongStatusException e){
			addPosStatus = 2;
			log.error(e.getMessage(), e);
		}catch(Throwable e){
			log.error(e.getMessage(), e);
			return ERROR;
		}
		return SUCCESS;
	}
	
	/**
	 * description：从交付单 移除Pos
	 * @return
	 * @time 2011-9-7   下午04:40:54
	 * @author Seek
	 */
	public String removePosForDelivery(){
		try{
			getDeliveryLogic().deletePosFromNote(deliveryId, deliveryNoteDetailId);
		}catch(DeliveryWithWrongStatusException e){
			log.error(e.getMessage(), e);
			errorMsg = "非草稿状态的交付单不允许进行该操作";
			return ERROR;
		}catch(Throwable e){
			log.error(e.getMessage(), e);
			return ERROR;
		}
		return SUCCESS;
	}
	
	/**
	 * description：等待POS机 Init
	 * @return
	 * @time 2011-9-7   下午07:22:25
	 * @author Seek
	 */
	public String showWaitPosInitDelivery(){
		try{
			deliveryNoteVO = getDeliveryLogic().fetchById(deliveryId);
			if(!"DRAFT".equals(deliveryNoteVO.getStatus())){
				log.warn("deliveryNoteVO.getStatus() is not DRAFT!");
				errorMsg = "非草稿状态的交付单不允许进行该操作";
				return ERROR;
			}
			
			deliveryNoteDetailVOList = getDeliveryLogic().delivery(deliveryId);
		}catch(Throwable e){
			log.error(e.getMessage(), e);
			return ERROR;
		}
		return SUCCESS;
	}
	
	/**
	 * description：显示交付单等待确认
	 * @return
	 * @time 2011-9-7   下午08:02:00
	 * @author Seek
	 */
	public String showDeliveryWaitConfirm(){
		try{
			deliveryNoteVO  = getDeliveryLogic().fetchById(deliveryId);
			if(!"DRAFT".equals(deliveryNoteVO.getStatus())){
				log.warn("deliveryNoteVO.getStatus() is not DRAFT!");
				errorMsg = "非草稿状态的交付单不允许进行该操作";
				return ERROR;
			}
			
			deliveryNoteDetailVOList = getDeliveryLogic().fetchDetailListByNoteId(deliveryId);
		}catch(Throwable e){
			log.error(e.getMessage(), e);
			return ERROR;
		}
		return SUCCESS;
	}
	
	/**
	 * description：确认交付单
	 * @return
	 * @time 2011-9-8   下午06:28:01
	 * @author Seek
	 */
	public String confirmDelivery(){
		try{
			deliveryNoteVO  = getDeliveryLogic().fetchById(deliveryId);
			if(!"DRAFT".equals(deliveryNoteVO.getStatus())){
				log.warn("deliveryNoteVO.getStatus() is DRAFT!");
				errorMsg = "非草稿状态的交付单不允许进行该操作";
				return ERROR;
			}
			
			//confirm
			getDeliveryLogic().confirmDelivery(deliveryId);	
		}catch(Throwable e){
			log.error(e.getMessage(), e);
			return ERROR;
		}
		return SUCCESS;
	}
	
	/**
	 * description：显示交付单已经确认
	 * @return
	 * @time 2011-9-8   下午07:00:27
	 * @author Seek
	 */
	public String showDeliveryConfirmDone(){
		try{
			if(deliveryId != null){
				deliveryNoteVO  = getDeliveryLogic().fetchById(deliveryId);
				deliveryNoteDetailVOList = getDeliveryLogic().fetchDetailListByNoteId(deliveryId);
			}
		}catch(Throwable e){
			log.error(e.getMessage(), e);
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String printDelivery(){
		try{
			deliveryNoteVO  = getDeliveryLogic().fetchById(id);
			deliveryNoteDetailVOList = getDeliveryLogic().fetchDetailListByNoteId(id);
			
			if("DRAFT".equals(deliveryNoteVO.getStatus())){
				log.warn("deliveryNoteVO.getStatus() is DRAFT!");
				errorMsg = "不能打印草稿状态的交付单";
				return ERROR;
			}
			//print
			getDeliveryLogic().printDelivery(id);
		}catch(Throwable e){
			log.error(e.getMessage(), e);
			return ERROR;
		}
		return SUCCESS;
	}

	public String deleteDelivery(){
		try{
			getDeliveryLogic().deleteDeliveryNote(id);
		}catch(DeliveryWithWrongStatusException e1){
			log.error(e1.getMessage(), e1);
			errorMsg = "不能删除非草稿状态的交付单";
			return ERROR;
		}catch(Throwable e){
			log.error(e.getMessage(), e);
			return ERROR;
		}
		return SUCCESS;
	}
	
	//---------------------------------------------------//
	
	private AgentLogic getAgentLogic() {
		Injector injector = (Injector)ServletActionContext.getServletContext().getAttribute(Injector.class.getName());
		agentLogic = injector.getInstance(AgentLogic.class);
		return agentLogic;
	}
	
	private DeliveryLogic getDeliveryLogic() {
		Injector injector = (Injector)ServletActionContext.getServletContext().getAttribute(Injector.class.getName());
		deliveryLogic = injector.getInstance(DeliveryLogic.class);
		return deliveryLogic;
	}
	
	public List<DeliveryNoteVO> getDeliveryNoteVOList() {
		return deliveryNoteVOList;
	}
	
	public void setDeliveryNoteVOList(List<DeliveryNoteVO> deliveryNoteVOList) {
		this.deliveryNoteVOList = deliveryNoteVOList;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getDeliveryId() {
		return deliveryId;
	}

	public void setDeliveryId(String deliveryId) {
		this.deliveryId = deliveryId;
	}
	
	public List<DeliveryNoteDetailVO> getDeliveryNoteDetailVOList() {
		return deliveryNoteDetailVOList;
	}

	public void setDeliveryNoteDetailVOList(
			List<DeliveryNoteDetailVO> deliveryNoteDetailVOList) {
		this.deliveryNoteDetailVOList = deliveryNoteDetailVOList;
	}
	
	public List<AgentVO> getAgentVOList() {
		return agentVOList;
	}

	public void setAgentVOList(List<AgentVO> agentVOList) {
		this.agentVOList = agentVOList;
	}
	
	public DeliveryNoteVO getDeliveryNoteVO() {
		return deliveryNoteVO;
	}

	public void setDeliveryNoteVO(DeliveryNoteVO deliveryNoteVO) {
		this.deliveryNoteVO = deliveryNoteVO;
	}
	
	public String getPosNum() {
		return posNum;
	}

	public void setPosNum(String posNum) {
		this.posNum = posNum;
	}
	
	public DeliveryNoteDetailVO getDeliveryNoteDetailVO() {
		return deliveryNoteDetailVO;
	}

	public void setDeliveryNoteDetailVO(DeliveryNoteDetailVO deliveryNoteDetailVO) {
		this.deliveryNoteDetailVO = deliveryNoteDetailVO;
	}
	
	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}
	
	public Boolean getProcessSuccess() {
		return processSuccess;
	}

	public void setProcessSuccess(Boolean processSuccess) {
		this.processSuccess = processSuccess;
	}
	
	public String getDeliveryNoteDetailId() {
		return deliveryNoteDetailId;
	}

	public void setDeliveryNoteDetailId(String deliveryNoteDetailId) {
		this.deliveryNoteDetailId = deliveryNoteDetailId;
	}
	
	public Integer getAddPosStatus() {
		return addPosStatus;
	}

	public void setAddPosStatus(Integer addPosStatus) {
		this.addPosStatus = addPosStatus;
	}
	
}
