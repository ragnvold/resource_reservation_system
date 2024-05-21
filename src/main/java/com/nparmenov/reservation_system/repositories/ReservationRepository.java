package com.nparmenov.reservation_system.repositories;

import org.springframework.stereotype.Repository;

import com.nparmenov.reservation_system.models.reservation.Reservation;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ReservationRepository {
    Flux<Reservation> findAllReservations();
    Mono<Integer> createReservation(Long reservationStart, Long reservationDuration, Integer reservationResourceId, Integer reservationResourceOwnerId);
    Mono<Boolean> releaseReservationById(Integer reservationId);
    Mono<Reservation> searchReservationById(Integer reservationId);
    Flux<Reservation> searchReservationsByUserId(Integer userId);
    Flux<Reservation> searchReservationsByResourceId(Integer resourceId);
    Flux<Reservation> searchReservationsByTimeFrame(Long reservationStart, Long reservationEnd);
    Mono<Reservation> searchNearestAvailableTimeToReservation(Integer resourceId);
}