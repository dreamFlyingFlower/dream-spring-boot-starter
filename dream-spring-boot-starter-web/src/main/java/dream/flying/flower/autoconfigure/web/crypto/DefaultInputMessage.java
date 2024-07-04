package dream.flying.flower.autoconfigure.web.crypto;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;

/**
 * 默认消息输入
 *
 * @author 飞花梦影
 * @date 2024-07-04 23:50:12
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class DefaultInputMessage implements HttpInputMessage {

	private HttpHeaders httpHeaders;

	private InputStream inputStream;

	public DefaultInputMessage(HttpHeaders httpHeaders, InputStream inputStream) {
		this.httpHeaders = httpHeaders;
		this.inputStream = inputStream;
	}

	@Override
	public HttpHeaders getHeaders() {
		return httpHeaders;
	}

	@Override
	public InputStream getBody() throws IOException {
		return inputStream;
	}
}