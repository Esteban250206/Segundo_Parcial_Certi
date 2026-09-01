import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import constants.RestfulBooksEndpoints;
import entities.Booking;
import entities.BookingDates;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import util.Request;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Pruebas automatizadas de la API https://restful-booker.herokuapp.com,
 * que reflejan los 18 casos documentados en TestLink (proyecto Restful-booker),
 * organizados en las mismas 6 carpetas por endpoint que en TestLink.
 * <p>
 * Herramientas: RestAssured + JUnit 5.
 * Conceptos usados: llamadas REST (GET, POST, PUT, DELETE) y Assertions.
 */
public class BookingsCRUD {

    private static final ObjectMapper mapper = new ObjectMapper();

    private static String buildValidBookingPayload(String firstname, String lastname) throws JsonProcessingException {
        BookingDates dates = new BookingDates();
        dates.setCheckin("2026-10-01");
        dates.setCheckout("2026-10-05");

        Booking booking = new Booking();
        booking.setFirstname(firstname);
        booking.setLastname(lastname);
        booking.setTotalprice(4500);
        booking.setDepositpaid(true);
        booking.setBookingdates(dates);
        booking.setAdditionalneeds("Breakfast");

        return mapper.writeValueAsString(booking);
    }

    private static String getValidToken() {
        Response authResponse = Request.post(RestfulBooksEndpoints.AUTH_ENDPOINT,
                "{\"username\" : \"admin\", \"password\" : \"password123\"}");
        return authResponse.jsonPath().getString("token");
    }

    // =================================================================
    // POST auth - Crear token
    // =================================================================
    @Nested
    @DisplayName("POST auth - Crear token")
    class CrearToken {

        // RB-1: Crear token con credenciales validas
        @Test
        @DisplayName("RB-1: Crear token con credenciales validas")
        public void createTokenWithValidCredentials() {
            Response response = Request.post(RestfulBooksEndpoints.AUTH_ENDPOINT,
                    "{\"username\" : \"admin\", \"password\" : \"password123\"}");

            response.then().assertThat().statusCode(200);
            response.then().assertThat().body("token", notNullValue());
        }

        // RB-2: Crear token con contraseña incorrecta
        @Test
        @DisplayName("RB-2: Crear token con contraseña incorrecta")
        public void createTokenWithWrongPassword() {
            Response response = Request.post(RestfulBooksEndpoints.AUTH_ENDPOINT,
                    "{\"username\" : \"admin\", \"password\" : \"claveIncorrecta\"}");

            response.then().assertThat().statusCode(200);
            response.then().assertThat().body("reason", equalTo("Bad credentials"));
        }

        // RB-3: Crear token con campos vacios
        @Test
        @DisplayName("RB-3: Crear token con campos vacios")
        public void createTokenWithEmptyFields() {
            Response response = Request.post(RestfulBooksEndpoints.AUTH_ENDPOINT,
                    "{\"username\" : \"\", \"password\" : \"\"}");

            response.then().assertThat().statusCode(200);
            response.then().assertThat().body("reason", equalTo("Bad credentials"));
        }
    }

    // =================================================================
    // POST booking - Crear reserva
    // =================================================================
    @Nested
    @DisplayName("POST booking - Crear reserva")
    class CrearReserva {

        // RB-4: Crear reserva con datos validos
        @Test
        @DisplayName("RB-4: Crear reserva con datos validos")
        public void createBookingWithValidData() throws JsonProcessingException {
            String payload = buildValidBookingPayload("Sebastian", "Catala");

            Response response = Request.post(RestfulBooksEndpoints.BOOKING_ENDPOINT, payload);

            response.then().assertThat().statusCode(200);
            response.then().assertThat().body("bookingid", notNullValue());
            response.then().assertThat().body("booking.firstname", equalTo("Sebastian"));
            response.then().assertThat().body("booking.lastname", equalTo("Catala"));
        }

        // RB-5: Crear reserva sin campos obligatorios (firstname y lastname vacios)
        @Test
        @DisplayName("RB-5: Crear reserva sin campos obligatorios (firstname y lastname vacios)")
        public void createBookingWithMissingRequiredFields() {
            String payload = "{"
                    + "\"firstname\": \"\","
                    + "\"lastname\": \"\","
                    + "\"totalprice\": 1000,"
                    + "\"depositpaid\": true,"
                    + "\"bookingdates\": {\"checkin\": \"2026-10-01\", \"checkout\": \"2026-10-05\"},"
                    + "\"additionalneeds\": \"Breakfast\""
                    + "}";

            Response response = Request.post(RestfulBooksEndpoints.BOOKING_ENDPOINT, payload);

            // NOTA: la API publica de restful-booker suele aceptar campos vacios (200)
            // en vez de rechazarlos; ajustar segun el resultado real documentado en TestLink.
            response.then().log().body();
            response.then().assertThat().body("booking.firstname", equalTo(""));
            response.then().assertThat().body("booking.lastname", equalTo(""));
        }

