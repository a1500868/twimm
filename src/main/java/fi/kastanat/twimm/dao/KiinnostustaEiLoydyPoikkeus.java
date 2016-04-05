package fi.kastanat.twimm.dao;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(value=HttpStatus.NOT_FOUND)
public class KiinnostustaEiLoydyPoikkeus extends RuntimeException {

	public KiinnostustaEiLoydyPoikkeus(Exception cause) {
		super(cause);
	}
	
}