package com.gjm.distributedlinkstatistics.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * @Auther: guojm
 * @Date: 2020-5-22 22:10
 * @Description:
 */

@RestController
public class ReadyController {

	@GetMapping("/ready")
	public String getReady(){
		return "hello world!";
	}
}
