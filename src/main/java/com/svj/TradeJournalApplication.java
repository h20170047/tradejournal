package com.svj;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Trade-Journal Service", version = "v3.0", description = "App helps in screening stocks and journaling executed trades"))
public class TradeJournalApplication {
	// TODO- API to define level for a trader, max draw-dawn, target and duration
	// TODO- CSV report should mention when user has advanced to next level or depromoted- Daily, Weekly, Monthly reports in CSV
	// TODO- Read daily data from excel and add to DB
	// TODO- Write daily, weekly, monthly reports with stats into excel
	public static void main(String[] args) {
		SpringApplication.run(TradeJournalApplication.class, args);
	}

}
