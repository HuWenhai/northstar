package tech.xuanwu.northstar.engine;

import xyz.redtorch.pb.CoreField.TickField;

/**
 * 消息引擎接口
 * @author kevinhuangwl
 *
 */
public interface MessageEngine {

	void emitTick(TickField tick);
	
	
}