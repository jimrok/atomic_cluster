


import static com.greghaskins.spectrum.Spectrum.afterAll;
import static com.greghaskins.spectrum.Spectrum.beforeAll;
import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;

import org.junit.runner.RunWith;

import com.greghaskins.spectrum.Spectrum;

@RunWith(Spectrum.class)
public class ATestTemplate {
	{

		describe(
				"一个测试项的说明",
				() -> {
					
					beforeAll(() -> {
					
					});
					
					afterAll(()->{
					
					});
					

					it("要验证什么，写在这里。例如：无token的请求会失败",
							() -> {

								//怎么验证写在这里，用assertTrue做断言
								int HTTP_RESPONSE_CODE = 401;
								org.junit.Assert.assertTrue(HTTP_RESPONSE_CODE == 401);

							});
					
					it("第二个验证写在这里，可以继续添加多个验证",
							() -> {

								//怎么验证写在这里，用assertTrue做断言
								int HTTP_RESPONSE_CODE = 401;
								org.junit.Assert.assertTrue(HTTP_RESPONSE_CODE == 401);

							});

				});

	}

}
