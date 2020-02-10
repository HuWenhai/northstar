package tech.xuanwu.northstar.core.engine.rt.event;

import java.util.EventObject;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.constant.EventType;
import tech.xuanwu.northstar.domain.IAccount;
import tech.xuanwu.northstar.engine.RuntimeEngine;
import xyz.redtorch.pb.CoreField.CancelOrderReqField;
import xyz.redtorch.pb.CoreField.OrderField;
import xyz.redtorch.pb.CoreField.SubmitOrderReqField;

@Slf4j
@Component
public class OrderingEventHandler implements RuntimeEngine.Listener, InitializingBean{

	@Autowired
	private RuntimeEngine rtEngine;
	
	ConcurrentHashMap<String, IAccount> orderAccountMap = new ConcurrentHashMap<String, IAccount>(100);
	
	@Override
	public void afterPropertiesSet() throws Exception {
		rtEngine.addEventHandler(EventType.PLACE_ORDER.toString(), this);	
		rtEngine.addEventHandler(EventType.WITHDRAW_ORDER.toString(), this);
		rtEngine.addEventHandler(EventType.FEEDBACK_ORDER.toString(), this);
	}

	@Override
	public void onEvent(EventObject e) {
		Object obj = e.getSource();
		if(obj instanceof OrderField) {
			doneOrder((OrderField) obj);
		}else if(obj instanceof SubmitOrderReqField) {
			placeOrder((SubmitOrderReqField) obj);
		}else if(obj instanceof CancelOrderReqField) {
			withdrawOrder((CancelOrderReqField) obj);
		}else {
			log.warn("传入了非法对象：{}", obj);
		}
	}
	
	void placeOrder(SubmitOrderReqField submitOrderReq) {
		String accountName = submitOrderReq.getGatewayId();
		IAccount account = rtEngine.getAccount(accountName);
		if(account!=null) {
			orderAccountMap.put(submitOrderReq.getOriginOrderId(), account);
			account.placeOrder(submitOrderReq);
		}else {
			log.warn("没有找到账户名为【{}】的账户", accountName);
		}
	}
	
	void withdrawOrder(CancelOrderReqField cancelOrderReq) {
		IAccount account = orderAccountMap.get(cancelOrderReq.getOriginOrderId());
		account.cancelOrder(cancelOrderReq);
	}
	
	void doneOrder(OrderField order) {
		//FIXME 要考虑订单部分成交时如何处理
//		String originOrderId = order.getOriginOrderId();
//		orderAccountMap.remove(originOrderId);
	}

}