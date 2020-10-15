package com.infrrd.regular.expression.match.contoller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infrrd.regular.expression.match.model.TextBody;
import com.infrrd.regular.expression.match.model.Validation;

@RestController
@RequestMapping(value = "/regular-expression")
public class RegularExpressionMatch {

	//I ran a small performance test 1,000,000 lines of the non-matching text to each regular expression in turn
	public static final int NUM_RUNS = 1000000;

	@PostMapping
	public Validation matchRegularExpression(@RequestBody TextBody textBody) throws IllegalStateException, Exception {
		Validation validationResponse = new Validation();

		String regrexExpression = textBody.getRegrex();
		String strTextBody = textBody.getTextBody();

		Pattern p = Pattern.compile(regrexExpression);
		Matcher matcher = p.matcher(strTextBody);

		boolean matches = false;
		Long start = System.currentTimeMillis();
		
		for (int i = 0; i < NUM_RUNS; i++) {
			matches |= matcher.matches();
		}
		
		Long timeElapsed = System.currentTimeMillis() - start;
		
		boolean flag = true;
		
		//The bad regular expression took on average 10,100 milliseconds to process all 1,000,000 lines
		if(timeElapsed > 10100) {
			flag = false;
			validationResponse.setMatch(null);
			validationResponse.setError(true);
		}


		if (matches && flag) {
            //The good regular expression took just 240 milliseconds
			validationResponse.setMatch(matcher.group(1));
			validationResponse.setError(false);

		} else {

			if (!matcher.find() && flag) {
				//No matching found although it is good regular expression
				validationResponse.setMatch(null);
				validationResponse.setError(false);
			}

		}

		return validationResponse;
	}

}
