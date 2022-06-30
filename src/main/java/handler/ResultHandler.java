package handler;

import it.tdlight.client.GenericResultHandler;
import it.tdlight.client.Result;

public class ResultHandler implements GenericResultHandler {

	@Override
	public void onResult(Result result) {
		System.out.println(result);
	}
}
