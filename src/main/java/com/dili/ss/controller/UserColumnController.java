package com.dili.ss.controller;

import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.UserColumn;
import com.dili.ss.service.UserColumnService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 用户列过滤控制器
 */
@Api("/userColumn")
@Controller
@RequestMapping("/userColumn")
@ConditionalOnExpression("'${redis.enable}'=='true'")
public class UserColumnController {
	@Autowired
	UserColumnService userColumnService;

	@RequestMapping(value = "/save.action", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	BaseOutput save(@ModelAttribute UserColumn userColumn) throws Exception {
		userColumnService.saveUserColumns(userColumn);
		return BaseOutput.success("保存列成功");
	}

	@RequestMapping(value = "/get.action", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	BaseOutput get(@ModelAttribute UserColumn userColumn) throws Exception {
		return BaseOutput.success("获取成功").setData(userColumnService.getUserColumns(userColumn));
	}


}