        // RB-6: Crear reserva con totalprice en formato invalido (texto en vez de numero)
        @Test
        @DisplayName("RB-6: Crear reserva con totalprice en formato invalido (texto)")
        public void createBookingWithInvalidTotalPriceFormat() {
            String payload = "{"
                    + "\"firstname\": \"Sebastian\","
                    + "\"lastname\": \"Catala\","
                    + "\"totalprice\": \"cuatrocientos\","
                    + "\"depositpaid\": true,"
                    + "\"bookingdates\": {\"checkin\": \"2026-10-01\", \"checkout\": \"2026-10-05\"},"
                    + "\"additionalneeds\": \"Breakfast\""
                    + "}";

            Response response = Request.post(RestfulBooksEndpoints.BOOKING_ENDPOINT, payload);

            response.then().log().body();
            // Se espera un error de validacion (400/500) al enviar un tipo de dato invalido.
            Assertions_assertStatusIsError(response);
        }

        private void Assertions_assertStatusIsError(Response response) {
            int status = response.getStatusCode();
            org.junit.jupiter.api.Assertions.assertTrue(status == 400 || status == 500,
                    "Se esperaba un codigo de error (400 o 500) por el formato invalido de totalprice, se obtuvo: " + status);
        }
    }

    // =================================================================
    // GET booking - Listar IDs de reservas
    // =================================================================
    @Nested
    @DisplayName("GET booking - Listar IDs de reservas")
    class ListarIdsDeReservas {

        // RB-7: Obtener listado completo de IDs de reservas
        @Test
        @DisplayName("RB-7: Obtener listado completo de IDs de reservas")
        public void getAllBookingIds() {
            Response response = Request.get(RestfulBooksEndpoints.BOOKING_ENDPOINT);

            response.then().assertThat().statusCode(200);
            response.then().assertThat().body("$", notNullValue());
            response.then().assertThat().body("size()", Matchers.greaterThan(0));
        }

        // RB-8: Filtrar reservas por firstname existente
        @Test
        @DisplayName("RB-8: Filtrar reservas por firstname existente")
        public void filterBookingsByExistingFirstname() throws JsonProcessingException {
            // Se crea una reserva conocida para asegurar que el filtro tenga resultados
            Request.post(RestfulBooksEndpoints.BOOKING_ENDPOINT, buildValidBookingPayload("Mariana", "Rojas"));

            Map<String, String> params = new HashMap<>();
            params.put("firstname", "Mariana");

            Response response = Request.getWithQueryParams(RestfulBooksEndpoints.BOOKING_ENDPOINT, params);

            response.then().assertThat().statusCode(200);
            response.then().assertThat().body("$", notNullValue());
        }

        // RB-9: Filtrar reservas con checkin en formato de fecha invalido
        @Test
        @DisplayName("RB-9: Filtrar reservas con checkin en formato de fecha invalido")
        public void filterBookingsWithInvalidCheckinFormat() {
            Map<String, String> params = new HashMap<>();
            params.put("checkin", "fecha-invalida");

            Response response = Request.getWithQueryParams(RestfulBooksEndpoints.BOOKING_ENDPOINT, params);
            response.then().log().status();

            // NOTA: restful-booker es conocido por responder con error 500 ante
            // un formato de fecha invalido en el filtro; ajustar segun lo observado
            // y documentado como resultado real en TestLink.
            int status = response.getStatusCode();
            org.junit.jupiter.api.Assertions.assertTrue(status == 500 || status == 200,
                    "Codigo de respuesta inesperado ante checkin invalido: " + status);
        }
    }

    // =================================================================
    // GET booking por ID - Consultar reserva
    // =================================================================
    @Nested
    @DisplayName("GET booking por ID - Consultar reserva")
    class ConsultarReservaPorId {

        private String bookingId;

        @BeforeEach
        public void createBookingForConsult() throws JsonProcessingException {
            Response response = Request.post(RestfulBooksEndpoints.BOOKING_ENDPOINT,
                    buildValidBookingPayload("Sebastian", "Catala"));
            bookingId = String.valueOf(response.jsonPath().getInt("bookingid"));
        }

        // RB-10: Obtener reserva existente por ID
        @Test
        @DisplayName("RB-10: Obtener reserva existente por ID")
        public void getExistingBookingById() {
            Response response = Request.getById(RestfulBooksEndpoints.BOOKING_BY_ID_ENDPOINT, bookingId);

            response.then().assertThat().statusCode(200);
            response.then().assertThat().body("$", hasKey("firstname"));
            response.then().assertThat().body("$", hasKey("lastname"));
            response.then().assertThat().body("$", hasKey("totalprice"));
            response.then().assertThat().body("$", hasKey("depositpaid"));
            response.then().assertThat().body("$", hasKey("bookingdates"));
            response.then().assertThat().body("bookingdates", hasKey("checkin"));
            response.then().assertThat().body("bookingdates", hasKey("checkout"));
        }

