package com.nparmenov.reservation_system.models.reservation;

import java.sql.Timestamp;

public record Reservation(Integer id, Timestamp reservationStart, Timestamp reservationEnd, Integer reservationResourceId, Integer reservationResourceOwnerId) {}