package com.nparmenov.reservation_system.services;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nparmenov.reservation_system.models.reservation.Reservation;
import com.nparmenov.reservation_system.repositories.ReservationRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    public Flux<Reservation> findAll() {
        return reservationRepository.findAllReservations();
    }

    public Mono<Boolean> releaseReservationById(Integer reservationId) {
        return reservationRepository.releaseReservationById(reservationId);
    }

    public Mono<Reservation> searchReservationById(Integer reservationId) {
        return reservationRepository.searchReservationById(reservationId);
    }

    public Mono<Reservation> searchNearestAvailableTimeToReservation(Integer resourceId) {
        return reservationRepository.searchNearestAvailableTimeToReservation(resourceId);
    }

    public Mono<Integer> createReservation(
        Long reservationStart,
        Long reservationDuration,
        Integer reservationResourceId,
        Integer reservationResourceOwnerId
    ) {
        return reservationRepository.createReservation(reservationStart, reservationDuration, reservationResourceId, reservationResourceOwnerId);
    }

    public Flux<Reservation> searchReservationsByCriterion(
        Map<String, String> allRequestParams
    ) {
        if (allRequestParams.containsKey("userId")) {
            Integer userId = Integer.parseInt(allRequestParams.get("userId"));
            return reservationRepository.searchReservationsByUserId(userId);
        } else if (allRequestParams.containsKey("resourceId")) {
            Integer resourceId = Integer.parseInt(allRequestParams.get("resourceId"));
            return reservationRepository.searchReservationsByResourceId(resourceId);
        } else if (allRequestParams.containsKey("reservationStart") && allRequestParams.containsKey("reservationEnd")) {
            Long reservationStartTimestamp = Long.parseLong(allRequestParams.get("reservationStart"));
            Long reservationEndTimestamp = Long.parseLong(allRequestParams.get("reservationEnd"));
            return reservationRepository.searchReservationsByTimeFrame(reservationStartTimestamp, reservationEndTimestamp);
        }

        return Flux.error(new Exception("can't find params for filter"));
    }
}
