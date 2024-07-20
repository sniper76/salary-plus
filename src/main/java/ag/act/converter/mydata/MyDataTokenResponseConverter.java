package ag.act.converter.mydata;

import ag.act.model.MyDataTokenResponse;
import org.springframework.stereotype.Component;

@Component
public class MyDataTokenResponseConverter {

    public ag.act.model.MyDataTokenResponse convert(String finpongAccessToken) {
        return new MyDataTokenResponse().token(finpongAccessToken);
    }
}
