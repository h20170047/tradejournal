package com.svj;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Trade-Journal Service", version = "v3.0", description = "App helps in screening stocks and journaling executed trades"))
public class TradeJournalApplication {
	// TODO- 3 APIs, 1. trader's preference, 2. add journal, 3. get stats b/w dates

	public static void main(String[] args) {
		SpringApplication.run(TradeJournalApplication.class, args);
	}

}
