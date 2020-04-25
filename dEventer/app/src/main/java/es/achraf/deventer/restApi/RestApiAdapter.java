package es.achraf.deventer.restApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestApiAdapter {

    public Endpoints establecerConexionApi() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(ConstantesRestApi.ROOT_URL).addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit.create(Endpoints.class);
    }
}