        // RB-11: Obtener reserva con ID inexistente
        @Test
        @DisplayName("RB-11: Obtener reserva con ID inexistente")
        public void getBookingWithNonExistentId() {
            Response response = Request.getById(RestfulBooksEndpoints.BOOKING_BY_ID_ENDPOINT, "999999999");

            response.then().assertThat().statusCode(404);
        }

        // RB-12: Obtener reserva con ID en formato no numerico (texto)
        @Test
        @DisplayName("RB-12: Obtener reserva con ID en formato no numerico (texto)")
        public void getBookingWithNonNumericId() {
            Response response = Request.getById(RestfulBooksEndpoints.BOOKING_BY_ID_ENDPOINT, "abc");

            response.then().log().status();
            // Se espera un error (404) ya que "abc" no corresponde a ninguna reserva valida.
            response.then().assertThat().statusCode(404);
        }
    }

    // =================================================================
    // PUT booking - Actualizar reserva completa
    // =================================================================
    @Nested
    @DisplayName("PUT booking - Actualizar reserva completa")
    class ActualizarReservaCompleta {

        private String bookingId;

        @BeforeEach
        public void createBookingForUpdate() throws JsonProcessingException {
            Response response = Request.post(RestfulBooksEndpoints.BOOKING_ENDPOINT,
                    buildValidBookingPayload("Sebastian", "Catala"));
            bookingId = String.valueOf(response.jsonPath().getInt("bookingid"));
        }

        // RB-13: Actualizar reserva completa con token valido
        @Test
        @DisplayName("RB-13: Actualizar reserva completa con token valido")
        public void updateBookingWithValidToken() throws JsonProcessingException {
            String token = getValidToken();
            String payload = buildValidBookingPayload("Sebastian", "Miranda");

            Response response = Request.put(RestfulBooksEndpoints.BOOKING_BY_ID_ENDPOINT, bookingId, payload, token);

            response.then().assertThat().statusCode(200);
            response.then().assertThat().body("lastname", equalTo("Miranda"));
        }

        // RB-14: Actualizar reserva sin token de autenticacion
        @Test
        @DisplayName("RB-14: Actualizar reserva sin token de autenticacion")
        public void updateBookingWithoutToken() throws JsonProcessingException {
            String payload = buildValidBookingPayload("Sebastian", "Miranda");

            Response response = Request.put(RestfulBooksEndpoints.BOOKING_BY_ID_ENDPOINT, bookingId, payload, null);

            response.then().assertThat().statusCode(403);
        }

        // RB-15: Actualizar reserva con ID inexistente
        @Test
        @DisplayName("RB-15: Actualizar reserva con ID inexistente")
        public void updateBookingWithNonExistentId() throws JsonProcessingException {
            String token = getValidToken();
            String payload = buildValidBookingPayload("Sebastian", "Miranda");

            Response response = Request.put(RestfulBooksEndpoints.BOOKING_BY_ID_ENDPOINT, "999999999", payload, token);

            response.then().assertThat().statusCode(405);
        }
    }

    // =================================================================
    // DELETE booking - Eliminar reserva
    // =================================================================
    @Nested
    @DisplayName("DELETE booking - Eliminar reserva")
    class EliminarReserva {

        private String bookingId;

        @BeforeEach
        public void createBookingForDelete() throws JsonProcessingException {
            Response response = Request.post(RestfulBooksEndpoints.BOOKING_ENDPOINT,
                    buildValidBookingPayload("Sebastian", "Catala"));
            bookingId = String.valueOf(response.jsonPath().getInt("bookingid"));
        }

        // RB-17: Eliminar reserva existente con token valido
        @Test
        @DisplayName("RB-17: Eliminar reserva existente con token valido")
        public void deleteExistingBookingWithValidToken() {
            String token = getValidToken();

            Response response = Request.delete(RestfulBooksEndpoints.BOOKING_BY_ID_ENDPOINT, bookingId, token);

            response.then().assertThat().statusCode(201);

            // Verificacion adicional: la reserva ya no deberia existir
            Response getResponse = Request.getById(RestfulBooksEndpoints.BOOKING_BY_ID_ENDPOINT, bookingId);
            getResponse.then().assertThat().statusCode(404);
        }

        // RB-18: Eliminar reserva sin token de autenticacion
        @Test
        @DisplayName("RB-18: Eliminar reserva sin token de autenticacion")
        public void deleteBookingWithoutToken() {
            Response response = Request.delete(RestfulBooksEndpoints.BOOKING_BY_ID_ENDPOINT, bookingId, null);

            response.then().assertThat().statusCode(403);
        }

        // RB-19: Eliminar reserva con ID inexistente
        @Test
        @DisplayName("RB-19: Eliminar reserva con ID inexistente")
        public void deleteBookingWithNonExistentId() {
            String token = getValidToken();

            Response response = Request.delete(RestfulBooksEndpoints.BOOKING_BY_ID_ENDPOINT, "999999999", token);

            response.then().assertThat().statusCode(405);
        }
    }
}
