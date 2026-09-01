package constants;

public class RestfulBooksEndpoints {
    public static final String BASE_URL = "https://restful-booker.herokuapp.com";

    public static final String AUTH_ENDPOINT = "/auth";
    public static final String BOOKING_ENDPOINT = "/booking";
    // NOTA: se corrige respecto del proyecto del primer parcial: se agrega
    // la barra "/" antes de {id} para que RestAssured arme correctamente
    // la URL "/booking/{id}".
    public static final String BOOKING_BY_ID_ENDPOINT = "/booking/{id}";
}
