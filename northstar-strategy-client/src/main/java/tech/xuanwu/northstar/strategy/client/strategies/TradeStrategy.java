package tech.xuanwu.northstar.strategy.client.strategies;

/**
 * 交易策略接口
 * @author kevinhuangwl
 *
 */
public interface TradeStrategy {
	
	/**
	 * 获取策略所使用的网关接口名称
	 * @return
	 */
	String getGatewayName();

	/**
	 * 获取策略名称
	 * @return
	 */
	String getStrategyName();
	
	/**
	 * 获取策略订阅的合约名称
	 * @return
	 */
	String[] getSubscribeContractList();
	
	/**
	 * 启用策略
	 * @return
	 */
	void resume();
	
	/**
	 * 停用策略
	 * @return
	 */
	void suspend();
	
	
	/**
	 * 是否运行
	 * @return
	 */
	boolean isRunning();
}
