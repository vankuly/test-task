package com.nortal.testtask;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

@SpringBootTest
class TestTaskApplicationTestsIT {

	@Test
	void simpleFluxExample() {
		Flux<String> fluxFruits = Flux.just("apple", "pear", "plum");
		Flux<String> fluxColors = Flux.just("red", "", "blue");
		Flux<Integer> fluxAmounts = Flux.just(10, 20);
		Flux.zip(fluxFruits, fluxColors, fluxAmounts).subscribe(System.out::println);
	}

}
