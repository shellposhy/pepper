package com.pepper.spring.cases;

import static cn.com.lemon.base.DateUtil.day;
import static cn.com.lemon.base.DateUtil.month;
import static cn.com.lemon.base.DateUtil.year;
import static cn.com.lemon.base.Strings.uuid;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.pepper.spring.model.Article;
import com.pepper.spring.service.PepperService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring.xml")
public class SpringCase {
	@Autowired
	private PepperService pepperService;

	@Test
	public void hello() {
		Article article = new Article();
		article.setUuid(uuid());
		article.setDocTime(new Date());
		article.setYear(year(article.getDocTime()));
		article.setMonth(month(article.getDocTime()));
		article.setDay(day(article.getDocTime()));
		
		pepperService.register(article);
	}

}
