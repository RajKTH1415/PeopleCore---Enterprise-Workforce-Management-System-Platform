package com.peoplecore.dto.response;



import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class GeocodeResponse {

    private BigDecimal latitude;

    private BigDecimal longitude;

    private String placeId;

    private String formattedAddress;

}
