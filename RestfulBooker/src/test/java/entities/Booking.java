package entities;

import lombok.Getter;
import lombok.Setter;

public class Booking {
    @Getter @Setter
    private String firstname;
    @Getter @Setter
    private String lastname;
    @Getter @Setter
    private Integer totalprice;
    @Getter @Setter
    private Boolean depositpaid;
    @Getter @Setter
    private BookingDates bookingdates;
    @Getter @Setter
    private String additionalneeds;

}
