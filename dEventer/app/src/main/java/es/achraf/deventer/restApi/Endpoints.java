package es.achraf.deventer.restApi;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface Endpoints {

    @FormUrlEncoded
    @POST(ConstantesRestApi.KEY_POST_ID_TOKEN)
    Call<Respuesta> registrarTokenId(@Field("token") String token, @Field("mensaje") String mensaje);

    @GET(ConstantesRestApi.KEY_TOQUE_ANIMAL)
    Call<Respuesta> traerUsuario(@Path("id") String id, @Path("mensaje") String mensaje);
}
