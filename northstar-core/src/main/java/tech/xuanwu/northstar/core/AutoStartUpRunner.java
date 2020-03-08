package tech.xuanwu.northstar.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.core.service.AccountService;

@Slf4j
@Component
public class AutoStartUpRunner implements CommandLineRunner{
	
	@Autowired
	private AccountService accService;
	
	

	@Override
	public void run(String... args) throws Exception {
		
		//自动连接网关
		accService.connectGateway();
	}

}
