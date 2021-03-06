package tech.xuanwu.northstar.core.engine.rt.event;

import java.util.EventObject;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.constant.CommonConstant;
import tech.xuanwu.northstar.constant.RuntimeEvent;
import tech.xuanwu.northstar.core.persistence.repo.ContractRepo;
import tech.xuanwu.northstar.core.persistence.repo.GatewayRepo;
import tech.xuanwu.northstar.engine.IndexEngine;
import tech.xuanwu.northstar.engine.RuntimeEngine;
import tech.xuanwu.northstar.entity.ContractInfo;
import tech.xuanwu.northstar.entity.GatewayInfo;
import tech.xuanwu.northstar.gateway.GatewayApi;
import xyz.redtorch.pb.CoreEnum.ConnectStatusEnum;
import xyz.redtorch.pb.CoreField.ContractField;

@Slf4j
@Component
public class GatewayReadyEventHandler implements RuntimeEngine.Listener, InitializingBean{

	@Autowired
	private RuntimeEngine rtEngine;
	
	@Autowired
	private ApplicationContext ctx;
	
	@Autowired
	private IndexEngine idxEngine;
	
	@Autowired
	private ContractRepo contractRepo;
	
	@Autowired
	private GatewayRepo gatewayRepo;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		rtEngine.addEventHandler(RuntimeEvent.GATEWAY_READY, this);
	}

	@Override
	public void onEvent(EventObject e) throws Exception {
		String gatewayId = (String) e.getSource();
		
//		GatewayInfo gatewayInfo = gatewayRepo.findGatewayById(gatewayId);
//		gatewayInfo.setStatus(ConnectStatusEnum.CS_Connected);
//		gatewayRepo.upsertById(gatewayInfo);
		
		log.info("=====开始自动续订合约=====");
		//自动续订阅合约
		List<ContractInfo> contractList = contractRepo.getAllSubscribedContracts(gatewayId);
		GatewayApi mktGateway = (GatewayApi) ctx.getBean(CommonConstant.CTP_MKT_GATEWAY);
		for(ContractInfo c : contractList) {
			ContractField contract = c.convertTo();
			if(contract != null) {
				mktGateway.subscribe(contract);
				log.info("订阅网关【{}】的合约【{}】", c.getGatewayId(), c.getSymbol());
			}else {
				log.warn("合约【{}】已过期", c.getSymbol());
				contractRepo.delete(c.getGatewayId(),c.getSymbol());				
			}
		}		
		
		//自动续订指数合约
		idxEngine.onGatewayReady(gatewayId);
		
		log.info("=====自动续订合约完成=====");
	}

}
