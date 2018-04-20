package com.exchange.core.provider.feign;

import com.exchange.core.config.FeignConfig;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import java.util.Map;

@Service
@FeignClient(name = "blockchainInfoProvider", url = "https://blockchain.info", configuration = FeignConfig.class)
public interface BlockchainInfoProvider {
    @RequestMapping(method = RequestMethod.GET, value = "/zh-cn/rawtx/{txHash}")
    Map<String, Object> getTransaction(@PathVariable("txHash") String txHash);
}
